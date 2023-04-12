package com.up201800388.thesis.Controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.up201800388.thesis.Models.ModelData;
import com.up201800388.thesis.Models.PositionAndIDs;
import com.up201800388.thesis.Services.AnalysisGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


import java.io.IOException;
import java.util.ArrayList;

import com.up201800388.thesis.Models.Comparison;


@Controller
public class FileUploadController implements  Serializable{
    private static final String SERIALIZED_FILE_PATH = System.getProperty("java.io.tmpdir") + "/serialized_result_map.bin";
    private Map<String, List<Map<String, List<ModelData>>>> resultMap;
    private Map<String, List<PositionAndIDs>> APlocationMap;
    private Map<String, double[]> MobileLocationMap;

    private String loadedHash = "";

    @PostConstruct
    public void init() {
        System.out.println("STARTING");
       /* // Load the serialized resultMap into memory on startup
        File serializedFile = new File(SERIALIZED_FILE_PATH);
        if (serializedFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializedFile))) {
                resultMap = (Map<String, List<Map<String, List<ModelData>>>>) ois.readObject();
                loadedHash = generateHash(resultMap);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            resultMap = new HashMap<>();
        }
        */

    }

    public static void saveSerializableObject(Serializable object) {
        File file = new File(SERIALIZED_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/upload-zip")
    public String handleFileUpload(@RequestParam("mobile-location") MultipartFile MobileLocation,
                                   @RequestParam("ap-location") MultipartFile APlocation,
                                   @RequestParam("data") MultipartFile data, Model model) {
        if (data.isEmpty()) {
            model.addAttribute("errorMsg", "File field empty");
            return "error";
        }
        System.out.println("Receiving POST");

        System.out.println("start processAPLocationFile");
        APlocationMap = processAPLocationFile(APlocation);
        System.out.println("end processAPLocationFile");

        System.out.println("start processMobileLocationFile");
        MobileLocationMap = processMobileLocationFile(MobileLocation);
        System.out.println("end processLocationFile");


        Map<String, List<Map<String, List<ModelData>>>> newResultMap = new HashMap<>();
        System.out.println("start processedCsvData");

        try (ZipInputStream zis = new ZipInputStream(data.getInputStream())) {
            ZipEntry zipEntry;

            while ((zipEntry = zis.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    String[] pathParts = zipEntry.getName().split("/");
                    String folderName = pathParts[0];
                    String fileName = pathParts[1];
                    System.out.println(zipEntry.getName());
                    int index = getCsvFileNameIndex(zipEntry.getName());
                    String[] parts = zipEntry.getName().split("/");
                    if (index != -1 && parts[index].replaceAll(".csv", "").length() >= 4) {
                        Map<String, List<ModelData>> processedCsvData = processCsvFile(zis);
                        if (newResultMap.get(parts[index - 1]) == null) {
                            newResultMap.put(parts[index - 1], new ArrayList<>() {{
                                add(processedCsvData);
                            }});

                        } else {
                            newResultMap.get(parts[index - 1]).add(processedCsvData);

                        }
                    }

                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMsg", "Error processing the zip file");

            return "error";
        }
        System.out.println("end processedCsvData");
/*
        if (!loadedHash.equals(generateHash(newResultMap))) {
            System.out.println("new Data");

         //   saveSerializableObject((Serializable) newResultMap);
        } else {
            System.out.println("data equal than the last uploaded");

        }

 */
        resultMap = newResultMap;
        Map<String, List<Double>>[] groundTruth = AnalysisGenerator.generateGroundTruth(APlocationMap, MobileLocationMap);
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, List<PositionAndIDs>> entry : APlocationMap.entrySet() ){
            String key = entry.getKey();
            Integer v = Integer.parseInt(key.split("_")[1]);
            map.put(entry.getValue().get(0).getName(),v);
            map.put(entry.getValue().get(1).getName(),v);

        }

        List<Comparison> comparisonData = AnalysisGenerator.compareGroundTruthAndMeasurements(resultMap, groundTruth,map);
        model.addAttribute("comparisonData", comparisonData);
        return "result";
    }

    private Map<String, List<ModelData>> processCsvFile(ZipInputStream zis) throws IOException {
        Map<String, List<ModelData>> dataMap = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

        for (CSVRecord record : csvParser) {
            if (record.size() == 5) {
                try {
                    long timestamp = Long.parseLong(record.get(0));
                    String id = record.get(1);
                    double measurement = Double.parseDouble(record.get(3));

                    ModelData model = new ModelData(timestamp, measurement);

                    if (!dataMap.containsKey(id)) {
                        dataMap.put(id, new ArrayList<>());
                    }
                    dataMap.get(id).add(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (record.size() == 3) {
                try {
                    long timestamp = Long.parseLong(record.get(0));
                    String id = record.get(1);
                    double measurement = Double.parseDouble(record.get(2));

                    ModelData model = new ModelData(timestamp, measurement);

                    if (!dataMap.containsKey(id)) {
                        dataMap.put(id, new ArrayList<>());
                    }
                    dataMap.get(id).add(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return dataMap;
    }

    public Map<String, List<PositionAndIDs>> processAPLocationFile(MultipartFile locationFile) {
        Map<String, List<PositionAndIDs>> resultMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<Map<String, Object>> rawDataList = objectMapper.readValue(locationFile.getInputStream(), new TypeReference<List<Map<String, Object>>>() {
            });

            for (Map<String, Object> data : rawDataList) {

                String bssid = (String) data.get("BSSID");
                int position = (Integer) data.get("POS");

                String id = (String) data.get("ID");
                double x = (Double) data.get("X");
                double y = (Double) data.get("Y");
                double z = (Double) data.get("Z");


                List<PositionAndIDs> positionAndIDsList = new ArrayList<>();
                positionAndIDsList.add(new PositionAndIDs(bssid, x, y, z));
                positionAndIDsList.add(new PositionAndIDs(id, x, y, z));

                resultMap.put("POS_" + position, positionAndIDsList);

            }

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed, e.g., return an empty result or throw a custom exception
        }

        return resultMap;
    }


    public Map<String, double[]> processMobileLocationFile(MultipartFile mobileLocationFile) {
        Map<String, double[]> resultMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<Map<String, Object>> rawData = objectMapper.readValue(mobileLocationFile.getInputStream(), new TypeReference<List<Map<String, Object>>>() {
            });

            for (Map<String, Object> data : rawData) {
                String name = (String) data.get("name");
                double x = (Double) data.get("X");
                double y = (Double) data.get("Y");
                double z = (Double) data.get("Z");

                double[] coordinates = new double[]{x, y, z};
                resultMap.put(name, coordinates);
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed, e.g., return an empty result or throw a custom exception
        }

        return resultMap;
    }

    private String generateHash(Map<String, List<Map<String, List<ModelData>>>> resultMap) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(resultMap);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(jsonString.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static int getCsvFileNameIndex(String filePath) {
        String[] parts = filePath.split("/");

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].contains(".csv")) {
                return i;
            }
        }

        return -1; // Return -1 if no .csv file is found in the path
    }
}
