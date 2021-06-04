package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.fragments.buy_fragments.CheckoutFinal;
import com.cabral.emaishapay.models.coupons_model.CouponsInfo;

import java.util.List;


/**
 * DemoCouponsListAdapter is the adapter class of ListView holding List of DemoCoupons in CheckoutFinal
 **/

public class DemoCouponsListAdapter extends BaseAdapter {

    Context context;
    private final CheckoutFinal checkoutFinal;
    private final List<CouponsInfo> couponsList;

    private final LayoutInflater layoutInflater;


    public DemoCouponsListAdapter(Context context, List<CouponsInfo> couponsList, CheckoutFinal checkoutFinal) {
        this.context = context;
        this.checkoutFinal = checkoutFinal;
        this.couponsList = couponsList;

        layoutInflater = LayoutInflater.from(context);
    }
    
    
    //********** Returns the total number of items in the data set represented by this Adapter *********//
    
    @Override
    public int getCount() {
        return couponsList.size();
    }
    
    
    //********** Returns the item associated with the specified position in the data set *********//
    
    @Override
    public Object getItem(int position) {
        return couponsList.get(position);
    }
    
    
    //********** Returns the item id associated with the specified position in the list *********//
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    
    
    //********** Returns a View that displays the data at the specified position in the data set *********//
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.buy_inputs__demo_coupon, parent, false);

            holder = new ViewHolder();

            holder.use_coupon = convertView.findViewById(R.id.use_coupon_btn);
            holder.coupon_code = convertView.findViewById(R.id.coupon_code);
            holder.coupon_type = convertView.findViewById(R.id.coupon_type);
            holder.coupon_amount = convertView.findViewById(R.id.coupon_amount);
            holder.coupon_details = convertView.findViewById(R.id.coupon_details);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        holder.coupon_code.setText("Code : "+ couponsList.get(position).getCode());
        holder.coupon_type.setText("Type : "+ couponsList.get(position).getDiscountType());
        holder.coupon_amount.setText("Discount : "+ couponsList.get(position).getAmount());
        holder.coupon_details.setText(couponsList.get(position).getDescription());


        holder.use_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutFinal.setCouponCode(couponsList.get(position).getCode());

            }
        });


        return convertView;
    }
    
    
    
    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/
    
    static class ViewHolder {
        private Button use_coupon;
        private TextView coupon_code, coupon_type, coupon_amount, coupon_details;

    }

}

