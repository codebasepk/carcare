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

public class CarWashAdapter extends ArrayAdapter<String> {

    private ViewHolder viewHolder;
    private ArrayList<CarWashItems> arrayList;
    private Activity activity;

    private ArrayList<Integer> servicesArrayList;


    public CarWashAdapter(Activity activity, ArrayList<CarWashItems> arrayList) {
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
        CarWashItems carWashItems = arrayList.get(position);
        viewHolder.checkBox.setText(carWashItems.getServiceName());
        viewHolder.carWashPrice.setText(carWashItems.getServicePrice());

        viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int seerviceId = carWashItems.getServiceId();
            if (isChecked) {
                servicesArrayList.add(seerviceId);
            } else {
                if (servicesArrayList.contains(seerviceId)) {
                    servicesArrayList.remove(seerviceId);
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
