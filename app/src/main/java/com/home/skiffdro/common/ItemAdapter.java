package com.home.skiffdro.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.home.skiffdro.R;
import com.home.skiffdro.models.ItemModel;

import java.util.List;


public class ItemAdapter  extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<ItemModel> items;

    public ItemAdapter(Context context, List<ItemModel> states) {
        this.items = states;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        ItemModel i = items.get(position);


        if (i.getFoud())
            holder.Row.setBackgroundColor(Color.YELLOW);
        else {
            if (i.getNN() % 2 == 0)
                holder.Row.setBackgroundColor(Color.parseColor("#E3FDFD"));
            else
                holder.Row.setBackgroundColor(Color.parseColor("#DCF6F6"));
        }

        //if (i.getNN() % 2 == 0) holder.Row.setBackgroundColor(R.color.first); else holder.Row.setBackgroundColor(R.color.second);
        holder.tv_X.setText("X: "+ formatDouble(i.getX()));
        holder.tv_S.setText(i.getNameS() + ": " + formatDouble(i.getS()));
        holder.tv_NN.setText(String.valueOf(i.getNN()) + ")");
        holder.ch_Check.setChecked(i.getCheck());
        holder.ch_Check.setScaleX(1.5f);
        holder.ch_Check.setScaleY(1.5f);
        holder.ch_Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.setCheck(!i.getCheck());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tv_X, tv_S, tv_NN;
        final CheckBox ch_Check;
        final ConstraintLayout Row;

        ViewHolder(View view){
            super(view);
            tv_X = view.findViewById(R.id.tv_X);
            tv_S = view.findViewById(R.id.tv_S);
            tv_NN = view.findViewById(R.id.tv_NN);
            ch_Check = view.findViewById(R.id.ch_Check);
            Row =  view.findViewById(R.id.cl_row);
        }
    }

    public static String formatDouble(Double valueToFormat) {
        long rounded = Math.round(valueToFormat*100);
        return String.valueOf(rounded/100.0);
    }
}