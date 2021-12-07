package com.trashparadise.lifemanager.constants;

import com.trashparadise.lifemanager.R;

import java.util.Calendar;
import java.util.TreeMap;

public class RepeatRes {
    private static TreeMap<Integer, Integer> treeMap;
    static {
        TreeMap<Integer, Integer> tmp=new TreeMap<>();
        tmp.put(0,R.string.repeat_policy);
        tmp.put(Calendar.DAY_OF_WEEK,R.string.every_day);
        tmp.put(Calendar.WEEK_OF_MONTH,R.string.every_week);
        tmp.put(Calendar.MONTH,R.string.every_month);
        tmp.put(Calendar.YEAR,R.string.every_year);
        treeMap= (TreeMap<Integer, Integer>) tmp;
    }


    public static Integer getStringId(Integer repeat){
        Integer ans=treeMap.get(repeat);
        return ans==null?0:ans;
    }
}
