package com.example.djung.locally.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Utils.DateUtils;
import com.example.djung.locally.Utils.MarketUtils;
import com.example.djung.locally.Utils.ThreadUtils;
import com.example.djung.locally.Utils.VendorUtils;
import com.example.djung.locally.View.VendorDetailsView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Andy Lin on 2016-11-27.
 */

public class VendorDetailsPresenter {
    private Activity activity;
    private VendorDetailsView vendorDetailsView;
    private List<String> produceList;
    private Vendor currentVendor;
    private String marketName;
    private String vendorName;
    private String vendorAddress;
    private String vendorHours;
    private String vendorDatesOpen;
    private final String TAG = "VendorDetailsPresenter";


    public VendorDetailsPresenter(Activity activity, VendorDetailsView vendorDetailsView, Bundle bundle){
        this.activity = activity;
        this.vendorDetailsView = vendorDetailsView;
        marketName = bundle.getString("marketName");
        vendorName = bundle.getString("vendorName");
        vendorAddress = bundle.getString("marketAddress");
        vendorHours = bundle.getString("marketHours");
        vendorDatesOpen = bundle.getString("marketDatesOpen");
    }

    public void setActionBar(){
        vendorDetailsView.setActionBarTitle(marketName);
    }

    public void setViews(){
        vendorDetailsView.showVendorName(currentVendor.getName());
        vendorDetailsView.showVendorDescription(currentVendor.getDescription());
        vendorDetailsView.showVendorLocation(vendorAddress);

        if (MarketUtils.isMarketCurrentlyOpen(vendorDatesOpen, vendorHours)){
            vendorDetailsView.showVendorStatus("Open Now!");
        }
        else {
            vendorDetailsView.showVendorStatus("Closed Now");
        }

        vendorDetailsView.showVendorHours(DateUtils.parseHours(vendorHours));
    }

    /**
     * Fetch the vendor details of the vendor with the given market name and vendor name from the database
     */
    public void getVendor(){
        VendorPresenter presenter = new VendorPresenter(activity);
        currentVendor = new Vendor();

        try {
            currentVendor = presenter.fetchVendor(marketName,vendorName);

        } catch (final ExecutionException ee) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(activity)
                            .setTitle("Execute Exception")
                            .setMessage(ee.getMessage())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            });
        } catch (final InterruptedException ie) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(activity)
                            .setTitle("Interrupted Exception")
                            .setMessage(ie.getMessage())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            });
        }

        //Get the list of products that the vendor sells
        produceList = new ArrayList<String>();
        List<String> produceItemsSet = VendorUtils.filterPlaceholderText(new ArrayList<String>(currentVendor.getItemSet()));
        for (String item: produceItemsSet){
            produceList.add(item);
        }
    }

    public void populateProduceList(){
        Log.e(TAG, "Populating Produce List");
        vendorDetailsView.showProduceList(produceList);
    }

    public void callVendor(){
        String number = currentVendor.getPhoneNumber();
        if (number == null || number.equals("")){
            Log.e(TAG, "Not a valid phone number");
            Toast.makeText(activity, "The vendor does not have a valid phone number", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.e(TAG, "Calling phone number " + number);
            Uri call = Uri.parse("tel:" + number);
            Intent intent = new Intent(Intent.ACTION_DIAL, call);
            activity.startActivity(intent);
        }
    }

    public void emailVendor(){
        Log.e(TAG, "Emailing Vendor " + currentVendor.getName() + " with email address " + currentVendor.getEmail());
    }
}

