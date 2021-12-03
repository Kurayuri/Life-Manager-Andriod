package com.trashparadise.lifemanager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class Bill {
    private BigDecimal amount;
    private Date date;
    private String type;
    private String note;
    private String uuid;

    public Bill(BigDecimal amount, Date date, String type, String note) {
        this.amount = amount;
        this.date = date;
        this.type=type;
        this.note = note;
        this.uuid = UUID.randomUUID().toString().replaceAll("-","");
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
