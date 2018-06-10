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
import com.byteshaft.carecare.gettersetter.ServicesProvidersListItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceProvidersListAdapter extends ArrayAdapter<String> {

    private ViewHolder viewHolder;
    private ArrayList<ServicesProvidersListItems> arrayList;
    private Activity activity;


    public ServiceProvidersListAdapter(Activity activity, ArrayList<ServicesProvidersListItems> arrayList) {
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
            viewHolder.providerContactTextView = convertView.findViewById(R.id.provider_number);
            viewHolder.callTextView = convertView.findViewById(R.id.call_button);
            viewHolder.requestTextView = convertView.findViewById(R.id.vehicle_make_model);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ServicesProvidersListItems servicesProvidersListItems = arrayList.get(position);
        viewHolder.servicesProvidersNameTextView.setText(servicesProvidersListItems.getServiceProviderName());
        viewHolder.providerContactTextView.setText(servicesProvidersListItems.getProvidersContactNumber());
        Picasso.with(AppGlobals.getContext()).load(servicesProvidersListItems.getServiceProviderImage()
        ).into(viewHolder.servicesProvidersImage);
        viewHolder.requestTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICK");
            }
        });
        viewHolder.callTextView.setOnClickListener(v -> {
            System.out.println("CLICK");
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(
                    "tel:" + servicesProvidersListItems.getProvidersContactNumber()));
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
        CircleImageView servicesProvidersImage;
        TextView servicesProvidersNameTextView;
        TextView providerContactTextView;
        TextView callTextView;
        TextView requestTextView;

    }
}
