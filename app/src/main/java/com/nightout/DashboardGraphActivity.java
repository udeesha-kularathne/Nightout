package com.nightout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.nightout.integration; // Adjust the package path as needed

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nightout.models.ModelDashboard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DashboardGraphActivity extends AppCompatActivity {

    private LineChart lineChart;
    private BarChart barChart;
    private LineChart monthProfitLineChart;
    private BarChart costVsProfitBarChart;
    private BarChart costVsIncomeBarChart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_graph);

//        Object integration = new Object();
//        com.nightout.integration PythonIntegration;
//        com.nightout.integration.initializePython((Context) integration); // Pass the activity's context

        lineChart = findViewById(R.id.lineChart);
        barChart = findViewById(R.id.barChart);
        monthProfitLineChart = findViewById(R.id.monthProfitLineChart);
        costVsProfitBarChart = findViewById(R.id.costVsProfitBarChart);
        costVsIncomeBarChart = findViewById(R.id.costVsIncomeBarChart);

        // Retrieve data and create the line and bar graphs
        retrieveAndPlotLineGraph();
        retrieveAndPlotBarGraph();
        retrieveAndPlotMonthProfitLineGraph();
        retrieveAndPlotCostVsProfitBarChart();
        retrieveAndPlotCostVsIncomeBarChart();
