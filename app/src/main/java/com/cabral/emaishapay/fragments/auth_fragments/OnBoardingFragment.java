package com.cabral.emaishapay.fragments.auth_fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.databinding.FragmentOnBoardingBinding;

public class OnBoardingFragment  extends Fragment {
    ViewPager viewPager;
    com.cabral.emaishapay.adapters.SliderAdapter sliderAdapter;
    TextView[] dots;
    Button letsGetStarted,nextBtn;
    Animation animation;
    int currentPos;
    FragmentOnBoardingBinding binding;
    Context context;
    
    @Override
    public void onAttach(@NonNull Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_boarding,container,false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void skip(View view) {
        //startActivity(new Intent(this, Login.class));
       // finish();
    }

    public void next(View view) {
        viewPager.setCurrentItem(currentPos + 1);
    }

    private void addDots(int position) {

        dots = new TextView[4];
        binding.dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(context);
            //•
            dots[i].setText(Html.fromHtml("●"));
            dots[i].setTextSize(20);
            dots[i].setTextColor((getResources().getColor(R.color.whiteColor)));

            binding.dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setText(Html.fromHtml("○"));
            dots[position].setTextSize(20);
            dots[position].setTextColor((getResources().getColor(R.color.whiteColor)));
        }

    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos = position;

            if (position == 0) {

                binding.topItemsLayout.setVisibility(View.VISIBLE);
                binding.blueGradientLayout.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                binding.gettingStarted.setVisibility(View.GONE);

            } else if (position == 1) {

                binding.topItemsLayout.setVisibility(View.VISIBLE);
                binding.blueGradientLayout.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                binding.gettingStarted.setVisibility(View.GONE);
            } else if (position == 2) {

                binding.topItemsLayout.setVisibility(View.VISIBLE);
                binding.blueGradientLayout.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                binding.gettingStarted.setVisibility(View.GONE);
            } else {
                animation = AnimationUtils.loadAnimation(context, R.anim.enter_from_left);
                letsGetStarted.setAnimation(animation);
                binding.gettingStarted.setAnimation(animation);
                binding.topItemsLayout.setVisibility(View.INVISIBLE);
                binding.blueGradientLayout.setVisibility(View.INVISIBLE);
                nextBtn.setVisibility(View.INVISIBLE);
                viewPager.setVisibility(View.INVISIBLE);
                binding.gettingStarted.setVisibility(View.VISIBLE);
                binding.dotsLayout.setVisibility(View.GONE);

            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
