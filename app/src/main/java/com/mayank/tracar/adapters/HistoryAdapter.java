package com.mayank.tracar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mayank.tracar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by Mayank on 15-09-2017.
 */
public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int lastPosition = -1;
    Context context;
    JSONArray jsonArray ;

    JSONArray result;
    private static DecimalFormat df2 = new DecimalFormat(".##");
    TextView startLocationTextView, endLocationTextView, startTimeTextView, endTimeTextView;
    TextView tripDurationTextView, avgSpeedTextView, maxSpeedTextView, tripDistanceTextView;


    public HistoryAdapter(Context mcontext , JSONArray array  ){

        result = new JSONArray();
        for (int i = 0; i< array.length(); i++){
            JSONObject obj = null;
            try {
                obj = new JSONObject(String.valueOf(array.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            result.put(obj);
        }

        this.context = mcontext;
        this.jsonArray  = array;
    }



    class MyHeaderViewHolder extends RecyclerView.ViewHolder

    {



        public MyHeaderViewHolder(View view) {
            super(view);



            startLocationTextView=(TextView) view.findViewById(R.id.startLocationTextView);
            endLocationTextView=(TextView) view.findViewById(R.id.endLocationTextView);
            startTimeTextView=(TextView) view.findViewById(R.id.startTimeTextView);
            endTimeTextView=(TextView) view.findViewById(R.id.endTimeTextView);
            tripDurationTextView=(TextView) view.findViewById(R.id.tripDurationTextView);
            avgSpeedTextView=(TextView) view.findViewById(R.id.avgSpeedTextView);
            maxSpeedTextView=(TextView) view.findViewById(R.id.maxSpeedTextView);
            tripDistanceTextView=(TextView) view.findViewById(R.id.tripDistanceTextView);



        }

        public void setHeaderText(String text) {
            //  headerLabel.setText(text);
        }
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View headerRow = LayoutInflater.from(context).inflate(R.layout.history_row, null);
        return new MyHeaderViewHolder(headerRow); // view holder for header items


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {



        try {
            JSONObject device = (JSONObject) result.get(position);

            double avg_speed = Float.parseFloat(device.getString("averageSpeed")) *1.852 ;
            double max_speed = Float.parseFloat(device.getString("maxSpeed"))*1.852;
            double duration = Float.parseFloat( device.getString("duration") ) / 60000;
            double distance = Float.parseFloat( device.getString("distance") ) / 1000;

/*try {
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    Date date = sdf.parse(device.getString("startTime"));
    sdf.setTimeZone(TimeZone.getTimeZone("IST"));
    System.out.println(sdf.format(date));
}catch (Exception e){}*/


            startLocationTextView.setText(String.format("Start Location : %s", device.getString("startAddress")));
            endLocationTextView.setText(String.format("End Location : %s", device.getString("endAddress")));
            startTimeTextView.setText(String.format("Start Time : %s", device.getString("startTime")));
            endTimeTextView.setText(String.format("End Time : %s", device.getString("endTime")));
            tripDurationTextView.setText(String.format("Duration : %s %s", df2.format(duration), " minutes"  ));
            avgSpeedTextView.setText(String.format("Avg Speed : %s %s", df2.format(avg_speed), " km/hr"));
            maxSpeedTextView.setText(String.format("Max Speed : %s %s", df2.format(max_speed), " km/hr" ));
           tripDistanceTextView.setText(String.format("Distance : %s %s", df2.format(distance) , " km"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //setAnimation(holder.itemView, position);

    }

      @Override
    public int getItemCount() {
        return result.length();
    }


    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}



