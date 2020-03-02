package ua.sunstones.sunstones_accounts_2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AccountHistoryActivity extends AppCompatActivity {

    TableLayout tl;
    TableRow tr;
    public String account_number;
    public List<Account> list_accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_history);


        list_accounts = new ArrayList<>();
        tl = (TableLayout) findViewById(R.id.table_history);


        Intent intent = getIntent();
        account_number = intent.getStringExtra("account_number");

//        /new Query1C(this).execute(Query1C.TASK_GET_ACCOUNT_HISTORY);


//        CallBack_account_history callback = new CallBack_account_history();
//        callback.act = this;
//        Query1C.getInstance().get_account_history(account_number, callback);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setTitle("ИСТОРИЯ");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Boolean rezalt = MainMenu.onOptionsItemSelected(item, this);
        if(rezalt)
            return true;
        else
            return super.onOptionsItemSelected(item);

    }

    public void addData(){

//        Display display = getWindowManager().getDefaultDisplay();
//        Point sizeWindow = new Point();
//        display.getSize(sizeWindow);



        TableRow.LayoutParams l_params = new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        l_params.setMargins(1,0,0,0);

        TableRow.LayoutParams row_params = new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        row_params.setMargins(1,0,0,0);




        EditText quantity_first = null;

        Double balance_uan = 0.0;
        Double balance_cny = 0.0;

        for (int i = 0; i < list_accounts.size(); i++) {


            /** Create a TableRow dynamically **/
            tr = new TableRow(this);
            tr.setLayoutParams(row_params);

            tr.setPadding(0, 0, 5, 5);
            tr.setBackgroundColor(Color.parseColor("#000000"));



            //1.
            TextView valueTV = new TextView(this);
            valueTV.setText(Integer.toString(i));
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 5, 5, 5);
            valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            valueTV.setBackgroundColor(Color.WHITE);
            tr.addView(valueTV); // Adding textView to tablerow.



            //2.
            valueTV = new TextView(this);
            valueTV.setText(list_accounts.get(i).date_time);
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 5, 5, 5);
            valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            valueTV.setBackgroundColor(Color.WHITE);
//            valueTV.setMaxWidth(sizeWindow.x / 2);
            tr.addView(valueTV); // Adding textView to tablerow.


            //3.
            valueTV = new TextView(this);
            if(list_accounts.get(i).kurs  != 0.0) {
                valueTV.setText(Double.toString(list_accounts.get(i).kurs));
            }
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 5, 5, 5);
            valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//            valueTV.setMaxWidth(sizeWindow.x / 2);
            valueTV.setBackgroundColor(Color.WHITE);
            tr.addView(valueTV); // Adding textView to tablerow.


            //4.
            valueTV = new TextView(this);
            if(list_accounts.get(i).sum_uan  > 0.0) {
                valueTV.setText(Double.toString(list_accounts.get(i).sum_uan));
            }
            valueTV.setTextColor(Color.GREEN);
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 5, 5, 5);
            valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            valueTV.setBackgroundColor(Color.WHITE);
//            valueTV.setMaxWidth(sizeWindow.x / 2);
            tr.addView(valueTV); // Adding textView to tablerow.


            //5.
            valueTV = new TextView(this);
            if(list_accounts.get(i).sum_uan  < 0.0){
                valueTV.setText(Double.toString(list_accounts.get(i).sum_minus ));
            }

            valueTV.setTextColor(Color.RED);
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 5, 5, 5);
            valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            valueTV.setBackgroundColor(Color.WHITE);
//            valueTV.setMaxWidth(sizeWindow.x / 2);
            tr.addView(valueTV); // Adding textView to tablerow.


//            balance_uan += list_accounts.get(i).sum_uan;
//
//
//            //6.
//            valueTV = new TextView(this);
//            valueTV.setText(String.format("%.2f", balance_uan));
////            valueTV.setTextColor(Color.RED);
//            valueTV.setLayoutParams(l_params);
//            valueTV.setPadding(5, 5, 5, 5);
//            valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//            valueTV.setBackgroundColor(Color.WHITE);
////            valueTV.setMaxWidth(sizeWindow.x / 2);
//            tr.addView(valueTV); // Adding textView to tablerow.







            //4.
            valueTV = new TextView(this);
            if(list_accounts.get(i).sum_cny  > 0.0) {
                valueTV.setText(Double.toString(list_accounts.get(i).sum_cny));
            }
            valueTV.setTextColor(Color.GREEN);
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 5, 5, 5);
            valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            valueTV.setBackgroundColor(Color.WHITE);
//            valueTV.setMaxWidth(sizeWindow.x / 2);
            tr.addView(valueTV); // Adding textView to tablerow.


            //5.
            valueTV = new TextView(this);
            if(list_accounts.get(i).sum_cny  < 0.0){
                valueTV.setText(Double.toString(list_accounts.get(i).sum_cny ));
            }

            valueTV.setTextColor(Color.RED);
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 5, 5, 5);
            valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            valueTV.setBackgroundColor(Color.WHITE);
//            valueTV.setMaxWidth(sizeWindow.x / 2);
            tr.addView(valueTV); // Adding textView to tablerow.


//            balance_cny += list_accounts.get(i).sum_cny;
//
//            //6.
//            valueTV = new TextView(this);
////            valueTV.setText(Double.toString(balance_cny));
//
//            valueTV.setLayoutParams(l_params);
//            valueTV.setPadding(5, 5, 5, 5);
//            valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//            valueTV.setBackgroundColor(Color.WHITE);
////            valueTV.setMaxWidth(sizeWindow.x / 2);
//            tr.addView(valueTV); // Adding textView to tablerow.

            tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));




        }



    }

    public void update(){
        addData();
    }



}
