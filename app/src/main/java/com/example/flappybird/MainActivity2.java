package com.example.flappybird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {
    SQLiteDatabase db;
    int record = 0;
    TextView recordView,scoreView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        hideSystemUI();
        int points = (int)getIntent().getSerializableExtra("Points");
        db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        Cursor query = db.rawQuery("SELECT record FROM records;", null);
        if(query.moveToFirst()){
            record = query.getInt(0);
        }
        if (record<points){
            ContentValues updateValues = new ContentValues();;
            updateValues.put("record", points);
            db.update("records",updateValues, "_id = 1",null);
        }
        query = db.rawQuery("SELECT record FROM records LIMIT 1;", null);
        if(query.moveToFirst()){
            record = query.getInt(0);
        }
        query.close();
        db.close();
        scoreView = findViewById(R.id.scoreNum);
        recordView = findViewById(R.id.recordNum);
        scoreView.setText(String.valueOf(points));
        recordView.setText(String.valueOf(record));
    }
    public void Start(View view){
        Intent intent = new Intent(MainActivity2.this,Game.class);
        startActivity(intent);
        finish();
    }
    private void hideSystemUI() {

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}