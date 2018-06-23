package com.byteshaft.carecare.fcm;

import android.util.Log;

import com.byteshaft.carecare.utils.AppGlobals;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FireBaseService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("TAG", "Token " + token);
        AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FCM_TOKEN, token);
    }
}