//        calculateStatisticsFromDatabase();

        Button btnOrganizationDashboard1 = findViewById(R.id.btnOrganizationDashboard1);
        Button btnOrganizationDashboard2 = findViewById(R.id.btnOrganizationDashboard2);
        btnOrganizationDashboard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to OrganizationDashboardActivity
                Intent intent = new Intent(DashboardGraphActivity.this, OrganizationDashboardActivity.class);
                startActivity(intent);
            }
        });
        btnOrganizationDashboard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to OrganizationDashboardActivity
                Intent intent = new Intent(DashboardGraphActivity.this, EventHistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void retrieveAndPlotLineGraph() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference("DataCost").orderByChild("uid").equalTo(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelDashboard> dataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelDashboard data = snapshot.getValue(ModelDashboard.class);
                    dataList.add(data);
                }

                // Create the entries for profit and sponsor income
                List<Entry> profitEntries = new ArrayList<>();
                List<Entry> sponsorIncomeEntries = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    ModelDashboard data = dataList.get(i);
                    float xValue = i; // X-axis value is the index
                    float profitYValue = (float) data.getTotalProfit();
                    float sponsorIncomeYValue = (float) Double.parseDouble(data.getSponsorIncome());
                    profitEntries.add(new Entry(xValue, profitYValue));
                    sponsorIncomeEntries.add(new Entry(xValue, sponsorIncomeYValue));
                }

                // Create datasets for profit and sponsor income
                LineDataSet profitDataSet = new LineDataSet(profitEntries, "Profit");
                profitDataSet.setColor(Color.BLUE);
                profitDataSet.setCircleColor(Color.RED);
                profitDataSet.setCircleRadius(4f);

                LineDataSet sponsorIncomeDataSet = new LineDataSet(sponsorIncomeEntries, "Sponsor Income");
                sponsorIncomeDataSet.setColor(Color.GREEN);
                sponsorIncomeDataSet.setCircleColor(Color.YELLOW);
                sponsorIncomeDataSet.setCircleRadius(4f);

                // Create LineData object and add datasets
                LineData lineData = new LineData(profitDataSet, sponsorIncomeDataSet);

                // Customize X-axis
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                // Customize Y-axis
                YAxis yAxisLeft = lineChart.getAxisLeft();
                YAxis yAxisRight = lineChart.getAxisRight();
                yAxisLeft.setGranularity(1f);
                yAxisRight.setGranularity(1f);

                // Set the LineData to the chart
                lineChart.setData(lineData);

                // Refresh the chart
                lineChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void retrieveAndPlotBarGraph() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference("DataCost").orderByChild("uid").equalTo(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelDashboard> dataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelDashboard data = snapshot.getValue(ModelDashboard.class);
                    dataList.add(data);
                }

                // Create the entries for ticket prices and ticket count
                List<BarEntry> barEntries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    ModelDashboard data = dataList.get(i);
                    float xValue = (float) Double.parseDouble(data.getTicketPrice()); // X-axis value is ticket price
                    float yValue = (float) Double.parseDouble(data.getTicketCount()); // Y-axis value is ticket count
                    barEntries.add(new BarEntry(xValue, yValue));
                }

                // Create a dataset for the bar chart
                BarDataSet dataSet = new BarDataSet(barEntries, "Ticket Count vs Ticket Prices");
                dataSet.setColor(Color.BLUE);

                // Create BarData object and add the dataset
                BarData barData = new BarData(dataSet);

                // Customize X-axis
                XAxis xAxis = barChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                // Customize Y-axis
                YAxis yAxisLeft = barChart.getAxisLeft();
                YAxis yAxisRight = barChart.getAxisRight();
                yAxisLeft.setGranularity(1f);
                yAxisRight.setGranularity(1f);

                // Set the BarData to the chart
                barChart.setData(barData);

                // Refresh the chart
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void retrieveAndPlotMonthProfitLineGraph() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference("DataCost").orderByChild("uid").equalTo(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelDashboard> dataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelDashboard data = snapshot.getValue(ModelDashboard.class);
                    dataList.add(data);
                }

                // Sort dataList based on months
                Collections.sort(dataList, new Comparator<ModelDashboard>() {
                    @Override
                    public int compare(ModelDashboard data1, ModelDashboard data2) {
                        // Assuming the month format is a string like "January", "February", etc.
                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.ENGLISH);
                        try {
                            Date date1 = sdf.parse(data1.getMonth());
                            Date date2 = sdf.parse(data2.getMonth());
                            return date1.compareTo(date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });

                // Create the entries for profit against months
                List<Entry> monthProfitEntries = new ArrayList<>();
                List<String> monthLabels = new ArrayList<>(); // To store the month names
                for (int i = 0; i < dataList.size(); i++) {
                    ModelDashboard data = dataList.get(i);
                    float xValue = i; // X-axis value is the index
                    float yValue = (float) data.getTotalProfit(); // Use the double value directly
                    monthProfitEntries.add(new Entry(xValue, yValue));

                    // Get the month name and add it to the labels list
                    String monthName = data.getMonth(); // Assuming you have a method to get the month name
                    monthLabels.add(monthName);
                }

                // Create a dataset for the line chart
                LineDataSet dataSet = new LineDataSet(monthProfitEntries, "Profit by Month");
                dataSet.setColor(Color.GREEN);
                dataSet.setCircleColor(Color.YELLOW);
                dataSet.setCircleRadius(4f);

                // Create LineData object and set the dataset
                LineData lineData = new LineData(dataSet);

                // Customize X-axis
                XAxis xAxis = monthProfitLineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int index = (int) value;
                        if (index >= 0 && index < monthLabels.size()) {
                            return monthLabels.get(index);
                        }
                        return ""; // Return an empty string for invalid indices
                    }
                });

                // Customize Y-axis
                YAxis yAxisLeft = monthProfitLineChart.getAxisLeft();
                YAxis yAxisRight = monthProfitLineChart.getAxisRight();
                yAxisLeft.setGranularity(1f);
                yAxisRight.setGranularity(1f);

                // Set the LineData to the chart
                monthProfitLineChart.setData(lineData);

                // Refresh the chart
                monthProfitLineChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    // New function to retrieve data and create the cost vs profit grouped bar chart
    private void retrieveAndPlotCostVsProfitBarChart() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference("DataCost").orderByChild("uid").equalTo(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelDashboard> dataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelDashboard data = snapshot.getValue(ModelDashboard.class);
                    dataList.add(data);
                }

                // Create entries for each cost component and profit
                // Create entries for each cost component and profit
                // Create entries for each cost component and profit
                List<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>(); // To store the labels (cost component names)

