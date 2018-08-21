package com.mio.jrdv.ambientnotifs;

import android.app.Activity;
import android.app.Notification;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.PowerManager;
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


   // protected MyInfoNotifcReceiver mReceiver = new MyInfoNotifcReceiver();// //NUCA LLEGABAN DATOS
    //public static String INTENT_ACTION_NOTIFICATION = "com.mio.jrdv.ambientnotifs";//NUCA LLEGABAN DATOS
    protected TextView title;
    protected TextView text;
    protected TextView subtext;
    protected ImageView largeIcon;


    //PARA EL LOGGING

    private String TAG = this.getClass().getSimpleName();



    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        Log.i("INICIO :", "AlarmReceiverActivity:");








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






        Button stopAlarm = (Button) findViewById(R.id.stopAlarm);
        stopAlarm.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {

                finish();
                return false;
            }
        });



        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);


        //recibimos data extra del intent?



        Bundle b = getIntent().getExtras();
        if(b !=null){


            Log.i(TAG, "SBN notification  :" + b );

            //rellenamos como en Main

            String notificationTitle = b.getString(Notification.EXTRA_TITLE);
            int notificationIcon = b.getInt(Notification.EXTRA_SMALL_ICON);
            Bitmap notificationLargeIcon = ((Bitmap) b.getParcelable(Notification.EXTRA_LARGE_ICON));
            CharSequence notificationText = b.getCharSequence(Notification.EXTRA_TEXT);
            CharSequence notificationSubText = b.getCharSequence(Notification.EXTRA_SUB_TEXT);

            title.setText(notificationTitle);
            text.setText(notificationText);
            subtext.setText(notificationSubText);

            if (notificationLargeIcon != null) {
                largeIcon.setImageBitmap(notificationLargeIcon);

            }

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
    
    @Override
    protected void onStop() {
    	super.onStop();
        if (wl.isHeld())
            wl.release();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("INFO", "pulsado para dismiss");

        mDPM.lockNow();
        this.finish();
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

}