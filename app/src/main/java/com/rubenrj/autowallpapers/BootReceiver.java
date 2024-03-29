package com.rubenrj.autowallpapers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
                intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON"))
        {
            new WallpaperAlarmManager(new SaveManager(context).getWallpaperRules(), context).startScheduledWallpaper();
        }
    }
}