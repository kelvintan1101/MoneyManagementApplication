package com.example.moneymanagementapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
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
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeeklyAnalyticActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, personalRef;

    private CardView transport, food, house, entertainment, education, charity, apparel, health, personal, other;

    private TextView totalSpending, analyticTransportAmount, analyticFoodAmount ,analyticHouseAmount ,analyticEntertainmentAmount ,analyticEducationAmount, analyticCharityAmount, analyticApparelAmount, analyticHealthAmount, analyticPersonalAmount, analyticOtherAmount;
    private AnyChartView anyChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_analytic);

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
        getTotalWeekSpending();

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

    private void getTotalWeekSpending() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0) {
                    double totalAmount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String,Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    totalSpending.setText(String.format("RM %.2f",totalAmount));
                } else {
                    totalSpending.setText("You've not spent this week");
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
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Other" + weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String,Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticOtherAmount.setText(String.format("RM %.2f",totalAmount));
                    }
                    personalRef.child("weekOther").setValue(totalAmount);
                } else {
                    other.setVisibility(View.GONE);
                    personalRef.child("weekOther").setValue(0);
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
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Personal" + weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String,Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticPersonalAmount.setText(String.format("RM %.2f",totalAmount));
                    }
                    personalRef.child("weekPer").setValue(totalAmount);
                } else {
                    personal.setVisibility(View.GONE);
                    personalRef.child("weekPer").setValue(0);
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
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Health" + weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String,Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticHealthAmount.setText(String.format("RM %.2f",totalAmount));
                    }
                    personalRef.child("weekHea").setValue(totalAmount);
                } else {
                    health.setVisibility(View.GONE);
                    personalRef.child("weekHea").setValue(0);
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
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Apparel" + weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String,Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticApparelAmount.setText(String.format("RM %.2f",totalAmount));
                    }
                    personalRef.child("weekApp").setValue(totalAmount);
                } else {
                    apparel.setVisibility(View.GONE);
                    personalRef.child("weekApp").setValue(0);
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
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Charity" + weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String,Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticCharityAmount.setText(String.format("RM %.2f",totalAmount));
                    }
                    personalRef.child("weekCha").setValue(totalAmount);
                } else {
                    charity.setVisibility(View.GONE);
                    personalRef.child("weekCha").setValue(0);
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
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Education" + weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String,Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticEducationAmount.setText(String.format("RM %.2f",totalAmount));
                    }
                    personalRef.child("weekEdu").setValue(totalAmount);
                } else {
                    education.setVisibility(View.GONE);
                    personalRef.child("weekEdu").setValue(0);
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
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Entertainment" + weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String,Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticEntertainmentAmount.setText(String.format("RM %.2f",totalAmount));
                    }
                    personalRef.child("weekEnt").setValue(totalAmount);
                } else {
                    entertainment.setVisibility(View.GONE);
                    personalRef.child("weekEnt").setValue(0);
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
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "House" + weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String,Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticHouseAmount.setText(String.format("RM %.2f",totalAmount));
                    }
                    personalRef.child("weekHouse").setValue(totalAmount);
                } else {
                    house.setVisibility(View.GONE);
                    personalRef.child("weekHouse").setValue(0);
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
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Food" + weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String,Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticFoodAmount.setText(String.format("RM %.2f",totalAmount));
                    }
                    personalRef.child("weekFood").setValue(totalAmount);
                } else {
                    food.setVisibility(View.GONE);
                    personalRef.child("weekFood").setValue(0);
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
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNweek = "Transport" + weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNweek").equalTo(itemNweek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalAmount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String,Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticTransportAmount.setText(String.format("RM %.2f",totalAmount));
                    }
                    personalRef.child("weekTrans").setValue(totalAmount);
                } else {
                    transport.setVisibility(View.GONE);
                    personalRef.child("weekTrans").setValue(0);
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
                    if (snapshot.hasChild("weekTrans")) {
                        traTotal = Double.parseDouble(snapshot.child("weekTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    double foodTotal;
                    if (snapshot.hasChild("weekFood")) {
                        foodTotal = Double.parseDouble(snapshot.child("weekFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    double houseTotal;
                    if (snapshot.hasChild("weekHouse")) {
                        houseTotal = Double.parseDouble(snapshot.child("weekHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }

                    double entTotal;
                    if (snapshot.hasChild("weekEnt")) {
                        entTotal = Double.parseDouble(snapshot.child("weekEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }

                    double eduTotal;
                    if (snapshot.hasChild("weekEdu")) {
                        eduTotal = Double.parseDouble(snapshot.child("weekEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }

                    double chaTotal;
                    if (snapshot.hasChild("weekCha")) {
                        chaTotal = Double.parseDouble(snapshot.child("weekCha").getValue().toString());
                    } else {
                        chaTotal = 0;
                    }

                    double appTotal;
                    if (snapshot.hasChild("weekApp")) {
                        appTotal = Double.parseDouble(snapshot.child("weekApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    double heaTotal;
                    if (snapshot.hasChild("weekHea")) {
                        heaTotal = Double.parseDouble(snapshot.child("weekHea").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }

                    double perTotal;
                    if (snapshot.hasChild("weekPer")) {
                        perTotal = Double.parseDouble(snapshot.child("weekPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    double otherTotal;
                    if (snapshot.hasChild("weekOther")) {
                        otherTotal = Double.parseDouble(snapshot.child("weekOther").getValue().toString());
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
                    pie.title("Weekly Analytics");
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
                    Toast.makeText(WeeklyAnalyticActivity.this, "Child does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}