package com.cwrh.oh.tools;

import android.content.Context;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by Nic on 4/27/2015.
 *
 * Extends fragment and adds new watch based feedback functionality to each fragment per action
 */
public class Feedback extends Fragment {

    protected void showFeedback(Context c, String msg){
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
        feelFeedback(c);
    }

    protected void feelFeedback(Context c){
        Vibrator v = (Vibrator)c.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(150);
    }
}
