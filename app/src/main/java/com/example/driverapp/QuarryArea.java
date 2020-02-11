package com.example.driverapp;

import android.database.Cursor;
import android.os.AsyncTask;


import java.util.List;

import static com.example.driverapp.DriverMapActivity.table1Db;


public class QuarryArea {
    public static final String LOCAL_URL = "http://192.248.14.80:4000/androidApp/locations/21"; // back end URL
    public static String URL = "http://192.248.14.80:4000/androidApp/locations/21";




    public int quarryarea(String area){

        // 1. Check the data of new area already available.
        // 2. If available, fetch all data and insert to temporary table
        // 3. If not available, get data from the server

        System.out.println("qarea " );
        Cursor res = table1Db.fetchAvalarea(area);
        System.out.println("qarea "+res.getCount() );
        if (res.getCount() == 0) {
            //make this area available
            table1Db.insertAvalarea(area,"1");

            //generate url
            URL = LOCAL_URL + area;

            //get data from server
            new LocationLoader().execute();

            //populate the temporary table
            populateTempTable(area);

        }else{
            //populate the temporary table
            populateTempTable(area);
            }
        return 0;
    }


    public void populateTempTable(String area) {
        Integer distance = 80;
        int n = 1;

        Cursor res = table1Db.getAllData();
        System.out.println("counting " + res.getCount());
        long countTB1 = table1Db.getRoadSignCount();
        long counttemp = DriverMapActivity.tempTable.getRoadSignCount();
        if(countTB1>counttemp){
            DriverMapActivity.tempTable.deleteAll();
            while (res.moveToNext()) {              //Add data to temp table
                String Table_id = res.getString(0);
                String area_id = res.getString(1);
                String Roadsign = res.getString(2);
                String Longi = res.getString(3);
                String Lati = res.getString(4);
                String Heading = res.getString(5);//heading will push data to relevant database
                System.out.println("area id " + area_id);

                System.out.println("area id " + Roadsign);
                DriverMapActivity.tempTable.insertData(n, Roadsign, Longi, Lati, distance, Double.valueOf(Heading), "tempTableA");
                n = n + 1;
                System.out.println("inserted");
            }
        }


    }

    private class LocationLoader extends AsyncTask<String, Void, List<List<String>>> {
        private List<List<String>> locationList;

        @Override
        protected List<List<String>> doInBackground(String... strings) {
            String jsonString = QueryUtil.getJsonString(LOCAL_URL);
            System.out.println("JSON   "+jsonString);
            return QueryUtil.ParseJson(jsonString);

        }

        @Override
        protected void onPostExecute(List<List<String>> result) {
            locationList = result;
            System.out.println(locationList);
            System.out.println("size "+ locationList.size());

            getData();
        }


        public void getData() {
            if (locationList == null) return;

            String RSlastid = "0";
            for (int i = 0; i < locationList.size(); i++) {
                System.out.println(locationList);
                String latitude = locationList.get(i).get(0);
                String longitude = locationList.get(i).get(1);
                String head = locationList.get(i).get(2);
                String sign = locationList.get(i).get(3);
                dataStoreInDatabase(latitude, longitude, head, sign,RSlastid);
                System.out.println(sign+" - "+head);
            }

        }
        public void dataStoreInDatabase(String Lati, String Longi, String Head, String Sign,String RSlastid){
            String area = "";

            Cursor res = table1Db.fetchprev("1");
            while (res.moveToNext()) {              //Add data to temp table
                String id = res.getString(0);
                area = res.getString(1);
            }

            table1Db.insertData(area,Sign,Longi,Lati,Head);       //Insert data to data base
      }
    }
}

