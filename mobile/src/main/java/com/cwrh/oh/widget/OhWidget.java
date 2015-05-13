package com.cwrh.oh.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.cwrh.oh.R;


/**
 * Implementation of App Widget functionality.
 */
public class OhWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        //Debug.log("Updating");
        for (int id : appWidgetIds) {
            RemoteViews remoteViews = updateWidgetStackView(context,id);
            appWidgetManager.updateAppWidget(id,remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private RemoteViews updateWidgetStackView(Context context,
                                             int appWidgetId) {


        Intent intent = new Intent(context, WidgetService.class);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.stack_view_widget);

        rv.setRemoteAdapter(R.id.stack, intent);
        rv.setEmptyView(R.id.stack, R.id.stackWidgetEmptyView);

        Intent smsIntent = new Intent(context, WidgetBroadcastReceiver.class);
        smsIntent.setAction(WidgetBroadcastReceiver.WIDGET_BUTTON_CLICKED);
        smsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, smsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.stack, pi);

        return rv;
    }

}


