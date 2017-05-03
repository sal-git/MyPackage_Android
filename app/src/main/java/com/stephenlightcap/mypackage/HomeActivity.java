package com.stephenlightcap.mypackage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.github.library.bubbleview.BubbleImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stephenlightcap.mypackage.model.Package;
import com.stephenlightcap.mypackage.model.Tracks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Germex on 2/24/2017.
 */

public class HomeActivity extends AppCompatActivity implements View.OnTouchListener, SpringListener {

    @BindView(R.id.imageview_home_server)
    ImageView localServer;
    @BindView(R.id.cardview_scanner)
    CardView scanCard;
    @BindView(R.id.cardview_viewpackages)
    CardView dbCard;
    @BindView(R.id.cardview_gps)
    CardView trackCard;
    @BindView(R.id.bubbleTextView_home_local_packages)
    com.github.library.bubbleview.BubbleTextView localPackagesBubble;
    @BindView(R.id.bubbleTextView_home_cloud_packages)
    com.github.library.bubbleview.BubbleTextView cloudPackagesBubble;
    private Toolbar mActionBarToolbar;
    private Realm realm;
    @BindView(R.id.linear_layout_main_home)
    LinearLayout mainLayout;
    @BindView(R.id.textview_home_user_welcome)
    TextView welcomeTextView;
    Spring spring;
    private static double TENSION = 800;
    private static double DAMPER = 20; //friction
    MyApplication app;

    //
    int packageCount = 0;
    boolean isThereLocalPackages = false;

    //
    int trackingCount = 0;
    boolean isThereTrackings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Initialize Realm
        Realm.init(this);
        //Bind View Initializer
        ButterKnife.bind(this);
        //Get Realm Default Instance
        realm = Realm.getDefaultInstance();
        //Grab window to change background color
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        //Get Toolbar
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(" My Package");

        app = (MyApplication) getApplicationContext();
        //Set up welcome screen
        welcomeTextView.setText("Welcome back, \n" + app.getName() + "!");



        // Create a system to run the physics loop for a set of springs.
        SpringSystem springSystem = SpringSystem.create();
        // Add a spring to the system.
        spring = springSystem.createSpring();
        configureSpringDynamics();
        // Set the spring in motion; moving from 0 to 1
        spring.addListener(this);
        SpringConfig config = new SpringConfig(TENSION, DAMPER);
        spring.setSpringConfig(config);

        trackCard.setOnTouchListener(this);
        scanCard.setOnTouchListener(this);
        dbCard.setOnTouchListener(this);

        checkToSeeIfLocalExist();

        try {
            checkOnlinePackageCount();
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        checkToSeeIfLocalTrackings();

    }

    private void checkOnlinePackageCount() throws JSONException {

        JSONObject object = app.getGlobalObjectFromUser();

        JSONArray venues = object.getJSONArray("packages");

        cloudPackagesBubble.setText("You have " + venues.length() + " in the cloud!");


    }

//    /**
//     * Check to see if the user saved any tracking numbers for future looks
//     */
//    private void checkToSeeIfLocalTrackings() {
//        //Query Realm Database
//        RealmResults<Tracks> list = realm.where(Tracks.class)
//                .findAll();
//
//        if (list.size() > 0) {
//            trackingCount = list.size();
//            trackingBubble.setText("You're tracking " + trackingCount + " packages");
//            trackingBubble.setTextSize(12f);
//            isThereTrackings = true;
//            configureAnimations();
//        } else {
//            trackingBubble.setTextSize(12f);
//            trackingBubble.setText("You're tracking 0 packages");
//            isThereTrackings = false;
//            trackingCount = 0;
//        }
//    }

    /**
     * See if there's any packages saved in Realm
     */
    private void checkToSeeIfLocalExist() {
        //Query Realm Database
        RealmResults<Package> list = realm.where(Package.class)
                .findAll();

        if (list.size() > 0) {
            packageCount = list.size();
            localPackagesBubble.setText(packageCount + " package(s) on this device!");
            localPackagesBubble.setTextSize(12f);
            isThereLocalPackages = true;
            configureAnimations();
        } else {
            localPackagesBubble.setTextSize(12f);
            localPackagesBubble.setText("0 packages saved on this device!");
            isThereLocalPackages = false;
            packageCount = 0;
        }
    }

    /**
     * Animations to move ImageView
     */
    private void configureAnimations() {
        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.wobble);

        if (isThereLocalPackages) {
            localServer.startAnimation(animShake);
        } else {
            localServer.clearAnimation();
        }

//        if (isThereTrackings) {
//            trackingAnimationBox.startAnimation(animShake);
//        } else {
//            trackingAnimationBox.clearAnimation();
//        }

    }

    /**
     * Takes in a View (CardView) and event (Screen Press) and animates it with Spring.
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                spring.setEndValue(1f);
                return true;
            case MotionEvent.ACTION_UP:
                spring.setEndValue(0f);

                //Open a new activity for whatever card is clicked
                if (v == findViewById(R.id.cardview_gps)) {
                    Intent i = new Intent(HomeActivity.this, TrackPackage.class);
                    startActivity(i);
                } else if (v == findViewById(R.id.cardview_viewpackages)) {
                    Intent i = new Intent(HomeActivity.this, SavedPackages.class);
                    startActivity(i);
                } else if (v == findViewById(R.id.cardview_scanner)) {
                    Intent i = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(i);
                }

                return true;
        }

        return false;
    }

    /**
     * Spring dynamics for each card - making them spring up and down when tapped
     */
    private void configureSpringDynamics() {

        // Add a listener to observe the motion of the spring.
        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);
                trackCard.setScaleX(scale);
                trackCard.setScaleY(scale);
                dbCard.setScaleX(scale);
                dbCard.setScaleY(scale);

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
//        checkToSeeIfLocalTrackings();
        checkToSeeIfLocalExist();
    }

    @Override
    public void onSpringUpdate(Spring spring) {

    }

    @Override
    public void onSpringAtRest(Spring spring) {

    }

    @Override
    public void onSpringActivate(Spring spring) {

    }

    @Override
    public void onSpringEndStateChange(Spring spring) {

    }
}
