package com.trashparadise.lifemanager.ui.bills;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.adapter.BillTypeAdapter;
import com.trashparadise.lifemanager.bean.Bill;
import com.trashparadise.lifemanager.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.trashparadise.lifemanager.bean.TypeBean;
import com.trashparadise.lifemanager.constants.BillTypeRes;
import com.trashparadise.lifemanager.databinding.ActivityBillEditBinding;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class BillEditActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, BillTypeAdapter.OnItemClickListener {
    private ActivityBillEditBinding binding;
    private DataManager dataManager;
    private RadioGroup radioGroup;
    private BillTypeAdapter billTypeAdapter;

    private BigDecimal amount;
    private Calendar date;
    private String type;
    private String note;
    private String uuid;
    private Integer form;
    private Integer typeId = 0;

    private ArrayList<TypeBean> typeList = new ArrayList<>();

    private List<Integer> buttonsAmount = new ArrayList<>();
    private Integer dotted = 0;
    private Bill bill;
    private DecimalFormat decimalFormat;
    private SimpleDateFormat dateFormatDate;
    private BigDecimal amountMax;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init
        super.onCreate(savedInstanceState);
        binding = ActivityBillEditBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        dataManager = DataManager.getInstance();
        Intent intent = getIntent();
        decimalFormat = new DecimalFormat(getResources().getString(R.string.amount_decimal_format));
        dateFormatDate = new SimpleDateFormat(getResources().getString(R.string.date_format_date));
        amountMax = new BigDecimal(getResources().getString(R.string.amount_max));

        // get object
        uuid = intent.getStringExtra("uuid");
        bill = dataManager.getBill(uuid);
        if (bill == null) {
            amount = new BigDecimal("0");
            note = new String("");
            form = 0;
            type = new String(BillTypeRes.NAMES[form][typeId]);
            date = Calendar.getInstance();
        } else {
            amount = new BigDecimal(bill.getAmount().toString());
            note = new String(bill.getNote());
            uuid = new String(bill.getUuid());
            date = Calendar.getInstance();
            date.setTime(bill.getDate().getTime());
            form = bill.getForm();
            type = bill.getType();
        }

        initTypeDate();

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


        initView();
        initListener();
    }


    private void initTypeDate() {
        typeId = BillTypeRes.getId(form, type);
        typeList.clear();
        for (int i = 0; i < BillTypeRes.NAMES[form].length; ++i) {
            typeList.add(new TypeBean(BillTypeRes.NAMES[form][i], BillTypeRes.ICONS[form][i], BillTypeRes.ICONS_GRAY[form][i], i));
        }
        typeList.get(typeId).setChecked(true);
    }

    private void initView() {
        binding.textViewAmount.setText(decimalFormat.format(amount));
        binding.textViewAmount.setTextColor(getResources().getColor(BillTypeRes.COLOR[form]));
        binding.editTextNote.setText(note);
        binding.textViewDate.setText(dateFormatDate.format(date.getTime()));
        binding.textViewDate.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.textViewType.setText(typeList.get(typeId).getName());
        binding.imageViewType.setImageResource(typeList.get(typeId).getIcon());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_bill_form);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        radioGroup = findViewById(R.id.radioGroup_form);
        radioGroup.check(formToId(form));

        binding.typeRecycleView.setLayoutManager(new LinearLayoutManager(this));
//        binding.typeRecycleView.setLayoutManager(new GridLayoutManager(this,2));
        billTypeAdapter = new BillTypeAdapter(typeList, this);
        binding.typeRecycleView.setAdapter(billTypeAdapter);
    }

    private void initListener() {
        for (Integer id : buttonsAmount) {
            Button x = findViewById(id);
            x.setOnClickListener(this);
        }
        binding.buttonConfirm.setOnClickListener(this);
        binding.textViewDate.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        Animation animationDown = AnimationUtils.loadAnimation(BillEditActivity.this, R.anim.anim_translate_down);
        binding.constraintLayoutKeyboardBorder.startAnimation(animationDown);
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {

            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    binding.constraintLayoutKeyboardBorder.clearAnimation();
                    binding.constraintLayoutKeyboardBorder.setVisibility(View.GONE);
                } else {
                    binding.constraintLayoutKeyboardBorder.setVisibility(View.VISIBLE);
                    binding.constraintLayoutKeyboardBorder.startAnimation(animationDown);
                }
            }
        });
    }

    private void onConfirm() {
        note = binding.editTextNote.getText().toString();
        type = typeList.get(typeId).getName();

        if (uuid.equals("")) {
            dataManager.addBill(new Bill(amount, date, type, form, note));
        } else {
            dataManager.setBill(uuid, new Bill(amount, date, type, form, note));
        }
        finish();
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
        dateTimeDialogFragment.setDefaultDateTime(date.getTime());

        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date newDate) {
                date.setTime(newDate);
                binding.textViewDate.setText(dateFormatDate.format(date.getTime()));
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
                if (amount.compareTo(amountMax) < 0) {
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
        }


        binding.textViewAmount.setText(decimalFormat.format(amount));
    }

    private int formToId(int form) {
        return form == 0 ? R.id.rb_expend : R.id.rb_income;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        form = form == 1 ? 0 : 1;
        initTypeDate();
        billTypeAdapter.notifyDataSetChanged();
        onItemClick(0);
        binding.textViewAmount.setTextColor(getResources().getColor(BillTypeRes.COLOR[form]));
    }

    @Override
    public void onItemClick(int position) {
        typeList.get(typeId).setChecked(false);
        typeList.get(position).setChecked(true);
        typeId = position;
        binding.textViewType.setText(typeList.get(typeId).getName());
        binding.imageViewType.setImageResource(typeList.get(typeId).getIcon());
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

}