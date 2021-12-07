package com.trashparadise.lifemanager;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


public class Bill implements Comparable<Bill>, Serializable {
    private BigDecimal amount;
    private Calendar date;
    private String type;
    private Integer form;
    private String note;
    private String uuid;



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
