package com.trashparadise.lifemanager;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;


public class Work implements Comparable<Work>, Serializable {
    private String title;       // 标题
    private Integer repeat;     // 重复策略
    private Calendar date;      // 时间
    private Integer form;       // 形式，未完成/已完成
    private String note;        // 备注
    private String uuid;        // uuid
    private String classUuid;   // 关联项目类uuid

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

    public static final int TITLE=0;
    public static final int FORM=1;
    public static final int REPEAT=2;
    public static final int NOTE=3;
    public static final int DATE=4;
    public static final int UUID=5;
    public static final int CLASSUUID=6;

    public void set(int field,Object object){
        switch (field){
            case TITLE:
                this.setTitle((String) object);
                break;
            case FORM:
                this.setForm((Integer) object);
                break;
            case REPEAT:
                this.setRepeat((Integer) object);
                break;
            case NOTE:
                this.setNote((String) object);
                break;
            case DATE:
                this.setDate((Calendar) object);
                break;
            case UUID:
                this.setUuid((String) object);
                break;
            case CLASSUUID:
                this.setClassUuid((String) object);
                break;
        }
    }


    public Work(String title, Calendar date,Integer repeat,Integer form, String note) {
        this.title=title;
        this.repeat=repeat;
        this.date = date;
        this.form = form;
        this.note = note;
        this.uuid = java.util.UUID.randomUUID().toString().replaceAll("-","");
        this.classUuid = java.util.UUID.randomUUID().toString().replaceAll("-","");
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
