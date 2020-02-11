package com.example.driverapp;
// Danuka

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.hardware.SensorManager;
import android.icu.text.SymbolTable;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static java.lang.Double.parseDouble;
import static java.lang.Math.abs;


public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    TextToSpeech tts;                        //Speech variables
    private static final String LOCAL_URL = "http://activeroadsigns.projects.uom.lk/androidApp/locations/21"; // back end URL

    public DatabaseHelper myDb;                    //Local database ghost
    public static DatabaseTable1 table1Db;                //Local database table1 ghost
    public static TemporaryTable tempTable;               //Local database tempTable ghost
    QuarryArea Qarea;                       //Quary area ghost
    ImageData imageData;

    private GoogleMap mMap;                 //Map instance
    GoogleApiClient mGoogleApiClient;       //Google API caller
    Location mLastLocation;                 //Last location variable
    LocationRequest mLocationRequest;       //Location request variable
    private ImageView mLogout;                 //Logout button
    private TextView mTime, mMsg,mHeading;
    private Boolean isLoggingout;           //Logout status variable
    public LatLng preLocation;          //a location variable
    private TextView signTxt;               //TextView for the Road sign
    //private TextView scanDetails;           //Scan Details on the bottom
    private TextView timer1, timer2, timer3, timerTest;
    private TextView distanceTxt;
    private TextView headingTextView;
    private LinearLayout signlayout;

    public int audio = 1;
    private ImageView signView1, imageView2, signView2, signView3, mSignImage1, mSignImage2;
    private SensorManager sensorManager;    //initiate sensors
    private int slot = 0;
    public static Integer areaprev = 0;     //this variable to store previous area value.
    public static double flag = 0;         //this flag to remember our direction of the roadsign
    public static Integer tempID = 0;       //this id is used to keep colsest road sign id
    private String temp = "";
    public double h = 0.0;
    public double htm1 = 0.0;
    public double htm2 = 0.0;
    public static int resPrev = 0;


    int ti;
    //WiFi Scanning
    private WifiManager wifiManager;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    Thread task;

    long unixTime;

    //Decryption
    String decryptedString;

    String key1 = "2b7e151628aed2a6abf7158809cf4f3c"; //hexkey
    String iv1 = "000102030405060708090a0b0c0d0e0f"; //hexiv
    String key2 = "K34VFiiu0qar9xWICc9PPA==";//base64key
    String iv2 = "AAECAwQFBgcICQoLDA0ODw==";//base64iv


    Calendar calendar;


    //SSID Decryption
    double beacontime, prevbeacontime, beaconhead;
    String[] alnum = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "1", "2", "3", "4", "5", "6", "7", "8"};
    ArrayList<String> obj = new ArrayList<String>(Arrays.asList(alnum));
    Map<String, String> signList = new HashMap<String, String>();
    Map<String, Integer> drawableList = new HashMap<String, Integer>();

    private int[] images = {R.drawable.nosign, R.drawable.mns01, R.drawable.mns02, R.drawable.mns03, R.drawable.mns04, R.drawable.mns05, R.drawable.zc02
            , R.drawable.phs09, R.drawable.osd06, R.drawable.rss05, R.drawable.a,R.drawable.dws_15,R.drawable.dws_33};
    private String[] roadSigns = {"", "turn left", "turn right", "Go straight", "go to the left", "go to the right", "pedestrian crossing",
            "no horning", "bus stop", "speed limit, fifty kilometers per hour", "pedestrian crossing ahead","'T' Junction Ahead","Children crossing Ahead"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        signList= new ImageData().getSignList();
        drawableList = new ImageData().getDrawableList();
        myDb = new DatabaseHelper(this);                        //DatabaseHelper object create
        table1Db = new DatabaseTable1(this);                    //DatabaseTable1 object create
        tempTable = new TemporaryTable(this);                   //TemporaryTable object create
        Qarea = new QuarryArea();

        mLogout = (ImageView) findViewById(R.id.logout);
        signView1 = (ImageView) findViewById(R.id.signView1);
        signView2 = (ImageView) findViewById(R.id.signView2);
        signView3 = (ImageView) findViewById(R.id.signView3);
        mSignImage1 = (ImageView) findViewById(R.id.imageView);
        mSignImage2 = (ImageView) findViewById(R.id.imageView2);
        mTime = (TextView) findViewById(R.id.time);
        mHeading = (TextView) findViewById(R.id.head);
        imageView2 = (ImageView) findViewById(R.id.audio);
        signTxt = findViewById(R.id.signTxt);
        //scanDetails = findViewById(R.id.scanDetails);
        timer1 = findViewById(R.id.timer1);
        timer2 = findViewById(R.id.timer2);
        timer3 = findViewById(R.id.timer3);
        timerTest = findViewById(R.id.timerTest);
        distanceTxt = findViewById(R.id.distanceTxt);
        headingTextView = findViewById(R.id.headingTextView);
        signlayout =findViewById(R.id.signLayout);

        preLocation = new LatLng(0.0, 0.0);
        imageView2.setImageResource(R.drawable.volume_up);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audio == 1) {
                    imageView2.setImageResource(R.drawable.volume_off);
                    audio = 0;
                } else {
                    imageView2.setImageResource(R.drawable.volume_up);
                    audio = 1;
                }
            }
        });


        // Map layout logout

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoggingout = true;
                Intent intent = new Intent(DriverMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                // write loging out logic .......................................................................
                return;
            }
        });

        // Text to speech // sanda
        tts = new TextToSpeech(DriverMapActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This Language is not supported");
                    } else {
                        if (audio == 1) {
                            ConvertTextToSpeech("");
                        }
                    }
                } else
                    Log.e("error", "Initilization Failed!");
            }
        });

        //getting data from online server

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
//        listView.setAdapter(adapter);
//        scanWifi();


        //Initialize the previous area which the driver stop the vehicle
        Cursor res = table1Db.fetchprev("1");
        if(res ==null){
            table1Db.insertPrev("1","1");
            areaprev = 1;
            System.out.println("areaPrev 1");
        }else {
            if (res.getCount() == 0) {
                table1Db.insertPrev("1","1");
                areaprev = 1;
                System.out.println("areaPrev 2");
            }else{
                while (res.moveToNext()) {
                    int TABLE_ID = Integer.parseInt(res.getString(0));
                    areaprev = Integer.parseInt(res.getString(1));
                    System.out.println("areaPrev "+areaprev);

                }
            }
        }



        task = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(900);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    scanWifi();
                }
            }
        });
        task.start();

    }


    //Wifi scaning
    private void scanWifi() {
        arrayList.clear();

        calendar = Calendar.getInstance();

        long hour=calendar.get(Calendar.HOUR_OF_DAY);
        long min=calendar.get(Calendar.MINUTE);
        long sec=calendar.get(Calendar.SECOND);

        unixTime= hour*3600+min*60+sec;

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
//        unixTime = System.currentTimeMillis()/60000L;
    }

    private String decryptCFB(String encryptedtext, String key, String iv) throws Exception{


        //byte [] bytekey = decryptkey.getBytes();
        //byte [] byteiv = iv.getBytes();

        byte[] bytekey = Base64.decode(String.valueOf(key), Base64.DEFAULT);
        byte[] byteiv = Base64.decode(String.valueOf(iv), Base64.DEFAULT);

        SecretKeySpec keySpec = new SecretKeySpec(bytekey, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(byteiv);

        Cipher c = Cipher.getInstance("AES/CFB/NoPadding");
        c.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        byte[] decodedValue = Base64.decode(String.valueOf(encryptedtext), Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);

        String decryptedValue = new String(decValue);
        decryptedString = Base64.encodeToString(decValue, Base64.DEFAULT);
        return decryptedString;
    }

    private String decrypt(String ssid) {
        String timers="";
        try {
            String encryptedSSID= ssid.substring(0,24);
            timers=ssid.substring(24);
            //System.out.println("Timers::"+timers);
            //System.out.println("ENC:"+encryptedSSID);
            ssid=decryptCFB(encryptedSSID, key2, iv2);
            ssid = ssid.substring(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println("DEC:"+ssid);
        int key = 11;
        String message = "";

        String[] alnum = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "1", "2", "3", "4", "5", "6", "7", "8"};

        String timest = ssid.substring(0,4);
        String lat_new = ssid.substring(4, 10);
        String lon_new = ssid.substring(10, 16);
        String headin = ssid.substring(16,18);
        String timer = ssid.substring(22,24);


        //String tim = timer.substring(0,1);
        //System.out.println(tim);
        //ti=obj.indexOf(tim);

//        timer2.setText(String.valueOf(ti));
//        timer3.setText(String.valueOf(ti));

        double n = 0;
        double btimestamp = 0;
        for (int i = 0; i < timest.length(); i++) {
            String a = timest.substring(i, i + 1);
            n = obj.indexOf(a);
            btimestamp += n * Math.pow(60, 3 - i);
        }
        beacontime= btimestamp;

        double p = 0;
        double bhead = 0;
        for (int i = 0; i < headin.length(); i++) {
            String a = headin.substring(i, i + 1);
            p = obj.indexOf(a);
            bhead += p * Math.pow(60, 1 - i);
        }
        beaconhead = bhead;

        ArrayList<String> obj = new ArrayList<String>(Arrays.asList(alnum));
        String lat_new_d = lat_new.substring(0, 2);
        String lat_new_b = lat_new.substring(2, 6);
        double m = 0;
        double lat_i = 0;
        for (int i = 0; i < lat_new_d.length(); i++) {
            String a = lat_new_d.substring(i, i + 1);
            m = obj.indexOf(a);
            lat_i += m * Math.pow(60, 1 - i);
        }
        double lat_f = 0;
        for (int i = 0; i < lat_new_b.length(); i++) {
            String a = lat_new_b.substring(i, i + 1);
            lat_f += Math.pow(60, 3 - i) * obj.indexOf(a);
        }
        lat_f = lat_f * 0.0000001;

        String lon_new_d = lon_new.substring(0, 2);
        String lon_new_b = lon_new.substring(2, 6);
        double lon_i = 0;
        for (int i = 0; i < lon_new_d.length(); i++) {
            String a = lon_new_d.substring(i, i + 1);
            lon_i += (Math.pow(60, 1 - i)) * obj.indexOf(a);
        }
        double lon_f = 0;
        for (int i = 0; i < lon_new_b.length(); i++) {
            String a = lon_new_b.substring(i, i + 1);
            lon_f += (Math.pow(60, 3 - i)) * obj.indexOf(a);
        }
        lon_f = lon_f * 0.0000001;

        double sign_lat = lat_i + lat_f;
        double sign_lon = lon_i + lon_f;

        String signLocTxt= "Lat: " + sign_lat + "\n" + "Lon: " + sign_lon;
        String encrypted = ssid.substring(18);
        char a[] = encrypted.toCharArray();
        System.out.println(a);
        String symbol_new="";
        for(int i=0; i<encrypted.length();i++){
            int k=obj.indexOf(Character.toString(a[i]))-key;
//            System.out.print(k+" ");
            if(k<0){
                k=60+k;
            }
            symbol_new+=obj.get(k);
        }
        symbol_new+=timers;

//        System.out.print(symbol_new);
        /**
        for (int i = 0; i < encrypted.length(); i++) {
            char symbol;
            if (Character.isLetter(a[i])) {
                int num = (int) a[i];

                if ((num >= 65) & (num <= 90)) {
                    num -= key;
                    if (num < 65)
                        num += 26;
                    if (num > 90)
                        num -= 26;
                    symbol = (char) num;
                } else if (a[i] == 'w') {
                    symbol = '-';
                } else {
                    num -= 49;
                    symbol = (char) num;
                }
            } else {
                symbol = a[i];
            }
            message += symbol;
        }
         **/
        return symbol_new;
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {

            results = wifiManager.getScanResults();
            try {
                unregisterReceiver(this);
                for (ScanResult scanResult : results) {
                    arrayList.add(scanResult.SSID + " ; " + scanResult.level + " ; " + scanResult.capabilities +  " ; " + scanResult.BSSID);
                    adapter.notifyDataSetChanged();
                }
                int scanfound=0;
                for (int i = 0; i < arrayList.size(); i++) {
                    String rsu[] = arrayList.get(i).split(";");
                    if (rsu.length == 4) {
                        String ssid = arrayList.get(i).split(";")[0];
                        if (ssid.contains("RF")) {
                            /**
                            All RSU signal message starts with "R2v" encrypted to "F5W"
                             **/

                            //Flag to remind that sign was discovered
                            scanfound=1;
                            String rssi_val = arrayList.get(i).split(";")[1];
                            String wifi_info = arrayList.get(i).split(";")[2];

                            //Decrypting Location, Heading, Timestamp and Ciphered message
                            String dec = decrypt(ssid.substring(1));
                            String dec_ssid = dec;
                            System.out.println("Message: "+dec);
                            if(unixTime-beacontime>600)
                                continue;

                            if(prevbeacontime>beacontime){
                                continue;
                            }
                            signTxt.setText(dec.substring(0,2));
//                            System.out.println(dec);

                            /**
                             * Sign Validation with
                             * TIMESTAMP
                             */


                            //If 4-Way Junction
                            if(dec.substring(0,2).equals("RF")){
                                signView1.setVisibility(View.VISIBLE);
                                signView2.setVisibility(View.VISIBLE);
                                signView3.setVisibility(View.VISIBLE);

                                signTxt.setText("4-Way Junction");

                                /**
                                 * Get angle difference between RSU and driver
                                 * Divide into 4 boundaries
                                 * 000 000 000 000 :: bitstream :: area1 area2 area3 area4
                                 * 000 means Left Forward Right
                                 * select which signals are relevant and show
                                 */
                                double Head = getHeading();
                                System.out.println(beaconhead);
                                double signselect = (beaconhead-Head)/90;
                                System.out.println("signselect :"+signselect);
                                if((signselect<0.5) & (signselect>-0.5)){
                                    signselect=0;
                                }else if((signselect<1.5) & (signselect>0.5)){
                                    signselect=1;
                                }
                                else if((signselect<2.5) & (signselect>1.5)){
                                    signselect=2;
                                }
                                else if((signselect<-2.5) & (signselect>-1.5)){
                                    signselect=2;
                                }
                                else if((signselect<-0.5) & (signselect>-1.5)){
                                    signselect=3;
                                }

                                String selectBeacon = dec.substring(4,6);

                                String a = selectBeacon.substring(0,  1);
                                String b = selectBeacon.substring(1,  2);
                                int l = obj.indexOf(a)+12;

                                int m = obj.indexOf(b)+12;
                                String signalStream= String.format("%6s",Integer.toBinaryString(l)).replace(" ","0")+String.format("%6s",Integer.toBinaryString(m)).replace(" ","0");
                                char[] n =(signalStream.substring((int)signselect*3,(int)signselect*3+3)).toCharArray();

                                timer1.setVisibility(View.VISIBLE);
                                timer2.setVisibility(View.VISIBLE);
                                timer3.setVisibility(View.VISIBLE);

                                if(n[0]=='1'){
                                    timer1.setText("Go");
                                    signView1.setImageResource(R.drawable.gap_01);
                                }else{
                                    timer1.setText("Stop");
                                    signView1.setImageResource(R.drawable.gap_02);
                                }

                                if(n[1]=='1'){
                                    timer2.setText("Go");
                                    signView2.setImageResource(R.drawable.gap_05);
                                }else{
                                    timer2.setText("Stop");
                                    signView2.setImageResource(R.drawable.gap_06);
                                }

                                if(n[2]=='1'){
                                    timer3.setText("Go");
                                    signView3.setImageResource(R.drawable.gap_03);
                                }else{
                                    timer3.setText("Stop");
                                    signView3.setImageResource(R.drawable.gap_04);
                                }

                                ConvertTextToSpeech("Four way junction");

                            }

                            //If T Junction
                            else if(dec.substring(0,2).equals("TJ")){
                                signlayout.setBackgroundResource(R.drawable.sign_back);
                                signTxt.setText("T Junction");
                                System.out.println("Inside T J");
                                /**
                                 * Get angle difference between RSU and driver
                                 * Divide into 4 boundaries
                                 * 000 000 000 :: bitstream :: area1 area2 area3
                                 * area1 is the left side in main road
                                 * 000 means Left Forward Right
                                 * select which signals are relevant and show
                                 * TESTING REQUIRED
                                 */

                                String timers=dec.substring(dec.length()-7);
                                //System.out.println("Timers T Junction: "+timers);
                                //double Head = 0;
                                double Head=0;

                                try {
                                    Head= getHeading();
                                }
                                catch (Exception e){
                                    //Error from reading heading - Set to default heading
                                    headingTextView.setText("getHeading Exception");
                                    //Head=0;
                                }

                                //headingTextView.setText(String.valueOf(Head));

                                System.out.println(beaconhead);
                                double signselect = abs((beaconhead-Head)/90);
                                System.out.println("signselect :"+signselect);
                                if((signselect<0.5) | (signselect>3.5)){
                                    signselect=0;
                                }else if((signselect<1.5) & (signselect>0.5)){
                                    signselect=2;
                                }
                                else if((signselect<3.5) & (signselect>2.5)){
                                    signselect=1;
                                }
                                else{
                                    continue;
                                }


                                String selectBeacon = dec.substring(2,3);

                                String a = selectBeacon.substring(0,  1);
                                //System.out.println("DEC:"+dec);
                                int l = obj.indexOf(a);
                                String signalStream= String.format("%6s",Integer.toBinaryString(l)).replace(" ","0");
                                char[] n =(signalStream.substring((int)signselect*2,(int)signselect*2+2)).toCharArray();
                                System.out.println("SignalStream : "+signalStream);
                                String tim = timers.substring((int)signselect*2,(int)signselect*2+2);
                                int tim1= obj.indexOf(String.valueOf(timers.charAt(0)));
                                int tim2= obj.indexOf(String.valueOf(timers.charAt(1)));
                                System.out.println("Tim Array: "+tim+"\nt1:"+tim1+", t2:"+tim2);


                                signView1.setVisibility(View.INVISIBLE);
                                signView2.setVisibility(View.INVISIBLE);
                                signView3.setVisibility(View.INVISIBLE);
                                timer1.setVisibility(View.INVISIBLE);
                                timer2.setVisibility(View.INVISIBLE);
                                timer3.setVisibility(View.INVISIBLE);

                                if(n[0]=='1'){
                                    if(signselect==0) {
                                        timer3.setVisibility(View.VISIBLE);
                                        signView3.setVisibility(View.VISIBLE);
                                        timer3.setText(String.valueOf(tim1));
                                        signView3.setImageResource(R.drawable.gap_03);
                                    }else if (signselect==1){
                                        timer3.setVisibility(View.VISIBLE);
                                        signView3.setVisibility(View.VISIBLE);
                                        timer3.setText(String.valueOf(tim1));
                                        signView3.setImageResource(R.drawable.gap_03);
                                    }else {
                                        timer1.setVisibility(View.VISIBLE);
                                        signView1.setVisibility(View.VISIBLE);
                                        timer1.setText(String.valueOf(tim1));
                                        signView1.setImageResource(R.drawable.gap_01);
                                    }


                                }else{
                                    if(signselect==0) {
                                        timer3.setVisibility(View.VISIBLE);
                                        signView3.setVisibility(View.VISIBLE);
                                        timer3.setText(String.valueOf(tim1));
                                        signView3.setImageResource(R.drawable.gap_04);
                                    }else if (signselect==1){
                                        timer3.setVisibility(View.VISIBLE);
                                        signView3.setVisibility(View.VISIBLE);
                                        timer3.setText(String.valueOf(tim1));
                                        signView3.setImageResource(R.drawable.gap_04);
                                    }else {
                                        timer1.setVisibility(View.VISIBLE);
                                        signView1.setVisibility(View.VISIBLE);
                                        timer1.setText(String.valueOf(tim1));
                                        signView1.setImageResource(R.drawable.gap_02);
                                    }
                                }

                                if(n[1]=='1'){
                                    if(signselect==0) {
                                        timer2.setVisibility(View.VISIBLE);
                                        signView2.setVisibility(View.VISIBLE);
                                        timer2.setText(String.valueOf(tim2));
                                        signView2.setImageResource(R.drawable.gap_05);
                                    }else if (signselect==1){
                                        timer1.setVisibility(View.VISIBLE);
                                        signView1.setVisibility(View.VISIBLE);
                                        timer1.setText(String.valueOf(tim2));
                                        signView1.setImageResource(R.drawable.gap_01);
                                    }else {
                                        timer2.setVisibility(View.VISIBLE);
                                        signView2.setVisibility(View.VISIBLE);
                                        timer2.setText(String.valueOf(tim2));
                                        signView2.setImageResource(R.drawable.gap_05);
                                    }
                                }else{
                                    if(signselect==0) {
                                        timer2.setVisibility(View.VISIBLE);
                                        signView2.setVisibility(View.VISIBLE);
                                        timer2.setText(String.valueOf(tim2));
                                        signView2.setImageResource(R.drawable.gap_06);
                                    }else if (signselect==1){
                                        timer1.setVisibility(View.VISIBLE);
                                        signView1.setVisibility(View.VISIBLE);
                                        timer1.setText(String.valueOf(tim2));
                                        signView1.setImageResource(R.drawable.gap_02);
                                    }else {
                                        timer2.setVisibility(View.VISIBLE);
                                        signView2.setVisibility(View.VISIBLE);
                                        timer2.setText(String.valueOf(tim2));
                                        signView2.setImageResource(R.drawable.gap_06);
                                    }

                                }
                                ConvertTextToSpeech("T Junction");
                            }
                            else if(dec.substring(0,2).equals("EM")) {

                                signView1.setVisibility(View.INVISIBLE);
                                signView2.setVisibility(View.INVISIBLE);
                                signView3.setVisibility(View.INVISIBLE);
                                timer1.setVisibility(View.INVISIBLE);
                                timer2.setVisibility(View.INVISIBLE);
                                timer3.setVisibility(View.INVISIBLE);

                                signTxt.setText("Emergency Sign");
                                System.out.println("DEC::"+dec);
                                String emergencymessage = dec.substring(2,3);
                                int l = obj.indexOf(emergencymessage)+11;
                                emergencymessage=obj.get(l);
                                System.out.println("l:"+l +" M:::"+emergencymessage);
                                if(emergencymessage.equals("1")){
                                    signTxt.setText("EM: Lane Merger");

                                    signView1.setVisibility(View.VISIBLE);
                                    signView1.setImageResource(R.drawable.em00);
                                }
                                else if(emergencymessage.equals("2")){
                                    signTxt.setText("EM: Road Flood");
                                    signView1.setVisibility(View.VISIBLE);
                                    signView1.setImageResource(R.drawable.em01);
                                }
                                else if(emergencymessage.equals("3")){
                                    signTxt.setText("EM: Road Fire");
                                    signView1.setVisibility(View.VISIBLE);
                                    signView1.setImageResource(R.drawable.em02);
                                }
                                else if(emergencymessage.equals("4")){
                                    signTxt.setText("EM: Road Block");
                                    signView1.setVisibility(View.VISIBLE);
                                    signView1.setImageResource(R.drawable.em03);
                                }
                                else if(emergencymessage.equals("5")){
                                    signTxt.setText("EM: Oil Leak");
                                    signView1.setVisibility(View.VISIBLE);
                                    signView1.setImageResource(R.drawable.em04);
                                }

                            }
                            else{
                                String dec_s = dec.toLowerCase();
                                dec_s = dec_s.replace('-', '_');
                                dec_s = dec_s.replace(" ", "");
                                signTxt.setText(signList.get(dec_s));
                                signView1.setVisibility(View.VISIBLE);
                                signView2.setVisibility(View.GONE);
                                signView3.setVisibility(View.GONE);
                                timer1.setVisibility(View.GONE);
                                timer2.setVisibility(View.GONE);
                                timer3.setVisibility(View.GONE);
                                if(drawableList.get(dec_s)!=null) {
                                    signView1.setImageResource(drawableList.get(dec_s));
                                }else{
                                    signView1.setVisibility(View.INVISIBLE);
                                }
                                ConvertTextToSpeech(signList.get(dec_s));
                            }
                            /**
                             *
                             * Distance based on GPS and RSSI value (Need to generate a function)
                             * Current Function> distance = (RSSI in dBm)**2 * 0.01
                             */
                            double distance = Math.pow(Double.valueOf(rssi_val),2)*0.01;
                            distanceTxt.setText(String.valueOf((int)distance)+"m Ahead");

                            prevbeacontime=beacontime;
                        }
                    }
                }

                /**
                 * Checks if a sign was found, if not reset all the labels
                 */
                if (scanfound==1) {
                    //scanDetails.setText("Found a nearby Road Signal");
                    //Toast.makeText(DriverMapActivity.this, "Found a nearby Road Signal", Toast.LENGTH_SHORT).show();
                }else {
                    //scanDetails.setText("Scanning for Road Signals");
                    //Toast.makeText(DriverMapActivity.this, "Found a nearby Road Signal", Toast.LENGTH_SHORT).show();
                    distanceTxt.setText("No sign found");
                    signView2.setVisibility(View.INVISIBLE);
                    signView3.setVisibility(View.INVISIBLE);
                    signView1.setVisibility(View.INVISIBLE);
                    timer1.setVisibility(View.INVISIBLE);
                    timer2.setVisibility(View.INVISIBLE);
                    timer3.setVisibility(View.INVISIBLE);
                    signTxt.setText("Road Sign Details");
                    signlayout.setBackgroundResource(0);
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

//    /*On pause text speech object stoped and shutdown the speech
//     * */
//    @Override
//    protected void onPause() {
//        // TODO Auto-generated method stub
//
//        if (tts != null) {
//
//            tts.stop();
//            tts.shutdown();
//        }
//        super.onPause();
//   }

    /*
    getAreaAppendToTemp
    Collect area code from "getMyArea()"
    Collect heading from "getHeading()"
    if driver is in new area, first delete whole table entries and enter new area data to temporary table
    then move to getColsestRoadSign()
    else driver is in the same area we directly move to the getClosestRoadSign();
    Find the corresponding table match to the area code
    Fetch data from corresponding Table and append to the Temporary database
    */
    private void getAreaAppendToTemp(double lati, double longi) {

        int areacode = getMyArea(lati, longi);
        Double heading = getHeading();
        mHeading.setText(String.valueOf(heading)); //Display heading angle value

        preLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()); //Update current value to get next direction

        if (areacode != areaprev) {
            table1Db.updateprev("1",String.valueOf(areacode));     //add new area code to the data base
            areaprev = areacode;
            tempTable.deleteAll();                  //delete all entries in temporary database
            signTxt.setText("line 693 passed");
            //Qarea.quarryarea(String.valueOf(areacode));
            /***Updated :: Commented the above code and created the code below**/
            // Qarea.quarryarea(String.valueOf(21));

        }
        Qarea.quarryarea(String.valueOf(areacode));

        getClosestRoadSign(heading, lati, longi);
    }

    private Cursor quaryDB(Integer area) {
        Cursor re = table1Db.fetch(String.valueOf(area));
        return re;
    }

    /*
    getMyArea
    First we collect our Longitude and Latitude
    Then we compare our coordinates with area coordinates which given in Area Database
    Area code added to the global variable "slot"
    Return Integer "slot" value
    */
    private int getMyArea(double lati, double longi) {

        if (lati < 10.065380 & lati > 5.904049) {
            if (longi > 79.563740 & longi < 81.939274) {
                slot = 21;
            } else slot = 2;
        } else slot = 2;
        return slot;
    }

    /*
   getHeading
   Within this method we use accelerometerReading and magnetometerReading to find our heading
   Return double "heading"
   */
    private Double parseHeading() {
        LatLng CurrentLocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        Double head = SphericalUtil.computeHeading(preLocation,CurrentLocation);
        if (head < 0) {
            return head + 360;
        } else return head;
    }

    public Double getHeading () {
        Double hd = parseHeading();

        if (abs(htm2 - htm1)<20 || abs(htm2 - htm1)>340) {
            if (abs(htm1 -hd)<20 || abs(hd - htm1)>340) {
                h = hd;
            } else {
                htm2 = htm1;
                htm1 = hd;

            }
        } else {
            htm2 = htm1;
            htm1 = hd;
        }
        return h;
    }


    /*
        getClosestRoadSign
        In this section we calculate the distance to each road sign in temporary table and store in the table.
        Then compare each and get closest road sign

        if the distance to the road sign is greater than 10m , send it to dispaly
    */
    private void getClosestRoadSign(Double heading, double lati, double longi) {

        long counter = tempTable.getRoadSignCount();
        float lowestdistance = 80;
        int n = 1;
        Integer TABLE_ID = 0;
        String ROAD_SIGN = "";
        String Temp_sign = "";
        String LONGI = "";
        String LATI = "";
        double DISTANCE = 0.0;
        double FLAG = 0.0;
        while (n < (counter + 1)) {

            Cursor res = tempTable.fetch(Integer.toString(n));
            // Show all data

            if (res.getCount() == 0) {
                // we have to load data from Online server database ..................................999999999999999999999999999999999999999999
                //showMessage("Error", "Nothing found");
                return;
            }


            while (res.moveToNext()) {
                TABLE_ID = Integer.parseInt(res.getString(0));
                ROAD_SIGN = res.getString(1);
                LONGI = res.getString(2);
                LATI = res.getString(3);
                DISTANCE = parseDouble(res.getString(4));
                FLAG = res.getDouble(5);        //heading
                System.out.println("getsign "+ ROAD_SIGN);

            }


            Location targetLocation = new Location("");         //provider name is unnecessary
            try {
                targetLocation.setLatitude(parseDouble(LATI));       //your co-ords of course
                targetLocation.setLongitude(parseDouble(LONGI));
            } catch (NumberFormatException e) {
                showMessage("Data", LATI);
            }

            float distanceInMeters = mLastLocation.distanceTo(targetLocation);   //Calculating distances to the road signs
            System.out.println("distance "+ distanceInMeters);

            //mTime.setText(String.valueOf(tempID));
            System.out.println("FLAG: "+FLAG);
            if (distanceInMeters <= DISTANCE) {                                     //Compare new distance to the RS with previous distance
                if (abs(heading - FLAG) < 20 || abs(heading-FLAG)>340) {     //abs(htm2 - htm1)<20 || abs(htm2 - htm1)>340
                    System.out.println("heading limits passed oooooooooooooo " + ROAD_SIGN);
                        if (distanceInMeters < lowestdistance) {
                            if (distanceInMeters > 10) {
                                tempID = TABLE_ID;
                                Temp_sign = ROAD_SIGN;
                                lowestdistance = distanceInMeters;
                                System.out.println("yo inside the end process and please show me "+ROAD_SIGN);

                            }
                        }

                }
            }

            tempTable.updateData(TABLE_ID.toString(), ROAD_SIGN, LONGI, LATI, distanceInMeters, FLAG);
            n = n + 1;
        }
        //RoadSignLoad thread = new RoadSignLoad(Temp_sign);
        //thread.start();
        distanceTxt.setText(String.valueOf((int)lowestdistance)+"m Ahead");
        //mSignImage1.setImageResource(images[Temp_sign]);
        if (temp != Temp_sign) {
            if (audio == 1) {
                if(drawableList.get(Temp_sign)!=null) {
                    mSignImage1.setImageResource(drawableList.get(Temp_sign));
                }else{
                    mSignImage1.setVisibility(View.INVISIBLE);
                }
                ConvertTextToSpeech(signList.get(Temp_sign));
                signTxt.setText(signList.get(Temp_sign));
                temp = Temp_sign;
            }
        }

    }



    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override //this is the function going to call every time our location changed
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17)); //zoom level

        getAreaAppendToTemp(location.getLatitude(), location.getLongitude());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest(); // Requesting Location
        mLocationRequest.setInterval(700); // Location updating interval
        mLocationRequest.setFastestInterval(700);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //Highest accuracy for loading location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    private void ConvertTextToSpeech(String RoadSign) {
        // TODO Auto-generated method stub
        String text = RoadSign;
        if(audio == 1) {
            if (text == null || "".equals(text)) {
                text = "";
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            } else tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    

    @Override
    protected void onStop() {
        super.onStop();

    }

}
