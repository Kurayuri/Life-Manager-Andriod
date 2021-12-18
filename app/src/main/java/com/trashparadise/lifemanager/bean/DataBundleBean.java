package com.trashparadise.lifemanager.bean;

import java.util.TreeSet;

public class DataBundleBean {
    private TreeSet<Bill> billList;
    private TreeSet<Work> workList;
    private TreeSet<Contact> contactList;
    private Preference preference;

    public DataBundleBean(TreeSet<Bill> billList, TreeSet<Work> workList, TreeSet<Contact> contactList, Preference preference) {
        this.billList = billList;
        this.workList = workList;
        this.contactList = contactList;
        this.preference = preference;
    }

    public TreeSet<Bill> getBillList() {
        return billList;
    }

    public void setBillList(TreeSet<Bill> billList) {
        this.billList = billList;
    }

    public TreeSet<Work> getWorkList() {
        return workList;
    }

    public void setWorkList(TreeSet<Work> workList) {
        this.workList = workList;
    }

    public TreeSet<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(TreeSet<Contact> contactList) {
        this.contactList = contactList;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }
}
