package oh.cwrh.com.oh.database;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import oh.cwrh.com.oh.tools.Debug;

/**
 * Created by Nic on 1/31/2015.
 */
public class Contact {
    private long id;
    private String name;
    private String phone;
    private Uri photo;

    public long getId(){return id;}
    public String getName(){return name;}
    public String getPhone(){return phone;}
    public Uri getPhotoUri(){return photo;}

    /**
     * Creates a JSON string
     * for sycning to wearable
     */
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("name", this.name);
            json.put("phone_number", this.phone);
            json.put("ID", this.id);

            if(photo == null){
                json.put("photo", JSONObject.NULL);
            }else{
                json.put("photo", this.photo);
            }
        }catch(JSONException e){
            Debug.log("Could not make JSON string from Contact --- " + e.getMessage());
        }

        return json;
    }


    /**
     * Building a contact from the database
     * @param i
     * @param n
     * @param p
     */
    public Contact(long id,String name, String phone){
        this.name = name;
        this.phone = phone;
        this.id = id;
        photo = null;
    }

    /**
     * Building a contact from the database
     * @param i
     * @param n
     * @param p
     * @param pic
     */
    public Contact(long id, String name, String phone, Uri pic){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.photo = pic;

    }

    /**
     * Building a contact to put into the database
     * @param n
     * @param p
     * @param pic
     */
    public Contact(String name, String phone, Uri pic){
        this.name = name;
        this.phone = phone;
        this.photo = pic;
    }

    /**
     * Building a contact to put into the database
     * @param n
     * @param p
     */
    public Contact(String name, String phone){
        this.name = name;
        this.phone = phone;
        photo = null;
    }

    /**
     * Blank constructor
     */
    public Contact(){
        name = "";
        phone = "";
        photo = null;
    }

}
