package com.wordpress.laaptu;

import com.wordpress.laaptu.data.DatabaseManager;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
	/*
	 * So pretty simple just defining the Adapter of the listview here Adapter
	 * is ListProvider and fetching the stored data from Database and providing
	 * it to ListProvider
	 */

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		int appWidgetId = intent.getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		DatabaseManager dbManager = DatabaseManager.INSTANCE;
		dbManager.init(getBaseContext());

		return (new ListProvider(this.getApplicationContext(), intent,
				dbManager.getStoredListItems(appWidgetId)));
	}

}
