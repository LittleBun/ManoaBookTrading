package com.uhmanoa.booktrade;

import android.app.Activity;
import android.app.Application;
import com.uhmanoa.booktrade.entity.Post;
import com.uhmanoa.booktrade.entity.User;
import com.uhmanoa.booktrade.utils.ActivityManagerUtils;
import com.uhmanoa.booktrade.utils.Constant;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class MyApplication extends Application {
    public static final String TAG = "MyApplication";
    public static MyApplication myApplication = null;
    private Post currentPost = null;
    public static MyApplication getMyApplication() {
        return myApplication;
    }

    public User getCurrentUser() {
        User user = BmobUser.getCurrentUser(myApplication, User.class);
        if (user != null) {
            return user;
        }
        return null;
    }

    @Override
    public void onCreate() {
        myApplication = this;
        Bmob.initialize(myApplication, Constant.BMOB_APP_ID);
        //LogUtils.i(TAG, getCurrentUser().getUsername());
        super.onCreate();
    }


    public void addActivity(Activity activity) {
        ActivityManagerUtils.getInstance().addActivity(activity);
    }

    public void exit() {
        ActivityManagerUtils.getInstance().removeAllActivity();
        System.gc();

    }

    public Activity getTopActivity() {
        return ActivityManagerUtils.getInstance().getTopActivity();
    }

    public Post getCurrentPost() {
        return currentPost;
    }

    public void setCurrentPost(Post currentPost) {
        this.currentPost = currentPost;
    }

}
