package com.mio.jrdv.ambientnotifs;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.view.Display;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by joseramondelgado on 19/08/18.
 */

public class NOTIFSService extends NotificationListenerService{


        //para saber si ya esta el sevivio running:
        private boolean ServiceYaRunning;


    //PARA EL LOGGING

    private String TAG = this.getClass().getSimpleName();

    //PARA EL COINTEXT EN SCREEN ON


    private Context mContext;


    //para el icono

    private String pacakgenamenotif;
    //para el color reloj igual que del icono:

    private String colorNotif;


    //para el quiet time

    private SharedPreferences mPrefs;//las uso con Myapplication


    //para evitar duplicados

    long LastWhastsppsbnTime;
    String lastmesagetext="ultimo";
    String Actualmesagetext="actual";


    public void onCreate() {
        super.onCreate();


        //mio:
        ServiceYaRunning=false;

     //   Log.i("INICIO SERVIVIO NOTIFS:", "OK");


        mContext = this;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

      //  Log.i("FINAL SERVIVIO NOTIFS:", "OK");



    }




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////METODO QUE SE EJECUTA CADA VEZ QUE SE RECIBE NOTIFICATION////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

/*

        //forma de ver los key del sbn(notific)

            Log.i(TAG, "ID:" + sbn.getId());
            Log.i(TAG, "Posted by:" + sbn.getPackageName());
            Log.i(TAG, "tickerText:" + sbn.getNotification().tickerText);

            for (String key : sbn.getNotification().extras.keySet()) {
                Log.i(TAG, key + "=" + sbn.getNotification().extras.get(key));
            }


*/



            if (sbn.isOngoing()) {
                return;
            }




            mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            pacakgenamenotif = sbn.getPackageName();


            if (!isNotif4packnamehabilitada(pacakgenamenotif)) {



                return;
            }



            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////para detectar sbn duplicado en whastapp///////////////////////////// ///// ////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


         //   Log.d(TAG, "Notification has arrived");
          //  Log.d(TAG, "KEY: " + sbn.getKey() +" ID: " + sbn.getId() + " Posted by: " + sbn.getPackageName() + " at: " + sbn.getPostTime() + " ");

            //guaradmos tiempo..luego se actualiza al recibir la notif si la pnatalla esta apagada
            LastWhastsppsbnTime=sbn.getPostTime();

            //1º) KEY: 0|com.whatsapp|1|34639689367@s.whatsapp.net|10185 ID: 1 Posted by: com.whatsapp at: 1534961145878

            //2º KEY: 0|com.whatsapp|1|null|10185 ID: 1 Posted by: com.whatsapp at: 1534961145913


            //si lleva foto son 4!!!

            /*

KEY: 0|com.whatsapp|1|34639689367@s.whatsapp.net|10185 ID: 1 Posted by: com.whatsapp at: 1535476999019
 KEY: 0|com.whatsapp|1|null|10185 ID: 1 Posted by: com.whatsapp at: 1535476999085
 KEY: 0|com.whatsapp|1|34639689367@s.whatsapp.net|10185 ID: 1 Posted by: com.whatsapp at: 1535477000861
 KEY: 0|com.whatsapp|1|null|10185 ID: 1 Posted by: com.whatsapp at: 1535477000896
             */




            String[] separated = sbn.getKey().split("\\|");
            if (separated[3].equalsIgnoreCase("null")&&sbn.getPackageName().equals("com.whatsapp")){

             //   Log.d(TAG, "Notification has arrived but the terrcer campo es null!!!..duplicada");
                return;

            }


            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////para detectar sbn duplicado en telegram///////////////////////////// ///// ////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*
            TAMBIEN MANDA 2 NOTIFS...

            KEY: 0|org.telegram.messenger|1|null|10200 ID: 1
            Y

            KEY: 0|org.telegram.messenger|10503770|null|10200 ID: 10503770

            osea el ID=1 lo descartamos y nos quedamos con el segundo!!!
  */





            if (mPrefs.getBoolean("Telegram",false) && (pacakgenamenotif.equals("org.telegram.messenger") && sbn.getId()==1 || pacakgenamenotif.equals("org.thunderdog.challegram") && sbn.getId()==1 ) ) {

              //  Log.i("INFO", "era un telegram con  ID=1 lo descartamos!!!");

                return;

            }



