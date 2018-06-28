package com.byteshaft.carecare.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.AutoMechanicItems;
import com.byteshaft.carecare.gettersetter.AutoMechanicSubItem;
import com.byteshaft.carecare.gettersetter.CarWashItems;

import java.util.ArrayList;
import java.util.HashMap;

public class CarWashAdapter extends ArrayAdapter<String> {

    private ViewHolder viewHolder;
    private ArrayList<CarWashItems> arrayList;
    private Activity activity;
    private ArrayList<Integer> servicesArrayList;
    private HashMap<Integer, Boolean> positionHasMap;


    public CarWashAdapter(Activity activity, ArrayList<CarWashItems> arrayList, HashMap<Integer, Boolean> positionHasMap) {
        super(activity, R.layout.delegate_car_wash);
        this.activity = activity;
        this.arrayList = arrayList;
        servicesArrayList = new ArrayList<>();
        this.positionHasMap = positionHasMap;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_car_wash, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = convertView.findViewById(R.id.check_box);
            viewHolder.carWashPrice = convertView.findViewById(R.id.car_wash_price_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CarWashItems carWashItems = arrayList.get(position);
        viewHolder.checkBox.setText(carWashItems.getServiceName());
        viewHolder.carWashPrice.setText(carWashItems.getServicePrice());

        viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int serviceId = carWashItems.getServiceId();
            if (positionHasMap.get(position)) {
                viewHolder.checkBox.setChecked(true);
            } else {
                viewHolder.checkBox.setChecked(false);
            }
            if (isChecked) {
                servicesArrayList.add(serviceId);
                positionHasMap.put(position, true);
            } else {
                if (servicesArrayList.contains(serviceId)) {
                    servicesArrayList.remove((Integer) serviceId);
                    positionHasMap.put(position, false);
                }
            }

        });
        if (positionHasMap.get(position)) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }
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
        CheckBox checkBox;
        TextView carWashPrice;
    }
}
