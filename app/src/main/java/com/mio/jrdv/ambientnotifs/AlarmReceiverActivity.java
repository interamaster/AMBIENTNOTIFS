package com.mio.jrdv.ambientnotifs;

import android.app.Activity;
import android.app.Notification;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * This activity will be called when the alarm is triggered.
 * 
 * @author Michael Irwin
 */
public class AlarmReceiverActivity extends Activity {

    private PowerManager.WakeLock wl;//encender screen
    private DevicePolicyManager mDPM;//lock screen

    //PARA VERLO DE MOMENTO  AQUI


    //protected MyInfoNotifcReceiver mReceiver = new MyInfoNotifcReceiver();// //NUCA LLEGABAN DATOS
    //public static String INTENT_ACTION_NOTIFICATION = "com.mio.jrdv.ambientnotifs";//NUCA LLEGABAN DATOS
    protected TextView title;
    protected TextView text;
    protected TextView subtext;
    protected ImageView largeIcon;
    protected ImageView FotoWhastapp;
    protected ImageView IconoApp;


    //icono app
    String packageNameWhataspp;


    //PARA EL LOGGING

    private String TAG = this.getClass().getSimpleName();


    //para el autolock timepo

    long tiempoAutoLock=10000;//5 secs


    //para el broadcast:

    LocalBroadcastManager mLocalBroadcastManager;



    //para guardar el ultimo text recibido del whataspp

    private  String lastWhaatspptext; //paso actualizo de moento

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i(TAG,"RECIBIDO BROADCAST PARA CERRAR ACTIVITY NOTIF ES PARA CERRAR?�?");
            if(intent.getAction().equals("com.mio.jrdv.action.close")){

                Log.i(TAG,"  BROADCAST PARA CERRAR ACTIVITY NOTIF...!!!");
                //finish();//ahora NO CIERRO actualizo INFO




                //TODO aqui deberiamos chequear si es lo mismo que ya teniamos ..o no

                //1º)sacamos el texto solo del intent


                Bundle extrasfromService = getIntent().getExtras();


                Log.i(TAG,"ESTO LLEGO"+extrasfromService.toString());


                if(extrasfromService !=null) {


                    CharSequence notificationText = extrasfromService.getCharSequence(Notification.EXTRA_TEXT);

                    /*

                    // de meonteo actuzalio

                    //2º)lo compàramos con el que teniamos

                    if (notificationText.toString().equals(lastWhaatspptext)) {

                        //3º)si es igual ignoramos

                        Log.i(TAG," ES EL MISMO WHATSPP TEXT..IGNORAMOS!!!");

                    } else {

                    */

                        // TODO 4º)si es distinto ACTUALIZAMOS



                        //rellenamos como en Main

                        String notificationTitle = extrasfromService.getString(Notification.EXTRA_TITLE);
                        int notificationIcon = extrasfromService.getInt(Notification.EXTRA_SMALL_ICON);
                        Bitmap notificationLargeIcon = ((Bitmap) extrasfromService.getParcelable(Notification.EXTRA_LARGE_ICON));

                        CharSequence notificationSubText = extrasfromService.getCharSequence(Notification.EXTRA_SUB_TEXT);

                        title.setText(notificationTitle);
                        text.setText(notificationText);
                        //guardamos el ultimo text

                        lastWhaatspptext= (String) notificationText;

                        subtext.setText(notificationSubText);

                        if (notificationLargeIcon != null) {
                            largeIcon.setImageBitmap(notificationLargeIcon);

                        }



                    Intent FotoFromService = getIntent();


                        packageNameWhataspp  = FotoFromService.getExtras().getString("packageName");


                        Log.i(TAG, "el package name es:" + packageNameWhataspp);

                        //el icono de wahastpp:
                        Drawable appIcon = null;
                        try {
                            appIcon = getPackageManager().getApplicationIcon(packageNameWhataspp);

                            IconoApp.setImageDrawable(appIcon);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }





                        lastWhaatspptext=notificationText.toString();


                    //actuzalizamos viewgruop




                }

                }


            }

    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        Log.i("INICIO :", "AlarmReceiverActivity:");



        //a los 5 segundos se autocerrara si no hemos pulasdo antes

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AutoLock();
            }
        }, tiempoAutoLock);



        //lo primero es crear un broadcast receiver para que desde el service no sobligue a cerralas si hay una nueva NOTIF:
        //https://stackoverflow.com/questions/25841544/how-to-finish-activity-from-service-class-in-android

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.mio.jrdv.action.close");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);








        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
		wl.acquire();
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | 
        	    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
        	    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
        	    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
        	    WindowManager.LayoutParams.FLAG_FULLSCREEN | 
        	    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
        	    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
        	    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.alarm);



        //Retrieve ui elements
        title = (TextView) findViewById(R.id.nt_title);
        text = (TextView) findViewById(R.id.nt_text);
        subtext = (TextView) findViewById(R.id.nt_subtext);
        largeIcon = (ImageView) findViewById(R.id.nt_largeicon);
        FotoWhastapp = (ImageView) findViewById(R.id.fotowhastapp);
        IconoApp = (ImageView) findViewById(R.id.iconoApp);





        Button stopAlarm = (Button) findViewById(R.id.stopAlarm);
        stopAlarm.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {

                finish();
                return false;
            }
        });



        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);


        //recibimos data extra del intent el notification.extras!!(desde el Service)

        //FORMA 1 PASADO EN EL INTENT EXTRAS

        Bundle extrasfromService = getIntent().getExtras();
        if(extrasfromService !=null){


            Log.i(TAG, "SBN notification  :" + extrasfromService.toString() );





            //rellenamos como en Main

            String notificationTitle = extrasfromService.getString(Notification.EXTRA_TITLE);
            int notificationIcon = extrasfromService.getInt(Notification.EXTRA_SMALL_ICON);
            Bitmap notificationLargeIcon = ((Bitmap) extrasfromService.getParcelable(Notification.EXTRA_LARGE_ICON));


            /*
            //foto ..no simepore tiene  la recueprpo del intent si existe mejor ver FORMA 3

            if (extrasfromService.containsKey(Notification.EXTRA_PICTURE)) {


                Bitmap fotodelaNotif = (Bitmap) extrasfromService.get(Notification.EXTRA_PICTURE);

                //si queremos comprimir:
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                fotodelaNotif.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                //Bitmap fotodelaNotif = ((Bitmap) extrasfromService.getParcelable(Notification.EXTRA_PICTURE));
                FotoWhastapp.setImageBitmap(fotodelaNotif);

                Log.i(TAG, "SI FOTO::" + fotodelaNotif);

            }

            */

            CharSequence notificationText = extrasfromService.getCharSequence(Notification.EXTRA_TEXT);
            CharSequence notificationSubText = extrasfromService.getCharSequence(Notification.EXTRA_SUB_TEXT);

            title.setText(notificationTitle);
            text.setText(notificationText);
            //guardamos el ultimo text

            lastWhaatspptext= (String) notificationText;

            subtext.setText(notificationSubText);

            if (notificationLargeIcon != null) {
                largeIcon.setImageBitmap(notificationLargeIcon);

            }


            /*
            if (fotodelaNotif != null) {

                Log.i(TAG, "SI FOTO::" + fotodelaNotif);
                FotoWhastapp.setImageBitmap(fotodelaNotif);

            }*/


        }


        //FORMA 2 PASADO EN EL INTENT PARCELABLE CON   EL SBN COMPLETO...no da error de too large size..asi que mejor recuperamos solo la foto si la tiene

