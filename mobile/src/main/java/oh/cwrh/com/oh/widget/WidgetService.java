package oh.cwrh.com.oh.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import oh.cwrh.com.oh.adapters.WidgetAdapter;

/**
 * Created by Nic on 2/7/2015.
 */
public class WidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //Debug.log("building things for widget");
        return (new WidgetAdapter(this.getApplicationContext(), intent));
    }


}