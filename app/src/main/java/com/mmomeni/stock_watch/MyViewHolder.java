package com.mmomeni.stock_watch;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView symbol; // we can make these three public too, but we should never make them private
    TextView company;
    TextView price;
    TextView cPrice;
    TextView cPercentage;

    MyViewHolder(View view){ //this objects will hold references to the items in our notes_list layout
        super(view);
        symbol = view.findViewById(R.id.symbol);
        company = view.findViewById(R.id.company);
        price = view.findViewById(R.id.price);
        cPrice = view.findViewById(R.id.price_change);
        cPercentage = view.findViewById(R.id.change_percentage);
    }
}
