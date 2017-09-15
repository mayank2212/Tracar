package com.mayank.tracar.activties;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mayank.tracar.R;
import com.mayank.tracar.adapters.BottomSheetAdapter;
import com.mayank.tracar.utils.Constants;
import com.mayank.tracar.utils.NetworkUtility;
import com.mayank.tracar.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , BottomSheetAdapter.AdapterCallback,OnMapReadyCallback {

    private GoogleMap mMap;
    JSONObject requiredDeviceData;
    String deviceId;

    TextView current_location , engine_status , current_speed , connected , bottomsheet_heading;

    private BottomSheetAdapter bottom_sheet_adapter ;

    String selected_car_name = "";
    RequestQueue queue;
    SessionManager sessionManager;
    ProgressDialog progressDialog;
    public static String userName;
    public static String sessionId;

    NetworkUtility networkUtility;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    ArrayList<String> device_number ;
    RecyclerView sheet_recycleview;


     ImageButton imagebutton , imagebutton1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        this.bottom_sheet_adapter = new BottomSheetAdapter(MainActivity.this );

        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        toolbar.bringToFront();

        imagebutton = (ImageButton)findViewById(R.id.imageButton);
        imagebutton1 = (ImageButton)findViewById(R.id.imageButton1);

        imagebutton.bringToFront(); imagebutton1.bringToFront();

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
         init_bottom_sheet();
        sheet_recycleview  = (RecyclerView)findViewById(R.id.bottom_recycle);


        current_location = (TextView)findViewById(R.id.current_location);
        engine_status = (TextView)findViewById(R.id.engine_status);
        current_speed = (TextView)findViewById(R.id.current_speed);
        connected = (TextView)findViewById(R.id.connected);
        bottomsheet_heading = (TextView)findViewById(R.id.bottomSheetHeading);

        //setting up option for device id
        sessionManager = new SessionManager(MainActivity.this);
        if(sessionManager.checkLogin()){
            // get user data from session
            HashMap<String, String> user = sessionManager.getUserDetails();
            sessionId = user.get(SessionManager.KEY_SESSION_ID);
            userName = user.get(SessionManager.KEY_USER_NAME);
        }

        queue = Volley.newRequestQueue(this);
        fetchUserDevice();


              //cahgne the deveice id from the bottom sheet here //// TODO: 14-09-2017
    /*    Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            deviceId = extras.getString("deviceId");
        }
*/


        ChangingMenuFont();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void init_bottom_sheet(){
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("Bottom Sheet Behaviour", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("Bottom Sheet Behaviour", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.e("Bottom Sheet Behaviour", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e("Bottom Sheet Behaviour", "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("Bottom Sheet Behaviour", "STATE_SETTLING");
                        break;
                }
            }


            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });
    }

    private void fetchUserDevice() {

        NetworkUtility networkUtility = new NetworkUtility(MainActivity.this);

        if( networkUtility.isNetworkAvailable() ) {

            String user_devices_url = Constants.hostIP + "devices";

            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET,user_devices_url, null, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    Log.d("MainActivity", response.toString());

                    try {
                        ParseJson(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("MainActivity", "Error: " + error.getMessage());
                    Log.e("MainActivity", "Site Info Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                /*** Passing request headers*/
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Cookie", sessionId);
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            queue.add(req);


        }
        else {
            Toast.makeText(MainActivity.this, "No internet...", Toast.LENGTH_LONG).show();
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void ParseJson(JSONArray json) throws JSONException {

        JSONArray jsonarray ;
        device_number = new ArrayList<>();
        ArrayList<String> device_id = new ArrayList<>();
        jsonarray = json;
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject device = (JSONObject) json.get(i);
            String deviceId = device.getString("name");
            device_number.add(deviceId);
            device_id.add(device.getString("id"));
        }

        Log.e("device", device_number.toString());

        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        sheet_recycleview.setLayoutManager(mLayoutManager);
        mAdapter = new BottomSheetAdapter(MainActivity.this, device_number , MainActivity.this , device_id);
        sheet_recycleview.setAdapter(mAdapter);
    }



    //for api above 19
    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

  private void ChangingMenuFont(){
      NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
      navigationView.setNavigationItemSelectedListener(this);
      Menu m = navigationView .getMenu();

      for (int i=0;i<m.size();i++) {
          MenuItem mi = m.getItem(i);

          //for applying a font to subMenu ...
          SubMenu subMenu = mi.getSubMenu();
          if (subMenu!=null && subMenu.size() >0 ) {
              for (int j=0; j <subMenu.size();j++) {
                  MenuItem subMenuItem = subMenu.getItem(j);
                  SpannableString s = new SpannableString(subMenuItem.getTitle());
                  s.setSpan(new TypefaceSpan("fonts/arial.ttf"), 0, s.length(),
                          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                  subMenuItem.setTitle(s);
              }
          }
      }

  }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
             if(id== R.id.history){

                 if(!selected_car_name.equalsIgnoreCase("")) {
                     Intent intent = new Intent();
                     intent.setClass(MainActivity.this, HistoryActivity.class);
                     intent.putExtra("deviceId", deviceId);
                     intent.putExtra("deviceName", selected_car_name);
                     startActivity(intent);
                     overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                 }else{
                     Toast.makeText(MainActivity.this , "Select device first" , Toast.LENGTH_LONG).show();
                     bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                 }
        }else if (id == R.id.logout) {
            new MaterialStyledDialog.Builder(MainActivity.this)
                    .setTitle("Logout")
                    .setDescription("Do you want to logout!!")
                    .setCancelable(false)
                    .setStyle(Style.HEADER_WITH_TITLE)
                    .setHeaderColor(R.color.colorPrimary)
                    .setPositiveText("Logout")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();

                            LogoutMethod();

                        }
                    })
                    .setNegativeText("Cancel")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();


        } else if(id == R.id.contact){

                 Intent i = new Intent(Intent.ACTION_SEND);
                 i.setType("message/rfc822");
                 i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"contact@tracar.in"});
                 i.putExtra(Intent.EXTRA_SUBJECT, "Tracar Support");
                 i.putExtra(Intent.EXTRA_TEXT   , "");
                 try {
                     startActivity(Intent.createChooser(i, "Send mail..."));
                 } catch (android.content.ActivityNotFoundException ex) {
                     Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                 }
             }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void LogoutMethod() {

        Toast.makeText(getApplicationContext(), "Logging Out...", Toast.LENGTH_LONG).show();

        // Fetch User Devices
        NetworkUtility networkUtility = new NetworkUtility(MainActivity.this);

        if (networkUtility.isNetworkAvailable()) {

            String user_logout_url = Constants.hostIP + "session";

            JsonArrayRequest req = new JsonArrayRequest(Request.Method.DELETE, user_logout_url, null, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    Log.d("MainActivity", response.toString());
//
//                        session.logoutUser();
//
//                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
//                        startActivity(i);
//                        finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("MainActivity Logout", "Error: " + error.getMessage());
                    Log.e("MainActivity", "Logout Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                /***
                 * Passing request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Cookie", sessionId);
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            queue.add(req);

            sessionManager.logoutUser();

            Intent i = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(i);
            finish();

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        networkUtility = new NetworkUtility(this);



    }

    private void getUserDeviceLocation(){

        String user_devices_url = Constants.hostIP + "positions";

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET,user_devices_url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d("Live Tracking Activity", response.toString());

                if(progressDialog != null) {
                    progressDialog.dismiss();
                }

                for (int j=0;j<response.length();j++){

                    try {
                        JSONObject jObj = (JSONObject) response.get(j);

                        if ( jObj.getString("deviceId").equals(deviceId) ){
                            requiredDeviceData = jObj;
                            Log.e("asdasdasd" , requiredDeviceData.toString() + "dfvdf");
                            break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                    addLocationToMap();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VehicleTrackingActivity", "Error: " + error.getMessage());

                if(progressDialog != null) {
                    progressDialog.dismiss();
                }

                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            /*** Passing request headers*/
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Cookie", sessionManager.getSessionId());
                headers.put("Accept", "application/json");
                return headers;
            }

        };

        queue.add(req);

        // UI work allowed here
        progressDialog = new ProgressDialog(MainActivity.this);
        // setup dialog here
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Validating...");
        progressDialog.setCancelable(false);
        progressDialog.show();


    }

    private void addLocationToMap() {

        String latitude = null, longitude = null, speed = null, address = null, attributes, ignition = null, charge = null, motion = null, ip, deviceTime = null;

        try {

            latitude = requiredDeviceData.getString("latitude");
            longitude = requiredDeviceData.getString("longitude");
            speed = requiredDeviceData.getString("speed");
            address = requiredDeviceData.getString("address");
            deviceTime = requiredDeviceData.getString("deviceTime");

            attributes = requiredDeviceData.getString("attributes");
            JSONObject attributesObj = new JSONObject(attributes);
            ignition = attributesObj.getString("ignition");
            charge = attributesObj.getString("charge");
            motion = attributesObj.getString("motion");
            ip = attributesObj.getString("ip");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Add a marker in Sydney and move the camera

        assert latitude != null;
        assert longitude != null;
        LatLng last_known_location = new LatLng(Double.parseDouble( latitude ), Double.parseDouble( longitude ) );

        engine_status.setText(String.format( "Ignition : %s", ignition) );
        current_speed.setText(String.format( "Speed : %s", speed) );
        current_location.setText(String.format( "Address : %s", address) );
       // timeTextview.setText(String.format( "Time : %s", deviceTime) );
        connected.setText(String.format( "Charge : %s", charge) );
     //   cordinatesTextview.setText(String.format( "Cordinates : %s, %s", latitude, longitude) );

        Log.i("VehicleTrackingActivity", "Adding Marker to map");

        mMap.addMarker(new MarkerOptions().position(last_known_location).title("Last Known Location"));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(last_known_location));
        moveToCurrentLocation(last_known_location);
    }


    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

    }


    @Override
    public void onMethodCallback(String s) {


    }

    public void AdapterMethod(String s , String name){
        Log.e("ss"  , s +"uij");
       bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        deviceId = s;
        selected_car_name = name;

        bottomsheet_heading.setText(name);
       if( networkUtility.isNetworkAvailable() ) {
           getUserDeviceLocation();
       }
       else {
           Toast.makeText(MainActivity.this, "No internet Connection", Toast.LENGTH_LONG).show();
       }

    }
}
