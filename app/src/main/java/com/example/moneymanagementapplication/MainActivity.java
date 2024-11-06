package com.example.moneymanagementapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.core.stock.series.Stick;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private TextView budgetAmount,remainingAmount, todayAmount, weeklyAmount, monthlyAmount,toDropdown, fromDropdown, currency, result;
    private CardView todayCardView, weekCardView, monthCardView,analyticCardView,budgetCardView, historyCardView;
    private ImageView account;
    private EditText amountConvert;
    private Button convertButton;
    private ArrayList<String> arrayList;
    private String convertFromValue, convertToValue, conversionValue;
    private String[] country = {"ALL","AFN","ARS","AWG","AUD","AZN","BSD","BBD","BDT","BYR","BZD","BMD","BOB","BAM","BWP","BGN","BRL","BND","KHR","CAD","KYD","CLP","CNY","COP","CRC","HRK","CUP","CZK","DKK","DOP","XCD","EGP","SVC","EEK","EUR","FKP","FJD","GHC","GIP","GTQ","GGP","GYD","HNL","HKD","HUF","ISK","INR","IDR","IRR","IMP","ILS","JMD","JPY","JEP","KZT","KPW","KRW","KGS","LAK","LVL","LBP","LRD","LTL","MKD","MYR","MUR","MXN","MNT","MZN","NAD","NPR","ANG","NZD","NIO","NGN","NOK","OMR","PKR","PAB","PYG","PEN","PHP","PLN","QAR","RON","RUB","SHP","SAR","RSD","SCR","SGD","SBD","SOS","ZAR","LKR","SEK","CHF","SRD","SYP","TWD","THB","TTD","TRY","TRL","TVD","UAH","GBP","USD","UYU","UZS","VEF","VND","YER","ZWD"};

    private DatabaseReference budgetRef, expensesRef, personalRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        expensesRef = FirebaseDatabase.getInstance().getReference().child("expenses").child(mAuth.getCurrentUser().getUid());
        personalRef = FirebaseDatabase.getInstance().getReference().child("personal").child(mAuth.getCurrentUser().getUid());

        budgetAmount = findViewById(R.id.budgetAmount);
        remainingAmount = findViewById(R.id.remainingAmount);
        todayAmount = findViewById(R.id.todayAmount);
        weeklyAmount = findViewById(R.id.weeklyAmount);
        monthlyAmount = findViewById(R.id.monthlyAmount);

        todayCardView = findViewById(R.id.todayCardView);
        weekCardView = findViewById(R.id.weekCardView);
        monthCardView = findViewById(R.id.monthCardView);
        analyticCardView = findViewById(R.id.analyticCardView);
        budgetCardView = findViewById(R.id.budgetCardView);
        historyCardView = findViewById(R.id.historyCardView);

        toDropdown = findViewById(R.id.toDropdown);
        fromDropdown = findViewById(R.id.fromDropdown);
        currency = findViewById(R.id.conversionRateText);
        amountConvert = findViewById(R.id.amountToConvert);
        convertButton = findViewById(R.id.conversionButton);
        result = findViewById(R.id.result);

        account = findViewById(R.id.account);

        arrayList = new ArrayList<>();
        for (String i : country) {
            arrayList.add(i);
        }

        toDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                dialog.getWindow().setLayout(650,800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText = dialog.findViewById(R.id.editText);
                ListView listView = dialog.findViewById(R.id.listView);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        toDropdown.setText(adapter.getItem(i));
                        dialog.dismiss();
                        convertToValue = adapter.getItem(i);
                    }
                });
            }
        });

        fromDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                dialog.getWindow().setLayout(650,800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText = dialog.findViewById(R.id.editText);
                ListView listView = dialog.findViewById(R.id.listView);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        fromDropdown.setText(adapter.getItem(i));
                        dialog.dismiss();
                        convertFromValue = adapter.getItem(i);
                    }
                });
            }
        });

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String amountCheck = amountConvert.getText().toString();
                    if (TextUtils.isEmpty(amountCheck)) {
                        amountConvert.setError("Cannot be empty");
                    }
                        Double amountToConvert = Double.valueOf(MainActivity.this.amountConvert.getText().toString());
                        getConvertRate(convertToValue, amountToConvert);
                } catch (Exception e) {

                }
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AccountActivity.class);
                startActivity(intent);
            }
        });

        todayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TodaySpendingActivity.class);
                startActivity(intent);
            }
        });

        weekCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeekSpendingActivity.class);
                intent.putExtra("type","week");
                startActivity(intent);
            }
        });

        monthCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeekSpendingActivity.class);
                intent.putExtra("type","month");
                startActivity(intent);
            }
        });

        analyticCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AnalyticActivity.class);
                startActivity(intent);
            }
        });

        budgetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });

        historyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });


        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        double pTotal = Double.parseDouble(String.valueOf(total));
                    }
                } else {
                    personalRef.child("budget").setValue(0);
                    Toast.makeText(MainActivity.this, "Please set a BUDGET", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        getBudgetAmount();
        getTodayAmount();
        getWeeklyAmount();
        getMonthlyAmount();
        getSaving();

    }

    private String getConvertRate(String convertToValue, Double amountToConvert) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://free.currconv.com/api/v7/convert?q="+convertFromValue+"_"+convertToValue+"&compact=ultra&apiKey=";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    Double convertRateValue = (Double) jsonObject.get(convertFromValue + "_" + convertToValue);
                    conversionValue = "" + (convertRateValue * amountToConvert);
                    currency.setText(Double.toString(convertRateValue));
                    result.setText(conversionValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        queue.add(stringRequest);
        return null;
    }

    private void getBudgetAmount() {
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    double totalAmount = 0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Data data = ds.getValue(Data.class);
                        totalAmount += data.getAmount();
                        String total = (String.format("RM %.2f", totalAmount));
                        budgetAmount.setText(total);
                    }
                } else {
                    budgetAmount.setText("RM 0.00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                        todayAmount.setText(String.format("RM %.2f", totalAmount));
                    }
                    personalRef.child("today").setValue(totalAmount);
                } else {
                    todayAmount.setText("RM 0.00");
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMonthlyAmount() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(mAuth.getCurrentUser().getUid());
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
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
                        monthlyAmount.setText(String.format("RM %.2f", totalAmount));
                    }
                    personalRef.child("month").setValue(totalAmount);
                } else {
                    monthlyAmount.setText("RM 0.00");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getWeeklyAmount() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(mAuth.getCurrentUser().getUid());
        Query query = reference.orderByChild("week").equalTo(weeks.getWeeks());
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
                        weeklyAmount.setText(String.format("RM %.2f", totalAmount));
                    }
                    personalRef.child("week").setValue(totalAmount);
                } else {
                    weeklyAmount.setText("RM 0.00");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSaving() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double budget;
                    if (snapshot.hasChild("budget")) {
                        budget = Double.parseDouble(snapshot.child("budget").getValue().toString());
                    } else {
                        budget = 0;
                    }
                    double monthSpending;
                    if (snapshot.hasChild("month")) {
                        monthSpending = Double.parseDouble(Objects.requireNonNull(snapshot.child("month").getValue().toString()));
                    } else {
                        monthSpending = 0;
                    }

                    double savings = budget - monthSpending;
                    remainingAmount.setText(String.format("%.2f", savings));
                } else {
                    remainingAmount.setText("RM 0.00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}