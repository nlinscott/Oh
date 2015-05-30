package com.cwrh.oh.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cwrh.oh.R;


/**
 * Created by Nic on 5/30/2015.
 */
public class SendButtonsFragment extends Fragment {

    public SendButtonsFragment(){}

    private long DELAY = 5000;

    private View rootView;

    private ButtonsClickedCallback callback;
    private Button here;
    private Button omw;

    public void setCallback(ButtonsClickedCallback c){
        callback = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_send_btns, container, false);

        setButtons();

        return rootView;

    }

    private void setButtons(){

        here = (Button)rootView.findViewById(R.id.frag_btn_here);
        omw = (Button)rootView.findViewById(R.id.frag_btn_omw);

        here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.clickedHere();
                disableButtons();
            }
        });

        omw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.clickedOMW();
                disableButtons();
            }
        });
    }

    /**
     * disables the buttons and shows a loading view for 5 seconds, prevents SMS spamming
     */
    private void disableButtons(){
        here.setEnabled(false);
        omw.setEnabled(false);
        final View disabledView = rootView.findViewById(R.id.disabled_view);
        disabledView.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                here.setEnabled(true);
                omw.setEnabled(true);
                disabledView.setVisibility(View.GONE);
            }
        }, DELAY);

    }

    public interface ButtonsClickedCallback{
        void clickedHere();

        void clickedOMW();
    }

}
