package oh.cwrh.com.oh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;

import oh.cwrh.com.oh.database.DataSource;
import oh.cwrh.com.oh.fragments.ContactsListFragment;

//TODO: make a tutorial view with a "do not show again" button

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
        Intent intent = new Intent( this, EditContactList.class );

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

                    AlertFactory.getAlertDialogBuilder(
                            getResources().getString(R.string.uh_oh),
                            name + getResources().getString(R.string.no_number) ,
                            this).show();

                }else {
                    int phoneType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                        //its a mobile number, so grab it, set the flag to true and save it
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                       // phoneNumber = phoneNumber.replaceAll("\\D", "");
                        //Debug.log(phoneNumber);
                        hasMobileNumber = true;
                    }
                }

                //close cursors to free memory
                phones.close();
                cursor.close();
                if(hasMobileNumber) {
                    DataSource ds = new DataSource(getApplicationContext());
                    ds.open();
                    ds.addContactToUI(name, phoneNumber);
                    ds.close();
                }else{
                    //no mobile number
                    AlertFactory.getAlertDialogBuilder(
                            getResources().getString(R.string.uh_oh),
                            name + getResources().getString(R.string.no_mobile_number),
                            this).show();
                }

            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        FragmentManager mgr = getFragmentManager();
        ContactsListFragment frag = (ContactsListFragment)mgr.findFragmentById(R.id.main_list_frag);
        frag.notifyDataSetChanged();

    }


    /**
     * alert factory builds dialogs with a title and body. Implementation shows it once build
     *
     */
    private static class AlertFactory{


        public static AlertDialog getAlertDialogBuilder(String title, String text, Context context){

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            return builder.create();

        }


    }
}
