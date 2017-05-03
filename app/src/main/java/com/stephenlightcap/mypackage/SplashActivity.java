package com.stephenlightcap.mypackage;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {


    private TextView title;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_splash);
        //To cover whole screen on launch
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        bindViews();


        //Count down time to finish circle logo
        new CountDownTimer(100, 1000) {

            public void onTick(long millisUntilFinished) {
//                circularFillableLoaders.setProgress(100 - ((int) millisUntilFinished / 100));

                Log.d("seconds remaining: ", "" + millisUntilFinished / 1000);
            }


            public void onFinish() {


//                    //Create the intro and telling it where to go afterwards.
//                TaskStackBuilder.create(getApplicationContext())
//                        .addNextIntentWithParentStack(new Intent(getApplicationContext(), HomeActivity.class))
//                        .addNextIntent(new Intent(getApplicationContext(), IntroActivity.class))
//                        .startActivities();

                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);


            }


        }.start();


    }

    private void bindViews() {
        title = (TextView) findViewById(R.id.textView_splash_title);
        title.setText("MyPackage");
        title.setAllCaps(false);

    }

}
