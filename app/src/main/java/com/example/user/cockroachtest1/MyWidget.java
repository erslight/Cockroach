package com.example.user.cockroachtest1;

/**
 * Created by User on 31.01.2017.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;


public class MyWidget extends AppWidgetProvider {


    private final static String ACTION_CHANGE = "change_count";
    final String UPDATE_ALL_WIDGETS = "update_all_widgets";


    static PendingIntent  pIntent;
    static String sText;
    static Integer count;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget);

        sText = MainActivity.sPref.getString(MainActivity.COUNTER, "");
        remoteViews.setTextViewText(R.id.tvPressCount, sText);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget);
        sText = MainActivity.sPref.getString(MainActivity.COUNTER, "");
        remoteViews.setTextViewText(R.id.tvPressCount, sText);


        // обновляем все экземпляры
        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    void updateWidget(Context ctx, AppWidgetManager appWidgetManager,
                             int widgetID) {

        // Читаем счетчик
        sText = MainActivity.sPref.getString(MainActivity.COUNTER, "");
        count = Integer.valueOf(sText);

        // Помещаем данные в текстовые поля
        RemoteViews widgetView = new RemoteViews(ctx.getPackageName(),
                R.layout.widget);
        widgetView.setTextViewText(R.id.tvPressCount, sText);


        // обработчик нажатия
        Intent countIntent = new Intent(ctx, MyWidget.class);
        countIntent.setAction(ACTION_CHANGE);
        countIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        pIntent = PendingIntent.getBroadcast(ctx, widgetID, countIntent, 0);
        widgetView.setOnClickPendingIntent(R.id.tvPressCount, pIntent);

        // Обновляем виджет
        appWidgetManager.updateAppWidget(widgetID, widgetView);
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        //обновление
        if (intent.getAction().equalsIgnoreCase(UPDATE_ALL_WIDGETS)) {
            ComponentName thisAppWidget = new ComponentName(
                    context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID : ids) {
                updateWidget(context, appWidgetManager, appWidgetID);
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.widget);
                sText = MainActivity.sPref.getString(MainActivity.COUNTER, "");
                remoteViews.setTextViewText(R.id.tvPressCount, sText);
                System.out.println("refresh");
            }
        }

        // Проверяем, что это intent от нажатия на третью зону
        if (intent.getAction().equalsIgnoreCase(ACTION_CHANGE)) {

            // извлекаем ID экземпляра
            int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mAppWidgetId = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);

            }
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                // Читаем значение счетчика, увеличиваем на 1 и записываем

                sText = MainActivity.sPref.getString(MainActivity.COUNTER, "");
                count = Integer.valueOf(sText);
                count++;
                sText = count.toString();
                MainActivity.sPref.edit().putString(MainActivity.COUNTER,
                        sText).apply();

                // Обновляем виджет
                updateWidget(context, AppWidgetManager.getInstance(context),
                        mAppWidgetId);

                Toast.makeText(context,"Killed one more!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}