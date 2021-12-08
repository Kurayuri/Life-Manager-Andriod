package com.trashparadise.lifemanager.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.trashparadise.lifemanager.Work;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;


import com.trashparadise.lifemanager.constants.RepeatRes;
import com.trashparadise.lifemanager.ui.works.WorkEditActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class WorkListAdapter extends RecyclerView.Adapter<WorkListAdapter.ViewHolder> {
    private ArrayList<Work> localDataSet;
    private Context context;
    private LifeManagerApplication application;
    private SimpleDateFormat dateFormatMonth;
    private SimpleDateFormat dateFormatTime;
    private Integer form;
    private Boolean auditOn;
    private Boolean slimOn;
    private int itemWorkLayout;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layout;
        private final TextView textViewDate;
        private final TextView textViewNote;
        private final TextView textViewTitle;
        private final TextView textViewRepeat;
        private final ImageView imageViewType;

        public TextView getTextViewRepeat() {
            return textViewRepeat;
        }

        public ViewHolder(View view) {
            super(view);
            layout = (ConstraintLayout) view.findViewById(R.id.layout);
            textViewDate = (TextView) view.findViewById(R.id.textView_date);
            textViewNote = (TextView) view.findViewById(R.id.textView_note);
            textViewTitle = (TextView) view.findViewById(R.id.textView_title);
            textViewRepeat = (TextView) view.findViewById(R.id.textView_repeat);

            imageViewType = (ImageView) view.findViewById(R.id.imageView_type);
        }

        public ConstraintLayout getLayout() {
            return layout;
        }

        public TextView getTextViewTitle() {
            return textViewTitle;
        }

        public ImageView getImageViewType() {
            return imageViewType;
        }

        public TextView getTextViewDate() {
            return textViewDate;
        }

        public TextView getTextViewNote() {
            return textViewNote;
        }
    }


    public WorkListAdapter(Context context, Integer form, Boolean auditOn, Boolean slimOn) {
        this.form = form;
        this.context = context;
        this.auditOn = auditOn;
        this.slimOn = slimOn;
        itemWorkLayout = slimOn ? R.layout.item_work_slim : R.layout.item_work;

        application = (LifeManagerApplication) ((AppCompatActivity) context).getApplication();
        updateDataSet();
        dateFormatMonth = new SimpleDateFormat(context.getString(R.string.date_format_month));
        dateFormatTime = new SimpleDateFormat(context.getString(R.string.date_format_time));
    }


    public void callUpdateDataSet(Integer form) {
        this.form = form;
        updateDataSet();
    }

    private void updateDataSet() {
        ArrayList<Work> allDataSet = application.getWorkList();
        localDataSet = new ArrayList<>();
        if (form != Work.ALL) {
            for (int i = 0; i < allDataSet.size(); ++i) {
                if (allDataSet.get(i).getForm().equals(form)) {
                    localDataSet.add(allDataSet.get(i));
                }
            }
            if (form == Work.TODO){
                Collections.reverse(localDataSet);
            }
        } else {
            localDataSet = allDataSet;
        }

        if (auditOn) {
            if (localDataSet.size() > 0) {
                localDataSet.add(0, new Work("", localDataSet.get(0).getDate(), 0, -1, ""));
            }

            for (int i = 1; i < localDataSet.size(); ++i) {
                Calendar pre =(Calendar)localDataSet.get(i - 1).getDate().clone();
                Calendar curr=(Calendar)localDataSet.get(i ).getDate().clone();
                if (pre.get(Calendar.MONTH) != curr.get(Calendar.MONTH) || pre.get(Calendar.YEAR) != curr.get(Calendar.YEAR)) {
                    localDataSet.add(i, new Work("", localDataSet.get(i).getDate(), 0, -1, ""));
                    i += 1;
                }
            }
        }
        WorkListAdapter.this.notifyDataSetChanged();
    }

    public boolean isAudit(int position) {
        return localDataSet.get(position).getForm().equals(-1);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(itemWorkLayout, viewGroup, false);
                break;
            case 1:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_work_month, viewGroup, false);
                break;
        }

        ViewHolder viewHolder = new ViewHolder(view);

        switch (viewType) {
            case 0:
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(context, WorkCheckActivity.class);
                        Intent intent = new Intent(context, WorkEditActivity.class);
                        intent.putExtra("uuid", localDataSet.get(viewHolder.getBindingAdapterPosition()).getUuid());
                        context.startActivity(intent);
                    }
                });
                if (!slimOn) {
                    viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(R.string.delete_confirm_text)
                                    .setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            application.delWork(localDataSet.get(viewHolder.getBindingAdapterPosition()).getUuid());
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
                        Intent intent = new Intent(context, WorkEditActivity.class);
                        intent.putExtra("date", localDataSet.get(viewHolder.getBindingAdapterPosition()).getDate().getTime().getTime());
                        intent.putExtra("uuid","");
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
        Work work = localDataSet.get(position);
//        Log.e("get",RepeatRes.getStringId(work.getRepeat())+"sad"+work.getRepeat());
        if (isAudit(position)) {
            viewHolder.getTextViewDate().setText(dateFormatMonth.format(work.getDate().getTime()));
        } else {
            Integer form = work.getForm();
            viewHolder.getTextViewDate().setText(dateFormatTime.format(work.getDate().getTime()));
            viewHolder.getTextViewTitle().setText(work.getTitle());
            viewHolder.getTextViewNote().setText(work.getNote());

            viewHolder.getTextViewRepeat().setText(application.getResources().getString(RepeatRes.getStringId(work.getRepeat())));
            viewHolder.getTextViewDate().setTextColor(application.getResources().getColor(form==0?R.color.colorTextRed:R.color.colorTextBlue));
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
