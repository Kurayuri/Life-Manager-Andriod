package com.trashparadise.lifemanager.ui.bills;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.trashparadise.lifemanager.Bill;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.trashparadise.lifemanager.databinding.ActivityBillEditBinding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class BillEditActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityBillEditBinding binding;
    private LifeManagerApplication application;

    private BigDecimal amount;
    private Date date;
    private String type;
    private String note;
    private String uuid;

    private List<Integer> buttonsAmount = new ArrayList<>();
    private Integer dotted = 0;
    private Bill bill;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBillEditBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        application = (LifeManagerApplication) this.getApplication();
        Intent intent = getIntent();

        decimalFormat= new DecimalFormat(getResources().getString(R.string.amount_decimal_format));

        uuid = intent.getStringExtra("uuid");
        bill = application.getBill(uuid);
        if (bill == null) {
            amount = new BigDecimal("0");
            note = new String("");
            type = new String("");
            date = new Date();
        } else {
            amount = new BigDecimal(bill.getAmount().toString());
            note = new String(bill.getNote());
            uuid = new String(bill.getUuid());
            date = new Date(bill.getDate().getTime());
        }

        binding.textViewAmount.setText(decimalFormat.format(amount));
        binding.editTextNote.setText(note);
        binding.textViewDate.setText(date.toString());

        buttonsAmount.add(R.id.button_0);
        buttonsAmount.add(R.id.button_1);
        buttonsAmount.add(R.id.button_2);
        buttonsAmount.add(R.id.button_3);
        buttonsAmount.add(R.id.button_4);
        buttonsAmount.add(R.id.button_5);
        buttonsAmount.add(R.id.button_6);
        buttonsAmount.add(R.id.button_7);
        buttonsAmount.add(R.id.button_8);
        buttonsAmount.add(R.id.button_9);
        buttonsAmount.add(R.id.button_dot);
        buttonsAmount.add(R.id.button_clear);
        buttonsAmount.add(R.id.button_delete);

        for (Integer id : buttonsAmount) {
            Button x = findViewById(id);
            x.setOnClickListener(this);
        }
        binding.buttonConfirm.setOnClickListener(this);
        binding.textViewDate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Integer id = v.getId();
        switch (id) {
            case R.id.button_0:
            case R.id.button_1:
            case R.id.button_2:
            case R.id.button_3:
            case R.id.button_4:
            case R.id.button_5:
            case R.id.button_6:
            case R.id.button_7:
            case R.id.button_8:
            case R.id.button_9:
            case R.id.button_dot:
            case R.id.button_clear:
            case R.id.button_delete:
                onAmountInput(id);
                break;
            case R.id.button_confirm:
                onConfirm();
                break;
            case R.id.textView_date:
                onDateInput();
                break;
        }
    }

    private void onConfirm() {
        note=binding.editTextNote.getText().toString();

        if (uuid.equals("")){
            application.addBill(new Bill(amount, date, type, note));}
        else {
            application.setBill(bill,new Bill(amount, date, type, note));
        }
        finish();
    }

    private void onDateInput() {
        SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                getResources().getString(R.string.date_picker_title),
                getResources().getString(R.string.positive),
                getResources().getString(R.string.negative)
        );

        dateTimeDialogFragment.startAtTimeView();
        dateTimeDialogFragment.set24HoursMode(true);
        dateTimeDialogFragment.setDefaultDateTime(date);

        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date newDate) {
                date=newDate;
                binding.textViewDate.setText(date.toString());
            }

            @Override
            public void onNegativeButtonClick(Date newDate) {
            }
        });

        dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");
    }

    private void onAmountInput(Integer input) {

        BigDecimal decimal_10 = new BigDecimal("10");
        int ip = input;
        switch (ip) {
            case R.id.button_dot:
                dotted = dotted == 0 ? 1 : dotted;
                break;
            case R.id.button_clear:
                dotted = 0;
                amount = new BigDecimal("0");
                break;
            case R.id.button_delete: {
                if (dotted <= 1) {
                    amount = amount.divideToIntegralValue(decimal_10);
                    dotted = 0;
                } else if (dotted <= 3) {
                    BigDecimal dotNumber = new BigDecimal("1");
                    for (int i = 0; i < dotted - 1; ++i) {
                        dotNumber = dotNumber.multiply(decimal_10);
                    }
                    dotted -= 1;
                    amount = amount.multiply(dotNumber).divideToIntegralValue(decimal_10).divide(dotNumber.divide(decimal_10));
                }
                break;
            }
            default: {
                if (dotted == 0) {
                    amount = amount.multiply(decimal_10).add(new BigDecimal(buttonsAmount.indexOf(input) + ""));
                } else if (dotted <= 2) {
                    BigDecimal dotnum = new BigDecimal("1");
                    for (int i = 0; i < dotted; ++i) {
                        dotnum = dotnum.multiply(new BigDecimal("0.1"));
                    }
                    dotted += 1;
                    amount = amount.add(new BigDecimal(buttonsAmount.indexOf(input) + "").multiply(dotnum));
                }
            }
        }


        binding.textViewAmount.setText(decimalFormat.format(amount));
    }
}