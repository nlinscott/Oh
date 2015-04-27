package oh.cwrh.com.oh;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import oh.cwrh.com.oh.database.Contact;
import oh.cwrh.com.oh.database.DataSource;
import oh.cwrh.com.oh.fragments.ContactInfoFragment;
import oh.cwrh.com.oh.services.SyncWearable;
import oh.cwrh.com.oh.tools.ContactInfoHandler;
import oh.cwrh.com.oh.tools.ZoomTransformer;

/**
 * Created by Nic on 3/10/2015.
 */
public class ContactSwipeViewActivity extends FragmentActivity {

    private ViewPager viewPager;

    private PagerAdapter pagerAdapter;

    private static final int PICK_CONTACT = 1;

    ArrayList<Contact> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);
        viewPager = (ViewPager) findViewById(R.id.pager);

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
        }else if(id == R.id.sync){
            startSync();
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

    private void startSync(){

        startService(new Intent(this, SyncWearable.class));
        Toast.makeText(this, getResources().getString(R.string.begin_sync), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        refreshContactsList();
        pagerAdapter = new ContactsSliderAdapter(getSupportFragmentManager());

        //only load one item ahead, the rest are destroyed
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, new ZoomTransformer());
    }

    private void refreshContactsList(){
        DataSource dataSource = DataSource.getInstance(this);
        dataSource.open();
        contactsList = dataSource.getAllContacts();
        dataSource.close();
    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (reqCode == PICK_CONTACT) {
                final Uri contactData = data.getData();

                //instantiate handler
                ContactInfoHandler infoHandler = new ContactInfoHandler(contactData, this);

                //get the contact
                Contact contact = infoHandler.buildContact();

                //is the contact valid?
                if(infoHandler.hasValidPhoneNumber()){
                    //save it
                    saveContact(contact);

                }else{
                    //alert an invalid contact
                    AlertFactory.getAlertDialogBuilder(
                            getResources().getString(R.string.uh_oh),
                            getResources().getString(R.string.no_number) ,
                            this).show();

                }
            }
        }
    }

    private void saveContact(Contact contact){
        DataSource ds = DataSource.getInstance(this);
        ds.open();
        //if the contact doesn't exist in the list
        if(!ds.contactExists(contact)){
            //add it
            contactsList.add( ds.addContactToUI(contact) );

            pagerAdapter.notifyDataSetChanged();
        }else{
            //cannot add twice
            AlertFactory.getAlertDialogBuilder(
                    getResources().getString(R.string.error_exists),
                    contact.getName() +" "+ getResources().getString(R.string.already_exists),
                    this).show();
        }

        ds.close();


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

    /**
     * Creates a slider adapter for the viewPager initialized in {@link #onResume();}
     * FragmentStatePagerAdapter loads and unloads only a certain
     * number of items at once. The rest are destroyed.
     */
    private class ContactsSliderAdapter extends FragmentStatePagerAdapter {


        public ContactsSliderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ContactInfoFragment frag = new ContactInfoFragment();
            Contact c = contactsList.get(position);
            frag.setContact(c);
            return frag;
        }

        @Override
        public int getCount() {
            return contactsList.size();
        }

    }
}
