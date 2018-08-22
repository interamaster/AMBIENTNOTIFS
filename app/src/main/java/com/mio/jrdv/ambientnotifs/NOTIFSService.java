package com.mio.jrdv.ambientnotifs;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Display;

import java.io.ByteArrayOutputStream;
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

    private String packageNameWhataspp;



    public void onCreate() {
        super.onCreate();


        //mio:
        ServiceYaRunning=false;

        Log.i("INICIO SERVIVIO NOTIFS:", "OK");


        mContext = this;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("FINAL SERVIVIO NOTIFS:", "OK");



    }




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////METODO QUE SE EJECUTA CADA VEZ QUE SE RECIBE NOTIFICATION////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
    public void onNotificationPosted(StatusBarNotification sbn) {


            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////para detectar sbn duplicado///////////////////////////// ///// ////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            Log.d(TAG, "Notification has arrived");
            Log.d(TAG, "KEY: " + sbn.getKey() +" ID: " + sbn.getId() + " Posted by: " + sbn.getPackageName() + " at: " + sbn.getPostTime() + " ");


            //1º) KEY: 0|com.whatsapp|1|34639689367@s.whatsapp.net|10185 ID: 1 Posted by: com.whatsapp at: 1534961145878

            //2º KEY: 0|com.whatsapp|1|null|10185 ID: 1 Posted by: com.whatsapp at: 1534961145913


            String[] separated = sbn.getKey().split("\\|");
            if (separated[3].equalsIgnoreCase("null")){

                Log.d(TAG, "Notification has arrived but the terrcer campo es null!!!..duplicada");
                return;

            }






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


            //TODO si es una llmada del tiron pasamos:

            if (sbn.isOngoing()) {
                return;
            }


              packageNameWhataspp  = sbn.getPackageName();


            String ticker = "";
            Log.i(TAG, "SBN notification extras :" + sbn.getNotification().extras);

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


            Log.i(TAG, "SBN notification  :" + sbn.getNotification());

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

            Log.i("NEW", "----------");

            /*
            if (extras.get("android.title")!=null) {
                //this is the title of the notification
                //algunas veces da error!!!

                //Key android.title expected String but value was a android.text.SpannableString.  The default value <null> was returned.

                String title = extras.getString("android.title");
                Log.i("Title", title);

            }
            */


            Log.i(TAG, "SBN APPNAME:" +  packageNameWhataspp);//SBN APPNAME:com.whatsapp


            CharSequence bigText = (CharSequence) extras.getCharSequence("android.title");
            if (bigText != null) {
                String title = bigText.toString();
                Log.i("Title", title);
            }


            CharSequence bigText2 = (CharSequence) extras.getCharSequence("android.subtext");
            if (bigText2 != null) {
                String SUBTETXT = bigText2.toString();
                Log.i("SUBTETXT", SUBTETXT.toString());
            }


            CharSequence bigText3 = (CharSequence) extras.getCharSequence("android.text");
            if (bigText3 != null) {
                String TEXT = bigText3.toString();
                Log.i("TEXT", TEXT.toString());
            }


            //this is a bitmap to be used instead of the small icon when showing the  notification


            Drawable appIcon = null;
            try {
                appIcon = getPackageManager().getApplicationIcon(packageNameWhataspp);
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


            Log.i("FINAL ", "----------");


            /*
            ESTO DA PARA UN WHATSAPP
             08-19 18:20:32.074 30873-30873/com.mio.jrdv.ambientnotifs I/NEW: ----------
                    08-19 18:20:32.074 30873-30873/com.mio.jrdv.ambientnotifs I/Title: Gustavo Hijo: ​
            08-19 18:20:32.074 30873-30873/com.mio.jrdv.ambientnotifs I/TEXT: 😂😂
            08-19 18:20:32.132 30873-30873/com.mio.jrdv.ambientnotifs I/ApplicationPackageManager: load=com.whatsapp, bg=96-96, dr=144-144, forDefault=false, density=0
            08-19 18:20:32.141 30873-30873/com.mio.jrdv.ambientnotifs I/ApplicationPackageManager: scaled rate=0.59999996, size=144, alpha=2, hold=0
            08-19 18:20:32.142 30873-30873/com.mio.jrdv.ambientnotifs I/ApplicationPackageManager: load=com.whatsapp-theme2, bg=96-96, dr=144-144, tarScale=0.59999996, relScale=0.41142857, mask=false


             */


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
                Log.e("PANTALLA ENCENDIDA ", "NO!!!!");


                //SEGUNDO VEMOS IS ES UN WHATSAPP


                if ((packageNameWhataspp.equals("com.whatsapp"))) {

                    Log.e("ES UN WHAATSAPP", "SI!!!!");

                    Notification mNotification = sbn.getNotification();


                    //TODO luego vemos que hacemos..de momento encender pantalla:
                /*
                    Log.i("NEW", "enviando a MAIN!!!");
                    if (mNotification != null) {

                        Log.i("NEW", "NO ERA NULL--VA  A MAIN!!!");
                        Intent intent = new Intent(MainActivity.INTENT_ACTION_NOTIFICATION);
                        intent.putExtras(mNotification.extras);
                        sendBroadcast(intent);

                        Notification.Action[] mActions = mNotification.actions;
                        if (mActions != null) {
                            for (Notification.Action mAction : mActions) {
                                int icon = mAction.icon;
                                CharSequence actionTitle = mAction.title;
                                PendingIntent pendingIntent = mAction.actionIntent;
                            }
                        }
                    }

                }

                else {


                    Log.e("ES UN WHAATSAPP", "NO!!!!");
                }

*/
                    //TODO luego vemos que hacemos..de moento encender pantalla:



                     //   new ScreenController(this, false).handleNotification(packageNameWhataspp);




                        //TODO  o esto de alarma:

                    /*
                    //Create an offset from the current time in which the alarm will go off.
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, 2);

                    //Create a new PendingIntent and add it to the AlarmManager
                    Intent intent = new Intent(this, AlarmReceiverActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this,
                            12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                            pendingIntent);
                    */


                    Intent dialogIntent = new Intent(this, AlarmReceiverActivity.class);
                   // dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //MEJO ASI CIERRA LA OTRA SI EXISTIA
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogIntent.putExtras(mNotification.extras);//esto no pasa el picture de la foto si la mandan ?¿?
                    dialogIntent.putExtra("packageName",packageNameWhataspp);



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



                        Log.i(TAG, "SI FOTO DESDE SERVICE" + fotodelaNotif);

                        //y lo ponemos de extra en el intent:
                        dialogIntent.putExtra("foto",fotodelaNotif);


                    }
                    else {

                        Log.i(TAG, "NO FOTO DESDE SERVICE !!!!!"  );
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

            else if (isScreenOn(mContext)&& (packageNameWhataspp.equals("com.whatsapp") && isActivityRunning(AlarmReceiverActivity.class))){

                Log.i(TAG,"LA PANTALLA ESTA ENCENDIDA PERO ES UN WHATASPP..DEBERIA HACER ALGO...");



                Log.i(TAG," VOY AMANDAR LA SBN.extras POR BROADCAST");
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
                dialogIntent.putExtras(sbn.getNotification().extras);//esto no pasa el picture de la foto si la mandan ?¿?
                dialogIntent.putExtra("packageName",packageNameWhataspp);
                startActivity(dialogIntent);


            }
            }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"********** onNOtificationRemoved");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());

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


                Log.i("***ESTADO DEL SCREEN:", String.valueOf(display.getState()));

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


            Log.i("***DEVUELVO  SCREEN:", String.valueOf(screenOn));

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


}
