package com.wordpress.laaptu.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String WIDGET_TABLE = "widgetContentTable",
			WIDGET_ID = "widgetId", HEADING = "heading", CONTENT = "content",
			IMAGE_REMOTE_URL = "imageUrl", IMAGE_FILE_URL = "fileUrl";
	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "listViewWidgetDB";

	public static final String CREATE_TABLE_QUERY = "create table if not exists "
			+ WIDGET_TABLE
			+ " (_id integer primary key autoincrement,"
			+ WIDGET_ID
			+ " text, "
			+ HEADING
			+ " text, "
			+ CONTENT
			+ " text, "
			+ IMAGE_REMOTE_URL + " text," + IMAGE_FILE_URL + " text);";

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_QUERY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + WIDGET_TABLE);
		onCreate(db);
	}

}
