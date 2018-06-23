package com.byteshaft.carecare.useraccounts;

import android.Manifest;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.carecare.Adapters.VehicleMakeWithModel;
import com.byteshaft.carecare.Adapters.VehicleModelAdapter;
import com.byteshaft.carecare.Adapters.VehicleType;
import com.byteshaft.carecare.R;
import com.byteshaft.carecare.WelcomeActivity;
import com.byteshaft.carecare.gettersetter.CarCompanyItems;
import com.byteshaft.carecare.gettersetter.VehicleMakeItems;
import com.byteshaft.carecare.gettersetter.VehicleMakeWithModelItems;
import com.byteshaft.carecare.gettersetter.VehicleTypeItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.carecare.utils.RotateUtil;
import com.byteshaft.requests.FormData;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class UserSignUp extends Fragment implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int STORAGE_CAMERA_PERMISSION = 1;
    private static final int SELECT_FILE = 2;
    private static final int LOCATION_PERMISSION = 4;
    private static final int REQUEST_CAMERA = 3;
    private static String imageUrl = "";
    public String mContactNumberString;
    private View mBaseView;
    private Button mSignUpButtonButton;
    private CircleImageView mUserImage;
    private EditText mFullNameEditText;
    private EditText mUserNameEditText;
    private EditText mContactNumberEditText;
    private EditText mEmailAddressEditText;
    private EditText mPasswordEditText;
    private EditText mVerifyPasswordEditText;
    private EditText mAddressEditText;
    private EditText mVehicleYearEditText;
    private TextView mPickForCurrentLocation;
    private TextView mSignInTextView;
    private Spinner mVehicleModelSpinner;
    private Spinner mVehicleMakeSpinner;
    private Spinner mVehicleTypeSpinner;
    private VehicleModelAdapter vehicleModelAdapter;
    private ArrayList<VehicleMakeWithModelItems> arrayList;
    private VehicleMakeWithModel vehicleMakeAdapter;
    private ArrayList<CarCompanyItems> vehicleMakeArrayList;
    private VehicleType vehicleTypeAdapter;
    private ArrayList<VehicleTypeItems> vehicleTypeArrayList;
    private int mVehicleMakeSpinnerId;
    private String mVehicleModelSpinnerString;
    private String mVehicleTypeSpinnerString;
    private String mUserNameEditTextString;
    private String mVehicleYearString;
    private String mAddressEditTextString;
    private String mFullNameString;
    private String mEmailAddressString;
    private String mVerifyPasswordString;
    private String mPasswordString;
    private String mLocationString;
    private HttpRequest request;
    private int locationCounter = 0;
    private File destination;
    private Uri selectedImageUri;
    private Bitmap profilePic;

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
                System.out.println("Lat: " + locationResult.getLastLocation().getLatitude() + "Long: " + locationResult.getLastLocation().getLongitude());
                getAddress(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
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
        mBaseView = inflater.inflate(R.layout.fragment_user_sign_up, container, false);
        client = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());
        mFullNameEditText = mBaseView.findViewById(R.id.full_name_edit_text);
        mUserNameEditText = mBaseView.findViewById(R.id.user_name_edit_text);
        mEmailAddressEditText = mBaseView.findViewById(R.id.email_edit_text);
        mContactNumberEditText = mBaseView.findViewById(R.id.contact_number_edit_text);
        mUserImage = mBaseView.findViewById(R.id.user_image);
        mVehicleYearEditText = mBaseView.findViewById(R.id.vehicle_year_edit_text);

        mPasswordEditText = mBaseView.findViewById(R.id.password_edit_text);
        mVerifyPasswordEditText = mBaseView.findViewById(R.id.confirm_edit_text);
        mAddressEditText = mBaseView.findViewById(R.id.address_edit_text);
        mPickForCurrentLocation = mBaseView.findViewById(R.id.pick_for_current_location);
        mSignInTextView = mBaseView.findViewById(R.id.sign_in_text_view);

        mSignUpButtonButton = mBaseView.findViewById(R.id.sign_up_button);
        mVehicleModelSpinner = mBaseView.findViewById(R.id.vehicle_model_Spinner);
        mVehicleMakeSpinner = mBaseView.findViewById(R.id.vehicle_make_spinner);
        mVehicleTypeSpinner = mBaseView.findViewById(R.id.vehicle_type_spinner);

        vehicleTypeArrayList = new ArrayList<>();
        arrayList = new ArrayList<>();
        vehicleMakeArrayList = new ArrayList<>();
        mPickForCurrentLocation.setOnClickListener(this);

        mSignInTextView.setOnClickListener(this);
        mSignUpButtonButton.setOnClickListener(this);
        mVehicleModelSpinner.setOnItemSelectedListener(this);
        mVehicleTypeSpinner.setOnItemSelectedListener(this);
        mVehicleMakeSpinner.setOnItemSelectedListener(this);
        mUserImage.setOnClickListener(this);

        getVehicleType();
        getVehicleMake();
        getVehicleModel(mVehicleMakeSpinnerId);
        return mBaseView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_text_view:
                UserAccount.getInstance().loadFragment(new UserLogin());
                break;
            case R.id.user_image:
                checkPermissions();
                break;
            case R.id.pick_for_current_location:
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

                break;
            case R.id.sign_up_button:
                if (validateEditText()) {
                    if (imageUrl.isEmpty()) {
                        registerUser(mUserNameEditTextString, mFullNameString, mEmailAddressString, mContactNumberString,
                                mAddressEditTextString, mLocationString, String.valueOf(mVehicleMakeSpinnerId), mVehicleModelSpinnerString,
                                mVehicleTypeSpinnerString, mVehicleYearString, mPasswordString);
                    } else {
                        registerUserWithImage(mUserNameEditTextString, mFullNameString, mEmailAddressString, mContactNumberString,
                                mAddressEditTextString, mLocationString, String.valueOf(mVehicleMakeSpinnerId), mVehicleModelSpinnerString,
                                mVehicleTypeSpinnerString, mVehicleYearString, mPasswordString, imageUrl);
                    }

                }
                break;
        }
    }


    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.ok_button, okListener)
                .setNegativeButton(R.string.cancel, okListener)
                .create()
                .show();
    }

    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_CAMERA_PERMISSION);
        } else {
            selectImage();
        }
    }

    public boolean locationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_library), getString(R.string.cancel_photo)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_photo);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals(getString(R.string.take_photo))) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            } else if (items[item].equals(getString(R.string.choose_library))) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)),
                        SELECT_FILE);
            } else if (items[item].equals(getString(R.string.cancel))) {
                dialog.dismiss();
            }

        });
        builder.show();
    }


    private void getVehicleModel(int id) {
        HttpRequest getStateRequest = new HttpRequest(getActivity());
        getStateRequest.setOnReadyStateChangeListener((request, readyState) -> {
            switch (readyState) {
                case HttpRequest.STATE_DONE:
                    switch (request.getStatus()) {
                        case HttpURLConnection.HTTP_OK:
                            arrayList = new ArrayList<>();
                            try {
                                JSONObject jsonObject = new JSONObject(request.getResponseText());
                                JSONArray jsonArray = jsonObject.getJSONArray("results");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    System.out.println("working " + jsonArray.getJSONObject(i));
                                    JSONObject VehicleTypeJsonObject = jsonArray.getJSONObject(i);
                                    VehicleMakeWithModelItems vehicleMakeWithModelItems = new VehicleMakeWithModelItems();
                                    vehicleMakeWithModelItems.setVehicleModelId(VehicleTypeJsonObject.getInt("id"));
                                    vehicleMakeWithModelItems.setVehicleModelName(VehicleTypeJsonObject.getString("name"));
                                    arrayList.add(vehicleMakeWithModelItems);
                                }
                                vehicleModelAdapter = new VehicleModelAdapter(getActivity(), arrayList);
                                mVehicleModelSpinner.setAdapter(vehicleModelAdapter);
                                mVehicleModelSpinner.setSelection(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
            }
        });
        getStateRequest.open("GET", String.format("%svehicles/make/%s/models", AppGlobals.BASE_URL, id));
        getStateRequest.send();
    }


    private void getVehicleMake() {
        HttpRequest getStateRequest = new HttpRequest(getActivity());
        getStateRequest.setOnReadyStateChangeListener((request, readyState) -> {
            switch (readyState) {
                case HttpRequest.STATE_DONE:
                    switch (request.getStatus()) {
                        case HttpURLConnection.HTTP_OK:
                            try {
                                JSONObject jsonObject = new JSONObject(request.getResponseText());
                                JSONArray jsonArray = jsonObject.getJSONArray("results");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    System.out.println("Test " + jsonArray.getJSONObject(i));
                                    JSONObject vehicleMakeJsonObject = jsonArray.getJSONObject(i);
                                    CarCompanyItems carCompanyItems = new CarCompanyItems();
                                    carCompanyItems.setCompanyId(vehicleMakeJsonObject.getInt("id"));
                                    carCompanyItems.setCompanyName(vehicleMakeJsonObject.getString("name"));
                                    vehicleMakeArrayList.add(carCompanyItems);
                                }
                                vehicleMakeAdapter = new VehicleMakeWithModel(getActivity(), vehicleMakeArrayList);
                                mVehicleMakeSpinner.setAdapter(vehicleMakeAdapter);
                                mVehicleMakeSpinner.setSelection(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
            }
        });
        getStateRequest.open("GET", String.format("%svehicles/make", AppGlobals.BASE_URL));
        getStateRequest.send();
    }

    private void getVehicleType() {
        HttpRequest getStateRequest = new HttpRequest(getActivity());
        getStateRequest.setOnReadyStateChangeListener((request, readyState) -> {
            switch (readyState) {
                case HttpRequest.STATE_DONE:
                    switch (request.getStatus()) {
                        case HttpURLConnection.HTTP_OK:
                            try {
                                JSONObject jsonObject = new JSONObject(request.getResponseText());
                                JSONArray jsonArray = jsonObject.getJSONArray("results");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    System.out.println("Test " + jsonArray.getJSONObject(i));
                                    JSONObject VehicleTypejsonObject = jsonArray.getJSONObject(i);
                                    VehicleTypeItems vehicleItems = new VehicleTypeItems();
                                    vehicleItems.setVehicleTypeId(VehicleTypejsonObject.getInt("id"));
                                    vehicleItems.setVehicleTypeName(VehicleTypejsonObject.getString("name"));
                                    vehicleTypeArrayList.add(vehicleItems);
                                }
                                vehicleTypeAdapter = new VehicleType(getActivity(), vehicleTypeArrayList);
                                mVehicleTypeSpinner.setAdapter(vehicleTypeAdapter);
                                mVehicleTypeSpinner.setSelection(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
            }
        });
        getStateRequest.open("GET", String.format("%svehicles/type", AppGlobals.BASE_URL));
        getStateRequest.send();
    }

    private void registerUserWithImage(String username, String name, String email, String contactNumber,
                              String address, String addressCoordinates, String vehiclMake, String vehicleModel,
                              String vehicleType, String vehicleYear, String password, String profilePhoto) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sregister-customer", AppGlobals.BASE_URL));
        request.send(getRegisterDataWithImage(username, name, email, contactNumber, address, addressCoordinates,
                vehiclMake, vehicleModel, vehicleType, vehicleYear, password, profilePhoto));
        Helpers.showProgressDialog(getActivity(), "Registering User");
    }


    private FormData getRegisterDataWithImage(String username, String name, String email, String contactNumber,
                                     String address, String addressCoordinates, String vehiclMake,
                                     String vehicleModel,
                                     String vehicleType, String vehicleYear, String password,
                                     String profilePhoto) {
        FormData formData = new FormData();
        formData.append(FormData.TYPE_CONTENT_TEXT, "username", username);
        formData.append(FormData.TYPE_CONTENT_TEXT, "name", name);
        formData.append(FormData.TYPE_CONTENT_TEXT, "email", email);
        formData.append(FormData.TYPE_CONTENT_TEXT, "contact_number", contactNumber);
        formData.append(FormData.TYPE_CONTENT_TEXT, "address", address);
        formData.append(FormData.TYPE_CONTENT_TEXT, "address_coordinates", addressCoordinates);
        formData.append(FormData.TYPE_CONTENT_TEXT, "vehicle_make", vehiclMake);
        formData.append(FormData.TYPE_CONTENT_TEXT, "vehicle_model", vehicleModel);
        formData.append(FormData.TYPE_CONTENT_TEXT, "vehicle_type", vehicleType);
        formData.append(FormData.TYPE_CONTENT_TEXT, "vehicle_year", vehicleYear);
        formData.append(FormData.TYPE_CONTENT_TEXT, "password", password);
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            formData.append(FormData.TYPE_CONTENT_FILE, "profile_photo", profilePhoto);
        }
        return formData;

    }

    private void registerUser(String username, String name, String email, String contactNumber,
                                       String address, String addressCoordinates, String vehiclMake, String vehicleModel,
                                       String vehicleType, String vehicleYear, String password) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sregister-customer", AppGlobals.BASE_URL));
        request.send(getRegisterData(username, name, email, contactNumber, address, addressCoordinates,
                vehiclMake, vehicleModel, vehicleType, vehicleYear, password));
        Helpers.showProgressDialog(getActivity(), "Registering User");
    }

    private String getRegisterData(String username, String name, String email, String contactNumber,
                                              String address, String addressCoordinates, String vehiclMake,
                                              String vehicleModel,
                                              String vehicleType, String vehicleYear, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("name", name);
            jsonObject.put("email", email);
            jsonObject.put("contact_number", contactNumber);
            jsonObject.put("address", address);
            jsonObject.put("address_coordinates", addressCoordinates);
            jsonObject.put("vehicle_make", vehiclMake);
            jsonObject.put("vehicle_model", vehicleModel);
            jsonObject.put("vehicle_type", vehicleType);
            jsonObject.put("vehicle_year", vehicleYear);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

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
                System.out.println(request.getResponseText());
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(getActivity(), getString(R.string.login_failed), getString(R.string.check_internet));
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        AppGlobals.alertDialog(getActivity(), getString(R.string.register_failed), getString(R.string.check_email));
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        AppGlobals.alertDialog(getActivity(), getString(R.string.register_failed), getString(R.string.check_password));
                        break;
                    case HttpURLConnection.HTTP_CREATED:
                        Toast.makeText(getActivity(), R.string.activation_code_send_message, Toast.LENGTH_SHORT).show();
                        System.out.println(request.getResponseText() + "working ");
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String token = jsonObject.getString(AppGlobals.KEY_TOKEN);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String fullName = jsonObject.getString(AppGlobals.KEY_FULL_NAME);
                            String userName = jsonObject.getString(AppGlobals.KEY_USER_NAME);

                            String contactNumber = jsonObject.getString(AppGlobals.KEY_CONTACT_NUMBER);
                            String vehicleModel = jsonObject.getString(AppGlobals.KEY_VEHICLE_MODEL);

                            String vehicleYear = jsonObject.getString(AppGlobals.KEY_VEHICLE_YEAR);
                            String location = jsonObject.getString(AppGlobals.KEY_LOCATION);
                            String address = jsonObject.getString(AppGlobals.KEY_ADDRESS);
                            String profilePhoto = jsonObject.getString(AppGlobals.KEY_SERVER_IMAGE);

                            JSONObject vehicleTypeJSONObject = jsonObject.getJSONObject("vehicle_type");
                            String vehicleTypeId = vehicleTypeJSONObject.getString(AppGlobals.KEY_VEHICLE_TYPE_SERVER_ID);
                            String vehicleTypeName = vehicleTypeJSONObject.getString(AppGlobals.KEY_VEHICLE_TYPE_NAME);
                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_VEHICLE_TYPE_ID, vehicleTypeId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_VEHICLE_TYPE_SERVER_NAME, vehicleTypeName);

                            JSONObject vehicleMakeJSONObject = jsonObject.getJSONObject("vehicle_make");
                            String vehicleMakeId = vehicleMakeJSONObject.getString(AppGlobals.KEY_VEHICLE_MAKE_SERVER_ID);
                            String vehicleMakeName = vehicleMakeJSONObject.getString(AppGlobals.KEY_VEHICLE_MAKE_SERVER_NAME);
                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_VEHICLE_MAKE_ID, vehicleMakeId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_VEHICLE_MAKE_NAME, vehicleMakeName);


                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TOKEN, token);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FULL_NAME, fullName);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_CONTACT_NUMBER, contactNumber);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_NAME, userName);

                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_VEHICLE_MODEL, vehicleModel);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_VEHICLE_YEAR, vehicleYear);

                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_LOCATION, location);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_SERVER_IMAGE, profilePhoto);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_ADDRESS, address);
                            WelcomeActivity.getInstance().finish();
                            UserAccount.getInstance().loadFragment(new CodeConfirmation());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
        }

    }

    private boolean validateEditText() {
        boolean valid = true;
        mPasswordString = mPasswordEditText.getText().toString();
        mVerifyPasswordString = mVerifyPasswordEditText.getText().toString();
        mEmailAddressString = mEmailAddressEditText.getText().toString();
        mUserNameEditTextString = mUserNameEditText.getText().toString();
        mContactNumberString = mContactNumberEditText.getText().toString();
        mFullNameString = mFullNameEditText.getText().toString();
        mAddressEditTextString = mAddressEditText.getText().toString();
        mVehicleYearString = mVehicleYearEditText.getText().toString();

        if (mPasswordString.trim().isEmpty() || mPasswordString.length() < 4) {
            mPasswordEditText.setError("enter at least 4 characters");
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        if (mVerifyPasswordString.trim().isEmpty() || mVerifyPasswordString.length() < 4 ||
                !mVerifyPasswordString.equals(mPasswordString)) {
            mVerifyPasswordEditText.setError("password does not match");
            valid = false;
        } else {
            mVerifyPasswordEditText.setError(null);
        }
        if (mContactNumberString.trim().isEmpty()) {
            mContactNumberEditText.setError("required");
            valid = false;
        } else {
            mContactNumberEditText.setError(null);
        }

        if (mAddressEditTextString.trim().isEmpty()) {
            mAddressEditText.setError("please click on pick for current location for address");
            valid = false;
        } else {
            mAddressEditText.setError(null);
        }

        if (mVehicleYearString.trim().isEmpty()) {
            mVehicleYearEditText.setError("required");
            valid = false;
        } else {
            mVehicleYearEditText.setError(null);
        }


        if (mFullNameString.trim().isEmpty()) {
            mFullNameEditText.setError("required");
            valid = false;
        } else {
            mFullNameEditText.setError(null);
        }

        if (mEmailAddressString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(
                mEmailAddressString).matches()) {
            mEmailAddressEditText.setError("please provide a valid email");
            valid = false;
        } else {
            mEmailAddressEditText.setError(null);
        }
        return valid;
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
            case STORAGE_CAMERA_PERMISSION:
                if (grantResults.length > 0) {
                    if (grantResults[0]
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.i("TAG", "permission granted !");
                        selectImage();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                showDialogOK(getString(R.string.camera_storage_permission),
                                        (dialog, which) -> {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        });
                            } else {
                                Toast.makeText(getActivity(), R.string.go_settings_permission, Toast.LENGTH_LONG)
                                        .show();
                                Helpers.showSnackBar(getView(), R.string.permission_denied);
                            }
                        }
                        break;
                    }

                }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                imageUrl = destination.getAbsolutePath();
                FileOutputStream fileOutputStream;
                try {
                    destination.createNewFile();
                    fileOutputStream = new FileOutputStream(destination);
                    fileOutputStream.write(bytes.toByteArray());
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profilePic = Helpers.getBitMapOfProfilePic(destination.getAbsolutePath());
                Bitmap orientedBitmap = RotateUtil.rotateBitmap(destination.getAbsolutePath(), profilePic);
                mUserImage.setImageBitmap(orientedBitmap);
            } else if (requestCode == SELECT_FILE) {
                selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                profilePic = Helpers.getBitMapOfProfilePic(selectedImagePath);
                Bitmap orientedBitmap = RotateUtil.rotateBitmap(selectedImagePath, profilePic);
                mUserImage.setImageBitmap(orientedBitmap);
                imageUrl = String.valueOf(selectedImagePath);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.vehicle_type_spinner:
                VehicleTypeItems vehicleTypeItems = vehicleTypeArrayList.get(position);
                mVehicleTypeSpinnerString = String.valueOf(vehicleTypeItems.getVehicleTypeId());
                break;
            case R.id.vehicle_model_Spinner:
                VehicleMakeWithModelItems vehicleMakeWithModelItems = arrayList.get(position);
                mVehicleModelSpinnerString = String.valueOf(vehicleMakeWithModelItems.getVehicleModelId());
                break;
            case R.id.vehicle_make_spinner:
                CarCompanyItems carCompanyItems = vehicleMakeArrayList.get(position);
                mVehicleMakeSpinnerId = carCompanyItems.getCompanyId();
                Log.e("TAG", "  " + mVehicleMakeSpinnerId);
                getVehicleModel(mVehicleMakeSpinnerId);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getAddress(double latitude, double longitude) {
        final StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(AppGlobals.getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getLocality()).append(" ").append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        getActivity().runOnUiThread(() -> mAddressEditText.setText(result.toString()));
    }


    public void startLocationUpdates() {
        Helpers.showSnackBar(getView(), R.string.acquiring_location);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.i("TAG", " create location request");
        LocationRequest request = new LocationRequest();
        request.setInterval(1000); // two minute interval
        request.setFastestInterval(500);
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        client.requestLocationUpdates(request, locationCallback, null);
    }

    private void stopLocationUpdate() {
        client.removeLocationUpdates(locationCallback);
    }
}
