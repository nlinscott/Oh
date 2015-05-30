package com.cwrh.oh.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.cwrh.oh.R;
import com.cwrh.oh.tools.Debug;


public class SendSMS extends IntentService {

    public static final String ACTION_SEND_OMW = "com.cwrh.oh.action.OMW";
    public static final String ACTION_SEND_HERE = "com.cwrh.oh.action.HERE";

    private static final int DELAY = 5000;
    public static final String PHONE_NUMBER = "phone";
    public static final String IS_LEAVING = "isLeaving";
    public static final String NAME = "name";
    private static final int NOTIFICATION_ID = 9;

    public SendSMS() {
        super("SendSMS");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            String number = intent.getStringExtra(PHONE_NUMBER);
            String name = intent.getStringExtra(NAME);

            if (action.equals(ACTION_SEND_OMW)) {
                sendSMS(getResources().getString(R.string.omw),number, name);
            } else if (action.equals(ACTION_SEND_HERE)) {
                sendSMS(getResources().getString(R.string.here),number,name);
            }
        }
    }

    private void sendSMS(String message, String number, String name){
       final NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


        Notification.Builder builder = new Notification.Builder(this)
                .setContentText("to " + name)
                .setOngoing(false)
                .setAutoCancel(true);

        boolean result;
        try{
            //TODO: uncomment this if you want to send SMS. Omitted for UI testing
            //Debug.log(message + " to " + name + " " + number);
            //debug line below:
            //int i = 1/0;
            //SmsManager smsManager = SmsManager.getDefault();
            //smsManager.sendTextMessage(number,null,message,null,null);

            builder.setContentTitle(getString(R.string.sent) + " " + message);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            nm.notify(NOTIFICATION_ID, builder.build());

            result = true;

        }catch (Exception e){
            result = false;
            e.printStackTrace();
            builder.setContentTitle(getString(R.string.message_not_sent))
                    .setSmallIcon(android.R.drawable.ic_dialog_alert);

            nm.notify(NOTIFICATION_ID, builder.build());
        }

        vibrate(this);

        if(result) {
            DelayedStop ds = new DelayedStop(nm);
            ds.start();
        }else{
            stopSelf();
        }

    }

    private void vibrate(Context c){
        Vibrator v = (Vibrator)c.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(150);
    }

    private class DelayedStop extends Thread{

        private NotificationManager manager;

        public DelayedStop(NotificationManager mgr){
            manager = mgr;
        }

        public void run(){

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException ex) {
                Debug.log("interrupted after sending sms!!! " + ex.getMessage());
            }
            manager.cancel(NOTIFICATION_ID);

            stopSelf();
        }
    }

    /*
    @Override
    public void onDestroy(){
        super.onDestroy();
        Debug.log("destroying SendSMS service");
    }
    */

}
