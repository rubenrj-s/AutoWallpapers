package com.rubenrj.autowallpapers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpgradeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.MY_PACKAGE_REPLACED")) {
            new WallpaperAlarmManager(new SaveManager(context).getWallpaperRules(), context).startScheduledWallpaper();
        }
    }
}
