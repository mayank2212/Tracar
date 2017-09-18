package com.mayank.tracar.activties;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.mayank.tracar.R;
import com.mayank.tracar.adapters.HistoryAdapter;
import com.mayank.tracar.utils.Constants;
import com.mayank.tracar.utils.NetworkUtility;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    JSONArray todaysTrips;
    RequestQueue queue;
    private SwitchDateTimeDialogFragment dateTimeFragment , dateTimeFragment_To;

    String fromDate, toDate, deviceId , deviceName;
    TextView fromtextView_value , to_textview_value;

    HistoryAdapter historyAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    RecyclerView histoty_recycle;


    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        queue = Volley.newRequestQueue(this);

        DateFormat fD = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'");
        fromDate = String.valueOf(fD.format( new Date() ));

        DateFormat tD = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        toDate = String.valueOf(tD.format( new Date() ));

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            deviceId = extras.getString("deviceId");
            deviceName = extras.getString("deviceName");
        }

        histoty_recycle  = (RecyclerView)findViewById(R.id.histoty_recycle);

        fromtextView_value = (TextView) findViewById(R.id.textViewfromdate);
        final TextView from_textview = (TextView)findViewById(R.id.from_textview);
        from_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDate();
            }
        });

        fromtextView_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDate();
            }
        });

        to_textview_value = (TextView) findViewById(R.id.textView4);
        TextView to_textview = (TextView)findViewById(R.id.textView3);
        to_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToDate();
            }
        });
        to_textview_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToDate();
            }
        });

        ///TOOOOOOO
        dateTimeFragment_To = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeFragment_To == null) {
            dateTimeFragment_To = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)

            );
        }
        // Init format
        final SimpleDateFormat myDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        // Assign unmodifiable values
       /* dateTimeFragment_To.set24HoursMode(true);
        dateTimeFragment_To.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeFragment_To.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());*/

        dateTimeFragment_To.setDefaultHourOfDay(15);
        dateTimeFragment_To.setDefaultMinute(20);
        dateTimeFragment_To.setDefaultDay(4);
        if (Build.VERSION.SDK_INT >= 24) {
            dateTimeFragment_To.setDefaultMonth(Calendar.SEPTEMBER);
        }
        dateTimeFragment_To.setDefaultYear(2017);

        // Define new day and month format
        try {
            dateTimeFragment_To.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MM-dd'T'", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("Asd", e.getMessage());
        }

        // Set listener for date
        // Or use dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
        dateTimeFragment_To.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                to_textview_value.setText(myDateFormat1.format(date));

                if(!fromtextView_value.getText().toString().equalsIgnoreCase("") &&
                        !to_textview_value.getText().toString().equalsIgnoreCase("")){

                    GetUserDevices();
                }
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                // Optional if neutral button does'nt exists
                to_textview_value.setText("");
            }
        });





        //FROMMMMMMMMM
        // Construct SwitchDateTimePicker
        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)

            );
        }

        // Init format
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        // Assign unmodifiable values
        /*dateTimeFragment.set24HoursMode(true);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());*/

        dateTimeFragment.setDefaultHourOfDay(15);
          dateTimeFragment.setDefaultMinute(20);
            dateTimeFragment.setDefaultDay(4);
        if (Build.VERSION.SDK_INT >= 24) {
            dateTimeFragment.setDefaultMonth(Calendar.SEPTEMBER);
        }
        dateTimeFragment.setDefaultYear(2017);

        // Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MM-dd'T'", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("Asd", e.getMessage());
        }

        // Set listener for date
        // Or use dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                fromtextView_value.setText(myDateFormat.format(date));

               if(!fromtextView_value.getText().toString().equalsIgnoreCase("") &&
                       !to_textview_value.getText().toString().equalsIgnoreCase("")){

                   GetUserDevices();
               }


            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                // Optional if neutral button does'nt exists
                fromtextView_value.setText("");
            }
        });

    }


    private void fromDate(){
        dateTimeFragment.startAtCalendarView();
//        dateTimeFragment.setDefaultDateTime(new GregorianCalendar(2017, Calendar.SEPTEMBER, 4, 15, 20).getTime());
        dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);

    }



    private void ToDate(){
        dateTimeFragment_To.startAtCalendarView();
       // dateTimeFragment_To.setDefaultDateTime(new GregorianCalendar(2017, Calendar.SEPTEMBER, 4, 15, 20).getTime());
        dateTimeFragment_To.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);

    }


    private void GetUserDevices(){
        NetworkUtility networkUtility = new NetworkUtility(HistoryActivity.this);

        if( networkUtility.isNetworkAvailable() ) {

            /////////////////////////////////////////////////
//
            Log.e("fromdate" , fromtextView_value.getText().toString().trim());

            Log.e("todate" , to_textview_value.getText().toString().trim());
            String user_trips_url = Constants.hostIP + "reports/trips?from=" + fromtextView_value.getText().toString().trim() + "&to="
                    + to_textview_value.getText().toString().trim() + "&deviceId=" + deviceId;
//            String user_trips_url = Constants.hostIP + "reports/trips?from=" + "2017-07-28T18:30:00Z" + "&to=" + "2017-08-28T18:30:00Z" + "&deviceId=" + "2";

            Log.e("urlll" , user_trips_url.toString());
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET,user_trips_url, null, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    Log.d("DailyTripsActivity", response.toString());

                    todaysTrips = response;

                    mLayoutManager = new LinearLayoutManager(HistoryActivity.this);
                    histoty_recycle.setLayoutManager(mLayoutManager);
                    mAdapter = new HistoryAdapter(HistoryActivity.this, todaysTrips);
                    histoty_recycle.setAdapter(mAdapter);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("TripsActivity", "Error: " + error.getMessage());
                    Log.e("TripsActivity", "Site Info Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                /*** Passing request headers*/
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Cookie", MainActivity.sessionId);
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            queue.add( req );
//
            /////////////////////////////////////////////////////

        }
        else {
            Toast.makeText(HistoryActivity.this, "No Internet", Toast.LENGTH_LONG).show();
        }


    }













    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            startActivity(new Intent(HistoryActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
