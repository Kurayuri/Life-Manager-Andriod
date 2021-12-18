package com.trashparadise.lifemanager.ui.bills;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.trashparadise.lifemanager.bean.Bill;
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

public class BillAuditActivity extends AppCompatActivity   implements OnChartValueSelectedListener {
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
        initBarChart(Bill.EXPAND);
        initBarChart(Bill.INCOME);
        updateBarChart(Bill.EXPAND);
        updateBarChart(Bill.INCOME);
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

    private void initBarChart(Integer form){
        BarChart chart;

        switch (form) {
            case Bill.INCOME:
                chart = binding.chartBarIncome;
                break;
            case Bill.EXPAND:
            default:
                chart = binding.chartBarExpand;
                break;
        }

        Legend legend=chart.getLegend();
        chart.getDescription().
                setEnabled(false);
//        chart.setExtraOffsets(30,30,30,30);
//        chart.setExtraOffsets(10,10,10,10);

        chart.setDragDecelerationFrictionCoef(0.95f);



        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        chart.setScaleEnabled(false);
//        chart.setBottom(0);
        // chart.setDrawYLabels(false);

//        ValueFormatter xAxisFormatter = new DayAxisValueFormatter();

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
//        xAxis.setValueFormatter(xAxisFormatter);

//        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = chart.getAxisRight();
        leftAxis.setEnabled(false);
//
//        leftAxis.setLabelCount(8, false);
////        leftAxis.setValueFormatter(custom);
//        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
//        leftAxis.setSpaceTop(15f);
//        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        leftAxis.setAxisMaximum(10);


        Legend l = chart.getLegend();
        l.setEnabled(false);

        XYMarkerView mv = new XYMarkerView(this, null);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart
    }
    private BigDecimal updateBarChart(Integer form) {
        BarChart chart;
        int colorId;
        switch (form) {
            case Bill.INCOME:
                chart = binding.chartBarIncome;
                colorId=getResources().getColor(R.color.colorTextBlue);
                break;
            case Bill.EXPAND:
            default:
                chart = binding.chartBarExpand;
                colorId=getResources().getColor(R.color.colorTextRed);
                break;
        }
        Legend legend=chart.getLegend();


        ArrayList<Bill> localDataSet = application.getBillList(dateStart, dateEnd, form);
        TreeMap<Long, Float> localDataSetAudit = new TreeMap<>();

        for (Long i=new Long(dateStart.get(Calendar.DATE));i<=dateEnd.get(Calendar.DATE);++i){
            localDataSetAudit.put(i,0f);
        }

        for (Bill bill : localDataSet) {
            Long time=new Long(bill.getDate().get(Calendar.DATE));
            localDataSetAudit.put(time,localDataSetAudit.get(time) + bill.getAmount().floatValue());
        }

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (Map.Entry<Long, Float> entry : localDataSetAudit.entrySet()) {
            if (entry.getValue().equals(0f))
                entry.setValue(0.004f);
//                continue;
            entries.add(new BarEntry(entry.getKey(),entry.getValue().floatValue()));
        }
//        int start=1;
//        int count=10;
//        int range=20;
//        for (int i = (int) start; i < start + count; i++) {
//            float val = (float) (Math.random() * (range + 1));
//
//            if (Math.random() * 100 < 25) {
//                entries.add(new BarEntry(i, val));
//            } else {
//                entries.add(new BarEntry(i, val));
//            }
//        }

//        legend.setEnabled(true);
//        // if entries is empty
//        if (entries.size() == 0) {
//            entries.add(new BarEntry((float) 0.001, ""));
//            legend.setEnabled(false);
//        }

        BarDataSet dataSet = new BarDataSet(entries, "");

        dataSet.setDrawIcons(false);


        dataSet.setIconsOffset(new MPPointF(0, 40));

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//        colors.remove(1);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);

//        colors.add(ColorTemplate.getHoloBlue());
//        dataSet.setValueLinePart1OffsetPercentage(90f);
//        dataSet.setValueLinePart1Length(0.3f);
//        colors.add(getResources().getColor(R.color.colorTextRed));
//        dataSet.setValueLinePart2Length(0.4f);
        colors.add(colorId);
        dataSet.setColors(colors);
//        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        dataSet.setValueLineColor(colorId);
//        dataSet.setSelectionShift(5f);

        BarData data = new BarData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                return decimalFormat.format(new BigDecimal(value));
            }
        });
        data.setValueTextSize(14f);
        data.setValueTextColor(colorId);
        data.setDrawValues(false);
//        chart.setEntryLabelColor(Color.WHITE);
//        chart.setEntryLabelColor(colorId);
//        data.setValueTextColor(Color.WHITE);
//        chart.setEntryLabelTextSize(14f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.resetAxisMaximum();
        leftAxis.setLabelCount(8, false);
//        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setSpaceBottom(0);



        chart.setOnChartValueSelectedListener(this);
        chart.setData(data);


        // undo all highlights
//        chart.highlightValues(null);
        chart.animateY(1400, Easing.EaseInOutQuad);
        chart.invalidate();
        return new BigDecimal(0);
    }

    private final RectF onValueSelectedRectF = new RectF();

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

        BarChart chart=binding.chartBarExpand;
        RectF bounds = onValueSelectedRectF;
        chart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = chart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + chart.getLowestVisibleX() + ", high: "
                        + chart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() { }
}