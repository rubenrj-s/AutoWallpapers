package com.rubenrj.autowallpapers;

import java.util.Comparator;

/**
 * Object for each wallpaper
 */
public class WallpaperRule {
    public int id;
    public String since;
    public boolean[] days;
    public String imagePath;

    public WallpaperRule(){
        this.since = "00:00";
        this.days = new boolean[] {true, true, true, true, true, true, true};
        this.imagePath = "";
        this.id = -1;
    }

    public WallpaperRule(String since, boolean[] days, String imagePath, int id){
        this.since = since;
        this.days = days;
        this.imagePath = imagePath;
        this.id = id;
    }

    public static Comparator<WallpaperRule> orderBySince = new Comparator<WallpaperRule>() {

        public int compare(WallpaperRule wrOne, WallpaperRule wrTwo) {

            String[] sinceOne = wrOne.since.split(":");
            String[] sinceTwo = wrTwo.since.split(":");
            int result = Integer.parseInt(sinceOne[0]) - Integer.parseInt(sinceTwo[0]);
            if (result == 0) {
                result = Integer.parseInt(sinceOne[1]) - Integer.parseInt(sinceTwo[1]);

            }
            return result;
        }};
}
