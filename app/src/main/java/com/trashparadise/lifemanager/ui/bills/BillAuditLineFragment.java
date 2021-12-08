package com.trashparadise.lifemanager.ui.bills;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.trashparadise.lifemanager.Bill;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.databinding.FragmentBillAuditLineBinding;

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


public class BillAuditLineFragment extends Fragment {
    private LineChart chart;
    private FragmentBillAuditLineBinding binding;
    private LifeManagerApplication application;
    private Calendar date;
    private Integer form;
    private SimpleDateFormat dateFormat;
    private DecimalFormat decimalFormat;
    private Legend legend;


    public BillAuditLineFragment(Calendar date, Integer form){
        this.date=date;
        this.form=form;
    }
    public BillAuditLineFragment(){
        this.date=Calendar.getInstance();
        this.form=-1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentBillAuditLineBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        application=(LifeManagerApplication)getActivity().getApplication();
        dateFormat = new SimpleDateFormat(getString(R.string.date_format_month));
        decimalFormat=new DecimalFormat(getString(R.string.amount_decimal_format_unit));

        chart=binding.chart;

        {
            chart.getDescription().setEnabled(false);
            chart.setExtraOffsets(5, 5, 5, 5);

            chart.setDragDecelerationFrictionCoef(0.95f);



            chart.setViewPortOffsets(0, 0, 0, 0);
            chart.setBackgroundColor(Color.rgb(104, 241, 175));

            // no description text
            chart.getDescription().setEnabled(false);

            // enable touch gestures
            chart.setTouchEnabled(true);

            // enable scaling and dragging
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            chart.setPinchZoom(false);

            chart.setDrawGridBackground(false);
            chart.setMaxHighlightDistance(300);

            XAxis x = chart.getXAxis();
            x.setEnabled(false);

            YAxis y = chart.getAxisLeft();
            y.setLabelCount(6, false);
            y.setTextColor(Color.WHITE);
            y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            y.setDrawGridLines(false);
            y.setAxisLineColor(Color.WHITE);

            chart.getAxisRight().setEnabled(false);



            chart.getLegend().setEnabled(false);

            chart.animateXY(2000, 2000);


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
        Calendar dateE=(Calendar) dateStart.clone();

        ArrayList<Bill> localDataSet=application.getBillList(dateStart,dateEnd,0);
        TreeMap<Integer, Float>  localDataSetAudit=new TreeMap<>();
        while (dateE.compareTo(dateEnd)<0){
            localDataSetAudit.put(dateE.get(Calendar.DATE),0f);
            dateE.add(Calendar.DATE,1);
        }




        for (Bill bill:localDataSet) {
            localDataSetAudit.put(bill.getDate().get(Calendar.DATE),localDataSetAudit.get(bill.getDate().get(Calendar.DATE))+bill.getAmount().floatValue());
        }

        ArrayList<Entry> entries = new ArrayList<>();

        float sum=0f;

        for (Map.Entry<Integer,Float> entry:localDataSetAudit.entrySet()) {
            sum+=entry.getKey();
            entries.add(new Entry(entry.getKey(),entry.getValue().floatValue()));
        }



        // if entries is empty
//        legend.setEnabled(true);
//        chart.setCenterText(dateFormat.format(date.getTime()));
//        if (entries.size()==0){
//            entries.add(new Entry((float) 0.001,""));
//            legend.setEnabled(false);
//            chart.setCenterText(chart.getCenterText()+"\n"+getString(R.string.empty));
//        }

        LineDataSet dataSet = new LineDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);
        dataSet.setDrawFilled(true);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(1.8f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet.setColor(Color.WHITE);
        dataSet.setFillColor(Color.WHITE);
        dataSet.setFillAlpha(100);
        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });



        LineData data = new LineData(dataSet);
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

        binding.textViewAmount.setText(decimalFormat.format(sum));
    }


}