package oh.cwrh.com.oh;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;

import oh.cwrh.com.oh.custom.SwipeView;

public class ViewContactsActivity extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                SwipeView sv = (SwipeView)findViewById(R.id.custom);
                SwipeView sv1 = (SwipeView)findViewById(R.id.custom1);
                SwipeView sv2 = (SwipeView)findViewById(R.id.custom2);
                SwipeView sv3 = (SwipeView)findViewById(R.id.custom3);
                sv.setNameAndNumber("Nic Linscott", "123456789");
                sv1.setNameAndNumber("Alfred Shaker", "12345677");
                sv2.setNameAndNumber("Lexie Leombruno", "123456789");
                sv3.setNameAndNumber("Matt Allen", "123456789");


            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle){
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int flag) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result){
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents){

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d("", "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d("", "DataItem changed: " + event.getDataItem().getUri());
            }
        }

    }

}


