package com.trashparadise.lifemanager.ui.works;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.trashparadise.lifemanager.Bill;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.Work;
import com.trashparadise.lifemanager.constants.RepeatRes;
import com.trashparadise.lifemanager.constants.TypeRes;
import com.trashparadise.lifemanager.databinding.ActivityWorkEditBinding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WorkEditActivity extends AppCompatActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener {
    private ActivityWorkEditBinding binding;
    private RadioGroup radioGroup;
    private LifeManagerApplication application;


    private Calendar date;
    private Integer form;
    private String note;
    private String title;
    private String uuid;
    private Calendar dateNew;
    private Integer repeat;

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


        // get object
        uuid = intent.getStringExtra("uuid");
        work = application.getWork(uuid);
        if (work == null) {
            date = Calendar.getInstance();
            form = 0;
            note = new String("");
            title = new String("");
            repeat = 0;
            date = Calendar.getInstance();
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
        }
    }

    private void initView() {
        binding.textViewDate.setText(dateFormatDate.format(dateNew.getTime()));
        binding.editTextNote.setText(note);
        binding.editTextTitle.setText(title);
        binding.textViewRepeat.setText(getString(RepeatRes.getStringId(repeat)));

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
        radioGroup.setOnCheckedChangeListener(this);
    }


    private int formToId(int form) {
        return form == 0 ? R.id.rb_todo : R.id.rb_done;
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
                repeatNew = Calendar.YEAR;
                break;
            case R.id.button_every_month:
                repeatNew = Calendar.MONTH;
                break;
            case R.id.button_every_week:
                repeatNew = Calendar.WEEK_OF_MONTH;
                break;
            case R.id.button_every_day:
                repeatNew = Calendar.DAY_OF_WEEK;
                break;
        }
        if (repeat.equals(repeatNew)) {
            repeatNew = 0;
        }
        repeat = repeatNew;
        binding.textViewRepeat.setText(getString(RepeatRes.getStringId(repeat)));

    }

    private void onConfirm() {
        note = binding.editTextNote.getText().toString();
        title = binding.editTextTitle.getText().toString();
        if (title.length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.work_confirm_warning);
            Dialog dialogFragment = builder.create();
            dialogFragment.show();

        } else {
            if (uuid.equals("")) {
                application.addWork(new Work(title, dateNew, repeat, form, note));
            } else {
                application.setWork(uuid, new Work(title, dateNew, repeat, form, note));
            }
            finish();
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



    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        form= form==1?0:1;
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