package com.cabral.emaishapay.fragments.auth_fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.databinding.FragmentGetStartedSignUpBinding;
import com.cabral.emaishapay.utils.ValidateInputs;

public class GetStartedSignUpFragment extends Fragment {
    FragmentGetStartedSignUpBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_get_started_sign_up,container,false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View decorView = getActivity().getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        binding.getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validate Login Form Inputs
                boolean isValidData = validateForm();
//
            if (isValidData) {
                // Proceed User Registration

 //               sendVerificationCode(getResources().getString(R.string.ugandan_code) + binding.userMobile.getText().toString().trim());
            }



                //navigate to Sign Up Fragment

                AuthActivity.navController.navigate(R.id.action_getStartedSignUpFragment_to_signUpFragment);
            }
        });

        binding.layoutSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to Sign Up Fragment

                AuthActivity.navController.navigate(R.id.action_getStartedSignUpFragment_to_loginFragment);

            }
        });
    }


    private boolean validateForm() {

        if (!ValidateInputs.isValidNumber(binding.userMobile.getText().toString().trim())) {
            binding.userMobile.setError(getString(R.string.invalid_contact));
            return false;

        }
        else {
            return true;
        }
    }

}
