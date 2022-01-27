package com.trashparadise.lifemanager.ui.works;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.adapter.WorkListAdapter;

import java.util.TreeSet;


public class WorkListFragment extends Fragment implements WorkListAdapter.OnItemClickListener {
    private LifeManagerApplication application;
    private View view;
    private WorkListAdapter workListAdapter;
    private RecyclerView recyclerView;
    private DataManager dataManager;
    private Integer form;
    private Boolean auditOn;
    private Boolean slimOn;
    private Boolean openOn;
    private AppCompatActivity activity;
    private int workListLayout;

    public WorkListFragment(Integer form, Boolean auditOn, Boolean slimOn, Boolean openOn) {
        this.form = form;
        this.auditOn = auditOn;
        this.slimOn = slimOn;
        this.openOn = openOn;
    }

    public WorkListFragment() {
        this.form = Work.ALL;
        this.auditOn = true;
        this.slimOn = false;
        this.openOn = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        workListLayout = slimOn ? R.layout.fragment_work_list_slim : R.layout.fragment_work_list;
        view = inflater.inflate(workListLayout, container, false);
        dataManager = DataManager.getInstance();
        application = (LifeManagerApplication) this.getActivity().getApplication();
        recyclerView = view.findViewById(R.id.recyclerView);

        workListAdapter = new WorkListAdapter(getContext(), form, auditOn, slimOn, openOn, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(workListAdapter);

        return view;
    }

    public void updateDateSet(Integer form) {
        this.form=form;
        workListAdapter.callUpdateDataSet(form);
    }

    @Override
    public void onItemClick(String uuid) {
        if (!openOn) {
            Intent intent = new Intent();
            intent.putExtra("uuid", uuid);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

    public void onMultiChoice() {
        workListAdapter.onMultiChoice();
    }

    public void onMultiDelete() {
        TreeSet<String> chosen = workListAdapter.onMultiDelete();
        for (String uuid : chosen) {
            dataManager.delWork(uuid);
        }
        workListAdapter.callUpdateDataSet(form);
    }

    public void onMultiInterval() {
        workListAdapter.onMultiInterval();
    }

    public void onMultiReverse() {
        workListAdapter.onMultiReverse();
    }

}