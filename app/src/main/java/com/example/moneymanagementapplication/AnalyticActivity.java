package com.example.moneymanagementapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AnalyticActivity extends AppCompatActivity {

    private CardView todayAnalytic, weekAnalytic, monthAnalytic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytic);

        todayAnalytic = findViewById(R.id.todayAnalytic);
        weekAnalytic = findViewById(R.id.weekAnalytic);
        monthAnalytic = findViewById(R.id.monthAnalytic);

        todayAnalytic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalyticActivity.this,DailyAnalyticActivity.class);
                startActivity(intent);
            }
        });

        weekAnalytic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalyticActivity.this,WeeklyAnalyticActivity.class);
                startActivity(intent);
            }
        });

        monthAnalytic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalyticActivity.this,MonthlyAnalyticActivity.class);
                startActivity(intent);
            }
        });
    }
}