package com.byteshaft.carecare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.byteshaft.carecare.userFragments.UserHomeFragment;
import com.byteshaft.carecare.utils.AppGlobals;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView name;
    private TextView email;
    private CircleImageView profileImage;

    private static MainActivity sInstance;
    public static MainActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new UserHomeFragment());
        sInstance = this;
        View headerView;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

    @Override
    protected void onResume() {
        super.onResume();
        name.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_ORGANIZATION_NAME));
        email.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        if (AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SERVER_IMAGE) != null) {
            String url = AppGlobals.SERVER_IP + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_SERVER_IMAGE);
            Picasso.with(AppGlobals.getContext()).load(url)
                    .placeholder(R.drawable.background_image).error(R.mipmap.ic_launcher).resize(250, 250)
                    .into(profileImage);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home_provider) {
            loadFragment(new UserHomeFragment());
        } else if (id == R.id.nav_logout) {
            logOutDialog();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container, fragment);
        tx.commit();
    }

    private void logOutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.confirmation);
        alertDialogBuilder.setMessage(R.string.logout_text)
                .setCancelable(false).setPositiveButton(getString(R.string.yes),
                (dialog, id) -> {
                    AppGlobals.clearSettings();
                    dialog.dismiss();
                    startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                    finish();

                });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
