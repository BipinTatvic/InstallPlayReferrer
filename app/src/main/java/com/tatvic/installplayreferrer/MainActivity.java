package com.tatvic.installplayreferrer;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    String referrerUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        try {
            InstallReferrerClient referrerClient;

            referrerClient = InstallReferrerClient.newBuilder(this).build();
//        ReferrerDetails response = null;
//        try {
//            response = referrerClient.getInstallReferrer();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        String referrerUrl = response.getInstallReferrer();
//        Log.d("TAG", "referrerUrl = "+referrerUrl);

            referrerClient.startConnection(new InstallReferrerStateListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onInstallReferrerSetupFinished(int responseCode) {
                    switch (responseCode) {
                        case InstallReferrerClient.InstallReferrerResponse.OK:
                            // Connection established.
                            ReferrerDetails response = null;
                            try {
                                response = referrerClient.getInstallReferrer();




                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            referrerUrl = response.getInstallReferrer();
                            long referrerClickTime = response.getReferrerClickTimestampSeconds();
                            long appInstallTime = response.getInstallBeginTimestampSeconds();
                            boolean instantExperienceLaunched = response.getGooglePlayInstantParam();

                            Log.d("TAG", "referrerUrl = " + referrerUrl);

                            String currentString = referrerUrl;
                            String[] separated = currentString.split("&");

                            Log.d("1", separated[0]);
                            Log.d("2", separated[1]);
                            Log.d("3", separated[2]);
                            Log.d("4", separated[3]);
                            Log.d("5", separated[4]);

                            Log.i("1", separated[0]);
                            Log.i("2", separated[1]);
                            Log.i("3", separated[2]);
                            Log.i("4", separated[3]);
                            Log.i("5", separated[4]);

                            String[] second_separated1 = separated[0].split("=");
                            String[] second_separated2 = separated[1].split("=");
                            String[] second_separated3 = separated[2].split("=");
                            String[] second_separated4 = separated[3].split("=");
                            String[] second_separated5 = separated[4].split("=");

                            Bundle bundle = new Bundle();
                            bundle.putString("utmSource", second_separated1[1]);
                            bundle.putString("utmMedium", second_separated2[1]);
                            bundle.putString("utmTerm", second_separated3[1]);
                            bundle.putString("utmContent", second_separated4[1]);
                            bundle.putString("utmCampaign", second_separated5[1]);
                            mFirebaseAnalytics.logEvent("tvc_campaign", bundle);

                            Log.d("referrerClickTime = ", String.valueOf(referrerClickTime));
                            Log.d("appInstallTime = ", String.valueOf(appInstallTime));
                            Log.d("instantExperienceLaunched = ", String.valueOf(instantExperienceLaunched));

                            break;
                        case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                            // API not available on the current Play Store app.
                            break;
                        case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                            // Connection couldn't be established.
                            break;
                    }
                }

                @Override
                public void onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    ReferrerDetails response = null;
                    try {
                        response = referrerClient.getInstallReferrer();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    String referrerUrl = response.getInstallReferrer();
                    Log.d("TAG", "referrerUrl = " + referrerUrl);
                }
            });
        }catch (Exception e) {
            e.printStackTrace();

            Log.i("Error: ", e.toString());
        }
    }
}