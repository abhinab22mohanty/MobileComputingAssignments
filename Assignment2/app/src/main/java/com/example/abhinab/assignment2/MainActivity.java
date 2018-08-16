package com.example.abhinab.assignment2;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

//import android.app.Activity;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.GridLabelRenderer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

//    static int FILTER_DATA_MIN_TIME = 1000; // ms
//    long last = System.currentTimeMillis();
    DatabaseHelper sensorDb;
    DownloadManager downloadManager;
    private Sensor accSensor;
    private SensorManager AM;
    private int runCount = 0;
    private long startTime = 0;
    private long currentTime = 0;
    boolean runPressed = false;
    boolean stopPressed = false;
    static List<DataPoint> pointArray1 = Collections.synchronizedList(new ArrayList<DataPoint> ());
    static List<DataPoint> pointArray2 = Collections.synchronizedList(new ArrayList<DataPoint> ());
    static List<DataPoint> pointArray3 = Collections.synchronizedList(new ArrayList<DataPoint> ());
    //    static List<DataPoint> pointArray = new CopyOnWriteArrayList<DataPoint>();
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series1;
    private LineGraphSeries<DataPoint> series2;
    private LineGraphSeries<DataPoint> series3;
    private LineGraphSeries<DataPoint> new_series1;
    private LineGraphSeries<DataPoint> new_series2;
    private LineGraphSeries<DataPoint> new_series3;
    private float sensorX = 0;
    private float sensorY = 0;
    private float sensorZ = 0;
    private  int Timer = 0;
//    private  int Ycount = 0;
//    private  int Zcount = 0;
    DataPoint newPoint1;
    DataPoint newPoint2;
    DataPoint newPoint3;
    Thread graphThread;
    String Name,Sex;
    int Id,Age;

    GraphView graph;

    private Button btnRun;
    private Button btnStop;
    private Button btnBack;
    private Button btnStore;
    private Button btnUpload;
    private Button btnDownload;
    String Table_name;
    String serverUrl = "http://impact.asu.edu/CSE535Spring18Folder/UploadToServer.php";
    String downloadUrl = "http://impact.asu.edu/CSE535Spring18Folder/Group9.db";
    String dbFilePath = "/sdcard/Android/data/CSE535_ASSIGNMENT2";


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

//        if ((System.currentTimeMillis() - last) > FILTER_DATA_MIN_TIME) {
//            last = System.currentTimeMillis();
            sensorX = sensorEvent.values[0];
            sensorY = sensorEvent.values[1];
            sensorZ = sensorEvent.values[2];
//        }





//        System.out.println("pointArray1"+pointArray1.get(pointArray1.size()-1));
//        System.out.println("Size pointArray1:"+pointArray1.size());
//        System.out.println("pointArray2"+pointArray2.get(pointArray2.size()-1));
//        System.out.println("Size pointArray2:"+pointArray2.size());
//        System.out.println("pointArray3"+pointArray3.get(pointArray3.size()-1));
//        System.out.println("Size pointArray3:"+pointArray2.size());
////        pointArray1.add(newPoint1);
////        pointArray2.add(newPoint2);
////        pointArray3.add(newPoint3);
//        series1.appendData(newPoint1,true,20);
//        series2.appendData(newPoint2,true,20);
//        series3.appendData(newPoint3,true,20);



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set buttons
        btnRun = (Button) findViewById(R.id.btnRun);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnStore = (Button) findViewById(R.id.btnStore);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnDownload = (Button) findViewById(R.id.btnDownload);


        //Initialize Database Helper
        sensorDb = new DatabaseHelper(this);


        //Intent catch
        Intent calledActivity = getIntent();
        Id = Integer.parseInt(calledActivity.getExtras().get("Id").toString());
        Name = calledActivity.getExtras().getString("Name");
        Age = Integer.parseInt(calledActivity.getExtras().get("Age").toString());
        Sex = calledActivity.getExtras().getString("Sex");

        System.out.println(Id);
        System.out.println(Name);
        System.out.println(Age);
        System.out.println(Sex);

        //We get the text inserted for run and stop activation
        //


        // we get graph view instance
        graph = (GraphView) findViewById(R.id.graph);


        //Sensor Manager settings
        AM = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Accelerometer Sensor
        accSensor = AM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        //Register Sensor listener
        AM.registerListener(this,accSensor,AM.SENSOR_DELAY_NORMAL);

        // data
        series1 = new LineGraphSeries<DataPoint>();
        series1.setColor(Color.RED);
        series1.setDrawDataPoints(true);
        series2 = new LineGraphSeries<DataPoint>();
        series2.setColor(Color.BLUE);
        series2.setDrawDataPoints(true);
        series3 = new LineGraphSeries<DataPoint>();
        series3.setColor(Color.GREEN);
        series3.setDrawDataPoints(true);

        // customize viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinY(-20);
        viewport.setMaxY(20);
        viewport.setMinX(0);
        viewport.setMaxX(10);

        viewport.scrollToEnd();
        viewport.setScalable(true);

        // legend
        series1.setTitle("X");
        series2.setTitle("Y");
        series3.setTitle("Z");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setFixedPosition(30,450);

        //Axis Labels
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time");
        gridLabel.setVerticalAxisTitle("Values");




