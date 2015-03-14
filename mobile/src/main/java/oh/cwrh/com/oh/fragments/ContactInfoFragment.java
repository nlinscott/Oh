package oh.cwrh.com.oh.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import oh.cwrh.com.oh.R;
import oh.cwrh.com.oh.SendSMS;
import oh.cwrh.com.oh.database.Contact;

/**
 * Created by Nic on 3/10/2015.
 */
public class ContactInfoFragment extends Fragment {

    public ContactInfoFragment(){}

    private View rootView;

    private Contact contact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Debug.log("view created");
        rootView = inflater.inflate(
                R.layout.contact_fragment, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        setTextViews();

        setButtons();

        //Debug.log("loaded " + contact.getName());
        setImage();
    }

    private void setTextViews(){
        TextView nameView = (TextView)rootView.findViewById(R.id.frag_name);
        TextView phoneView = (TextView)rootView.findViewById(R.id.frag_phone_number);

        nameView.setText(contact.getName());
        phoneView.setText(contact.getPhone());
    }

    /**
     * If you want the buttons to actually send SMS, you must uncomment some code here:
     * {@link oh.cwrh.com.oh.SendSMS#sendSMS(String, String, String)}
     */
    private void setButtons(){
        Button here = (Button)rootView.findViewById(R.id.frag_btn_here);
        Button omw = (Button)rootView.findViewById(R.id.frag_btn_omw);

        here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHere();
            }
        });

        omw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOMW();
            }
        });
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
     * with the {@link oh.cwrh.com.oh.SendSMS} intent handler
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
     * Refer to {@link oh.cwrh.com.oh.SendSMS#ACTION_SEND_HERE} and
     * {@link oh.cwrh.com.oh.SendSMS#ACTION_SEND_OMW}
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

}
