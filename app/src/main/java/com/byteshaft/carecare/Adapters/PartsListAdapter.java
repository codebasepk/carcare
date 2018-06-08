package com.byteshaft.carecare.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

        PartsListItems partsListItems = arrayList.get(position);
        viewHolder.partNameTextView.setText(partsListItems.getPartName());
        viewHolder.partPriceTextView.setText(partsListItems.getPartPrice());
        viewHolder.partVehicleMakeModelTextView.setText(partsListItems.getPartMake());
        viewHolder.partModelYear.setText(partsListItems.getModelYear());
        Picasso.with(AppGlobals.getContext()).load(partsListItems.getPartImage()).into(viewHolder.partImage);
        viewHolder.providerContactTextView.setOnClickListener(v -> {
            System.out.println("CLICK");
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(
                    "tel:" + partsListItems.getProvidersContactNumber()));
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            activity.startActivity(intent);
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
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
