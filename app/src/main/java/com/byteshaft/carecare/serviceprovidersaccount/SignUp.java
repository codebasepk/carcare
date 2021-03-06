package com.byteshaft.carecare.serviceprovidersaccount;

import android.Manifest;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.carecare.utils.RealPathUtil;
import com.byteshaft.carecare.utils.RotateUtil;
import com.byteshaft.requests.FormData;
import com.byteshaft.requests.HttpRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class SignUp extends Fragment implements View.OnClickListener, HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener, RadioGroup.OnCheckedChangeListener {

    private View mBaseView;
    public static final int MULTIPLE_PERMISSIONS = 10;
    int PLACE_PICKER_REQUEST = 121;
    int FILE_PICK_CODE = 2001;
    private EditText etOrganizationName, etUsername, etEmail, etContactNumber, etContactPerson, etPassword, etVerifyPassword, etProviderDescription;

    private TextView etAddress, certificate, tvSelectType;

    private String organizationName, username, email, contactNumber, contactPerson, password, address, verifyPassword, addressCoordinates, providerDescription;

    private CircleImageView organizationImage;
    private File destination;
    private Uri selectedImageUri;
    private Uri certificateUri;
    private Bitmap profilePic;
    private String url;
    private RadioGroup radioGroup;

    private static String imageUrl = "";
    private String certificateFilePath = "";
    private String providerType = "Company";

    private static final int REQUEST_CAMERA = 3;
    private static final int STORAGE_CAMERA_PERMISSION = 1;
    private static final int SELECT_FILE = 2;

    private Button mButtonCreateAccoutn;
    private TextView mButtonLogin;
    ArrayList<String> options;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_service_provider_sign_up, container, false);
