package com.trashparadise.lifemanager.constants;

import android.util.Log;

import com.trashparadise.lifemanager.R;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class BillTypeRes {
    public static String[][] NAMES = new String[][]{{
            "餐饮", "出行", "购物", "日用", "娱乐", "零食", "水果", "烟酒",
            "水电", "宠物", "就医", "运动", "衣物", "教育", "美妆", "育婴"},{

            "薪资", "奖金", "补助", "报销", "红包", "理财", "股票", "基金",
            "兼职", "礼物", "退款"}
    };
    public static int[] COLOR =new int[]{
            R.color.colorTextRed,R.color.colorTextBlue
    };


    public static int[][] ICONS = new int[][]{{
            R.drawable.classify_eat, R.drawable.classify_traffic, R.drawable.classify_shop, R.drawable.classify_dailyuse,
            R.drawable.classify_game, R.drawable.classify_snack, R.drawable.classify_fruit, R.drawable.classify_smoke,
            R.drawable.classify_waterpower, R.drawable.classify_pet, R.drawable.classify_doctor, R.drawable.classify_sport,
            R.drawable.classify_cloth, R.drawable.classify_edu, R.drawable.classify_face, R.drawable.classify_baby},{

            R.drawable.classify_income_wage, R.drawable.classify_income_bonus, R.drawable.classify_income_buzhu, R.drawable.classify_income_baoxiao,
            R.drawable.classify_income_redpacket, R.drawable.classify_income_finance, R.drawable.classify_income_stock, R.drawable.classify_income_jijin,
            R.drawable.classify_income_parttime, R.drawable.classify_income_gift, R.drawable.classify_income_refund}
    };

    public static int[][] ICONS_GRAY = new int[][]{{
            R.drawable.classify_eat_gray, R.drawable.classify_traffic_gray, R.drawable.classify_shop_gray, R.drawable.classify_dailyuse_gray,
            R.drawable.classify_game_gray, R.drawable.classify_snack_gray, R.drawable.classify_fruit_gray, R.drawable.classify_smoke_gray,

            R.drawable.classify_waterpower_gray, R.drawable.classify_pet_gray, R.drawable.classify_doctor_gray, R.drawable.classify_sport_gray,
            R.drawable.classify_cloth_gray, R.drawable.classify_edu_gray, R.drawable.classify_face_gray, R.drawable.classify_baby_gray},{

            R.drawable.classify_income_wage_gray, R.drawable.classify_income_bonus_gray, R.drawable.classify_income_buzhu_gray, R.drawable.classify_income_baoxiao_gray,
            R.drawable.classify_income_redpacket_gray, R.drawable.classify_income_finance_gray, R.drawable.classify_income_stock_gray, R.drawable.classify_income_jijin_gray,
            R.drawable.classify_income_parttime_gray, R.drawable.classify_income_gift_gray, R.drawable.classify_income_refund_gray}};

    public static int[][] ICONS_OTHER = new int[][]{{
            R.drawable.classify_eat, R.drawable.classify_traffic, R.drawable.classify_shop, R.drawable.classify_dailyuse,
            R.drawable.classify_game, R.drawable.classify_snack, R.drawable.classify_fruit, R.drawable.classify_smoke,
            R.drawable.classify_waterpower, R.drawable.classify_pet},{

            R.drawable.classify_income_wage, R.drawable.classify_income_bonus, R.drawable.classify_income_buzhu, R.drawable.classify_income_baoxiao,
            R.drawable.classify_income_redpacket, R.drawable.classify_income_finance, R.drawable.classify_income_stock, R.drawable.classify_income_jijin,
            R.drawable.classify_income_parttime, R.drawable.classify_income_gift, R.drawable.classify_income_refund
    }};

    public static int[][] ICONS_OTHER_GRAY = new int[][]{{
            R.drawable.classify_eat_gray, R.drawable.classify_traffic_gray, R.drawable.classify_shop_gray, R.drawable.classify_dailyuse_gray,
            R.drawable.classify_game_gray, R.drawable.classify_snack_gray, R.drawable.classify_fruit_gray, R.drawable.classify_smoke_gray,
            R.drawable.classify_waterpower_gray, R.drawable.classify_pet_gray},{

            R.drawable.classify_income_wage_gray, R.drawable.classify_income_bonus_gray, R.drawable.classify_income_buzhu_gray, R.drawable.classify_income_baoxiao_gray,
            R.drawable.classify_income_redpacket_gray, R.drawable.classify_income_finance_gray, R.drawable.classify_income_stock_gray, R.drawable.classify_income_jijin_gray,
            R.drawable.classify_income_parttime_gray, R.drawable.classify_income_gift_gray, R.drawable.classify_income_refund_gray}};

    private static TreeMap<String, Integer> treeMap;
    static {
        TreeMap<String, Integer> tmp=new TreeMap<>();
        for (int i=0;i<NAMES.length;++i){
            for (Integer j=0;j<NAMES[i].length;++j){
                tmp.put(i+":"+NAMES[i][j],j);
            }
        }
        treeMap= (TreeMap<String, Integer>) tmp;
    }

    public static Integer getId(Integer form,String type){
        Integer ans=treeMap.get(form+":"+type);
        return ans==null?0:ans;
    }
}
