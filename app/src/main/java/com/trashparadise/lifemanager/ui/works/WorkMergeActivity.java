package com.trashparadise.lifemanager.ui.works;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.databinding.ActivityWorkMergeBinding;

public class WorkMergeActivity extends AppCompatActivity {
    private LifeManagerApplication application;
    private ActivityWorkMergeBinding binding;
    private RadioGroup radioGroup;
    private Integer form;
    private WorkListFragment workListFragment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkMergeBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        application = (LifeManagerApplication) getApplication();
        Intent intent=getIntent();
        form=intent.getIntExtra("form", Work.ALL);
        workListFragment=new WorkListFragment(form,true,true,false);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer_workList,workListFragment).commit();

        initView();
    }

    private void initView()    {
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setCustomView(R.layout.actionbar_work_form);
//        actionBar.setDisplayShowTitleEnabled(false);
//        radioGroup = findViewById(R.id.radioGroup_form);
//        radioGroup.check(formToId(form));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.select_to_merge);

    }

    private int formToId(int form) {
        switch (form){
            case Work.ALL:
                return -1;
            case Work.TODO:
                return R.id.rb_todo;
            case Work.DONE:
                return R.id.rb_done;
        }
        return -1;
    }
    // Actionbar Button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}