package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.Shop.PosProductAdapter;
import com.cabral.emaishapay.adapters.Shop.ProductAdapter;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.databinding.FragmentShopPosBinding;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.modelviews.ShopPOSModelView;
import com.cabral.emaishapay.modelviews.ShopProductsModelView;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.utils.Resource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopPOSFragment extends Fragment {

    FragmentManager fm;
    Activity shop;
    private Context context;
    public static EditText etxtSearch, etxtCharge;
    ProductAdapter productAdapter;
    TextView txtNoProducts, txtEnter, txtItems;
    public  TextView totalItems,totalprice;
    View enterView, itemsView;
    ConstraintLayout layoutCart;
    ImageView imgNoProduct, imgScanner;
    private RecyclerView recyclerView;
    Toolbar toolbar; String userId;
    private DbHandlerSingleton dbHandler;
    public double chargeAmount;
    private WeakReference<ShopPOSFragment> fragmentReference;
    FrameLayout posChargeLayout;
    ImageView imageTick;
    private ShopProductsModelView viewModel;
    FragmentShopPosBinding binding;


    User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();

    List<CartProduct> cartItemsList = new ArrayList<>();
    List<CartProduct> finalCartItemsList = new ArrayList<>();
    List<ProductDetails> cartProducts = new ArrayList<>();
    public ShopPOSFragment() {
    }

    public ShopPOSFragment(ShopActivity shopActivity, FragmentManager supportFragmentManager) {
        this.fm = supportFragmentManager;
        this.shop = shopActivity;
    }

    public ShopPOSFragment(boolean b) {
    }

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
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.all_product);

        viewModel = new ViewModelProvider(requireActivity()).get(ShopProductsModelView.class);
        fragmentReference = new WeakReference<>(ShopPOSFragment.this);

        dbHandler = DbHandlerSingleton.getHandlerInstance(getContext());

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

            }
        });


        binding.layoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShopPayments nextFrag= new ShopPayments(chargeAmount);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.shop_navigation_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();


            }
        });

        binding.imgCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShopPayments nextFrag= new ShopPayments( Double.parseDouble(binding.posCharge.getText().toString()) );
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.shop_navigation_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();


            }
        });


//        binding.imageNoProduct.setVisibility(View.GONE);
//        binding.txtNoProducts.setVisibility(View.GONE);


//        refreshCartProducts();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productAdapter = new ProductAdapter(context);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.posRecycler.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        binding.posRecycler.setHasFixedSize(true);
        binding.posRecycler.setAdapter(productAdapter);
        subscribeToProducts(viewModel.getMerchantProducts());
        binding.txtItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.enterSelected.setVisibility(View.INVISIBLE);
                binding.itemsSelected.setVisibility(View.VISIBLE);
                binding.etxtSearch.setVisibility(View.VISIBLE);
                binding.posRecycler.setVisibility(View.VISIBLE);
                binding.layoutCart.setVisibility(View.VISIBLE);
                binding.layoutPosCharge.setVisibility(View.GONE);
                binding.posCharge.clearFocus();




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
        refreshCartProducts();
        super.onResume();
    }

    public void refreshCartProducts() {
        ClearCart();
        int itemsCounter=0; double priceCounter=0;
        cartItemsList = user_cart_BuyInputs_db.getCartItems();

        for (int i = 0; i < cartItemsList.size(); i++) {
            ProductDetails product=cartItemsList.get(i).getCustomersBasketProduct();
            itemsCounter=itemsCounter+product.getCustomersBasketQuantity();
            priceCounter=priceCounter+(product.getCustomersBasketQuantity() * Double.parseDouble(product.getProductsPrice() ));
            Log.e("CartProduct",product.getProductsName()+" "+product.getCustomersBasketQuantity()+" "+Double.parseDouble(product.getProductsPrice()));
        }
        String currency =context.getString(R.string.currency);

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
}
