package com.cabral.emaishapay.fragments.wallet_fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.LayoutScanAndPayProcessStep1Binding;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

public class ScanAndPayStep1 extends Fragment {
    private static final String TAG = "ScanAndPayStep1";
    private ScannerLiveView camera;
    private TextView text_merchant_id;
    private Context context;
    private LayoutScanAndPayProcessStep1Binding binding;
    private Toolbar toolbarScanPayProcess1;
    DialogLoader dialogLoader;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.layout_scan_and_pay_process_step_1,container,false);
        WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        WalletHomeActivity.scanCoordinatorLayout.setVisibility(View.GONE);
        WalletHomeActivity.bottomNavigationShop.setVisibility(View.GONE);
        dialogLoader = new DialogLoader(context);

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarScanPayProcess1);
       // binding.toolbarScanPayProcess1.setTitle("Scan and Pay");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.cameraPreview.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) {
                // method is called when scanner is started
//                Toast.makeText(context, "Scanner Started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerStopped(ScannerLiveView scanner) {
                // method is called when scanner is stoped.
//                Toast.makeText(context, "Scanner Stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerError(Throwable err) {
                // method is called when scanner gives some error.
                Toast.makeText(context, "Scanner Error: " + err.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeScanned(String data) {
                // method is called when camera scans the
                // qr code and the data from qr code is
                // stored in data in string format.
                binding.textMerchantId.setText(data);
                Log.d(TAG, "onCodeScanned: merchant_code"+data);
                if(data!=null) {

                    dialogLoader.showProgressDialog();
                    //navigate to step 2
                    //call scan merchant code fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("merchant_id", data);
                    WalletHomeActivity.navController.navigate(R.id.action_scanAndPayStep1_to_scanAndPayStep2,bundle);
                }else {
                    binding.cameraPreview.startScanner();
                }
                dialogLoader.hideProgressDialog();
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        ZXDecoder decoder = new ZXDecoder();
        // 0.5 is the area where we have
        // to place red marker for scanning.
        decoder.setScanAreaPercent(0.9);
        // below method will set secoder to camera.
        binding.cameraPreview.setDecoder(decoder);
        binding.cameraPreview.startScanner();
    }

    @Override
    public void onPause() {
        // on app pause the
        // camera will stop scanning.
        binding.cameraPreview.stopScanner();
        super.onPause();
    }
}
