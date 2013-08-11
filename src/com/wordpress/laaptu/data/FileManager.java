package com.wordpress.laaptu.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

/**
 * A singleton class to manage file operation
 */
public enum FileManager {
    INSTANCE;
    
	String directoryName = Environment.getExternalStorageDirectory()
			+ "/appwidgetlistview/";

    /**
     * creating a file or initializing a file
     * here I haven't checked sdcard availability
     * so while doing any file operation,better to check
     * sdcard state first
     */
    public void init(String fileName){
    	File file = new File(directoryName+fileName+"/");
    	if(!file.exists())
    		file.mkdirs();
    }
    
    /**
     * Making a jpeg file with downloaded bitmap
     * and giving the database the image path to be updated on
     * its equivalent record
     */
    public void storeBitmap(int appWidgetId,Bitmap bitmap,String heading,Context context){
    	init(Integer.toString(appWidgetId));
    	String fileName = directoryName+Integer.toString(appWidgetId)+"/"+heading+".jpg";
    	try {
			FileOutputStream fileOutputStream =new FileOutputStream(fileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
			fileOutputStream.close();
			fileOutputStream.flush();
			DatabaseManager dbManager = DatabaseManager.INSTANCE;
			dbManager.init(context);
			dbManager.updateImageFilePath(appWidgetId, fileName, heading);
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
    }
}