            //Y EL TELEGRAM X IGUAL

            //KEY: 0|org.thunderdog.challegram|5|null|10202 ID: 5 Posted by: org.thunderdog.challegram at: 1535570658980
            //KEY: 0|org.thunderdog.challegram|1|null|10202 ID: 1 Posted by: org.thunderdog.challegram at: 1535570659183

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////para detectar sbn duplicado en GMAIL///////////////////////////// ///// ////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




           // KEY: 0|com.google.android.gm|0|gig:2060997336:^sq_ig_i_personal|10139 ID: 0 Posted by: com.google.android.gm at: 1536077157871
            //KEY: 0|com.google.android.gm|451854755|gig:2060997336:^sq_ig_i_personal|10139 ID: 451854755 Posted by: com.google.android.gm at: 1536077157909


            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////para detectar sbn duplicado en OUTLOOK///////////////////////////// ///// ////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




            //KEY: 0|com.microsoft.office.outlook|2|1:[1:AAMkAD... ID: 2 Posted by: com.microsoft.office.outlook at: 1536074763260
            //KEY: 0|com.microsoft.office.outlook|1|notifGroup:1|10230 ID: 1 Posted by: com.microsoft.office.outlook at: 1536074763392


            //osea debo ignorar el ID1

            if (mPrefs.getBoolean("Outlook",false) && (pacakgenamenotif.equals("com.microsoft.office.outlook") && sbn.getId()==1 )) {

             //   Log.i("INFO", "era un OUTLOOK con  ID=1 lo descartamos!!!");

                return;

            }



            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////ES QUIET TIME?¿?//// ///// ////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////







           if (isInQuietTime()){
               return;
           }




            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////QUE APLICACIOENS ESTOY VIGILANDO//// ///// ////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        //   Log.i("VIGILANDO WHATASPP:", String.valueOf(mPrefs.getBoolean("Whastapp",false)));
        //    Log.i("VIGILANDO GMAIL:", String.valueOf(mPrefs.getBoolean("Gmail",false)));
         //   Log.i("VIGILANDO OUTLOOK:", String.valueOf(mPrefs.getBoolean("Outlook",false)));
        //    Log.i("VIGILANDO TELEGRAM:", String.valueOf(mPrefs.getBoolean("Telegram",false)));


            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////MANDAMOS UN BROADCAST PARA CERRAR SI YA EXISTIA UNA ACTIVITY//// ///// ////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


           // if (AlarmReceiverActivity.active) {

            if (isActivityRunning(AlarmReceiverActivity.class)) {

                //esto es lento y llega despues de abrir la activity!!!
                /*

                Log.i(TAG,"MANDO BROADCAST PARA CERRAR ACTIVITY NOTIF");

                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                        .getInstance(NOTIFSService.this);
                localBroadcastManager.sendBroadcast(new Intent(
                        "com.mio.jrdv.action.close"));

                        */

                /*
                //PASO DE BROADCAST CRE UNA NUEVA ACTIVITY Y PUNTO CERANDO LA OPTRA DEL STACK:FLAG_ACTIVITY_CLEAR_TASK

                Log.i(TAG,"DEBERIA  CERRAR ACTIVITY NOTIF...VOY AMANDAR LA SBN.extras POR BROADCAST");

                Intent intentbrodcast = new Intent("com.mio.jrdv.action.close");
               // Bundle extras = new Bundle();
               // extras=sbn.getNotification().extras;
               // intentbrodcast.putExtras(extras);
                intentbrodcast.putExtras(sbn.getNotification().extras);

                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(NOTIFSService.this);
                localBroadcastManager.sendBroadcast(intentbrodcast);
                //mContext.sendBroadcast(intentbrodcast);
                */

           }

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////PARA PASARLO A ALARMRECEIVERACTIVITY//////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*
                                /com.mio.jrdv.ambientnotifs I/NEW: enviando a MAIN!!!
08-19 18:02:56.584 29190-29190/com.mio.jrdv.ambientnotifs I/NEW: NO ERA NULL--VA  A MAIN!!!
08-19 18:02:56.587 29190-29190/com.mio.jrdv.ambientnotifs E/JavaBinder: !!! FAILED BINDER TRANSACTION !!!  (parcel size = 1641904)
08-19 18:02:56.587 29190-29190/com.mio.jrdv.ambientnotifs D/AndroidRuntime: Shutting down VM
08-19 18:02:56.589 29190-29190/com.mio.jrdv.ambientnotifs E/AndroidRuntime: FATAL EXCEPTION: main
                                                                            Process: com.mio.jrdv.ambientnotifs, PID: 29190
                                                                            java.lang.RuntimeException: android.os.TransactionTooLargeException: data parcel size 1641904 bytes
                                                                                at android.app.ContextImpl.sendBroadcast(ContextImpl.java:965)
 */