//        ((AppCompatActivity) getActivity()).getSupportActionBar()
//                .setTitle("Registration");
//        options = new ArrayList<>();
//        options.add("Mechanical");
//        options.add("Electrical");
//        options.add("Flat Tyre");
//        options.add("Emergency Repair");
//        MultiSelectSpinner multiSelectSpinner =  mBaseView.findViewById(R.id.multiselect_spinner);
//        ArrayAdapter<String> adapter = new ArrayAdapter <String>(getActivity(),
//                android.R.layout.simple_list_item_multiple_choice, options);
//        multiSelectSpinner.setListAdapter(adapter);
//        multiSelectSpinner.setMinSelectedItems(2);
//        multiSelectSpinner.setTitle("Expertise");
//        multiSelectSpinner.setListener(new BaseMultiSelectSpinner.MultiSpinnerListener() {
//            @Override
//            public void onItemsSelected(boolean[] selected) {
//            }
//        });

        radioGroup = mBaseView.findViewById(R.id.provider_type);
        etOrganizationName = mBaseView.findViewById(R.id.organization_edit_text);
        organizationImage = mBaseView.findViewById(R.id.organization_image);
        etUsername = mBaseView.findViewById(R.id.username_edit_text);
        etEmail = mBaseView.findViewById(R.id.email_edit_text);
        etContactNumber = mBaseView.findViewById(R.id.contact_number_edit_text);
        etContactPerson = mBaseView.findViewById(R.id.contact_person_edit_text);
        etPassword = mBaseView.findViewById(R.id.password_edit_text);
        etAddress = mBaseView.findViewById(R.id.address_edit_text);
        etProviderDescription = mBaseView.findViewById(R.id.provider_description);
        etVerifyPassword = mBaseView.findViewById(R.id.verify_password_edit_text);
        mButtonCreateAccoutn = mBaseView.findViewById(R.id.register_button);
        mButtonLogin = mBaseView.findViewById(R.id.sign_up_text_view);
        certificate = mBaseView.findViewById(R.id.tv_certificate);
        tvSelectType = mBaseView.findViewById(R.id.select_type);

        etAddress = mBaseView.findViewById(R.id.address_edit_text);
        mButtonCreateAccoutn.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);
        etAddress.setOnClickListener(this);
        organizationImage.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        certificate.setOnClickListener(this);

        if (AppGlobals.isLogin()) {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("Profile");
            radioGroup.setVisibility(View.GONE);
            tvSelectType.setVisibility(View.GONE);
            mButtonCreateAccoutn.setText("Update");
            etOrganizationName.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_ORGANIZATION_NAME));
            etOrganizationName.setSelection(etOrganizationName.getText().toString().length());
            etUsername.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_NAME));
            etEmail.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
            etEmail.setEnabled(false);
            etEmail.setCursorVisible(false);
            etUsername.setEnabled(false);
            etUsername.setCursorVisible(false);
            etProviderDescription.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_PROVIDER_DESCRIPTION));
            etContactPerson.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_CONTACT_PERSON));
            etContactNumber.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_CONTACT_NUMBER));
            etAddress.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_ADDRESS));
            etPassword.setVisibility(View.GONE);
            etVerifyPassword.setVisibility(View.GONE);
            mButtonLogin.setVisibility(View.GONE);
            address = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_ADDRESS);
            addressCoordinates = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LOCATION);
            if (AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SERVER_IMAGE) != null) {
                String url = AppGlobals.SERVER_IP + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SERVER_IMAGE);
                Picasso.with(AppGlobals.getContext())
                        .load(url)
                        .placeholder(R.drawable.background_image)// optional
                        .error(R.mipmap.ic_launcher)      // optional
                        .resize(250, 250)
                        .into(organizationImage);
            }
        }
        return mBaseView;
    }

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,};


    private boolean storagePermissons() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(AppGlobals.getContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                if (AppGlobals.isLogin()) {
                    updateProfile(etOrganizationName.getText().toString(), etContactPerson.getText().toString(),
                            addressCoordinates, etContactNumber.getText().toString(), address, imageUrl,etProviderDescription.getText().toString());
                } else {
                    if (validateEditText()) {
                        if (imageUrl.isEmpty()) {
                            registerUser(organizationName, email, username, contactPerson, addressCoordinates, contactPerson, address, password, providerDescription, providerType);
                        } else {
                            registerUserWithImage(organizationName, email, username, contactPerson, addressCoordinates, contactNumber, address, password, imageUrl, certificateFilePath,
                                    providerDescription, providerType);
                        }
                    }
                }
                break;
            case R.id.sign_up_text_view:
                ServiceProviderAccount.getInstance().loadFragment(new Login());
                break;
            case R.id.organization_image:
                checkPermissions();
                break;
            case R.id.address_edit_text:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_certificate:
                if (storagePermissons()) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, FILE_PICK_CODE);
                }
        }
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

    private void updateProfile(String name, String contactPerson, String coordinates,
                               String contactNumber, String address,
                               String imageUrl, String description) {
        Helpers.showProgressDialog(getActivity(), "Updating...");
        HttpRequest request = new HttpRequest(getContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int i) {
                switch (i) {
                    case HttpRequest.STATE_DONE:
                        Log.wtf("now ---------  ", request.getResponseText());
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Helpers.showSnackBar(getView(), "Profile Updated");
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(request.getResponseText());
                                    String address = jsonObject.getString(AppGlobals.KEY_ADDRESS);
                                    String contactNumber = jsonObject.getString(AppGlobals.KEY_CONTACT_NUMBER);
                                    String contactPerson = jsonObject.getString(AppGlobals.KEY_CONTACT_PERSON);
                                    String organizationName = jsonObject.getString(AppGlobals.KEY_ORGANIZATION_NAME);
                                    String profilePhoto = jsonObject.getString(AppGlobals.KEY_SERVER_IMAGE);
                                    String addressCoordinates = jsonObject.getString(AppGlobals.KEY_LOCATION);
                                    String providerDescription = jsonObject.getString(AppGlobals.KEY_PROVIDER_DESCRIPTION);

                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_CONTACT_NUMBER, contactNumber);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_CONTACT_PERSON, contactPerson);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_ADDRESS, address);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_LOCATION, addressCoordinates);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_ORGANIZATION_NAME, organizationName);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_SERVER_IMAGE, profilePhoto);
                                    AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_PROVIDER_DESCRIPTION, providerDescription);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.open("PUT", String.format("%sme", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(getProfileData(name, contactPerson, coordinates, contactNumber, address, imageUrl, description));
    }

    private FormData getProfileData(String name, String contactPerson, String coordinates,
                                    String contactNumber, String address,
                                    String imageUrl, String description) {

        FormData formData = new FormData();
        formData.append(FormData.TYPE_CONTENT_TEXT, "name", name);
        formData.append(FormData.TYPE_CONTENT_TEXT, "contact_person", contactPerson);
        formData.append(FormData.TYPE_CONTENT_TEXT, "contact_number", contactNumber);
        formData.append(FormData.TYPE_CONTENT_TEXT, "address", address);
        formData.append(FormData.TYPE_CONTENT_TEXT, "address_coordinates", coordinates);
        formData.append(FormData.TYPE_CONTENT_TEXT, "provider_description", description);
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            formData.append(FormData.TYPE_CONTENT_FILE, "profile_photo", imageUrl);
        }
        return formData;
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
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.select_file)),
                        SELECT_FILE);
            } else if (items[item].equals(getString(R.string.cancel))) {
                dialog.dismiss();
            }

        });
        builder.show();
    }

    private boolean validateEditText() {
        boolean valid = true;
        providerDescription = etProviderDescription.getText().toString();
        organizationName = etOrganizationName.getText().toString();
        username = etUsername.getText().toString();
        email = etEmail.getText().toString();
        contactNumber = etContactNumber.getText().toString();
        contactPerson = etContactPerson.getText().toString();
        password = etPassword.getText().toString();
        verifyPassword = etVerifyPassword.getText().toString();
        address = etAddress.getText().toString();

        if (providerType.contains("Freelancer") && certificateFilePath.isEmpty()) {
            valid = false;
            Toast.makeText(getContext(), "Please attache Certificate", Toast.LENGTH_SHORT).show();
        } else {
            valid = true;
        }

        if (imageUrl.isEmpty()) {
            valid = false;
            Toast.makeText(getContext(), "Please select an Image", Toast.LENGTH_SHORT).show();
        } else {
            valid = true;
        }
        if (providerDescription.trim().isEmpty()) {
            etProviderDescription.setError("required");
            valid = false;
        } else {
            etProviderDescription.setError(null);
        }

        if (organizationName.trim().isEmpty()) {
            etOrganizationName.setError("required");
            valid = false;
        } else {
            etOrganizationName.setError(null);
        }

        if (username.trim().isEmpty()) {
            etUsername.setError("required");
            valid = false;
        } else {
            etUsername.setError(null);
        }

        if (contactNumber.trim().isEmpty()) {
            etContactNumber.setError("required");
            valid = false;
        } else {
            etContactNumber.setError(null);
        }

        if (contactPerson.trim().isEmpty()) {
            etContactPerson.setError("required");
            valid = false;
        } else {
            etContactPerson.setError(null);
        }

        if (address.trim().isEmpty()) {
            etAddress.setError("required");
            valid = false;
        } else {
            etAddress.setError(null);
        }

        if (email.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please provide a valid email");
            valid = false;
        } else {
            etEmail.setError(null);
        }
        if (password.trim().isEmpty() || password.length() < 4) {
            etPassword.setError("enter at least 4 characters");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (verifyPassword.trim().isEmpty() || verifyPassword.length() < 4 ||
                !password.equals(verifyPassword)) {
            etVerifyPassword.setError("password does not match");
            valid = false;
        } else {
            etVerifyPassword.setError(null);
        }
        return valid;
    }


    private void registerUser(String name, String email, String userName, String contactPerson, String coordinates,
                              String contactNumber, String address, String password,
                              String description, String type) {
        Helpers.showProgressDialog(getActivity(), "Please wait...");
        HttpRequest request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sregister-provider", AppGlobals.BASE_URL));
        request.send(getRegisterData(name, email, userName, contactPerson, coordinates,
                contactNumber, address, password, description, type));
        Helpers.showProgressDialog(getActivity(), "Registering...");
    }

    private String getRegisterData(String name, String email, String userName, String contactPerson, String coordinates,
                                   String contactNumber, String address, String password,
                                   String description, String type) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", userName);
            jsonObject.put("name", name);
            jsonObject.put("email", email);
            jsonObject.put("contact_person", contactPerson);
            jsonObject.put("contact_number", contactNumber);
            jsonObject.put("address", address);
            jsonObject.put("provider_description", description);
            jsonObject.put("provider_type", type);
            jsonObject.put("address_coordinates", coordinates);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private void registerUserWithImage(String name, String email, String userName, String contactPerson, String coordinates,
                                       String contactNumber, String address, String password,
                                       String imageUrl, String file,
                                       String description, String type) {
        Helpers.showProgressDialog(getActivity(), "Please wait...");
        HttpRequest request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sregister-provider", AppGlobals.BASE_URL));
        request.send(getRegisterDataWithImage(name, email, userName, contactPerson, coordinates, contactNumber, address, password, imageUrl, file, description, type));
        Helpers.showProgressDialog(getActivity(), "Registering...");
    }

    private FormData getRegisterDataWithImage(String name, String email, String userName, String contactPerson, String coordinates,
                                              String contactNumber, String address, String password,
                                              String imageUrl, String file,
                                              String description, String type) {

        FormData formData = new FormData();
        formData.append(FormData.TYPE_CONTENT_TEXT, "name", name);
        formData.append(FormData.TYPE_CONTENT_TEXT, "email", email);
        formData.append(FormData.TYPE_CONTENT_TEXT, "username", userName);
        formData.append(FormData.TYPE_CONTENT_TEXT, "contact_person", contactPerson);
        formData.append(FormData.TYPE_CONTENT_TEXT, "contact_number", contactNumber);
        formData.append(FormData.TYPE_CONTENT_TEXT, "address", address);
        formData.append(FormData.TYPE_CONTENT_TEXT, "address_coordinates", coordinates);
        formData.append(FormData.TYPE_CONTENT_TEXT, "password", password);

        formData.append(FormData.TYPE_CONTENT_TEXT, "provider_type", type);
        formData.append(FormData.TYPE_CONTENT_TEXT, "provider_description", description);

        if (certificateFilePath != null && !certificateFilePath.trim().isEmpty()) {
            formData.append(FormData.TYPE_CONTENT_FILE, "freelancer_certificate", file);
        }
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            formData.append(FormData.TYPE_CONTENT_FILE, "profile_photo", imageUrl);
        }
        return formData;
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Log.wtf("Done", request.getResponseText());
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_CREATED:
                        AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                        ServiceProviderAccount.getInstance().loadFragment(new CodeConfrimation());
                        break;
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(getActivity(), getString(R.string.register_failed), getString(R.string.check_internet));
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        AppGlobals.alertDialog(getActivity(), getString(R.string.register_failed), getString(R.string.check_email));
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        AppGlobals.alertDialog(getActivity(), getString(R.string.register_failed), getString(R.string.check_password));
                        break;
                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
    }


    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.ok_button, okListener)
                .setNegativeButton(R.string.cancel, okListener)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                return;
            }
