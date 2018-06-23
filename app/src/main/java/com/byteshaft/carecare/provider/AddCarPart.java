package com.byteshaft.carecare.provider;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.carecare.Adapters.VehicleMakeWithModel;
import com.byteshaft.carecare.Adapters.VehicleModelAdapter;
import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.CarCompanyItems;
import com.byteshaft.carecare.gettersetter.VehicleMakeWithModelItems;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.carecare.utils.RotateUtil;
import com.byteshaft.requests.FormData;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddCarPart extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private static final int SELECT_FILE = 2;
    private static final int REQUEST_CAMERA = 3;
    private static final int STORAGE_CAMERA_PERMISSION = 1;
    private File destination;
    private Uri selectedImageUri;
    private Bitmap profilePic;
    private String url;
    private static String imageUrl = "";

    private CircleImageView partImage;
    private EditText partDescription, partPrice;
    private Button addButton;
    private TextView pickYear;
    private String selectedDate;


    private VehicleModelAdapter vehicleModelAdapterAdapter;
    private ArrayList<VehicleMakeWithModelItems> arrayList;

    private VehicleMakeWithModel vehicleMakeAdapter;
    private ArrayList<CarCompanyItems> vehicleMakeArrayList;

    private int mVehicleMakeSpinnerId;
    private String mVehicleModelSpinnerString;

    private Spinner mVehicleModelSpinner;
    private Spinner mVehicleMakeSpinner;

    private DatePickerDialog.OnDateSetListener date;
    private Calendar mCalendar;

    private String description, price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car_part);
        setTitle("Add Car Part");
        mCalendar = Calendar.getInstance();
        partImage = findViewById(R.id.part_image);
        pickYear = findViewById(R.id.pick_year);
        addButton = findViewById(R.id.button_add);
        partDescription = findViewById(R.id.part_description);
        partPrice = findViewById(R.id.part_price);

        mVehicleModelSpinner = findViewById(R.id.vehicle_model_Spinner);
        mVehicleMakeSpinner = findViewById(R.id.vehicle_make_spinner);

        mVehicleModelSpinner.setOnItemSelectedListener(this);
        mVehicleMakeSpinner.setOnItemSelectedListener(this);
        arrayList = new ArrayList<>();
        vehicleMakeArrayList = new ArrayList<>();

        date = (view, year, monthOfYear, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        pickYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendar.add(Calendar.DAY_OF_YEAR, 0);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddCarPart.this, date, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        partImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                checkPermissions();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    addPart(description, String.valueOf(mVehicleMakeSpinnerId), mVehicleModelSpinnerString, price, imageUrl, selectedDate);
                    Log.wtf(" ok ", mVehicleModelSpinnerString + "  " + mVehicleMakeSpinnerId);
                }
            }
        });
        getVehicleMake();
        getVehicleModel(mVehicleMakeSpinnerId);

    }

    private void updateLabel() {
        String myFormat = "yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        pickYear.setText(sdf.format(mCalendar.getTime()));
        selectedDate = sdf.format(mCalendar.getTime());
    }

    private void addPart(String description, String make, String model, String price, String image, String year) {
        Helpers.showProgressDialog(AddCarPart.this, "Please wait...");
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_CREATED:
                                finish();
                                Helpers.showSnackBar(pickYear, "Item Added");
                        }
                }
            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {

            }
        });
        request.open("POST", String.format("%sprovider/parts", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(partData(description, make, model, price, image, year));
        Helpers.showProgressDialog(AddCarPart.this, "Adding...");
    }

    private FormData partData(String description, String make, String model, String price, String image, String year) {

        FormData formData = new FormData();
        formData.append(FormData.TYPE_CONTENT_TEXT, "description", description);
        formData.append(FormData.TYPE_CONTENT_TEXT, "make", make);
        formData.append(FormData.TYPE_CONTENT_TEXT, "model", model);
        formData.append(FormData.TYPE_CONTENT_TEXT, "price", price);
        formData.append(FormData.TYPE_CONTENT_TEXT, "end_year", year);
        formData.append(FormData.TYPE_CONTENT_TEXT, "start_year", year);
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            formData.append(FormData.TYPE_CONTENT_FILE, "image", image);
        }
        return formData;
    }

    public boolean validate() {
        boolean valid = true;
        description = partDescription.getText().toString();
        price = partPrice.getText().toString();

        if (selectedDate.trim().isEmpty()) {
            Toast.makeText(this, "Select Year", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (description.trim().isEmpty()) {
            partDescription.setError("Required");
            valid = false;
        } else {
            partDescription.setError(null);
        }

        if (price.trim().isEmpty()) {
            partPrice.setError("Required");
            valid = false;
        } else {
            partPrice.setError(null);
        }

        if (imageUrl.trim().isEmpty()) {
            Toast.makeText(this, "Add an Image", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_CAMERA_PERMISSION);
        } else {
            selectImage();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_library), getString(R.string.cancel_photo)};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddCarPart.this);
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


    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(AddCarPart.this)
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
                                Toast.makeText(getApplicationContext(), R.string.go_settings_permission, Toast.LENGTH_LONG)
                                        .show();
                                //                            //proceed with logic by disabling the related features or quit the
//                                Helpers.showSnackBar(getView(), R.string.permission_denied);
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
                partImage.setImageBitmap(orientedBitmap);
            } else if (requestCode == SELECT_FILE) {
                selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getApplicationContext(),
                        selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                profilePic = Helpers.getBitMapOfProfilePic(selectedImagePath);
                Bitmap orientedBitmap = RotateUtil.rotateBitmap(selectedImagePath, profilePic);
                partImage.setImageBitmap(orientedBitmap);
                imageUrl = String.valueOf(selectedImagePath);
            }
        }
    }

    private void getVehicleModel(int id) {
        HttpRequest getStateRequest = new HttpRequest(getApplicationContext());
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
                                vehicleModelAdapterAdapter = new VehicleModelAdapter(AddCarPart.this, arrayList);
                                mVehicleModelSpinner.setAdapter(vehicleModelAdapterAdapter);
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
        HttpRequest getStateRequest = new HttpRequest(getApplicationContext());
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
                                vehicleMakeAdapter = new VehicleMakeWithModel(AddCarPart.this, vehicleMakeArrayList);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.vehicle_model_Spinner:
                VehicleMakeWithModelItems vehicleMakeWithModelItems = arrayList.get(position);
                mVehicleModelSpinnerString = String.valueOf(vehicleMakeWithModelItems.getVehicleModelId());
                break;
            case R.id.vehicle_make_spinner:
                CarCompanyItems carCompanyItems = vehicleMakeArrayList.get(position);
                mVehicleMakeSpinnerId = carCompanyItems.getCompanyId();
                getVehicleModel(mVehicleMakeSpinnerId);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
