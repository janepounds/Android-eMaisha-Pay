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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.R;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

public class ScanAndPayStep1 extends Fragment {
    private static final String TAG = "ScanAndPayStep1";
    private ScannerLiveView camera;
    private TextView text_merchant_id;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.layout_scan_and_pay_process_step_1, container, false);
        camera = view.findViewById(R.id.camera_preview);
        text_merchant_id = view.findViewById(R.id.text_merchant_id);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        camera.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) {
                // method is called when scanner is started
                Toast.makeText(context, "Scanner Started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerStopped(ScannerLiveView scanner) {
                // method is called when scanner is stoped.
                Toast.makeText(context, "Scanner Stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerError(Throwable err) {
                // method is called when scanner gives some error.
                Toast.makeText(context, "Scanner Error: " + err.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeScanned(String data) {
                // method is called when camera scans the
                // qr code and the data from qr code is
                // stored in data in string format.
                text_merchant_id.setText(data);
                Log.d(TAG, "onCodeScanned: merchant_code"+data);
                if(data!=null) {

                    //navigate to step 2
                    //call scan merchant code fragment
                    ScanAndPayStep2 scanMerchantCode = new ScanAndPayStep2();
                    Bundle bundle = new Bundle();
                    bundle.putString("merchant_id", data);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    scanMerchantCode.setArguments(bundle);
                    transaction.replace(R.id.wallet_home_container, scanMerchantCode);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }else {
                    camera.startScanner();
                }
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        ZXDecoder decoder = new ZXDecoder();
        // 0.5 is the area where we have
        // to place red marker for scanning.
        decoder.setScanAreaPercent(0.8);
        // below method will set secoder to camera.
        camera.setDecoder(decoder);
        camera.startScanner();
    }

    @Override
    public void onPause() {
        // on app pause the
        // camera will stop scanning.
        camera.stopScanner();
        super.onPause();
    }
}
