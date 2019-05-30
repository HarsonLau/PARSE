package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



public class Main {

    public static  void main(String[] args) {
        PrepareArffHead("2011-2013train.arff");
        PrepareArffHead("2011-2014test.arff");
        ParseJson();
    }
    public static JsonArray AnalysisBigJson(String content){
        JsonParser parser = new JsonParser();
        JsonArray Jarray = parser.parse(content).getAsJsonArray();
        return Jarray;
    }

    public static  void PrepareArffHead(String Path){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(Path));
            out.write("@relation reHospital\r\n");
            out.write("@attribute pid string\r\n");
            out.write("@attribute GENDER {1,2,3}\r\n");
            out.write("@attribute AGE real\r\n");
            out.write("@attribute BLOOD {1,2,3,4,5}\r\n");
            out.write("@attribute BLOOD_RH {1,2,3}\r\n");
            out.write("@attribute EDUCATION {1,2,3,4,5,6}\r\n");
            out.write("@attribute OCCUPATION {1,2,3,4,5,6,7,8,9,10,11}\r\n");
            out.write("@attribute MARRIAGE {1,2,3,4,5}\r\n");
            out.write("@attribute HosTimes real\r\n");
            out.write("@attribute HosFees real\r\n");
            out.write("@attribute HosMedicine real\r\n");
            out.write("@attribute OutTimes real\r\n");
            out.write("@attribute OutFees real\r\n");
            out.write("@attribute OutMedicine real\r\n");
            out.write("@attribute MaxTimes real\r\n");
            out.write("@attribute Group {0,1}\r\n");
            out.write("@data\r\n");
            out.close();
        }catch (IOException ioe){
            System.out.println("Failed to write to file "+Path+" when preparing arff head");
        }
    }

    public static void ParseJson (){
        String localFile="/home/harson/algProject/dataset/PATIENTINFO.json";
        StringBuffer stringbuffer =new StringBuffer();
        String lineTxt = null;
        InputStreamReader read=null;
        try {
            read=new InputStreamReader( new FileInputStream(localFile),"GBK");
        }catch (FileNotFoundException fnfe){
            System.out.println("File "+localFile+" not found!");
        }catch (UnsupportedEncodingException uee){
            System.out.println("GBK not supported");
        }
        BufferedReader bufferedReader = new BufferedReader(read);
        while(true){
            try {
                lineTxt=bufferedReader.readLine();
            }catch (IOException ioe){
                System.out.println("IOException detected when reading JSON file");
            }
            if(lineTxt==null)
                break;
            stringbuffer.append(lineTxt);
        }
        JsonArray Jarray = AnalysisBigJson(stringbuffer.toString());
        BufferedWriter trainOut=null;
        BufferedWriter testOut=null;

        try{
            trainOut=new BufferedWriter(new FileWriter("2011-2013train.arff",true));
        }catch(IOException ioe){
            System.err.println("Failed to open 2011-2013train.arff");
        }

        try {
            testOut=new BufferedWriter(new FileWriter("2011-2014test.arff",true));
        }catch (IOException ioe){
            System.err.println("Failed to open 2013-2014test.txt");
        }

        for(JsonElement obj : Jarray ){
            JSONObject jsonObject = JSONObject.fromObject(obj.toString());
            try{
                trainOut.write(jsonObject.getString("pid"));
                testOut.write(jsonObject.getString("pid"));
                trainOut.write(",");
                testOut.write(",");
            }catch (IOException ioe){
                System.out.println("failed to write ");
            }
            JSONObject pinfo=jsonObject.getJSONObject("pinfo");

            try{
                trainOut.write(pinfo.getString("GENDER").charAt(0));
                testOut.write(pinfo.getString("GENDER").charAt(0));
                trainOut.write(",");
                testOut.write(",");
            }catch (IOException ioe){
                System.out.println("failed to write ");
            }

            try {
                trainOut.write(getAge(pinfo.getString("BIRTHDAY")));
                testOut.write(getAge(pinfo.getString("BIRTHDAY")));
                trainOut.write(",");
                testOut.write(",");
            }catch (IOException ioe){
                System.out.println("failed to write ");
            }

            try {
                trainOut.write(pinfo.getString("BLOOD").charAt(0));
                testOut.write(pinfo.getString("BLOOD").charAt(0));
                trainOut.write(",");
                testOut.write(",");
            }catch (IOException ioe){
                System.out.println("failed to write ");
            }

            try {
                trainOut.write(pinfo.getString("BLOOD_RH").charAt(0));
                testOut.write(pinfo.getString("BLOOD_RH").charAt(0));
                trainOut.write(",");
                testOut.write(",");
            }catch (IOException ioe){
                System.out.println("failed to write ");
            }

            try {
                trainOut.write(pinfo.getString("EDUCATION").charAt(0));
                testOut.write(pinfo.getString("EDUCATION").charAt(0));
                trainOut.write(",");
                testOut.write(",");
            }catch (IOException ioe){
                System.out.println("failed to write ");
            }

            try {
                trainOut.write(getOccupation(pinfo.getString("OCCUPATION")));
                testOut.write(getOccupation(pinfo.getString("OCCUPATION")));
                trainOut.write(",");
                testOut.write(",");
            }catch (IOException ioe){
                System.out.println("failed to write ");
            }

            try {
                trainOut.write(pinfo.getString("MARRIAGE").charAt(0));
                testOut.write(pinfo.getString("MARRIAGE").charAt(0));
                trainOut.write(",");
                testOut.write(",");
            }catch (IOException ioe){
                System.out.println("failed to write ");
            }


            JSONObject history=jsonObject.getJSONObject("history");
            JSONArray out=history.getJSONArray("out");
            JSONArray hos=history.getJSONArray("hos");
            Map<String, Integer> testCounter = new HashMap<String, Integer>();
            Map<String, Integer> trainCounter = new HashMap<String, Integer>();

            int hosTimesTrain=0;
            int hosTimesTest=0;

            double hosFeeSumTrain=0;
            double hosFeeSumTest=0;

            int hosMedicineTrain=0;
            int hosMedicineTest=0;

            int outTimesTrain=0;
            int outTimesTest=0;

            double  outFeeSumTrain=0;
            double  outFeeSumTest=0;


            int outMedicineTrain=0;
            int outMedicineTest=0;

            int TestCodeTimes=0;
            int TrainCodeTimes=0;

            boolean TestRehos=false;
            boolean TrainRehos=false;



            for(int i=0;i<out.size();i++){
                JSONObject curVisit=out.getJSONObject(i);
                int timePeriod=TimePeriod(curVisit.getString("TIME"));
                if(timePeriod==0){
                    outTimesTrain++;
                    outTimesTest++;
                    outFeeSumTrain +=curVisit.getDouble("FEE");
                    outFeeSumTest+=curVisit.getDouble("FEE");
                    outMedicineTrain+=curVisit.getJSONArray("MEDICINE").size();
                    outMedicineTest+=curVisit.getJSONArray("MEDICINE").size();
                    if(testCounter.containsKey(curVisit.getString("ICD_CODE")))
                        testCounter.put(curVisit.getString("ICD_CODE"),testCounter.get(curVisit.getString("ICD_CODE"))+1);
                    else
                        testCounter.put(curVisit.getString("ICD_CODE"),1);

                    if(trainCounter.containsKey(curVisit.getString("ICD_CODE")))
                        trainCounter.put(curVisit.getString("ICD_CODE"),trainCounter.get(curVisit.getString("ICD_CODE"))+1);
                    else
                        trainCounter.put(curVisit.getString("ICD_CODE"),1);
                }
                if(timePeriod==1){
                    outTimesTest++;
                    outFeeSumTest+=curVisit.getDouble("FEE");
                    outMedicineTest+=curVisit.getJSONArray("MEDICINE").size();
                    if(testCounter.containsKey(curVisit.getString("ICD_CODE")))
                        testCounter.put(curVisit.getString("ICD_CODE"),testCounter.get(curVisit.getString("ICD_CODE"))+1);
                    else
                        testCounter.put(curVisit.getString("ICD_CODE"),1);
                }
            }

            for(int i=0;i<hos.size();i++){
                JSONObject curVisit=hos.getJSONObject(i);
                int timePeriod=TimePeriod(curVisit.getString("TIME"));
                if(timePeriod==0){
                    hosTimesTrain++;
                    hosTimesTest++;
                    hosFeeSumTrain+=curVisit.getDouble("FEE");
                    hosFeeSumTest+=curVisit.getDouble("FEE");
                    hosMedicineTrain+=curVisit.getJSONArray("MEDICINE").size();
                    hosMedicineTest+=curVisit.getJSONArray("MEDICINE").size();
                    if(testCounter.containsKey(curVisit.getString("ICD_CODE")))
                        testCounter.put(curVisit.getString("ICD_CODE"),testCounter.get(curVisit.getString("ICD_CODE"))+1);
                    else
                        testCounter.put(curVisit.getString("ICD_CODE"),1);

                    if(trainCounter.containsKey(curVisit.getString("ICD_CODE")))
                        trainCounter.put(curVisit.getString("ICD_CODE"),trainCounter.get(curVisit.getString("ICD_CODE"))+1);
                    else
                        trainCounter.put(curVisit.getString("ICD_CODE"),1);
                }
                if(timePeriod==1){
                    TrainRehos=true;
                    hosTimesTest++;
                    hosFeeSumTest+=curVisit.getDouble("FEE");
                    hosMedicineTest+=curVisit.getJSONArray("MEDICINE").size();
                    if(testCounter.containsKey(curVisit.getString("ICD_CODE")))
                        testCounter.put(curVisit.getString("ICD_CODE"),testCounter.get(curVisit.getString("ICD_CODE"))+1);
                    else
                        testCounter.put(curVisit.getString("ICD_CODE"),1);
                }
                if(timePeriod==2){
                    TestRehos=true;
                }
            }

            for(Map.Entry<String,Integer>entry:testCounter.entrySet()){
                if(entry.getValue()>TestCodeTimes)
                    TestCodeTimes=entry.getValue();
            }
            for(Map.Entry<String,Integer>entry:trainCounter.entrySet()){
                if(entry.getValue()>TrainCodeTimes)
                    TrainCodeTimes=entry.getValue();
            }

            try {
                trainOut.write(""+hosTimesTrain);
                trainOut.write(",");
                trainOut.write(""+(int)hosFeeSumTrain);
                trainOut.write(",");
                trainOut.write(""+hosMedicineTrain);
                trainOut.write(",");
                trainOut.write(""+outTimesTrain);
                trainOut.write(",");
                trainOut.write(""+(int)outFeeSumTrain);
                trainOut.write(",");
                trainOut.write(""+outMedicineTrain);
                trainOut.write(",");
                trainOut.write(""+TrainCodeTimes);
                trainOut.write(",");
                if(TrainRehos)
                    trainOut.write("1");
                else
                    trainOut.write("0");
                trainOut.write("\r\n");
            }catch (IOException ioe){
                System.out.println("failed to write to train set");
            }

            try {
                testOut.write(""+hosTimesTest);
                testOut.write(",");
                testOut.write(""+(int)hosFeeSumTest);
                testOut.write(",");
                testOut.write(""+hosMedicineTest);
                testOut.write(",");
                testOut.write(""+outTimesTest);
                testOut.write(",");
                testOut.write(""+(int)outFeeSumTest);
                testOut.write(",");
                testOut.write(""+outMedicineTest);
                testOut.write(",");
                testOut.write(""+TestCodeTimes);
                testOut.write(",");
                if(TestRehos)
                    testOut.write("1");
                else
                    testOut.write("0");
                testOut.write("\r\n");
            }catch (IOException ioe){
                System.out.println("failed to write to test set");
            }

        }

        try {
            testOut.close();
            trainOut.close();
        }catch (IOException ioe){
            System.out.println("failed to close files");
        }
    }

    public static String getAge(String Birth){
        int age=0;
        for(int i=0;i<4;i++){
            age=age*10;
            age+=(Birth.charAt(i)-'0');
        }
        age=2019-age;
        String AGE="";
        AGE=AGE+age;
        return AGE;
    }

    public static String getOccupation(String Occ){
        if(Occ.charAt(1)=='.')
            return Occ.substring(0,1);
        else
            return Occ.substring(0,2);
    }

    public static int TimePeriod(String Time){
        int year=0;
        int month=0;
        for(int i=0;i<4;i++){
            year=year*10;
            year+=(Time.charAt(i)-'0');
        }
        for(int i=5;i<7;i++){
            month=month*10;
            month+=(Time.charAt(i)-'0');
        }
        if(year<2013)
            return 0;
        if(year==2013&&month<=8)
            return 0;
        if(year==2013&&month>8)
            return 1;
        if(year==2014&&month<=8)
            return 1;
        return 2;
    }
}
