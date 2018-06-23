package com.byteshaft.carecare.userFragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.byteshaft.carecare.Adapters.AutoMechanicCarWashAdapter;
import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.AutoMechanicCarWashItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class AutoMechanicFragment extends Fragment implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener, View.OnClickListener {

    private View mBaseView;
    private ListView listView;
    private EditText mDetailsEditText;
    private Button mNextButton;
    private HttpRequest request;

    private ArrayList<AutoMechanicCarWashItems> arrayList;
    private AutoMechanicCarWashAdapter adapter;
    private int serviceId;
    private AutoMechanicCarWashItems items;

    private String mLocationString;

    private static final int LOCATION_PERMISSION = 4;
    private int locationCounter = 0;

    private FusedLocationProviderClient client;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.i("TAG", "onLocationResult");
            locationCounter++;
            if (locationCounter > 1) {
                stopLocationUpdate();
                mLocationString = locationResult.getLastLocation().getLatitude()
                        + "," + locationResult.getLastLocation().getLongitude();
                System.out.println("Lat: " + mLocationString);

            }

        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.i("TAGG", "onLocationAvailability");
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_auto_mechanic, container, false);
        client = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());
        listView = mBaseView.findViewById(R.id.services_list_view);
        mDetailsEditText = mBaseView.findViewById(R.id.details_edit_text);
        mNextButton = mBaseView.findViewById(R.id.button_next);
        mNextButton.setOnClickListener(this);
        getAutoMechanicsServicesList();

        locationCounter = 0;
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle(getResources().getString(R.string.permission_dialog_title));
            alertDialogBuilder.setMessage(getResources().getString(R.string.permission_dialog_message))
                    .setCancelable(false).setPositiveButton(R.string.button_continue, (dialog, id) -> {
                dialog.dismiss();
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_PERMISSION);
                }
            });
            alertDialogBuilder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


        } else {
            if (locationEnabled()) {
                startLocationUpdates();
            } else {
                Helpers.dialogForLocationEnableManually(getActivity());
            }
        }

        return mBaseView;
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
        switch (readyState) {
            case HttpRequest.ERROR_CONNECTION_TIMED_OUT:
                Helpers.showSnackBar(getView(), getString(R.string.connection_time_out));
                break;
            case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                Helpers.showSnackBar(getView(), exception.getLocalizedMessage());
                break;
        }

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        arrayList = new ArrayList<>();
                        adapter = new AutoMechanicCarWashAdapter(getActivity(), arrayList);
                        listView.setAdapter(adapter);
                        try {
                            JSONArray jsonArray = new JSONArray(request.getResponseText());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                System.out.println("Test " + jsonArray.getJSONObject(i));
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                items = new AutoMechanicCarWashItems();
                                items.setCategoryName(jsonObject.getString("name"));
                                JSONArray serviceSubItemsJsonArray = jsonObject.getJSONArray("sub_services");
                                for (int j = 0; j < serviceSubItemsJsonArray.length(); j++) {
                                    System.out.println("serviceSubItemsJsonArray " + serviceSubItemsJsonArray.getJSONObject(j));
                                    items.setServiceId(jsonObject.getInt("id"));
                                    items.setServiceName(jsonObject.getString("name"));
                                }
                                arrayList.add(items);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }

    private void getAutoMechanicsServicesList() {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%smechanic-services", AppGlobals.BASE_URL));
        request.send();
        Helpers.showProgressDialog(getActivity(), "Fetching Services...");
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("service_id", adapter.serviceRequestData());
        if (mLocationString.equals(" ")) {
            String userLocation = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LOCATION);
            bundle.putString("location", userLocation);
        } else {
            bundle.putString("location", mLocationString);
        }
        Log.e("onClick", "" + adapter.serviceRequestData().size());
        Log.e("onClick", "" + mLocationString);
        ListOfServicesProviders listOfServicesProviders = new ListOfServicesProviders();
        listOfServicesProviders.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container, listOfServicesProviders).commit();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (locationEnabled()) {
                        startLocationUpdates();
                    } else {
                        Helpers.dialogForLocationEnableManually(getActivity());
                    }
                } else {
                    Helpers.showSnackBar(getView(), R.string.permission_denied);
                }

                break;
        }
    }

    public boolean locationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(
                Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled || network_enabled;
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.i("TAG", " create location request");
        LocationRequest request = new LocationRequest();
        request.setInterval(2000); // two minute interval
        request.setFastestInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        client.requestLocationUpdates(request, locationCallback, null);
    }

    private void stopLocationUpdate() {
        client.removeLocationUpdates(locationCallback);

    }

}