// Add entries for each cost component and profit
                for (int i = 0; i < dataList.size(); i++) {
                    ModelDashboard data = dataList.get(i);
                    float xValue = i; // X-axis value is the index

                    // Convert cost components from strings to floats
                    float marketingCost = Float.parseFloat(data.getMarketingCost());
                    float laborCost = Float.parseFloat(data.getLaborCost());
                    float utilitiesCost = Float.parseFloat(data.getUtilitiesCost());
                    float personnelCost = Float.parseFloat(data.getPersonnelCost());
                    float venueCost = Float.parseFloat(data.getVenueCost());
                    float materialCost = Float.parseFloat(data.getMaterialCost());
                    float totalProfit = (float) data.getTotalProfit(); // Convert profit to float

                    // Add entries for each cost component and profit
                    entries.add(new BarEntry(xValue, new float[]{
                            marketingCost,
                            laborCost,
                            utilitiesCost,
                            personnelCost,
                            venueCost,
                            materialCost,
                            totalProfit
                    }));

                    // Populate the labels with cost component names
                    labels.add("Marketing");
                    labels.add("Labor");
                    labels.add("Utilities");
                    labels.add("Personnel");
                    labels.add("Venue");
                    labels.add("Material");
                    labels.add("Profit");
                }

                // Create a dataset for the grouped bar chart
                BarDataSet dataSet = new BarDataSet(entries, "Cost vs Profit");
                dataSet.setColors(new int[]{
                        Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.LTGRAY
                }); // Set different colors for each cost component

                // Create BarData object and add the dataset
                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.2f); // Adjust the bar width

                // Customize X-axis
                XAxis xAxis = costVsProfitBarChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int index = (int) value;
                        if (index >= 0 && index < labels.size()) {
                            return labels.get(index);
                        }
                        return ""; // Return an empty string for invalid indices
                    }
                });

                // Customize Y-axis
                YAxis yAxisLeft = costVsProfitBarChart.getAxisLeft();
                YAxis yAxisRight = costVsProfitBarChart.getAxisRight();
                yAxisLeft.setGranularity(1f);
                yAxisRight.setGranularity(1f);

                // Set the BarData to the chart
                costVsProfitBarChart.setData(barData);

                // Refresh the chart
                costVsProfitBarChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void retrieveAndPlotCostVsIncomeBarChart() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference("DataCost").orderByChild("uid").equalTo(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelDashboard> dataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelDashboard data = snapshot.getValue(ModelDashboard.class);
                    dataList.add(data);
                }

                // Create entries for total cost and total income
                List<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    ModelDashboard data = dataList.get(i);
                    float xValue = i; // X-axis value is the index

                    // Calculate total income (ticket count * ticket price + sponsor income + sales income)
                    float ticketCount = Float.parseFloat(data.getTicketCount());
                    float ticketPrice = Float.parseFloat(data.getTicketPrice());
                    float sponsorIncome = Float.parseFloat(data.getSponsorIncome());
                    float salesIncome = Float.parseFloat(data.getSalesIncome());
                    float totalIncome = ticketCount * ticketPrice + sponsorIncome + salesIncome;

                    // Calculate total cost based on cost components
                    float marketingCost = Float.parseFloat(data.getMarketingCost());
                    float laborCost = Float.parseFloat(data.getLaborCost());
                    float utilitiesCost = Float.parseFloat(data.getUtilitiesCost());
                    float personnelCost = Float.parseFloat(data.getPersonnelCost());
                    float venueCost = Float.parseFloat(data.getVenueCost());
                    float materialCost = Float.parseFloat(data.getMaterialCost());
                    float totalCost = marketingCost + laborCost + utilitiesCost + personnelCost + venueCost + materialCost;

                    // Add entries for total cost and total income
                    entries.add(new BarEntry(xValue, new float[]{totalCost, totalIncome}));

                    // Populate the labels with corresponding names
                    labels.add("Total Cost");
                    labels.add("Total Income");
                }

                // Create a dataset for the grouped bar chart
                BarDataSet dataSet = new BarDataSet(entries, "Cost vs Income");
                dataSet.setColors(new int[]{Color.RED, Color.GREEN}); // Set different colors for cost and income

                // Create BarData object and add the dataset
                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.2f); // Adjust the bar width

                // Customize X-axis
                XAxis xAxis = costVsIncomeBarChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int index = (int) value;
                        if (index >= 0 && index < labels.size()) {
                            return labels.get(index);
                        }
                        return ""; // Return an empty string for invalid indices
                    }
                });

                // Customize Y-axis
                YAxis yAxisLeft = costVsIncomeBarChart.getAxisLeft();
                YAxis yAxisRight = costVsIncomeBarChart.getAxisRight();
                yAxisLeft.setGranularity(1f);
                yAxisRight.setGranularity(1f);

                // Set the BarData to the chart
                costVsIncomeBarChart.setData(barData);

                // Refresh the chart
                costVsIncomeBarChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


}
