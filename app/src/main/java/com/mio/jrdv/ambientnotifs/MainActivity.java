package com.mio.jrdv.ambientnotifs;

import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mio.jrdv.ambientnotifs.textclock.TextClock;

public class MainActivity extends AppCompatActivity {


    //v015 añadido que se encienda pantalla completa con uneca activity al recibir un whatsapp pte de pasarle la notificacion y sacar luego de ahi lo que queramos
    //V02 AÑADIDO PANTALLA EN GRIS CON DATOS DE TEXTO Y FOTO REMITENTE ..PTE DISEÑO
    //V03 AÑADIDO ICONO APK,IMPOSIBLE PONER FOTO ADJUNTA PUES SOLO SALE AL DESPLEGAR LA NOTIF,AÑADIDO AUTO BLOCK NOTIF A LOS 5 SECS,AÑADIDO LOGICA
    // SI MANDAN OTRO WHATSAPP COPN NOTIF ENCENDIDO..OTE DE IMPLEMENTAR

    //v04 AÑADIDO DETECTAR NUEVOS WHATASPP SEGUIDO Y QUE LOS PONGA EL TRUCO ERA:  dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

    //v045 ANULADAS LAS SBNM DUPLICADAS..WHATASPP MANDA 2 SEGUIDAS!!!
    //v050 añadido clock modifcable en mis clases
    //v055 VERSION .8 DEL XML DEL NOTIF LISTA
    //v059 QUITADO ADMIN MANAGER PORQUE NOS EPODIA DESBLOQUEAR CON HUELLA
    //v065 AÑADIDO PANTALLAINICIAL DE PREFERENCIA CON QUIETE TIME PTE DE IMPLEMENTAR SU LOGICA PERO CON LOGGING EN SERVICE
    //v07 AÑADIDO QUIET TIME YA FUNCIONA OK
    //v08 AÑADIDO DETECCION OK DE GMAIL,TELEGRM Y OUTLOOK ..FALLA EL DE GMAIL AL ENCENDER PANTALLA
    //v081  AÑADIDO DETECCION DUPLICADOS  Y COLOR OK EN RELOJ
    //V082 AAÑADIDO DETECCION TLEGRAM X Y QUITADA NAV BAR
    //v09 AÑADIDO AUTODEJAR APAGAR PANTALLA A LSO 5 SEGS Y A LOS 12 SE AUTO ELIMINA(SUELEN SER 5+6 DE SCREENTIMOUT DEL LOCK
    //OJO PARA QUE NOP SALGAN LAS HEADS UP SI HAY MAS DE UNA NOTIFI HAY QUE HACERLO EN OREO EN AJUSTES DE AL APK NO MOISTRAR EN BLOQUEO

    //v091 CAMBIADO XML DE PRESENTACION Y DETCETA CLICK PARA HACER UN DIMISS

/*
    //para el device manager
    //mejor no o no funciona luego lahuella
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
   // private CheckBoxPreference mDeviceAdminPreference;

   */


    //PARA VERLO DE MOMENTO  AQUI




    protected TextView title;
    protected TextView text;
    protected TextView subtext;
    protected ImageView largeIcon, appicon;
    protected ImageView Fondodifuminar;


    //para el reloj color


    private TextClock reloj;


    // variable to track event time
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //https://stackoverflow.com/questions/30246425/turning-on-screen-from-receiver-service:

       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);



        super.onCreate(savedInstanceState);





    setContentView(R.layout.settings);

