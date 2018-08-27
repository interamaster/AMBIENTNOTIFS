package com.mio.jrdv.ambientnotifs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.mio.jrdv.ambientnotifs.helpers.NotificationServiceHelper;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private SharedPreferences mPrefs;//las uso con Myapplication

    private boolean mServiceActive;
    private CheckBoxPreference mServicePreference;

//para als app


    private CheckBoxPreference mWhastappPreference;
    private CheckBoxPreference mGmailPreference;
    private CheckBoxPreference mOutlookPreference;
    private CheckBoxPreference mTelegramPreference;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());//las uso con Myapplication


        findPreference("version").setSummary(BuildConfig.VERSION_NAME);


        initializeService();

        initializeTime();

        initializeApps();

        CheckApp();


    }

    private void CheckApp() {


        //1ºWhastspp ///////////////////////////////////////////////////////////////////////////////////

        mWhastappPreference = (CheckBoxPreference) findPreference("Whastapp");
        mWhastappPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mWhastappPreference.isChecked()) {

                    //lo guardamos a SI
                    mPrefs.edit().putBoolean("Whastapp",true).apply();


                    Log.i("Whastapp pref CIERTO:", String.valueOf(mPrefs.getBoolean("Whatsapp",true)));
                } else {

                    //lo guardamos a NO
                    mPrefs.edit().putBoolean("Whastapp",false).apply();
                    Log.i("Whastapp pref FALSO:", String.valueOf(mPrefs.getBoolean("Whastapp",true)));
                }

                // don't update checkbox until we're really active
                return false;
            }
        });


        //2º)Gmail ///////////////////////////////////////////////////////////////////////////////////

        mGmailPreference = (CheckBoxPreference) findPreference("Gmail");
        mGmailPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mGmailPreference.isChecked()) {

                    //lo guardamos a SI
                    mPrefs.edit().putBoolean("Gmail",true).apply();
                } else {

                    //lo guardamos a NO
                    mPrefs.edit().putBoolean("Gmail",false).apply();
                }

                // don't update checkbox until we're really active
                return false;
            }
        });



        //3º)Outlook ///////////////////////////////////////////////////////////////////////////////////


        mOutlookPreference = (CheckBoxPreference) findPreference("Outlook");
        mOutlookPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mOutlookPreference.isChecked()) {

                    //lo guardamos a SI
                    mPrefs.edit().putBoolean("Outlook",true).apply();
                } else {

                    //lo guardamos a NO
                    mPrefs.edit().putBoolean("Outlook",false).apply();
                }

                // don't update checkbox until we're really active
                return false;
            }
        });



        //4º)Telegram ///////////////////////////////////////////////////////////////////////////////////



        mTelegramPreference = (CheckBoxPreference) findPreference("Telegram");
        mTelegramPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mTelegramPreference.isChecked()) {

                    //lo guardamos a SI
                    mPrefs.edit().putBoolean("Telegram",true).apply();
                } else {

                    //lo guardamos a NO
                    mPrefs.edit().putBoolean("Telegram",false).apply();
                }

                // don't update checkbox until we're really active
                return false;
            }
        });


    }

    private void initializeApps() {


        //asi se saca el icono:

        /*

         //el icono de wahastpp:
                        Drawable appIcon = null;
                        try {
                            appIcon = getPackageManager().getApplicationIcon(packageNameWhataspp);

                            IconoApp.setImageDrawable(appIcon);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

         */


        //1º)whatsapp ///////////////////////////////////////////////////////////////////////////////////

        Drawable appIcon = null;
        try {


            appIcon =getActivity().getPackageManager().getApplicationIcon("com.whatsapp");



            Preference whatsapp = findPreference("Whastapp");
            whatsapp.setIcon(appIcon);
            //lo guardamos a SI
            mPrefs.edit().putBoolean("Whastapp",true).commit();

        } catch (PackageManager.NameNotFoundException e) {

            //si no existe el icono ..tampoc existe la app
            Preference whatsapp = findPreference("Whastapp");
            whatsapp.setEnabled(false);

            //lo guardamos a NO
            mPrefs.edit().putBoolean("Whastapp",false).commit();


            e.printStackTrace();
        }



        //2º)Gmail ///////////////////////////////////////////////////////////////////////////////////




        Drawable appIconGmail = null;
        try {


            appIconGmail =getActivity().getPackageManager().getApplicationIcon("com.gmail");



            Preference gmail = findPreference("Gmail");
            gmail.setIcon(appIconGmail);
            //lo guardamos a SI
            mPrefs.edit().putBoolean("Gmail",true).apply();

        } catch (PackageManager.NameNotFoundException e) {

            //si no existe el icono ..tampoc existe la app
            Preference gmail = findPreference("Gmail");
            gmail.setEnabled(false);

            //lo guardamos a NO
            mPrefs.edit().putBoolean("Gmail",false).apply();


            e.printStackTrace();
        }

        //3º)Outlook ///////////////////////////////////////////////////////////////////////////////////




        Drawable appIconOutlook = null;
        try {


            appIconOutlook=getActivity().getPackageManager().getApplicationIcon("com.outlook");



            Preference outlook = findPreference("Outlook");
            outlook.setIcon(appIconOutlook);
            //lo guardamos a SI
            mPrefs.edit().putBoolean("Outlook",true).apply();

        } catch (PackageManager.NameNotFoundException e) {

            //si no existe el icono ..tampoc existe la app
            Preference outlook = findPreference("Outlook");
            outlook.setEnabled(false);

            //lo guardamos a NO
            mPrefs.edit().putBoolean("Outlook",false).apply();


            e.printStackTrace();
        }

        //4º)Telegram ///////////////////////////////////////////////////////////////////////////////////




        Drawable appIconTelegram = null;
        try {


            appIconTelegram =getActivity().getPackageManager().getApplicationIcon("com.telegram");



            Preference telegram = findPreference("Telegram");
            telegram.setIcon(appIconTelegram);
            //lo guardamos a SI
            mPrefs.edit().putBoolean("Telegram",true).apply();

        } catch (PackageManager.NameNotFoundException e) {

            //si no existe el icono ..tampoc existe la app
            Preference telegram = findPreference("Telegram");
            telegram.setEnabled(false);

            //lo guardamos a NO
            mPrefs.edit().putBoolean("whastapp",false).apply();


            e.printStackTrace();
        }
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
        start.setSummary(handleTime(mPrefs.getString("startTime", "22:00")));//las uso con Myapplication

       // start.setSummary(handleTime(Myapplication.preferences.getString(Myapplication.startTime, "22:00")));
        stop.setSummary(handleTime(mPrefs.getString("stopTime", "08:00")));//las uso con Myapplication

       // stop.setSummary(handleTime(Myapplication.preferences.getString(Myapplication.stopTime, "08:00")));


        start.setOnPreferenceChangeListener(listener);
        stop.setOnPreferenceChangeListener(listener);

        Log.i("starquiettime: ",mPrefs.getString("startTime","null"));
        Log.i("stopquiettime:  ",mPrefs.getString("stopTime","null"));




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