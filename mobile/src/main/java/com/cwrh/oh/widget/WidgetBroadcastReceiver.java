package com.cwrh.oh.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cwrh.oh.services.SendSMS;

public class WidgetBroadcastReceiver extends BroadcastReceiver {


    public static final String WIDGET_BUTTON_CLICKED = "com.cwrh.oh.custom.intent.WIDGET_BUTTON_CLICKED";

    public WidgetBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context c, Intent intent){

        String action = intent.getAction();

        Intent start = new Intent(c, SendSMS.class);
        //was packed into a bundle in the ContactsAdapter, must get a bundle of extras
        //using SendSMS constants
        Bundle bundle =  intent.getExtras();
        //Debug.log(b.getString(SendSMS.PHONE_NUMBER));

        if(action.equals(WIDGET_BUTTON_CLICKED)){
            if(intent.getBooleanExtra(SendSMS.IS_LEAVING,true)){
                start.setAction(SendSMS.ACTION_SEND_OMW);
            }else if(!intent.getBooleanExtra(SendSMS.IS_LEAVING,false)){
                start.setAction(SendSMS.ACTION_SEND_HERE);
            }
        }

        start.putExtra(SendSMS.NAME, bundle.getString(SendSMS.NAME));
        start.putExtra(SendSMS.PHONE_NUMBER, bundle.getString(SendSMS.PHONE_NUMBER));

        c.startService(start);

    }

}
