package com.trashparadise.lifemanager.ui.bills;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.trashparadise.lifemanager.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

@SuppressLint("ViewConstructor")
public class XYMarkerView extends MarkerView {

    private final TextView tvContent;
    private final ValueFormatter xAxisValueFormatter;

    private final DecimalFormat format;
    private final SimpleDateFormat simpleDateFormat;

    public XYMarkerView(Context context, ValueFormatter xAxisValueFormatter) {
        super(context, R.layout.markar_view);

        this.xAxisValueFormatter = xAxisValueFormatter;
        tvContent = findViewById(R.id.tvContent);
        format = new DecimalFormat(context.getResources().getString(R.string.amount_decimal_format));
        simpleDateFormat=new SimpleDateFormat(context.getResources().getString(R.string.date_format_day));
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        tvContent.setText(String.format("%s: %s",xAxisValueFormatter.getFormattedValue(e.getX()),format.format(e.getY())));

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
