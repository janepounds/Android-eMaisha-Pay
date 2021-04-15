package com.cabral.emaishapay.adapters.Shop;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.databinding.ProductItemBinding;
import com.cabral.emaishapay.fragments.sell_fragment.MyProduceFragment;
import com.cabral.emaishapay.fragments.shop_fragment.ShopPOSFragment;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.modelviews.ShopProductsModelView;
import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.network.db.entities.EcProductCart;
import com.cabral.emaishapay.utils.Resource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PosProductAdapter extends RecyclerView.Adapter<PosProductAdapter.MyViewHolder> {
    private static final String TAG = "PosProductAdapter";


    MediaPlayer player;
    private List<? extends EcProduct> productData;
    private Context context;
    private DbHandlerSingleton dbHandler;
    ProductItemBinding binding;
    String currency;
    WeakReference<ShopPOSFragment> fragmentReference;
    ShopProductsModelView viewModel;
    LifecycleOwner viewLifecycleOwner;

    public PosProductAdapter(Context context, ShopProductsModelView viewModel, LifecycleOwner viewLifecycleOwner) {
        this.context = context;
        this.viewModel = viewModel;
        this.viewLifecycleOwner = viewLifecycleOwner;
        setHasStableIds(true);
    }


    public void setProductList(final List<? extends EcProduct> productList) {
        if ( this.productData== null) {
            this.productData =  productList;
            notifyItemRangeInserted(0, productList.size());
        }
        else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return productData.size();
                }

                @Override
                public int getNewListSize() {
                    return productList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return productData.get(oldItemPosition).getProduct_id() ==
                            productList.get(newItemPosition).getProduct_id();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    EcProduct newProduct = productList.get(newItemPosition);
                    EcProduct oldProduct = productData.get(oldItemPosition);
                    return newProduct.getProduct_weight() == oldProduct.getProduct_weight()
                            && TextUtils.equals(newProduct.getProduct_name(), oldProduct.getProduct_name())
                            && TextUtils.equals(newProduct.getProduct_weight_unit(), oldProduct.getProduct_weight_unit())
                            && newProduct.getProduct_sell_price() == oldProduct.getProduct_sell_price();
                }
            });
            productData = productList;
            result.dispatchUpdatesTo(this);
        }
        Log.d("AdapterSize","########"+this.getItemCount());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.product_item,  parent, false);

        return new PosProductAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setProductData(productData.get(position));

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.new_product)
                .error(R.drawable.new_product)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);


        Glide.with(context).load(Base64.decode(productData.get(position).getProduct_image(), Base64.DEFAULT)).apply(options).into(holder.binding.productImage);

        holder.binding.executePendingBindings();
        Log.d(TAG, "onBindViewHolder: product_image"+productData.get(position).getProduct_image());

        binding.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                //set product cart items
