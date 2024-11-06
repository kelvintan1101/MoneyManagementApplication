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
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MonthlyAnalyticActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, personalRef;

    private CardView transport, food, house, entertainment, education, charity, apparel, health, personal, other;

    private TextView totalSpending, analyticTransportAmount, analyticFoodAmount ,analyticHouseAmount ,analyticEntertainmentAmount ,analyticEducationAmount, analyticCharityAmount, analyticApparelAmount, analyticHealthAmount, analyticPersonalAmount, analyticOtherAmount;
    private AnyChartView anyChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_analytic);

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

        getTotalMonthTransportExpense();
        getTotalMonthFoodExpense();
        getTotalMonthHouseExpense();
        getTotalMonthEntertainmentExpense();
        getTotalMonthEducationExpense();
        getTotalMonthCharityExpense();
        getTotalMonthApparelExpense();
        getTotalMonthHealthExpense();
        getTotalMonthPersonalExpense();
        getTotalMonthOtherExpense();
        getTotalMonthSpending();

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

    private void getTotalMonthSpending() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
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
                    totalSpending.setText("You've not spent this month");
                    anyChartView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthOtherExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Other" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
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
                    personalRef.child("monthOther").setValue(totalAmount);
                } else {
                    other.setVisibility(View.GONE);
                    personalRef.child("monthOther").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthPersonalExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Personal" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
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
                    personalRef.child("monthPer").setValue(totalAmount);
                } else {
                    personal.setVisibility(View.GONE);
                    personalRef.child("monthPer").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthHealthExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Health" +months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
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
                    personalRef.child("monthHea").setValue(totalAmount);
                } else {
                    health.setVisibility(View.GONE);
                    personalRef.child("monthHea").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthApparelExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Apparel" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
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
                    personalRef.child("monthApp").setValue(totalAmount);
                } else {
                    apparel.setVisibility(View.GONE);
                    personalRef.child("monthApp").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthCharityExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Charity" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
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
                    personalRef.child("monthCha").setValue(totalAmount);
                } else {
                    charity.setVisibility(View.GONE);
                    personalRef.child("monthCha").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthEducationExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Education" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
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
                    personalRef.child("monthEdu").setValue(totalAmount);
                } else {
                    education.setVisibility(View.GONE);
                    personalRef.child("monthEdu").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthEntertainmentExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Entertainment" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
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
                    personalRef.child("monthEnt").setValue(totalAmount);
                } else {
                    entertainment.setVisibility(View.GONE);
                    personalRef.child("monthEnt").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthHouseExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "House" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
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
                    personalRef.child("monthHouse").setValue(totalAmount);
                } else {
                    house.setVisibility(View.GONE);
                    personalRef.child("monthHouse").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthFoodExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Food" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
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
                    personalRef.child("monthFood").setValue(totalAmount);
                } else {
                    food.setVisibility(View.GONE);
                    personalRef.child("monthFood").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthTransportExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Transport" + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
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
                    personalRef.child("monthTrans").setValue(totalAmount);
                } else {
                    transport.setVisibility(View.GONE);
                    personalRef.child("monthTrans").setValue(0);
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
                    if (snapshot.hasChild("monthTrans")) {
                        traTotal = Double.parseDouble(snapshot.child("monthTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    double foodTotal;
                    if (snapshot.hasChild("monthFood")) {
                        foodTotal = Double.parseDouble(snapshot.child("monthFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    double houseTotal;
                    if (snapshot.hasChild("monthHouse")) {
                        houseTotal = Double.parseDouble(snapshot.child("monthHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }

                    double entTotal;
                    if (snapshot.hasChild("monthEnt")) {
                        entTotal = Double.parseDouble(snapshot.child("monthEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }

                    double eduTotal;
                    if (snapshot.hasChild("monthEdu")) {
                        eduTotal = Double.parseDouble(snapshot.child("monthEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }

                    double chaTotal;
                    if (snapshot.hasChild("monthCha")) {
                        chaTotal = Double.parseDouble(snapshot.child("monthCha").getValue().toString());
                    } else {
                        chaTotal = 0;
                    }

                    double appTotal;
                    if (snapshot.hasChild("monthApp")) {
                        appTotal = Double.parseDouble(snapshot.child("monthApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    double heaTotal;
                    if (snapshot.hasChild("monthHea")) {
                        heaTotal = Double.parseDouble(snapshot.child("monthHea").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }

                    double perTotal;
                    if (snapshot.hasChild("monthPer")) {
                        perTotal = Double.parseDouble(snapshot.child("monthPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    double otherTotal;
                    if (snapshot.hasChild("monthOther")) {
                        otherTotal = Double.parseDouble(snapshot.child("monthOther").getValue().toString());
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
                    pie.title("Monthly Analytics");
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
                    Toast.makeText(MonthlyAnalyticActivity.this, "Child does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}