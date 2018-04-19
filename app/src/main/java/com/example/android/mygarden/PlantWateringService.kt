package com.example.android.mygarden

import android.app.IntentService
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import com.example.android.mygarden.provider.PlantContract
import com.example.android.mygarden.provider.PlantContract.PATH_PLANTS
import com.example.android.mygarden.utils.PlantUtils


const val ACTION_WATER_PLANTS = "com.example.android.mygarden.action.water_plants"
const val ACTION_UPDATE_PLANT_WIDGETS = "com.example.android.mygarden.update_plant_widgets"

class PlantWateringService: IntentService("PlantWaterService"){

    override fun onHandleIntent(intent: Intent?) {
        if(intent==null) return

        when(intent.action){
            ACTION_WATER_PLANTS         ->   handleActionWaterPlants()
            ACTION_UPDATE_PLANT_WIDGETS ->   handleActionupdatePlantWidgets()
        }

    }

    private fun handleActionupdatePlantWidgets() {
        val PLANTS_URI = PlantContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PLANTS).build()

        val cursor = contentResolver.query(
                PLANTS_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME
        )

        var imageResource = R.drawable.grass
        if(cursor.count>0) {
            with(cursor) {
                moveToFirst()
                val timeNow = System.currentTimeMillis()
                val wateredAt = getLong(getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME))
                val createdAt = getLong(getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME))
                val plantType = getInt(getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE))

                close()
                imageResource = PlantUtils.getPlantImageRes(this@PlantWateringService,
                        timeNow - createdAt,
                        timeNow - wateredAt,
                        plantType)
            }
        }

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetIDs = appWidgetManager.getAppWidgetIds(ComponentName(this, PlantWidgetProvider::class.java))
        PlantWidgetProvider.updatePlantWidgets(this, appWidgetManager, imageResource, appWidgetIDs)
    }




    private fun handleActionWaterPlants() {
        val PLANTS_URI = PlantContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PLANTS).build()

        val timeRightNow = System.currentTimeMillis()
        val plantContentValues = ContentValues()
        plantContentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeRightNow)

        contentResolver.update(PLANTS_URI, plantContentValues, PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME + ">?",
                arrayOf("${timeRightNow - PlantUtils.MAX_AGE_WITHOUT_WATER}"))
    }


    companion object {
        fun startActionWaterPlants(context: Context){
            val requestServiceIntent = Intent(context, PlantWateringService::class.java)
            requestServiceIntent.setAction(ACTION_WATER_PLANTS)
            context.startService(requestServiceIntent)
        }
    }
}