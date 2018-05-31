package com.byteshaft.carecare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.byteshaft.carecare.serviceprovidersaccount.ServiceProviderAccount;
import com.byteshaft.carecare.useraccounts.UserAccount;
import com.byteshaft.carecare.utils.AppGlobals;

public class WelcomeActivity extends AppCompatActivity {
    private static WelcomeActivity sInstance;

    public static WelcomeActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_TYPE).equals("3") && AppGlobals.isLogin()) {
            startActivity(new Intent(WelcomeActivity.this, ServiceProviderActivity.class));
            finish();
        } else if (AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_TYPE).equals("2") && AppGlobals.isLogin()) {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_welcome);
        sInstance = this;
        Button customerButton = findViewById(R.id.button_customer);
        Button providerButton = findViewById(R.id.button_service_provider);

        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, UserAccount.class));

            }
        });

        providerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, ServiceProviderAccount.class));
            }
        });

    }
}