            //PRIMERO MIRAMOS SI LA PMNATLLA ESTA ENCENDIDA:


            if (!isScreenOn(mContext)) {
                // YOUR CODE
             //   Log.e("PANTALLA ENCENDIDA ", "NO!!!!");


                //SEGUNDO VEMOS IS ES UN WHATSAPP

               // if ((pacakgenamenotif.equals("com.whatsapp"))) {

                // o ahora si es una appl habilitada:


                if (isNotif4packnamehabilitada(pacakgenamenotif)) {


                  //  Log.e("ES UNA APP VIGILADA: ", "SI!!!!");

                    Notification mNotification = sbn.getNotification();



                    //ponemos el logging

                    LogInfodelSBN(sbn);


                    //guaradsmoa sui tiempo

                    LastWhastsppsbnTime=sbn.getPostTime();






                    Intent dialogIntent = new Intent(this, AlarmReceiverActivity.class);
                   // dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //MEJO ASI CIERRA LA OTRA SI EXISTIA
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    //PASANDO EL INTENT CON EXTRAS DEL TIRON FALLA EN GMAIL

                    //dialogIntent.putExtras(mNotification.extras);//esto no pasa el picture de la foto si la mandan ?¿?

                    //ASI QUE LO PONEMOS EN UN BUNDLE:NO FUNCIONA O NO SE SACARLO DE UN BUNDLE
                    /*
                    Bundle bundle =new Bundle();
                    bundle.putBundle("bundle",sbn.getNotification().extras);

                    dialogIntent.putExtra("bundle",bundle);
                    */

                    //ASI QUE PASAMOS LA FOTO Y EL TITULO Y EL TEXT

                    String notificationTitle = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE);

                    //y lo ponemos de extra en el intent:
                    dialogIntent.putExtra("EXTRA_TITLE",notificationTitle);


                    //EL BIG TEXT SOLO LO USA OUTLOOK:
                    CharSequence notificationBigTitle = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_BIG_TEXT);

                    //y lo ponemos de extra en el intent:
                    dialogIntent.putExtra("EXTRA_BIG_TEXT",notificationBigTitle);




