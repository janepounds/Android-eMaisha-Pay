package com.cabral.emaishapay.fragments.buy_fragments;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.CircularImageView;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Info_BuyInputsDB;
import com.cabral.emaishapay.models.contact_model.ContactUsData;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.models.user_model.UserDetails;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.receivers.AlarmReceiver;
import com.cabral.emaishapay.utils.NotificationScheduler;
import com.cabral.emaishapay.utils.Utilities;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends Fragment {

    View rootView;

    DialogLoader dialogLoader;
    MyAppPrefsManager appPrefs;
    SharedPreferences sharedPreferences;

    CircularImageView profile_image;
    Button btn_edit_profile, btn_logout;
    TextView profile_name, profile_email;
    Switch local_notification, push_notification;
    TextView change_password, select_language, official_web, share_app, rate_app, privacy_policy, refund_policy, service_terms, test_ad_interstitial,
            a_z_terms;

    EditText oldPassword;
    EditText newPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.buy_inputs_settings, container, false);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        // noInternetDialog.show();

        dialogLoader = new DialogLoader(getContext());
        appPrefs = new MyAppPrefsManager(getContext());
        sharedPreferences = getContext().getSharedPreferences("UserInfo", MODE_PRIVATE);

        //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.actionSettings));


        // Binding Layout Views
        rate_app = rootView.findViewById(R.id.rate_app);
        share_app = rootView.findViewById(R.id.share_app);
        official_web = rootView.findViewById(R.id.official_web);
        refund_policy = rootView.findViewById(R.id.refund_policy);
        service_terms = rootView.findViewById(R.id.service_terms);
        a_z_terms = rootView.findViewById(R.id.a_z_terms);
        privacy_policy = rootView.findViewById(R.id.privacy_policy);
        change_password = rootView.findViewById(R.id.change_password);
        select_language = rootView.findViewById(R.id.select_language);
        test_ad_interstitial = rootView.findViewById(R.id.test_ad_interstitial);
        push_notification = rootView.findViewById(R.id.switch_push_notification);
        local_notification = rootView.findViewById(R.id.switch_local_notification);

        btn_logout = rootView.findViewById(R.id.btn_logout);
        btn_edit_profile = rootView.findViewById(R.id.btn_edit_account);
        profile_name = rootView.findViewById(R.id.profile_name);
        profile_email = rootView.findViewById(R.id.profile_email);
        profile_image = rootView.findViewById(R.id.profile_image);


        setupAppBarHeader();

        if (!ConstantValues.IS_USER_LOGGED_IN) {
            btn_logout.setText(getString(R.string.login));
        }


        local_notification.setChecked(appPrefs.isLocalNotificationsEnabled());
        push_notification.setChecked(appPrefs.isPushNotificationsEnabled());


        local_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appPrefs.setLocalNotificationsEnabled(isChecked);
                ConstantValues.IS_LOCAL_NOTIFICATIONS_ENABLED = appPrefs.isLocalNotificationsEnabled();

                if (isChecked) {
                    NotificationScheduler.setReminder(getContext(), AlarmReceiver.class);
                } else {
                    NotificationScheduler.cancelReminder(getContext(), AlarmReceiver.class);
                }

            }
        });


        push_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appPrefs.setPushNotificationsEnabled(isChecked);
                ConstantValues.IS_PUSH_NOTIFICATIONS_ENABLED = appPrefs.isPushNotificationsEnabled();

                TogglePushNotification(ConstantValues.IS_PUSH_NOTIFICATIONS_ENABLED);
            }
        });


 


        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog fullscreenDialog = new Dialog(getContext());
                fullscreenDialog.setContentView(R.layout.dialog_change_password);

                oldPassword = fullscreenDialog.findViewById(R.id.current_pin);
                newPassword = fullscreenDialog.findViewById(R.id.new_pin);
                Button saveButton = fullscreenDialog.findViewById(R.id.dialog_button);
                String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (validatePasswordForm()) {
                            dialogLoader.showProgressDialog();
                            Call<UserData> call = BuyInputsAPIClient.getInstance().updatePassword(access_token,oldPassword.getText().toString().trim(),
                                    newPassword.getText().toString().trim(),
                                    WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext())
                            );

                            call.enqueue(new Callback<UserData>() {
                                @Override
                                public void onResponse(Call<UserData> call, Response<UserData> response) {
                                    if (response.isSuccessful()) {
                                        if (response.body().getSuccess().equalsIgnoreCase("1") && response.body().getData() != null) {
                                            // User's Info has been Updated.
                                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        }else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                                            // Unable to Update User's Info.
                                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        else if(response.body().getSuccess().equalsIgnoreCase("2")){
                                            // Unable to Update User's Info.
                                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            // Unable to get Success status
                                            Toast.makeText(getContext(), getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        Toast.makeText(getContext(), ""+response.message(), Toast.LENGTH_SHORT).show();
                                    }
                                    dialogLoader.hideProgressDialog();
                                }

                                @Override
                                public void onFailure(Call<UserData> call, Throwable t) {
                                    Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                                    dialogLoader.hideProgressDialog();
                                }
                            });

                        } else {

                            Toast.makeText(getContext(), "Invalid Password", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

                fullscreenDialog.show();

            }
        });

        select_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Languages Fragment
                Fragment fragment = new Languages();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.main_fragment_container, fragment)
                        .addToBackStack(null).commit();
            }
        });


        official_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String web_url = ((EmaishaPayApp) getActivity().getApplicationContext()).getAppSettingsDetails().getSiteUrl();
                if (!web_url.startsWith("https://") && !web_url.startsWith("http://"))
                    web_url = "http://" + web_url;

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(web_url)));
            }
        });

        share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.shareMyApp(getContext());
            }
        });

        rate_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Utilities.rateMyApp(getContext());
            }
        });

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_NoTitleBar);
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);

                final ImageButton dialog_button = dialogView.findViewById(R.id.dialog_button);
                final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                final WebView dialog_webView = dialogView.findViewById(R.id.dialog_webView);

                dialog_title.setText(getString(R.string.privacy_policy));


                String description = ConstantValues.PRIVACY_POLICY;
                String styleSheet = "<style> " +
                        "body{background:#ffffff; margin:0; padding:0} " +
                        "p{color:#757575;} " +
                        "img{display:inline; height:auto; max-width:100%;}" +
                        "</style>";

                dialog_webView.setHorizontalScrollBarEnabled(false);
                dialog_webView.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);


                final AlertDialog alertDialog = dialog.create();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                }

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

            }
        });

        refund_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_NoTitleBar);
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);

                final ImageButton dialog_button = dialogView.findViewById(R.id.dialog_button);
                final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                final WebView dialog_webView = dialogView.findViewById(R.id.dialog_webView);

                dialog_title.setText(getString(R.string.refund_policy));


                String description = ConstantValues.REFUND_POLICY;
                String styleSheet = "<style> " +
                        "body{background:#ffffff; margin:0; padding:0} " +
                        "p{color:#757575;} " +
                        "img{display:inline; height:auto; max-width:100%;}" +
                        "</style>";

                dialog_webView.setHorizontalScrollBarEnabled(false);
                dialog_webView.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);


                final AlertDialog alertDialog = dialog.create();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                }

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });


        a_z_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_NoTitleBar);
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);

                final ImageButton dialog_button = dialogView.findViewById(R.id.dialog_button);
                final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                final WebView dialog_webView = dialogView.findViewById(R.id.dialog_webView);

                dialog_title.setText(getString(R.string.a_z));


                String description = ConstantValues.A_Z;
                String styleSheet = "<style> " +
                        "body{background:#eeeeee; margin:10; padding:10} " +
                        "p{color:#757575;} " +
                        "img{display:inline; height:auto; max-width:100%;}" +
                        "</style>";

                dialog_webView.setVerticalScrollBarEnabled(true);
                dialog_webView.setHorizontalScrollBarEnabled(false);
                dialog_webView.setBackgroundColor(Color.TRANSPARENT);
                dialog_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                dialog_webView.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);


                final AlertDialog alertDialog = dialog.create();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                }

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });

        service_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_NoTitleBar);
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);

                final ImageButton dialog_button = dialogView.findViewById(R.id.dialog_button);
                final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                final WebView dialog_webView = dialogView.findViewById(R.id.dialog_webView);

                dialog_title.setText(getString(R.string.service_terms));


                String description = ConstantValues.TERMS_SERVICES;
                String styleSheet = "<style> " +
                        "body{background:#ffffff; margin:0; padding:0} " +
                        "p{color:#757575;} " +
                        "img{display:inline; height:auto; max-width:100%;}" +
                        "</style>";

                dialog_webView.setHorizontalScrollBarEnabled(false);
                dialog_webView.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);


                final AlertDialog alertDialog = dialog.create();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                }

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ConstantValues.IS_USER_LOGGED_IN) {
                    // Edit UserID in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, "");
                    editor.apply();

                    // Set UserLoggedIn in MyAppPrefsManager
                    MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(getContext());
                    myAppPrefsManager.setUserLoggedIn(false);

                    // Set isLogged_in of ConstantValues
                    ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();

                    setupAppBarHeader();
                    btn_logout.setText(getString(R.string.login));

                } else {
                    // Navigate to Login Activity
                    startActivity(new Intent(getContext(), AuthActivity.class));
                    ((WalletHomeActivity) getContext()).finish();
                    ((WalletHomeActivity) getContext()).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                }
            }
        });


        return rootView;
    }


    //*********** Setup Header of Navigation Drawer ********//

    public void setupAppBarHeader() {

        // Check if the User is Authenticated
        if (ConstantValues.IS_USER_LOGGED_IN) {
            // Check User's Info from SharedPreferences
            if (!"".equalsIgnoreCase(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext()))) {

                // Get User's Info from Local Database User_Info_DB
                User_Info_BuyInputsDB userInfoDB = new User_Info_BuyInputsDB();
                UserDetails userInfo = userInfoDB.getUserData(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext()));

                // Set User's Name, Email and Photo
                profile_email.setText(userInfo.getEmail());
                profile_name.setText(userInfo.getFirstName() + " " + userInfo.getLastName());

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);
                Glide.with(getContext())
                        .setDefaultRequestOptions(options)
                        .asBitmap()
                        .load(ConstantValues.ECOMMERCE_URL + userInfo.getAvatar())
                        .into(profile_image);

                btn_edit_profile.setText(getString(R.string.edit_profile));
                btn_edit_profile.setBackground(getResources().getDrawable(R.drawable.rounded_corners_button_green));

                btn_edit_profile.setVisibility(View.GONE);

            } else {
                // Set Default Name, Email and Photo
                profile_image.setImageResource(R.drawable.profile);
                profile_name.setText(getString(R.string.login_or_signup));
                profile_email.setText(getString(R.string.login_or_create_account));
                btn_edit_profile.setText(getString(R.string.login));
                btn_edit_profile.setBackground(getResources().getDrawable(R.drawable.rounded_corners_button_red));
            }

        } else {
            // Set Default Name, Email and Photo
            profile_image.setImageResource(R.drawable.profile);
            profile_name.setText(getString(R.string.login_or_signup));
            profile_email.setText(getString(R.string.login_or_create_account));
            btn_edit_profile.setText(getString(R.string.login));
            btn_edit_profile.setBackground(getResources().getDrawable(R.drawable.rounded_corners_button_red));
            change_password.setVisibility(View.GONE);
        }


        // Handle DrawerHeader Click Listener
