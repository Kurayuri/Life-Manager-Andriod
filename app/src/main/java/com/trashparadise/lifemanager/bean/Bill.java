package com.trashparadise.lifemanager.bean;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;


public class Bill implements Comparable<Bill>, Serializable {
    private BigDecimal amount;  // 金额
    private Calendar date;      // 时间
    private String type;        // 类型名称
    private Integer form;       // 形式，输入/指出
    private String note;        // 备注
    private String uuid;        // uuid

    public static final int ALL=-1;
    public static final int EXPAND=0;
    public static final int INCOME=1;


    public Bill(BigDecimal amount, Calendar date, String type, Integer form, String note) {
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.form = form;
        this.note = note;
        this.uuid = UUID.randomUUID().toString().replaceAll("-","");
    }

    @Override
    public int compareTo(Bill o) {
        int ans=o.getDate().compareTo(this.getDate());
        return ans == 0 ? o.getUuid().compareTo(this.getUuid()) : ans;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        return this.amount.equals(((Bill)obj).getAmount()) &&
                this.date.equals(((Bill)obj).getDate()) &&
                this.type.equals(((Bill)obj).getType()) &&
                this.note.equals(((Bill)obj).getNote()) &&
                this.form.equals(((Bill)obj).getForm()) ;
    }

    public Integer getForm() {
        return form;
    }

    public void setForm(Integer form) {
        this.form = form;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
