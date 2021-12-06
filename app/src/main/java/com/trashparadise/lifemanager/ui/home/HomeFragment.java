package com.trashparadise.lifemanager.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.databinding.FragmentHomeBinding;
import com.trashparadise.lifemanager.ui.bills.BillAuditPieFragment;
import com.trashparadise.lifemanager.ui.bills.BillEditActivity;
import com.trashparadise.lifemanager.ui.bills.BillListFragment;
import com.trashparadise.lifemanager.ui.works.WorkEditActivity;

public class HomeFragment extends Fragment{

    private FragmentHomeBinding binding;
    private AppCompatActivity activity;
    private FragmentTransaction fragmentTransaction;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        activity=(AppCompatActivity)getActivity();
        ActionBar actionBar=activity.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        fragmentTransaction=getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer_chart,new BillAuditPieFragment());
        fragmentTransaction.add(R.id.fragmentContainer_list,new BillListFragment());
        fragmentTransaction.commit();




        initListener();
        return root;
    }
    private void initListener(){
        binding.floatingActionButtonNewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), BillEditActivity.class);
                intent.putExtra("uuid","");
                startActivity(intent);
            }
        });
        binding.floatingActionButtonNewWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), WorkEditActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}