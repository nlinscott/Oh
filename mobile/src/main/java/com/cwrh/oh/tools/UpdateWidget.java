package com.cwrh.oh.tools;

import android.content.Context;
import android.content.Intent;

import com.cwrh.oh.adapters.WidgetAdapter;
import com.cwrh.oh.widget.OhWidget;

/**
 * Created by Nic on 5/27/2015.
 */
public class UpdateWidget {
    private Context context;
    public UpdateWidget(Context c){
        context = c;
    }

    /**
     * calls {@link WidgetAdapter#onDataSetChanged()} and rebuilds the entire widget view
     */
    public void update(){
        //Debug.log("Updating from Tools.UpdateWidget");

        //updates in a new thread
        new StartUpdate().start();
    }

    private class StartUpdate extends Thread{

        public StartUpdate(){

        }

        public void run(){

            Intent intent = new Intent(context, OhWidget.class);

            intent.setAction(OhWidget.UPDATE_WIDGET);

            context.sendBroadcast(intent);

            /*
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), OhWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack);
            */
        }
    }

}
