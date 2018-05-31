package com.byteshaft.carecare;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.byteshaft.carecare.utils.AppGlobals;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceProviderActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView name;
    private TextView email;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider);
        View headerView;
        setTitle(getResources().getString(R.string.app_name));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        name = (headerView.findViewById(R.id.username_text_view));
        email = headerView.findViewById(R.id.user_email_text_view);
        profileImage = headerView.findViewById(R.id.org_image);
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container, fragment);
        tx.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        name.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_ORGANIZATION_NAME));
        email.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        if (AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SERVER_IMAGE) != null) {
            String url = AppGlobals.SERVER_IP + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SERVER_IMAGE);
            Picasso.with(AppGlobals.getContext())
                    .load(url)
                    .placeholder(R.drawable.background_image)// optional
                    .error(R.mipmap.ic_launcher)      // optional
                    .resize(250, 250)
                    .into(profileImage);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.service, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
