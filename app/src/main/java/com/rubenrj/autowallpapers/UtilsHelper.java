package com.rubenrj.autowallpapers;

public class UtilsHelper {
    public static Integer compareTimes(String t1, String t2) {
        String[] a1 = t1.split(":");
        String[] a2 = t2.split(":");
        Integer result = Integer.parseInt(a1[0]) - Integer.parseInt(a2[0]);
        if (result == 0) {
            result = Integer.parseInt(a1[1]) - Integer.parseInt(a2[1]);
        }
        return result;
    }
}
