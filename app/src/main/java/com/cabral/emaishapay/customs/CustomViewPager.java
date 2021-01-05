package com.cabral.emaishapay.customs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {
    private View mCurrentView;


    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    //********** Measure the View and its content to determine the measured Width and the measured Height *********//

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int mode = View.MeasureSpec.getMode(heightMeasureSpec);

        // Unspecified means that the ViewPager is in a ScrollView WRAP_CONTENT.
        // At Most means that the ViewPager is not in a ScrollView WRAP_CONTENT.
        if (mode == View.MeasureSpec.UNSPECIFIED || mode == View.MeasureSpec.AT_MOST) {

            // super.onMeasure has to be called in the beginning so the ChildViews can be Initialized.
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            int height = 0;

            for (int i = 0; i < getChildCount(); i++) {
                // Get the Child at specified Position
                View child = getChildAt(i);
                // Measure the Specs of Child
                child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                // Measure the Height of the Child
                int h = measureViewHeight(child);

                // Compare with Height of ViewPager
                if (h > height) {
                    // Set the Height of ViewPager
                    height = h;
                }
            }

            // Set the Height of ViewPager based on the given measured Height
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        }

        // super.onMeasure has to be called again so the new Specs are treated as exact measurements
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }



    //********** Set the current View with given View and Requests to initiate Layout again *********//

    public void setCurrentView(View currentView) {
        mCurrentView = currentView;

        // Initiates Layout
        // This Method is called by a View on itself when it believes that iy can no longer fit within its current bounds
        requestLayout();
    }



    //********** Returns the measured Height of the given View *********//

    public int measureViewHeight(View view) {
        /*if (view == null)
            return 0;*/

        view.measure(0, 0);

        return view.getMeasuredHeight();
    }
}
