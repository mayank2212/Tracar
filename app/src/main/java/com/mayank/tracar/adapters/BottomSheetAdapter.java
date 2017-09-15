package com.mayank.tracar.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mayank.tracar.R;
import com.mayank.tracar.activties.MainActivity;

import java.util.ArrayList;

/**
 * Created by Mayank on 14-09-2017.
 */
public class BottomSheetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int lastPosition = -1;
    Context context;
    ArrayList<String> size , device_id;

    private TextView number;
    private CardView cardView;

     MainActivity mactivty;
    private AdapterCallback mAdapterCallback;

    public BottomSheetAdapter(Context context ) {
        try {
            this.mAdapterCallback = (AdapterCallback) context;
            Log.e("interface" ,"intializeddddddddddddddddddd");
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public BottomSheetAdapter(Context mcontext , ArrayList<String> size , MainActivity mact , ArrayList<String> mid){
        this.context = mcontext;
        this.size  = size;
     this.device_id = mid;
      this.mactivty = mact;
    }




    class MyHeaderViewHolder extends RecyclerView.ViewHolder

    {

        private TextView headerLabel;

        public MyHeaderViewHolder(View view) {
            super(view);

            number = (TextView)view.findViewById(R.id.number_textview);
            cardView = view.findViewById(R.id.complete_card_view);
        }

        public void setHeaderText(String text) {
            //  headerLabel.setText(text);
        }
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View headerRow = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_row, null);
        return new MyHeaderViewHolder(headerRow); // view holder for header items


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {



        number.setText(size.get(position));

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //Toast.makeText(context , size.get(position),Toast.LENGTH_LONG).show();
                    mactivty.AdapterMethod(device_id.get(position) , size.get(position));
                } catch (ClassCastException exception) {
                    //
                }
            }
        });
        setAnimation(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return size.size();
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

    public static interface AdapterCallback {
        void onMethodCallback(String s);
    }
}


