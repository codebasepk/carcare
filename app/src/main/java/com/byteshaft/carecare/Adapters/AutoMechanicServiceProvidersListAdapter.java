package com.byteshaft.carecare.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.AutoMechanicsServiceRequestActivity;
import com.byteshaft.carecare.gettersetter.ServicesProvidersListItems;
import com.byteshaft.carecare.userFragments.ListOfServicesProviders;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class AutoMechanicServiceProvidersListAdapter extends ArrayAdapter<String> {

    private ViewHolder viewHolder;
    private ArrayList<ServicesProvidersListItems> arrayList;
    private Activity activity;
    private ServicesProvidersListItems servicesProvidersListItems;
    private String mContactNumber;


    public AutoMechanicServiceProvidersListAdapter(Activity activity, ArrayList<ServicesProvidersListItems> arrayList) {
        super(activity, R.layout.delegate_service_providers_list);
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_service_providers_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.servicesProvidersImage = convertView.findViewById(R.id.provider_image);
            viewHolder.servicesProvidersNameTextView = convertView.findViewById(R.id.service_provider_name);
            viewHolder.servicesProvidersDistanceTextView = convertView.findViewById(R.id.service_provider_distance);
            viewHolder.servicesProvidersExperienceTextView = convertView.findViewById(R.id.service_provider_experience);
            viewHolder.servicesProvidersStartTimeTextView = convertView.findViewById(R.id.service_provider_start_time);
            viewHolder.servicesProvidersEndTimeTextView = convertView.findViewById(R.id.service_provider_end_time);
            viewHolder.callTextView = convertView.findViewById(R.id.call_button);
            viewHolder.requestTextView = convertView.findViewById(R.id.request_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        servicesProvidersListItems = arrayList.get(position);
        viewHolder.servicesProvidersNameTextView.setText(servicesProvidersListItems.getServiceProviderName());
        viewHolder.servicesProvidersExperienceTextView.setText(
                servicesProvidersListItems.getServiceProviderExperience() + " Years");

        String startTime =servicesProvidersListItems.getServiceProviderStartTime();
        String endTime = servicesProvidersListItems.getServiceProviderEndTime();
        StringTokenizer tk = new StringTokenizer(startTime);
        StringTokenizer tk1 = new StringTokenizer(endTime);
        String time = tk.nextToken();
        String time1 = tk1.nextToken();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
        Date dt;
        Date dt1;
        try {
            dt = sdf.parse(time);
            dt1 = sdf.parse(time1);
            viewHolder.servicesProvidersStartTimeTextView.setText(sdfs.format(dt) + " TO ");
            viewHolder.servicesProvidersEndTimeTextView.setText(sdfs.format(dt1));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String[] startLocation = servicesProvidersListItems.getServiceProviderLocation().split(",");
        String[] endLocation = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LOCATION).split(",");
        if (ListOfServicesProviders.mLocationString != null) {
            String[] userLocation = ListOfServicesProviders.mLocationString.split(",");
            viewHolder.servicesProvidersDistanceTextView.setText(String.valueOf(
                    Helpers.calculationByDistance(new LatLng(Double.parseDouble(userLocation[0]),
                            Double.parseDouble(startLocation[1])), new LatLng(Double.parseDouble(userLocation[0]),
                            Double.parseDouble(userLocation[1])))) + " " + "KM");
        } else {
            viewHolder.servicesProvidersDistanceTextView.setText(String.valueOf(
                    Helpers.calculationByDistance(new LatLng(Double.parseDouble(startLocation[0]),
                            Double.parseDouble(startLocation[1])), new LatLng(Double.parseDouble(endLocation[0]),
                            Double.parseDouble(endLocation[1])))) + " " + "KM");
        }

        Picasso.with(AppGlobals.getContext()).load(servicesProvidersListItems.getServiceProviderImage()
        ).into(viewHolder.servicesProvidersImage);
        viewHolder.requestTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICK");
                activity.startActivity(new Intent(activity, AutoMechanicsServiceRequestActivity.class));
            }
        });
        viewHolder.callTextView.setOnClickListener(v -> {
            System.out.println("CLICK");
            if (isPermissionGranted()) {
                callAction();
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    public void callAction() {
        mContactNumber = servicesProvidersListItems.getProvidersContactNumber();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mContactNumber));
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        activity.startActivity(callIntent);
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }


    class ViewHolder {
        ImageView servicesProvidersImage;
        TextView servicesProvidersNameTextView;
        TextView callTextView;
        TextView requestTextView;
        TextView servicesProvidersDistanceTextView;
        TextView servicesProvidersExperienceTextView;
        TextView servicesProvidersStartTimeTextView;
        TextView servicesProvidersEndTimeTextView;

    }
}
