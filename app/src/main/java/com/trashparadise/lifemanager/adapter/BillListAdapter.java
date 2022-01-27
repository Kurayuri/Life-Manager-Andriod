package com.trashparadise.lifemanager.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.bean.Bill;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.constants.BillTypeRes;
import com.trashparadise.lifemanager.ui.bills.BillAuditActivity;
import com.trashparadise.lifemanager.ui.bills.BillCheckActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeSet;

public class BillListAdapter extends RecyclerView.Adapter<BillListAdapter.ViewHolder> {
    private ArrayList<Bill> localDataSet;
    private Context context;
    private LifeManagerApplication application;
    private DataManager dataManager;
    private DecimalFormat decimalFormat;
    private SimpleDateFormat dateFormatMonth;
    private SimpleDateFormat dateFormatTime;
    private Integer form;
    private Boolean auditOn;
    private Boolean slimOn;
    private int itemBillLayoutStyle;

    private Boolean multiChoice;
    private TreeSet<Integer> chosen;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layout;
        private final TextView textViewDate;
        private final TextView textViewAmount;
        private final TextView textViewType;
        private final ImageView imageViewType;
        private final CheckBox checkBox;


        public ViewHolder(View view) {
            super(view);
            layout = (ConstraintLayout) view.findViewById(R.id.layout);
            textViewDate = (TextView) view.findViewById(R.id.textView_date);
            textViewAmount = (TextView) view.findViewById(R.id.textView_amount);
            textViewType = (TextView) view.findViewById(R.id.textView_type);
            imageViewType = (ImageView) view.findViewById(R.id.imageView_type);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
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

        public CheckBox getCheckBox() {
            return checkBox;
        }

    }


    public BillListAdapter(Context context, Integer form, Boolean auditOn, Boolean slimOn) {
        this.form = form;
        this.context = context;
        this.auditOn = auditOn;
        this.slimOn = slimOn;
        itemBillLayoutStyle = slimOn ? R.layout.item_bill_slim : R.layout.item_bill;

        multiChoice = false;
        chosen = new TreeSet<>();

        application = (LifeManagerApplication) ((AppCompatActivity) context).getApplication();
        dataManager = DataManager.getInstance();
        updateDataSet();
        decimalFormat = new DecimalFormat(context.getString(R.string.amount_decimal_format));
        dateFormatMonth = new SimpleDateFormat(context.getString(R.string.date_format_month));
        dateFormatTime = new SimpleDateFormat(context.getString(R.string.date_format_time));
    }


    public void callUpdateDataSet(Integer form) {
        this.form = form;
        updateDataSet();
    }

    private void updateDataSet() {
        ArrayList<Bill> allDataSet = dataManager.getBillList();
        localDataSet = new ArrayList<>();
        chosen.clear();

        if (form != -1) {
            for (int i = 0; i < allDataSet.size(); ++i) {
                if (allDataSet.get(i).getForm().equals(form)) {
                    localDataSet.add(allDataSet.get(i));
                }
            }
        } else {
            localDataSet = allDataSet;
        }

        if (auditOn) {
            if (localDataSet.size() > 0) {
                localDataSet.add(0, new Bill(new BigDecimal("0"), localDataSet.get(0).getDate(), "", -1, ""));
            }

            for (int i = 1; i < localDataSet.size(); ++i) {
                Calendar pre = Calendar.getInstance();
                pre.setTime(localDataSet.get(i - 1).getDate().getTime());
                Calendar curr = Calendar.getInstance();
                curr.setTime(localDataSet.get(i).getDate().getTime());
                if (pre.get(Calendar.MONTH) != curr.get(Calendar.MONTH) || pre.get(Calendar.YEAR) != curr.get(Calendar.YEAR)) {
                    localDataSet.add(i, new Bill(new BigDecimal("0"), localDataSet.get(i).getDate(), "", -1, ""));
                    i += 1;
                }
            }
        }
        BillListAdapter.this.notifyDataSetChanged();
    }

    public boolean isAudit(int position) {
        return localDataSet.get(position).getForm().equals(-1);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(itemBillLayoutStyle, viewGroup, false);
                break;
            case 1:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bill_month, viewGroup, false);
                break;
        }

        ViewHolder viewHolder = new ViewHolder(view);
        switch (viewType) {
            case 0:
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, BillCheckActivity.class);
                        intent.putExtra("uuid", localDataSet.get(viewHolder.getBindingAdapterPosition()).getUuid());
                        context.startActivity(intent);
                    }
                });
                if (!slimOn) {
                    viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                chosen.add(viewHolder.getBindingAdapterPosition());
                            } else {
                                chosen.remove(viewHolder.getBindingAdapterPosition());
                            }
                        }
                    });

                    viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(R.string.delete_confirm_text)
                                    .setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String uuid = localDataSet.get(viewHolder.getBindingAdapterPosition()).getUuid();

                                            dataManager.delBill(uuid);
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
                }
                break;
            case 1:
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, BillAuditActivity.class);
                        intent.putExtra("date", localDataSet.get(viewHolder.getBindingAdapterPosition()).getDate().getTime().getTime());
                        context.startActivity(intent);
                    }
                });
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return isAudit(position) ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Bill bill = localDataSet.get(position);
        if (isAudit(position)) {
            viewHolder.getTextViewDate().setText(dateFormatMonth.format(bill.getDate().getTime()));
        } else {
            Integer form = bill.getForm();
            viewHolder.getTextViewDate().setText(dateFormatTime.format(bill.getDate().getTime()));
            viewHolder.getTextViewType().setText(localDataSet.get(position).getType());
            viewHolder.getImageViewType().setImageResource(BillTypeRes.ICONS[form][BillTypeRes.getId(bill.getForm(), bill.getType())]);
            viewHolder.getTextViewAmount().setTextColor(context.getColor(BillTypeRes.COLOR[bill.getForm()]));
            if (bill.getForm() == Bill.EXPAND) {
                viewHolder.getTextViewAmount().setText("-" + decimalFormat.format(bill.getAmount()));
            } else {
                viewHolder.getTextViewAmount().setText("+" + decimalFormat.format(bill.getAmount()));
            }
            if (!slimOn) {
                viewHolder.getCheckBox().setVisibility(multiChoice ? View.VISIBLE : View.GONE);
                viewHolder.getCheckBox().setChecked(chosen.contains(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void onMultiChoice() {
        if (!multiChoice) {
            chosen.clear();
        }
        multiChoice = !multiChoice;
        notifyDataSetChanged();
    }

    public TreeSet<String> onMultiDelete() {
        TreeSet<String> chosenUuid = new TreeSet<>();
        for (Integer position : chosen) {
            chosenUuid.add(localDataSet.get(position).getUuid());
        }
        return chosenUuid;
    }

    public void onMultiReverse() {
        TreeSet<Integer> tmp = new TreeSet<>();
        for (int i = 0; i < localDataSet.size(); ++i) {
            tmp.add(i);
        }
        if (chosen.size() > 0) {
            for (int i = chosen.first(); i <= chosen.last(); ++i)
                tmp.remove(i);
        }
        chosen = tmp;
        notifyItemRangeChanged(0,localDataSet.size());
    }

    public void onMultiInterval() {
        for (int i = chosen.first(); i <= chosen.last(); ++i)
            chosen.add(i);
        notifyItemRangeChanged(chosen.first(), chosen.last());
    }

}
