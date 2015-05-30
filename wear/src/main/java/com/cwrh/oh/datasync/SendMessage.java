package com.cwrh.oh.datasync;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;

import com.cwrh.oh.R;
import com.cwrh.oh.database.Contact;
import com.cwrh.oh.tools.Debug;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nic on 4/27/2015.
 */
public class SendMessage implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private static final String SMS_PATH = "/sms";
    public static final String ACTION_SEND_OMW = "com.cwrh.oh.action.OMW";
    public static final String ACTION_SEND_HERE = "com.cwrh.oh.action.HERE";

    public Context context;

    public SendMessage(Context c){

        context = c;
        googleApiClient = new GoogleApiClient.Builder(c)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();

        googleApiClient.connect();
    }


    public void sendMessage(Contact c, String action){
        new SendTextMessageData(c, action).start();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private class SendTextMessageData extends Thread {

        private Contact contact;
        private String action;
        // Constructor for sending data objects to the data layer
        SendTextMessageData(Contact contact, String action) {
            this.contact = contact;
            this.action = action;
        }

        public void run() {

            try {
                JSONObject json = contact.toJSON();

                json.put("action", action);

                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
                for (Node node : nodes.getNodes()) {

                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            googleApiClient, node.getId(),
                            SMS_PATH, json.toString().getBytes())
                            .await();

                    if(result.getStatus().isSuccess()){
                        Debug.log("message sent successfully");
                        //TODO: handle success/failure
                        success();
                    }else{
                        fail();
                    }
                }

            }catch (JSONException ex){
                Debug.log("Error creating message to send to handheld: " + ex.getMessage());
                fail();
            }


            googleApiClient.disconnect();
        }
    }

    public void success(){
        Intent intent = new Intent(context, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                context.getResources().getString(R.string.sent));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
    }

    public void fail(){
        Intent intent = new Intent(context, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.FAILURE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                context.getResources().getString(R.string.not_sent));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
