package com.byteshaft.carecare.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AppGlobals extends Application {

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static Context sContext;

    public static final String SERVER_IP = "http://178.62.126.92:8000";
    public static final String SERVER_IP_FOR_IMAGE = "http://178.62.126.92:8000/";
    public static final String BASE_URL = String.format("%s/api/", SERVER_IP);
    public static final String KEY_USER_NAME = "username";
    public static final String KEY_FULL_NAME = "full_name";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_MOBILE_NUMBER = "mobile_number";
    public static final String KEY_LANDLINE_NUMBER = "landline";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_SECTOR_VALUE = "sector";
    public static final String KEY_SECTOR_ID = "id";
    public static final String KEY_SERVER_IMAGE = "photo";
    public static final String KEY_LOGIN = "login";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_ID = "id";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_GOT_INFO = "got_info";
    public static final int LOCATION_ENABLE = 3;
    public static Typeface typefaceBold;
    public static Typeface typefaceNormal;
    public static ImageLoader sImageLoader;
    public static final String KEY_FCM_TOKEN = "fcm_token";
    public static final String KEY_MIN_PRICE = "price";


    @Override
    public void onCreate() {
        super.onCreate();
//        FirebaseApp.initializeApp(getApplicationContext());
        sImageLoader = ImageLoader.getInstance();
        sImageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        sContext = getApplicationContext();
        typefaceBold = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/bold.ttf");
        typefaceNormal = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/normal.ttf");
    }


    public static void loginState(boolean type) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(KEY_LOGIN, type).apply();
    }

    public static boolean isLogin() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(KEY_LOGIN, false);
    }

    public static void gotInfo(boolean type) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(KEY_GOT_INFO, type).apply();
    }

    public static boolean isInfoAvailable() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(KEY_GOT_INFO, false);
    }


    public static void clearSettings() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().clear().apply();
    }

    public static SharedPreferences getPreferenceManager() {
        return getContext().getSharedPreferences("shared_prefs", MODE_PRIVATE);
    }

    public static void saveDataToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getStringFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getString(key, "");
    }

    public static void firstTimeLaunch(boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(IS_FIRST_TIME_LAUNCH, value).apply();
    }

    public static boolean isFirstTimeLaunch() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, false);
    }

    public static Context getContext() {
        return sContext;
    }

    public static void alertDialog(Activity activity, String title, String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0D1D1D1, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }
}


