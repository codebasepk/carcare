package com.byteshaft.carecare.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.AutoMechanicCarWashItems;

import java.util.ArrayList;
import java.util.Random;

public class AutoMechanicCarWashAdapter extends ArrayAdapter<String> {

    private ViewHolder viewHolder;
    private ArrayList<AutoMechanicCarWashItems> arrayList;
    private Activity activity;
    private AutoMechanicCarWashItems autoMechanicCarWashItems;
    private ArrayList<Integer> servicesArrayList;


    public AutoMechanicCarWashAdapter(Activity activity, ArrayList<AutoMechanicCarWashItems> arrayList) {
        super(activity, R.layout.delegate_auto_mechanic);
        this.activity = activity;
        this.arrayList = arrayList;
        servicesArrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_auto_mechanic, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.serviceNameCheckBox = convertView.findViewById(R.id.check_box);
            viewHolder.categoryName = convertView.findViewById(R.id.category_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        autoMechanicCarWashItems = arrayList.get(position);
        viewHolder.serviceNameCheckBox.setText(autoMechanicCarWashItems.getServiceName());
        viewHolder.categoryName.setText(autoMechanicCarWashItems.getCategoryName());
        viewHolder.serviceNameCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            autoMechanicCarWashItems = arrayList.get(position);
            int serviceId = autoMechanicCarWashItems.getServiceId();
            if (isChecked) {
                servicesArrayList.add(serviceId);
            } else {
                if (servicesArrayList.contains(serviceId)) {
                    servicesArrayList.remove(serviceId);
                }
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    public ArrayList<Integer> serviceRequestData() {
        return servicesArrayList;
    }

    class ViewHolder {
        CheckBox serviceNameCheckBox;
        TextView categoryName;
    }
}
