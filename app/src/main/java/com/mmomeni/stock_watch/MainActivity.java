package com.mmomeni.stock_watch;

import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;

import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.resources.TextAppearance;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener { //in order to make methods listeners we need
    //to import these View.OnClickListener, View.OnLongClickListener

    private final List<Stock> stockList = new ArrayList<>();
    private final List<Stock> rawStockList = new ArrayList<>();//we should have this final in order to make changes to noteList in the other parts of code, otherwise
    //adapter always draws the old list and not the new one
    private RecyclerView recyclerView;
    //private TextView desc;
    private final String TITLEBAR = "Stocks";
    private SwipeRefreshLayout swiper;
    private StockAdapter sAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadFile();
        setContentView(R.layout.activity_main); //this line also is an inflator

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        StockLoaderRunnable stockLoaderRunnable = new StockLoaderRunnable(this);
        new Thread(stockLoaderRunnable).start();
        //Collections.sort(rawStockList, rawSto);
        updateRecycler();

        updateTitleNoteCount();



    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            //statusText.setText(R.string.connected);
            return true;
        } else {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }
    }




    private void updateTitleNoteCount() {
        setTitle(TITLEBAR + " (" + stockList.size() + ")");
    }

    public void updateData1(ArrayList<Stock> cList) {
        rawStockList.clear();
        rawStockList.addAll(cList);
        //Collections.sort(rawStockList);
        updateRecycler();
        //mAdapter.notifyDataSetChanged();
    }

    public void updateData2(Stock stock) {
        boolean check = false;
        if (!stockList.isEmpty()) {
            for (int i = 0; i < stockList.size(); i++) {
                Stock s = stockList.get(i);
                if (s.getSymbol().equals(stock.getSymbol())) {
                    //Toast.makeText(this, "Already exists in the list!", Toast.LENGTH_SHORT).show();
                    dialog4(s.getSymbol());
                    check = true;
                    break;
                }
            }
        }
        if (check == false){
            stockList.add(0, stock);
            updateRecycler();
        }
        //Collections.sort(stockList);

        //mAdapter.notifyDataSetChanged();
    }

    public void downloadFailed() {
        rawStockList.clear();
        Toast.makeText(this, "Download failed!!", Toast.LENGTH_SHORT).show();
        //mAdapter.notifyDataSetChanged();
    }

    private void doRefresh() {
       // Collections.shuffle(stockList);
        //sAdapter.notifyDataSetChanged();
        if(doNetCheck() == false){
            dialog3(null);
            swiper.setRefreshing(false);
        }

        else{
            StockLoaderRunnable stockLoaderRunnable = new StockLoaderRunnable(this);
            new Thread(stockLoaderRunnable).start();
            List<Stock> oldList = new ArrayList<>();
            oldList.addAll(stockList);
            stockList.clear();
            DataUpdaterRunnable dataUpdaterRunnable = new DataUpdaterRunnable(this, oldList);
            new Thread(dataUpdaterRunnable).start();

            updateRecycler();
            swiper.setRefreshing(false);

        }
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu){ //this is the only code we have for menues
        //the menu we pass here is the actual menu we have made in layout
        //inflating means to build live objects
        getMenuInflater().inflate(R.menu.first_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_add:
                if(doNetCheck() == false){
                    dialog3(null);
                }
                else {
                    dialog1(null);
                }
                 //Toast.makeText(this, "hahaha", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void dialog1(View v) {
        // Single input value dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this); //these 3 lines are for building a textview
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        builder.setView(et);

        //builder.setIcon(R.drawable.icon1);

        builder.setPositiveButton("FIND", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //tv1.setText(et.getText());
                dialog2(et.getText().toString());
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
                //tv1.setText(getString(R.string.no_way));
            }
        });

        builder.setMessage("Please enter a symbol/name:");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialog2(String v) {
        // List selection dialog
        int counter1 = 0;
        for (int i = 0; i < rawStockList.size(); i++) {
            Stock s = rawStockList.get(i);
            if (s.getSymbol().contains(v) || s.getCompany().contains(v)) {
                counter1++;
            }
        }
        //ake an array of strings
        int counter2 = 0;
        int singleResultPos = 0;
        final CharSequence[] sArray = new CharSequence[counter1];
        for (int j = 0; j < rawStockList.size(); j++) {
            Stock b = rawStockList.get(j);
            if (b.getSymbol().contains(v) || b.getCompany().contains(v)) {
                sArray[counter2] = b.getSymbol() + " - " + b.getCompany();
                counter2++;
                singleResultPos = j;
            }
        }


        if (counter2 > 1) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");
        //builder.setIcon(R.drawable.icon2);

        // Set the builder to display the string array as a selectable
        // list, and add the "onClick" for when a selection is made
        builder.setItems(sArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { //which here is the position of the array list
                //.setText(sArray[which]);
                String[] a = sArray[which].toString().split(" - ");
                String b = a[0];
                int position = 0;
                for (int j = 0; j < rawStockList.size(); j++) {
                    Stock s = rawStockList.get(j);
                    if (s.getSymbol().equals(b)) {
                        position = j;
                        break;
                    }
                }
                getResult(position);


            }
        });

        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
                //tv2.setText(getString(R.string.nevermind_selected));
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }
        else if (counter2 == 1){
            getResult(singleResultPos);
        }
        else{
            dialog5(v);
        }

    }

    public void dialog3(View v) {
        // Simple dialog - no buttons.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //builder.setIcon(R.drawable.icon1);

        builder.setMessage("Stocks cannot be updated without a Network connection.");
        builder.setTitle("No Network Connection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialog4(String s) {
        // Simple dialog - no buttons.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //builder.setIcon(R.drawable.icon1);

        builder.setMessage("Stock symbol " + s + " is already displayed");
        builder.setTitle("Duplicate Stock");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialog5(String s) {
        // Simple dialog - no buttons.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //builder.setIcon(R.drawable.icon1);

        builder.setMessage("No data found for that symb/name");
        builder.setTitle("Symbol Not Found: " + s);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void getResult(int p){
        Stock chosen = rawStockList.get(p);
        FinancialLoaderRunnable stockLoaderRunnable = new FinancialLoaderRunnable(this, chosen.getSymbol());
        new Thread(stockLoaderRunnable).start();
    }


    public void updateRecycler(){
        Collections.sort(stockList);
        recyclerView = findViewById(R.id.recycler);
        StockAdapter vh = new StockAdapter(stockList, this);
        recyclerView.setAdapter(vh);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateTitleNoteCount();
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock m = stockList.get(pos);
        String symbol = m.getSymbol();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://www.marketwatch.com/investing/stock/" + symbol));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Data");
        builder.setMessage("Do you want to delete this data?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int pos = recyclerView.getChildLayoutPosition(v);
                stockList.remove(pos);
                updateRecycler();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        //Toast.makeText(v.getContext(), "LONG " + m.toString(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    protected void onPause() {
        saveFile();
        super.onPause();
    }

    @Override
    protected void onResume() {

        updateRecycler();

        super.onResume();
    }

    public void saveFile() {
        // Log.d(TAG, "saveFile: ");

        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for (Stock n : stockList) {
                writer.beginObject();
                writer.name("symbol").value(n.getSymbol());
                writer.name("company").value(n.getCompany());
                writer.name("price").value(n.getPrice());
                writer.name("pChange").value(n.getPChange());
                writer.name("cPercentage").value(n.getCPercentage());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            //Log.d(TAG, "writeJSONData: " + e.getMessage());
        }


    }



    private void loadFile(){
        try {
            FileInputStream fis = getApplicationContext().openFileInput(getString(R.string.file_name));

            byte[] data = new byte[fis.available()];
            int loaded = fis.read(data);
            fis.close();
            String json = new String(data);
            //Create JSON array form string file content
            JSONArray noteArr = new JSONArray(json);
            for (int i = 0; i < noteArr.length(); i++){
                JSONObject nObj = noteArr.getJSONObject(i);

                // Access note data fields
                String symbol = nObj.getString("symbol");
                String company = nObj.getString("company");
                Double price = nObj.getDouble("price");
                Double pChange = nObj.getDouble("pChange");
                Double cPercentage = nObj.getDouble("cPercentage");

                //create Note and add to ArrayList
                Stock n = new Stock(symbol, company, price, pChange, cPercentage);
                stockList.add(n);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}