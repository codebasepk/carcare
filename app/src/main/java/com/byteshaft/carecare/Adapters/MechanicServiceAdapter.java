package com.byteshaft.carecare.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.MechanicServices;

import java.util.ArrayList;

public class MechanicServiceAdapter extends BaseAdapter {

    private ViewHolder viewHolder;
    private ArrayList<MechanicServices> arrayList;
    private Activity activity;

    public MechanicServiceAdapter(Activity activity, ArrayList<MechanicServices> arrayList) {
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
        MechanicServices mechanicServices = arrayList.get(position);
        viewHolder.spinnerText.setText(mechanicServices.getName());
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
