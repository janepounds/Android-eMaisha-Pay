package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;

import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.adapters.Shop.PosProductAdapter;

import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.databinding.FragmentShopPosBinding;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.modelviews.ShopProductsModelView;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.utils.Resource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ShopPOSFragment extends Fragment implements View.OnClickListener {

    private Context context;
    PosProductAdapter productAdapter;
    TextView txtNoProducts;
    public  TextView totalItems,totalprice;
    ImageView imgNoProduct;
    private RecyclerView recyclerView;
    public double chargeAmount;
    private WeakReference<ShopPOSFragment> fragmentReference;
    private ShopProductsModelView viewModel;
    FragmentShopPosBinding binding;
    private int items=0;

    User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
    List<CartProduct> cartItemsList = new ArrayList<>();
    private SparseArray<String> keyValues = new SparseArray<>();
    private static InputConnection inputConnection;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_shop_pos,container,false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbarShopPos);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.all_product);

        viewModel = new ViewModelProvider(requireActivity()).get(ShopProductsModelView.class);
        fragmentReference = new WeakReference<>(ShopPOSFragment.this);

        totalItems = binding.totalItems;
        totalprice = binding.tvTotalPrice;

        binding.layoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] splited = binding.totalItems.getText().toString().split("\\s+");
                if(Integer.parseInt(splited[0]) > 0){
                    //naviagete to payments

                    Bundle args=new Bundle();


                    args.putDouble("Charge", chargeAmount );

                    ShopActivity.navController.navigate(R.id.action_shopPOSFragment_to_shopPayments,args);

                }else{
                    Toast.makeText(context,"First add Items to cart",Toast.LENGTH_LONG).show();
                }



            }
        });

        binding.imgCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( TextUtils.isEmpty(binding.posCharge.getText()) ){
                    binding.posCharge.setError("Charge amount required");
                    return;
                }else if( binding.posCharge.getText().length()<2 &&  Integer.parseInt( binding.posCharge.getText().toString() )<0 ){
                    binding.posCharge.setError("Invalid Charge amount");
                    return;
                }

                chargeAmount=Double.parseDouble(binding.posCharge.getText().toString());
                Bundle args=new Bundle();
                args.putDouble("Charge", chargeAmount );
                ShopActivity.navController.navigate(R.id.action_shopPOSFragment_to_shopPayments,args);

            }
        });

//        binding.imageNoProduct.setVisibility(View.GONE);
//        binding.txtNoProducts.setVisibility(View.GONE);

