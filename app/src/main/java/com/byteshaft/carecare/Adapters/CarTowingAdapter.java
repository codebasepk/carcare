package com.byteshaft.carecare.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.CarTowingItems;
import com.byteshaft.carecare.gettersetter.CarWashItems;
import com.byteshaft.carecare.gettersetter.TowServiceItems;

import java.util.ArrayList;

public class CarTowingAdapter extends ArrayAdapter<String> {

    private ViewHolder viewHolder;
    private ArrayList<CarTowingItems> arrayList;
    private Activity activity;

    private ArrayList<Integer> servicesArrayList;


    public CarTowingAdapter(Activity activity, ArrayList<CarTowingItems> arrayList) {
        super(activity, R.layout.delegate_car_wash);
        this.activity = activity;
        this.arrayList = arrayList;
        servicesArrayList = new ArrayList<>();
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
        CarTowingItems carTowingItems = arrayList.get(position);
        viewHolder.checkBox.setText(carTowingItems.getServiceName());
        viewHolder.carWashPrice.setText(carTowingItems.getServicePrice());

        viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int serviceId = carTowingItems.getServiceId();
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
        CheckBox checkBox;
        TextView carWashPrice;
    }
}
