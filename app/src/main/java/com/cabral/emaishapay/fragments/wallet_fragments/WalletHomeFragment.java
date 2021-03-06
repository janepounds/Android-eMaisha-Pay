package com.cabral.emaishapay.fragments.wallet_fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BlurMaskFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.DailogFragments.DepositPayments;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.NewEmaishaPayHomeBinding;
import com.cabral.emaishapay.models.BalanceResponse;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.models.WalletTransactionSummary;
import com.cabral.emaishapay.models.banner_model.BannerDetails;
import com.cabral.emaishapay.models.product_model.Image;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.indicators.PagerIndicator;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class WalletHomeFragment extends Fragment {
    private static final String TAG = "WalletHomeFragment";
    private NewEmaishaPayHomeBinding binding;
    private Context context;
    private final List<WalletTransactionResponse.TransactionData.Transactions> models = new ArrayList<>();
    public static double balance = 0, commisionbalance=0,totalBalance=0;
    public static FragmentManager fm;
    DialogLoader dialog;
    public static DialogFragment depositPaymentsDialog;
    private static SharedPreferences sharedPreferences;
    NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.new_emaisha_pay_home, container, false);
        dialog = new DialogLoader(getContext());

        fm = requireActivity().getSupportFragmentManager();
        sharedPreferences = getActivity().getSharedPreferences(MyAppPrefsManager.PREF_NAME, MODE_PRIVATE);
        NavHostFragment navHostFragment =
                (NavHostFragment) fm.findFragmentById(R.id.wallet_home_container);

        navController = navHostFragment.getNavController();
