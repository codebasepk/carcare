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
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.PartsListItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PartsListAdapter extends ArrayAdapter<String> {

    private ViewHolder viewHolder;
    private ArrayList<PartsListItems> arrayList;
    private Activity activity;
    private PartsListItems partsListItems;

    public String mContactNumber;


    public PartsListAdapter(Activity activity, ArrayList<PartsListItems> arrayList) {
        super(activity, R.layout.delegate_buy_parts);
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_buy_parts, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.partImage = convertView.findViewById(R.id.part_image);
            viewHolder.partNameTextView = convertView.findViewById(R.id.part_name);
            viewHolder.partPriceTextView = convertView.findViewById(R.id.part_price);
            viewHolder.partVehicleMakeModelTextView = convertView.findViewById(R.id.vehicle_make_model);
            viewHolder.partModelYear = convertView.findViewById(R.id.model_year);
            viewHolder.providerContactTextView = convertView.findViewById(R.id.call_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        partsListItems = arrayList.get(position);
        viewHolder.partNameTextView.setText(partsListItems.getPartName());
        viewHolder.partPriceTextView.setText(partsListItems.getPartPrice());
        viewHolder.partVehicleMakeModelTextView.setText(partsListItems.getPartMake());
        viewHolder.partModelYear.setText(partsListItems.getModelYear());
        Picasso.with(AppGlobals.getContext()).load(partsListItems.getPartImage()).into(viewHolder.partImage);
        viewHolder.providerContactTextView.setOnClickListener(v -> {
            System.out.println("CLICK");
            if(isPermissionGranted()){
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
        mContactNumber = partsListItems.getProvidersContactNumber();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mContactNumber));
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        activity.startActivity(callIntent);
    }

    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }



    class ViewHolder {
        CircleImageView partImage;
        TextView partNameTextView;
        TextView partPriceTextView;
        TextView partVehicleMakeModelTextView;
        TextView providerContactTextView;
        TextView partModelYear;

    }
}
