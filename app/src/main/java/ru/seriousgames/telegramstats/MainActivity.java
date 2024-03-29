package ru.seriousgames.telegramstats;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import ru.seriousgames.telegramstats.chartlib.Chart;
import ru.seriousgames.telegramstats.chartlib.ChartSlider;
import ru.seriousgames.telegramstats.chartlib.ChartView;

public class MainActivity extends AppCompatActivity {

    JSONArray chartArray;
    ArrayList<Chart> charts;
    ChartView chartView;
    ChartSlider chartSlider;
    LinearLayout chartList;
    ArrayList<CheckBox> boxes;
    boolean darkTheme = false;
    int currentChart;
    Spinner spinner;
    CardView info;
    LinearLayout infoData;
    TextView dateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        charts = new ArrayList<>();
        parseJSON(this);

        setContentView(R.layout.activity_main);

        chartList = findViewById(R.id.chart_list);
        boxes = new ArrayList<>();

        chartView = findViewById(R.id.chartView);
        chartView.setCharts(charts);
        chartSlider = findViewById(R.id.chartSlider);
        chartSlider.setChartView(chartView);
        chartSlider.setCharts(charts);
        info = findViewById(R.id.info);
        infoData = findViewById(R.id.data);
        dateView = findViewById(R.id.date);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.chart_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setCurrentChart(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setCurrentChart(0);
    }

    public void hideInfo(){
        info.setVisibility(View.GONE);
    }

    public void setDataToInfoActivity(String[] arr, String date, float x){
        Chart chart = getCurrentChart();
        info.setVisibility(View.VISIBLE);
        dateView.setText(date);
        infoData.removeAllViews();
        int ind = 0;
        for (int i=0; i<chart.y.length; i++){
            if (chart.yVisible[i]){
                View v = LayoutInflater.from(this).inflate(R.layout.title_layout, infoData, false);
                TextView amount = v.findViewById(R.id.amount);
                TextView title = v.findViewById(R.id.title);
                amount.setTextColor(chart.yColors[i]);
                title.setTextColor(chart.yColors[i]);
                amount.setText(arr[ind]);
                title.setText(chart.yNames[i]);
                ind++;
                infoData.addView(v);
                info.setX(
                                (x + 5 + info.getWidth() > chartView.getWidth() ?
                                        chartView.getWidth() - info.getWidth() - 5 :
                                        x + 5)
                );
            }
        }
    }

    public void setLineVisibility(int line, boolean b){
        chartSlider.setLineVisibility(line, b);
        chartView.setLineVisibility(line, b);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.theme:
                darkTheme = !darkTheme;
                AppCompatDelegate.setDefaultNightMode(
                        (darkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));
                recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCheckBoxListeners(){
        for(int i=0; i < boxes.size(); i++){
            final int ii = i;
            final CheckBox box = boxes.get(i);
            box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    setLineVisibility(ii, box.isChecked());
                }
            });
        }
    }

    private void setCurrentChart(int chart){
        this.currentChart = chart;
        for (int i = 0; i < getCurrentChart().yVisible.length; i++){
            getCurrentChart().yVisible[i] = true;
        }
        this.chartView.setCurrentChart(chart);
        this.chartSlider.setCurrentChart(chart);

        chartList.removeAllViews();
        boxes.clear();
        addChartList(currentChart);
        setCheckBoxListeners();
    }

    public Chart getCurrentChart(){
        return charts.get(currentChart);
    }

    private void addChartList(int chartIndex){
        Chart chart = charts.get(chartIndex);
        for (int i=0; i<chart.y.length; i++){
            View v = LayoutInflater.from(this).inflate(R.layout.chart_line, chartList, false);
            ColorStateList lst = new ColorStateList(
                    new int[][]{
                            new int[] {android.R.attr.state_enabled}
                    },
                    new int[]{
                        chart.yColors[i]
                    }
            );
            CheckBox box = ((CheckBox)v.findViewById(R.id.checkBox));
            box.setButtonTintList(lst);
            box.setText(chart.yNames[i]);
            boxes.add(box);
            chartList.addView(v);
        }
    }

    private void parseJSON(Context ctx){
        try {
            this.chartArray = new JSONArray(getJSONString(ctx));
            this.charts = new ArrayList<>();

            for (int i = 0; i < chartArray.length(); i++){
                charts.add(Chart.parseChartFromJSON(
                        chartArray.getJSONObject(i)
                ));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getJSONString(Context ctx){
        String json = null;

        try{
            InputStream stream = ctx.getAssets().open("chart_data.json");
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            stream.close();

            json = new String(bytes, StandardCharsets.UTF_8);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return json;
    }

}
