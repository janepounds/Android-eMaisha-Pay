package com.cabral.emaishapay.fragments.buy_fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.models.banner_model.BannerDetails;

import com.cabral.emaishapay.models.category_model.CategoryDetails;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.indicators.PagerIndicator;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.glide.slider.library.transformers.BaseTransformer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class BannerStyle1 extends Fragment implements BaseSliderView.OnSliderClickListener {


    View rootView;
    SliderLayout sliderLayout;
    PagerIndicator pagerIndicator;

    FragmentManager fragmentManager;

    List<BannerDetails> bannerImages = new ArrayList<>();
    List<CategoryDetails> allCategoriesList = new ArrayList<>();

    public BannerStyle1(List<BannerDetails> bannerImages, List<CategoryDetails> allCategoriesList) {
        this.bannerImages = bannerImages;
        this.allCategoriesList = allCategoriesList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.buy_inputs_banner_style_1, container, false);

        sliderLayout = rootView.findViewById(R.id.banner_slider);
        pagerIndicator = rootView.findViewById(R.id.banner_slider_indicator);


        fragmentManager = getFragmentManager();

        setupBannerSlider(bannerImages);

        return rootView;
    }

    private void setupBannerSlider(final List<BannerDetails> bannerImages) {

        // Initialize new LinkedHashMap<ImageName, ImagePath>
        final LinkedHashMap<String, String> slider_covers = new LinkedHashMap<>();


        for (int i = 0; i < bannerImages.size(); i++) {
            // Get bannerDetails at given Position from bannerImages List
            BannerDetails bannerDetails = bannerImages.get(i);

            // Put Image's Name and URL to the HashMap slider_covers
            slider_covers.put
                    (
                            "Image" + bannerDetails.getTitle(),
                            ConstantValues.ECOMMERCE_URL + bannerDetails.getImage()
                    );
        }


        for (String name : slider_covers.keySet()) {
            // Initialize DefaultSliderView
            final DefaultSliderView defaultSliderView = new DefaultSliderView(getContext());


            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop()
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder);

            // Set Attributes(Name, Image, Type etc) to DefaultSliderView
            defaultSliderView
                    .description(name)
                    .image(slider_covers.get(name))
                    .setRequestOption(requestOptions)
                    .setOnSliderClickListener(this);


            // Add DefaultSliderView to the SliderLayout
            sliderLayout.addSlider(defaultSliderView);
        }

        // Set PresetTransformer type of the SliderLayout
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);


        // Check if the size of Images in the Slider is less than 2
        if (slider_covers.size() < 2) {
            // Disable PagerTransformer
            sliderLayout.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float v) {
                }
            });

            // Hide Slider PagerIndicator
            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);

        } else {
            // Set custom PagerIndicator to the SliderLayout
            sliderLayout.setCustomIndicator(pagerIndicator);
            // Make PagerIndicator Visible
            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
        }

    }


    @Override
    public void onSliderClick(BaseSliderView slider) {
        int position = sliderLayout.getCurrentPosition();
        String url = bannerImages.get(position).getUrl();
        String type = bannerImages.get(position).getType();

        if (type.equalsIgnoreCase("product")) {
            if (!TextUtils.isEmpty(url)) {
                // Get Product Info
                Bundle bundle = new Bundle();
                bundle.putInt("itemID", Integer.parseInt(url));

                // Navigate to Product_Description of selected Product
                Fragment fragment = new Product_Description();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.main_fragment_container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
            }

        } else if (type.equalsIgnoreCase("category")) {
            if (!TextUtils.isEmpty(url)) {
                int categoryID = 0;
                String categoryName = "";

                for (int i = 0; i < allCategoriesList.size(); i++) {
                    if (url.equalsIgnoreCase(allCategoriesList.get(i).getId())) {
                        categoryName = allCategoriesList.get(i).getName();
                        categoryID = Integer.parseInt(allCategoriesList.get(i).getId());
                    }
                }

                // Get OrderProductCategory Info
                Bundle bundle = new Bundle();
                bundle.putInt("CategoryID", categoryID);
                bundle.putString("CategoryName", categoryName);

                // Navigate to Products Fragment
                Fragment fragment = new Products();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.main_fragment_container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
            }

        } else if (type.equalsIgnoreCase("deals")) {
            Bundle bundle = new Bundle();
            bundle.putString("sortBy", "special");

            // Navigate to Products Fragment
            Fragment fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(R.id.main_fragment_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

        } else if (type.equalsIgnoreCase("top seller")) {
            Bundle bundle = new Bundle();
            bundle.putString("sortBy", "top seller");

            // Navigate to Products Fragment
            Fragment fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(R.id.main_fragment_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

        } else if (type.equalsIgnoreCase("most liked")) {
            Bundle bundle = new Bundle();
            bundle.putString("sortBy", "most liked");

            // Navigate to Products Fragment
            Fragment fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(R.id.main_fragment_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

        }
    }
}
