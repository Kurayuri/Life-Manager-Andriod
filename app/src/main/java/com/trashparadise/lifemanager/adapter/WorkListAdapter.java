package com.trashparadise.lifemanager.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;


import com.trashparadise.lifemanager.constants.RepeatRes;
import com.trashparadise.lifemanager.ui.works.WorkCheckActivity;
import com.trashparadise.lifemanager.util.SoundPoolUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.TreeSet;

public class WorkListAdapter extends RecyclerView.Adapter<WorkListAdapter.ViewHolder> {
    private ArrayList<Work> localDataSet;
    private Context context;
    private LifeManagerApplication application;
    private DataManager dataManager;
    private SimpleDateFormat dateFormatMonth;
    private SimpleDateFormat dateFormatTime;
    private Integer form;
    private Boolean auditOn;
    private Boolean slimOn;
    private Boolean openOn;
    private Boolean multiChoice;
    private TreeSet<Integer> chosen;

    private Vibrator vibrator;
    private SoundPoolUtils soundPoolUtils;
    private int itemWorkLayoutStyle;

    private int animationCount;


    private WorkListAdapter.OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(String uuid);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layout;
        private final TextView textViewDate;
        private final TextView textViewNote;
        private final TextView textViewTitle;
        private final TextView textViewRepeat;
        private final ImageView imageViewForm;
        private final CheckBox checkBox;

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
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);

            imageViewForm = (ImageView) view.findViewById(R.id.imageView_form);
        }

        public ConstraintLayout getLayout() {
            return layout;
        }

        public TextView getTextViewTitle() {
            return textViewTitle;
        }

        public ImageView getImageViewForm() {
            return imageViewForm;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public TextView getTextViewDate() {
            return textViewDate;
        }

        public TextView getTextViewNote() {
            return textViewNote;
        }
    }

    public WorkListAdapter(Context context, Integer form, Boolean auditOn, Boolean slimOn, Boolean openOn, OnItemClickListener listener) {
        this.form = form;
        this.context = context;
        this.auditOn = auditOn;
        this.slimOn = slimOn;
        this.openOn = openOn;
        this.listener = listener;
        application = (LifeManagerApplication) ((AppCompatActivity) context).getApplication();
        dataManager = DataManager.getInstance();

        multiChoice = false;
        chosen = new TreeSet<>();

        updateDataSet();
        dateFormatMonth = new SimpleDateFormat(context.getString(R.string.date_format_month));
        dateFormatTime = new SimpleDateFormat(context.getString(R.string.date_format_time));
        itemWorkLayoutStyle = slimOn ? R.layout.item_work_slim : R.layout.item_work;
        vibrator = (Vibrator) application.getSystemService(Context.VIBRATOR_SERVICE);
        soundPoolUtils = SoundPoolUtils.getInstance(context);


        animationCount = 0;
    }


    public void callUpdateDataSet(Integer form) {
        this.form = form;
        updateDataSet();
    }

    private void updateDataSet() {
        ArrayList<Work> allDataSet = dataManager.getWorkList();
        localDataSet = new ArrayList<>();
        chosen.clear();

        if (form != Work.ALL) {
            for (int i = 0; i < allDataSet.size(); ++i) {
                if (allDataSet.get(i).getForm().equals(form)) {
                    localDataSet.add(allDataSet.get(i));
                }
            }
            if (form == Work.TODO) {
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
                Calendar pre = (Calendar) localDataSet.get(i - 1).getDate().clone();
                Calendar curr = (Calendar) localDataSet.get(i).getDate().clone();
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
                view = LayoutInflater.from(viewGroup.getContext()).inflate(itemWorkLayoutStyle, viewGroup, false);
                break;
            case 1:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_work_month, viewGroup, false);
                break;
        }

        ViewHolder viewHolder = new ViewHolder(view);

        if (openOn) {
            //open mode

            switch (viewType) {
                case 0:
                    viewHolder.imageViewForm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));


                            int currForm = localDataSet.get(viewHolder.getBindingAdapterPosition()).getForm();
                            Animation animationScaleRotateOut = AnimationUtils.loadAnimation(context, R.anim.anim_scale_rotate_out);
                            Animation animationScaleRotateIn = AnimationUtils.loadAnimation(context, R.anim.anim_scale_rotate_in);
                            Animation animationTranslateLeft = AnimationUtils.loadAnimation(context, R.anim.anim_translate_left);
                            Animation animationRotateShake = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_shake);


                            switch (currForm) {
                                case Work.DONE:
                                    soundPoolUtils.play(2);
                                    viewHolder.imageViewForm.startAnimation(animationRotateShake);
                                    break;
                                case Work.TODO:
                                    soundPoolUtils.play(1);
                                    switch (form) {
                                        case Work.TODO:
                                            viewHolder.imageViewForm.startAnimation(animationScaleRotateOut);
                                            animationScaleRotateOut.setAnimationListener(new Animation.AnimationListener() {
                                                @Override
                                                public void onAnimationStart(Animation animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animation animation) {
                                                    viewHolder.imageViewForm.setColorFilter(application.getColor(R.color.iconDone));
                                                    viewHolder.textViewDate.setTextColor(application.getColor(R.color.iconDone));
                                                    viewHolder.imageViewForm.setImageResource(R.drawable.ic_done);
                                                    viewHolder.imageViewForm.startAnimation(animationScaleRotateIn);
                                                    dataManager.setWork(localDataSet.get(viewHolder.getBindingAdapterPosition()).getUuid(), Work.FORM, Work.DONE);
                                                }

                                                @Override
                                                public void onAnimationRepeat(Animation animation) {

                                                }
                                            });
                                            animationScaleRotateIn.setAnimationListener(new Animation.AnimationListener() {
                                                @Override
                                                public void onAnimationStart(Animation animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animation animation) {
                                                    viewHolder.layout.startAnimation(animationTranslateLeft);
                                                    ++animationCount;
                                                }

                                                @Override
                                                public void onAnimationRepeat(Animation animation) {

                                                }
                                            });
                                            animationTranslateLeft.setAnimationListener(new Animation.AnimationListener() {
                                                @Override
                                                public void onAnimationStart(Animation animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animation animation) {
                                                    --animationCount;

                                                    if (animationCount <= 0) {
                                                        updateDataSet();
                                                    }
                                                }

                                                @Override
                                                public void onAnimationRepeat(Animation animation) {

                                                }
                                            });
                                            break;
                                        case Work.ALL:
                                            viewHolder.imageViewForm.startAnimation(animationScaleRotateOut);
                                            animationScaleRotateOut.setAnimationListener(new Animation.AnimationListener() {
                                                @Override
                                                public void onAnimationStart(Animation animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animation animation) {
                                                    viewHolder.imageViewForm.setColorFilter(application.getColor(R.color.iconDone));
                                                    viewHolder.textViewDate.setTextColor(application.getColor(R.color.iconDone));
                                                    viewHolder.imageViewForm.setImageResource(R.drawable.ic_done);
                                                    viewHolder.imageViewForm.startAnimation(animationScaleRotateIn);
                                                }

                                                @Override
                                                public void onAnimationRepeat(Animation animation) {

                                                }
                                            });
                                            animationScaleRotateIn.setAnimationListener(new Animation.AnimationListener() {
                                                @Override
                                                public void onAnimationStart(Animation animation) {
                                                }

                                                @Override
                                                public void onAnimationEnd(Animation animation) {
                                                }

                                                @Override
                                                public void onAnimationRepeat(Animation animation) {

                                                }
                                            });
                                            break;
                                    }

                            }

                        }
                    });


                    viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                        Intent intent = new Intent(context, WorkCheckActivity.class);
                            Intent intent = new Intent(context, WorkCheckActivity.class);
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
                                                dataManager.delWork(localDataSet.get(viewHolder.getBindingAdapterPosition()).getUuid());
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
//                    viewHolder.layout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(context, WorkEditActivity.class);
//                            intent.putExtra("date", localDataSet.get(viewHolder.getBindingAdapterPosition()).getDate().getTime().getTime());
//                            intent.putExtra("uuid", "");
//                            context.startActivity(intent);
//                        }
//                    });
                    break;
            }
        } else {
            // select mode
            switch (viewType) {
                case 0:
                    viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Work work = localDataSet.get(viewHolder.getBindingAdapterPosition());
                            listener.onItemClick(work.getUuid());
                        }
                    });
            }

        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Work work = localDataSet.get(position);
        if (isAudit(position)) {
            viewHolder.getTextViewDate().setText(dateFormatMonth.format(work.getDate().getTime()));
        } else {
            Integer form = work.getForm();
            viewHolder.getTextViewDate().setText(dateFormatTime.format(work.getDate().getTime()));
            viewHolder.getTextViewTitle().setText(work.getTitle());
            viewHolder.getTextViewNote().setText(work.getNote());
            viewHolder.getImageViewForm().setColorFilter(application.getColor(form == 0 ? R.color.iconTodo : R.color.iconDone));
            viewHolder.getImageViewForm().setImageResource((form == 0 ? R.drawable.ic_todo : R.drawable.ic_done));

            viewHolder.getTextViewRepeat().setText(application.getResources().getString(RepeatRes.getStringId(work.getRepeat())));
            viewHolder.getTextViewDate().setTextColor(application.getColor(form == 0 ? R.color.colorTextRed : R.color.colorPrimary));
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

    @Override
    public int getItemViewType(int position) {
        return isAudit(position) ? 1 : 0;
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
        notifyItemRangeChanged(0, localDataSet.size());
    }

    public void onMultiInterval() {
        for (int i = chosen.first(); i <= chosen.last(); ++i)
            chosen.add(i);
        notifyItemRangeChanged(chosen.first(), chosen.last());
    }

}
