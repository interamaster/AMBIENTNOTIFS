package com.mio.jrdv.ambientnotifs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
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
    //v0915 CAMBIADA SINGÑLE TAP DISMIS Y DOBLE TAP FINISH
    //V0916 AÑADIDO COLORES A AMANO SEGUN APK EN VEZ DE COGERLOS DEL ICONO
    //v0917 CAMBIADO ENVIO DE INFO A LA ACTIVITY AHORMA NAMDO LOS TEXTO Y LOAS IMAGENES EN VEZ DE EXTRA COMPLETO AL INTENT
    //v095 ARREGLADO NOTIS DE  GMAIL Y OUTLOOK CON TEXTO OK
    //v096 AÑADIDO ADS OK!!!
    //v096 AÑADIDO ICONO  AYUDA EN PANTALLA PRAL Y ADS EN MODO TEST AVER COMO VA LA COSA..
    //v099 ANTES DE PUBLICAR AUN CON ADS TEST
    //V1.0 PUBLICADA 5/9/2018 HECO LIMPEZA DE LOGS Y DE TEXT DE AYUDA !!!
    //V1.1 AÑADIDAS ESTADISTICAS DE USO
    //V1.1.01 CON GRAFICOS MAS BONITA
    //V1.1.02 CON GARFICOS DE TOTALES Y MEDIA
    //v1.1.03 iconos oreo ok
    //V1,1 FINAL GOOGLE PLAY AÑADIDO MEDIA DIARIA



    protected TextView title;
    protected TextView text;
    protected TextView subtext;
    protected ImageView largeIcon, appicon;
    protected ImageView Fondodifuminar;


    //para el reloj color


    private TextClock reloj;


    // variable to track event time
    private long mLastClickTime = 0;
    private boolean issingleclicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //https://stackoverflow.com/questions/30246425/turning-on-screen-from-receiver-service:

       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);



        super.onCreate(savedInstanceState);


        issingleclicked=false;


    setContentView(R.layout.settings);


    }



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

}
