package com.example.pointofinterest2;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE0 = "com.example.myfirstapp.MESSAGE0";
    public final static String EXTRA_MESSAGE1 = "com.example.myfirstapp.MESSAGE1";

    //ArrayList<Place> places;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void startMap(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        EditText editText0 = (EditText) findViewById(R.id.from_label);
        EditText editText1 = (EditText) findViewById(R.id.to_label);
        String message0 = editText0.getText().toString();
        String message1 = editText1.getText().toString();
        intent.putExtra(EXTRA_MESSAGE0, message0);
        intent.putExtra(EXTRA_MESSAGE1, message1);
        //intent.putExtra("places", places);
        startActivity(intent);


    }
}