//        lblId.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Toast.makeText(getApplicationContext(),"Required Field",Toast.LENGTH_SHORT).show();
//
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//
//                btnStop.setEnabled((!lblId.getText().toString().trim().isEmpty()) && (!lblName.getText().toString().trim().isEmpty()));
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//
//            }
//        });
//
//
//        lblName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Toast.makeText(getApplicationContext(),"Required Field",Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                btnRun.setEnabled((!lblId.getText().toString().trim().isEmpty()) && (!lblName.getText().toString().trim().isEmpty()));
//                btnStop.setEnabled((!lblId.getText().toString().trim().isEmpty()) && (!lblName.getText().toString().trim().isEmpty()));
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//
//            }
//        });




        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runPressed = !runPressed;
//                if (runCount == 1 || stopPressed)
//
//                {

                if(runPressed || stopPressed){
                    System.out.println("<<<<<<<< INSIDE RUNNNNNNNNNN >>>>>>>");
                    Toast.makeText(getApplicationContext(), "Running", Toast.LENGTH_SHORT).show();

                    btnStore.setEnabled(runPressed && stopPressed);
                    //runCount++;
                    btnBack.setEnabled(false);
                    graph.addSeries(series1);
                    graph.addSeries(series2);
                    graph.addSeries(series3);

                    stopPressed = false;
                }




//                else if(!runPressed)
//                    Toast.makeText(getApplicationContext(),"Pausing",Toast.LENGTH_SHORT).show();
                if (runPressed) {

                    System.out.println("<<<<<<<< INSIDE THREAD CREATE >>>>>>>");


                    graphThread = new Thread(new Runnable() {

                        @Override
                        public void run() {


                            // we add new entries
                            for (int i = 0; i >= 0; i++) {
                                if (runPressed) {


                                    //pointArray.add(randPoint);
                                    runOnUiThread(new Runnable() {


                                        @Override
                                        public void run() {
                                            if (!stopPressed) {


//                                            currentTime = System.currentTimeMillis() - startTime;
//                                            int seconds = (int) (currentTime / 1000);
//                                            //int minutes = seconds / 60;
//                                            seconds = seconds % 60;
                                                updateData(Timer++);

                                            } else
                                                return;
                                        }
                                    });

                                    // sleep to slow down the add of entries
                                    try {
                                        Thread.sleep(1000);

                                    } catch (InterruptedException e) {
                                        // manage error ...
                                    }

                                }

                                else
                                    continue;
                            }


                        }
                    });

                    graphThread.start();

                }

                if (!runPressed) {
                    System.out.println("CAN'T CATCH ME");
                    runPressed = true;
                    //return;

                }


//                if(!runPressed)
//                {
//                    try {
//                        graphThread.join();
//
//                    } catch (InterruptedException e) {
//                        System.out.println("Error in join");
//                    }
//
//                }
            }
//            }
        });



        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPressed = true;

                System.out.println("X-Array: "+pointArray1);

                System.out.println("Y-Array: "+pointArray2);

                System.out.println("Z-Array "+pointArray3);





                btnStore.setEnabled(runPressed && stopPressed);
                graph.removeAllSeries();
                Toast.makeText(getApplicationContext(),"Stopping",Toast.LENGTH_SHORT).show();

            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    graphThread.join();
