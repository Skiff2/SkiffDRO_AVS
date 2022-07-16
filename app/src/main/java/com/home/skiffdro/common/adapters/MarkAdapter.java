package com.home.skiffdro.common.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.home.skiffdro.R;
import com.home.skiffdro.common.Utils;
import com.home.skiffdro.models.MarkModel;

import java.util.List;


public class MarkAdapter extends RecyclerView.Adapter<MarkAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<MarkModel> items;

    public MarkAdapter(Context context, List<MarkModel> states) {
        this.items = states;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public MarkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.mark_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MarkAdapter.ViewHolder holder, int position) {
        MarkModel i = items.get(position);


        if (i.getFound())
            holder.Row.setBackgroundColor(Color.YELLOW);
        else
            holder.Row.setBackgroundColor(Color.parseColor("#DCF6F6"));

        if (i.getNameA().length() == 0)  //Если не указано - то и нафиг с пляжа
            holder.tv_A.setVisibility(View.GONE);
        else {
            holder.tv_A.setVisibility(View.VISIBLE);
            holder.tv_A.setText(i.getNameA() + " " + Utils.ValToPrint(i.getA()));
        }

        if (i.getNameB().length() == 0)  //Если не указано - то и нафиг с пляжа
            holder.tv_B.setVisibility(View.GONE);
        else {
            holder.tv_B.setVisibility(View.VISIBLE);
            holder.tv_B.setText(i.getNameB() + " " + Utils.ValToPrint(i.getB()));
        }

        if (i.getNameC().length() == 0)  //Если не указано - то и нафиг с пляжа
            holder.tv_C.setVisibility(View.GONE);
        else {
            holder.tv_C.setVisibility(View.VISIBLE);
            holder.tv_C.setText(i.getNameC() + " " + Utils.ValToPrint(i.getC()));
        }

        if (i.getName().length() == 0)  //Если не указано - то и нафиг с пляжа
            holder.tv_Name.setVisibility(View.GONE);
        else {
            holder.tv_Name.setVisibility(View.VISIBLE);
            holder.tv_Name.setText(i.getName());
        }

        holder.cmdNotify.setOnClickListener(view -> {
            i.setNotify(!i.getNotify());

            if (i.getNotify())
                holder.cmdNotify.setImageResource(R.drawable.notification_on);
            else
                holder.cmdNotify.setImageResource(R.drawable.ic_notifications_black_24dp);
        });

        holder.cmdDel.setOnClickListener( viev ->
        {
            items.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tv_A, tv_B, tv_C, tv_Name;
        final ImageButton cmdNotify, cmdDel;
        final ConstraintLayout Row;

        ViewHolder(View view){
            super(view);
            tv_Name = view.findViewById(R.id.tv_Name);

            tv_A = view.findViewById(R.id.tv_A);
            tv_B = view.findViewById(R.id.tv_B);
            tv_C = view.findViewById(R.id.tv_C);

            cmdNotify = view.findViewById(R.id.cmdNotify);
            cmdDel = view.findViewById(R.id.cmdDelMark);

            Row =  view.findViewById(R.id.cl_row);
        }
    }
}