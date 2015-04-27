package oh.cwrh.com.oh.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import oh.cwrh.com.oh.R;
import oh.cwrh.com.oh.database.Contact;
import oh.cwrh.com.oh.datasync.SendMessage;
import oh.cwrh.com.oh.tools.Feedback;

/**
 * Created by Nic on 3/10/2015.
 */
public class ContactInfoFragment extends Feedback {

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

        //setImage();
    }

    private void setTextViews(){
        TextView nameView = (TextView)rootView.findViewById(R.id.frag_name);
        TextView phoneView = (TextView)rootView.findViewById(R.id.frag_phone_number);

        nameView.setText(contact.getName());
        phoneView.setText(contact.getPhone());
    }

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

    /*
    private void setImage(){
        Uri uri = contact.getPhotoUri();
        ImageView photo = (ImageView)rootView.findViewById(R.id.frag_photo);
        if (uri != null) {
            photo.setImageURI(uri);
        } else {
            //TODO: set to default image
        }
    }
    */

    private void sendHere(){
        SendMessage msg = new SendMessage(getActivity().getApplicationContext());
        msg.sendMessage(contact, SendMessage.ACTION_SEND_HERE);
        feelFeedback(getActivity().getApplicationContext());
    }

    private void sendOMW(){
        SendMessage msg = new SendMessage(getActivity().getApplicationContext());
        msg.sendMessage(contact, SendMessage.ACTION_SEND_OMW);
        feelFeedback(getActivity().getApplicationContext());
    }



}
