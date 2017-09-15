package com.mayank.tracar.activties;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.mayank.tracar.R;
import com.mayank.tracar.utils.Constants;
import com.mayank.tracar.utils.NetworkUtility;
import com.mayank.tracar.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {


    protected EditText mUserName;
    protected EditText mPassword;
    protected Button mButton;

    String userId, userName;

    ProgressDialog progressDialog;

    // Session Manager Class
    static SessionManager session;
    List<Exception> exceptions;
    String globalSessionId;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        try{

          getSupportActionBar().hide();
        }catch (Exception e){
        }
        session = new SessionManager(LoginActivity.this);

        mUserName = (EditText) findViewById(R.id.et_username);
        mPassword = (EditText) findViewById(R.id.checkoutPhoneNumber);
        mButton = (Button) findViewById(R.id.btn_login);

        mButton.setOnClickListener(new View.OnClickListener() {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            @Override
            public void onClick(View v) {

                if ("".equals(mUserName.getText().toString()) || "".equals(mPassword.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Please enter the right login details", Toast.LENGTH_LONG).show();
                } else {
                    NetworkUtility networkUtility = new NetworkUtility(LoginActivity.this);

                    if (networkUtility.isNetworkAvailable()) {

                        CustomStringRequest stringRequest = new CustomStringRequest(Request.Method.POST, Constants.hostIP + "session",
                                new Response.Listener<CustomStringRequest.ResponseM>() {
                                    @Override
                                    public void onResponse(CustomStringRequest.ResponseM result) {
                                        //From here you will get headers
                                        String sessionId = result.headers.get("Set-Cookie");

                                        JSONObject userProfile = null;

                                        String responseString = result.response;
                                        try {
                                            userProfile = new JSONObject(responseString);

                                            userId = userProfile.getString("id");
                                            userName = userProfile.getString("name");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        if (progressDialog != null) {
                                            progressDialog.dismiss();
                                        }

                                        Toast.makeText(LoginActivity.this, "Login Successful...", Toast.LENGTH_LONG).show();
                                        session.createLoginSession(sessionId, userId, userName);

                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(i);
                                        LoginActivity.this.finish();

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        if (progressDialog != null) {
                                            progressDialog.dismiss();
                                        }

                                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }) {

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("email", mUserName.getText().toString().trim());
                                map.put("password", mPassword.getText().toString().trim());
                                return map;
                            }
                        };

                        requestQueue.add(stringRequest);

                        // UI work allowed here
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        // setup dialog here
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("Validating...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Network Unavailable",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });





    }


    public class CustomStringRequest extends Request<CustomStringRequest.ResponseM> {

        private Response.Listener<CustomStringRequest.ResponseM> mListener;

        CustomStringRequest(int method, String url, Response.Listener<CustomStringRequest.ResponseM> responseListener, Response.ErrorListener listener) {
            super(method, url, listener);
            this.mListener = responseListener;
        }

        @Override
        protected void deliverResponse(ResponseM response) {
            this.mListener.onResponse(response);
        }

        @Override
        protected Response<ResponseM> parseNetworkResponse(NetworkResponse response) {
            String parsed;
            try {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data);
            }

            ResponseM responseM = new ResponseM();
            responseM.headers = response.headers;
            responseM.response = parsed;

            return Response.success(responseM, HttpHeaderParser.parseCacheHeaders(response));
        }

        public class ResponseM {
            Map<String, String> headers;
            String response;
        }

    }

}
