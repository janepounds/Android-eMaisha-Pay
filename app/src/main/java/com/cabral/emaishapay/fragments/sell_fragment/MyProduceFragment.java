package com.cabral.emaishapay.fragments.sell_fragment;

import androidx.fragment.app.Fragment;

public class MyProduceFragment extends Fragment {
//    private static final String TAG = "MyProduceFragment";
//    private Context context;
//
//    public ArrayList<MyProduce> produceList = new ArrayList<>();
//
//    private RecyclerView.Adapter adapter;
//    private RecyclerView.LayoutManager layoutManager;
//
//    // image picker code
//    private static final int IMAGE_PICK_CODE = 0;
//    //permission code
//    private static final int PERMISSION_CODE = 1;
//
//    private Dialog dialog;
//    private Uri produceImageUri = null;
//    private Bitmap produceImageBitmap = null;
//    private String encodedImage;
//    private ImageView produceImageView;
//
//    private MyProduce myProduce;
//
//    protected final ActivityResultClass<Intent, ActivityResult> activityResult = null;
//    private RecyclerView recyclerView;
//    LinearLayout layoutEmptyProduceList;
//
//    public MyProduceFragment() {
//    }
//
//    @Override
//    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_my_produce, container, false);
//
//        recyclerView = view.findViewById(R.id.recyclerView_my_produce_fragment);
//        layoutEmptyProduceList = view.findViewById(R.id.produce_view_empty);
//        FloatingActionButton addProduce = view.findViewById(R.id.btn_add_my_produce);
//
//        getAllProduce();
//        Log.d(TAG, "onCreateView: " + produceList.size());
//
//        if (produceList.size() == 0) {
//            layoutEmptyProduceList.setVisibility(View.VISIBLE);
//        } else {
//            layoutEmptyProduceList.setVisibility(View.GONE);
//        }
//
//        addProduce.setOnClickListener(v -> addProduce());
//
//        return view;
//    }
//
//    public void getAllProduce() {
//        getAllProduceTask( new WeakReference<>(MyProduceFragment.this));
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        this.context = context;
//        //initialize the onresult class
//         activityResult =  ActivityResultClass.registerForActivityResult();
//
//    }
//
//    private void addProduce() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
//
//        View addProduceDialog = getLayoutInflater().inflate(R.layout.add_produce_dialog, null);
//
//        ImageView close = addProduceDialog.findViewById(R.id.produce_close);
//        Spinner name = addProduceDialog.findViewById(R.id.produce_name);
//        EditText variety = addProduceDialog.findViewById(R.id.produce_variety);
//        Spinner quantityUnit = addProduceDialog.findViewById(R.id.produce_quantity_unit);
//        EditText quantity = addProduceDialog.findViewById(R.id.produce_quantity);
//        TextView quantityMeasure = addProduceDialog.findViewById(R.id.produce_quantity_measure);
//        EditText price = addProduceDialog.findViewById(R.id.produce_price);
//        CardView cardView = addProduceDialog.findViewById(R.id.image_view_holder);
//        ImageView image = addProduceDialog.findViewById(R.id.produce_image);
//        Button submit = addProduceDialog.findViewById(R.id.produce_submit_button);
//
//        close.setOnClickListener(view -> dialog.dismiss());
//
//        quantity.setOnClickListener(view -> quantityMeasure.setText(quantityUnit.getSelectedItem().toString()));
//
//        quantityUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedItem=quantityUnit.getSelectedItem().toString();
//
//                if(position==0){
//                    quantityMeasure.setText("/unit");
//
//                }
//                else if(position==1){
//                    quantityMeasure.setText("/kg");
//                }
//                else if(position==2){
//                    quantityMeasure.setText("/box");
//                }
//                else if(position==3){
//                    quantityMeasure.setText("/ltr");
//                }
//                else if(position==4){
//                    quantityMeasure.setText("/tonne");
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        image.setOnClickListener(v -> {
//            produceImageView = image;
//            //check runtime permission
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                    //permission denied
//                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                    //show popup to request runtime permission
//                    requestPermissions(permissions, PERMISSION_CODE);
//                } else {
//                    //permission granted
//                    chooseImage();
//                }
//            } else {
//                //version is less than marshmallow
//                chooseImage();
//            }
//        });
//
//        submit.setOnClickListener(v -> {
//            if (name.getSelectedItem().toString().trim().isEmpty() || name.getSelectedItem().toString().equals("Crop")) {
//                Toast.makeText(context, "Please choose crop name from the dropdown", Toast.LENGTH_SHORT).show();
//            } else if (variety.getText().toString().trim().isEmpty()) {
//                variety.setError("Variety Required!!");
//            } else if (quantity.getText().toString().trim().isEmpty()) {
//                quantity.setError("Quantity Required!!");
//            } else if (price.getText().toString().trim().isEmpty()) {
//                price.setError("Price Required!!");
//            } else if (produceImageUri == null) {
//                Toast.makeText(context, "Image Required!!", Toast.LENGTH_SHORT).show();
//            } else {
//                // fetch data and create produce object
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
//                String date = simpleDateFormat.format(new Date());
//                Log.d(TAG, "onCreateView: Date is " + date);
//
//                myProduce = new MyProduce(name.getSelectedItem().toString(),
//                        variety.getText().toString(), quantity.getText().toString(),
//                        price.getText().toString(), encodedImage, date,quantityUnit.getSelectedItem().toString()
//                );
//
//                // create worker thread to insert data into database
//                insertProduceTask(new WeakReference<>(MyProduceFragment.this), myProduce);
//            }
//
//        });
//
//        builder.setView(addProduceDialog);
//        builder.setCancelable(false);
//
//        dialog = builder.create();
//        dialog.show();
//    }
//
//    private void chooseImage() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        activityResult.launch(intent, result -> {
//            if (result.getResultCode() == Activity.RESULT_OK) {
//                // There are no request codes
//                Intent data = result.getData();
//                assert data != null;
//                if (data.getData() != null) {
//                    produceImageUri = data.getData();
//
//                    try {
//                        produceImageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), produceImageUri);
//                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                        produceImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//                        byte[] b = byteArrayOutputStream.toByteArray();
//
//                        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                Glide.with(context).load(produceImageUri).into(produceImageView);
//            }
//        });
////        activityResult.launch(intent);
//      //  startActivityForResult(intent, IMAGE_PICK_CODE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                chooseImage();
//            } else {
//                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
//
//            assert data != null;
//            if (data.getData() != null) {
//                produceImageUri = data.getData();
//
//                try {
//                    produceImageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), produceImageUri);
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    produceImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//                    byte[] b = byteArrayOutputStream.toByteArray();
//
//                    encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            Glide.with(context).load(produceImageUri).into(produceImageView);
//        }
//
//    }
//
//    private void  getAllProduceTask(WeakReference<MyProduceFragment>  fragmentReference)  {
//
//            fragmentReference.get().requireActivity().runOnUiThread(() -> {
//                fragmentReference.get().recyclerView.setHasFixedSize(true);
//                fragmentReference.get().layoutManager = new LinearLayoutManager(context);
//                fragmentReference.get().adapter = new MyProduceListAdapter(context, fragmentReference);
//
//                fragmentReference.get().recyclerView.setLayoutManager(fragmentReference.get().layoutManager);
//                fragmentReference.get().recyclerView.setAdapter(fragmentReference.get().adapter);
//
//                if (fragmentReference.get().produceList.size() == 0) {
//                    fragmentReference.get().layoutEmptyProduceList.setVisibility(View.VISIBLE);
//                } else {
//                    fragmentReference.get().layoutEmptyProduceList.setVisibility(View.GONE);
//                }
//            });
//
//    }
//
//    private void insertProduceTask(WeakReference<MyProduceFragment>  fragmentReference, MyProduce myProduce) {
//
//         fragmentReference.get().requireActivity().runOnUiThread(() -> {
//            Toast.makeText(context, "Produce Added", Toast.LENGTH_SHORT).show();
//
//            fragmentReference.get().getAllProduce();
//        });
//
//        Log.d(TAG, "onPostExecute: Complete");
//
//
//    }

}
