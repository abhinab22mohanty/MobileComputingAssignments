package com.example.abhinab.assignment1;

import android.os.Bundle;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    //EditText variables to catch Name and Id of pateint for future use
    EditText lblName,lblId;

    //Flags to check if run is pressed or stop is pressed
    boolean runPressed = false;
    boolean stopPressed = false;

    //Thread Safe synchronized ArrayList to store the random values generated on the graph
    static List<DataPoint> pointArray = Collections.synchronizedList(new ArrayList<DataPoint> ());
    private static final Random RANDOM = new Random();

    //Series of points to be plotted on the Graph
    private LineGraphSeries<DataPoint> series;
    //Timer initialized
    private int lastX = 0;
    DataPoint newPoint;

    //Declaring the Thread
    Thread graphThread;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //We get the text inserted for run and stop activation
        lblName = findViewById(R.id.patientName);
        lblId = findViewById(R.id.patientId);
        final Button btnRun = (Button) findViewById(R.id.btnRun);
        final Button btnStop = (Button) findViewById(R.id.btnStop);
        // we get graph view instance
        final GraphView graph = (GraphView) findViewById(R.id.graph);

        // Line Graph Series declaration for data containment and plotting on graph
        series = new LineGraphSeries<DataPoint>();

        //Axis Labels
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time");
        gridLabel.setVerticalAxisTitle("Values");


        // customize viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(10);
        viewport.setMinX(0);
        viewport.setMaxX(10);

        viewport.scrollToEnd();
        viewport.setScalable(true);


        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    graph.addSeries(series);
                    runPressed = !runPressed;
                    stopPressed = false;


                    if (runPressed)
                        Toast.makeText(getApplicationContext(), "Running", Toast.LENGTH_SHORT).show();

                    if (runPressed) {
                        graphThread = new Thread(new Runnable() {


                            @Override
                            public void run() {


                                // we add new entries
                                for (int i = 0; i >= 0; i++) {
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            if (!stopPressed)
                                                updateData();

                                            else
                                                return;
                                        }
                                    });

                                    // sleep to slow down the add of entries
                                    try {
                                        Thread.sleep(500);

                                    } catch (InterruptedException e) {
                                        // manage error ...
                                    }
                                }


                            }
                        });
                        graphThread.start();
                    }

                    if (!runPressed) {
                        runPressed = true;

                    }
                }
        });


        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPressed = true;
                graph.removeAllSeries();
                Toast.makeText(getApplicationContext(),"Stopping",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateData(){

        //Random point generated and set into a Datapoint with respective time
        newPoint = new DataPoint(lastX++, RANDOM.nextDouble() * 10d);

        //Adding the datapoint to the synchronized ArrayList
        pointArray.add(newPoint);
        System.out.println(pointArray.get(pointArray.size()-1));

        //The last added datapoint in the synchronized ArrayList is appended to the graph series
        series.appendData(pointArray.get(pointArray.size()-1),true,12);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        pointArray.clear();


            try {
                graphThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }



