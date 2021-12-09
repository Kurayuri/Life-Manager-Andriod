package com.trashparadise.lifemanager.ui.bills;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.trashparadise.lifemanager.Bill;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.databinding.ActivityBillAuditBinding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class BillAuditActivity extends AppCompatActivity {
    private ActivityBillAuditBinding binding;
    private LifeManagerApplication application;
    private FragmentTransaction fragmentTransaction;
    private SimpleDateFormat simpleDateFormat;
    private DecimalFormat decimalFormat;

    private Calendar dateStart;
    private Calendar dateEnd;
    private BigDecimal amountExpand;
    private BigDecimal amountIncome;
    private BigDecimal amountAll;

    private PieChart chartExpand;
    private PieChart chartIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (LifeManagerApplication) getApplication();
        binding = ActivityBillAuditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();


        Intent intent = getIntent();

        long dateLong = intent.getLongExtra("date", new Date().getTime());

        Calendar date = Calendar.getInstance();
        date.setTime(new Date(dateLong));
        dateStart = Calendar.getInstance();
        dateEnd = Calendar.getInstance();
        dateStart.setTime(date.getTime());
        dateStart.set(Calendar.DAY_OF_MONTH, 1);
        dateStart.set(Calendar.HOUR_OF_DAY, 0);
        dateStart.set(Calendar.MINUTE, 0);
        dateStart.set(Calendar.SECOND, 0);
        dateStart.set(Calendar.MILLISECOND, 0);
        dateEnd.setTime(dateStart.getTime());
        dateEnd.add(Calendar.MONTH, 1);
        dateEnd.add(Calendar.MILLISECOND, -1);

        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_day));
        decimalFormat=new DecimalFormat(getString(R.string.amount_decimal_format));


        initView();
        initListener();
        setContentView(view);
    }

    private void initView(){
        binding.textViewDateStart.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.textViewDateEnd.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        binding.textViewDateStart.setText(simpleDateFormat.format(dateStart.getTime()));
        binding.textViewDateEnd.setText(simpleDateFormat.format(dateEnd.getTime()));

        initPieChart(Bill.EXPAND);
        initPieChart(Bill.INCOME);
        amountExpand= updatePieChart(Bill.EXPAND);
        amountIncome= updatePieChart(Bill.INCOME);
        amountAll=amountIncome.subtract(amountExpand);
        binding.textViewAmountExpand.setText(decimalFormat.format(amountExpand));
        binding.textViewAmountIncome.setText(decimalFormat.format(amountIncome));
        binding.textViewAmountBalance.setText(decimalFormat.format(amountAll));
        if (amountAll.compareTo(new BigDecimal(0))<0){
            binding.textViewBalance.setTextColor(getResources().getColor(R.color.colorTextRed));
            binding.textViewAmountBalance.setTextColor(getResources().getColor(R.color.colorTextRed));
        }
        else {
            binding.textViewBalance.setTextColor(getResources().getColor(R.color.colorTextBlue));
            binding.textViewAmountBalance.setTextColor(getResources().getColor(R.color.colorTextBlue));
        }
    }

    private void initListener() {
        binding.textViewDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                        getResources().getString(R.string.date_picker_title),
                        getResources().getString(R.string.positive),
                        getResources().getString(R.string.negative),
                        null,
                        Locale.getDefault().getLanguage()
                );

                dateTimeDialogFragment.startAtCalendarView();
                dateTimeDialogFragment.set24HoursMode(true);
                dateTimeDialogFragment.setDefaultDateTime(dateStart.getTime());

                dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date newDate) {
                        dateStart.setTime(newDate);
                        checkDate(false);

                    }

                    @Override
                    public void onNegativeButtonClick(Date newDate) {
                    }
                });

                dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");
            }
        });
        binding.textViewDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                        getResources().getString(R.string.date_picker_title),
                        getResources().getString(R.string.positive),
                        getResources().getString(R.string.negative),
                        null,
                        Locale.getDefault().getLanguage()
                );

                dateTimeDialogFragment.startAtCalendarView();
                dateTimeDialogFragment.set24HoursMode(true);
                dateTimeDialogFragment.setDefaultDateTime(dateEnd.getTime());

                dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date newDate) {
                        dateEnd.setTime(newDate);
                        checkDate(true);

                    }

                    @Override
                    public void onNegativeButtonClick(Date newDate) {
                    }
                });

                dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");
            }
        });
    }

    private void checkDate(boolean isEnd) {
        if (isEnd) {
            if (dateStart.compareTo(dateEnd) > 0) {
                dateStart.setTime(dateEnd.getTime());
                dateStart.set(Calendar.DAY_OF_MONTH, 1);
                dateStart.set(Calendar.HOUR_OF_DAY, 0);
                dateStart.set(Calendar.MINUTE, 0);
                dateStart.set(Calendar.SECOND, 0);
                dateStart.set(Calendar.MILLISECOND, 0);
            }
        } else {
            if (dateStart.compareTo(dateEnd) > 0) {
                dateEnd.setTime(dateStart.getTime());
                dateEnd.set(Calendar.HOUR_OF_DAY, 0);
                dateEnd.set(Calendar.MINUTE, 0);
                dateEnd.set(Calendar.SECOND, 0);
                dateEnd.set(Calendar.DAY_OF_MONTH, 1);
                dateEnd.set(Calendar.MILLISECOND, 0);
                dateEnd.add(Calendar.MONTH, 1);
                dateEnd.add(Calendar.MILLISECOND, -1);
            }
        }

        initView();
    }
    private void initPieChart(Integer form){
        PieChart chart;

        switch (form) {
            case Bill.INCOME:
                chart = binding.chartPieIncome;
                break;
            case Bill.EXPAND:
            default:
                chart = binding.chartPieExpand;
                break;
        }

        Legend legend=chart.getLegend();
        chart.getDescription().

                setEnabled(false);
        chart.setExtraOffsets(30,30,30,30);
//        chart.setExtraOffsets(10,10,10,10);

        chart.setDragDecelerationFrictionCoef(0.95f);


        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(

                getResources().

                        getColor(R.color.transparent));

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        chart.setUsePercentValues(false);
        legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
    }
    private BigDecimal updatePieChart(Integer form) {
        PieChart chart;
        int colorId;
        switch (form) {
            case Bill.INCOME:
                chart = binding.chartPieIncome;
                colorId=getResources().getColor(R.color.colorTextBlue);
                break;
            case Bill.EXPAND:
            default:
                chart = binding.chartPieExpand;
                colorId=getResources().getColor(R.color.colorTextRed);
                break;
        }
        Legend legend=chart.getLegend();


        ArrayList<Bill> localDataSet = application.getBillList(dateStart, dateEnd, form);
        TreeMap<String, Float> localDataSetAudit = new TreeMap<>();
        for (Bill bill : localDataSet) {
                localDataSetAudit.put(bill.getType(), localDataSetAudit.getOrDefault(bill.getType(), 0f) + bill.getAmount().floatValue());
        }

        ArrayList<PieEntry> entries = new ArrayList<>();


        for (Map.Entry<String, Float> entry : localDataSetAudit.entrySet()) {
            if (entry.getValue().equals(0f))
                continue;
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }


        legend.setEnabled(true);
        // if entries is empty
        if (entries.size() == 0) {
            entries.add(new PieEntry((float) 0.001, ""));
            legend.setEnabled(false);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        colors.remove(1);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setValueLinePart1OffsetPercentage(90f);
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setColors(colors);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLineColor(colorId);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                return decimalFormat.format(new BigDecimal(value));
            }
        });
        data.setValueTextSize(14f);
        data.setValueTextColor(colorId);
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelColor(colorId);
//        data.setValueTextColor(Color.WHITE);
        chart.setEntryLabelTextSize(14f);


        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);
        chart.animateY(1400, Easing.EaseInOutQuad);
        chart.invalidate();
        return new BigDecimal(data.getYValueSum()+"");
    }
}