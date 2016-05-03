package com.example.dakota.poopcounter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.TextView;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MyActivity extends AppCompatActivity{
    private int poops;
    public static FragmentManager fragmentManager;
    MyMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        retrievePoops();


        TextView textOut = (TextView) findViewById(R.id.textView);
        textOut.setText(String.valueOf(getPoops()));

        if(findViewById(R.id.contentContainer) != null){
            if(savedInstanceState != null){
                return;
            }
            //creating map fragment to be placed in activity layout
            mapFragment = new MyMapFragment();

            //If the activity is started with special instructions from an Intent,
            //pass the intent's extra's to the fragment as arguments
            mapFragment.setArguments(getIntent().getExtras());

            //add the fragment to the content_my framelayout
            getSupportFragmentManager().beginTransaction().add(R.id.contentContainer,mapFragment).commit();


        }else{
            textOut.setText("Map was null");
        }


    }
    @Override
    public void onPause(){
        super.onPause(); //Always call superclass method
        //Saving poop value to MyPref sharedpreference file
        savePoops();
        mapFragment.savePastLocations();
        updateWidget();


    }
    @Override
    public void onResume(){
        super.onResume();
        //shared preference to retrieve stored poop value
        retrievePoops();
        TextView textOut = (TextView) findViewById(R.id.textView);
        textOut.setText(String.valueOf(getPoops()));
    }
    @Override
    protected void onStop(){
        super.onStop();

        //Saving poop value to MyPref sharedpreference file
        savePoops();
        mapFragment.savePastLocations();
        updateWidget();


    }
    protected void onRestart(){
        super.onRestart();

        retrievePoops();
        TextView textOut = (TextView) findViewById(R.id.textView);
        textOut.setText(String.valueOf(getPoops()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void incrementPoops(View view){

        poops++;
        TextView textOut = (TextView) findViewById(R.id.textView);
        textOut.setText(String.valueOf(getPoops()));
        mapFragment.poopTaken();
    }
    private int getPoops(){
        return poops;
    }
    public void decrementPoops(View view){
        if(poops != 0) {
            poops--;
        }
        TextView textOut = (TextView) findViewById(R.id.textView);
        textOut.setText(String.valueOf(getPoops()));
    }
    private void savePoops(){
        SharedPreferences sp = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("poops",poops);
        editor.apply();
    }
    private void retrievePoops(){
        //shared preference to retrieve stored poop value
        SharedPreferences sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        //use 0 if no stored value is found
        poops = sp.getInt("poops",0);
    }
    public void resetPoops(MenuItem item) {
        poops =0;
        savePoops();
        TextView textOut = (TextView) findViewById(R.id.textView);
        textOut.setText(String.valueOf(getPoops()));
    }
    public void updateWidget(){
        Context context = this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        ComponentName thisWidget = new ComponentName(context, PoopWidgetProvider.class);
        remoteViews.setTextViewText(R.id.widgetTextView, String.valueOf(getPoops()));
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }
}
