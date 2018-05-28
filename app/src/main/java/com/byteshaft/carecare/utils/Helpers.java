package com.byteshaft.carecare.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.byteshaft.carecare.R;
import com.byteshaft.requests.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.byteshaft.carecare.utils.AppGlobals.sImageLoader;


public class Helpers {

    private static ProgressDialog progressDialog;
    private static AlertDialog alertDialog;

    // get default sharedPreferences.
    private static SharedPreferences getPreferenceManager() {
        return PreferenceManager.getDefaultSharedPreferences(AppGlobals.getContext());
    }

    public static void showProgressDialog(Activity activity, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    public static void sendKey(String token) {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest httpRequest, int i) {
                switch (i) {
                    case HttpRequest.STATE_DONE:
                        switch (httpRequest.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.i("TAG", httpRequest.getResponseText());
                                break;
                        }
                }
            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest httpRequest, int i, short i1, Exception e) {


            }

        });
        request.open("POST", String.format("%spush_keys/", AppGlobals.BASE_URL));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("TAG", jsonObject.toString());
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(jsonObject.toString());
    }

    public static AlertDialog getAlertDialog() {
        return alertDialog;
    }

    public static void alertDialog(Activity activity, String title, String msg, final SwitchCompat compat) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (compat != null) {
                    compat.setChecked(false);
                    compat.setEnabled(true);
                }
                dialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void getBitMap(String url, CircleImageView circleImageView) {
        if (url.length() > 31) {
            ImageLoadingListener animateFirstListener;
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.mipmap.ic_launcher)
                    .showImageOnLoading(R.drawable.ic_menu_camera)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .cacheInMemory(false)
                    .cacheOnDisc(false).considerExifParams(true).build();
            animateFirstListener = new AnimateFirstDisplayListener();
            sImageLoader.displayImage(url, circleImageView, options, animateFirstListener);
        } else {
            circleImageView.setImageResource(R.drawable.ic_menu_camera);
        }
    }

    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                FadeInBitmapDisplayer.animate(imageView, 500);
                displayedImages.add(imageUri);
            }
        }
    }

    public static Bitmap getBitMapOfProfilePic(String selectedImagePath) {
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 100;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        return bm;
    }

    public static void showSnackBar(View view, int id) {
        Snackbar.make(view, AppGlobals.getContext().getResources()
                .getString(id), Snackbar.LENGTH_SHORT)
                .setActionTextColor(AppGlobals.getContext().getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    public static void showSnackBar(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setActionTextColor(AppGlobals.getContext().getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    public static boolean locationEnabled() {
        LocationManager lm = (LocationManager) AppGlobals.getContext()
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled || network_enabled;
    }

//    public static void dialogForLocationEnableManually(final Activity activity) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
//        dialog.setMessage(R.string.location_not_enabled);
//        dialog.setPositiveButton(R.string.turn_on, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                // TODO Auto-generated method stub
//                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                activity.startActivityForResult(myIntent, AppGlobals.LOCATION_ENABLE);
//                //get gps
//            }
//        });
//        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                // TODO Auto-generated method stub
//
//            }
//        });
//        dialog.show();
//    }


    public static String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }


    public static String getDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }

    public static String getTime24HourFormat() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(c.getTime());
    }

    public static String getTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm aa");
        return df.format(c.getTime());
    }

    public static String getDateNextSevenDays() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 7);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }

    public static String calculateAge(String dateOfBirth) {
        String[] dob = dateOfBirth.split("/");
        int date = Integer.parseInt(dob[0]);
        int month = Integer.parseInt(dob[1]);
        int year = Integer.parseInt(dob[2]);
        String years = Helpers.getAge(year, month, date);
        return years;
    }

    public static String getFormattedTime(String startTime) {
        SimpleDateFormat formatterFrom = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        Date rawDate = null;
        try {
            rawDate = formatterFrom.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = dateFormat.format(rawDate);
        return formattedDate;
    }


    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return df.format(c.getTime());
    }

    public static String getCurrentTimeAndDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm aa");
        return df.format(c.getTime());
    }

    public static String getDateForHeader() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        return df.format(c.getTime());
    }

    public static String getDateForComparison() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        return df.format(c.getTime());
    }

    public static String getPreviousDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
        return df.format(c.getTime());
    }

    public static boolean getDifference(String startTime) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        try {
            Date startDate = simpleDateFormat.parse(startTime);
            Date current = simpleDateFormat.parse(simpleDateFormat.format(c.getTime()));
            Log.i("TAG", "start time" + startTime);
            Log.i("TAG", "end time" + getTimeForCompare());
            Log.e("TAG", "before " + String.valueOf(startDate.after(current)));
            return startDate.after(current);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getTimeForCompare() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        return df.format(c.getTime());
    }
}
