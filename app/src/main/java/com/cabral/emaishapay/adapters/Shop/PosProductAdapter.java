package com.cabral.emaishapay.adapters.Shop;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
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
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.fragments.sell_fragment.MyProduceFragment;
import com.cabral.emaishapay.fragments.shop_fragment.ShopPOSFragment;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.product_model.ProductDetails;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PosProductAdapter extends RecyclerView.Adapter<PosProductAdapter.MyViewHolder> {


    MediaPlayer player;
    private List<HashMap<String, String>> productData;
    private Context context;
    private DbHandlerSingleton dbHandler;
    String currency;
    WeakReference<ShopPOSFragment> fragmentReference;
    public PosProductAdapter(Context context, List<HashMap<String, String>> productData, WeakReference<ShopPOSFragment> fragmentReference) {
        this.context = context;
        this.productData = productData;
        this.fragmentReference = fragmentReference;
        currency =context.getString(R.string.currency);
    }


    @NonNull
    @Override
    public PosProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final PosProductAdapter.MyViewHolder holder, int position) {
        dbHandler = DbHandlerSingleton.getHandlerInstance(context);
        final String product_id = productData.get(position).get("product_id");
        String name = productData.get(position).get("product_name");
        String category = productData.get(position).get("product_category");
        String supplier_id = productData.get(position).get("product_supplier");
        String buy_price = productData.get(position).get("product_buy_price");
        String sell_price = productData.get(position).get("product_sell_price");
        String base64Image = productData.get(position).get("product_image");
        String productstock = productData.get(position).get("product_stock");

        final String product_weight = productData.get(position).get("product_weight");
        final String product_price = productData.get(position).get("product_sell_price");
        final String weight_unit_name = productData.get(position).get("product_weight_unit");

        String supplier_name = dbHandler.getSupplierName(supplier_id);
        holder.txtProductName.setText(name);
        holder.categoryName.setText(category);
        holder.txt_product_stock.setText(productstock);
        holder.txtSupplierName.setText(supplier_name);
        holder.txtBuyPrice.setText(currency + " " + buy_price);
        holder.txtSellPrice.setText(currency + " " + sell_price);
        holder.txt_per_unit.setText(productData.get(position).get("product_weight_unit"));

        if (base64Image != null) {
            if (base64Image.length() < 6) {
                Log.d("64base", base64Image);
                holder.product_image.setImageResource(R.drawable.image_placeholder);
            } else {
                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.product_image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }



        holder.product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int check = dbHandler.addToCart(product_id, product_weight, weight_unit_name, product_price, 1);

                if (check == 1) {
                    Toasty.success(context, "Product Added to cart", Toast.LENGTH_SHORT).show();
                    player.start();
                } else if (check == 2) {

                    Toasty.info(context, "Product already added to cart", Toast.LENGTH_SHORT).show();

                } else {
                    Toasty.error(context, "Product added to cart failed try again", Toast.LENGTH_SHORT).show();

                }


            }
        });

        holder.cardProductItem.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public int getItemCount() {
        return productData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductName, txtSupplierName, txtBuyPrice, txtSellPrice, txt_product_stock, categoryName,txt_per_unit;
       // Button btnAddToCart;
        ImageView product_image, imgDelete;
        CardView cardProductItem;
        LinearLayout img_delete_shadow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtSupplierName = itemView.findViewById(R.id.txt_product_supplier_value);
            txtBuyPrice = itemView.findViewById(R.id.txt_product_buy_price_value);
            txtSellPrice = itemView.findViewById(R.id.txt_product_sell_price_value);
            txt_product_stock = itemView.findViewById(R.id.txt_product_stock_value);
            img_delete_shadow = itemView.findViewById(R.id.img_delete_shadow);
            imgDelete = itemView.findViewById(R.id.img_delete);
            product_image = itemView.findViewById(R.id.product_image);
            categoryName = itemView.findViewById(R.id.category_name);
            txt_per_unit= itemView.findViewById(R.id.txt_per_unit);
            imgDelete = itemView.findViewById(R.id.img_delete);
            imgDelete.setVisibility(View.GONE);
           // btnAddToCart = itemView.findViewById(R.id.btn_add_cart);
            cardProductItem = itemView.findViewById(R.id.card_product_item);


        }
    }

    private void initialiseProductDialog(View dialogView, AlertDialog alertDialog, HashMap<String, String> productData){

        double sell_price =Double.parseDouble( productData.get("product_sell_price") );

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
        ProductName.setText( productData.get("product_name")  );
        ProductPrice.setText( currency+" "+productData.get("product_sell_price")  );

        CartProduct cartProduct=GetCartProduct(productData.get("product_id"),productData.get("product_name"));
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
                product.setProductsName( productData.get("product_name") );
                product.setCustomersBasketQuantity( productQTY );
                try {
                    product.setProductsId( Integer.parseInt(productData.get("product_id")) );
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                product.setCategoryNames( productData.get("product_category") );
                product.setProductsImage( productData.get("product_image") );
                product.setProductsPrice( productData.get("product_sell_price") );
                product.setSelectedProductsWeight( productData.get("product_weight") );
                product.setSelectedProductsWeightUnit( productData.get("product_weight_unit") );


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
    }
}
