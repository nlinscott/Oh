package oh.cwrh.com.oh.adapters;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import oh.cwrh.com.oh.R;
import oh.cwrh.com.oh.SendSMS;
import oh.cwrh.com.oh.database.Contact;
import oh.cwrh.com.oh.database.DataSource;

/**
 * Created by Nic on 2/7/2015.
 */
public class WidgetAdapter implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Contact> list;
    private Context context = null;
    private int appWidgetId;


    public void onCreate() {

        DataSource ds = DataSource.getInstance(context);
        ds.open();
        list = ds.getAllContacts();
        ds.close();

    }


    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public void onDataSetChanged(){
    }

    @Override
    public void onDestroy(){
        ///
    }

    @Override
    public boolean hasStableIds(){
        return false;
    }

    public WidgetAdapter(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.stack_item);


        Contact c = list.get(position);

        //set the textview
        remoteView.setTextViewText( R.id.name,c.getName());
        remoteView.setTextViewText( R.id.number,c.getPhone());

        //pass a bundle of data
        Bundle hereBundle = new Bundle();
        // is leaving is false, so its a HERE action
        hereBundle.putBoolean(SendSMS.IS_LEAVING, false);
        hereBundle.putString(SendSMS.NAME, c.getName());
        hereBundle.putString(SendSMS.PHONE_NUMBER, c.getPhone());

        Intent hereIntent = new Intent();
        hereIntent.putExtras(hereBundle);
        //set each item
        remoteView.setOnClickFillInIntent(R.id.btn_widget_here, hereIntent);

        //pass a bundle of data
        Bundle leavingBundle = new Bundle();
        // is leaving is true, so its a LEAVING action
        leavingBundle.putBoolean(SendSMS.IS_LEAVING, true);
        leavingBundle.putString(SendSMS.NAME, c.getName());
        leavingBundle.putString(SendSMS.PHONE_NUMBER, c.getPhone());


        Intent leavingIntent = new Intent();
        leavingIntent.putExtras(leavingBundle);

        remoteView.setOnClickFillInIntent(R.id.btn_widget_leaving, leavingIntent);

        //will wrap the entire list in OnUpdate
        return remoteView;
    }
}
