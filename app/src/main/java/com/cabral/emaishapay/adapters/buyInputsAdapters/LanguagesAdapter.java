package com.cabral.emaishapay.adapters.buyInputsAdapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.fragments.buy_fragments.Languages;
import com.cabral.emaishapay.models.language_model.LanguageDetails;



import java.util.List;


/**
 * LanguagesAdapter is the adapter class of ListView holding List of Languages in Languages Fragment
 **/

public class LanguagesAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    
    private final Languages languages_fragment;
    private final List<LanguageDetails> languagesList;


    public LanguagesAdapter(Context context, List<LanguageDetails> languagesList, Languages languages_fragment) {
        this.context = context;
        this.languagesList = languagesList;
        this.languages_fragment = languages_fragment;

        layoutInflater = LayoutInflater.from(context);
    }
    
    
    //********** Returns the total number of items in the data set represented by this Adapter *********//
    
    @Override
    public int getCount() {
        return languagesList.size();
    }
    
    
    //********** Returns the item associated with the specified position in the data set *********//
    
    @Override
    public Object getItem(int position) {
        return languagesList.get(position);
    }
    
    
    //********** Returns the item id associated with the specified position in the list *********//
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    
    
    //********** Returns a View that displays the data at the specified position in the data set *********//
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_language, parent, false);

            holder = new ViewHolder();

            holder.languageName = convertView.findViewById(R.id.language_name);
            holder.languageIcon = convertView.findViewById(R.id.language_icon);
            holder.languageSelector = convertView.findViewById(R.id.cb_language);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


    
        holder.languageName.setText(languagesList.get(position).getName());
    
        Glide.with(context).load(ConstantValues.ECOMMERCE_URL+ languagesList.get(position).getImage()).into(holder.languageIcon);
        
        
        if (languagesList.get(position).getLanguagesId().equalsIgnoreCase(languages_fragment.getSelectedLanguageID())) {
            holder.languageSelector.setChecked(true);
            languages_fragment.setLastCheckedCB(holder.languageSelector);
            
        } else {
            holder.languageSelector.setChecked(false);
        }


        return convertView;
    }
    
    
    
    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/
    
    static class ViewHolder {
        TextView languageName;
        ImageView languageIcon;
        CheckBox languageSelector;

    }


}

