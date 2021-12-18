package com.trashparadise.lifemanager.ui.works;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import com.trashparadise.lifemanager.R;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.constants.RepeatRes;
import com.trashparadise.lifemanager.databinding.ActivityWorkCheckBinding;
import com.trashparadise.lifemanager.ui.contact.ContactSelectActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WorkCheckActivity extends AppCompatActivity {
    private ActivityWorkCheckBinding binding;
    private LifeManagerApplication application;

    private Work work;
    private SimpleDateFormat dateFormatDate;

    private Calendar date;
    private Integer form;
    private String note;
    private String title;
    private String uuid;
    private Calendar dateNew;
    private Integer repeat;
    private boolean started = false;
    private Integer typeId = 0;

    ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormatDate = new SimpleDateFormat(getString(R.string.date_format_date));
        binding = ActivityWorkCheckBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        application = (LifeManagerApplication) getApplication();

        Intent intent = getIntent();
        uuid = intent.getStringExtra("uuid");

        work = application.getWork(uuid);
        title = work.getTitle();
        date = work.getDate();
        repeat = work.getRepeat();
        note = work.getNote();
        form = work.getForm();

        initView();

        initListener();

    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_work_form);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        RadioGroup radioGroup = findViewById(R.id.radioGroup_form);

        findViewById(R.id.rb_todo).setClickable(false);
        findViewById(R.id.rb_done).setClickable(false);

        radioGroup.check(formToId(form));


        binding.textViewDate.setText(dateFormatDate.format(date.getTime()));
        binding.editTextNote.setText(note);
        binding.editTextTitle.setText(title);
        binding.textViewRepeat.setText(getString(RepeatRes.getStringId(repeat)));
    }

    private void initListener(){

        binding.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(com.trashparadise.lifemanager.ui.works.WorkCheckActivity.this,ContactSelectActivity.class);
                intent.putExtra("uuid", uuid);
                launcher.launch(intent);
            }
        });
        binding.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started = true;
                Intent intent = new Intent(com.trashparadise.lifemanager.ui.works.WorkCheckActivity.this, WorkEditActivity.class);
                intent.putExtra("uuid", uuid);
                startActivity(intent);
            }
        });
        binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(com.trashparadise.lifemanager.ui.works.WorkCheckActivity.this);
                builder.setMessage(R.string.delete_confirm_text_chain)
                        .setPositiveButton(R.string.single_item, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                application.delWork(uuid);
                                com.trashparadise.lifemanager.ui.works.WorkCheckActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.chain_item, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                application.delWorkChain(uuid);
                                com.trashparadise.lifemanager.ui.works.WorkCheckActivity.this.finish();
                            }
                        })
                        .setNeutralButton(R.string.negative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                Dialog dialogFragment = builder.create();
                dialogFragment.show();
            }
        });
        launcher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode()== Activity.RESULT_OK){
                            Intent data=result.getData();
                            String contactUuid=data.getStringExtra("contactUuid");
                            Log.e(uuid,contactUuid);
                        }
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (started) {
            finish();
        }
    }

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

    private int formToId(int form) {
        return form == 0 ? R.id.rb_todo : R.id.rb_done;
    }
}