package com.trashparadise.lifemanager.bean;

public class TypeBean {
    private String name;
    private int icon;
    private int iconGray;
    private int id;
    private boolean checked;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIconGray() {
        return iconGray;
    }

    public void setIconGray(int iconGray) {
        this.iconGray = iconGray;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public TypeBean(String name, int icon, int iconGray, int id) {
        this.name = name;
        this.icon = icon;
        this.iconGray = iconGray;
        this.id = id;
    }

}
