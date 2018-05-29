package com.byteshaft.carecare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.byteshaft.carecare.serviceprovidersaccount.ServiceProviderAccount;
import com.byteshaft.carecare.useraccounts.UserAccount;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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
