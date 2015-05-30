package com.cwrh.oh.tools;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;

import com.cwrh.oh.database.Contact;
import com.cwrh.oh.database.DataSource;
import com.cwrh.oh.interfaces.SyncCallback;
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
public class SyncWearable  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String SYNC_PATH = "/contacts";
    private Context context;
    private SyncCallback callback;


    private GoogleApiClient googleClient;

    public SyncWearable(Context c, SyncCallback callback){
        this.callback = callback;
        context = c;
        googleClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleClient.connect();
    }


    public void sync() {
        new SendContactList(context).start();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Debug.log("connected");
        callback.onStart();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Debug.log("connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Debug.log("connection failed");

        callback.onComplete(false);
    }

    private class SendContactList extends Thread {

        private Context context;
        // Constructor for sending data objects to the data layer
        SendContactList(Context c) {
            context = c;
        }

        public void run() {


            Looper.prepare();

            DataSource ds = DataSource.getInstance(context);
            ds.open();
            ArrayList<Contact> list = ds.getAllContacts();
            ds.close();

            JSONObject json = new JSONObject();

            try {

                for (int i = 0; i < list.size(); ++i) {
                    json.put(i + "", list.get(i).toJSON());
                }

            }catch (JSONException ex) {
                Debug.log(ex.getMessage());
                callback.onComplete(false);
                tearDown();
                return;
            }

            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {

                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                        googleClient, node.getId(),
                        SYNC_PATH, json.toString().getBytes())
                        .await();

                if(result.getStatus().isSuccess()){
                    Debug.log("message sent successfully");
                    callback.onComplete(true);
                }else{
                    Debug.log("error: message not sent");
                    callback.onComplete(false);
                }

            }
            Looper.loop();

            tearDown();
        }
    }

    private void tearDown(){
        if(googleClient.isConnected()){
            googleClient.disconnect();
        }
    }

}
