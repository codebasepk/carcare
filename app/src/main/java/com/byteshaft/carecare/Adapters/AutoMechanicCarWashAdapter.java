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


    public AutoMechanicCarWashAdapter(Activity activity, ArrayList<AutoMechanicCarWashItems> arrayList) {
        super(activity, R.layout.delegate_auto_mechanic);
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_auto_mechanic, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.radioGroup = convertView.findViewById(R.id.radio_group);
            RadioGroup.LayoutParams layoutParams;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        autoMechanicCarWashItems = arrayList.get(position);
        viewHolder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                autoMechanicCarWashItems = arrayList.get(position);
                int serviceId = autoMechanicCarWashItems.getServiceId();
                Log.e("onCheckedChanged", "" + serviceId);

            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    class ViewHolder {
        RadioGroup radioGroup;

    }
}
