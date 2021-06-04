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


public class SliderAdapter extends PagerAdapter {
    private final Context mContext;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        mContext = context;

    }

    int[] images ={


            R.drawable.ic_onboarding_pay_img,
            R.drawable.ic_onboarding_credit_img,
            R.drawable.ic_onboarding_shop_img,
            R.drawable.onboarding_2
    };
    int[] imagesbg ={


            R.drawable.city,
            R.drawable.worlbgsmaller,
            R.drawable.city,
            R.drawable.city
    };
    int[] titles ={
           R.string.pay,
            R.string.get_credit,
            R.string.shop,
            R.string.actionAbout


    };
    int[] descriptions ={
            R.string.pay_description,
            R.string.get_credit_description,
            R.string.shop_description,
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

        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slides_layout,container,false);

        //to solve svg inflating error
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //hooks
        ImageView imageView = view.findViewById(R.id.slider_image);
        TextView desc = view.findViewById(R.id.slider_desc);
        TextView title = view.findViewById(R.id.slider_title);
        ImageView imageViewBg = view.findViewById(R.id.slider_image_bg);

        imageView.setImageResource(images[position]);
        imageViewBg.setImageResource(imagesbg[position]);
        desc.setText(descriptions[position]);
        title.setText(titles[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
