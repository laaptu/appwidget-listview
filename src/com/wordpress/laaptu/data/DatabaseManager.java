package com.wordpress.laaptu.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wordpress.laaptu.ListItem;

/**
 * A singleton class of manage all the Database related work
 */
public enum DatabaseManager {
	INSTANCE;

	private SQLiteDatabase db;
	private boolean isDbClosed = true;

	/**
	 * Initializng SQListDatabase with help of our SQLiteOpenHelper class
	 * DatabaseHelper
	 */
	public void init(Context context) {
		if (context != null && isDbClosed) {
			isDbClosed = false;
			DatabaseHelper dbHelper = new DatabaseHelper(context);
			db = dbHelper.getWritableDatabase();
		}

	}

	/**
	 * Closing already opened writable database
	 */
	public void closeDatabase() {
		if (!isDbClosed && db != null) {
			isDbClosed = true;
			db.close();
		}
	}

	/**
	 * Knowing the opened or closed state of writable database
	 */
	public boolean isDbClosed() {
		return isDbClosed;
	}

	/**
	 * Storing the fetched and parsed content of Json file on database
	 */
	public void storeListItems(int widgetId, ArrayList<ListItem> listItems) {
		String widgetIdValue = Integer.toString(widgetId);
		db.delete(DatabaseHelper.WIDGET_TABLE, DatabaseHelper.WIDGET_ID + "=?",
				new String[] { widgetIdValue });
		for (ListItem listItem : listItems) {
			ContentValues cv = new ContentValues();
			cv.put(DatabaseHelper.WIDGET_ID, widgetIdValue);
			cv.put(DatabaseHelper.HEADING, listItem.heading);
			cv.put(DatabaseHelper.CONTENT, listItem.content);
			cv.put(DatabaseHelper.IMAGE_REMOTE_URL, listItem.imageUrl);
			Log.i("listtem image url @ databaseManger", listItem.imageUrl);
			db.insert(DatabaseHelper.WIDGET_TABLE, null, cv);
		}
	}

	/**
	 * 
	 * Fetching the stored items with given widget id
	 */
	public ArrayList<ListItem> getStoredListItems(int widgetId) {
		String widgetIdValue = Integer.toString(widgetId);
		Cursor cursor = db.query(DatabaseHelper.WIDGET_TABLE, null,
				DatabaseHelper.WIDGET_ID + "=?",
				new String[] { widgetIdValue }, null, null, null);
		ArrayList<ListItem> listItems = new ArrayList<ListItem>();
		while (cursor.moveToNext()) {
			ListItem listItem = new ListItem();
			listItem.heading = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.HEADING));
			listItem.content = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.CONTENT));
			listItem.imageUrl = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.IMAGE_REMOTE_URL));
			listItem.fileUrl = cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.IMAGE_FILE_URL));
			listItems.add(listItem);
		}

		cursor.close();
		return listItems.size() == 0 ? null : listItems;
	}

	/**
	 * Once Image have been downloaded and saved on file then update the file
	 * path of saved image to database row
	 * 
	 */
	public boolean updateImageFilePath(int widgetId, String filePath,
			String heading) {
		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.IMAGE_FILE_URL, filePath);
		return db.update(DatabaseHelper.WIDGET_TABLE, cv,
				DatabaseHelper.WIDGET_ID + "=? and " + DatabaseHelper.HEADING
						+ "=?", new String[] { Integer.toString(widgetId),
						heading }) > 0;
	}
}