//            case LOCATION_PERMISSION:
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (locationEnabled()) {
//                        new UserSignUp.LocationTask().execute();
//                    } else {
//                        Helpers.dialogForLocationEnableManually(getActivity());
//                    }
//                } else {
//                    Helpers.showSnackBar(getView(), R.string.permission_denied);
//                }
//                break;
            case STORAGE_CAMERA_PERMISSION:
                if (grantResults.length > 0) {
                    // Check for both permissions
                    if (grantResults[0]
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.i("TAG", "permission granted !");
                        selectImage();
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                showDialogOK(getString(R.string.camera_storage_permission),
                                        (dialog, which) -> {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        });
                            }
                            //permission is denied (and never ask again is  checked)
                            //shouldShowRequestPermissionRationale will return false
                            else {
                                Toast.makeText(getActivity(), R.string.go_settings_permission, Toast.LENGTH_LONG)
                                        .show();
                                //                            //proceed with logic by disabling the related features or quit the
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
            if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, getActivity().getApplicationContext());
                LatLng latLng = place.getLatLng();
                String latitude = String.valueOf(latLng.latitude);
                String longitude = String.valueOf(latLng.longitude);

                addressCoordinates = latitude + "," + longitude;
                Log.wtf("Address:  ", addressCoordinates);
                address = (String) place.getName();
                etAddress.setText(place.getName());
            }
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
                organizationImage.setImageBitmap(orientedBitmap);
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
                organizationImage.setImageBitmap(orientedBitmap);
                imageUrl = String.valueOf(selectedImagePath);
            } else if (requestCode == FILE_PICK_CODE) {
                certificateFilePath = RealPathUtil.getPath(getContext(), data.getData());
                Log.wtf("ok", certificateFilePath);
                File file = new File(certificateFilePath);
                certificate.setText(file.getName());
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_button_company:
                certificate.setVisibility(View.GONE);
                providerType = "Company";
                certificateFilePath = "";
                break;
            case R.id.radio_button_contractor:
                certificate.setVisibility(View.GONE);
                providerType = "Contractor";
                certificateFilePath = "";
                break;
            case R.id.radio_button_seller:
                certificate.setVisibility(View.GONE);
                providerType = "Seller";
                certificateFilePath = "";
                break;
            case R.id.radio_button_freelancer:
                providerType = "Freelancer";
                certificate.setVisibility(View.VISIBLE);
                break;
        }

        Log.wtf("now", providerType);
    }
}
