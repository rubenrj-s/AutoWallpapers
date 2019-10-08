package com.rubenrj.autowallpapers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class WallpaperAlarmManager {
    ArrayList<WallpaperRule> list;
    Context context;
    static private final int RECURSIVE_RC = -2;

    public WallpaperAlarmManager(ArrayList<WallpaperRule> list, Context context) {
        this.list = filterToday(list);
        this.context = context;
    }

    private ArrayList<WallpaperRule> filterToday(ArrayList<WallpaperRule> list){
        Collections.sort(list, WallpaperRule.orderBySince);
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek == 1){
            dayOfWeek = 6;
        } else {
            dayOfWeek = dayOfWeek - 2;
        }
        ArrayList<WallpaperRule> filtered = new ArrayList<>();
        for (WallpaperRule wr: list) {
            if(wr.days[dayOfWeek]){
                filtered.add(wr);
            }
        }
        return filtered;
    }

    //Start to organize all wallpapers task
    public void startScheduledWallpaper() {
        int s = list.size();
        if(s > 0) {
            for (int i = 0; i < s; i++) {
                addScheduledWallpaper(list.get(i));
            }
        }
        setRecursiveTask();
    }

    public void setRecursiveTask(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, WallpaperReceiver.class);
        intent.putExtra("checkRecursive", true);
        Log.d("WallpaperAlarmManager", calendar.toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RECURSIVE_RC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void stopRecursiveTask(){
        cancelScheduledWallpaper(RECURSIVE_RC);
    }

    // Cancel a particular wallpaper task
    public void cancelScheduledWallpaper(int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, WallpaperReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    // Check if add a new/edit wallpaper task
    public void checkScheduledWallpaper(int requestCode, boolean edit) {
        if(edit){
            cancelScheduledWallpaper(requestCode);
        }

        int wrIndex = -1;
        Calendar c = Calendar.getInstance();
        String currentTime = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        for (int i = 0, s = list.size(); i < s;  i++) {
            // If it was found
            if (wrIndex != -1) {
                // If it's the next then it's greater so if it's greater than currentTime then we must add
                if(UtilsHelper.compareTimes(list.get(i).since, currentTime) > 0){
                    addScheduledWallpaper(list.get(wrIndex));
                }
                i = s;
                // If it wasn't found and id is equals to requestCode
            } else if (list.get(i).id == requestCode) {
                wrIndex = i;
                // If it's the last or new time is more than current time
                if(UtilsHelper.compareTimes(list.get(wrIndex).since, currentTime) > 0 || i + 1 == s){
                    addScheduledWallpaper(list.get(wrIndex));
                    if(edit) checkToChange();
                    //Stop loop
                    i = s;
                }
            }
        }

    }

    private void addScheduledWallpaper(WallpaperRule wr){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, WallpaperReceiver.class);
        intent.putExtra("imagePath", wr.imagePath);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, wr.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar c = Calendar.getInstance();
        String[] time = wr.since.split(":");
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        c.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        c.set(Calendar.SECOND, 0);
        alarmManager.setExact(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
    }

    /**
     * Check if there is a wallpaper which must be setted at the current time
     */
    public void checkToChange(){
        int s = list.size();
        if (s > 0) {
            Calendar c = Calendar.getInstance();
            String currentTime = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
            if(s == 1 && UtilsHelper.compareTimes(currentTime, list.get(0).since) > 0) {
                addScheduledWallpaper(list.get(0));
            } else {
                int iW = -1;
                for (int i = 0; i < s;  i++) {
                    if (UtilsHelper.compareTimes(currentTime, list.get(i).since) > 0) {
                        iW = i;
                    } else {
                        i = s;
                    }
                }
                if (iW != -1) {
                    addScheduledWallpaper(list.get(iW));
                }
            }
        }
    }
}
