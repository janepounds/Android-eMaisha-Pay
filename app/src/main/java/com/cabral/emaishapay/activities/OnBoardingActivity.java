package com.cabral.emaishapay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.daimajia.slider.library.SliderAdapter;

public class OnBoardingActivity extends AppCompatActivity {
    //Variables
    ViewPager viewPager;
    LinearLayout dotsLayout;
    RelativeLayout topItemsLayout,blueGradientLayout,signInLayout;
    com.cabral.emaishapay.adapters.SliderAdapter sliderAdapter;
    TextView[] dots;
    Button letsGetStarted,nextBtn;
    Animation animation;
    FrameLayout gettingStartedLayout;
    int currentPos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_boarding);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        //to solve svg inflating error
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //Hooks
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        letsGetStarted = findViewById(R.id.get_started_btn);
        topItemsLayout = findViewById(R.id.top_items_layout);
        blueGradientLayout = findViewById(R.id.blue_bg);
        signInLayout = findViewById(R.id.layout_signin);
        nextBtn = findViewById(R.id.next_btn);
        gettingStartedLayout = findViewById(R.id.getting_started);

        //Call adapter
        sliderAdapter = new com.cabral.emaishapay.adapters.SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        //Dots
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
    }

    public void skip(View view) {
        startActivity(new Intent(this, Login.class));
        finish();
    }

    public void next(View view) {
        viewPager.setCurrentItem(currentPos + 1);
    }

    private void addDots(int position) {

        dots = new TextView[4];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            //•
            dots[i].setText(Html.fromHtml("●"));
            dots[i].setTextSize(20);
            dots[i].setTextColor((getResources().getColor(R.color.whiteColor)));

            dotsLayout.addView(dots[i]);
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

                topItemsLayout.setVisibility(View.VISIBLE);
                blueGradientLayout.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                gettingStartedLayout.setVisibility(View.GONE);

            } else if (position == 1) {

                topItemsLayout.setVisibility(View.VISIBLE);
                blueGradientLayout.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                gettingStartedLayout.setVisibility(View.GONE);
            } else if (position == 2) {

                topItemsLayout.setVisibility(View.VISIBLE);
                blueGradientLayout.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                gettingStartedLayout.setVisibility(View.GONE);
            } else {
                animation = AnimationUtils.loadAnimation(OnBoardingActivity.this, R.anim.enter_from_left);
                letsGetStarted.setAnimation(animation);
                gettingStartedLayout.setAnimation(animation);
                topItemsLayout.setVisibility(View.INVISIBLE);
                blueGradientLayout.setVisibility(View.INVISIBLE);
                nextBtn.setVisibility(View.INVISIBLE);
                viewPager.setVisibility(View.INVISIBLE);
                gettingStartedLayout.setVisibility(View.VISIBLE);
                dotsLayout.setVisibility(View.GONE);

            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}