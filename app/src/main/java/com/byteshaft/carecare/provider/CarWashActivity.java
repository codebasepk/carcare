package com.byteshaft.carecare.provider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.gettersetter.CarWashService;
import com.byteshaft.carecare.utils.AppGlobals;
import com.byteshaft.carecare.utils.Helpers;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class CarWashActivity extends AppCompatActivity {

    private ListView listView;
    private List<CarWashService> arrayList;
    private ServiceAdapter adapter;

    private TextView emptyList;
    private boolean swipeRefresh = false;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_wash);
        setTitle("Car Wash");
        listView = findViewById(R.id.car_wash_list);
        arrayList = new ArrayList<>();
        emptyList = findViewById(R.id.tv_empty);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_wishlist);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            arrayList.clear();
            swipeRefresh = true;
            getAllCarWashServices();
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CarWashService service = arrayList.get(i);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                CarWashService service = arrayList.get(i);
                deleteDialog(service.getId());
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        arrayList.clear();
        getAllCarWashServices();
    }

    private void deleteDialog(int partId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.confirmation);
        alertDialogBuilder.setMessage(R.string.delete)
                .setCancelable(false).setPositiveButton(getString(R.string.yes),
                (dialog, id) -> {
                    deletePart(partId);
                });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deletePart(int id) {
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_NO_CONTENT:
                                Helpers.showSnackBar(listView, "Item Deleted");
                                arrayList.clear();
                                getAllCarWashServices();
                        }
                }
            }
        });
        request.open("DELETE", String.format("%sservice-station/services/%s", AppGlobals.BASE_URL, id));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_carparts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            startActivity(new Intent(CarWashActivity.this, AddCarWashService.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getAllCarWashServices() {
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        swipeRefresh = false;
                        swipeRefreshLayout.setRefreshing(false);
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                try {
                                    JSONObject object = new JSONObject(request.getResponseText());
                                    JSONArray jsonArray = object.getJSONArray("results");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        CarWashService carWashService = new CarWashService();
                                        JSONObject serviceObject = jsonObject.getJSONObject("service");
                                        carWashService.setId(jsonObject.getInt("id"));
                                        carWashService.setName(serviceObject.getString("name"));
                                        carWashService.setPrice(jsonObject.getString("price"));
                                        arrayList.add(carWashService);
                                    }

                                    if (arrayList.size() == 0) {
                                        emptyList.setVisibility(View.VISIBLE);
                                    } else {
                                        emptyList.setVisibility(View.GONE);
                                    }
                                    adapter = new ServiceAdapter(getApplicationContext(), arrayList);
                                    listView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.open("GET", String.format("%sservice-station/services", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }

    private class ServiceAdapter extends BaseAdapter {
        private ViewHolder viewHolder;
        private Context context;
        private List<CarWashService> listItems;

        public ServiceAdapter(Context context, List<CarWashService> wishlistItems) {
            this.context = context;
            this.listItems = wishlistItems;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater()
                        .inflate(R.layout.delegate_mechanic_service, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.price = convertView.findViewById(R.id.service_price);
                viewHolder.name = convertView.findViewById(R.id.service_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CarWashService items = listItems.get(position);
            viewHolder.price.setText("Price: " + items.getPrice());
            viewHolder.name.setText("Service: " + items.getName());

            return convertView;
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        private class ViewHolder {
            private TextView price;
            private TextView name;
        }
    }
}
