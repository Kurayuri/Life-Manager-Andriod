package com.trashparadise.lifemanager.ui.bills;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.trashparadise.lifemanager.Adapter.BillTypeAdapter;
import com.trashparadise.lifemanager.Bill;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.trashparadise.lifemanager.bean.TypeBean;
import com.trashparadise.lifemanager.constants.TypeRes;
import com.trashparadise.lifemanager.databinding.ActivityBillEditBinding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class BillEditActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, BillTypeAdapter.OnItemClickListener {
    private ActivityBillEditBinding binding;
    private LifeManagerApplication application;
    private RecyclerView recyclerView;
    private RadioGroup radioGroup;
    private BillTypeAdapter billTypeAdapter;

    private BigDecimal amount;
    private Date date;
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
        super.onCreate(savedInstanceState);
        binding = ActivityBillEditBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        application = (LifeManagerApplication) this.getApplication();
        Intent intent = getIntent();

        decimalFormat = new DecimalFormat(getResources().getString(R.string.amount_decimal_format));
        dateFormatDate = new SimpleDateFormat(getResources().getString(R.string.date_format_date));
        amountMax = new BigDecimal(getResources().getString(R.string.amount_max));

        uuid = intent.getStringExtra("uuid");
        bill = application.getBill(uuid);
        if (bill == null) {
            amount = new BigDecimal("0");
            note = new String("");
            form = 0;
            type = new String(TypeRes.NAMES[form][typeId]);
            date = new Date();
        } else {
            amount = new BigDecimal(bill.getAmount().toString());
            note = new String(bill.getNote());
            uuid = new String(bill.getUuid());
            date = new Date(bill.getDate().getTime());
            form = bill.getForm();
            type = bill.getType();
            Log.e("hh", "onCreate: Old" + form + type);
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



        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.toolbar_expand_income);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        radioGroup = findViewById(R.id.radioGroup_form);
        radioGroup.check(formToId(form));



        recyclerView = binding.typeRecycleView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        billTypeAdapter = new BillTypeAdapter(typeList, this);
        recyclerView.setAdapter(billTypeAdapter);

        initView();
        initListener();
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

    private void initTypeDate() {
        typeId = TypeRes.getId(form, type);
        typeList.clear();
        for (int i = 0; i < TypeRes.NAMES[form].length; ++i) {
            typeList.add(new TypeBean(TypeRes.NAMES[form][i], TypeRes.ICONS[form][i], TypeRes.ICONS_GRAY[form][i], i));
        }
        typeList.get(typeId).setChecked(true);
    }

    private void initView() {
        binding.textViewAmount.setText(decimalFormat.format(amount));
        binding.textViewAmount.setTextColor(getResources().getColor(TypeRes.COLOR[form]));
        binding.editTextNote.setText(note);
        binding.textViewDate.setText(dateFormatDate.format(date));
        binding.textViewDate.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.textViewType.setText(typeList.get(typeId).getName());
        binding.imageViewType.setImageResource(typeList.get(typeId).getIcon());
    }

    private void initListener(){
        for (Integer id : buttonsAmount) {
            Button x = findViewById(id);
            x.setOnClickListener(this);
        }
        binding.buttonConfirm.setOnClickListener(this);
        binding.textViewDate.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
    }

    private void onConfirm() {
        note = binding.editTextNote.getText().toString();
        type = typeList.get(typeId).getName();

        if (uuid.equals("")) {
            application.addBill(new Bill(amount, date, type, form, note));
        } else {
            application.setBill(bill, new Bill(amount, date, type, form, note));
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
        dateTimeDialogFragment.setDefaultDateTime(date);

        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date newDate) {
                date = newDate;
                binding.textViewDate.setText(dateFormatDate.format(date));
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
        int formNew = 0;
        switch (checkedId) {
            case R.id.rb_expend:
                formNew = 0;
                break;
            case R.id.rb_income:
                formNew = 1;
                break;
        }

        if (formNew != form) {
            form = formNew;
            initTypeDate();
            billTypeAdapter.notifyDataSetChanged();
            onItemClick(0);
            binding.textViewAmount.setTextColor(getResources().getColor(TypeRes.COLOR[form]));
        }

    }

    @Override
    public void onItemClick(int position) {
        typeList.get(typeId).setChecked(false);
        typeList.get(position).setChecked(true);
        typeId = position;
        binding.textViewType.setText(typeList.get(typeId).getName());
        binding.imageViewType.setImageResource(typeList.get(typeId).getIcon());
    }
}