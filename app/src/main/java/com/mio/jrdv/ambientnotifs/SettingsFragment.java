package com.mio.jrdv.ambientnotifs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import com.mio.jrdv.ambientnotifs.helpers.NotificationServiceHelper;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private SharedPreferences mPrefs;

    private boolean mServiceActive;
    private CheckBoxPreference mServicePreference;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        findPreference("version").setSummary(BuildConfig.VERSION_NAME);


        initializeService();

        initializeTime();


    }

    @Override
    public void onStart() {
        super.onStart();


    }

    public void onResume() {
        super.onResume();

        checkForRunningService();

    }

    private void initializeService() {
        mServicePreference = (CheckBoxPreference) findPreference("service");
        mServicePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mServiceActive) {
                   // showServiceDialog(R.string.notification_listener_launch);
                    showServiceDialog(R.string.notification_listener_launch);
                } else {
                   // showServiceDialog(R.string.notification_listener_warning);
                    showServiceDialog(R.string.notification_listener_warning);
                }

                // don't update checkbox until we're really active
                return false;
            }
        });
    }

    private void checkForRunningService() {
        mServiceActive = NotificationServiceHelper.isServiceRunning(getActivity());
        if (mServiceActive) {
            mServicePreference.setChecked(true);
            enableOptions(true);
        } else {
            mServicePreference.setChecked(false);
            enableOptions(false);
        }
    }



    private void initializeTime() {
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(handleTime(newValue.toString()));
                return true;
            }
        };

        Preference start = findPreference("startTime");
        Preference stop = findPreference("stopTime");
        start.setSummary(handleTime(mPrefs.getString("startTime", "22:00")));
        stop.setSummary(handleTime(mPrefs.getString("stopTime", "08:00")));
        start.setOnPreferenceChangeListener(listener);
        stop.setOnPreferenceChangeListener(listener);


    }

    private void enableOptions(boolean enable) {

        findPreference("quiet").setEnabled(enable);
        findPreference("startTime").setEnabled(enable);
        findPreference("stopTime").setEnabled(enable);
      //  findPreference("status-bar").setEnabled(enable);
    }

    private String handleTime(String time) {
        String[] timeParts = time.split(":");
        int lastHour = Integer.parseInt(timeParts[0]);
        int lastMinute = Integer.parseInt(timeParts[1]);

        boolean is24HourFormat = DateFormat.is24HourFormat(getActivity());

        if(is24HourFormat) {
            return ((lastHour < 10) ? "0" : "")
                    + Integer.toString(lastHour)
                    + ":" + ((lastMinute < 10) ? "0" : "")
                    + Integer.toString(lastMinute);
        } else {
            int myHour = lastHour % 12;
            return ((myHour == 0) ? "12" : ((myHour < 10) ? "0" : "") + Integer.toString(myHour))
                    + ":" + ((lastMinute < 10) ? "0" : "")
                    + Integer.toString(lastMinute)
                    + ((lastHour >= 12) ? " PM" : " AM");
        }
    }

    private void showServiceDialog(int message) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface alertDialog, int id) {
                        alertDialog.cancel();
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    }
                })
                .show();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        return false;
    }
}
