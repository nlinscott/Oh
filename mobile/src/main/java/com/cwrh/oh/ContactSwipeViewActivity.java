package com.cwrh.oh;

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

import com.cwrh.oh.database.Contact;
import com.cwrh.oh.database.DataSource;
import com.cwrh.oh.fragments.ContactInfoFragment;
import com.cwrh.oh.interfaces.NodesConnectedCallback;
import com.cwrh.oh.interfaces.SyncCallback;
import com.cwrh.oh.tools.ContactInfoHandler;
import com.cwrh.oh.tools.SyncWearable;
import com.cwrh.oh.tools.UpdateWidget;
import com.cwrh.oh.tools.ZoomTransformer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nic on 3/10/2015.
 */
public class ContactSwipeViewActivity extends FragmentActivity {

    private ViewPager viewPager;

    private PagerAdapter pagerAdapter;

    private final int DELAY = 5000;

    private static final int PICK_CONTACT = 1;

    ArrayList<Contact> contactsList;

    private boolean wearableConnected = false;
    private boolean currentlySyncing = false;

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

        MenuItem item =  menu.findItem(R.id.sync);
        if(wearableConnected){
            item.setEnabled(true);
            item.setTitle(R.string.sync_now);
        }else{
            item.setEnabled(false);
            item.setTitle(R.string.not_connected);
        }

        if(currentlySyncing && wearableConnected){
            item.setEnabled(false);
            item.setTitle(R.string.begin_sync);
        }else if(!currentlySyncing && wearableConnected){
            item.setEnabled(true);
            item.setTitle(R.string.sync_now);
        }
        
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

        SyncWearable syncWearable = new SyncWearable(this, new SyncCallback() {
            @Override
            public void onStart() {
                currentlySyncing = true;
                invalidateOptionsMenu();

                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.begin_sync),
                        Toast.LENGTH_LONG).show();

            }

            @Override
            public void onComplete(boolean success) {
                currentlySyncing = false;
                if(success){
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.contacts_synced),
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.error_syncing),
                            Toast.LENGTH_LONG).show();
                }


                invalidateOptionsMenu();
            }
        });

        syncWearable.sync();

    }

    @Override
    protected void onResume(){
        super.onResume();

        checkWearableDevice(nodesConnectedCallback);
        refreshContactsList();
        pagerAdapter = new ContactsSliderAdapter(getSupportFragmentManager());

        //only load one item ahead, the rest are destroyed
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, new ZoomTransformer());
    }

    private final NodesConnectedCallback nodesConnectedCallback = new NodesConnectedCallback() {
        @Override
        public void setStatus(boolean connected) {
            wearableConnected = connected;
            invalidateOptionsMenu();
        }
    };

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
                            getResources().getString(R.string.no_number),
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
                    contact.getName() + " " + getResources().getString(R.string.already_exists),
                    this).show();
        }

        ds.close();

        UpdateWidget udw = new UpdateWidget(this);
        udw.update();
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
     * Uses a NodesConnectedCallback to update the UI accordingly if no wearable devices are connected
     * @param callback
     */
    private void checkWearableDevice(final NodesConnectedCallback callback){

       final GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        new Thread(new Runnable() {

            @Override
            public void run() {
                client.blockingConnect(DELAY, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();

                callback.setStatus(nodes.size() > 0);

                client.disconnect();
            }
        }).start();
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
