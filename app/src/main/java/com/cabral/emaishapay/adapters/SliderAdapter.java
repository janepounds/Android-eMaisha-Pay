package com.cabral.emaishapay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.cabral.emaishapay.R;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {
    private Context mContext;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        mContext = context;

    }

    int images[] ={


            R.drawable.ic_bag,
            R.drawable.onboarding_2,
            R.drawable.ic_truck,
            R.drawable.onboarding_2
    };
    int descriptions[]={
           R.string.add_category,
            R.string.accept_payment,
            R.string.actionRateApp,
            R.string.actionAbout


    };

    @Override
    public int getCount() {
        return descriptions.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slides_layout,container,false);

        //to solve svg inflating error
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //hooks
        ImageView imageView = view.findViewById(R.id.slider_image);
        TextView desc = view.findViewById(R.id.slider_desc);
        ImageView imageViewBg = view.findViewById(R.id.slider_image_bg);

        imageView.setImageResource(images[position]);
        desc.setText(descriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