//        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Check if the User is Authenticated
//                if (ConstantValues.IS_USER_LOGGED_IN) {
//
//                    // Navigate to Update_Account Fragment
//                    Fragment fragment = new UpdateAccountFragment();
//                    FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .add(R.id.main_fragment_container, fragment)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            .addToBackStack(getString(R.string.actionCart)).commit();
//
//                } else {
//                    // Navigate to Login Activity
//                    startActivity(new Intent(getContext(), Login.class));
//                    ((DashboardActivity) getContext()).finish();
//                    ((DashboardActivity) getContext()).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
//                }
//            }
//        });
    }


    /*********** Request Server to Enable or Disable Notifications ********/

    public void TogglePushNotification(Boolean enable) {

        dialogLoader.showProgressDialog();

        String notify = "1";
        if (!enable) {
            notify = "0";
        }

        String deviceID = "";

        deviceID = FirebaseInstanceId.getInstance().getToken();

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;

        Call<ContactUsData> call = BuyInputsAPIClient.getInstance()
                .notify_me
                        (access_token,
                                notify,
                                deviceID
                        );

        call.enqueue(new Callback<ContactUsData>() {
            @Override
            public void onResponse(Call<ContactUsData> call, Response<ContactUsData> response) {

                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    } else {
                        // Unable to get Success status
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContactUsData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validatePasswordForm() {
        if (!ValidateInputs.isValidPassword(newPassword.getText().toString().trim())) {
            newPassword.setError(getString(R.string.invalid_password));
            return false;
        } else {
            return true;
        }
    }
}
