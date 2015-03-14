package oh.cwrh.com.oh.tools;

import android.provider.ContactsContract;

/**
 * Created by Nic on 3/13/2015.
 * Consolidates the ContactsContract.Contacts  and .CommonDataKinds fields
 */
abstract class CCWrapper {

    /**
     * Used to query contact data
     */
    public final String ID = ContactsContract.Contacts._ID;
    public final String NAME = ContactsContract.Contacts.DISPLAY_NAME;
    public final String HAS_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    public final String PHOTO_THUMB_URI = ContactsContract.Contacts.PHOTO_URI;
    public final String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    public final String NUMBER_TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;

    /**
     * Used as a WHERE clause to select only mobile phone numbers from a contact
     */
    public final String REQUIRE_MOBILE = ContactsContract.CommonDataKinds.Phone.TYPE +
            "=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

    /**
     * Used as a WHERE clause to ensure the contact has a phone number
     */
    public final String REQUIRE_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";

    /**
     * get the id, name, phone number flag and photo uri
     */
    public final String[] BASIC_PROJECTION = {
            ID,
            NAME,
            HAS_NUMBER,
            PHOTO_THUMB_URI
    };

    /**
     * get the number and type of the number. Must be a mobile number
     */
    public final String[] PHONE_NUMBER_PROJECTION = new String[] {
            NUMBER,
            NUMBER_TYPE
    };

}
