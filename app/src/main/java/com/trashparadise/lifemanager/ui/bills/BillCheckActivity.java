package com.trashparadise.lifemanager.ui.bills;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.bean.Bill;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.constants.TypeRes;
import com.trashparadise.lifemanager.databinding.ActivityBillCheckBinding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BillCheckActivity extends AppCompatActivity {
    private ActivityBillCheckBinding binding;
    private DataManager dataManager;

    private Bill bill;
    private DecimalFormat decimalFormat;
    private SimpleDateFormat dateFormatDate;

    private String uuid;
    private BigDecimal amount;
    private Calendar date;
    private String type;
    private String note;
    private Integer form;
    private boolean started = false;
    private Integer typeId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decimalFormat = new DecimalFormat(getResources().getString(R.string.amount_decimal_format));
        dateFormatDate = new SimpleDateFormat(getResources().getString(R.string.date_format_date));

        binding = ActivityBillCheckBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        dataManager=DataManager.getInstance();

        Intent intent = getIntent();
        uuid = intent.getStringExtra("uuid");

        bill = dataManager.getBill(uuid);
        amount = bill.getAmount();
        date = bill.getDate();
        type = bill.getType();
        note = bill.getNote();
        form = bill.getForm();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_bill_form);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        RadioGroup radioGroup = findViewById(R.id.radioGroup_form);
        radioGroup.check(formToId(form));

        findViewById(R.id.rb_expend).setClickable(false);
        findViewById(R.id.rb_income).setClickable(false);


        binding.textViewAmount.setText(decimalFormat.format(amount));
        binding.textViewAmount.setTextColor(getResources().getColor(TypeRes.COLOR[form]));
        binding.editTextNote.setText(note);
        binding.textViewDate.setText(dateFormatDate.format(date.getTime()));
//        binding.textViewDate.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        binding.textViewType.setText(type);
        binding.imageViewType.setImageResource(TypeRes.ICONS[form][TypeRes.getId(form, type)]);

        binding.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started=true;
                Intent intent = new Intent(BillCheckActivity.this, BillEditActivity.class);
                intent.putExtra("uuid", uuid);
                startActivity(intent);
            }
        });
        binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BillCheckActivity.this);
                builder.setMessage(R.string.delete_confirm_text)
                        .setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dataManager.delBill(uuid);
                                BillCheckActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                Dialog dialogFragment = builder.create();
                dialogFragment.show();
            }
        });
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
        return form == 0 ? R.id.rb_expend : R.id.rb_income;
    }
}