/*
        Intent FULLNOTIFICfromService = getIntent();
        StatusBarNotification myNotificationFULL = (StatusBarNotification)FULLNOTIFICfromService.getParcelableExtra("sbn");
        if(myNotificationFULL != null) {
            //StatusBarNotification successfully retrieved

            Log.i(TAG, "SI SBN FULL::" + myNotificationFULL.getNotification().extras);

        }

        */

        //forma 3 si tiene una foto el whataspp lo recuperamos:

        //foto ..no simepore tiene

        Intent FotoFromService = getIntent();


        if(FotoFromService.hasExtra("foto")) {

            Bitmap fotodelaNotif = BitmapFactory.decodeByteArray( FotoFromService.getByteArrayExtra("byteArray"),0,FotoFromService.getByteArrayExtra("foto").length);
            FotoWhastapp.setImageBitmap(fotodelaNotif);

            Log.i(TAG, "SI FOTO RECIBIDO EN ACTIVITY" + fotodelaNotif);
        }



        packageNameWhataspp  = FotoFromService.getExtras().getString("packageName");


        Log.i(TAG, "el package name es:" + packageNameWhataspp);

        //el icono de wahastpp:
        Drawable appIcon = null;
        try {
            appIcon = getPackageManager().getApplicationIcon(packageNameWhataspp);

            IconoApp.setImageDrawable(appIcon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //la ponemos en balnco y negro:

        // Create a paint object with 0 saturation (black and white)
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        Paint greyscalePaint = new Paint();
        greyscalePaint.setColorFilter(new ColorMatrixColorFilter(cm));
// Create a hardware layer with the greyscale paint

        RelativeLayout RV=(RelativeLayout) findViewById(R.id.relative1);
        RV.setLayerType(View.LAYER_TYPE_HARDWARE,greyscalePaint);

      //  v.setLayerType(LAYER_TYPE_HARDWARE, greyscalePaint);


    }

    private void AutoLock() {

        if (wl.isHeld())
            wl.release();


        this.finish();
        mDPM.lockNow();
    }

    @Override
    protected void onStop() {
    	super.onStop();
        if (wl.isHeld())



             /*
         * Step 4: Ensure to unregister the receiver when the activity is destroyed so that
         * you don't face any memory leak issues in the app
         */
            if(mLocalBroadcastManager != null) {
                mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
            }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("INFO", "pulsado para dismiss");



        this.finish();
        mDPM.lockNow();

        return false;
    }




/*


//NUCA LLEGABAN DATOS..PASO
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////PARA PASARLO A ALGUN SITIO DE MOMENTO A  MAIN//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public class MyInfoNotifcReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("NEW", "recibido en AlarmReceiverActivity!!!");

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
        if (mReceiver == null) mReceiver = new MyInfoNotifcReceiver();
        registerReceiver(mReceiver, new IntentFilter(INTENT_ACTION_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

*/


//para cerrar con un broadcast:https://stackoverflow.com/questions/25841544/how-to-finish-activity-from-service-class-in-android

    protected void onDestroy() {
        super.onDestroy();

        if (wl.isHeld())
            wl.release();



        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }
}