//                EcProductCart productCart = new EcProductCart();
//                productCart.setProduct_id(productData.get(position).getProduct_id());
//                productCart.setProduct_weight(productData.get(position).getProduct_weight());
//                productCart.setProduct_weight_unit(productData.get(position).getProduct_weight_unit());
//                productCart.setProduct_price(productData.get(position).getProduct_sell_price());
//                productCart.setProduct_qty(1);

                //insert item to cart
                subscribeToCartProduct(viewModel.addToCart());


            }
        });

       binding.cardProductItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                ViewGroup viewGroup = v.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_pos_product_details, viewGroup, false);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                initialiseProductDialog(dialogView,alertDialog, productData.get(position));
            }
        });

    }

    private void subscribeToCartProduct(LiveData<Integer> cartProduct) {

        cartProduct.observe(viewLifecycleOwner, myCartProduct->{
            // dialogLoader.showProgressDialog();

            if(myCartProduct==1) {
                Toasty.success(context, "Product Added to cart", Toast.LENGTH_SHORT).show();
                player.start();
            }else if(myCartProduct==2){
                Toasty.info(context, "Product already added to cart", Toast.LENGTH_SHORT).show();
            }else {
                Toasty.error(context, "Product added to cart failed try again", Toast.LENGTH_SHORT).show();
            }
            binding.executePendingBindings();
        });

    }


    @Override
    public int getItemCount() {
        return productData == null ? 0 : productData.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        Log.e("ErrorMJ",holder.binding.txtProductName.getText().toString());
        super.onViewRecycled(holder);
    }



    static class MyViewHolder extends RecyclerView.ViewHolder {

        final ProductItemBinding binding;

        public MyViewHolder(ProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


    private void initialiseProductDialog(View dialogView, AlertDialog alertDialog, EcProduct productData){

        double sell_price =Double.parseDouble( productData.getProduct_sell_price());

        TextView cancel = dialogView.findViewById(R.id.btn_cancel);
        TextView update = dialogView.findViewById(R.id.btn_update);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        TextView totalPriceTxt = dialogView.findViewById(R.id.txt_online_overall_total_price);

        TextView txtQty = (TextView) dialogView.findViewById(R.id.txt_product_qty);
        TextView ProductName = dialogView.findViewById(R.id.product_name);
        TextView ProductPrice = dialogView.findViewById(R.id.txt_product_price);
        ProductName.setText( productData.getProduct_name() );
        ProductPrice.setText( currency+" "+productData.getProduct_sell_price() );

        //get cart product



        CartProduct cartProduct=GetCartProduct(productData.getProduct_id(),productData.getProduct_name());
        if(cartProduct!=null && cartProduct.getCustomersBasketProduct()!=null && cartProduct.getCustomersBasketProduct().getCustomersBasketQuantity()!=0){
            Log.e("error",cartProduct.getCustomersBasketProduct().getProductsName());
            txtQty.setText(cartProduct.getCustomersBasketProduct().getCustomersBasketQuantity()+"");
        }

        int productQTY=Integer.parseInt(txtQty.getText().toString());
        totalPriceTxt.setText(currency+" "+(productQTY*sell_price));

        LinearLayout plusButton= dialogView.findViewById(R.id.plus);
        LinearLayout minusButton= dialogView.findViewById(R.id.minus);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtQty = dialogView.findViewById(R.id.txt_product_qty);
                int productQTY=Integer.parseInt(txtQty.getText().toString());
                productQTY++;
                txtQty.setText(productQTY+"");
                totalPriceTxt.setText(currency+" "+(productQTY*sell_price));
            }
        });
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtQty = dialogView.findViewById(R.id.txt_product_qty);
                int productQTY=Integer.parseInt(txtQty.getText().toString());
                productQTY--;
                txtQty.setText(productQTY+"");
                totalPriceTxt.setText(currency+" "+(productQTY*sell_price));
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                TextView txtQty = dialogView.findViewById(R.id.txt_product_qty);
                int productQTY=Integer.parseInt(txtQty.getText().toString());

                ProductDetails product=new ProductDetails();
                product.setProductsName( productData.getProduct_name() );
                product.setCustomersBasketQuantity( productQTY );
                try {
                    product.setProductsId( Integer.parseInt(productData.getProduct_id()) );
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                product.setCategoryNames( productData.getProduct_category() );
                product.setProductsImage( productData.getProduct_image() );
                product.setProductsPrice( productData.getProduct_sell_price());
                product.setSelectedProductsWeight( productData.getProduct_weight() );
                product.setSelectedProductsWeightUnit( productData.getProduct_weight_unit() );


                CartProduct cartProduct = new CartProduct();
                cartProduct.setCustomersBasketProduct(product);

                User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
                if( user_cart_BuyInputs_db.checkCartProduct( cartProduct.getCustomersBasketProduct().getProductsId(), cartProduct.getCustomersBasketProduct().getProductsName())){
                    UpdateCartItem(cartProduct,user_cart_BuyInputs_db );
                }else{
                    AddCartItem(cartProduct,user_cart_BuyInputs_db );
                }

                refreshCartProducts(user_cart_BuyInputs_db);
            }
        });

    }
    public static void AddCartItem(CartProduct cartProduct, User_Cart_BuyInputsDB user_cart_BuyInputs_db) {
        user_cart_BuyInputs_db.addCartItem
                (
                        cartProduct
                );
    }

    public static CartProduct GetCartProduct(String product_id, String product_name) {
        User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        CartProduct cartProduct = user_cart_BuyInputs_db.getCartProduct2
                (
                        product_id,product_name
                );
        return cartProduct;
    }
    public static void UpdateCartItem(CartProduct cartProduct, User_Cart_BuyInputsDB user_cart_BuyInputs_db) {
        user_cart_BuyInputs_db.updateCartItem2
                (
                        cartProduct
                );
    }


    public void refreshCartProducts(User_Cart_BuyInputsDB user_cart_BuyInputs_db) {
        int itemsCounter=0; double priceCounter=0;
        List<CartProduct> cartItemsList = new ArrayList<>();

        cartItemsList = user_cart_BuyInputs_db.getCartItems();
        for (int i = 0; i < cartItemsList.size(); i++) {
            ProductDetails product=cartItemsList.get(i).getCustomersBasketProduct();
            itemsCounter=itemsCounter+product.getCustomersBasketQuantity();
            priceCounter=priceCounter+(product.getCustomersBasketQuantity() * Double.parseDouble(product.getProductsPrice() ));
            Log.w("CartProduct",product.getProductsName()+" "+product.getCustomersBasketQuantity()+" "+Double.parseDouble(product.getProductsPrice()));

        }
        fragmentReference.get().totalItems.setText(itemsCounter+" Items");
        fragmentReference.get().totalprice.setText(currency+" "+priceCounter);
        fragmentReference.get().chargeAmount=priceCounter;
    }
}
