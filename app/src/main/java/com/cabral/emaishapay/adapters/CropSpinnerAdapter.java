package com.cabral.emaishapay.adapters;



import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.SpinnerItem;

import java.util.ArrayList;

public class CropSpinnerAdapter extends ArrayAdapter<SpinnerItem> {
    ArrayList<SpinnerItem> values=new ArrayList<>();
    String fieldLabel;
    Context context;
    public CropSpinnerAdapter(ArrayList<SpinnerItem> items, final String fieldLabel, Context context) {
        super(context,  android.R.layout.simple_spinner_item);
        this.fieldLabel =fieldLabel;
        if(fieldLabel!=null)
            values.add(new SpinnerItem() {
                @Override
                public String getId() {
                    return null;
                }
                public String toString(){
                    return "Select "+fieldLabel;
                }
            });
        values.addAll(items);
        this.context =context;
    }
    public void add(SpinnerItem item){
        values.add(item);
        notifyDataSetChanged();
    }
    public void changeDefaultItem(SpinnerItem defaultItem){
        values.remove(0);
        values.add(0,defaultItem);
        notifyDataSetChanged();
    }
    @Override
    public int getCount(){
        return values.size();
    }

    @Override
    public SpinnerItem getItem(int position){
        return values.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    public void changeItems(ArrayList<SpinnerItem> items){
        values.clear();
        values.add(new SpinnerItem() {
            @Override
            public String getId() {
                return null;
            }
            public String toString(){
                return "Select "+fieldLabel;
            }
            public String getUnits(){
                return null;
            }
        });
        values.addAll(items);
        notifyDataSetChanged();
    }
    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                label.setTextColor(context.getColor(R.color.colorPrimary));

            }
            else {
                label.setTextColor(context.getResources().getColor(R.color.colorPrimary)); //Change selected text color
            }
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);//Change selected text size
        }catch (Exception e){

        }

        // Then you can get the current item using the values array (CropSpinnerItems array) and the current position
        // You can NOW reference each method you has created in your bean object (CropSpinnerItem class)
        label.setText(values.get(position).toString());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(values.get(position).toString());

        return label;
    }
}
