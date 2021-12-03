package com.trashparadise.lifemanager.ui.bills;

import com.trashparadise.lifemanager.R;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.trashparadise.lifemanager.databinding.ActivityBillEditBinding;
import com.trashparadise.lifemanager.databinding.FragmentBillsBinding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class BillEditActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityBillEditBinding binding;
    private BigDecimal amount;
    private java.util.Map<Integer, Integer> bnt2num = new java.util.HashMap<Integer, Integer>();
    private List<Integer> bnt = new ArrayList<Integer>();
    private Integer dotted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBillEditBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        amount=new BigDecimal("0");
        bnt.add(R.id.button_0);
        bnt.add(R.id.button_1);
        bnt.add(R.id.button_2);
        bnt.add(R.id.button_3);
        bnt.add(R.id.button_4);
        bnt.add(R.id.button_5);
        bnt.add(R.id.button_6);
        bnt.add(R.id.button_7);
        bnt.add(R.id.button_8);
        bnt.add(R.id.button_9);
        bnt.add(R.id.button_dot);
        bnt.add(R.id.button_clear);
        bnt.add(R.id.button_confirm);
        bnt.add(R.id.button_delete);


        for (Integer id : bnt) {
            Button x = findViewById(id);
            x.setOnClickListener(this);
        }

//        binding.buttonClear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                binding.textView.setText("");
//            }
//        });
//        binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String str = binding.textView.getText().toString();
//                int lg = str.length();
//                lg = (lg - 1) > 0 ? (lg - 1) : 0;
//                binding.textView.setText(str.substring(0, lg));
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        Integer id = v.getId();
        if (bnt.contains(id)) {
            onAmountInput(id);
        }
    }

    private void onAmountInput(Integer input) {
//        String strAmount = binding.textViewAmount.getText().toString();
//        Integer strAmountLength = strAmount.length();
//        strAmountLength = (strAmountLength - 1) > 0 ? (strAmountLength - 1) : 0;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        int ip=input;
        switch (ip) {
            case R.id.button_dot:
                dotted = dotted == 0 ? 1 : dotted;
                break;
            case R.id.button_clear:
                dotted=0;
                amount = new BigDecimal("0");
                break;
            case R.id.button_delete:
                break;
            case R.id.button_confirm:
                break;
            default: {
                if (dotted == 0) {
                    amount = amount.multiply(new BigDecimal("10")).add(new BigDecimal(bnt.indexOf(input)+""));
                } else {
                    BigDecimal dotnum = new BigDecimal("1");
                    for (int i = 0; i < dotted; ++i) {
                        dotnum = dotnum.multiply(new BigDecimal("0.1"));
                    }
                    dotted += 1;
                    amount = amount.add(new BigDecimal(bnt.indexOf(input) + "").multiply(dotnum));

                }
            }
        }


        binding.textViewAmount.setText(decimalFormat.format(amount));
    }


}