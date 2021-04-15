package com.codyvbs.phfbpwnedchecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class MainActivity extends AppCompatActivity {
    MaterialSpinner spinner;
    Button search,reset;
    EditText searchText;
    TextView result;

    //sqlite database name
    final String dbName = "ph_fb_leak.db";

    boolean isPwned;
    ACProgressFlower dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        search = findViewById(R.id.searchBtn);
        searchText = findViewById(R.id.tvSearch);
        result = findViewById(R.id.result);
        reset = findViewById(R.id.resetBtn);

        //load the database
        try {
            loadDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
        }


        spinner.setItems("Search By", "Search By Email", "Search By Facebook ID", "Search By Full Name","Search By Phone Number");


        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if(item.equals("Search By Email")){
                    searchText.setHint("Enter Your Email");
                    search.setEnabled(true);
                    searchText.setEnabled(true);
                    searchText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    result.setVisibility(View.GONE);
                }else if(item.equals("Search By Facebook ID")){
                    searchText.setVisibility(View.VISIBLE);
                    searchText.setHint("Enter Your Facebook ID");
                    search.setEnabled(true);
                    searchText.setEnabled(true);
                    searchText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    result.setVisibility(View.GONE);
                }else if(item.equals("Search By Full Name")){
                    searchText.setHint("Enter Your Full Name");
                    search.setEnabled(true);
                    searchText.setEnabled(true);
                    searchText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    result.setVisibility(View.GONE);
                }else if(item.equals("Search By Phone Number")){
                    searchText.setHint("Enter Your Phone Number");
                    search.setEnabled(true);
                    searchText.setEnabled(true);
                    searchText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    result.setVisibility(View.GONE);
                }else{
                    searchText.setEnabled(false);
                    search.setEnabled(false);
                    result.setVisibility(View.GONE);
                }
            }
        });

        //hide the views
        searchText.setEnabled(false);
        result.setVisibility(View.GONE);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              new myTask().execute();

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
                spinner.setSelectedIndex(0);
                result.setVisibility(View.GONE);
            }
        });

    }

    private void loadDatabase() throws IOException {
        File checkDB = null;
        checkDB = new File(getFilesDir() + "/" + dbName);

        if(!checkDB.exists()){
            InputStream inputStream = getApplicationContext().getAssets().open(dbName);
            OutputStream outputStream = new FileOutputStream(getFilesDir() + "/" + dbName);

            byte[] buffer = new byte[1024];
            int length;

            while((length = inputStream.read(buffer)) > 0){
                outputStream.write(buffer,0,length);
            }

            //close the streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }
    }

    private void checkMe(String searchVal) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        DriverManager.registerDriver((Driver) Class.forName("org.sqldroid.SQLDroidDriver").newInstance());
        String dbUrl ="jdbc:sqldroid:" + getFilesDir() + "/" + dbName;
        Connection connection = DriverManager.getConnection(dbUrl);

        int counter = 0;

        Statement stmt = connection.createStatement();
        String query = "SELECT count(*) from ph_leak where field1 LIKE '%" + searchVal + "%'";
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()){
            counter = Integer.parseInt(rs.getString("count(*)"));
        }


        if(counter > 0){
            isPwned = true;
        }else{
            isPwned  = false;
        }




    }



    @Override
    public void onBackPressed() {
        String titleText = "Confirm Exit";
        String message = "Are you sure you want to exit ?";

        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.WHITE);

        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);

        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        SpannableStringBuilder ssBuilder2 = new SpannableStringBuilder(message);


        ssBuilder2.setSpan(
                foregroundColorSpan,
                0,
                message.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setTitle(ssBuilder);
        builder1.setMessage(ssBuilder2);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
        alert11.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        Button buttonbackground = alert11.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonbackground.setTextColor(Color.GRAY);

        Button buttonbackground1 = alert11.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonbackground1.setTextColor(Color.RED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case R.id.about:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("About")
                        .setMessage("This app contains leaked data exposed on public database. " +
                                "The app contains exclusive PH exposed personal data that was posted in a hackers forum. This app will help you check whether your data was part of the" +
                                " Facebook 2019 data breach. The developer of this app will not take responsibility if anything you do using this application. Use at your own risk!")
                        .setPositiveButton("Ok",null)
                        .show();
                break;
        }

        return super.onOptionsItemSelected(item);



    }

    private class myTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            dialog = new ACProgressFlower.Builder(MainActivity.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text("Processing...")
                    .fadeColor(Color.DKGRAY).build();
            dialog.show();


            super.onPreExecute();




        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                checkMe(searchText.getText().toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return "Execute";
        }

        @Override
        protected void onPostExecute(String s) {

            if(dialog.isShowing()){
                dialog.dismiss();

                if(isPwned){
                    result.setVisibility(View.VISIBLE);
                    result.setText("You have been pwned!");
                    result.setBackgroundColor(Color.parseColor("#1b1717"));
                    result.setTextColor(Color.parseColor("#ce1212"));

                }else{
                    result.setVisibility(View.VISIBLE);
                    result.setText("Good news - you are not pwned");
                    result.setBackgroundColor(Color.GREEN);
                    result.setTextColor(Color.WHITE);

                }
            }

            super.onPostExecute(s);
        }
    }
}