/*
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////PARA PROBAR LA VENTANA NOTIF////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON  ,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON) ;



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);





        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }


        setContentView(R.layout.alarm2);

        //Retrieve ui elements
        title = (TextView) findViewById(R.id.nt_title);
        text = (TextView) findViewById(R.id.nt_text);
        subtext = (TextView) findViewById(R.id.nt_subtext);
        largeIcon = (ImageView) findViewById(R.id.nt_largeicon);
        appicon = (ImageView) findViewById(R.id.iconoApp);

        reloj =(TextClock)findViewById(R.id.reloj);
        reloj.setColor(0xff075e54);


        title.setTextColor(0xff075e54);


        Fondodifuminar=(ImageView)findViewById(R.id.fondodifuminar);
        Fondodifuminar.setAlpha(0.0f);



        //aqui le damos el click listener al fondo:

        Fondodifuminar.setOnTouchListener(new View.OnTouchListener() {
            Handler handler = new Handler();

            int numberOfTaps = 0;
            long lastTapTimeMs = 0;
            long touchDownMs = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchDownMs = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacksAndMessages(null);

                        if ((System.currentTimeMillis() - touchDownMs) > ViewConfiguration.getTapTimeout()) {
                            //it was not a tap

                            numberOfTaps = 0;
                            lastTapTimeMs = 0;
                            break;
                        }

                        if (numberOfTaps > 0
                                && (System.currentTimeMillis() - lastTapTimeMs) < ViewConfiguration.getDoubleTapTimeout()) {
                            numberOfTaps += 1;
                        } else {
                            numberOfTaps = 1;
                        }

                        lastTapTimeMs = System.currentTimeMillis();

                        if (numberOfTaps == 2) {

                            //TODO FINALIZAMOS ..DEBERIA SER LANZAR NOTIF EN APP

                            finish();

                        }

                        if (numberOfTaps ==1){

                            Log.i("INFO", "pulsado para dismiss en DIFUMINAR VIEW");
                            Fondodifuminar.animate()
                                    .alpha(1f)
                                    .setDuration(1500)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something.

                                            Log.i("INFO", "FINAL DIFUMINAR VIEW");

                                            AutoLock();
                                        }
                                    })
                                    .start();
                        }



                }

                return true;
            }
        });

        //icono whastspp

        //el icono de wahastpp:
        Drawable appIcon = null;
        try {
            appIcon = getPackageManager().getApplicationIcon("com.whatsapp");

            appicon.setImageDrawable(appIcon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //la ponemos en balnco y negro:
/*
        // Create a paint object with 0 saturation (black and white)
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        Paint greyscalePaint = new Paint();
        greyscalePaint.setColorFilter(new ColorMatrixColorFilter(cm));
// Create a hardware layer with the greyscale paint

        LinearLayout RV=(LinearLayout) findViewById(R.id.relative1);
        RV.setLayerType(View.LAYER_TYPE_HARDWARE,greyscalePaint);

*/


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//ver si ya esta habilñotado el acceso a notifs
        /*

        //LO HAGO EN EL FRAGMET SETTINGS

        if (!isNotificationAccessGiven()) {

            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }

*/

        //ver si ya sou admin:
        //mejor no o no funciona luego lahuella
       // EnableAdmin();

        // StartServiceYa();

        //en vez de arrancarlo comprobamo si ya esta runnnig!!

        /*

        //LO HAGO EN EL FRAGMET SETTINGS
        if (!isMyServiceRunning(NOTIFSService.class)){
            StartServiceYa();
        }


*/



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


/*

//LO HAGO EN EL FRAGMET SETTINGS
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
*/

/*

//LO HAGO EN EL FRAGMET SETTINGS
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////ARRANCAR SERVICE/////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void StartServiceYa() {

        Intent intent =new Intent(this,NOTIFSService.class);

        //  LOS NotificationListenerService NO TIENEN EXTRAS..CREO
       // intent.putExtra(NOTIFSService.EXTRA_MESSAGE,"DesdeMain");
       // intent.putExtra(NOTIFSService.EXTRA_TIME,"0");//al arranacra no le damos mas tiempo pero hay que psar el intent
        startService(intent);

        finish();
    }



*/
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////device manager//////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //mejor no o no funciona luego lahuella
    /*
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
                // asi se puede bloquear!!! : mDPM.lockNow();
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


*/
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


 /*

 //LO HAGO EN EL FRAGMET SETTINGS
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

*/




    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void AutoLock() {



        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //  this.finish();
        //mDPM.lockNow();//mejor no o no funciona luego lahuella
    }
    /*
    public void pulsadodifuminar(View view) {



        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1600) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Log.i("INFO", "pulsado para dismiss en DIFUMINAR VIEW");


        /*
        // Make the object 50% transparent
        ObjectAnimator anim = ObjectAnimator.ofFloat(Fondodifuminar,"alpha",1.0f);
        anim.setDuration(1000); // duration 3 seconds
        anim.start();
        */
/*
        Fondodifuminar.animate()
                .alpha(1f)
                .setDuration(1500)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // Do something.

                        Log.i("INFO", "FINAL DIFUMINAR VIEW");

                        finish();
                    }
                })
                .start();
    }

*/
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////PARA PASARLO A ALGUN SITIO DE MOMENTO A  MAIN//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
