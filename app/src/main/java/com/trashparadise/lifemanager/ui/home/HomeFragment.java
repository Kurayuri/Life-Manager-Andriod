package com.trashparadise.lifemanager.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.bean.Bill;
import com.trashparadise.lifemanager.bean.Preference;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.databinding.FragmentHomeBinding;
import com.trashparadise.lifemanager.ui.bills.BillAuditActivity;
import com.trashparadise.lifemanager.ui.bills.BillAuditPieFragment;
import com.trashparadise.lifemanager.ui.bills.BillEditActivity;
import com.trashparadise.lifemanager.ui.bills.BillListFragment;
import com.trashparadise.lifemanager.util.BillAuditUtils;
import com.trashparadise.lifemanager.ui.works.WorkEditActivity;
import com.trashparadise.lifemanager.ui.works.WorkListFragment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private AppCompatActivity activity;
    private DataManager dataManager;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private BillAuditPieFragment billAuditPieFragment;
    private BillListFragment billListFragment;
    private WorkListFragment workListAFragment;
    private SimpleDateFormat simpleDateFormat;
    private Integer init;
    private ViewPager2 viewPager;
    private FragmentStateAdapter adapter;

    private DecimalFormat decimalFormat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        billAuditPieFragment = new BillAuditPieFragment(Calendar.getInstance(), Bill.EXPAND);
        billListFragment = new BillListFragment();
        workListAFragment = new WorkListFragment(Work.TODO, false, true, true);
        decimalFormat = new DecimalFormat(getString(R.string.amount_decimal_format_unit));
        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_month));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        dataManager = DataManager.getInstance();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.app_name);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fragmentManager = getChildFragmentManager();


        addFragment(R.id.fragmentContainer_chart, billAuditPieFragment, "pie");

        viewPager = binding.viewPage;
        adapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        switch (dataManager.getPreference().getHome()) {
            case Preference.HOME_BILL:
                viewPager.setCurrentItem(Preference.HOME_BILL);
//                addFragment(R.id.fragmentContainer_list,billListFragment,"bill");
                break;
            case Preference.HOME_WORK:
                viewPager.setCurrentItem(Preference.HOME_WORK);
//                addFragment(R.id.fragmentContainer_list,workListAFragment,"work");
                break;
        }

        init = 0;


        audit();
        initListener();
        return root;
    }

    private void audit() {
        Map<Integer, BigDecimal> sum = BillAuditUtils.getSum(dataManager.getBillList(Calendar.getInstance(), Bill.ALL));

        binding.textViewDate.setText(simpleDateFormat.format(Calendar.getInstance().getTime()));

        binding.textViewAmountExpand.setText(decimalFormat.format(sum.get(Bill.EXPAND)));
        binding.textViewAmountIncome.setText(decimalFormat.format(sum.get(Bill.INCOME)));

        if (sum.get(Bill.EXPAND).equals(new BigDecimal(0))) {
            binding.textViewDate.setText(binding.textViewDate.getText() + "\n" + getString(R.string.empty));
        }
    }

    private void initListener() {
        binding.textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BillAuditActivity.class);
                intent.putExtra("uuid", "");
                startActivity(intent);
            }
        });

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

        binding.floatingActionButtonNewBill.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                fragmentTransaction = getChildFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fragmentContainer_list, billListFragment);
//                fragmentTransaction.commit();
                viewPager.setCurrentItem(Preference.HOME_BILL);
                dataManager.getPreference().set(Preference.HOME, Preference.HOME_BILL);
                billAuditPieFragment.callUpdateData();
                return true;
            }
        });
        binding.floatingActionButtonNewWork.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                fragmentTransaction = getChildFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fragmentContainer_list, workListAFragment);
//                fragmentTransaction.commit();
                viewPager.setCurrentItem(Preference.HOME_WORK);
                dataManager.getPreference().set(Preference.HOME, Preference.HOME_WORK);
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
                audit();
            } catch (Exception e) {
            }
            try {
                workListAFragment.updateDateSet(0);
            } catch (Exception e) {
            }
            try {
                billListFragment.updateDateSet(-1);
            } catch (Exception e) {
            }

        }
        init = 1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addFragment(int containerViewId, Fragment fragment, String tag) {
        if (!fragment.isAdded() && null == fragmentManager.findFragmentByTag(tag)) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            fragmentManager.executePendingTransactions();
            ft.add(containerViewId, fragment, tag);
            ft.commitAllowingStateLoss();
        }

    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(HomeFragment homeFragment) {
            super(homeFragment);
        }

        @Override
        public Fragment createFragment(int position) {
            return position == Preference.HOME_BILL ? billListFragment : workListAFragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    private class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

}