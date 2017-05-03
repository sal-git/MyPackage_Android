package com.stephenlightcap.mypackage;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.stephenlightcap.mypackage.adapters.PackageListAdapter;
import com.stephenlightcap.mypackage.model.Package;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class SavedPackages extends AppCompatActivity {

    //List to provide to adapter
    private ArrayList<Package> packageArrayList = new ArrayList<>();
    private ArrayList<Package> localPackageArrayList = new ArrayList<>();
    private Realm realm;
    private RecyclerView rv;
    private TextView mongo;
    private PackageListAdapter adapter;
    private CardView online, local;
    private FloatingActionButton sync;
    private RequestQueue queue;
    private ArrayList<HashMap<String, String>> packagesToSync = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_packages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Saved Packages");
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        sync = (FloatingActionButton) findViewById(R.id.floatingActionButton_savedpackages_sync);
        queue = Volley.newRequestQueue(this);

        mongo = (TextView) findViewById(R.id.textview_test_mongodb);
        online = (CardView) findViewById(R.id.cardview_viewpackages_online);
        local = (CardView) findViewById(R.id.cardview_viewpackages_local);


        rv = (RecyclerView) findViewById(R.id.recyclerview_package_list);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);

        //fillUpRecyclerViewer();


        configureListeners();


    }

    private void configureListeners() {

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packagesToSync.clear();
                syncPackagesToCloud();
            }
        });

        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localPackageArrayList.clear();
                updateLocalPackages();
            }
        });

        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packageArrayList.clear();
                //Send HTTP request to get JSON packages
                sendRequestToGetPackages();


            }
        });
    }

    private ArrayList<HashMap<String, String>> getPackagesToSync() {
        MyApplication app = (MyApplication) getApplicationContext();
        ArrayList<HashMap<String, String>> listOfMaps = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();

        try {
            RealmResults<Package> list = realm.where(Package.class)
                    .findAll();

            for (Package _package : list) {

                map.put("username", app.getName());
                map.put("from", _package.getFromAddress().toString());
                map.put("to", _package.getToAddress().toString());
                map.put("tracking", _package.getTracking().toString());


                listOfMaps.add(map);
            }

            return listOfMaps;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ERROR", "Error in RV " + e.toString());
        }

        return listOfMaps;
    }

    private void syncPackagesToCloud() {



        final String URL = "https://blooming-springs-83917.herokuapp.com/users/update";
        // Post params to be sent to the server
//        HashMap<String, String> params = new HashMap<String, String>();
//        //params.put("primaryKey", "OTHER");
//        params.put("username", app.getName());
//        params.put("from", "FROM");
//        params.put("to", "TO");
//        params.put("tracking", "TRACKING");
//        /params.put("barcode", "111110111");

        packagesToSync = getPackagesToSync();

        for (int i = 0; i < packagesToSync.size(); i++) {
            JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(packagesToSync.get(i)),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Toast.makeText(getApplicationContext(),
                                        "SUCCESS",
                                        Toast.LENGTH_LONG).show();

                                deleteRealmStorage();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //VolleyLog.e("Error: ", error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "ERROR:" + error.toString(),
                            Toast.LENGTH_LONG).show();
                    Log.d("Volly Error:", "Volley error for request -- " + error.toString());
                }
            });

            //Add process to queue to get JSON in background thread
            queue.add(request_json);
        }



    }

    private void deleteRealmStorage() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });

    }

    private void updateLocalPackages() {
        fillUpRecyclerViewer();
        adapter = new PackageListAdapter(localPackageArrayList, this);

        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //localPackageArrayList.clear();
    }

    private void sendRequestToGetPackages() {
        JSONObject obj = new JSONObject();
//        obj.put("id", "1");
//        obj.put("name", "myname");


        JsonArrayRequest req = new JsonArrayRequest("http://192.168.1.65:3000/api/packages",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            // Parsing json array response
                            // loop through each json object
                            StringBuilder jsonResponse = new StringBuilder();
                            for (int i = 0; i < response.length(); i++) {
                                Package newPackage = new Package();
                                JSONObject _package = (JSONObject) response
                                        .get(i);
                                //Get packages elements from online
                                //newPackage.setPrimaryKey(Integer.parseInt(_package.getString("primaryKey")));
                                newPackage.setFromAddress(_package.getString("fromAddress"));
                                newPackage.setTracking(_package.getString("tracking"));
                                newPackage.setToAddress(_package.getString("toAddress"));
                                newPackage.setBarcode(_package.getString("barcode").getBytes());

                                packageArrayList.add(newPackage);
                            }
                            setRecyclerView();
                            //mongo.setText(jsonResponse.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mongo.setText(error.toString());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        //Add process to queue to get JSON in background thread
        queue.add(req);


    }

    private void setRecyclerView() {
        adapter = new PackageListAdapter(packageArrayList, this);

        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //packageArrayList.clear();
    }

    private void fillUpRecyclerViewer() {

        try {
            RealmResults<Package> list = realm.where(Package.class)
                    .findAll();

            for (Package _package : list) {
                localPackageArrayList.add(_package);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ERROR", "Error in RV " + e.toString());
        }

    }

}
