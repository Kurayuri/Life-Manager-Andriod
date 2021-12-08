package com.trashparadise.lifemanager;

import androidx.annotation.NonNull;

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
    private String classUuid;

    public static final int EVERY_NONE=0;

    public String getClassUuid() {
        return classUuid;
    }

    public Work clone(){
        Calendar calendar;
        calendar=(Calendar) this.date.clone();
        Work workNew=new Work(this.title,calendar,this.repeat,this.form,this.note);
        workNew.setClassUuid(this.classUuid);
        return workNew;
    }

    public void setClassUuid(String classUuid) {
        this.classUuid = classUuid;
    }

    public static final int EVERY_DAY=7;
    public static final int EVERY_WEEK=4;
    public static final int EVERY_MONTH=2;
    public static final int EVERY_YEAR=1;

    public static final int ALL=-1;
    public static final int TODO=0;
    public static final int DONE=1;




    public Work(String title, Calendar date,Integer repeat,Integer form, String note) {
        this.title=title;
        this.repeat=repeat;
        this.date = date;
        this.form = form;
        this.note = note;
        this.uuid = UUID.randomUUID().toString().replaceAll("-","");
        this.classUuid = UUID.randomUUID().toString().replaceAll("-","");
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
