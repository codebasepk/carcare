package com.byteshaft.carecare.useraccounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.byteshaft.carecare.R;
import com.byteshaft.carecare.userFragments.UserHomeFragment;
import com.byteshaft.carecare.utils.AppGlobals;

public class UserAccount extends AppCompatActivity {

    private static UserAccount sInstance;
    public static UserAccount getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        if (AppGlobals.isLogin()) {
            loadFragment(new UserHomeFragment());
        } else {
            loadFragment(new UserLogin());
        }

        sInstance = this;
    }

    public void loadLogin(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.user_account_container, fragment, backStateName);
        fragmentTransaction.commit();
    }

    public void  loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.user_account_container, fragment, backStateName);
        FragmentManager manager = getSupportFragmentManager();
        Log.e("TAG", backStateName);
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) {
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();
        }
    }
}
