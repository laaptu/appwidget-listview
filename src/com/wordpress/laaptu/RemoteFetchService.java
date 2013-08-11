package com.wordpress.laaptu;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.wordpress.laaptu.data.DatabaseManager;
import com.wordpress.laaptu.data.FileManager;

public class RemoteFetchService extends Service {

	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private AQuery aquery;
	private String remoteJsonUrl = "http://laaptu.files.wordpress.com/2013/07/widgetlist.key";

	public static ArrayList<ListItem> listItemList;
	private int count = 0;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/*
	 * Retrieve appwidget id from intent it is needed to update widget later
	 * initialize our AQuery class
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
			appWidgetId = intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		aquery = new AQuery(getBaseContext());
		fetchDataFromWeb();
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * method which fetches data(json) from web aquery takes params
	 * remoteJsonUrl = from where data to be fetched String.class = return
	 * format of data once fetched i.e. in which format the fetched data be
	 * returned AjaxCallback = class to notify with data once it is fetched
	 */
	private void fetchDataFromWeb() {
		aquery.ajax(remoteJsonUrl, String.class, new AjaxCallback<String>() {
			@Override
			public void callback(String url, String result, AjaxStatus status) {
				processResult(result);
				super.callback(url, result, status);
			}
		});
	}

	/**
	 * Json parsing of result and populating ArrayList<ListItem> as per json
	 * data retrieved from the string
	 */
	private void processResult(String result) {
		Log.i("Resutl", result);
		listItemList = new ArrayList<ListItem>();
		try {
			JSONArray jsonArray = new JSONArray(result);
			int length = jsonArray.length();
			for (int i = 0; i < length; i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ListItem listItem = new ListItem();
				listItem.heading = jsonObject.getString("heading");
				listItem.content = jsonObject.getString("content");
				listItem.imageUrl = jsonObject.getString("imageUrl");
				listItemList.add(listItem);
			}
			storeListItem();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Instead of using static ArrayList as we have used before,no we rely upon
	 * data stored on database so saving the fetched json file content into
	 * database and at same time downloading the image from web as well
	 */
	private void storeListItem() {
		DatabaseManager dbManager = DatabaseManager.INSTANCE;
		dbManager.init(getBaseContext());
		dbManager.storeListItems(appWidgetId, listItemList);

		int length = listItemList.size();
		for (int i = 0; i < length; i++) {
			ListItem listItem = listItemList.get(i);
			final int index = i;
			aquery.ajax(listItem.imageUrl, Bitmap.class,
					new AjaxCallback<Bitmap>() {
						@Override
						public void callback(String url, Bitmap bitmap,
								AjaxStatus status) {
							super.callback(url, bitmap, status);
							storeBitmap(index, bitmap);
						};
					});
		}
	}

	/**
	 * Saving the downloaded images into file and after all the download of
	 * images be complete begin to populate widget as done previously
	 */
	private void storeBitmap(int index, Bitmap bitmap) {
		FileManager.INSTANCE.storeBitmap(appWidgetId, bitmap,
				listItemList.get(index).heading, getBaseContext());
		count++;
		Log.i("count",
				String.valueOf(count) + "::"
						+ Integer.toString(listItemList.size()));
		if (count == listItemList.size()) {
			count = 0;
			populateWidget();
		}

	}

	/**
	 * Method which sends broadcast to WidgetProvider so that widget is notified
	 * to do necessary action and here action == WidgetProvider.DATA_FETCHED
	 */
	private void populateWidget() {

		Intent widgetUpdateIntent = new Intent();
		widgetUpdateIntent.setAction(WidgetProvider.DATA_FETCHED);
		widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				appWidgetId);
		sendBroadcast(widgetUpdateIntent);

		this.stopSelf();
	}
}