                    CharSequence notificationText = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT);

                    //y lo ponemos de extra en el intent:
                    dialogIntent.putExtra("EXTRA_TEXT",notificationText);


                    if (sbn.getNotification().extras.containsKey(Notification.EXTRA_LARGE_ICON)) {


                        Bitmap FOTOCONTACTO = (Bitmap) mNotification.extras.get(Notification.EXTRA_LARGE_ICON);

                        if (FOTOCONTACTO!=null) {

                            //si queremos comprimir:

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            FOTOCONTACTO.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream);


                            //y lo ponemos de extra en el intent:
                            dialogIntent.putExtra("fotoCONTACTO", FOTOCONTACTO);

                        }


                    }





                    dialogIntent.putExtra("packageName", pacakgenamenotif);
                    dialogIntent.putExtra("colornotif",colorNotif);





                    //voy a pasar la notificaione entera :
                    //pero da un error si lleva foto de :Caused by: android.os.TransactionTooLargeException: data parcel size
                    //dialogIntent.putExtra("sbn",sbn);
                    //asi que mejor paso solo la foto comprimida si existe:
                    //NO SE PUEDE SOLO LA PASA AL EXPANDIR LA NOTIFICACION PANEL.....TODO


                    if (sbn.getNotification().extras.containsKey(Notification.EXTRA_PICTURE)) {


                        Bitmap fotodelaNotif = (Bitmap) mNotification.extras.get(Notification.EXTRA_PICTURE);

                        //si queremos comprimir:
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        fotodelaNotif.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream);



                       // Log.i(TAG, "SI FOTO DESDE SERVICE" + fotodelaNotif);

                        //y lo ponemos de extra en el intent:
                        dialogIntent.putExtra("foto",fotodelaNotif);


                    }
                    else {

                       // Log.i(TAG, "NO FOTO DESDE SERVICE !!!!!"  );
                    }


                    /*
                //ESTO NUCA LANZA EL RECEIVER Y ES NULL..NO LE LLEGA:
                    if (mNotification != null) {

                        Log.i("NEW", "NO ERA NULL--VA  A ALARMRECEIVERACTIVITY!!!");
                        Intent intent = new Intent(AlarmReceiverActivity.INTENT_ACTION_NOTIFICATION);
                        intent.putExtras(mNotification.extras);
                        sendBroadcast(intent);



                    }
*/

                    startActivity(dialogIntent);
                }
            }

            //else if (isScreenOn(mContext)&& (pacakgenamenotif.equals("com.whatsapp") && isActivityRunning(AlarmReceiverActivity.class))){


            //MEJOR CON TODAS LAS APK:





            else if (isScreenOn(mContext)&& (isNotif4packnamehabilitada(pacakgenamenotif) && isActivityRunning(AlarmReceiverActivity.class))&& checkDuplicateonTime(sbn.getPostTime(),sbn)){

              //  Log.i(TAG,"LA PANTALLA ESTA ENCENDIDA PERO ES UNA APK VIGILADA..DEBERIA HACER ALGO...");





                //Log.i(TAG," VOY AMANDAR LA SBN.extras POR BROADCAST");

                //YA NO AHOR ABRO ACTIVITY ENCIMA

              //  Log.i(TAG," VOY AMANDAR LA SBN.extras A UNA ACTIVITY QUE CIERRA LA ANTERIOR!!");
                /*

                Intent intentbrodcast = new Intent("com.mio.jrdv.action.close");
                // Bundle extras = new Bundle();
                // extras=sbn.getNotification().extras;
                // intentbrodcast.putExtras(extras);
                intentbrodcast.putExtras(sbn.getNotification().extras);

                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(NOTIFSService.this);
                localBroadcastManager.sendBroadcast(intentbrodcast);
                //mContext.sendBroadcast(intentbrodcast);

                */

                // en ve x de boradcast lanzo activity ?¿?...tampoco actualiza?¿
                //SI CON ESTE FLAG:  dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


                Intent dialogIntent = new Intent(this, AlarmReceiverActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                //PASANDO EL INTENT CON EXTRAS DEL TIRON FALLA EN GMAIL
                //dialogIntent.putExtras(sbn.getNotification().extras);//esto no pasa el picture de la foto si la mandan ?¿?


                //ASI QUE PASAMOS LA FOTO Y EL TITULO Y EL TEXT

                String notificationTitle = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE);

                //y lo ponemos de extra en el intent:
                dialogIntent.putExtra("EXTRA_TITLE",notificationTitle);

                CharSequence notificationText = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT);

                //y lo ponemos de extra en el intent:
                dialogIntent.putExtra("EXTRA_TEXT",notificationText);


                if (sbn.getNotification().extras.containsKey(Notification.EXTRA_LARGE_ICON)) {


                    Bitmap FOTOCONTACTO = (Bitmap) sbn.getNotification().extras.get(Notification.EXTRA_LARGE_ICON);


                    if (FOTOCONTACTO!=null) {
                        //si queremos comprimir:
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        FOTOCONTACTO.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream);


                        //y lo ponemos de extra en el intent:
                        dialogIntent.putExtra("fotoCONTACTO", FOTOCONTACTO);

                    }


                }



                dialogIntent.putExtra("packageName", pacakgenamenotif);
                dialogIntent.putExtra("colornotif",colorNotif);
                startActivity(dialogIntent);


            }
            }

    private boolean checkDuplicateonTime( long notifctime ,StatusBarNotification sbn ) {


        //Whaastapp si lleva foto son 4!!!

            /*

KEY: 0|com.whatsapp|1|34639689367@s.whatsapp.net|10185 ID: 1 Posted by: com.whatsapp at: 1535476999019
 KEY: 0|com.whatsapp|1|null|10185 ID: 1 Posted by: com.whatsapp at: 1535476999085
 KEY: 0|com.whatsapp|1|34639689367@s.whatsapp.net|10185 ID: 1 Posted by: com.whatsapp at: 1535477000861
 KEY: 0|com.whatsapp|1|null|10185 ID: 1 Posted by: com.whatsapp at: 1535477000896
             */

            //1º)miramos si es el mismo text de la anterior

        //buscamos el valor del text de esta ultima notif
        Bundle extras = sbn.getNotification().extras;


        //guaradmos el text de esta notif

        CharSequence bigText3 = (CharSequence) extras.getCharSequence("android.text");
        if (bigText3 != null) {
            //String TEXT = bigText3.toString();
            Actualmesagetext = bigText3.toString();
           // Log.i("TEXT", Actualmesagetext.toString());
        }




       // Log.i("TEXTOS COMAPRE","ACTUAL:"+Actualmesagetext+" Y ULTIMO:"+lastmesagetext);

            if (Actualmesagetext.equals(lastmesagetext)){


              //  Log.i("TEXTOS","IGUALES la ignoramos");
                //son iguales  return del tiron

                return false;
            }


      //  Log.i("SBN","textos distinto ..ESCUCHAMOS");
        return true;

            //2º)miramos cuanto timepo ha pasado desde el anterior guaradao

        //pasamos del timepo con el texto es sufcicnte
        /*


        Log.d( "TIMES:" , "ahora: "+System.currentTimeMillis()+" cuando se reicibio tercera notif:"+notifctime);
        //esto da: D/TIMES:: ahora: 1535479238047 cuando se reicibio tercera notif:1535479238006 ...41 milisecs!!



        if ((System.currentTimeMillis()-notifctime) <300){
            //mas de 300 milisecs..puede ser uno nuevo

            Log.i("SBN","menos de 300 milis la ignoramos");

            return false;

        }

        Log.i("SBN","MAS de 300 milis la ESCUCHAMOS");
            return true;

*/
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
      //  Log.i(TAG,"********** onNOtificationRemoved");
       // Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());

    }



    /**
     * Is the screen of the device on.
     * @param context the context
     * @return true when (at least one) screen is on
     */
    public boolean isScreenOn(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {


             //   Log.i("***ESTADO DEL SCREEN:", String.valueOf(display.getState()));

                /*
                OJO CON AOD EL ESTADO ES 3 O 4
                https://developer.android.com/reference/android/view/Display.html#STATE_OFF

STATE_DOZE
added in API level 21
public static final int STATE_DOZE
Display state: The display is dozing in a low power state; it is still on but is optimized for showing system-provided content while the device is non-interactive.

See also:

getState()
PowerManager.isInteractive()
Constant Value: 3 (0x00000003)

STATE_DOZE_SUSPEND
added in API level 21
public static final int STATE_DOZE_SUSPEND
Display state: The display is dozing in a suspended low power state; it is still on but the CPU is not updating it. This may be used in one of two ways: to show static system-provided content while the device is non-interactive, or to allow a "Sidekick" compute resource to update the display. For this reason, the CPU must not control the display in this mode.

See also:

getState()
PowerManager.isInteractive()
Constant Value: 4 (0x00000004

                 */
                if (display.getState() != Display.STATE_OFF && display.getState() != Display.STATE_DOZE  && display.getState() != Display.STATE_DOZE_SUSPEND) {
                    screenOn = true;
                }
            }


          //  Log.i("***DEVUELVO  SCREEN:", String.valueOf(screenOn));

            return screenOn;
        } else {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //noinspection deprecation
            return pm.isScreenOn();
        }
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////PARA SABER SI MI ACTIVITY ESTA ACTIVA//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    protected Boolean isActivityRunning(Class activityClass)


    {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////PARA SABER SI ES QUIET TIME//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    private boolean isInQuietTime() {
        boolean quietTime = false;

        if(mPrefs.getBoolean("quiet", false)) {
       // if(Myapplication.preferences.getBoolean(Myapplication.QuietTime, false)) {
            String startTime = mPrefs.getString("startTime", "22:00");
            String stopTime = mPrefs.getString("stopTime", "08:00");

          //  Log.i("starquiettime: ",startTime);
          //  Log.i("stopquiettime:  ",stopTime);

            SimpleDateFormat sdfDate = new SimpleDateFormat("H:mm");
            String currentTimeStamp = sdfDate.format(new Date());
            int currentHour = Integer.parseInt(currentTimeStamp.split("[:]+")[0]);
            int currentMinute = Integer.parseInt(currentTimeStamp.split("[:]+")[1]);

            int startHour = Integer.parseInt(startTime.split("[:]+")[0]);
            int startMinute = Integer.parseInt(startTime.split("[:]+")[1]);

            int stopHour = Integer.parseInt(stopTime.split("[:]+")[0]);
            int stopMinute = Integer.parseInt(stopTime.split("[:]+")[1]);

            if (startHour < stopHour && currentHour > startHour && currentHour < stopHour) {
                quietTime = true;
            } else if (startHour > stopHour && (currentHour > startHour || currentHour < stopHour)) {
                quietTime = true;
            } else if (currentHour == startHour && currentMinute >= startMinute) {
                quietTime = true;
            } else if (currentHour == stopHour && currentMinute < stopMinute) {
                quietTime = true;
            }
        }

      //  Log.d(TAG,"Device is in quiet time: " + quietTime);
        return quietTime;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////PARA SABER SI ES UNA NOTIFIC HABILOTADOA//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private  boolean isNotif4packnamehabilitada(String packname){


        if (mPrefs.getBoolean("Whastapp",false) && packname.equals("com.whatsapp")) {

          //  Log.i("INFO","era un wahstapp!");
            colorNotif="#25d366";

            return true;
        }

        else if (mPrefs.getBoolean("Gmail",false) && packname.equals("com.google.android.gm")) {

          //  Log.i("INFO","era un gmail!");
            colorNotif="#D44638";

            return true;
        }

        else if (mPrefs.getBoolean("Outlook",false) && packname.equals("com.microsoft.office.outlook")) {

          //  Log.i("INFO","era un outlook!");
            colorNotif="#0072C6";

            return true;
        }

        else if (mPrefs.getBoolean("Telegram",false) && (packname.equals("org.telegram.messenger") ||packname.equals("org.thunderdog.challegram"))  ) {

         //   Log.i("INFO","era un telegram!");
            colorNotif="#0072C6";

            return true;
        }

        else

        return false;
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////PARA PONER EN LOG.I TODA LA INFO QUE SE VA A PSASR//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void LogInfodelSBN(StatusBarNotification sbn ){
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////PARA ER EL LOGGING SOLO//////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        // Log.i(TAG,"**********  onNotificationPosted");
        // Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
/*
            Log.i(TAG, "SBN :" + sbn );

            Log.i(TAG, "ID:" + sbn.getId());
            Log.i(TAG, "Posted by:" + sbn.getPackageName());
            Log.i(TAG, "tickerText:" + sbn.getNotification().tickerText);

            for (String key : sbn.getNotification().extras.keySet()) {
                Log.i(TAG, key + "=" + sbn.getNotification().extras.get(key).toString());
            }

*/



        pacakgenamenotif = sbn.getPackageName();

        /*
        //PASO EL COLOR A MANO EN isNotif4packnamehabilitada

        int colordelanotif=sbn.getNotification().color;//esto da valores extraños ?¿?¿ ej:SBN notif color:-16746281

        colorNotif = String.format("#%06X", (0xFFFFFF & colordelanotif));
        Log.i(TAG, "color en hex:"+colorNotif);
        */

        String ticker = "";
      //  Log.i(TAG, "SBN notification extras :" + sbn.getNotification().extras);

        //esto da: para KIDSTIMER EN EL RELOJ!! CDA SEGUNDO
/*
            SBN :Bundle[{android.title=null,
             android.subText=null,
              android.showChronometer=false,
              android.icon=2131099752,
               android.text=REMAINIG TIME: 01:20:57,
                android.progress=0,
                android.progressMax=0,
                 android.appInfo=ApplicationInfo{c2e75d5 com.sfc.jrdv.kidstimer},
                  android.showWhen=true,
                   android.largeIcon=null,
                    android.infoText=null,
                    android.originatingUserId=0,
                     android.progressIndeterminate=false,
                      android.remoteInputHistory=null}]

   */

//ESTO DA EN UN WTASAPP
/*
            I/NEW: ----------
                    08-19 18:20:32.071 30873-30873/com.mio.jrdv.ambientnotifs I/NOTIFSService: SBN notification extras :
                    Bundle[{android.title=Gustavo Hijo: ​,
                    android.conversationTitle=Gustavo Hijo,
                    android.subText=null,
                     android.car.EXTENSIONS=Bundle[mParcelledData.dataSize=1076],
                      android.template=android.app.Notification$MessagingStyle,
                      android.showChronometer=false,
                      android.icon=2131231581,
                      android.text=😂😂,
                      android.progress=0,
                      android.progressMax=0,
                      android.selfDisplayName=Gustavo Hijo,
                       android.appInfo=ApplicationInfo{6b50372 com.whatsapp},
                       android.messages=[Landroid.os.Parcelable;@e4b63c3,
                        android.showWhen=true,
                         android.largeIcon=android.graphics.Bitmap@a511440,
                          android.infoText=null,
                          android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=764],
                           android.progressIndeterminate=false,
                           android.remoteInputHistory=null}]





                    */


     //   Log.i(TAG, "SBN notification  :" + sbn.getNotification());

            /*

             //esto da: para KIDSTIMER EN EL RELOJ!! CDA SEGUNDO
             SBN notification  :Notification(pri=0 contentView=null vibrate=null sound=null defaults=0x0 flags=0xa color=0x00000000 vis=PRIVATE)

             */

            /*
            ESTO PARA WHATASAPP


             08-19 18:20:32.074 30873-30873/com.mio.jrdv.ambientnotifs I/NOTIFSService: SBN notification
            :Notification(channel=group_chat_defaults_2
            pri=0
            contentView=null
            vibrate=null
            sound=null
            defaults=0x0
            flags=0x8
            color=0xff075e54
            groupKey=group_key_messages
            sortKey=3
            actions=1
            number=1
            vis=PRIVATE s
            emFlags=0x0
            semPriority=0
            semMissedCount=0)
             */


        // if (sbn.getNotification().tickerText != null) {
        //  ticker = sbn.getNotification().tickerText.toString();
        //   }
        Bundle extras = sbn.getNotification().extras;
//        try {
//            //sbn.getNotification().contentIntent.send();
//        } catch (PendingIntent.CanceledException e) {
//            e.printStackTrace();
//        }

      //  Log.i("NEW", "----------");

            /*
            if (extras.get("android.title")!=null) {
                //this is the title of the notification
                //algunas veces da error!!!

                //Key android.title expected String but value was a android.text.SpannableString.  The default value <null> was returned.

                String title = extras.getString("android.title");
                Log.i("Title", title);

            }
            */


      //  Log.i(TAG, "SBN APPNAME:" + pacakgenamenotif);//SBN APPNAME:com.whatsapp

      //  Log.i(TAG, "SBN notif color:" +  colorNotif);


        CharSequence bigText = (CharSequence) extras.getCharSequence("android.title");
        if (bigText != null) {
            String title = bigText.toString();
          //  Log.i("Title", title);
        }


        CharSequence bigText2 = (CharSequence) extras.getCharSequence("android.subtext");
        if (bigText2 != null) {
            String SUBTETXT = bigText2.toString();
          //  Log.i("SUBTETXT", SUBTETXT.toString());
        }


        CharSequence bigText3 = (CharSequence) extras.getCharSequence("android.text");
        if (bigText3 != null) {
            //String TEXT = bigText3.toString();
            lastmesagetext = bigText3.toString();
           // Log.i("TEXT", lastmesagetext.toString());
        }



        CharSequence bigText4 = (CharSequence) extras.getCharSequence("android.bigText");
        if (bigText4 != null) {
            String TEXT = bigText4.toString();
          //  Log.i("BIGTEXT", TEXT.toString());
        }


        //this is a bitmap to be used instead of the small icon when showing the  notification


        Drawable appIcon = null;
        try {
            appIcon = getPackageManager().getApplicationIcon(pacakgenamenotif);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        Bitmap largeIcon = null;
        try {
            largeIcon = (Bitmap) sbn.getNotification().extras.getParcelable(Notification.EXTRA_LARGE_ICON);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap smallIcon = null;
        try {
            int idsmallicon = sbn.getNotification().extras.getInt(Notification.EXTRA_SMALL_ICON);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int id1 = extras.getInt("android.icon");


     //   Log.i("FINAL ", "----------");


            /*
            ESTO DA PARA UN WHATSAPP
             08-19 18:20:32.074 30873-30873/com.mio.jrdv.ambientnotifs I/NEW: ----------
                    08-19 18:20:32.074 30873-30873/com.mio.jrdv.ambientnotifs I/Title: Gustavo Hijo: ​
            08-19 18:20:32.074 30873-30873/com.mio.jrdv.ambientnotifs I/TEXT: 😂😂
            08-19 18:20:32.132 30873-30873/com.mio.jrdv.ambientnotifs I/ApplicationPackageManager: load=com.whatsapp, bg=96-96, dr=144-144, forDefault=false, density=0
            08-19 18:20:32.141 30873-30873/com.mio.jrdv.ambientnotifs I/ApplicationPackageManager: scaled rate=0.59999996, size=144, alpha=2, hold=0
            08-19 18:20:32.142 30873-30873/com.mio.jrdv.ambientnotifs I/ApplicationPackageManager: load=com.whatsapp-theme2, bg=96-96, dr=144-144, tarScale=0.59999996, relScale=0.41142857, mask=false


             */


    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////LO QUE SE CAPTA AL EXPANDIR UN WHASTAPP CON FOTO//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /*
     SBN notification extras :Bundle[
     {android.title=Yo Philips,
      android.subText=null,
      android.car.EXTENSIONS=Bundle[mParcelledData.dataSize=1052],
       android.template=android.app.Notification$BigPictureStyle,
        android.showChronometer=false
        , android.icon=2131231583
        , android.text=📷 Foto,
         android.progress=0,
          android.progressMax=0,
          android.picture=android.graphics.Bitmap@fb2eb8f,
           android.showWhen=true,
            android.rebuild.applicationInfo=ApplicationInfo{28e0e1c com.whatsapp},
             android.people=[Ljava.lang.String;@470ba25, android.largeIcon=android.graphics.Bitmap@a54b7fa, android.infoText=null, android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=5904],
              android.originatingUserId=0, a
              ndroid.progressIndeterminate=false,
               android.summaryText=📷 Foto}]
     */

    /*

     SBN notification  :Notification
     (pri=0
     contentView=com.whatsapp/0x10900b4
     vibrate=null
      sound=null
      tick defaults=0x0
      flags=0x200
      color=0xff075e54
      category=msg
      groupKey=group_key_messages
       actions=2
       vis=PRIVATE


        publicVersion=Notification
        (pri=0
        contentView=com.whatsapp/0x10900b4
        vibrate=null sound=null defaults=0x0
        flags=0x0
         color=0xff075e54
          category=msg
          vis=PRIVATE
          secFlags=0x0
          secPriority=0)
           secFlags=0x0
           secPriority=0)
     */



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////LO QUE SE CAPTA AL ENVIAR UN WHASTAPP CON FOTO//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
    SBN notification extras :
    Bundle[{android.title=Yo Philips,
     android.subText=null,
     android.car.EXTENSIONS=Bundle[mParcelledData.dataSize=1052],

      android.showChronometer=false,
       android.icon=2131231583,
       android.text=📷 Foto,
        android.progress=0,
        android.progressMax=0,

          android.showWhen=true,
           android.rebuild.applicationInfo=ApplicationInfo{76e5758 com.whatsapp},
           android.people=[Ljava.lang.String;@ea830b1, android.largeIcon=android.graphics.Bitmap@658d996, android.infoText=null, android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=5904],
            android.originatingUserId=0,
             android.progressIndeterminate=false,
             android.summaryText=📷 Foto}]
*/

/*

    SBN notification  :Notification(
    pri=0 cOntentView=com.whatsapp/0x10900b4
     vibrate=null
      sound=null
      tick defaults=0x0
      flags=0x200
       color=0xff075e54
        category=msg
         groupKey=group_key_messages actions=2
         vis=PRIVATE publicVersion=Notification(
         pri=0
         contentView=com.whatsapp/0x10900b4
         vibrate=null
          sound=null
           defaults=0x0
           flags=0x0
           color=0xff075e54
           category=msg
           vis=PRIVATE
           secFlags=0x0
           secPriority=0)
            secFlags=0x0
            secPriority=0)
*/



/*

 */

}
