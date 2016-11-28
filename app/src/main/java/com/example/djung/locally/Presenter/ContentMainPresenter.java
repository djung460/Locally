package com.example.djung.locally.Presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.djung.locally.DB.VendorItemDatabase;
import com.example.djung.locally.DB.VendorItemsProvider;
import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.MarketUtils;
import com.example.djung.locally.View.Adapters.ContentMainAdapter;
import com.example.djung.locally.View.Adapters.SuggestionAdapter;
import com.example.djung.locally.View.ContentMainView;
import com.example.djung.locally.View.EnablePermissionsCard;
import com.example.djung.locally.View.MainActivity;
import com.example.djung.locally.View.MarketCardSection;
import com.example.djung.locally.View.QuickLinkCard;
import com.example.djung.locally.View.QuickLinkCardSection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Angy Chung on 2016-11-28.
 */

public class ContentMainPresenter {
    private Activity mActivity;
    private ContentMainView mContentMainView;
    private Location mCurrentLocation;
    private ArrayList<Object> mContentMainSectionsData;
    private ArrayList<Market> mAllMarketsList;
    private SuggestionAdapter mVendorItemsSuggestionAdapter;
    private static final String TAG = "ContentMainPresenter";

    /**
     * Constructor
     * @param activity
     * @param view
     */
    public ContentMainPresenter(Activity activity, ContentMainView view) {
        mActivity = activity;
        mContentMainView = view;
        mContentMainSectionsData = new ArrayList<>();
        mAllMarketsList = new ArrayList<>();
    }

    /**
     * Gets the data for content main at the given position
     * @param position
     * @return
     */
    public Object getContentMainData(int position) {
        return mContentMainSectionsData.get(position);
    }

    /**
     * Returns the size of content main data
     */
    public int getContentMainDataSize() {
        return mContentMainSectionsData.size();
    }

    /**
     * Sets the action bar title as "Locally"
     */
    public void setActionBar() {
        mContentMainView.setActionBarTitle("Locally");
    }

    /**
     * Gets the user's current location or requests app permissions if not already granted
     */
    public void getUserLocation(){
        LocationManager lm = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            mCurrentLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    /**
     * Gets a list of all the markets
     */
    private void fetchAllMarketsData() {
        MarketPresenter presenter = new MarketPresenter(mActivity);
        mAllMarketsList = new ArrayList<>();
        try {
            mAllMarketsList = new ArrayList<>(presenter.fetchMarkets());
        } catch (final ExecutionException | InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Populates content main including quick link cards section, nearby/recently viewed markets,
     * and a message asking the user to enable permissions if currently off
     */
    public void populateContentMain() {
        fetchAllMarketsData();
        populateQuickLinksCardSection();
        populateMarketsCardSection();

        ContentMainAdapter adapter = new ContentMainAdapter(mActivity, mContentMainSectionsData, mCurrentLocation);
        mContentMainView.showContentMain(adapter);
    }

    /**
     * Initializes the quick links section which includes All Markets, Calendar,
     * Fruits & Vegetables, Your Lists
     */
    public void populateQuickLinksCardSection() {
        ArrayList<QuickLinkCard> q = new ArrayList<>();

        String allMarkets = "";
        String marketsOpen = "";

        if(mAllMarketsList != null) {
            int numMarkets = mAllMarketsList.size();
            if(numMarkets >= 1) {
                allMarkets = numMarkets + " market";
                if(numMarkets != 1)
                    allMarkets = allMarkets + "s";
            }
            int numOpen = MarketUtils.getNumberOfCurrentlyOpenMarkets(mAllMarketsList);
            if(numOpen != 1) {
                marketsOpen = numOpen + " markets open now";
            } else {
                marketsOpen = numOpen + " market open now";
            }
        }

//       TODO: get number of items on the user's grocery list

        q.add(new QuickLinkCard(R.drawable.ubc, "All Markets", allMarkets));
        q.add(new QuickLinkCard(R.drawable.thumbnail2, "Calendar", marketsOpen));
        q.add(new QuickLinkCard(R.drawable.thumbnail3, "In Season Produce", "16 items"));
        q.add(new QuickLinkCard(R.drawable.thumbnail4, "Your Grocery List", "3 saved items"));

        QuickLinkCardSection qs = new QuickLinkCardSection(q);
        mContentMainSectionsData.add(qs);
    }

    /**
     * Decide which markets to display in the sections
     * TODO: for now we'll just add all the markets
     */
    public void populateMarketsCardSection() {
        MarketCardSection nearbySection = new MarketCardSection();
        nearbySection.setSectionTitle("Markets Nearby");

        MarketCardSection recentlyViewedSection = new MarketCardSection();
        recentlyViewedSection.setSectionTitle("Recently Viewed");

        if (mAllMarketsList != null && !mAllMarketsList.isEmpty()) {
            if(mCurrentLocation != null){

                List singleSectionItems = new ArrayList<>(mAllMarketsList);
                singleSectionItems = MarketUtils.getClosestMarkets(singleSectionItems, mCurrentLocation);

                ArrayList marketItems = new ArrayList();
                // show the first 4 markets
                for(int i=0; i != 4 && i != singleSectionItems.size(); ++i) {
                    marketItems.add(singleSectionItems.get(i));
                }

                nearbySection.setMarketList(marketItems);
                mContentMainSectionsData.add(nearbySection);

            }

            ArrayList recentlyViewed = new ArrayList();
            // show the first 4 markets
            for(int i=0; i != 4 && i != mAllMarketsList.size(); ++i) {
                recentlyViewed.add(mAllMarketsList.get(i));
            }
            recentlyViewedSection.setMarketList(recentlyViewed);
            mContentMainSectionsData.add(recentlyViewedSection);
        }

        // if location permissions denied, show the rationale
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mContentMainSectionsData.add(new EnablePermissionsCard(mActivity));

        }
    }

    /**
     * Searches the vendor items and displays results for the given query.
     * @param query The search query
     */
    public void showResults(String query) {
        Cursor cursor = mActivity.managedQuery(VendorItemsProvider.CONTENT_URI, null, null,
                new String[] {query}, null);

        if (cursor == null) {
        } else {
            // Specify the columns we want to display in the result
            String[] from = new String[] {VendorItemDatabase.KEY_VENDOR_ITEM_NAME,
                    VendorItemDatabase.KEY_VENDOR_ITEM_INFO};

            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] { R.id.vendor_item_name_search_result,
                    R.id.vendor_item_info_search_result };

            // Create a simple cursor adapter for vendor
            mVendorItemsSuggestionAdapter = new SuggestionAdapter(mActivity, cursor);

            mContentMainView.showSearchSuggestions(mVendorItemsSuggestionAdapter);
        }
    }

    /**
     * Shows the search results of vendors selling the selected item
     * @param position
     */
    public void onSuggestionClick(int position) {
        String vendorItem = mVendorItemsSuggestionAdapter.getSuggestion(position);
        mContentMainView.clearSearchFocus();
        ((MainActivity) mActivity).launchVendorSearchItemFragment(vendorItem);
    }

}