package oh.cwrh.com.oh;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;


public class SendSMS extends IntentService {

    public static final String ACTION_SEND_OMW = "oh.cwrh.com.oh.action.OMW";
    public static final String ACTION_SEND_HERE = "oh.cwrh.com.oh.action.HERE";

    private static final int DELAY = 2000;
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
                sendSMS(getResources().getString(R.string.leaving),number, name);
            } else if (action.equals(ACTION_SEND_HERE)) {
                sendSMS(getResources().getString(R.string.here),number,name);
            }
        }
    }

    private void sendSMS(String message, String number, String name){
       final NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle( getString(R.string.sending) + " " + message )
                .setContentText("to " + name)
                .setOngoing(false)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);

        nm.notify(NOTIFICATION_ID, builder.build());

        try{
            //TODO: uncomment this if you want to send SMS. Omitted for UI testing
            //Debug.log(message + " to " + name + " " + number);
            //SmsManager smsManager = SmsManager.getDefault();
           // smsManager.sendTextMessage(number,null,message,null,null);
            builder.setContentTitle(getString(R.string.sent) + " " + message);
            nm.notify(NOTIFICATION_ID, builder.build());


        }catch (Exception e){
            e.printStackTrace();
            builder.setContentTitle(getString(R.string.message_not_sent))
                    .setSmallIcon(android.R.drawable.ic_dialog_alert);

            nm.notify(NOTIFICATION_ID, builder.build());
        }

        stopSelf();

    }

}
