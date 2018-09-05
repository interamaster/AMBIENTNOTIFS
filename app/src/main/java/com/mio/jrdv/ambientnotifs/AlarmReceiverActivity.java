package com.mio.jrdv.ambientnotifs;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mio.jrdv.ambientnotifs.textclock.TextClock;

/**
 * This activity will be called when the alarm is triggered.
 * 
 * @author Michael Irwin
 */
public class AlarmReceiverActivity extends Activity {

    private PowerManager.WakeLock wl;//encender screen
    //mejor no o no funciona luego lahuella
   // private DevicePolicyManager mDPM;//lock screen

    //PARA VERLO DE MOMENTO  AQUI


    //protected MyInfoNotifcReceiver mReceiver = new MyInfoNotifcReceiver();// //NUCA LLEGABAN DATOS
    //public static String INTENT_ACTION_NOTIFICATION = "com.mio.jrdv.ambientnotifs";//NUCA LLEGABAN DATOS
    protected TextView title;
    protected TextView text;
    ///protected TextView subtext;
    protected ImageView largeIcon;
    protected ImageView FotoWhastapp;
    protected ImageView IconoApp;
    protected ImageView Fondodifuminar;


    //icono app
    String packageNameWhataspp;


    //PARA EL LOGGING

    private String TAG = this.getClass().getSimpleName();


    //para el autolock timepo

    long tiempoAutoLock=5000;//5 secs

    long tiempoAutoFinish=12000;//5 secs


    //para el broadcast:

    LocalBroadcastManager mLocalBroadcastManager;



    //para guardar el ultimo text recibido del whataspp

    private  String lastWhaatspptext; //paso actualizo de moento



    //para el reloj color


    private TextClock reloj;
    private String colorPonerReloj;



    // variable to track event time
    private long mLastClickTime = 0;
    private boolean issingleclicked;

    //para los anuncios

    private AdView mAdView;





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
                        //pero no puede medir mas e un maximo:

                    // This get's the width of your display.
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = displaymetrics.widthPixels;


                        title.setMaxWidth(width-200);//que no oucope mas de 200


                        text.setText(notificationText);
                        //guardamos el ultimo text

                        lastWhaatspptext= (String) notificationText;

                        //subtext.setText(notificationSubText);

                        if (notificationLargeIcon != null) {
                            largeIcon.setImageBitmap(notificationLargeIcon);

                        }



                    Intent FotoFromService = getIntent();


                        packageNameWhataspp  = FotoFromService.getExtras().getString("packageName");


                        Log.i(TAG, "el package name es:" + packageNameWhataspp);



                        colorPonerReloj=FotoFromService.getExtras().getString("colornotif");
                         Log.i(TAG, "el color es:" + colorPonerReloj);

                         //y le ponemos el color al reloj:

                         // TODO poner ok
                            reloj.setColor(Color.parseColor(colorPonerReloj));//color wahatspp 0xff075e54

                         // reloj.setColor(0xff075e54);


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



