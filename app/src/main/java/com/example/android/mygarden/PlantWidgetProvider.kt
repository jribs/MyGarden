package com.example.android.mygarden

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.android.mygarden.ui.MainActivity

/**
 * Implementation of App Widget functionality.
 */
class PlantWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
            PlantWateringService.startActionWaterPlants(context)

    }



    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }



    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.plant_widget)

            val startMyGardenIntent = Intent(context, MainActivity::class.java)
            views.setOnClickPendingIntent(R.id.widget_plant_image,
                    PendingIntent.getActivity(context,0,startMyGardenIntent,0))

            var waterPlantsIntent = Intent(context, PlantWateringService::class.java)
            waterPlantsIntent.action = ACTION_WATER_PLANTS

            views.setOnClickPendingIntent(R.id.widget_water_drop, PendingIntent.getActivity(
                    context, 0, waterPlantsIntent, PendingIntent.FLAG_UPDATE_CURRENT
            ))



            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updatePlantWidgets(context: Context, appWidgetManager: AppWidgetManager,
                               imageResource: Int, appWidgetIds: IntArray){

            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }

        }
    }
}

