package com.example.dakota.poopcounter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;

import static android.app.PendingIntent.getActivity;


public class MyActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.mycompany.poopcounter.MESSAGE";
    private int poops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        retrievePoops();

        TextView textOut = (TextView) findViewById(R.id.textView);
        textOut.setText(String.valueOf(getPoops()));

    }
    @Override
    public void onPause(){
        super.onPause(); //Always call superclass method
        //Saving poop value to MyPref sharedpreference file
        savePoops();
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
        //Intent intent = new Intent(this,DisplayMessageActivity.class);
        poops++;
        TextView textOut = (TextView) findViewById(R.id.textView);
        textOut.setText(String.valueOf(getPoops()));
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, poops);
        //startActivity(intent);
    }
    public int getPoops(){
        return poops;
    }
    public void decrementPoops(View view){
        poops--;
        TextView textOut = (TextView) findViewById(R.id.textView);
        textOut.setText(String.valueOf(getPoops()));
    }
    public void savePoops(){
        SharedPreferences sp = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("poops",poops);
        editor.commit();
    }
    public void retrievePoops(){
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
        remoteViews.setTextViewText(R.id.textView, String.valueOf(getPoops()));
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }
}
