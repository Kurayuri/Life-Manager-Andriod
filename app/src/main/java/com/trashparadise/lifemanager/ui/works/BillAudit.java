package com.trashparadise.lifemanager.ui.works;

import com.trashparadise.lifemanager.Bill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BillAudit {
    public static Map<Integer, BigDecimal> getSum(ArrayList<Bill> billList){
        Map<Integer,BigDecimal> sum=new HashMap<>();
        sum.put(Bill.ALL,new BigDecimal(0));
        sum.put(Bill.EXPAND,new BigDecimal(0));
        sum.put(Bill.INCOME,new BigDecimal(0));
        for (Bill bill:billList){
            sum.put(bill.getForm(),sum.get(bill.getForm()).add(bill.getAmount()));
        }
        return sum;
    }
}
