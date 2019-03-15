package ru.seriousgames.telegramstats.chartlib;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONObject;

public class Chart {

    public long[] x;
    public int[][] y;
    public boolean[] yVisible;
    public int[] yTypes; // 1 = line
    public String[] yNames;
    public int[] yColors;
    public int maxY[], minY[];

    private Chart(){

    }

    public Chart(long[] x, int[][] y, int[] yTypes, String[] yNames, int[] yColors) {
        this.x = x;
        this.y = y;
        this.yTypes = yTypes;
        this.yNames = yNames;
        this.yColors = yColors;
        initVisibleArray();
    }

    public void initVisibleArray(){
        yVisible = new boolean[y.length];
        for (int i=0; i < y.length; i++){
            yVisible[i] = true;
        }
    }

    public void setLineVisibility(int line, boolean b){
        this.yVisible[line] = b;
    }

    public int getMaxYAmongVisible(){
        int max = 0;
        for (int i=0; i < maxY.length; i++){
            if (yVisible[i])
                max = Math.max(max, maxY[i]);
        }
        return max;
    }

    public int getMinYAmongVisible(){
        int min = minY[0];
        for (int i=0; i < minY.length; i++){
            if (yVisible[i])
                min = Math.min(min, minY[i]);
        }
        return min;
    }

    public void countMaxAndMinY(){
        this.maxY = new int[this.y.length];
        this.minY = new int[this.y.length];
        for (int i = 0; i < maxY.length; i++){
            int tMax = 0;
            for (int j = 0; j < y[i].length; j++){
                tMax = Math.max(tMax, y[i][j]);
            }
            int tMin = tMax;
            for (int j = 0; j < y[i].length; j++){
                tMin = Math.min(tMin, y[i][j]);
            }

            maxY[i] = tMax;
            minY[i] = tMin;
        }
    }

    public static Chart parseChartFromJSON(JSONObject object) {
        Chart chart = new Chart();
        try{
            JSONArray colomns = object.getJSONArray("columns");
            JSONArray xArr = colomns.getJSONArray(0);
            int lng = xArr.length();
            chart.x = new long[lng-1];
            for (int i = 1; i < lng; i++){
                chart.x[i-1] = xArr.getLong(i);
            }

            JSONArray yArr = colomns.getJSONArray(1);
            lng = colomns.length();
            chart.y = new int[lng-1][yArr.length()-1];
            int yLng;
            for (int i = 1; i < lng; i++){
                yArr = colomns.getJSONArray(i);
                yLng = yArr.length();
                for (int j = 1; j < yLng; j++){
                    chart.y[i-1][j-1] = yArr.getInt(j);
                }
            }

            JSONObject types = object.getJSONObject("types");
            chart.yTypes = new int[types.length()-1];
            for (int i = 0; i < types.length()-1; i++){
                chart.yTypes[i] = types.getString("y"+i).equals("line") ? 1 : 0;
            }

            JSONObject names = object.getJSONObject("names");
            chart.yNames = new String[names.length()];
            for (int i = 0; i < names.length(); i++){
                chart.yNames[i] = names.getString("y"+i);
            }

            JSONObject colors = object.getJSONObject("colors");
            chart.yColors = new int[colors.length()];
            for (int i = 0; i < colors.length(); i++){
                chart.yColors[i] = Color.parseColor(colors.getString("y"+i));
            }

            chart.countMaxAndMinY();
            chart.initVisibleArray();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return chart;
    }
}
