package oh.cwrh.com.oh;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


import oh.cwrh.com.oh.custom.SwipeView;
import oh.cwrh.com.oh.database.Contact;
import oh.cwrh.com.oh.database.DataSource;
import oh.cwrh.com.oh.tools.Debug;


public class ContactsListActivity extends Activity {
    private static final int PICK_CONTACT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.add){
            addItem();
        }else if(id == R.id.remove){
            removeItem();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addItem(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT);
    }

    private void removeItem(){
        Intent intent = new Intent( this, RemoveFavorites.class );

        startActivity(intent);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (reqCode == PICK_CONTACT) {
                final Uri contactData = data.getData();

                //we want the id, name and number
                String[] projection = {
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER};
                //for those who have a number
                String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";

                //of the numbers, make sure its mobile
                final String[] numberProjection = new String[] {
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                };


                Cursor cursor = getContentResolver()
                        .query(contactData, projection, selection, null, null);

                cursor.moveToFirst();

                Cursor phones;
                String phoneNumber = "";

                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                boolean hasMobileNumber = false;

                // get all the phone numbers from a contact to look for a mobile number
                phones = getContentResolver().query(contactData,
                        numberProjection,
                        null,
                        null, null);
                if(!phones.moveToFirst()){
                    //TODO: No phone number, probably an email contact
                }else {
                    int phoneType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                        //its a mobile number, so grab it, set the flag to true and save it
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                       // phoneNumber = phoneNumber.replaceAll("\\D", "");
                        Debug.log(phoneNumber);
                        hasMobileNumber = true;
                    }
                }

                //close cursors to free memory
                phones.close();
                cursor.close();
                if(hasMobileNumber) {
                    DataSource ds = new DataSource(getApplicationContext());
                    ds.open();
                    ds.addContact(name, phoneNumber);
                    ds.close();
                }

            }
        }
        initialize();
    }

    @Override
    protected void onPause(){
        super.onPause();
        LinearLayout layout = (LinearLayout)findViewById(R.id.container);
        layout.removeAllViews();
    }
    @Override
    protected void onResume(){
        super.onResume();
        LinearLayout layout = (LinearLayout)findViewById(R.id.container);
        layout.removeAllViews();
        initialize();
    }

    public void initialize(){
        LinearLayout layout = (LinearLayout)findViewById(R.id.container);
        DataSource ds = new DataSource(this);
        ds.open();
        for(Contact c : ds.getAllContacts()){
            View item = LayoutInflater.from(this).inflate(R.layout.contacts_list_item, null);
            layout.addView(item);
            SwipeView lrc = (SwipeView)item.findViewById(R.id.contact);
            lrc.setNameAndNumber(c.getName(),c.getPhone());
        }
        ds.close();
    }
}
