package com.rubenrj.autowallpapers;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WallpaperRuleAdapter extends ArrayAdapter<WallpaperRule> {
        private Context context;
        private ArrayList<WallpaperRule> wallpaperRules;

        //constructor, call on creation
        public WallpaperRuleAdapter(Context context, int resource, ArrayList<WallpaperRule> objects) {
            super(context, resource, objects);

            this.context = context;
            this.wallpaperRules = objects;
        }

        //called when rendering the list
        public View getView(int position, View convertView, ViewGroup parent) {

            //get the wallpaperrule we are displaying
            WallpaperRule wallpaperRule = wallpaperRules.get(position);

            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.wrlist_item, null);

            //TODO: Get id items from layout
            TextView hours = view.findViewById(R.id.textHours);
            TextView[] days = new TextView[]{
                    view.findViewById(R.id.mondayView),
                    view.findViewById(R.id.tuesdayView),
                    view.findViewById(R.id.wednesdayView),
                    view.findViewById(R.id.thursdayView),
                    view.findViewById(R.id.fridayView),
                    view.findViewById(R.id.saturdayView),
                    view.findViewById(R.id.sundayView)
            };
//            ImageView wifi = (ImageView) view.findViewById(R.id.imgWifi);
//            ImageView localization = (ImageView) view.findViewById(R.id.imgLoc);
            ImageView wallpaper = view.findViewById(R.id.imgWallpaper);

            //Fill hours
            hours.setText(String.format("%1$s/%2$s", wallpaperRule.since, wallpaperRule.to));
            //Fill days
            for (int i = 0; i < 7; i++){
                if(wallpaperRule.days[i]){
                    days[i].setBackgroundColor(context.getResources().getColor(R.color.colorAccent, null));
                }
            }
            //Fill wallpaper
            wallpaper.setImageURI(Uri.fromFile(new File(context.getFilesDir() + "/" + wallpaperRule.imagePath)));
            //TODO: Fill options like Wifi or Location
            return view;
        }
    }
