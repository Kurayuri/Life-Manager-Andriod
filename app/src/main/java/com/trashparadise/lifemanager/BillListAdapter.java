package com.trashparadise.lifemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.trashparadise.lifemanager.ui.bills.BillEditActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BillListAdapter extends RecyclerView.Adapter<BillListAdapter.ViewHolder> {
    private ArrayList<Bill> localDataSet;
    private Context context;
    private LifeManagerApplication application;
    private DecimalFormat decimalFormat;
    private SimpleDateFormat dateFormatMonth;
    private SimpleDateFormat dateFormatTime;


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

    public BillListAdapter(Context context){
        this.context=context;
        application=(LifeManagerApplication) ((AppCompatActivity)context).getApplication();
        updateDataSet();
        decimalFormat=new DecimalFormat(context.getString(R.string.amount_decimal_format));
        dateFormatMonth=new SimpleDateFormat(context.getString(R.string.date_format_month));
        dateFormatTime=new SimpleDateFormat(context.getString(R.string.date_format_time));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view=null;
        switch (viewType){
            case 0:
                view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bill, viewGroup, false);
                break;
            case 1:
                view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_month, viewGroup, false);
                break;
        }

        ViewHolder viewHolder=new ViewHolder(view);

        switch (viewType){
            case 0:
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, BillEditActivity.class);
                        intent.putExtra("uuid",localDataSet.get(viewHolder.getBindingAdapterPosition()).getUuid());
                        context.startActivity(intent);
                    }
                });
                viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(R.string.delete_confirm_text)
                                .setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        application.delBill(localDataSet.get(viewHolder.getBindingAdapterPosition()).getUuid());
                                        updateDataSet();
                                    }
                                })
                                .setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        Dialog dialogFragment=builder.create();
                        dialogFragment.show();
                        return false;
                    }
                });
                break;
            case 1:
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, BillEditActivity.class);
                        intent.putExtra("uuid",localDataSet.get(viewHolder.getBindingAdapterPosition()).getUuid());
                        context.startActivity(intent);
                    }
                });
                break;
        }
        return viewHolder;

    }

    public void updateDataSet(){
        localDataSet=application.getBillList();
        if (localDataSet.size()>0){
            localDataSet.add(0,new Bill(new BigDecimal("-1"),localDataSet.get(0).getDate(),"",""));
        }

        for (int i=1;i<localDataSet.size();++i){
            Calendar pre=Calendar.getInstance();
            pre.setTime(localDataSet.get(i-1).getDate());
            Calendar curr=Calendar.getInstance();
            curr.setTime(localDataSet.get(i).getDate());
            if (pre.get(Calendar.MONTH)!=curr.get(Calendar.MONTH) || pre.get(Calendar.YEAR)!=curr.get(Calendar.YEAR)){
                localDataSet.add(i,new Bill(new BigDecimal("-1"),localDataSet.get(i).getDate(),"",""));
                i+=1;
            }
        }
        BillListAdapter.this.notifyDataSetChanged();
    }

    public boolean isAudit(int position){
        return localDataSet.get(position).getAmount().equals(new BigDecimal("-1"));
    }

    @Override
    public int getItemViewType(int position) {
        return isAudit(position)?1:0;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (isAudit(position)) {
            viewHolder.getTextViewDate().setText(dateFormatMonth.format(localDataSet.get(position).getDate()));
        }else {
            viewHolder.getTextViewAmount().setText(decimalFormat.format(localDataSet.get(position).getAmount()));
            viewHolder.getTextViewDate().setText(dateFormatTime.format(localDataSet.get(position).getDate()));
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
