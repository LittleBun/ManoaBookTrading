package com.uhmanoa.booktrade.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.uhmanoa.booktrade.R;


public class SplashActivity extends BaseActivity {

    private String TAG;
    private static final long DELAY_TIME = 2000L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TAG = getClass().getSimpleName();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, DELAY_TIME);
    }
}
