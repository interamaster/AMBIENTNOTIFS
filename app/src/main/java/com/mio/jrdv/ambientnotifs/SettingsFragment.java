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
import com.mio.jrdv.ambientnotifs.views.PiegraphPreference;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private SharedPreferences mPrefs;//las uso con Myapplication

    private boolean mServiceActive;
    private CheckBoxPreference mServicePreference;

//para als app


    private CheckBoxPreference mWhastappPreference;
    private CheckBoxPreference mGmailPreference;
    private CheckBoxPreference mOutlookPreference;
    private CheckBoxPreference mTelegramPreference;

    //para als estadisticas de uso

    int numWhastapp,numgamil,numoutlook,numtelegram;
    long FechaInicio;


    //PARA EL LOGGING

    private String TAG = this.getClass().getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());//las uso con Myapplication

       // rellenardatosimageniario();//para ver como va


       actulizarEstadisticas();


        findPreference("version").setSummary(BuildConfig.VERSION_NAME);


        initializeService();

        initializeTime();

        initializeApps();

        CheckApp();




    }

    private void rellenardatosimageniario() {



    mPrefs.edit().putInt("numWhastapp",456).commit();
        mPrefs.edit().putInt("numgamil",1237).commit();
        mPrefs.edit().putInt("numoutlook",45).commit();
        mPrefs.edit().putInt("numtelegram",0).commit();


    }


    private void actulizarEstadisticas() {

        //recupermaos numero de usos..por defecto es 0
        numWhastapp=mPrefs.getInt("numWhastapp",0);
        numgamil=mPrefs.getInt("numgamil",0);
        numoutlook=mPrefs.getInt("numoutlook",0);
        numtelegram=mPrefs.getInt("numtelegram",0);



        Log.d(TAG,"numwha: "+numWhastapp+ " numgmnail: "+numgamil+" numout: "+numoutlook+" numtel: "+numtelegram);



        //para la fecha inicio
        FechaInicio = mPrefs.getLong("FechaInicio",0);//por defecto vale 0)

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String fechainicio = formatter.format(FechaInicio);
        Log.d("INFO","se instalo el "+fechainicio);

        //dias que se instalo
        //http://stackoverflow.com/questions/23323792/android-days-between-two-dates

        long msDiff = System.currentTimeMillis() - FechaInicio;
        long diasDesdeInstalo = TimeUnit.MILLISECONDS.toDays(msDiff);


        //TODO quitar

       // diasDesdeInstalo=3;




        if (FechaInicio==0){

            //si es 0 qeu ponga aun no esta lista:
            fechainicio="TODAY!";
            diasDesdeInstalo=1;
            //guaradamo el dia de ahora mismo
            mPrefs.edit().putLong("FechaInicio",System.currentTimeMillis()).apply();
            diasDesdeInstalo=0;

        }

        if (diasDesdeInstalo<1){
            diasDesdeInstalo=1;
        }
        else  diasDesdeInstalo++;//le sumo +1 porque no cuenta le dia inicial!!!
        //aun asi no es perfecto porquer cuneta desde q se instalo los dias+


        Log.d(TAG,"hace "+diasDesdeInstalo+" DIAS");


        double whatasppaldianum=  numWhastapp/diasDesdeInstalo;
        String whatasppaldia="TOTAL: "+String.valueOf(numWhastapp)+" --->"+ String.format("%.1f",whatasppaldianum)+"/DAY";
        double Gmailsaldianum=   numgamil/diasDesdeInstalo;
        String Gmailsaldia="TOTAL: "+String.valueOf(numgamil)+" --->"+String.format("%.1f",Gmailsaldianum)+"/DAY";
        double Outlookaldianum=   numoutlook/diasDesdeInstalo;
        String Outlookaldia="TOTAL: "+String.valueOf(numoutlook)+" --->"+String.format("%.1f",Outlookaldianum)+"/DAY";
        double Telegramaldianum=   numtelegram/diasDesdeInstalo;
        String Telegramaldia="TOTAL: "+String.valueOf(numtelegram)+" --->"+String.format("%.1f",Telegramaldianum)+"/DAY";

        Log.d(TAG,"MEDIA DIARIA: numwha: "+numWhastapp+ " numgmnail: "+numgamil+" numout: "+numoutlook+" numtel: "+numtelegram);

        //lo actualizamos


        findPreference("fechainstall").setSummary(fechainicio);

        /*
        //NO LO PONGO EN GRAFICAS MAS BONITO
        findPreference("usoswhastaspp").setSummary(whatasppaldia);
        findPreference("usosgmail").setSummary (Gmailsaldia);
        findPreference("usosoutlook").setSummary(Outlookaldia);
        findPreference("usostelegram").setSummary(Telegramaldia);
        */

/*
    //TODO HORIONTAL PARA LAS MEDIAS

        //ponemos en unn grafico horchar

        HorizontalBarPreference horizontalchart = (HorizontalBarPreference) findPreference("piegraphtoday");

        //y desde aqui le paso los datos

        horizontalchart.SetDatas(numWhastapp,numgamil,numoutlook,numtelegram);
*/

        //ponemos en unn grafico horchar

        PiegraphPreference horizontalchart = (PiegraphPreference) findPreference("piegraphtoday");

        //y desde aqui le paso los datos

        horizontalchart.SetDatas( (int) whatasppaldianum, (int) Gmailsaldianum, (int) Outlookaldianum, (int) Telegramaldianum );



        //idem con valores totales/dia


        PiegraphPreference piecharttotal = (PiegraphPreference) findPreference("piegraphtotal");

        //y desde aqui le paso los datos

        piecharttotal.SetDatas((int) numWhastapp, (int) numgamil, (int) numoutlook, (int) numtelegram );

       // piechart.getView(null,null);


        /*

        //1º)obtenemos la preference


        //asi no lo pilla:
      Preference piechartpref = findPreference("piegraph");

         View v = piechartpref.getView(null, null);



        //a ver asi:

        //tampoco
       // LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     //  View v = inflater.inflate(R.layout.piegraph, null);


        //3ºpasandole la view en l emtod desde oncreateview..tampoco



        PieChart pieChart=(PieChart)v.findViewById(R.id.piechart_1);



        pieChart.setVisibility(View.INVISIBLE);


        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(true);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(34f,"Ilala"));
        yValues.add(new PieEntry(56f,"Temeke"));
        yValues.add(new PieEntry(66f,"Kinondoni"));
        yValues.add(new PieEntry(45f,"Kigamboni"));

        PieDataSet dataSet = new PieDataSet(yValues, "Desease Per   Regions");
                dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData((dataSet));
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.YELLOW);
        pieChart.setData(pieData);
        //PieChart Ends Here


*/
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


                  //  Log.i("Whastapp pref CIERTO:", String.valueOf(mPrefs.getBoolean("Whatsapp",true)));
                } else {

                    //lo guardamos a NO
                    mPrefs.edit().putBoolean("Whastapp",false).apply();
                  //  Log.i("Whastapp pref FALSO:", String.valueOf(mPrefs.getBoolean("Whastapp",true)));
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



        /*

        //ESTO DA EL SBN:

        SBN notification extras :Bundle[{
        android.title=jose ramon delgado,
         android.subText=interamaster@gmail.com,
          android.template=android.app.Notification$BigTextStyle,
          android.showChronometer=false,
           android.icon=2130837958,
           android.text=hola asunto,
            android.progress=0,
             android.progressMax=0,
             android.showWhen=true,
             android.rebuild.applicationInfo=ApplicationInfo{c7a4f5b com.google.android.gm},
              android.people=[Ljava.lang.String;@fa3b3f8,
              android.largeIcon=android.graphics.Bitmap@35960d1,
               android.bigText=hola asunto  hoa cuerpo,
               android.infoText=null,
                android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=1404],
                 android.originatingUserId=0,
                  android.progressIndeterminate=false}]


 SBN notification  :Notification(
 pri=0
 contentView=com.google.android.gm/0x10900b4
  vibrate=null
   sound=content://settings/system/notification_sound
   tick defaults=0x4
   flags=0x11
   color=0xffdb4437
   category=email
   actions=2
    vis=PRIVATE
    publicVersion=Notification(pri=0
    contentView=com.google.android.gm/0x10900b4
    vibrate=null
    sound=null
    defaults=0x0
     flags=0x0
     color=0xffdb4437 category=email vis=PUBLIC secFlags=0x0 secPriority=0) secFlags=0x0 secPriority=0)



 RESUMEN GMAIL:

 SBN notification extras:
             android.title=jose ramon delgado,
             android.text=hola asunto,
             android.subText=interamaster@gmail.com,
             android.bigText=hola asunto  hoa cuerpo,


    MANDA 1 KEY AL PRINICIPIO PERO DA ERROR: Not allowed to write file descriptors here SI ES EL PRIMER MENSAJE!!!:


    I/NOTIFSService: SBN notification extras :Bundle[{android.title=mí, android.subText=interamaster@gmail.com, android.template=android.app.Notification$BigTextStyle, android.showChronometer=false, android.icon=2130837958, android.text=ASUNTO, android.progress=0, android.progressMax=0, android.showWhen=true, android.rebuild.applicationInfo=ApplicationInfo{45055e6 com.google.android.gm}, android.people=[Ljava.lang.String;@644d727, android.largeIcon=android.graphics.Bitmap@407add4, android.bigText=ASUNTO
                                                                           TEXTO 1 SOLO, android.infoText=null, android.wearable.EXTENSIONS=Bundle[mParcelledData.dataSize=1500], android.originatingUserId=0, android.progressIndeterminate=false}]
09-03 23:33:13.139 16258-18793/com.mio.jrdv.ambientnotifs I/NOTIFSService: SBN notification  :Notification(pri=0 contentView=com.google.android.gm/0x10900b4 vibrate=null sound=content://settings/system/notification_sound tick defaults=0x4 flags=0x219 color=0xffdb4437 category=email groupKey=gig:2060997336:^sq_ig_i_personal sortKey=92233705008443867500 actions=2 vis=PRIVATE publicVersion=Notification(pri=0 contentView=com.google.android.gm/0x10900b4 vibrate=null sound=content://settings/system/notification_sound tick defaults=0x4 flags=0x19 color=0xffdb4437 category=email vis=PUBLIC secFlags=0x0 secPriority=0) secFlags=0x0 secPriority=0)



  I/NEW: ----------
 I/NOTIFSService: SBN APPNAME:com.google.android.gm
  I/NOTIFSService: SBN notif color:#D44638
  I/Title: mí
 I/TEXT: ASUNTO
0 I/BIGTEXT: ASUNTO
                                                                     TEXTO 1 SOLO
  I/FINAL: ----------







   KEY: 0|com.google.android.gm|0|gig:2060997336:^sq_ig_i_personal|10120 ID: 0 Posted by: com.google.android.gm at: 1535994595967


   EL SEGUNDO MENSAJE NO DA ERROR PERO SOLO MANDA EL TITLE

    I/NOTIFSService: SBN notification extras :Bundle
    [{android.title=2 mensajes nuevos,
     android.textLines=[Ljava.lang.CharSequence;@2283619,
      android.subText=interamaster@gmail.com,
      android.template=android.app.Notification$InboxStyle,
       android.showChronometer=false,
       android.icon=2130838004,
        android.text=null,
        android.progress=0,
         android.progressMax=0,
          android.showWhen=true,
           android.rebuild.applicationInfo=ApplicationInfo{bf0d6de com.google.android.gm},
            android.people=[Ljava.lang.String;@4b750bf,
            android.infoText=null,
             android.originatingUserId=0,
             android.progressIndeterminate=false}]

09-03 19:12:50.882 14240-14253/com.mio.jrdv.ambientnotifs I/NOTIFSService: SBN notification
:Notification(pri=0
contentView=com.google.android.gm/0x10900b4
 vibrate=null
  sound=content://settings/system/notification_sound tick
  defaults=0x4
  flags=0x219
  color=0xffdb4437
   category=email
    groupKey=gig:2060997336:^sq_ig_i_personal
    sortKey=92233705008600077230
    vis=PRIVATE publicVersion=Notification(pri=0
    contentView=com.google.android.gm/0x10900b4
    vibrate=null sound=content://settings/system/notification_sound tick defaults=0x4 flags=0x19 color=0xffdb4437 category=email vis=PUBLIC secFlags=0x0 secPriority=0) secFlags=0x0 secPriority=0)

   I/NEW: ----------
 SBN APPNAME:com.google.android.gm
 SBN notif color:#D44638
 I/Title: 2 mensajes nuevos
  I/FINAL: ----------

     */

        Drawable appIconGmail = null;
        try {


            appIconGmail =getActivity().getPackageManager().getApplicationIcon("com.google.android.gm");



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


            appIconOutlook=getActivity().getPackageManager().getApplicationIcon("com.microsoft.office.outlook");



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

        /*
        RESUMEN TELEGRAM:

        SBN notification extras:
        android.title=Yo Philips (1 mensaje nuevo)
        android.text=  Hola que tal
        android.subText=null
        android.bigText=  Hola que tal



TAMBIEN MANDA 2 NOTIFS...

KEY: 0|org.telegram.messenger|1|null|10200 ID: 1
Y

 KEY: 0|org.telegram.messenger|10503770|null|10200 ID: 10503770
        */



                Drawable appIconTelegram = null;
        try {


            appIconTelegram =getActivity().getPackageManager().getApplicationIcon("org.telegram.messenger");



            Preference telegram = findPreference("Telegram");
            telegram.setIcon(appIconTelegram);
            //lo guardamos a SI
            mPrefs.edit().putBoolean("Telegram",true).apply();

        } catch (PackageManager.NameNotFoundException e) {

            //si no existe el icono ..tampoc existe la app
            Preference telegram = findPreference("Telegram");
            telegram.setEnabled(false);

            //lo guardamos a NO
            mPrefs.edit().putBoolean("Telegram",false).apply();


            e.printStackTrace();
        }


        // TELEGRAM X:

        try {


            appIconTelegram =getActivity().getPackageManager().getApplicationIcon("org.thunderdog.challegram");



            Preference telegram = findPreference("Telegram");
            telegram.setIcon(appIconTelegram);
            //lo guardamos a SI
            mPrefs.edit().putBoolean("Telegram",true).apply();
            telegram.setEnabled(true);

        } catch (PackageManager.NameNotFoundException e) {

            //si no existe el icono ..tampoc existe la app
            Preference telegram = findPreference("Telegram");
            telegram.setEnabled(false);

            //lo guardamos a NO
            mPrefs.edit().putBoolean("Telegram",false).apply();


            e.printStackTrace();
        }





        //SPOTIFY



        /*
        ID:2131363978
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: Posted by:com.spotify.music
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: tickerText:El Cielo Cambió de Forma — Robe
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.title=El Cielo Cambió de Forma
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.subText=Robe Iniesta
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.template=android.app.Notification$MediaStyle
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.showChronometer=false
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.icon=2131231420
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.text=Robe
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.progress=0
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.progressMax=0
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.appInfo=ApplicationInfo{7d2b55d com.spotify.music}
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.showWhen=true
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.largeIcon=android.graphics.Bitmap@ae7bed2
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.infoText=null
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.mediaSession=android.media.session.MediaSession$Token@ebc3b3b
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.progressIndeterminate=false
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.remoteInputHistory=null
09-04 18:24:07.787 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.compactActions=[I@93c6ba3
09-04 18:24:07.855 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: ID:2131363978
09-04 18:24:07.855 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: Posted by:com.spotify.music
09-04 18:24:07.855 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: tickerText:El Cielo Cambió de Forma — Robe
09-04 18:24:07.855 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.title=El Cielo Cambió de Forma
09-04 18:24:07.856 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.subText=Robe Iniesta
09-04 18:24:07.856 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.template=android.app.Notification$MediaStyle
09-04 18:24:07.856 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.showChronometer=false
09-04 18:24:07.856 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.icon=2131231420
09-04 18:24:07.856 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.text=Robe
09-04 18:24:07.856 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.progress=0
09-04 18:24:07.856 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.progressMax=0
09-04 18:24:07.856 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.appInfo=ApplicationInfo{a97aea0 com.spotify.music}
09-04 18:24:07.856 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.showWhen=true
09-04 18:24:07.859 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.largeIcon=android.graphics.Bitmap@98a4a59
09-04 18:24:07.859 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.infoText=null
09-04 18:24:07.860 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.mediaSession=android.media.session.MediaSession$Token@ebc3b3b
09-04 18:24:07.860 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.progressIndeterminate=false
09-04 18:24:07.861 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.remoteInputHistory=null
09-04 18:24:07.861 3776-3776/com.mio.jrdv.ambientnotifs I/NOTIFSService: android.compactActions=[I@c5ef61e
09-04 18:24:07.939 3776-3792/com.mio.jrdv.ambientnotifs I/zygote64: NativeAllocBackground concurrent copying GC freed 23069(4MB) AllocSpace objects, 3(60KB) LOS objects, 81% free, 1378KB/7MB, paused 4.295ms total 112.342ms
         */
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

     //   Log.i("starquiettime: ",mPrefs.getString("startTime","null"));
     //   Log.i("stopquiettime:  ",mPrefs.getString("stopTime","null"));




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
