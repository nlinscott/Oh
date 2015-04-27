package oh.cwrh.com.oh.datasync;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import oh.cwrh.com.oh.database.Contact;
import oh.cwrh.com.oh.database.DataSource;
import oh.cwrh.com.oh.tools.Debug;

public class DataService extends WearableListenerService {


    private static final String SYNC_PATH = "/contacts";


    @Override
    public void onMessageReceived(MessageEvent messageEvent){
        if( messageEvent.getPath().equalsIgnoreCase( SYNC_PATH ) ) {

            DataSource ds = DataSource.getInstance(this);
            ds.open();
            ds.deleteAllContacts();

            byte[] bytes = messageEvent.getData();
            String jsonStr = new String(bytes);

            try {

                JSONObject json = new JSONObject(jsonStr);
                for (int i = 0; i < json.length(); ++i){
                    JSONObject child = json.getJSONObject(i + "");
                    //TODO: later
                    //temp.getString("photo")
                    Contact c = new Contact(child.getString("name"),
                            child.getString("phone_number")
                    );
                    ds.addContact(c);
                }

            }catch(JSONException ex){
                Debug.log("json error " + ex.getMessage());
            }finally{
                ds.close();
            }

        } else {
            super.onMessageReceived( messageEvent );
        }
    }
}
