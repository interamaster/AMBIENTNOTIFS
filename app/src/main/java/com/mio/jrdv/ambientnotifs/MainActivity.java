package com.mio.jrdv.ambientnotifs;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mio.jrdv.ambientnotifs.receivers.DeviceAdmin;

import java.util.Set;

public class MainActivity extends AppCompatActivity {


    //v015 añadido que se encienda pantalla completa con uneca activity al recibir un whatsapp pte de pasarle la notificacion y sacar luego de ahi lo que queramos
    //V02 AÑADIDO PANTALLA EN GRIS CON DATOS DE TEXTO Y FOTO REMITENTE ..PTE DISEÑO
    //V03 AÑADIDO ICONO APK,IMPOSIBLE PONER FOTO ADJUNTA PUES SOLO SALE AL DESPLEGAR LA NOTIF,AÑADIDO AUTO BLOCK NOTIF A LOS 5 SECS,AÑADIDO LOGICA
    // SI MANDAN OTRO WHATSAPP COPN NOTIF ENCENDIDO..OTE DE IMPLEMENTAR

    //v04 AÑADIDO DETECTAR NUEVOS WHATASPP SEGUIDO Y QUE LOS PONGA EL TRUCO ERA:  dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

    //v045 ANULADAS LAS SBNM DUPLICADAS..WHATASPP MANDA 2 SEGUIDAS!!!


    //para el device manager

    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
   // private CheckBoxPreference mDeviceAdminPreference;


    //PARA VERLO DE MOMENTO  AQUI


    protected MyReceiver mReceiver = new MyReceiver();
    public static String INTENT_ACTION_NOTIFICATION = "com.mio.jrdv.ambientnotifs";

    protected TextView title;
    protected TextView text;
    protected TextView subtext;
    protected ImageView largeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //https://stackoverflow.com/questions/30246425/turning-on-screen-from-receiver-service:

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Retrieve ui elements
        title = (TextView) findViewById(R.id.nt_title);
        text = (TextView) findViewById(R.id.nt_text);
        subtext = (TextView) findViewById(R.id.nt_subtext);
        largeIcon = (ImageView) findViewById(R.id.nt_largeicon);



//ver si ya esta habilñotado el acceso a notifs

        if (!isNotificationAccessGiven()) {

            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }



        //ver si ya sou admin:

        EnableAdmin();

        // StartServiceYa();

        //en vez de arrancarlo comprobamo si ya esta runnnig!!

        if (!isMyServiceRunning(NOTIFSService.class)){
            StartServiceYa();
        }





    }



/*
//VOY  A PROBAR SIN DEVICE MANAGER

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////device manager//////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE == requestCode) {
            if (requestCode == Activity.RESULT_OK) {
                // done with activate to Device Admin
            } else {
                // cancle it.
            }
        }
    }


*/

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////saber si mi service esat runnig/////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////ARRANCAR SERVICE/////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void StartServiceYa() {

        Intent intent =new Intent(this,NOTIFSService.class);

        //TODO LOS NotificationListenerService NO TIENEN EXTRAS..CREO
       // intent.putExtra(NOTIFSService.EXTRA_MESSAGE,"DesdeMain");
       // intent.putExtra(NOTIFSService.EXTRA_TIME,"0");//al arranacra no le damos mas tiempo pero hay que psar el intent
        startService(intent);

        finish();
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////device manager//////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void EnableAdmin() {


        Log.i("MAIN:", "CHECK ADMIN?¿");

        try
        {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
            // Set DeviceAdminDemo Receiver for active the component with different option
            mAdminName = new ComponentName(this, DeviceAdmin.class);

            if (!mDPM.isAdminActive(mAdminName)) {
                // try to become active
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, R.string.device_admin_explanation);
                startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
            }
            else
            {

                Log.i("MAIN:", "YA SOY ADMIN!!!");
                // Already is a device administrator, can do security operations now.
                //TODO asi se puede bloquear!!! : mDPM.lockNow();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_ENABLE_ADMIN == requestCode)
        {
            if(requestCode == Activity.RESULT_OK)
            {
                // done with activate to Device Admin
            }
            else
            {
                // cancle it.
            }
        }
    }

 /*
// o asi:

private void initializeDeviceAdmin() {
        mDPM = (DevicePolicyManager)  getSystemService(Context.DEVICE_POLICY_SERVICE);
    mDeviceAdmin = new ComponentName(this, ScreenNotificationsDeviceAdminReceiver.class);
      //  mDeviceAdminPreference = (CheckBoxPreference) findPreference("device_admin");

        mDeviceAdminPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    // Launch the activity to have the user enable our admin.
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, R.string.device_admin_explanation);
                    startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);

                    // don't update checkbox until we're really active
                    return false;
                } else {
                    mDPM.removeActiveAdmin(mDeviceAdmin);


                    return true;
                }
            }
        });
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////device manager//////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkForActiveDeviceAdmin() {
        if(mDPM.isAdminActive(mDeviceAdmin)) {
            mDeviceAdminPreference.setChecked(true);

        } else {
            mDeviceAdminPreference.setChecked(false);

        }
    }


*/

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////SABER SI PUEDO LEER NOTIFS//////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //check notification access setting is enabled or not





    private boolean isNotificationAccessGiven() {
        boolean enabled = false;
        Set<String> enabledListenerPackagesSet = NotificationManagerCompat.getEnabledListenerPackages(MainActivity.this);
        for (String string: enabledListenerPackagesSet)
            if (string.contains(getPackageName())) enabled = true;
        return enabled;
    }





    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////PARA PASARLO A ALGUN SITIO DE MOMENTO A  MAIN//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("NEW", "recibido en MAIN!!!");

            if (intent != null) {
                Bundle extras = intent.getExtras();
                String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
                int notificationIcon = extras.getInt(Notification.EXTRA_SMALL_ICON);
                Bitmap notificationLargeIcon = ((Bitmap) extras.getParcelable(Notification.EXTRA_LARGE_ICON));
                CharSequence notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);
                CharSequence notificationSubText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);

                title.setText(notificationTitle);
                text.setText(notificationText);
                subtext.setText(notificationSubText);

                if (notificationLargeIcon != null) {
                    largeIcon.setImageBitmap(notificationLargeIcon);

                }




                }
            }


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mReceiver == null) mReceiver = new MyReceiver();
        registerReceiver(mReceiver, new IntentFilter(INTENT_ACTION_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////PARA PASARLO A ALGUN SITIO DE MOMENTO A  MAIN//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
