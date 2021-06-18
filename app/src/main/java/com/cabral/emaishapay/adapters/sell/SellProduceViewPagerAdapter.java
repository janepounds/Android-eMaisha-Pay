//package com.cabral.emaishapay.adapters.sell;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentPagerAdapter;
//
//
//import com.cabral.emaishapay.fragments.sell_fragment.MarketPriceFragment;
//import com.cabral.emaishapay.fragments.sell_fragment.MyProduceFragment;
//
//import java.util.Objects;
//
//public class SellProduceViewPagerAdapter extends FragmentPagerAdapter {
//
//    public SellProduceViewPagerAdapter(@NonNull FragmentManager fm) {
//        super(fm);
//    }
//
//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        CharSequence title = null;
//
//        switch (position) {
//            case 0:
//                title = "My Produce";
//                break;
//            case 1:
//                title = "Market Price";
//                break;
//        }
//        return title;
//    }
//
//    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//        Fragment fragment = null;
//
//        switch (position) {
//            case 0:
//                fragment = new MyProduceFragment();
//                break;
//            case 1:
//                fragment = new MarketPriceFragment();
//                break;
//        }
//        return Objects.requireNonNull(fragment);
//    }
//
//    @Override
//    public int getCount() {
//        return 2;
//    }
//}
