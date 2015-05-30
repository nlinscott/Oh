package com.cwrh.oh.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cwrh.oh.R;
import com.cwrh.oh.database.Contact;
import com.cwrh.oh.services.SendSMS;

/**
 * Created by Nic on 3/10/2015.
 */
public class ContactInfoFragment extends Fragment implements SendButtonsFragment.ButtonsClickedCallback{

    public ContactInfoFragment(){}

    private View rootView;

    private Contact contact;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        //Debug.log("view created");
        rootView = inflater.inflate(
                R.layout.contact_fragment, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        setTextViews();

        //Debug.log("loaded " + contact.getName());
        setImage();

        setBtnFragment();
    }


    private void setTextViews(){
        TextView nameView = (TextView)rootView.findViewById(R.id.frag_name);
        TextView phoneView = (TextView)rootView.findViewById(R.id.frag_phone_number);

        nameView.setText(contact.getName());
        phoneView.setText(contact.getPhone());
    }

    /**
     * If you want the buttons to actually send SMS, you must uncomment some code here:
     * {@link SendSMS#sendSMS(String, String, String)}
     */
    private void setBtnFragment(){

        FragmentManager fm = getChildFragmentManager();
        SendButtonsFragment fragment =  (SendButtonsFragment)fm.findFragmentById(R.id.btns_fragment);
        fragment.setCallback(this);

    }

    public void setContact(Contact c){
        contact = c;
    }

    private void setImage(){
        Uri uri = contact.getPhotoUri();
        ImageView photo = (ImageView)rootView.findViewById(R.id.frag_photo);
        if (uri != null) {
            photo.setImageURI(uri);
        } else {
            //TODO: set to default image
        }
    }

    /**
     * Builds a generic intent from the {@link #contact} object
     * @return an intent with the string extras needed to work
     * with the {@link SendSMS} intent handler
     */
    private Intent buildIntent(){
        Intent intent = new Intent(getActivity().getApplicationContext(), SendSMS.class);
        intent.putExtra(SendSMS.NAME, contact.getName());
        intent.putExtra(SendSMS.PHONE_NUMBER, contact.getPhone());
        //TODO: send photo URI to appear in notification?
        return intent;
    }

    /**
     * Adds a specific action to the intent and starts the service
     * Refer to {@link SendSMS#ACTION_SEND_HERE} and
     * {@link SendSMS#ACTION_SEND_OMW}
     */
    private void sendHere(){
        Intent intent = buildIntent();
        intent.setAction(SendSMS.ACTION_SEND_HERE);
        getActivity().startService(intent);
    }

    /**
     * See {@link #sendHere()}
     */
    private void sendOMW(){
        Intent intent = buildIntent();
        intent.setAction(SendSMS.ACTION_SEND_OMW);
        getActivity().startService(intent);
    }


    /**
     * Callback methods to know when and which button was clicked
     */
    @Override
    public void clickedHere() {
        sendHere();
    }

    @Override
    public void clickedOMW() {
        sendOMW();
    }
}
