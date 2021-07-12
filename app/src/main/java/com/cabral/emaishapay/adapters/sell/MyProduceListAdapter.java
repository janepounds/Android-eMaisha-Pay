package com.cabral.emaishapay.adapters.sell;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.fragments.sell_fragment.MyProduceFragment;
import com.cabral.emaishapay.models.marketplace.MyProduce;


import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class MyProduceListAdapter extends RecyclerView.Adapter<MyProduceListAdapter.MyProduceListViewHolder> {
    Context context;
//    private final ArrayList<MyProduce> myProduceArrayList;
    private final WeakReference<MyProduceFragment> fragmentReference;
    FragmentManager fm;
    Dialog dialog;
    public static class MyProduceListViewHolder extends RecyclerView.ViewHolder {
        public TextView name, variety, quantity, price, date;
        public ImageView image;
        public CardView produceCard;


        public MyProduceListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_produce_name);
            variety = itemView.findViewById(R.id.item_produce_variety);
            quantity = itemView.findViewById(R.id.item_produce_quantity);
            price = itemView.findViewById(R.id.item_produce_price);
            date = itemView.findViewById(R.id.item_produce_date);
            image = itemView.findViewById(R.id.item_produce_image);
            produceCard = itemView.findViewById(R.id.produce_card);



        }
    }

    public MyProduceListAdapter(Context context, WeakReference<MyProduceFragment> fragmentReference) {
        this.context = context;
//        this.myProduceArrayList = fragmentReference.get().produceList;
        this.fragmentReference=fragmentReference;
    }

    @NonNull
    @Override
    public MyProduceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_produce_list_item, parent, false);

        return new MyProduceListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProduceListViewHolder holder, int position) {
//        MyProduce myProduce = myProduceArrayList.get(position);

//        holder.name.setText(myProduce.getName());
//        holder.variety.setText(myProduce.getVariety());
//        holder.quantity.setText(myProduce.getQuantity());
//        holder.price.setText(myProduce.getPrice());
//        holder.date.setText(myProduce.getDate());
//
//        Glide.with(context).load(Base64.decode(myProduce.getImage(), Base64.DEFAULT)).into(holder.image);
//
//        holder.produceCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editMyProduce(myProduce);
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private void editMyProduce(MyProduce myProduce) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        //LayoutInflater inflater = requireActivity().getLayoutInflater();
        View addProduceDialog = View.inflate(context,R.layout.add_produce_dialog,null);


        ImageView close = addProduceDialog.findViewById(R.id.produce_close);
        Spinner name = addProduceDialog.findViewById(R.id.produce_name);
        setSelectionValue(myProduce.getName(),name, R.array.crop_add_produce);

        EditText variety = addProduceDialog.findViewById(R.id.produce_variety);
        variety.setText(myProduce.getVariety());
        Spinner quantityUnit = addProduceDialog.findViewById(R.id.produce_quantity_unit);
        setSelectionValue(myProduce.getUnits(),quantityUnit, R.array.crop_quantity_unit);

        EditText quantity = addProduceDialog.findViewById(R.id.produce_quantity);
        quantity.setText(myProduce.getQuantity());
        TextView quantityMeasure = addProduceDialog.findViewById(R.id.produce_quantity_measure);

        EditText price = addProduceDialog.findViewById(R.id.produce_price);
        price.setText(myProduce.getPrice());

        CardView cardView = addProduceDialog.findViewById(R.id.image_view_holder);
        ImageView image = addProduceDialog.findViewById(R.id.produce_image);
        Button submit = addProduceDialog.findViewById(R.id.produce_submit_button);
        LinearLayout layoutSubmitBtn = addProduceDialog.findViewById(R.id.layout_submit_button);
        LinearLayout layoutEditBtns = addProduceDialog.findViewById(R.id.edit_buttons);
        Button deleteBtn = addProduceDialog.findViewById(R.id.delete_produce);
        Button editBtn = addProduceDialog.findViewById(R.id.edit_Produce);

        TextView produceTitle = addProduceDialog.findViewById(R.id.produce_title);


        name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(ContextCompat.getColor(context,R.color.white));
                    //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size
                } catch (Exception e) {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        quantityUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem=quantityUnit.getSelectedItem().toString();
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(ContextCompat.getColor(context,R.color.white));
                    //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size
                } catch (Exception e) {

                }



                if(position==0){
                    quantityMeasure.setText("/unit");

                }
                else if(position==1){
                    quantityMeasure.setText("/kg");
                }
                else if(position==2){
                    quantityMeasure.setText("/box");
                }
                else if(position==3){
                    quantityMeasure.setText("/ltr");
                }
                else if(position==4){
                    quantityMeasure.setText("/tonne");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        produceTitle.setText("Edit Produce");
        layoutSubmitBtn.setVisibility(View.GONE);
        layoutEditBtns.setVisibility(View.VISIBLE);


        builder.setView(addProduceDialog);
        dialog = builder.create();
        builder.setCancelable(false);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getSelectedItem().toString().trim().isEmpty() || name.getSelectedItem().toString().equals("Crop")) {
                    Toast.makeText(context, "Please choose crop name from the dropdown", Toast.LENGTH_SHORT).show();
                } else if (variety.getText().toString().trim().isEmpty()) {
                    variety.setError("Variety Required!!");
                } else if (quantity.getText().toString().trim().isEmpty()) {
                    quantity.setError("Quantity Required!!");
                } else if (price.getText().toString().trim().isEmpty()) {
                    price.setError("Price Required!!");
                } else {
                    // fetch data and create produce object
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String date = simpleDateFormat.format(new Date());
                    Log.d(TAG, "onCreateView: Date is " + date);

                    MyProduce myProduce = new MyProduce(name.getSelectedItem().toString(),
                            variety.getText().toString(), quantity.getText().toString(),
                            price.getText().toString(), null, date, quantityUnit.getSelectedItem().toString()
                    );

                    // create worker thread to insert data into database
                    try {
//                        dbHandler.updateProduce(myProduce,myProduce.getId()+"");
                    }catch (SQLiteException exception){
                        exception.printStackTrace();
                        Toast.makeText(context, ""+exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }finally {
                        dialog.dismiss();
//                        fragmentReference.get().getAllProduce();
                    }
                }
            }
        });
        close.setOnClickListener(view1 -> dialog.dismiss());
       // dialog = builder.create();
        dialog.show();

    }

    private void setSelectionValue(String compareValue,Spinner mSpinner, int array_ressouce_id){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,array_ressouce_id, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            mSpinner.setSelection(spinnerPosition);
        }
    }

}
