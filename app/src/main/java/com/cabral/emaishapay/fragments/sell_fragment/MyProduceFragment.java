package com.cabral.emaishapay.fragments.sell_fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.sell.MyProduceListAdapter;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.models.marketplace.MyProduce;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyProduceFragment extends Fragment {
    private static final String TAG = "MyProduceFragment";
    private Context context;

    private ArrayList<MyProduce> produceList = new ArrayList<>();

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    // image picker code
    private static final int IMAGE_PICK_CODE = 0;
    //permission code
    private static final int PERMISSION_CODE = 1;

    Dialog dialog;
    private Uri produceImageUri = null;
    private Bitmap produceImageBitmap = null;
    private String encodedImage;
    private ImageView produceImageView;

    private MyProduce myProduce;

    private DbHandlerSingleton dbHandler;

    private RecyclerView recyclerView;
    private LinearLayout layoutEmptyProduceList;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_produce, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_my_produce_fragment);
        layoutEmptyProduceList = view.findViewById(R.id.produce_view_empty);
        FloatingActionButton addProduce = view.findViewById(R.id.btn_add_my_produce);

        dbHandler = DbHandlerSingleton.getHandlerInstance(context);

        getAllProduce();
        Log.d(TAG, "onCreateView: " + produceList.size());

        if (produceList.size() == 0) {
            layoutEmptyProduceList.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyProduceList.setVisibility(View.GONE);
        }

        addProduce.setOnClickListener(v -> addProduce());

        return view;
    }

    private void getAllProduce() {
        new getAllProduceTask(MyProduceFragment.this).execute();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void addProduce() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        View addProduceDialog = getLayoutInflater().inflate(R.layout.add_produce_dialog, null);

        ImageView close = addProduceDialog.findViewById(R.id.produce_close);
        Spinner name = addProduceDialog.findViewById(R.id.produce_name);
        EditText variety = addProduceDialog.findViewById(R.id.produce_variety);
        Spinner quantityUnit = addProduceDialog.findViewById(R.id.produce_quantity_unit);
        EditText quantity = addProduceDialog.findViewById(R.id.produce_quantity);
        TextView quantityMeasure = addProduceDialog.findViewById(R.id.produce_quantity_measure);
        EditText price = addProduceDialog.findViewById(R.id.produce_price);
        CardView cardView = addProduceDialog.findViewById(R.id.image_view_holder);
        ImageView image = addProduceDialog.findViewById(R.id.produce_image);
        Button submit = addProduceDialog.findViewById(R.id.produce_submit_button);

        close.setOnClickListener(view -> dialog.dismiss());

        quantity.setOnClickListener(view -> quantityMeasure.setText(quantityUnit.getSelectedItem().toString()));

        image.setOnClickListener(v -> {
            produceImageView = image;
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //permission denied
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    //permission granted
                    chooseImage();
                }
            } else {
                //version is less than marshmallow
                chooseImage();
            }
        });

        submit.setOnClickListener(v -> {
            if (name.getSelectedItem().toString().trim().isEmpty() || name.getSelectedItem().toString().equals("Crop")) {
                Toast.makeText(context, "Please choose crop name from the dropdown", Toast.LENGTH_SHORT).show();
            } else if (variety.getText().toString().trim().isEmpty()) {
                variety.setError("Variety Required!!");
            } else if (quantity.getText().toString().trim().isEmpty()) {
                quantity.setError("Quantity Required!!");
            } else if (price.getText().toString().trim().isEmpty()) {
                price.setError("Price Required!!");
            } else if (produceImageUri == null) {
                Toast.makeText(context, "Image Required!!", Toast.LENGTH_SHORT).show();
            } else {
                // fetch data and create produce object
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                String date = simpleDateFormat.format(new Date());
                Log.d(TAG, "onCreateView: Date is " + date);

                myProduce = new MyProduce(name.getSelectedItem().toString(),
                        variety.getText().toString(), quantity.getText().toString(),
                        price.getText().toString(), encodedImage, date
                );

                // create worker thread to insert data into database
                new InsertProduceTask(MyProduceFragment.this, myProduce).execute();
            }

        });

        builder.setView(addProduceDialog);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImage();
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {

            assert data != null;
            if (data.getData() != null) {
                produceImageUri = data.getData();

                try {
                    produceImageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), produceImageUri);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    produceImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] b = byteArrayOutputStream.toByteArray();

                    encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Glide.with(context).load(produceImageUri).into(produceImageView);
        }

    }

    private static class getAllProduceTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<MyProduceFragment> fragmentReference;
        private Context context;

        // only retain a weak reference to the activity
        getAllProduceTask(MyProduceFragment context) {
            fragmentReference = new WeakReference<>(context);
            this.context = context.context;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: " + "Getting produce.");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ArrayList<MyProduce> produce = fragmentReference.get().dbHandler.getAllProduce();
            fragmentReference.get().produceList = produce;

            if (produce.size() == 0) {
                fragmentReference.get().layoutEmptyProduceList.setVisibility(View.VISIBLE);
            } else {
                fragmentReference.get().layoutEmptyProduceList.setVisibility(View.GONE);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                fragmentReference.get().requireActivity().runOnUiThread(() -> {
                    fragmentReference.get().recyclerView.setHasFixedSize(true);
                    fragmentReference.get().layoutManager = new LinearLayoutManager(context);
                    fragmentReference.get().adapter = new MyProduceListAdapter(context, fragmentReference.get().produceList);

                    fragmentReference.get().recyclerView.setLayoutManager(fragmentReference.get().layoutManager);
                    fragmentReference.get().recyclerView.setAdapter(fragmentReference.get().adapter);
                });
                Log.d(TAG, "onPostExecute: Complete");
            }
        }
    }

    private static class InsertProduceTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<MyProduceFragment> fragmentReference;
        private MyProduce myProduce;
        private ProgressDialog dialog;
        private Context context;

        // only retain a weak reference to the activity
        InsertProduceTask(MyProduceFragment context, MyProduce myProduce) {
            fragmentReference = new WeakReference<>(context);
            this.myProduce = myProduce;
            dialog = new ProgressDialog(context.context);
            this.context = context.context;
        }

        @Override
        protected void onPreExecute() {
            dialog.setIndeterminate(true);
            dialog.setMessage("Please Wait..");
            dialog.setCancelable(false);
            fragmentReference.get().requireActivity().runOnUiThread(() -> dialog.show());
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Executing");
            fragmentReference.get().dbHandler.insertProduce(myProduce);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                fragmentReference.get().requireActivity().runOnUiThread(() -> {
                    Toast.makeText(context, "Produce Added", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    fragmentReference.get().dialog.dismiss();
                    fragmentReference.get().getAllProduce();
                });
                Log.d(TAG, "onPostExecute: Complete");
            }
        }
    }
}
