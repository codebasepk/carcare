package com.byteshaft.carecare.userFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.byteshaft.carecare.R;

public class ListOfServicesProviders extends Fragment{

    private View mBaseView;
    private ListView mServiceProvidersListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_service_providers, container, false);
        mServiceProvidersListView = mBaseView.findViewById(R.id.service_providers_list_view);
        return mBaseView;
    }
}
