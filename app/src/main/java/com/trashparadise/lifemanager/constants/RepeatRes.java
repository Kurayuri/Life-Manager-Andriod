package com.trashparadise.lifemanager.constants;

import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.bean.Work;

import java.util.TreeMap;

public class RepeatRes {
    private static TreeMap<Integer, Integer> treeMap;
    static {
        TreeMap<Integer, Integer> tmp=new TreeMap<>();
        tmp.put(Work.EVERY_NONE,R.string.repeat_policy);
        tmp.put(Work.EVERY_DAY,R.string.every_day);
        tmp.put(Work.EVERY_WEEK,R.string.every_week);
        tmp.put(Work.EVERY_MONTH,R.string.every_month);
        tmp.put(Work.EVERY_YEAR,R.string.every_year);
        treeMap= (TreeMap<Integer, Integer>) tmp;
    }


    public static Integer getStringId(Integer repeat){
        Integer ans=treeMap.get(repeat);
        return ans==null?0:ans;
    }
}
