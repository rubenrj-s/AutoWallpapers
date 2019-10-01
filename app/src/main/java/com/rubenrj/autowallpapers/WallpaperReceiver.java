package com.rubenrj.autowallpapers;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class WallpaperReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String imagePath = intent.getStringExtra("imagePath");
        if(imagePath != null){
            Log.i("WallpaperReceiver", imagePath);
            try {
                InputStream is = new FileInputStream(new File(context.getFilesDir(), imagePath));
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                wallpaperManager.setStream(is);
            } catch (FileNotFoundException e) {
                Log.e("WallpaperReceiver", "Error creating a input stream to set wallpaper.");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("WallpaperReceiver", "Error setting the input stream.");
                e.printStackTrace();
            }
        } else if(intent.getBooleanExtra("checkRecursive", false)) {
           new WallpaperAlarmManager(new SaveManager(context).getWallpaperRules(), context).startScheduledWallpaper();
        }

    }
}
