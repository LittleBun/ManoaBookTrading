package com.uhmanoa.booktrade.utils;

import android.app.Activity;

import java.util.ArrayList;

/**
 *
 */
public class ActivityManagerUtils {


    private ArrayList<Activity> activityList = new ArrayList<Activity>();

    private static ActivityManagerUtils activityManagerUtils;

    private ActivityManagerUtils() {

    }

    public static ActivityManagerUtils getInstance() {
        if (activityManagerUtils == null) {
            activityManagerUtils = new ActivityManagerUtils();
        }
        return activityManagerUtils;
    }

    public Activity getTopActivity() {
        return activityList.get(activityList.size() - 1);
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void removeAllActivity() {
        for (Activity activity : activityList) {
            if (activity != null) {
                if (!activity.isFinishing()) {
                    activity.finish();
                }
                activity = null;
            }
        }
        activityList.clear();
    }
}
