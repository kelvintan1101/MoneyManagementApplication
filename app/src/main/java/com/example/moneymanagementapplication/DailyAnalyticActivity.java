package com.example.moneymanagementapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DailyAnalyticActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, personalRef;

    private CardView transport, food, house, entertainment, education, charity, apparel, health, personal, other;

    private TextView totalSpending, analyticTransportAmount, analyticFoodAmount ,analyticHouseAmount ,analyticEntertainmentAmount ,analyticEducationAmount, analyticCharityAmount, analyticApparelAmount, analyticHealthAmount, analyticPersonalAmount, analyticOtherAmount;
    private AnyChartView anyChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_analytic);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);
        totalSpending = findViewById(R.id.totalSpending);
        analyticTransportAmount = findViewById(R.id.analyticTransportAmount);
        analyticFoodAmount = findViewById(R.id.analyticFoodAmount);
        analyticHouseAmount = findViewById(R.id.analyticHouseAmount);
        analyticEntertainmentAmount = findViewById(R.id.analyticEntertainmentAmount);
        analyticEducationAmount = findViewById(R.id.analyticEducationAmount);
        analyticCharityAmount = findViewById(R.id.analyticCharityAmount);
        analyticApparelAmount = findViewById(R.id.analyticApparelAmount);
        analyticHealthAmount = findViewById(R.id.analyticHealthAmount);
        analyticPersonalAmount = findViewById(R.id.analyticPersonalAmount);
        analyticOtherAmount = findViewById(R.id.analyticOtherAmount);
        anyChartView = findViewById(R.id.anyChartView);

        transport = findViewById(R.id.transport);
        food = findViewById(R.id.food);
        house = findViewById(R.id.house);
        entertainment = findViewById(R.id.entertainment);
        education = findViewById(R.id.education);
        charity = findViewById(R.id.charity);
        apparel = findViewById(R.id.apparel);
        health = findViewById(R.id.health);
        personal = findViewById(R.id.personal);
        other = findViewById(R.id.other);

        getTotalWeekTransportExpense();
        getTotalWeekFoodExpense();
        getTotalWeekHouseExpense();
        getTotalWeekEntertainmentExpense();
        getTotalWeekEducationExpense();
        getTotalWeekCharityExpense();
        getTotalWeekApparelExpense();
        getTotalWeekHealthExpense();
        getTotalWeekPersonalExpense();
        getTotalWeekOtherExpense();
        getTodayAmount();
        loadGraph();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        loadGraph();
                    }
                },
                2000
        );
    }


    private void getTodayAmount() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(mAuth.getCurrentUser().getUid());
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    double totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount+=pTotal;
                    }
                    totalSpending.setText(String.format("RM %.2f", totalAmount));
                } else {
                    totalSpending.setText("You've not spent today");
                    anyChartView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getTotalWeekOtherExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Other" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticOtherAmount.setText(String.format("RM %.2f", totalAmount));
                    }
                    personalRef.child("dayOther").setValue(totalAmount);
                } else {
                    other.setVisibility(View.GONE);
                    personalRef.child("dayOther").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalWeekPersonalExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Personal" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticPersonalAmount.setText(String.format("$ %.2f", totalAmount));
                    }
                    personalRef.child("dayPer").setValue(totalAmount);
                } else {
                    personal.setVisibility(View.GONE);
                    personalRef.child("dayPer").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalWeekHealthExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Health" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticHealthAmount.setText(String.format("RM %.2f", totalAmount));
                    }
                    personalRef.child("dayHea").setValue(totalAmount);
                } else {
                    health.setVisibility(View.GONE);
                    personalRef.child("dayHea").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalWeekApparelExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Apparel" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticApparelAmount.setText(String.format("RM %.2f", totalAmount));
                    }
                    personalRef.child("dayApp").setValue(totalAmount);
                } else {
                    apparel.setVisibility(View.GONE);
                    personalRef.child("dayApp").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalWeekCharityExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Charity" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticCharityAmount.setText(String.format("RM %.2f", totalAmount));
                    }
                    personalRef.child("dayCha").setValue(totalAmount);
                } else {
                    charity.setVisibility(View.GONE);
                    personalRef.child("dayCha").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalWeekEducationExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Education" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticEducationAmount.setText(String.format("RM %.2f", totalAmount));
                    }
                    personalRef.child("dayEdu").setValue(totalAmount);
                } else {
                    education.setVisibility(View.GONE);
                    personalRef.child("dayEdu").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalWeekEntertainmentExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Entertainment" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticEntertainmentAmount.setText(String.format("RM %.2f", totalAmount));
                    }
                    personalRef.child("dayEnt").setValue(totalAmount);
                } else {
                    entertainment.setVisibility(View.GONE);
                    personalRef.child("dayEnt").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalWeekHouseExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "House" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticHouseAmount.setText(String.format("RM %.2f", totalAmount));
                    }
                    personalRef.child("dayHouse").setValue(totalAmount);
                } else {
                    house.setVisibility(View.GONE);
                    personalRef.child("dayHouse").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalWeekFoodExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Food" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticFoodAmount.setText(String.format("RM %.2f", totalAmount));
                    }
                    personalRef.child("dayFood").setValue(totalAmount);
                } else {
                    food.setVisibility(View.GONE);
                    personalRef.child("dayFood").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalWeekTransportExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Transport" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount+=pTotal;
                        analyticTransportAmount.setText(String.format("RM %.2f", totalAmount));
                    }
                    personalRef.child("dayTrans").setValue(totalAmount);
                } else {
                    transport.setVisibility(View.GONE);
                    personalRef.child("dayTrans").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGraph() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double traTotal;
                    if (snapshot.hasChild("dayTrans")) {
                        traTotal = Double.parseDouble(snapshot.child("dayTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    double foodTotal;
                    if (snapshot.hasChild("dayFood")) {
                        foodTotal = Double.parseDouble(snapshot.child("dayFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    double houseTotal;
                    if (snapshot.hasChild("dayHouse")) {
                        houseTotal = Double.parseDouble(snapshot.child("dayHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }

                    double entTotal;
                    if (snapshot.hasChild("dayEnt")) {
                        entTotal = Double.parseDouble(snapshot.child("dayEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }

                    double eduTotal;
                    if (snapshot.hasChild("dayEdu")) {
                        eduTotal = Double.parseDouble(snapshot.child("dayEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }

                    double chaTotal;
                    if (snapshot.hasChild("dayCha")) {
                        chaTotal = Double.parseDouble(snapshot.child("dayCha").getValue().toString());
                    } else {
                        chaTotal = 0;
                    }

                    double appTotal;
                    if (snapshot.hasChild("dayApp")) {
                        appTotal = Double.parseDouble(snapshot.child("dayApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    double heaTotal;
                    if (snapshot.hasChild("dayHea")) {
                        heaTotal = Double.parseDouble(snapshot.child("dayHea").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }

                    double perTotal;
                    if (snapshot.hasChild("dayPer")) {
                        perTotal = Double.parseDouble(snapshot.child("dayPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    double otherTotal;
                    if (snapshot.hasChild("dayOther")) {
                        otherTotal = Double.parseDouble(snapshot.child("dayOther").getValue().toString());
                    } else {
                        otherTotal = 0;
                    }

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Transport",traTotal));
                    data.add(new ValueDataEntry("House",houseTotal));
                    data.add(new ValueDataEntry("Food",foodTotal));
                    data.add(new ValueDataEntry("Entertainment",entTotal));
                    data.add(new ValueDataEntry("Education",eduTotal));
                    data.add(new ValueDataEntry("Charity",chaTotal));
                    data.add(new ValueDataEntry("Apparel",appTotal));
                    data.add(new ValueDataEntry("Health",heaTotal));
                    data.add(new ValueDataEntry("Personal",perTotal));
                    data.add(new ValueDataEntry("Other",otherTotal));

                    pie.data(data);
                    pie.title("Daily Analytics");
                    pie.labels().position("outside");
                    pie.legend().title().enabled(true);
                    pie.legend().title()
                            .text("Items Spent On")
                            .padding(0d, 0d, 10d, 0d);

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);

                    anyChartView.setChart(pie);
                } else {
                    Toast.makeText(DailyAnalyticActivity.this, "Child does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}