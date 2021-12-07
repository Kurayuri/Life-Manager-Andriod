package com.trashparadise.lifemanager;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;


public class Work implements Comparable<Work>, Serializable {
    private String title;
    private Integer repeat;
    private Calendar date;
    private Integer form;
    private String note;
    private String uuid;


    public Work(String title, Calendar date,Integer repeat,Integer form, String note) {
        this.title=title;
        this.repeat=repeat;
        this.date = date;
        this.form = form;
        this.note = note;
        this.uuid = UUID.randomUUID().toString().replaceAll("-","");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getRepeat() {
        return repeat;
    }

    public void setRepeat(Integer repeat) {
        this.repeat = repeat;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Integer getForm() {
        return form;
    }

    public void setForm(Integer form) {
        this.form = form;
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

    @Override
    public int compareTo(Work o) {
        int ans=o.getDate().compareTo(this.getDate());
        return ans == 0 ? o.getUuid().compareTo(this.getUuid()) : ans;
    }

}
