package oh.cwrh.com.oh.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import oh.cwrh.com.oh.R;
import oh.cwrh.com.oh.tools.Debug;

/**
 * Created by Nic on 2/7/2015.
 */
public class WidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Debug.log("building things for widget");
        return (new ContactsAdapter(this.getApplicationContext(), intent));
    }


}