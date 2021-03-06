package com.cwrh.oh.services;

import android.content.Intent;

import com.cwrh.oh.tools.Debug;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;

public class ReceiveWearableMessages extends WearableListenerService {

    private static final String SMS_PATH = "/sms";

    public ReceiveWearableMessages() {
    }

    /*
    @Override
    public void onDestroy() {
        super.onDestroy();

        Debug.log("destroying ReceiveWearableMEssages service");
    }
    */


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase( SMS_PATH ) ) {



            byte[] bytes = messageEvent.getData();
            String jsonStr = new String(bytes);

            try {

                JSONObject json = new JSONObject(jsonStr);

                Intent intent = new Intent(this, SendSMS.class);
                intent.putExtra(SendSMS.NAME, json.getString("name"));
                intent.putExtra(SendSMS.PHONE_NUMBER, json.getString("phone_number"));
                intent.setAction(json.getString("action"));

                startService(intent);

            }catch(JSONException ex){
                Debug.log("json error " + ex.getMessage());
            }

        } else {
            super.onMessageReceived( messageEvent );
        }

    }

}
