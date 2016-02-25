package com.example.dakota.poopcounter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;



/**
 * Created by Dakota on 2/23/2016.
 */
public class PoopWidgetProvider extends AppWidgetProvider {
    public static String WIDGET_BUTTON = "com.example.dakota.poopcounter.WIDGET_BUTTON";
    int poops;
    @Override
    public void onUpdate(Context context, AppWidgetManager appwidgetManager, int[] appWidgetIds){
        final int count = appWidgetIds.length;
        super.onUpdate(context, appwidgetManager, appWidgetIds);
        SharedPreferences sp = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];


            int number = sp.getInt("poops", 0);

            //poops = number;
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.textView, String.valueOf(number));

            //create an intent to launch MyActivity
            Intent intent = new Intent(WIDGET_BUTTON);
            //intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,appWidgetIds);

            //Get layout for the App Widget and attach an onClick Listener
            //to the button


            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
            appwidgetManager.updateAppWidget(widgetId,remoteViews);



        }

    }
    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);

        if(WIDGET_BUTTON.equals(intent.getAction())){
            incrementPoops(context);
            savePoops(context);
            onUpdate(context);

        }
    }
    private void onUpdate(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(),getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }
    /*
    private PendingIntent getPendingSelfIntent(Context context, String action) {
        // An explicit intent directed at the current class (the "self").
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
    */
    public void incrementPoops(Context context){
        SharedPreferences sp = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        poops = sp.getInt("poops",0);
        poops++;

    }

    public void savePoops(Context context){
        SharedPreferences sp = context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("poops",poops);
        editor.commit();
    }
}
