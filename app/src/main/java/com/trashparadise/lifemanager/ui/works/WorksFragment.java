package com.trashparadise.lifemanager.ui.works;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.databinding.FragmentWorksBinding;

public class WorksFragment extends Fragment {

    private FragmentWorksBinding binding;
    private AppCompatActivity activity;
    private RadioGroup radioGroupForm;
    private ActionBar actionBar;
    private Integer form;
    private View layoutMultiGroup;
    private WorkListFragment workListFragment;
    private boolean multiChoice;

    public static WorksFragment newInstance() {
        return new WorksFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        form = Work.TODO;
        multiChoice = false;

        activity = (AppCompatActivity) getActivity();
        binding = FragmentWorksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        workListFragment = new WorkListFragment(form, true, false, true);

        getChildFragmentManager().beginTransaction().add(R.id.fragmentContainer_workList, workListFragment).commit();

        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_work_form_multi_choice);
        radioGroupForm = actionBar.getCustomView().findViewById(R.id.radioGroup_form);
        radioGroupForm.check(R.id.rb_todo);
        layoutMultiGroup = actionBar.getCustomView().findViewById(R.id.layout_multiGroup);

        initListener();


        return root;
    }

    private void initListener() {
        binding.floatingActionButtonNewWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WorkEditActivity.class);
                intent.putExtra("uuid", "");
                startActivity(intent);
            }
        });

        radioGroupForm.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (form == 0) {
                    form = -1;
                    radioGroupForm.clearCheck();
                } else {
                    form = 0;
                }
                updateDateSet();
            }
        });
        radioGroupForm.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (form == 1) {
                    form = -1;
                    radioGroupForm.clearCheck();
                } else {
                    form = 1;
                }
                updateDateSet();
            }
        });
        actionBar.getCustomView().findViewById(R.id.button_multiChoice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workListFragment.onMultiChoice();
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
                                workListFragment.onMultiDelete();
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
                workListFragment.onMultiInterval();
            }
        });
        actionBar.getCustomView().findViewById(R.id.button_multiReverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workListFragment.onMultiReverse();
            }
        });

    }

    public void updateDateSet() {
        workListFragment.updateDateSet(form);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDateSet();
    }
}