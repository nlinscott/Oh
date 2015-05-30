package com.cwrh.oh.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cwrh.oh.R;
import com.cwrh.oh.interfaces.ExitButtonClickCallback;

/**
 * Created by Nic on 5/30/2015.
 */
public class ExitOverlayView extends RelativeLayout {

    private static final String PREFERENCE_NAME = "com.cwrh.oh.ExitOVerlayView.INITIAL_START";
    private static final String INITIAL_START = "initialStart";

    private SharedPreferences preferences;

    private ImageButton dismissButton;
    private TextView infoTextView;
    private View rootView;
    private ExitButtonClickCallback callback;

    private boolean initialStart;

    public ExitOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);

        initialStart = preferences.getBoolean(INITIAL_START, true);

        LayoutInflater inflater = (LayoutInflater.from(context));

        rootView = inflater.inflate(R.layout.exit_overlay, this, true);

        dismissButton = (ImageButton)rootView.findViewById(R.id.exit_btn);
        infoTextView = (TextView)rootView.findViewById(R.id.exit_text);

        this.setVisibility(GONE);
    }

    public void setButtonClickCallback(ExitButtonClickCallback cb){
        callback = cb;
        dismissButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.buttonClicked();
            }
        });
    }

    public void setText(int resId){
        infoTextView.setText(getResources().getString(resId));
    }

    public void setText(String text){
        infoTextView.setText(text);
    }

    public void showOnFirstRun(){
        if(initialStart) {
            if(TextUtils.isEmpty(infoTextView.getText())) {
                initialStart = false;
            } else {
                infoTextView.setVisibility(VISIBLE);
                dismissButton.setVisibility(GONE);
                this.setVisibility(VISIBLE);
                this.postDelayed(new Runnable() {
                    public void run() {
                        ExitOverlayView.this.hide();
                    }
                }, 3000L);
            }
        }
    }

    public void show() {
        if(this.getVisibility() == GONE) {

            this.setAlpha(0.0F);
            this.infoTextView.setVisibility(GONE);
            this.dismissButton.setVisibility(VISIBLE);
            this.setVisibility(VISIBLE);
            this.animate().alpha(1.0F).setDuration(200L).start();

        }else{

            hide();

        }

    }

    private void hide() {
        this.animate().alpha(0.0F).setDuration(200L).withEndAction(new Runnable() {
            public void run() {
                ExitOverlayView.this.setVisibility(GONE);
                ExitOverlayView.this.setAlpha(1.0F);
            }
        }).start();
        if(initialStart) {
            initialStart = false;
            preferences.edit().putBoolean(INITIAL_START, false).apply();
        }

    }

    /**
     * For debugging, this sets the first run boolean to be true so you can test the interface fully
     */
    public void debugMode(){
        preferences.edit().putBoolean(INITIAL_START, true).apply();
    }

}
