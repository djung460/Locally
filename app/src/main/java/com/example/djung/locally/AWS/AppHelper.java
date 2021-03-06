package com.example.djung.locally.AWS;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.regions.Regions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for handling user sign up, sign in, and confirmation forms
 * Class for handling AWS Cognito and AWS User Pools
 *
 * Created by David Jung on 03/11/16.
 */
public class AppHelper {
    private static AppHelper appHelper;
    private static CognitoUserPool cognitoUserPool;
    private static String user;

    private static CognitoDevice newDevice;

    // User pool ID
    private static final String userPoolId =AwsConfiguration.AMAZON_COGNITO_VENDOR_IDENTITY_POOL_ID;
    private static final String clientId = AwsConfiguration.AMAZON_COGNITO_CLIENT_ID;
    private static final String clientSecret = AwsConfiguration.AMAZON_CILENT_SECRET;
    private static final Regions cognitoRegion = AwsConfiguration.AMAZON_COGNITO_REGION;

    // User details from the service
    private static CognitoUserSession currSession;
    private static CognitoUserDetails userDetails;
    private static CognitoCachingCredentialsProvider mCredentialsProvider;
    private static Map<String, String> firstTimeLogInUserAttributes;
    private static List<String> firstTimeLogInRequiredAttributes;
    private static ArrayList<ItemToDisplay> firstTimeLogInDetails;

    private static Map<String, String> signUpFieldsC2O;
    private static Map<String, String> signUpFieldsO2C;

    private static boolean emailAvailable;
    private static boolean emailVerified;

    public static void initialize(Context context) {
        setData();

        if(appHelper != null && cognitoUserPool != null)
            return;

        if(appHelper == null) {
            appHelper = new AppHelper();
        }

        if(cognitoUserPool == null) {
            cognitoUserPool = new CognitoUserPool(context, userPoolId, clientId,clientSecret,cognitoRegion);
        }

        emailVerified = false;
        emailAvailable = false;

        if( mCredentialsProvider == null) {
            mCredentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    userPoolId,
                    cognitoRegion
            );
        }
    }

    private static void setData() {
        signUpFieldsC2O = new HashMap<String, String>();
        signUpFieldsC2O.put("Username", "custom:username");
        signUpFieldsC2O.put("Vendor Name", "custom:vendor_name");
        signUpFieldsC2O.put("Phone number", "custom:phone_number");
        signUpFieldsC2O.put("Phone number verified", "custom:number_verified");
        signUpFieldsC2O.put("Email verified", "custom:email_verified");
        signUpFieldsC2O.put("Email","email");
        signUpFieldsC2O.put("Market Name", "custom:market_name");

        signUpFieldsO2C = new HashMap<String, String>();
        signUpFieldsO2C.put("custom:username", "Username");
        signUpFieldsO2C.put("custom:vendor_name", "Vendor Name");
        signUpFieldsO2C.put("custom:phone_number", "Phone number");
        signUpFieldsO2C.put("custom:number_verified", "Phone number verified");
        signUpFieldsO2C.put("custom:email_verified", "Email verified");
        signUpFieldsO2C.put("email", "Email");
        signUpFieldsC2O.put("custom:market_name", "Market Name");
    }

    public static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e("App Error",exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if(temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            if(temp != null && temp.length() > 0) {
                return formattedString;
            }
        }

        return  formattedString;
    }

    public static void setUser(String user) {
        AppHelper.user = user;
    }

    public static CognitoUserPool getCognitoUserPool() {
        return cognitoUserPool;
    }

    public static void setCurrSession(CognitoUserSession currSession) {
        AppHelper.currSession = currSession;
    }

    public static void setUserDetails(CognitoUserDetails userDetails) {
        AppHelper.userDetails = userDetails;
    }

    public static void setNewDevice(CognitoDevice newDevice) {
        AppHelper.newDevice = newDevice;
    }

    public static CognitoDevice getNewDevice() {
        return newDevice;
    }

    public static CognitoUserSession getCurrSession() {
        return currSession;
    }

    public static Map<String, String> getSignUpFieldsC2O() {
        return signUpFieldsC2O;
    }

    public static String getUser() {
        return user;
    }

    public static CognitoUserDetails getUserDetails() {
        return userDetails;
    }

    public static boolean isEmailAvailable() {
        return emailAvailable;
    }

    public static boolean isEmailVerified() {
        return emailVerified;
    }

    public static CognitoCachingCredentialsProvider getCredentialsProvider() {
        return mCredentialsProvider;
    }
}
