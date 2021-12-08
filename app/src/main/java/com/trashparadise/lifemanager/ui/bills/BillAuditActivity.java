package com.trashparadise.lifemanager.ui.bills;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.databinding.ActivityBillAuditBinding;
import com.trashparadise.lifemanager.databinding.ActivityMainBinding;
import com.trashparadise.lifemanager.databinding.FragmentHomeBinding;

import java.util.Calendar;
import java.util.Date;

public class BillAuditActivity extends AppCompatActivity {
    private ActivityBillAuditBinding binding;
    private LifeManagerApplication application;
    private FragmentTransaction fragmentTransaction;
    private Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application=(LifeManagerApplication)getApplication();
        binding = ActivityBillAuditBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();

        Intent intent = getIntent();
        long dateLong = intent.getLongExtra("date",new Date().getTime());
        date=Calendar.getInstance();
        date.setTime(new Date(dateLong));

        fragmentTransaction=getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.chart_pie_expand,new BillAuditPieFragment(date,0));
        fragmentTransaction.add(R.id.chart_pie_expand,new BillAuditLineFragment(date,0));
        fragmentTransaction.commit();

        setContentView(view);
    }
}