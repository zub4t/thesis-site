package com.up201800388.thesis.Models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputFile {
    private List<Comparison> comparisonData;
    private List<String> listIDsAndBSSIDS ;
    private List<String> listExp;
    private Map<String, double[]> MobileLocationMap;


    public Map<String, double[]> getMobileLocationMap() {
        return MobileLocationMap;
    }

    public void setMobileLocationMap(Map<String, double[]> mobileLocationMap) {
        MobileLocationMap = mobileLocationMap;
    }

    public List<Comparison> getComparisonData() {
        return comparisonData;
    }

    public void setComparisonData(List<Comparison> comparisonData) {
        this.comparisonData = comparisonData;
    }

    public List<String> getListIDsAndBSSIDS() {
        return listIDsAndBSSIDS;
    }

    public void setListIDsAndBSSIDS(List<String> listIDsAndBSSIDS) {
        this.listIDsAndBSSIDS = listIDsAndBSSIDS;
    }

    public List<String> getListExp() {
        return listExp;
    }

    public void setListExp(List<String> listExp) {
        this.listExp = listExp;
    }


}
