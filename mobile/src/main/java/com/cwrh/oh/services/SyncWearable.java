package com.cwrh.oh.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cwrh.oh.database.Contact;
import com.cwrh.oh.database.DataSource;
import com.cwrh.oh.tools.Debug;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nic on 4/26/2015.
 */
public class SyncWearable extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String SYNC_PATH = "/contacts";



    private GoogleApiClient googleClient;

    public SyncWearable(){
        super("SyncWearable");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleClient.connect();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(googleClient.isConnected()){
            googleClient.disconnect();
        }
    }

    public void sync() {
        new SendContactList(this).start();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Debug.log("connected");
        sync();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Debug.log("connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Debug.log("connection failed");
    }

    private class SendContactList extends Thread {

        private Context context;
        // Constructor for sending data objects to the data layer
        SendContactList(Context c) {
            context = c;
        }

        public void run() {


            DataSource ds = DataSource.getInstance(context);
            ds.open();
            ArrayList<Contact> list = ds.getAllContacts();
            ds.close();

            JSONObject json = new JSONObject();

            try {

                for (int i = 0; i < list.size(); ++i) {
                    json.put(i + "", list.get(i).toJSON());
                }

            }catch (JSONException ex){
                Debug.log(ex.getMessage());
            }

            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {

                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                        googleClient, node.getId(),
                        SYNC_PATH, json.toString().getBytes())
                        .await();

                if(result.getStatus().isSuccess()){
                    Debug.log("message sent successfully");
                    //TODO: handle success/failure
                   // Toast.makeText(context, context.getResources().getString(R.string.contacts_synced),Toast.LENGTH_LONG).show();
                }else{
                    Debug.log("error: message not sent");
                    //TODO: handle success/failure
                   // Toast.makeText(context, context.getResources().getString(R.string.error_syncing),Toast.LENGTH_LONG).show();

                }

            }

            stopSelf();
        }
    }

}
