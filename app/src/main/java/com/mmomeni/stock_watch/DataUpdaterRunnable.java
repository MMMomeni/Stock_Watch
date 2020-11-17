package com.mmomeni.stock_watch;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class DataUpdaterRunnable implements Runnable {

    private static final String TAG = "CountryLoaderRunnable";
    private MainActivity mainActivity;
    private List<Stock> oldList = new ArrayList<>();
    //private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";

    DataUpdaterRunnable(MainActivity mainActivity, List<Stock> s) {
        this.mainActivity = mainActivity;
        this.oldList.addAll(s);
    }

    @Override
    public void run() {

        for (int i = oldList.size() - 1; i > -1; i--){
            Stock a = oldList.get(i);
            String DATA_URL = "https://cloud.iexapis.com/stable/stock/" + a.getSymbol() + "/quote?token=pk_f2955502fbdd48718146ef719b5cd70e";
            Uri dataUri = Uri.parse(DATA_URL);
            String urlToUse = dataUri.toString();
            Log.d(TAG, "run: " + urlToUse);

            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(urlToUse);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                    handleResults(null);
                    return;
                }

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }

                Log.d(TAG, "run: " + sb.toString());

            } catch (Exception e) {
                Log.e(TAG, "run: ", e);
                handleResults(null);
                return;
            }

            handleResults(sb.toString());

        }

    }

    private void handleResults(String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.downloadFailed();
                }
            });
            return;
        }



        final Stock stock = parseJSON(s);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (stock != null)
                    mainActivity.updateData2(stock);
            }
        });
    }

    private Stock parseJSON(String s) {

        //ArrayList<Stock> stockList = new ArrayList<>();

        Stock rs = new Stock(null, null, 0,0,0);
        try {
            //JSONArray jObjMain = new JSONArray(s);
            JSONObject result = new JSONObject(s);

            String symbol = result.getString("symbol");
            String company = result.getString("companyName");
            Double price = result.getDouble("latestPrice");
            Double pChange = result.getDouble("change");
            Double cPercent = result.getDouble("changePercent");

            if (price.equals(null) && pChange.equals(null) && cPercent.equals(null)){
                rs = new Stock(symbol, company, 0,0,0);
            }
            else {
                rs = new Stock(symbol, company, price, pChange, cPercent);
            }



            return rs;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


}

