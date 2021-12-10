package com.trashparadise.lifemanager.ui.works;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.trashparadise.lifemanager.Bill;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.Work;
import com.trashparadise.lifemanager.constants.RepeatRes;
import com.trashparadise.lifemanager.databinding.ActivityWorkEditBinding;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WorkEditActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private ActivityWorkEditBinding binding;
    private RadioGroup radioGroup;
    private LifeManagerApplication application;


    private Calendar date;
    private Integer form;
    private String note;
    private String title;
    private String uuid;
    private ArrayList<String> mergeList;
    private Calendar dateNew;
    private Integer repeat;

    ActivityResultLauncher<Intent> launcher;

    private SimpleDateFormat dateFormatDate;
    private Work work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init
        super.onCreate(savedInstanceState);
        binding = ActivityWorkEditBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        application = (LifeManagerApplication) this.getApplication();
        Intent intent = getIntent();
        dateFormatDate = new SimpleDateFormat(getString(R.string.date_format_date));
        mergeList = new ArrayList<>();


        // get object
        uuid = intent.getStringExtra("uuid");

        work = application.getWork(uuid);
        if (work == null) {
            String tmpUuid = intent.getStringExtra("extraUuid");
            work = application.getWork(tmpUuid);
            if (work == null)
                work = application.getWorkTmp(tmpUuid);
            if (work == null) {
                date = Calendar.getInstance();
                date.add(Calendar.HOUR, 1);
                form = 0;
                note = new String("");
                title = new String("");
                repeat = 0;
            } else {
                date = (Calendar) work.getDate().clone();
                form = work.getForm();
                note = new String(work.getNote());
                uuid = new String(work.getUuid());
                repeat = work.getRepeat();
                title = new String(work.getTitle());
                uuid = "";
            }
        } else {
            date = (Calendar) work.getDate().clone();
            form = work.getForm();
            note = new String(work.getNote());
            uuid = new String(work.getUuid());
            repeat = work.getRepeat();
            title = new String(work.getTitle());
        }

        dateNew = (Calendar) date.clone();


        initView();
        initListrner();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_day:
            case R.id.button_week:
            case R.id.button_month:
            case R.id.button_year:
                onDeadlineInput(v.getId());
                break;
            case R.id.button_every_day:
            case R.id.button_every_week:
            case R.id.button_every_month:
            case R.id.button_every_year:
                onRepeatInput(v.getId());
                break;
            case R.id.button_confirm:
                onConfirm();
                break;
            case R.id.textView_date:
                onDateInput();
                break;
            case R.id.imageView_merge:
                onMerge();
                break;
        }
    }

    private void initView() {
        binding.textViewDate.setText(dateFormatDate.format(dateNew.getTime()));
        binding.editTextNote.setText(note);
        binding.editTextTitle.setText(title);
        binding.textViewRepeat.setText(getString(RepeatRes.getStringId(repeat)));
        binding.textViewDate.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_work_form);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        radioGroup = findViewById(R.id.radioGroup_form);
        radioGroup.check(formToId(form));
    }

    private void initListrner() {
        binding.buttonDay.setOnClickListener(this);
        binding.buttonWeek.setOnClickListener(this);
        binding.buttonMonth.setOnClickListener(this);
        binding.buttonYear.setOnClickListener(this);
        binding.buttonEveryDay.setOnClickListener(this);
        binding.buttonEveryWeek.setOnClickListener(this);
        binding.buttonEveryMonth.setOnClickListener(this);
        binding.buttonEveryYear.setOnClickListener(this);
        binding.textViewDate.setOnClickListener(this);
        binding.buttonConfirm.setOnClickListener(this);
        binding.imageViewMerge.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String mergeUuid = data.getStringExtra("uuid");
                            Work mergeWork = application.getWork(mergeUuid);
                            if (!uuid.equals(mergeUuid)) {
                                mergeList.add(mergeUuid);
                                binding.editTextTitle.setText(binding.editTextTitle.getText() + "\n" + mergeWork.getTitle());
                                binding.editTextNote.setText(binding.editTextNote.getText() + " " + mergeWork.getNote());
                            }
                        }
                    }
                }
        );
    }


    private int formToId(int form) {
        return form == Work.TODO ? R.id.rb_todo : R.id.rb_done;
    }

    private void onDeadlineInput(int id) {
        dateNew.setTime(Calendar.getInstance().getTime());

        switch (id) {
            case R.id.button_year:
                dateNew.set(Calendar.MONTH, dateNew.getActualMaximum(Calendar.MONTH));
            case R.id.button_month:
                dateNew.set(Calendar.DAY_OF_MONTH, dateNew.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case R.id.button_week:
                dateNew.set(Calendar.DAY_OF_WEEK, dateNew.getActualMaximum(Calendar.DAY_OF_WEEK));
            case R.id.button_day:
                break;
        }
        dateNew.set(Calendar.HOUR_OF_DAY, dateNew.getActualMaximum(Calendar.HOUR_OF_DAY));
        dateNew.set(Calendar.MINUTE, dateNew.getActualMaximum(Calendar.MINUTE));
        dateNew.set(Calendar.SECOND, dateNew.getActualMaximum(Calendar.SECOND));
        binding.textViewDate.setText(dateFormatDate.format(dateNew.getTime()));
    }


    private void onRepeatInput(int id) {
        Integer repeatNew = 0;
        switch (id) {
            case R.id.button_every_year:
                repeatNew = Work.EVERY_YEAR;
                break;
            case R.id.button_every_month:
                repeatNew = Work.EVERY_MONTH;
                break;
            case R.id.button_every_week:
                repeatNew = Work.EVERY_WEEK;
                break;
            case R.id.button_every_day:
                repeatNew = Work.EVERY_DAY;
                break;
        }
        if (repeat.equals(repeatNew)) {
            repeatNew = Work.EVERY_NONE;
        }
        repeat = repeatNew;
        binding.textViewRepeat.setText(getString(RepeatRes.getStringId(repeat)));

    }

    private void onConfirm() {
        note = binding.editTextNote.getText().toString();
        title = binding.editTextTitle.getText().toString();
        if (title.length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.work_confirm_warning).setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            Dialog dialogFragment = builder.create();
            dialogFragment.show();

        } else {
            Work workNew = new Work(title, dateNew, repeat, form, note);
            if (uuid.equals("")) {
                //New
                application.addWork(workNew);
                finish();
            } else {
                //Edit
                if (workNew.equals(work)) {
                    // edited
                    finish();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.edit_confirm_text_chain)
                            .setPositiveButton(R.string.single_item, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    application.setWork(uuid, workNew);
                                    for (String mergeUUid : mergeList) {
                                        application.delWork(mergeUUid);
                                    }
                                    finish();
                                }
                            })
                            .setNegativeButton(R.string.chain_item, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    application.setWorkChain(uuid, workNew);
                                    for (String mergeUUid : mergeList) {
                                        application.delWorkChain(mergeUUid);
                                    }
                                    finish();
                                }
                            });
                    Dialog dialogFragment = builder.create();
                    dialogFragment.show();
                }
            }

        }

    }

    private void onDateInput() {
        SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                getResources().getString(R.string.date_picker_title),
                getResources().getString(R.string.positive),
                getResources().getString(R.string.negative),
                null,
                Locale.getDefault().getLanguage()
        );

        dateTimeDialogFragment.startAtTimeView();
        dateTimeDialogFragment.set24HoursMode(true);
        dateTimeDialogFragment.setDefaultDateTime(dateNew.getTime());

        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date newDate) {
                dateNew.setTime(newDate);
                binding.textViewDate.setText(dateFormatDate.format(dateNew.getTime()));
            }

            @Override
            public void onNegativeButtonClick(Date newDate) {
            }
        });

        dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");
    }


    private void onMerge() {
        Intent intent = new Intent(this, WorkMergeActivity.class);
        intent.putExtra("form", form);
        launcher.launch(intent);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        form = form == 1 ? 0 : 1;
    }

    // Actionbar Button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}