        //a los 15 segundos se autoeliminara

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AutoFinish();
            }
        }, tiempoAutoFinish);


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
        	    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON  ,
        	    WindowManager.LayoutParams.FLAG_FULLSCREEN | 
        	    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
        	    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
        	    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON) ;



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);




        //setContentView(R.layout.alarm);

        //otro modelo

        setContentView(R.layout.alarm2);


        //anuncios//////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////

        //ads initialize:


      //  if (!(lasttimepulse7timesadsfree>100) || (System.currentTimeMillis()-(lasttimepulse7timesadsfree+7*24*60*60*1000))>0  )
            //para probar 60 segs


            // Initialize the Mobile Ads SDK.
            MobileAds.initialize(this, "ca-app-pub-6700746515260621~7458435159");

            mAdView = findViewById(R.id.adView);
           // AdRequest adRequest = new AdRequest.Builder().build();
          //  mAdView.loadAd(adRequest);

        //TODO quitar cunado se acaben para test:

                AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
               // .addTestDevice("29C041BA6E3ACCE9AB0FCBF2740B1E3D")//note 4
                .addTestDevice("60246B11E032931F244C3FD34084E0D2") //note 8
                .build();

            mAdView.loadAd(adRequest);



            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    Log.i("TAG", "Code to be executed when an ad finishes loading.");
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    Log.i("TAG", "Code to be executed when an ad request fails.");
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.

                    Log.i("TAG", "Code to be executed when the user has left the app.");
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the user is about to return
                    // to the app after tapping on an ad.

                    Log.i("TAG", "// Code to be executed when when the user is about to return\n" +
                            "                // to the app after tapping on an ad.");

                }
            });




        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////




        //Retrieve ui elements
        title = (TextView) findViewById(R.id.nt_title);
        text = (TextView) findViewById(R.id.nt_text);
       // subtext = (TextView) findViewById(R.id.nt_subtext);
        largeIcon = (ImageView) findViewById(R.id.nt_largeicon);
        FotoWhastapp = (ImageView) findViewById(R.id.fotowhastapp);
        IconoApp = (ImageView) findViewById(R.id.iconoApp);
        reloj =(TextClock)findViewById(R.id.reloj);
        Fondodifuminar=(ImageView)findViewById(R.id.fondodifuminar);



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



                        if (numberOfTaps == 2  ) {

                            //TODO FINALIZAMOS ..DEBERIA SER LANZAR NOTIF EN APP

                            finish();

                        }


                        if (numberOfTaps ==1 && !issingleclicked){

                            Log.i("INFO", "pulsado para dismiss en DIFUMINAR VIEW");
                            issingleclicked=true;
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

        //aqui le damos el color de la notificacion... no tras recibir intent..mas abajo
       // reloj.setColor(Color.RED);






        //mejor no o no funciona luego lahuella
      //  mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);


        //recibimos data extra del intent el notification.extras!!(desde el Service)

        //FORMA 1 PASADO EN EL INTENT EXTRAS

        Bundle extrasfromService = getIntent().getExtras();
        if(extrasfromService !=null){


            Log.i(TAG, "SBN notification  :" + extrasfromService.toString() );




            Log.i(TAG, "SBN notification  :" +    extrasfromService.getBundle("bundle"));

            //rellenamos como en Main

            /*
            //PASANDO UN BUNDLE ON EXTRAS NO SE SACARLOS
            String titulodesdebunlde=extrasfromService.getBundle("bundle").getString(Notification.EXTRA_TITLE);
            int notificationIcondesdebundle = extrasfromService.getBundle("bundle").getInt(Notification.EXTRA_SMALL_ICON);
            Bitmap LargeIcondesdebundle = ((Bitmap) extrasfromService.getBundle("bundle").getParcelable(Notification.EXTRA_LARGE_ICON));
           */

            //METODO ORIGINAL PASANDO EL EXTRA COMPLETO:
            /*
            String notificationTitle = extrasfromService.getString(Notification.EXTRA_TITLE);
            int notificationIcon = extrasfromService.getInt(Notification.EXTRA_SMALL_ICON);
            Bitmap notificationLargeIcon = ((Bitmap) extrasfromService.getParcelable(Notification.EXTRA_LARGE_ICON));
            */

            //METODO SACANDO EL EXTRA DE CADA COSA

            String notificationTitle = extrasfromService.getString("EXTRA_TITLE");

            Bitmap notificationLargeIcon = ((Bitmap) extrasfromService.getParcelable("fotoCONTACTO"));

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

            //CharSequence notificationText = extrasfromService.getCharSequence(Notification.EXTRA_TEXT);

            CharSequence notificationText = extrasfromService.getCharSequence("EXTRA_TEXT");
           // CharSequence notificationSubText = extrasfromService.getCharSequence(Notification.EXTRA_SUB_TEXT);//no manda anda en whastapp akl menos


            CharSequence notificationBIGText = extrasfromService.getCharSequence("EXTRA_BIG_TEXT");

            if (notificationTitle!= null){
                title.setText(notificationTitle);
            }

            if (notificationText !=null ){

                text.setText(notificationText);
            }

            //si es un outlook recortamos el bigtext del text para tener el contenido del email!!:
            //en whatspp TET y BIGTEXT son lo mismo!!!!

            if (notificationBIGText !=null && !notificationBIGText.equals(notificationText)){

                //primeor contamos las letras el encbaezado(title)
                int numerocartacterestitulo=notificationText.length();

                //segundo los restamos empezadon por la izqda

                String cuerpoFinal=notificationBIGText.toString().substring(numerocartacterestitulo);

                //tercero actualizamos le texto del tittulo y el texto

                text.setText(cuerpoFinal);
                title.setText(notificationText);

            }

            //guardamos el ultimo text

            lastWhaatspptext= ""+text.getText();

            //subtext.setText(notificationSubText);//no mand anda en whastapp akl menos


            if (notificationLargeIcon != null) {
                largeIcon.setImageBitmap(notificationLargeIcon);

            }




            colorPonerReloj=extrasfromService.getString("colornotif");
            Log.i(TAG, "el color es:" + colorPonerReloj);

            //y le ponemos el color al reloj:

            // TODO poner ok
            reloj.setColor(Color.parseColor(colorPonerReloj));//color wahatspp 0xff075e54

            title.setTextColor(Color.parseColor(colorPonerReloj));

            //si e sgmail se ve muy mal lopongo luego a rojo al ver que apk es




            // reloj.setColor(0xff075e54);



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

        //si e sgmail se ve muy mal el colo del texto si es gamil lo pongo rojo;


        if (packageNameWhataspp.equals("com.google.android.gm")){

            title.setTextColor(Color.RED);
            text.setTextColor(Color.LTGRAY);
        }


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
        //no!!!
        /*

        // Create a paint object with 0 saturation (black and white)
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        Paint greyscalePaint = new Paint();
        greyscalePaint.setColorFilter(new ColorMatrixColorFilter(cm));
// Create a hardware layer with the greyscale paint

        LinearLayout RV=(LinearLayout) findViewById(R.id.relative1);
        RV.setLayerType(View.LAYER_TYPE_HARDWARE,greyscalePaint);

      //  v.setLayerType(LAYER_TYPE_HARDWARE, greyscalePaint);

*/


    }

    private void AutoFinish() {

        if (wl.isHeld())
            wl.release();



        if (mAdView != null) {
            mAdView.destroy();}

        this.finish();
    }

    private void AutoLock() {

        if (wl.isHeld())
            wl.release();


        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      //  this.finish();
        //mDPM.lockNow();//mejor no o no funciona luego lahuella
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

/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("INFO", "pulsado para dismiss");

        //mejor lo hago en el click de la view Difuminar



        //lo hacemos animado

        TranslateAnimation animate = new TranslateAnimation(0,0,0,Fondodifuminar.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        Fondodifuminar.startAnimation(animate);
        Fondodifuminar.setVisibility(View.VISIBLE);

       // this.finish();
       // mDPM.lockNow();//mejor no o no funciona luego lahuella

        return false;
    }

*/

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////PARA que esconda el navigation bar//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //https://stackoverflow.com/questions/21724420/how-to-hide-navigation-bar-permanently-in-android-activity


    @Override
    protected void onResume() {

        Log.i(TAG,"onresume");
        super.onResume();
        FullScreencall();

        if (mAdView != null) {
            mAdView.resume();
        }



    }

    public void FullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }


      //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    /*
    public void pulsadodifuminar(View view) {





        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1600) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Log.i("INFO", "pulsado para dismiss en DIFUMINAR VIEW");
        //desahbilitamos el poder volver a pulsar

        Fondodifuminar.setClickable(false);
        Fondodifuminar.setEnabled(false);


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

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 6000);
                    }
                })
                .start();

    }
*/

}