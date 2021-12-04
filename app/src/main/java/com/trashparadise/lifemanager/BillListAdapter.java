package com.trashparadise.lifemanager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.trashparadise.lifemanager.ui.bills.BillEditActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class BillListAdapter extends RecyclerView.Adapter<BillListAdapter.ViewHolder> {
    private ArrayList<Bill> localDataSet;
    private Context context;
    private DecimalFormat decimalFormat;


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ConstraintLayout layout;
        private final TextView textViewDate;
        private final TextView textViewAmount;

        public ViewHolder(View view){
            super(view);
            layout=(ConstraintLayout)view.findViewById(R.id.layout);
            textViewDate=(TextView)view.findViewById(R.id.textView_date);
            textViewAmount=(TextView)view.findViewById(R.id.textView_amount);
        }

        public TextView getTextViewDate() {
            return textViewDate;
        }

        public TextView getTextViewAmount() {
            return textViewAmount;
        }
    }
    public BillListAdapter(ArrayList<Bill> dataSet, Context context){
        localDataSet=dataSet;
        this.context=context;
        decimalFormat=new DecimalFormat(context.getString(R.string.amount_decimal_format));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_bill, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(view);

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BillEditActivity.class);
                intent.putExtra("uuid",localDataSet.get(viewHolder.getBindingAdapterPosition()).getUuid());
                context.startActivity(intent);
            }
        });
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextViewAmount().setText(decimalFormat.format(localDataSet.get(position).getAmount()));
        viewHolder.getTextViewDate().setText(localDataSet.get(position).getDate().toString());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
