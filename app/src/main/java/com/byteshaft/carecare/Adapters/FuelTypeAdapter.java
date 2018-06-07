package com.byteshaft.carecare.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.FuelTypeItems;
import com.byteshaft.carecare.gettersetter.VehicleMakeItems;

import java.util.ArrayList;

public class FuelTypeAdapter extends BaseAdapter {

    private ViewHolder viewHolder;
    private ArrayList<FuelTypeItems> arrayList;
    private Activity activity;

    public FuelTypeAdapter(Activity activity, ArrayList<FuelTypeItems> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_spinner, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.spinnerText = convertView.findViewById(R.id.spinner_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FuelTypeItems fuelTypeItems = arrayList.get(position);
        viewHolder.spinnerText.setText(fuelTypeItems.getFuelName());
        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
