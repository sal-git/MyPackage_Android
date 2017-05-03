package com.stephenlightcap.mypackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.stephenlightcap.mypackage.model.Package;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    RequestQueue queue;
    @BindView(R.id.edittext_login_username)
    EditText editTextUsername;
    @BindView(R.id.edittext_login_password)
    EditText editTextPassword;
    @BindView(R.id.button_sign_in)
    Button btnSignIn;
    @BindView(R.id.imageView_login_box)
    ImageView box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        queue = Volley.newRequestQueue(this);

        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));


        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.wobble);

        box.startAnimation(animShake);

        configureListeners();

    }

    private void configureListeners() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get user info from stack
                getUser();


            }
        });
    }

    /**
     * Get user from REST API
     */
    private void getUser() {
        //Create the body for the POST
        Map<String, String> params = new HashMap<>();
        params.put("username", editTextUsername.getText().toString());
        params.put("password", editTextPassword.getText().toString());

        JSONObject parameters = new JSONObject(params);
        //Create the request
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "https://blooming-springs-83917.herokuapp.com/users/authenticate", parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.d("login", "" + response.getString("user"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        MyApplication app = (MyApplication) getApplicationContext();
                        try {
                            app.setName(response.getString("username"));
                            app.setGlobalObjectFromUser(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("login", "" + response.toString());
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.printStackTrace();
                    }
                });
//        JsonArrayRequest req = new JsonArrayRequest("https://blooming-springs-83917.herokuapp.com/users/authenticate",
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//
//
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//        });


        //Add process to queue to get JSON in background thread
        queue.add(jsObjRequest);
    }
}
