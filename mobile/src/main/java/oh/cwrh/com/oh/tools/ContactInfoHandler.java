package oh.cwrh.com.oh.tools;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import oh.cwrh.com.oh.database.Contact;

/**
 * Created by Nic on 3/12/2015. Uses inherited final identifiers in cursor queries
 */
public class ContactInfoHandler extends CCWrapper{

    /**
     * Data uri received in onActivityResult
     */
    private Uri contactData;

    private Context context;

    /**
     * used to check whether the contact is valid before saving and while attempting to save
     */
    private boolean hasPhoneNumber = false;

    /**
     * Needs the intent data from OnActivityResult() and a context to getContentResolver
     * @param data
     * @param c
     */
    public ContactInfoHandler(Uri data, Context c){
        contactData = data;
        context = c;
    }

    /**
     * Build a Contact to be stored in the database
     * @return
     */
    public Contact buildContact(){

        String phoneNumber;
        String name;
        Uri photo = null;

        //query to see if the phone number exists and is mobile
        Cursor phones = context.getContentResolver().query(
                contactData,
                PHONE_NUMBER_PROJECTION,
                REQUIRE_MOBILE,
                null, null);

        hasPhoneNumber = phones.moveToFirst();

        if(hasPhoneNumber){
            phoneNumber = phones.getString(phones.getColumnIndex(NUMBER));
        }else{
            //phone number is not mobile
            //close cursor
            phones.close();
            //return a default, nothing contact.
            return new Contact();
        }
        //done with this cursor
        phones.close();

        //query name, photo where has phone number is true
        Cursor data = context.getContentResolver().query(
                contactData,
                BASIC_PROJECTION,
                REQUIRE_PHONE_NUMBER,
                null, null);
        data.moveToFirst();

        name = data.getString(data.getColumnIndexOrThrow(NAME));

        //checks the index of the cursor before getting data
        if( data.getString(data.getColumnIndex(PHOTO_THUMB_URI)) != null )
            photo = Uri.parse(data.getString(data.getColumnIndex(PHOTO_THUMB_URI)));
        //done
        data.close();

        //build a contact
        return new Contact(name, phoneNumber, photo);
    }

    /**
     * Returns true if the the contact has phone numbers, and if one of the numbers is mobile.
     * @return
     */
    public boolean hasValidPhoneNumber(){
        return hasPhoneNumber;
    }

}
