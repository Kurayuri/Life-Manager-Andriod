package com.trashparadise.lifemanager.ui.bills;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.Bill;
import com.trashparadise.lifemanager.databinding.FragmentBillsBinding;

public class BillsFragment extends Fragment {

    private FragmentBillsBinding binding;
    private AppCompatActivity activity;
    private RadioGroup radioGroupForm;
    private View layoutMultiGroup;
    private ActionBar actionBar;
    private Integer currForm;
    private BillListFragment billListFragment;
    private boolean multiChoice;

    public static BillsFragment newInstance() {
        return new BillsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        currForm = Bill.ALL;
        multiChoice = false;

        activity = (AppCompatActivity) getActivity();
        binding = FragmentBillsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        billListFragment = new BillListFragment(currForm, true);

        getChildFragmentManager().beginTransaction().add(R.id.fragmentContainer_billList, billListFragment).commit();

        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_bill_form_multi_choice);
        radioGroupForm = actionBar.getCustomView().findViewById(R.id.radioGroup_form);
        layoutMultiGroup = actionBar.getCustomView().findViewById(R.id.layout_multiGroup);

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
                if (currForm == Bill.EXPAND) {
                    currForm = Bill.ALL;
                    radioGroupForm.clearCheck();
                } else {
                    currForm = Bill.EXPAND;
                }
                updateDateSet();
            }
        });
        radioGroupForm.getChildAt(1).setOnClickListener(new View.OnClickListener() {
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

        actionBar.getCustomView().findViewById(R.id.button_multiChoice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billListFragment.onMultiChoice();
                multiChoice = !multiChoice;
                if (multiChoice) {
                    layoutMultiGroup.setVisibility(View.VISIBLE);
                    radioGroupForm.setVisibility(View.GONE);
                } else {
                    layoutMultiGroup.setVisibility(View.GONE);
                    radioGroupForm.setVisibility(View.VISIBLE);
                }

            }
        });
        actionBar.getCustomView().findViewById(R.id.button_multiDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.delete_confirm_text)
                        .setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                billListFragment.onMultiDelete();
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
        actionBar.getCustomView().findViewById(R.id.button_multiInterval).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billListFragment.onMultiInterval();
            }
        });
        actionBar.getCustomView().findViewById(R.id.button_multiReverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billListFragment.onMultiReverse();
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