package com.stephenlightcap.mypackage;

import android.app.Dialog;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.easypost.EasyPost;
//import com.easypost.exception.EasyPostException;
//import com.easypost.model.Tracker;

import com.easypost.EasyPost;
import com.easypost.exception.EasyPostException;
import com.easypost.model.Tracker;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.stephenlightcap.mypackage.adapters.TrackingListAdapter;
import com.stephenlightcap.mypackage.dialog.MapDialogFragment;
import com.stephenlightcap.mypackage.model.TrackingList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ernestoyaquello.com.verticalstepperform.*;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;


public class TrackPackage extends AppCompatActivity {

    private VerticalStepperFormLayout verticalStepperForm;
    private String address;
    private ImageView check;
    private TextView addressView;
    private Spinner carriers;
    private EditText trackingNumber;
    private Button find;
    private RecyclerView rvTrackings;
    @BindView(R.id.fab_menu_tracking)
    com.github.clans.fab.FloatingActionMenu fabMenu;
    @BindView(R.id.fab_tracking_look_path)
    com.github.clans.fab.FloatingActionButton fabTrackingPath;
    @BindView(R.id.fab_tracking_package_details)
    com.github.clans.fab.FloatingActionButton fabTrackingPackageDetails;
    @BindView(R.id.fab_tracking_save_tracking)
    com.github.clans.fab.FloatingActionButton fabSaveTracking;
    private ArrayList<TrackingList> list = new ArrayList<>();
    private ArrayList<TrackingList> listOfCities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_package);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        //TODO bind these with butter
        carriers = (Spinner) findViewById(R.id.spinner_carrier);
        trackingNumber = (EditText) findViewById(R.id.editText_tracking_number_search);
        find = (Button) findViewById(R.id.button_search_tracking_number);
        rvTrackings = (RecyclerView) findViewById(R.id.rv_tracking_list);

        rvTrackings.setVisibility(View.INVISIBLE);

        ArrayAdapter<CharSequence> carrierAdapter = ArrayAdapter.createFromResource(this, R.array.carriers, android.R.layout.simple_spinner_item);
        carrierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carriers.setAdapter(carrierAdapter);

        String urlString = "9361289689090109235381";

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LongOperation().execute(trackingNumber.getText().toString(), carriers.getSelectedItem().toString());
            }
        });

        configureAllListeners();

    }

    private void configureAllListeners() {
        fabTrackingPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createMapDialog();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list", listOfCities);

                MapDialogFragment map = new MapDialogFragment();
                map.setArguments(bundle);
                map.show(getSupportFragmentManager(), null);
            }
        });
    }

    private void createMapDialog() {

    }

    private class LongOperation extends AsyncTask<String, Void, Tracker> {

        String code;

        @Override
        protected Tracker doInBackground(String... params) {
            EasyPost.apiKey = "v58m00iThjy2kcFH4KKvVw";

            Tracker tracker = null;

            // Creating a Tracker
            Map<String, Object> par = new HashMap<String, Object>();
            par.put("tracking_code", params[0]);
            par.put("carrier", params[1]);

            code = params[0].toString();

            try {
                tracker = Tracker.create(par);
            } catch (EasyPostException e) {
                e.printStackTrace();
            }

            //System.out.println(tracker.getTrackingDetails().get(1).getTrackingLocation().getCity());
            return tracker;
        }

        @Override
        protected void onPostExecute(Tracker tracker) {
            list.clear();
            listOfCities.clear();

            if (tracker != null) {
                for (int i = 0; i < tracker.getTrackingDetails().size(); i++) {
                    list.add(new TrackingList(tracker.getTrackingDetails().get(i).getMessage()));
                    listOfCities.add(new TrackingList(tracker.getTrackingDetails().get(i).getTrackingLocation().getCity() + "," +
                            tracker.getTrackingDetails().get(i).getTrackingLocation().getState()));
                }

                fillUpRVTrackings(list);
            } else {
                Toast.makeText(getBaseContext(), "Sorry, we could not find a tracking for that given number and carrier.", Toast.LENGTH_LONG);
            }



        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    /**
     *
     */
    private void fillUpRVTrackings(List<TrackingList> list) {
        rvTrackings.setVisibility(View.VISIBLE);

        TrackingListAdapter trackingAdapter = new TrackingListAdapter(list, this);
        rvTrackings.setAdapter(trackingAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvTrackings.setLayoutManager(llm);


    }

    private void saveNewTrackingToRealm(String code) {

    }

    private void refreshCounter() {
    }

}
