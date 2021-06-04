package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.models.shipping_model.TimeSlotsList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Muhammad Nabeel on 15/01/2019.
 */
public class ShippingTimeSlotsAdapter extends RecyclerView.Adapter<ShippingTimeSlotsAdapter.MyViewHolder> {
    
    Context context;
    private final List<TimeSlotsList> timeSlotsLists;
    private onItemClickListener mItemClickListener;
    
    public ShippingTimeSlotsAdapter(Context context, List<TimeSlotsList> timeSlotsLists) {
        this.context = context;
        this.timeSlotsLists = timeSlotsLists;
    }
    
    
    
    //********** Called to Inflate a Layout from XML and then return the Holder *********//
    
    @Override
    public ShippingTimeSlotsAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_inputs_row_item_shipping_time_slots, parent, false);

        return new ShippingTimeSlotsAdapter.MyViewHolder(itemView);
    }



    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final ShippingTimeSlotsAdapter.MyViewHolder holder, final int position) {

        // Get the data model based on Position
        final TimeSlotsList timeSlotsList = timeSlotsLists.get(position);

        Date dt = new Date (Long.parseLong(timeSlotsList.getTimeFrom())*1000L);
        Date dt2 = new Date(Long.parseLong(timeSlotsList.getTimeTo())*1000L);
        SimpleDateFormat sd = new SimpleDateFormat("hh:mm a");

        holder.from_time.setText(sd.format(dt));
        holder.to_time.setText(sd.format(dt2));
        holder.delivery_price.setText(ConstantValues.CURRENCY_SYMBOL+" "+ timeSlotsList.getPrice());

        if(timeSlotsList.isSelected()){
            holder.main_time_slots.setBackgroundResource(R.color.socialColorGoogle);
        }
        else {
            holder.main_time_slots.setBackgroundResource(R.color.bt_white_pressed);
        }

        holder.main_time_slots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickListener(view, holder.getAdapterPosition(), timeSlotsLists.get(holder.getAdapterPosition()));


                }


            }
        });

    }



    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return timeSlotsLists.size();
    }



    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public class MyViewHolder extends RecyclerView.ViewHolder  {

        LinearLayout main_time_slots;

        TextView from_time, to_time, delivery_price;


        public MyViewHolder(final View itemView) {
            super(itemView);
            main_time_slots = itemView.findViewById(R.id.main_time_slots);

            from_time = itemView.findViewById(R.id.from_time);
            to_time = itemView.findViewById(R.id.to_time);
            delivery_price = itemView.findViewById(R.id.delivery_price);

        }


    }

    public void setOnItemClickListener(onItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClickListener(View view, int position, TimeSlotsList myData);
    }
}
