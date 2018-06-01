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
import com.byteshaft.carecare.gettersetter.AutoMechanicCarWashItems;

import java.util.ArrayList;

public class AutoMechanicCarWashAdapter extends ArrayAdapter<String> {

    private ViewHolder viewHolder;
    private ArrayList<AutoMechanicCarWashItems> arrayList;
    private Activity activity;


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
            viewHolder.itemsCheckBox = convertView.findViewById(R.id.items_checkbox);
            viewHolder.priceTextView = convertView.findViewById(R.id.price_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AutoMechanicCarWashItems autoMechanicCarWashItems = arrayList.get(position);
        viewHolder.itemsCheckBox.setText(autoMechanicCarWashItems.getServiceName());
        viewHolder.priceTextView.setText(autoMechanicCarWashItems.getServicePrice());
        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    class ViewHolder {

        TextView priceTextView;
        CheckBox itemsCheckBox;

    }
}
