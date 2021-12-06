package com.trashparadise.lifemanager.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.trashparadise.lifemanager.Bill;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.constants.TypeRes;
import com.trashparadise.lifemanager.ui.bills.BillAuditActivity;
import com.trashparadise.lifemanager.ui.bills.BillEditActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BillListAdapter extends RecyclerView.Adapter<BillListAdapter.ViewHolder> {
    private ArrayList<Bill> localDataSet;
    private Context context;
    private LifeManagerApplication application;
    private DecimalFormat decimalFormat;
    private SimpleDateFormat dateFormatMonth;
    private SimpleDateFormat dateFormatTime;
    private Integer form;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layout;
        private final TextView textViewDate;
        private final TextView textViewAmount;
        private final TextView textViewType;
        private final ImageView imageViewType;

        public ViewHolder(View view) {
            super(view);
            layout = (ConstraintLayout) view.findViewById(R.id.layout);
            textViewDate = (TextView) view.findViewById(R.id.textView_date);
            textViewAmount = (TextView) view.findViewById(R.id.textView_amount);
            textViewType = (TextView) view.findViewById(R.id.textView_type);
            imageViewType = (ImageView) view.findViewById(R.id.imageView_type);
        }

        public ConstraintLayout getLayout() {
            return layout;
        }

        public TextView getTextViewType() {
            return textViewType;
        }

        public ImageView getImageViewType() {
            return imageViewType;
        }

        public TextView getTextViewDate() {
            return textViewDate;
        }

        public TextView getTextViewAmount() {
            return textViewAmount;
        }
    }


    public BillListAdapter(Context context, Integer form) {
        this.form = form;
        this.context = context;
        application = (LifeManagerApplication) ((AppCompatActivity) context).getApplication();
        updateDataSet();
        decimalFormat = new DecimalFormat(context.getString(R.string.amount_decimal_format));
        dateFormatMonth = new SimpleDateFormat(context.getString(R.string.date_format_month));
        dateFormatTime = new SimpleDateFormat(context.getString(R.string.date_format_time));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bill, viewGroup, false);
                break;
            case 1:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_month, viewGroup, false);
                break;
        }

        ViewHolder viewHolder = new ViewHolder(view);

        switch (viewType) {
            case 0:
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, BillEditActivity.class);
                        intent.putExtra("uuid", localDataSet.get(viewHolder.getBindingAdapterPosition()).getUuid());
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
                        Dialog dialogFragment = builder.create();
                        dialogFragment.show();
                        return false;
                    }
                });
                break;
            case 1:
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, BillAuditActivity.class);
                        intent.putExtra("uuid", localDataSet.get(viewHolder.getBindingAdapterPosition()).getUuid());
                        context.startActivity(intent);
                    }
                });
                break;
        }
        return viewHolder;

    }

    public void callUpdateDataSet(Integer form){
        this.form=form;
        updateDataSet();
    }
    private void updateDataSet() {
        ArrayList<Bill> allDataSet = application.getBillList();
        localDataSet = new ArrayList<>();
        if (form != -1) {
            for (int i = 0; i < allDataSet.size(); ++i) {
                if (allDataSet.get(i).getForm().equals(form)) {
                    localDataSet.add(allDataSet.get(i));
                }
            }
        } else {
            localDataSet = allDataSet;
        }

        if (localDataSet.size() > 0) {
            localDataSet.add(0, new Bill(new BigDecimal("0"), localDataSet.get(0).getDate(), "", -1, ""));
        }

        for (int i = 1; i < localDataSet.size(); ++i) {
            Calendar pre = Calendar.getInstance();
            pre.setTime(localDataSet.get(i - 1).getDate());
            Calendar curr = Calendar.getInstance();
            curr.setTime(localDataSet.get(i).getDate());
            if (pre.get(Calendar.MONTH) != curr.get(Calendar.MONTH) || pre.get(Calendar.YEAR) != curr.get(Calendar.YEAR)) {
                localDataSet.add(i, new Bill(new BigDecimal("0"), localDataSet.get(i).getDate(), "", -1, ""));
                i += 1;
            }
        }
        BillListAdapter.this.notifyDataSetChanged();
    }

    public boolean isAudit(int position) {
        return localDataSet.get(position).getForm().equals(-1);
    }

    @Override
    public int getItemViewType(int position) {
        return isAudit(position) ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Bill bill = localDataSet.get(position);
        if (isAudit(position)) {
            viewHolder.getTextViewDate().setText(dateFormatMonth.format(bill.getDate()));
        } else {
            Integer form = bill.getForm();
            viewHolder.getTextViewDate().setText(dateFormatTime.format(bill.getDate()));
            viewHolder.getTextViewType().setText(localDataSet.get(position).getType());
            viewHolder.getImageViewType().setImageResource(TypeRes.ICONS[form][TypeRes.getId(bill.getForm(), bill.getType())]);
            viewHolder.getTextViewAmount().setTextColor(context.getResources().getColor(TypeRes.COLOR[bill.getForm()]));
            if (bill.getForm() == 0) {
                viewHolder.getTextViewAmount().setText("-" + decimalFormat.format(bill.getAmount()));
            } else {
                viewHolder.getTextViewAmount().setText("+" + decimalFormat.format(bill.getAmount()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
