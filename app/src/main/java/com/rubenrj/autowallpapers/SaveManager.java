package com.rubenrj.autowallpapers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Manage the archive with wallpapers rules.
 */
public class SaveManager {
    private static String jsonString; //"[{since:'',to:'',days:[false,false,false,false,false,false,false], path:''}]";
    private static ArrayList<WallpaperRule> wrList;
    //TODO: Watch context variable because a static Context could produce memory leaks
    private static Context context;
    private static final Gson gson = new Gson();
    private static final String WR_FILENAME = "wallpapersrules.json";
    private static final Type wrType = new TypeToken<ArrayList<WallpaperRule>>(){}.getType();

    public SaveManager(){}

    public SaveManager(Context context){
        if(this.context == null) {
            this.context = context;
        }
    }

    private void setFileContent(String newContents) {
        try {
            //Write on file
            FileOutputStream os = context.openFileOutput(WR_FILENAME, Context.MODE_PRIVATE);
            os.write(newContents.getBytes());
            os.close();
            //Rewrite jsonString
            jsonString = newContents;
        } catch (Exception e) {
            //TODO: Test errors on "saving method. Force some error to test"
            Log.w("savemanager", "Some error has occurred writing.");
            e.printStackTrace();
        }
    }

    private String getJsonString(){
        if (jsonString == null) {
            try {
                if (!context.getFileStreamPath(WR_FILENAME).exists()) {
                    setFileContent("[]");
                } else {
                    FileInputStream is = context.openFileInput(WR_FILENAME);
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    jsonString = new String(buffer);
                }
            } catch (Exception e) {
                Log.w("savemanager", "Some error has occurred reading wallpaper rules.");
                //If there are data is better return the last data.
                if(jsonString.isEmpty()){
                    jsonString = "[]";
                }
                Toast.makeText(context, context.getString(R.string.savemanager_error_reading), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        return jsonString;
    }

    private void setJsonString() {
        setFileContent(gson.toJson(wrList, wrType));
    }

    public ArrayList<WallpaperRule> getWallpaperRules(){
        if(wrList == null) {
            wrList = gson.fromJson(getJsonString(), wrType);
        }
        return wrList;
    }


    public void addWallpaperRule(WallpaperRule element) {
        boolean doRecursive = false;
        if(wrList.size() == 0) doRecursive = true;
        wrList.add(element);
        WallpaperAlarmManager wam = new WallpaperAlarmManager(wrList, context);
        setJsonString();
        if(doRecursive) wam.setRecursiveTask();
        wam.checkScheduledWallpaper(element.id, false);
    }

    public void removeWallpaperRule(int index) {
        new WallpaperAlarmManager(wrList, context).cancelScheduledWallpaper(wrList.get(index).id);
        wrList.remove(index);
        setJsonString();
    }

    public void setWallpaperRule(int index, WallpaperRule element) {
        wrList.set(index, element);
        setJsonString();
        new WallpaperAlarmManager(wrList, context).checkScheduledWallpaper(element.id, true);
    }
}
