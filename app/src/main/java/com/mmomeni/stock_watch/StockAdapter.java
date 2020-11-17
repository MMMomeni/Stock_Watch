package com.mmomeni.stock_watch;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.GenericLifecycleObserver;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainAct;

    StockAdapter(List<Stock> sList, MainActivity ma) {
        this.stockList = sList;
        mainAct = ma;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_box, parent, false);

        itemView.setOnClickListener(mainAct); // means that main activity owns the onClickListener
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Stock s = stockList.get(position);

        holder.symbol.setText(s.getSymbol());
        holder.company.setText(s.getCompany());
        holder.price.setText(s.getPrice() + "");

        if (s.getPChange() > 0) {
            holder.cPrice.setText("▲ " + s.getPChange());
            holder.company.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.symbol.setTextColor(Color.GREEN);
            holder.cPrice.setTextColor(Color.GREEN);
            holder.cPercentage.setTextColor(Color.GREEN);
        }
        else if (s.getPChange() == 0.0){
            holder.cPrice.setText("      " + s.getPChange());
            holder.company.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.symbol.setTextColor(Color.GREEN);
            holder.cPrice.setTextColor(Color.GREEN);
            holder.cPercentage.setTextColor(Color.GREEN);
        }
        else{
            holder.cPrice.setText("▼ " + s.getPChange());
            holder.company.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.symbol.setTextColor(Color.RED);
            holder.cPrice.setTextColor(Color.RED);
            holder.cPercentage.setTextColor(Color.RED);
        }

        holder.cPercentage.setText(String.format("(%.3f%%)", s.getCPercentage()));
        //double percent means that we want a percent symbol in our output



    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