//                } catch (InterruptedException e) {
//                    System.out.println("Exception in Join");
//                }

//                onDestroy();

                getIntentBack();


            }
        });

        btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                System.out.println((pointArray1.get(pointArray1.size()-1).getY()));
                System.out.println((pointArray2.get(pointArray2.size()-1).getY()));
                System.out.println((pointArray3.get(pointArray3.size()-1).getY()));
                String table_name = Name +"_"+Id+"_"+Age+"_"+Sex;
                Table_name = table_name;

                new StoreAsyncTask (MainActivity.this).execute(table_name);




            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadAsyncTask (MainActivity.this).execute();
            }
        });


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadAsyncTask (MainActivity.this).execute(downloadUrl);
            }
        });

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
//
//                return;
//            }
//        }
//        enable_button();










    }



//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == 100 &&(grantResults[0] == PackageManager.PERMISSION_GRANTED)){
//            enable_button();
//        }
//        else{
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
//                }
//            }
//        }
//    }

//    private void enable_button() {
//
//    }

    private void updateData(int time){

        //newPoint = new DataPoint(lastX++, RANDOM.nextDouble() * 10d);
        newPoint1 = new DataPoint(time , sensorX);

        newPoint2 = new DataPoint(time, sensorY);

        newPoint3 = new DataPoint(time, sensorZ);
        pointArray1.add(newPoint1);
        pointArray2.add(newPoint2);
        pointArray3.add(newPoint3);

        System.out.println("X: "+pointArray1.get(pointArray1.size()-1));
//        System.out.println("Size pointArray1: "+pointArray1.size());
        System.out.println("Y: "+pointArray2.get(pointArray2.size()-1));
//        System.out.println("Size pointArray2: "+pointArray2.size());
        System.out.println("Z: "+pointArray3.get(pointArray3.size()-1));
//        System.out.println("Size pointArray3: "+pointArray2.size());
//        pointArray1.add(newPoint1);
//        pointArray2.add(newPoint2);
//        pointArray3.add(newPoint3);
        series1.appendData(newPoint1,true,20);
        series2.appendData(newPoint2,true,20);
        series3.appendData(newPoint3,true,20);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        pointArray1.clear();
        pointArray2.clear();
        pointArray3.clear();


        try {
            graphThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void getIntentBack() {

//        if(graphThread.isAlive()) {
//            try {
//                graphThread.join();
//            } catch (InterruptedException e) {
//                System.out.println("Exception in Thread Join");
//            }
//        }
        Intent getInfoIntent = new Intent(this,EnterScreen.class);
        startActivity(getInfoIntent);
//        onDestroy();
    }


     public class StoreAsyncTask extends AsyncTask<String,Void,Void> {
        private ProgressDialog dialog;
         boolean createAndInsert;

        public StoreAsyncTask(MainActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Storing in your SDCard. Please Wait");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... args) {
            // do background work here
            String table_name = args[0];
            createAndInsert = sensorDb.createAndInsertTable(table_name,pointArray1,pointArray2,pointArray3);





            //sensorDb.showData(table_name);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // do UI work here
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            btnUpload.setEnabled(true);

            btnBack.setEnabled(true);

            if(createAndInsert){
                Toast.makeText(getApplicationContext(),"Data Inserted Successfully",Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(getApplicationContext(),"Sorry!! Something Went Wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }

        public class UploadAsyncTask extends AsyncTask<Void,Void,Void> {
        private ProgressDialog dialog;
        boolean errorLog = false;


        public UploadAsyncTask(MainActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Uploading");
            dialog.setMessage("Please Wait ...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... args) {
            // do background work here

            File db = new File(dbFilePath+"/Group9");
            System.out.println(db.getPath());
            String content_type = "application/x-sqlite3";



            OkHttpClient client = new OkHttpClient();
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type),db);

            RequestBody requestBody = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("type",content_type)
                                            .addFormDataPart("uploaded_file","Group9.db",file_body)
                                            .build();


            Request request = new Request.Builder()
                                    .url(serverUrl)
                                    .post(requestBody)
                                    .build();

            try {
                Response response = client.newCall(request).execute();

                if(!response.isSuccessful()){
                    errorLog = true;
                    throw new IOException("Error : "+response );

                }
                else
                    errorLog = false;

            } catch (IOException e) {
                e.printStackTrace();
            }

//            System.out.println(dbFile.getPath());
//            System.out.println(content_type);

            return null;
        }

//            private String getMimeType(String path) {
//                String extension = MimeTypeMap.getFileExtensionFromUrl(path);
//                System.out.println(extension);
//
//                return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//            }

            @Override
        protected void onPostExecute(Void result) {
            // do UI work here
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

                if(!errorLog){
                    Toast.makeText(getApplicationContext(),"Data Uploaded Successfully",Toast.LENGTH_SHORT).show();
                    btnDownload.setEnabled(true);

                }else{
                    Toast.makeText(getApplicationContext(),"Sorry!! Something Went Wrong",Toast.LENGTH_SHORT).show();
                }


        }
    }


    public class DownloadAsyncTask extends AsyncTask<String, String, String> {
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);


        boolean file_found;

        public DownloadAsyncTask(MainActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Downloading file. Please wait...");
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // do background work here
            int count;
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection con = null;
            URL url = null;



            try {
                HttpURLConnection.setFollowRedirects(false);
                url = new URL(args[0]);
                con = (HttpURLConnection) url.openConnection();
                con.connect();
                if(con.getResponseCode() == HttpURLConnection.HTTP_OK){

                    file_found = true;


//                    downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
//                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
//                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Downloaded_Group-9.db");
//
//                    Long reference = downloadManager.enqueue(request);


                }
            }
            catch (Exception e) {
                file_found = false;
                e.printStackTrace();

            }



            if(file_found){
                System.out.println("File Found");
                //Download data
                int fileLength = con.getContentLength();

                try {
                    //Download File
                    input = new BufferedInputStream(url.openStream(),
                            8192);

                    output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/Downloaded_Group-9.db");

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / fileLength));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();



                } catch (IOException e) {
                    e.printStackTrace();
                }










            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            dialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String args) {
            // do UI work here
            ArrayList<ArrayList<DataPoint>> returnResult = new ArrayList<ArrayList<DataPoint>>();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }






            if(file_found){
                Toast.makeText(getApplicationContext(),"File Downloaded",Toast.LENGTH_SHORT).show();
                SQLiteDatabase downloaded_db = sensorDb.openDatabase();
                returnResult = sensorDb.showData(downloaded_db,Table_name);
                //Re-initialize the new points extracted from the downloaded Database File

                pointArray1 = returnResult.get(0);
                pointArray2 = returnResult.get(1);
                pointArray3 = returnResult.get(2);

                System.out.println(pointArray1);
                System.out.println(pointArray2);
                System.out.println(pointArray3);

                // data
                new_series1 = new LineGraphSeries<DataPoint>();
                new_series1.setColor(Color.RED);
                new_series1.setDrawDataPoints(true);
                new_series2 = new LineGraphSeries<DataPoint>();
                new_series2.setColor(Color.BLUE);
                new_series2.setDrawDataPoints(true);
                new_series3 = new LineGraphSeries<DataPoint>();
                new_series3.setColor(Color.GREEN);
                new_series3.setDrawDataPoints(true);


                // legend
                new_series1.setTitle("X");
                new_series2.setTitle("Y");
                new_series3.setTitle("Z");
                graph.getLegendRenderer().setVisible(true);
                graph.getLegendRenderer().setFixedPosition(30,450);

                for(int i=pointArray1.size()-11;i<pointArray1.size()-1;i++){
                    new_series1.appendData(pointArray1.get(i),true,10);
                    new_series2.appendData(pointArray2.get(i),true,10);
                    new_series3.appendData(pointArray3.get(i),true,10);
                }

                graph.addSeries(new_series1);
                graph.addSeries(new_series2);
                graph.addSeries(new_series3);
                Viewport viewport = graph.getViewport();
                viewport.scrollToEnd();
                viewport.setScalable(true);


//                SQLiteDatabase downloaded_db = sensorDb.openDatabase();
//                sensorDb.showData(downloaded_db,Table_name);

            }else{
                Toast.makeText(getApplicationContext(),"File Not Found",Toast.LENGTH_SHORT).show();
            }
        }


    }
}






