package com.trashparadise.lifemanager.ui.bills;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.trashparadise.lifemanager.Bill;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.databinding.FragmentBillAuditPieBinding;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;


public class BillAuditPieFragment extends Fragment {
    private PieChart chart;
    private FragmentBillAuditPieBinding binding;
    private LifeManagerApplication application;
    private Calendar date;
    private Integer form;
    private SimpleDateFormat dateFormat;
    private Legend legend;


    public BillAuditPieFragment(Calendar date, Integer form){
        this.date=date;
        this.form=form;
    }
    public BillAuditPieFragment(){
        this.date=Calendar.getInstance();
        this.form=-1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentBillAuditPieBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        application=(LifeManagerApplication)getActivity().getApplication();
        dateFormat = new SimpleDateFormat(getString(R.string.date_format_month));

        chart=binding.chart;

        {
            chart.getDescription().setEnabled(false);
            chart.setExtraOffsets(5, 5, 5, 5);

            chart.setDragDecelerationFrictionCoef(0.95f);

            chart.setCenterText(dateFormat.format(date.getTime()));
            chart.setCenterTextSize(16);
            chart.setCenterTextColor(getResources().getColor(R.color.colorPrimary));
            chart.setDrawHoleEnabled(true);
            chart.setHoleColor(Color.WHITE);

            chart.setTransparentCircleColor(Color.WHITE);
            chart.setTransparentCircleAlpha(110);

            chart.setHoleRadius(58f);
            chart.setTransparentCircleRadius(61f);

            chart.setDrawCenterText(true);

            chart.setRotationAngle(0);
            // enable rotation of the chart by touch
            chart.setRotationEnabled(true);
            chart.setHighlightPerTapEnabled(true);

            chart.setUsePercentValues(false);
//            chart.animateY(1400, Easing.EaseInOutQuad);
            legend = chart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setDrawInside(true);
            legend.setXEntrySpace(7f);
            legend.setYEntrySpace(0f);
            legend.setYOffset(0f);

            // add a selection listener
//            chart.setOnChartValueSelectedListener(this);
        }

        updateData();

        return view;
    }


    public void callUpdateData(){
        updateData();
    }

    private void updateData() {
        chart.animateY(1400, Easing.EaseInOutQuad);
        Calendar dateStart=Calendar.getInstance();
        Calendar dateEnd=Calendar.getInstance();
        dateStart.setTime(date.getTime());
        dateStart.set(Calendar.DAY_OF_MONTH,1);
        dateStart.set(Calendar.HOUR_OF_DAY, 0);
        dateStart.set(Calendar.MINUTE, 0);
        dateStart.set(Calendar.SECOND, 0);
        dateStart.set(Calendar.MILLISECOND, 0);
        dateEnd.setTime(dateStart.getTime());
        dateEnd.add(Calendar.MONTH,1);

        ArrayList<Bill> localDataSet=application.getBillList(dateStart,dateEnd,0);
        TreeMap<String, Float>  localDataSetAudit=new TreeMap<>();
        Float float0=new Float(0);
        for (Bill bill:localDataSet) {
            localDataSetAudit.put(bill.getType(),localDataSetAudit.getOrDefault(bill.getType(),float0)+bill.getAmount().floatValue());
        }

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String,Float> entry:localDataSetAudit.entrySet()) {
            if (entry.getValue().equals(float0))
                continue;
            entries.add(new PieEntry(entry.getValue().floatValue(),entry.getKey()));
        }

        // if entries is empty
        legend.setEnabled(true);
        chart.setCenterText(dateFormat.format(date.getTime()));
        if (entries.size()==0){
            entries.add(new PieEntry((float) 0.001,""));
            legend.setEnabled(false);
            chart.setCenterText(chart.getCenterText()+"\n"+getString(R.string.empty));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
//        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat decimalFormat=new DecimalFormat("0.00");
                return decimalFormat.format(new BigDecimal(value));
            }
        });
        data.setValueTextSize(11f);
//        data.setValueTextColor(Color.BLACK);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }


}