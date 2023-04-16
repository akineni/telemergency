package com.telemergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;

public class ForgotPasswordActivity extends AppCompatActivity implements EmailRecoveryFragment.OnFragmentInteractionListener,
        PhoneNumberRecoveryFragment.OnFragmentInteractionListener {

    Fragment emailFragment = new EmailRecoveryFragment();
    Fragment phoneNumberFragment = new PhoneNumberRecoveryFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        onFragmentInteraction(Uri.parse(""));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Fragment newFragment = emailFragment;

        if(uri.toString() == "phone_number_recovery") {
            newFragment = phoneNumberFragment;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.recoveryFrameLayout, newFragment);
        transaction.commit();
    }
}
