package com.trashparadise.lifemanager.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.Preference;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.Work;
import com.trashparadise.lifemanager.databinding.FragmentHomeBinding;
import com.trashparadise.lifemanager.ui.bills.BillAuditPieFragment;
import com.trashparadise.lifemanager.ui.bills.BillEditActivity;
import com.trashparadise.lifemanager.ui.bills.BillListFragment;
import com.trashparadise.lifemanager.ui.works.WorkEditActivity;
import com.trashparadise.lifemanager.ui.works.WorkListFragment;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private AppCompatActivity activity;
    private LifeManagerApplication application;
    private FragmentTransaction fragmentTransaction;
    private BillAuditPieFragment billAuditPieFragment;
    private BillListFragment billListFragment;
    private WorkListFragment workListAFragment;
    private Integer init;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        billAuditPieFragment = new BillAuditPieFragment(Calendar.getInstance(), 0);
        billListFragment = new BillListFragment();
        workListAFragment = new WorkListFragment(Work.TODO, false, true,true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        application = (LifeManagerApplication) activity.getApplication();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer_chart, billAuditPieFragment);

        switch (application.getPreference().getHome()) {
            case Preference.HOME_BILL:
                fragmentTransaction.add(R.id.fragmentContainer_list, billListFragment);
                break;
            case Preference.HOME_WORK:
                fragmentTransaction.add(R.id.fragmentContainer_list, workListAFragment);
                break;
        }


//        fragmentTransaction.add(R.id.fragmentContainer_list, billListFragment);
//        fragmentTransaction.add(R.id.fragmentContainer_list, workListAFragment);
        fragmentTransaction.commit();
        init = 0;


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


        binding.floatingActionButtonNewWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WorkEditActivity.class);
                intent.putExtra("uuid", "");
                startActivity(intent);
            }
        });
        // for test
        binding.floatingActionButtonNewBill.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer_list, billListFragment);
                fragmentTransaction.commit();
                application.getPreference().setHome(Preference.HOME_BILL);
                billAuditPieFragment.callUpdateData();
                return true;
            }
        });
        binding.floatingActionButtonNewWork.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer_list, workListAFragment);
                fragmentTransaction.commit();
                application.getPreference().setHome(Preference.HOME_WORK);
                billAuditPieFragment.callUpdateData();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (init != 0) {
            try {
                billAuditPieFragment.callUpdateData();
                billListFragment.updateDateSet(-1);
                workListAFragment.updateDateSet(0);
            }
            catch (Exception e){
            }
        }
        init = 1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}