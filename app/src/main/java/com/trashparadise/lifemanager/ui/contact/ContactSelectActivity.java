package com.trashparadise.lifemanager.ui.contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.bean.Contact;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.databinding.ActivityContactSelectBinding;

public class ContactSelectActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityContactSelectBinding binding;
    private DataManager dataManager;
    //    private Integer form;
    private ContactListFragment contactListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactSelectBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        dataManager=DataManager.getInstance();

        contactListFragment = new ContactListFragment();

        initView();
        initListener();
    }

    private void initListener() {
        binding.imageViewAdd.setOnClickListener(this);
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.select_to_share);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer_contactList, contactListFragment).commit();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_add:
                onAdd();
                break;
        }
    }


    private void onAdd() {
        String name = binding.editTextName.getText().toString();
        String uuid = binding.editTextUuid.getText().toString();
        if (uuid.length() != 0) {
            dataManager.addContact(new Contact(name, uuid));
        }
        binding.editTextName.setText("");
        binding.editTextUuid.setText("");
        contactListFragment.updateData();
    }

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
}