//        getTransactionsData();
        getTransactionSummary();
        getBalanceAndCommission();

        String name=ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, context));

        binding.username.setText("Hello "+ name +", ");

        if(WalletHomeActivity.Banners!=null){
            Log.w("BannerWarning1",WalletHomeActivity.Banners.size()+" Banners");
            if(WalletHomeActivity.Banners.size()==1 && WalletHomeActivity.Banners.get(0).getImage()==null)
                ImageSlider("", WalletHomeActivity.Banners );
            else
                ImageSlider("Ads", WalletHomeActivity.Banners );
        }


        String user_pic =WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_PIC, context);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_user_profile_placeholder_home)
                .error(R.drawable.ic_user_profile_placeholder_home)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        Glide.with(requireContext()).load(ConstantValues.WALLET_DOMAIN +user_pic).apply(options).into(binding.imgUsername);


        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);

        String role = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);

        if(role.equalsIgnoreCase("merchant")){
            binding.layoutTransactWithCustomers.setVisibility(View.GONE);
            binding.labelTransact.setVisibility(View.GONE);
            binding.layoutTransfer.setVisibility(View.INVISIBLE);
            binding.layoutSettle.setVisibility(View.VISIBLE);
            binding.cardBalanceLabel.setText("Commission");
        }
        else if(role.equalsIgnoreCase(getString(R.string.role_master_agent)) ){

            binding.layoutTransactWithCustomers.setVisibility(View.VISIBLE);
            binding.labelTransact.setVisibility(View.VISIBLE);
            binding.layoutTransfer.setVisibility(View.INVISIBLE);
            binding.layoutSettle.setVisibility(View.VISIBLE);
            binding.cardBalanceLabel.setText("Commission");

        }
        else{
            binding.layoutTransactWithCustomers.setVisibility(View.GONE);
            binding.labelTransact.setVisibility(View.GONE);
            binding.layoutTransfer.setVisibility(View.VISIBLE);
            binding.layoutSettle.setVisibility(View.INVISIBLE);
            binding.cardBalanceLabel.setText("Card");
            //Log.d(TAG, "onCreateView: *"+role+"*");
        }


        binding.layoutTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //To TransferMoney
                Bundle args=new Bundle();
                args.putString("KEY_ACTION", getString(R.string.transactions) );
                navController.navigate(R.id.action_walletHomeFragment2_to_transferMoney,args);

            }
        });
        binding.layoutSettle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //To List and not form
                Bundle args=new Bundle();
                args.putString("KEY_TITLE", getString(R.string.settlements) );
                //navController.navigate(R.id.action_walletHomeFragment2_to_transferMoney,args);
                navController.navigate(R.id.action_walletHomeFragment2_to_walletTransactionsListFragment2,args);
            }
        });
        binding.layoutTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                depositPaymentsDialog = new DepositPayments( WalletHomeFragment.balance);
                depositPaymentsDialog.show(ft, "dialog");
            }
        });

        binding.layoutLoan.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {


                  //Go to coming soon
                  AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                  View dialogView = getLayoutInflater().inflate(R.layout.layout_coming_soon, null);
                  dialog.setView(dialogView);
                  dialog.setCancelable(true);

                  ImageView close = dialogView.findViewById(R.id.coming_soon_close);
                  Button ok = dialogView.findViewById(R.id.button_submit);

                  final AlertDialog alertDialog = dialog.create();

                  close.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          alertDialog.dismiss();
                      }
                  });
                  ok.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          alertDialog.dismiss();
                      }
                  });


                  alertDialog.show();


              }
          }
        );
        binding.layoutPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to PayFragment
                navController.navigate(R.id.action_walletHomeFragment2_to_payFragment);
            }
        });

        binding.layoutBeneficiaries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To BeneficiariesListFragment
                navController.navigate(R.id.action_walletHomeFragment2_to_beneficiariesListFragment);
            }
        });

        binding.imgAmountVisibile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.imgAmountVisibile.setVisibility(View.GONE);
                binding.imgAmountBlur.setVisibility(View.VISIBLE);

                //hiding the amounts
                binding.totalBalance.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                float radius = binding.totalBalance.getTextSize() / 3;
                BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
                binding.totalBalance.getPaint().setMaskFilter(filter);

                binding.walletBalance.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                float radius1 = binding.walletBalance.getTextSize() / 3;
                BlurMaskFilter filter1 = new BlurMaskFilter(radius1, BlurMaskFilter.Blur.NORMAL);
                binding.walletBalance.getPaint().setMaskFilter(filter1);

                binding.cardBalance.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                float radius2 = binding.cardBalance.getTextSize() / 3;
                BlurMaskFilter filter2 = new BlurMaskFilter(radius2, BlurMaskFilter.Blur.NORMAL);
                binding.cardBalance.getPaint().setMaskFilter(filter2);


            }
        });

        binding.imgAmountBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.imgAmountVisibile.setVisibility(View.VISIBLE);
                binding.imgAmountBlur.setVisibility(View.GONE);

                binding.totalBalance.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                binding.totalBalance.getPaint().setMaskFilter(null);

                binding.walletBalance.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                binding.walletBalance.getPaint().setMaskFilter(null);


                binding.cardBalance.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                binding.cardBalance.getPaint().setMaskFilter(null);
            }
        });

        binding.layoutUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_walletHomeFragment2_to_walletAccountFragment2);
            }
        });
        binding.moreTransactionCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args=new Bundle();
                args.putString("KEY_TITLE", getString(R.string.transactions) );


                navController.navigate(R.id.action_walletHomeFragment2_to_walletTransactionsListFragment2,args);
            }
        });
        binding.layoutLoanApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to coming soon
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                View dialogView = getLayoutInflater().inflate(R.layout.layout_coming_soon, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);

                ImageView close = dialogView.findViewById(R.id.coming_soon_close);
                Button ok = dialogView.findViewById(R.id.button_submit);




                final AlertDialog alertDialog = dialog.create();

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });



    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private String ucf(String str) {
        if (str == null || str.length() < 2)
            return str;

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

//    private void getTransactionsData() {
//        dialog.showProgressDialog();
//
//        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
//        String request_id = WalletHomeActivity.generateRequestId();
//        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
//
//        /**********RETROFIT IMPLEMENTATION************/
//        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
//        Call<WalletTransactionResponse> call = apiRequests.transactionList2(access_token,transactions_limit,request_id,category,"getTransactionLogs");
//
//        call.enqueue(new Callback<WalletTransactionResponse>() {
//            @Override
//            public void onResponse(Call<WalletTransactionResponse> call, Response<WalletTransactionResponse> response) {
//                if (response.code() == 200) {
//                    try {
//                        WalletTransactionResponse.TransactionData walletTransactionResponseData = response.body().getData();
//                        List<WalletTransactionResponse.TransactionData.Transactions> transactions = walletTransactionResponseData.getTransactions();
//                        models.clear();
//                        if(transactions.size()!=0){
//                            int loop_limit=transactions_limit;
//                            if(transactions.size()<transactions_limit)
//                                loop_limit=transactions.size();
//
//                            for (int i = 0; i < loop_limit; i++) {
//                                WalletTransactionResponse.TransactionData.Transactions res = transactions.get(i);
//                                models.add( res );
//                            }
//                        }
//
//
//                    }
//                    catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    finally {
//                        if(models.size()>0){
//                            WalletTransactionsListAdapter adapter = new WalletTransactionsListAdapter( models, getActivity().getSupportFragmentManager());
//                            binding.recyclerView.setAdapter(adapter);
//                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//                            binding.recyclerView.setHasFixedSize(true);
//                        } else{
//                            binding.moreTransactionCards.setVisibility(View.GONE);
//                            binding.noTransactionCards.setVisibility(View.VISIBLE);
//                        }
//                    }
//
//
//                    dialog.hideProgressDialog();
//                } else if (response.code() == 401) {
//
//                    TokenAuthFragment.startAuth(true);
//                    //getActivity().finish();
//                    if (response.errorBody() != null) {
//                        Log.e("info", new String(String.valueOf(response.errorBody())));
//                    } else {
//                        Log.e("info", "Something got very wrong");
//                    }
//                    dialog.hideProgressDialog();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<WalletTransactionResponse> call, Throwable t) {
//                Log.e("info", "Something got very very wrong");
//                dialog.hideProgressDialog();
//            }
//        });
//
//
//    }

    private void getTransactionSummary() {
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<WalletTransactionSummary> call = apiRequests.getSummary(access_token,request_id,"getTransactionsSummary");

        call.enqueue(new Callback<WalletTransactionSummary>() {
            @Override
            public void onResponse(@NotNull Call<WalletTransactionSummary> call, @NotNull Response<WalletTransactionSummary> response) {
                dialog.hideProgressDialog();

                if (response.isSuccessful()) {
                    if(response.body().getStatus()==1){
                        if(response.body().getData().getLastCredit()!=null ){
                            String receiver_name = response.body().getData().getLastCredit().getReceiver();
                            double amount = response.body().getData().getLastCredit().getAmount();
                            String date_completed =response.body().getData().getLastCredit().getDateCompleted();
                            binding.textCreditName.setText(receiver_name);
                            binding.textAmountCredit.setText(response.body().getData().getLastCredit().getTrans_currency()+amount);
                            binding.dateCredit.setText(date_completed);

                        } else{

                            binding.textCreditName.setText("");
                            binding.textAmountCredit.setText("No Data");
                            binding.dateCredit.setText("");
                        }

                        if(response.body().getData().getLastDebit()!=null){
                            String sender_name = response.body().getData().getLastDebit().getReceiver();
                            double amount = response.body().getData().getLastDebit().getAmount();
                            String date_completed =response.body().getData().getLastDebit().getDateCompleted();
                            binding.textDebitName.setText(sender_name);
                            binding.textAmountDebit.setText(response.body().getData().getLastDebit().getTrans_currency() +amount);
                            binding.dateDebit.setText(date_completed);


                        }else{
                            binding.textDebitName.setText("");
                            binding.textAmountDebit.setText("No Data");
                            binding.dateDebit.setText("");

                        }

                    }else{
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    }


                }
                else if (response.code() == 401) {
                    Toast.makeText(context, "Session Expired", Toast.LENGTH_LONG).show();
                    //Omitted to avoid current Destination conflicts
                    TokenAuthFragment.startAuth(true);
                } else {
                    Log.e("info", String.valueOf(response.body().getMessage()));
                }
            }

            @Override
            public void onFailure(@NotNull Call<WalletTransactionSummary> call, @NotNull Throwable t) {
                dialog.hideProgressDialog();
                Log.e("info : ", String.valueOf(t.getMessage()));
                Toast.makeText(context, "An error occurred Try again Later", Toast.LENGTH_LONG).show();

            }
        });

    }

    public void getBalanceAndCommission() {
        dialog.showProgressDialog();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());

        Call<BalanceResponse> call = apiRequests.requestBalance(access_token,request_id,category,"getBalance");
        call.enqueue(new Callback<BalanceResponse>() {
            @Override
            public void onResponse(@NotNull Call<BalanceResponse> call, @NotNull Response<BalanceResponse> response) {
                dialog.hideProgressDialog();

                if (response.code() == 200 && response.body().getData()!=null) {
                  balance =  response.body().getData().getBalance();
                  commisionbalance = response.body().getData().getCommission();
                  totalBalance = response.body().getData().getTotalBalance();

                    WalletHomeActivity.savePreferences(String.valueOf(WalletHomeActivity.PREFERENCE_WALLET_BALANCE), balance+"", context);

                    binding.totalBalance.setText(getString(R.string.currency)+" "+ NumberFormat.getInstance().format(totalBalance));
                    binding.walletBalance.setText(getString(R.string.currency)+" "+ NumberFormat.getInstance().format(balance));
                    binding.cardBalance.setText(getString(R.string.currency)+" "+ NumberFormat.getInstance().format(commisionbalance));

                } else if (response.code() == 401) {
                    Toast.makeText(context, "Session Expired", Toast.LENGTH_LONG).show();
                    //Omitted to avoid current Destination conflicts
                    TokenAuthFragment.startAuth(true);
                } else {
                    Log.e("info", String.valueOf(response.body().getMessage()));
                }
            }

            @Override
            public void onFailure(@NotNull Call<BalanceResponse> call, @NotNull Throwable t) {
                dialog.hideProgressDialog();
                Log.e("info : ", String.valueOf(t.getMessage()));
                Toast.makeText(context, "An error occurred Try again Later", Toast.LENGTH_LONG).show();

            }
        });
    }

    //*********** Setup the ImageSlider with the given List of Product Images ********//


    //*********** Setup the ImageSlider with the given List of Product Images ********//

    @SuppressLint("CheckResult")
    private void ImageSlider(String itemThumbnail, List<BannerDetails> banner) {
         //Log.w(TAG, banner.size()+" ads");
        // Initialize new HashMap<ImageName, ImagePath>
        final HashMap<String, String> slider_covers = new HashMap<>();
        // Initialize new Array for Image's URL
        final String[] images = new String[banner.size()];


        if (banner.size() > 0) {
            for (int i = 0; i < banner.size(); i++) {
                // Get Image's URL at given Position from itemImages List
                images[i] = banner.get(i).getImage();
            }
        }


        // Put Image's Name and URL to the HashMap slider_covers
        if (itemThumbnail.equalsIgnoreCase("")) {
            slider_covers.put("a", R.drawable.banner_placeholder+"" );

        } else if (images.length == 0) {
            slider_covers.put("a",  itemThumbnail);

        } else {
            slider_covers.put("a",  itemThumbnail);

            for (int i = 0; i < images.length; i++) {
                slider_covers.put("b" + i,  images[i]);
            }
        }


        for (String name : slider_covers.keySet()) {

            // Initialize DefaultSliderView
            TextSliderView defaultSliderView = new TextSliderView(context);

            RequestOptions requestOptions = new RequestOptions();

            requestOptions.centerCrop();

            // Set Attributes(Name, Placeholder, Image, Type etc) to DefaultSliderView
            defaultSliderView
                    //.description(name)
                    .setRequestOption(requestOptions)
                    .image(slider_covers.get(name));
            Log.w("BannerWarnings",slider_covers.get(name));
            // Add DefaultSliderView to the SliderLayout
            binding.productCoverSlider.addSlider(defaultSliderView);
        }

        // Set PresetTransformer type of the SliderLayout
        binding.productCoverSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        binding.productCoverSlider.setCustomAnimation(new DescriptionAnimation());
        binding.productCoverSlider.setDuration(4000);
        //binding.productCoverSlider.setBackgroundColor(ContextCompat.getColor(context,R.color.glide_slider_background_color));
        binding.productCoverSlider.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.productCoverSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);

    }








}