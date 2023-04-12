package com.up201800388.thesis.Services;
import com.up201800388.thesis.Models.Comparison;
import com.up201800388.thesis.Models.ModelData;
import com.up201800388.thesis.Models.PositionAndIDs;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AnalysisGenerator {
    public static Map<String, List<Double>>[] generateGroundTruth(Map<String, List<PositionAndIDs>> positionsAndIDsMap, Map<String, double[]> mobileLocations) {
        Map<String, List<Double>> idsDistances = new HashMap<>();
        Map<String, List<Double>> bssidDistances = new HashMap<>();

        // Convert the set to a list
        List<String> keysList = new ArrayList<>(mobileLocations.keySet());

        // Perform a natural sort on the list of keys
        keysList.sort((key1, key2) -> {
            int expNumber1 = Integer.parseInt(key1.split("_")[1]);
            int expNumber2 = Integer.parseInt(key2.split("_")[1]);
            return Integer.compare(expNumber1, expNumber2);
        });


        for (String expKey : keysList) {
            double[] mobileLocation = mobileLocations.get(expKey);


            for(int i = 0; i<positionsAndIDsMap.size();i++){
                List<PositionAndIDs> positionAndIDsList = positionsAndIDsMap.get("POS_"+i);

                double bssidDistance = calculateEuclideanDistance(mobileLocation, positionAndIDsList.get(0));
                double idDistance = calculateEuclideanDistance(mobileLocation, positionAndIDsList.get(1));

                bssidDistances.computeIfAbsent(expKey, k -> new ArrayList<>()).add(bssidDistance);
                idsDistances.computeIfAbsent(expKey, k -> new ArrayList<>()).add(idDistance);
            }
        }

        // Combine the two maps as needed or return them separately
        // In this example, we return the IDs distances map
        Map<String, List<Double>>[] arr = new Map[]{idsDistances, bssidDistances};
        return arr;
    }
    private static double calculateEuclideanDistance(double[] mobileLocation, PositionAndIDs positionAndIDs) {
        double x1 = mobileLocation[0];
        double y1 = mobileLocation[1];
        double z1 = mobileLocation[2];

        double x2 = positionAndIDs.getX();
        double y2 = positionAndIDs.getY();
        double z2 = positionAndIDs.getZ();

        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
    }
    public static List<Comparison> compareGroundTruthAndMeasurements(Map<String, List<Map<String, List<ModelData>>>> modelDataMap, Map<String, List<Double>>[] groundTruthMap, Map<String, Integer> map) {
        List<Comparison> comparisons = new ArrayList<>();

        for (String expKey : modelDataMap.keySet()) {
            List<Map<String, List<ModelData>>> list = modelDataMap.get(expKey);
            for (Map<String, List<ModelData>>  innerMap: list) {
                for (String idKey : innerMap.keySet()) {
                    List<ModelData> modelDataList = innerMap.get(idKey);
                    List<Double> groundTruthListIDS = groundTruthMap[0].get(expKey);
                    List<Double> groundTruthListBSSIDS = groundTruthMap[1].get(expKey);

                    for (int i = 0; i < modelDataList.size() ; i++) {
                        ModelData modelData = modelDataList.get(i);
                        double groundTruth = groundTruthListIDS.get(map.get(idKey));
                        Comparison comparison = new Comparison(expKey, idKey, groundTruth, modelData.getMeasurement());
                        comparisons.add(comparison);
                    }
                }
            }
        }

        return comparisons;
    }
}
