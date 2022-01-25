package com.trashparadise.lifemanager.ui.bills;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.Bill;
import com.trashparadise.lifemanager.databinding.FragmentBillsBinding;

public class BillsFragment extends Fragment {

    private FragmentBillsBinding binding;
    private AppCompatActivity activity;
    private RadioGroup radioGroupForm;
    private Integer currForm;
    private Integer newForm;
    private BillListFragment billListFragment;

    public static BillsFragment newInstance() {
        return new BillsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        currForm = Bill.ALL;
        newForm = Bill.ALL;

        activity = (AppCompatActivity) getActivity();
        binding = FragmentBillsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        billListFragment = new BillListFragment(currForm, true);

        getChildFragmentManager().beginTransaction().add(R.id.fragmentContainer_billList, billListFragment).commit();

        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_bill_form_multi_choice);
        radioGroupForm = actionBar.getCustomView().findViewById(R.id.radioGroup_form);

        initListener();

        return root;
    }


    private void initListener() {
        binding.floatingActionButtonNewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BillEditActivity.class);
                intent.putExtra("uuid", "");
                startActivity(intent);
            }
        });

        radioGroupForm.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currForm == Bill.INCOME) {
                    currForm = Bill.ALL;
                    radioGroupForm.clearCheck();
                } else {
                    currForm = Bill.INCOME;
                }
                updateDateSet();
            }
        });
        radioGroupForm.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currForm == Bill.EXPAND) {
                    currForm = Bill.ALL;
                    radioGroupForm.clearCheck();
                } else {
                    currForm = Bill.EXPAND;
                }
                updateDateSet();
            }
        });
    }

    public void updateDateSet() {
        billListFragment.updateDateSet(currForm);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDateSet();
    }
}