//        refreshCartProducts();
        setKeyValues();
        setKeyListeningEvents();

        return binding.getRoot();
    }


    private void setKeyValues() {
        keyValues.put(R.id.tv_key_0, "0");
        keyValues.put(R.id.tv_key_1, "1");
        keyValues.put(R.id.tv_key_2, "2");
        keyValues.put(R.id.tv_key_3, "3");
        keyValues.put(R.id.tv_key_4, "4");
        keyValues.put(R.id.tv_key_5, "5");
        keyValues.put(R.id.tv_key_6, "6");
        keyValues.put(R.id.tv_key_7, "7");
        keyValues.put(R.id.tv_key_8, "8");
        keyValues.put(R.id.tv_key_9, "9");
    }

    private void setKeyListeningEvents() {
        binding.tvKey0.setOnClickListener(this);
        binding.tvKey1.setOnClickListener(this);
        binding.tvKey2.setOnClickListener(this);
        binding.tvKey3.setOnClickListener(this);
        binding.tvKey4.setOnClickListener(this);
        binding.tvKey5.setOnClickListener(this);
        binding.tvKey6.setOnClickListener(this);
        binding.tvKey7.setOnClickListener(this);
        binding.tvKey8.setOnClickListener(this);
        binding.tvKey9.setOnClickListener(this);
        binding.tvKeyBackspace.setOnClickListener(this);
        binding.tvKeyEnter.setOnClickListener(this);
        binding.posCharge.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        setInputConnection(binding.posCharge);
        binding.posCharge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    public void setInputConnection(TextView editText) {
        InputConnection ic = editText.onCreateInputConnection(new EditorInfo());
        inputConnection = ic;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.txtEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.enterSelected.setVisibility(View.VISIBLE);
                binding.itemsSelected.setVisibility(View.INVISIBLE);
                binding.etxtSearch.setVisibility(View.GONE);
                binding.posRecycler.setVisibility(View.GONE);
                binding.layoutCart.setVisibility(View.GONE);
                binding.layoutPosCharge.setVisibility(View.VISIBLE);
                binding.posCharge.requestFocus();
                binding.layoutKeyboard.setVisibility(View.VISIBLE);
                binding.scrollviewLayout.setVisibility(View.GONE);
            }
        });


        if(binding.enterSelected.getVisibility()==View.VISIBLE){
            binding.enterSelected.setVisibility(View.VISIBLE);
            binding.itemsSelected.setVisibility(View.INVISIBLE);
            binding.etxtSearch.setVisibility(View.GONE);
            binding.posRecycler.setVisibility(View.GONE);
            binding.layoutCart.setVisibility(View.GONE);
            binding.layoutPosCharge.setVisibility(View.VISIBLE);
            binding.posCharge.requestFocus();
            binding.layoutKeyboard.setVisibility(View.VISIBLE);
            binding.scrollviewLayout.setVisibility(View.GONE);
        }
        else {
            binding.enterSelected.setVisibility(View.INVISIBLE);
            binding.itemsSelected.setVisibility(View.VISIBLE);
            binding.etxtSearch.setVisibility(View.VISIBLE);
            binding.posRecycler.setVisibility(View.VISIBLE);
            binding.layoutCart.setVisibility(View.VISIBLE);
            binding.layoutPosCharge.setVisibility(View.GONE);
            binding.posCharge.clearFocus();
            binding.layoutKeyboard.setVisibility(View.GONE);
            binding.scrollviewLayout.setVisibility(View.VISIBLE);
        }


        binding.txtItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productAdapter = new PosProductAdapter(context,viewModel,getViewLifecycleOwner(),fragmentReference);
                // set a LinearLayoutManager with default vertical orientation
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                binding.posRecycler.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
                binding.posRecycler.setHasFixedSize(true);
                binding.posRecycler.setAdapter(productAdapter);
                subscribeToProducts(viewModel.getMerchantProducts());

                binding.enterSelected.setVisibility(View.INVISIBLE);
                binding.itemsSelected.setVisibility(View.VISIBLE);
                binding.etxtSearch.setVisibility(View.VISIBLE);
                binding.posRecycler.setVisibility(View.VISIBLE);
                binding.layoutCart.setVisibility(View.VISIBLE);
                binding.layoutPosCharge.setVisibility(View.GONE);
                binding.posCharge.clearFocus();
                binding.layoutKeyboard.setVisibility(View.GONE);
                binding.scrollviewLayout.setVisibility(View.VISIBLE);

                binding.etxtSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        viewModel.setQuery(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                });

            }
        });
    }

    @Override
    public void onResume() {
        ClearCart();
        super.onResume();
    }

    public void refreshCartProducts() {

        int itemsCounter=0; double priceCounter=0;
        cartItemsList = user_cart_BuyInputs_db.getCartItems();

        for (int i = 0; i < cartItemsList.size(); i++) {
            ProductDetails product=cartItemsList.get(i).getCustomersBasketProduct();
            itemsCounter=itemsCounter+product.getCustomersBasketQuantity();
            priceCounter=priceCounter+(product.getCustomersBasketQuantity() * Double.parseDouble(product.getProductsPrice() ));
            Log.e("CartProduct",product.getProductsName()+" "+product.getCustomersBasketQuantity()+" "+Double.parseDouble(product.getProductsPrice()));
        }
        String currency =context.getString(R.string.currency);
        items = itemsCounter;

        binding.totalItems.setText(itemsCounter+" Items");
        binding.tvTotalPrice.setText(currency+" "+priceCounter);
    }

    public static void ClearCart() {
        User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        user_cart_BuyInputs_db.clearCart();
    }

    private void subscribeToProducts(LiveData<Resource<List<EcProduct>>> products) {
        products.observe(getViewLifecycleOwner(), myProducts->{
            //dialogLoader.showProgressDialog();
            Log.d("debug","------->>>>");
            if(myProducts.data!=null && myProducts.data.size()>0){

                binding.posRecycler.setVisibility(View.VISIBLE);
                binding.imageNoProduct.setVisibility(View.GONE);
                binding.txtNoProducts.setVisibility(View.GONE);
                productAdapter.setProductList( myProducts.data);

            }else {
                binding.posRecycler.setVisibility(View.GONE);
                binding.imageNoProduct.setVisibility(View.VISIBLE);
                binding.imageNoProduct.setImageResource(R.drawable.not_found);
                binding.txtNoProducts.setVisibility(View.VISIBLE);
            }
        });

    }


    private void subscribeToSearchedProducts(LiveData<Resource<List<EcProduct>>> products) {
        products.observe(getViewLifecycleOwner(), searchedProducts -> {
            //dialogLoader.showProgressDialog();
            Log.d("debug", "------->>>>");
            if (searchedProducts.data != null && searchedProducts.data.size() <= 0) {
                recyclerView.setVisibility(View.GONE);
                imgNoProduct.setVisibility(View.VISIBLE);
                imgNoProduct.setImageResource(R.drawable.not_found);
                txtNoProducts.setVisibility(View.VISIBLE);


            } else {
                recyclerView.setVisibility(View.VISIBLE);
                imgNoProduct.setVisibility(View.GONE);
                txtNoProducts.setVisibility(View.GONE);
                productAdapter.setProductList( searchedProducts.data);


            }
        });
    }


    @Override
    public void onClick(View v) {
        if (inputConnection == null)
            return;

        if(  v.getId()==R.id.tv_key_backspace ){
            try {
                CharSequence selectedText = inputConnection.getSelectedText(0);

                if (TextUtils.isEmpty(selectedText)) {
                    inputConnection.deleteSurroundingText(1, 0);
                } else {
                    inputConnection.commitText("", 1);
                }
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }

        }else if( v.getId()==R.id.tv_key_enter  ){
            try {
                chargeAmount=Double.parseDouble(binding.posCharge.getText().toString());
                Bundle args=new Bundle();
                args.putDouble("Charge", chargeAmount );
                ShopActivity.navController.navigate(R.id.action_shopPOSFragment_to_shopPayments,args);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }

        }else{
            String value = keyValues.get(v.getId()).toString();
            inputConnection.commitText(value, 1);
            setInputConnection( binding.posCharge);
        }

    }

}
