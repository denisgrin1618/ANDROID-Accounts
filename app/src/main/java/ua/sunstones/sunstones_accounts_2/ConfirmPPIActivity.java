package ua.sunstones.sunstones_accounts_2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfirmPPIActivity extends AppCompatActivity {

    TableLayout tl;
    TableRow tr;
    String account_number;
    public List<PPI> list_ppi;
    public List<String> list_ppi_delete;
    public List<String> list_ppi_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_ppi);

        list_ppi = new ArrayList<>();
        tl = (TableLayout) findViewById(R.id.table_history);


        Intent intent = getIntent();
        account_number = intent.getStringExtra("account_number");


        new Query1C(this).execute(Query1C.TASK_GET_NOT_CONFIRMED_PPI);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setTitle("НЕПОДТВЕРЖДЕННЫЕ РАСХОДЫ");
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

    public void onClickButtonClose(View view) {
        finish();
    }

    public void onClickButtonCreatePPI(View view){

        Intent intent = new Intent(this, PPIActivity.class);
        intent.putExtra("account_number", this.account_number);
//        intent.putExtra("account_name", this.account_name);

        startActivity(intent);

    }


    public void button_confirm_ppi_and_exit(View view) {



        ///////////////////////
        list_ppi_number = new ArrayList<>();

        for(int i = 1, j = tl.getChildCount(); i < j; i++) {
            TableRow  child_row = (TableRow )tl.getChildAt(i);

            TextView num = (TextView)child_row.getChildAt(7);
            CheckBox ch = (CheckBox)child_row.getChildAt(1);
            if(ch instanceof CheckBox && ch.isChecked()){
                list_ppi_number.add(num.getText().toString());
            }

        }





        ///////////////////////
        list_ppi_delete = new ArrayList<>();

        for(int i = 1, j = tl.getChildCount(); i < j; i++) {
            TableRow  child_row = (TableRow )tl.getChildAt(i);

            TextView num = (TextView)child_row.getChildAt(7);
            CheckBox ch = (CheckBox)child_row.getChildAt(2);
            if(ch instanceof CheckBox && ch.isChecked()){
                list_ppi_delete.add(num.getText().toString());
            }

        }


//        CallBack_confirm_ppi callback = new CallBack_confirm_ppi();
//        callback.act = this;
//        Query1C.getInstance().confirm_ppi(list_ppi_number, list_ppi_delete, callback);

        new Query1C(this).execute(Query1C.TASK_POST_CONFIRM_PPI);

    }

    public void end_confirm(Boolean rezalt, String error){

        if(rezalt){

            final Intent intent = new Intent(this, OneAccountActivity.class);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Подтверждение прошло успешно")
                    .setMessage(error)
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    startActivity(intent);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();


        }

        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ошибка")
                    .setMessage(error)
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void update(){
        addHeaders();
        addData();
    }

    /** This function add the headers to the table **/
    public void addHeaders(){

        tl = (TableLayout) findViewById(R.id.table_history);
        /** Create a TableRow dynamically **/
        tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        //1.
        TextView companyTV = new TextView(this);
        companyTV.setText("#");
        companyTV.setTextColor(Color.GRAY);
        companyTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        companyTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        companyTV.setPadding(5, 5, 5, 0);
        tr.addView(companyTV);  // Adding textView to tablerow.

        //5.
        TextView valueQ = new TextView(this);
        valueQ.setText("Подт.");
        valueQ.setTextColor(Color.GRAY);
        valueQ.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        valueQ.setPadding(5, 5, 5, 0);
        valueQ.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tr.addView(valueQ); // Adding textView to tablerow.

        //5.
        valueQ = new TextView(this);
        valueQ.setText("Удал.");
        valueQ.setTextColor(Color.GRAY);
        valueQ.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        valueQ.setPadding(5, 5, 5, 0);
        valueQ.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tr.addView(valueQ); // Adding textView to tablerow.




        //3.
        TextView valueTV = new TextView(this);
        valueTV.setText("Дата");
        valueTV.setTextColor(Color.GRAY);
        valueTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        valueTV.setPadding(5, 5, 5, 0);
        valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tr.addView(valueTV); // Adding textView to tablerow.


        //4.
        valueQ = new TextView(this);
        valueQ.setText("Юань");
        valueQ.setTextColor(Color.GRAY);
        valueQ.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        valueQ.setPadding(5, 5, 5, 0);
        valueQ.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tr.addView(valueQ); // Adding textView to tablerow.

        //4.
        valueQ = new TextView(this);
        valueQ.setText("Курс");
        valueQ.setTextColor(Color.GRAY);
        valueQ.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        valueQ.setPadding(5, 5, 5, 0);
        valueQ.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tr.addView(valueQ); // Adding textView to tablerow.

        //4.
        valueQ = new TextView(this);
        valueQ.setText("Грн");
        valueQ.setTextColor(Color.GRAY);
        valueQ.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        valueQ.setPadding(5, 5, 5, 0);
        valueQ.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tr.addView(valueQ); // Adding textView to tablerow.




        //2.
        TextView Q = new TextView(this);
//        Q.setVisibility(View.INVISIBLE);
        Q.setText("Номер");
        Q.setTextColor(Color.GRAY);
        Q.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        Q.setPadding(5, 5, 5, 0);
        Q.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tr.addView(Q); // Adding textView to tablerow.






        // Add the TableRow to the TableLayout
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        // we are adding two textviews for the divider because we have two columns
        tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

    }

    /** This function add the data to the table **/
    public void addData(){

        Display display = getWindowManager().getDefaultDisplay();
        Point sizeWindow = new Point();
        display.getSize(sizeWindow);



        Map<String, List<PPI>> hashMap = new HashMap<>();
        for (int i = 0; i < list_ppi.size(); i++) {

            String key = list_ppi.get(i).account_number;

            List<PPI> list = hashMap.get(key);
            if(list == null){
                list = new ArrayList<>();
            }
            list.add(list_ppi.get(i));
            hashMap.put(key, list);

        }


        Set<Map.Entry<String, List<PPI>>> set = hashMap.entrySet();


        for (Map.Entry<String, List<PPI>> me : set) {
            String account_number = me.getKey() ;
            String account_name = "" ;


            List<PPI> list_ppi_2 = me.getValue();

            if(list_ppi_2.size() > 0){
                account_name = list_ppi_2.get(0).account_name;
            }
            TableRow.LayoutParams row_params = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT);
            row_params.setMargins(0,15,0,0);
            row_params.span = 8;


            /** Create a TableRow dynamically **/
            tr = new TableRow(this);
            tr.setLayoutParams(row_params);
            tr.setPadding(0, 10, 0, 0);


            String NAME_ACCOUNT = account_number;
            if(!account_name.isEmpty()){
                NAME_ACCOUNT = account_name + "(" + account_number + ")";
            }

            //1.
            TextView valueTV = new TextView(this);
            valueTV.setText(NAME_ACCOUNT);
            valueTV.setTextColor(Color.BLUE);
            valueTV.setLayoutParams(new TableRow.LayoutParams(0,TableRow.LayoutParams.WRAP_CONTENT));
            valueTV.setPadding(5, 5, 20, 5);
            valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

            TableRow.LayoutParams param =new TableRow.LayoutParams();
            param.height = TableRow.LayoutParams.WRAP_CONTENT;
            param.width = 0;
//            param.setGravity(Gravity.CENTER);
            param.span = 8;
//            param.rowSpec = GridLayout.spec(r);

            valueTV.setLayoutParams (param);

            tr.addView(valueTV); // Adding textView to tablerow.

            tl.addView(tr, row_params);


            for (int i = 0; i <list_ppi_2.size(); i++) {


                row_params = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT);
                row_params.setMargins(0,2,0,0);

                /** Create a TableRow dynamically **/
                tr = new TableRow(this);
                tr.setLayoutParams(row_params);
                tr.setPadding(0, 10, 0, 0);



                //1.
                valueTV = new TextView(this);
                valueTV.setText(Integer.toString(i));
//            valueTV.setTextColor(Color.GREEN);
                valueTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
                valueTV.setPadding(5, 5, 20, 5);
                valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                tr.addView(valueTV); // Adding textView to tablerow.


                //5.
                CheckBox check = new CheckBox(this);
                //check.setChecked(list_ppi_2.get(i).);
                //check.setTextColor(Color.RED);
                check.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));
                check.setPadding(5, 5, 5, 5);
                check.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                check.setMaxWidth(sizeWindow.x / 2);
                tr.addView(check); // Adding textView to tablerow.

                //5.
                check = new CheckBox(this);
                //check.setChecked(list_ppi_2.get(i).);
                //check.setTextColor(Color.RED);
                check.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));
                check.setPadding(5, 5, 5, 5);
                check.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                check.setMaxWidth(sizeWindow.x / 2);
                tr.addView(check); // Adding textView to tablerow.




                //3.
                valueTV = new TextView(this);
                valueTV.setText(list_ppi_2.get(i).date_time);
//            valueTV.setTextColor(Color.GREEN);
                valueTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));
                valueTV.setPadding(5, 5, 20, 5);
                valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                valueTV.setMaxWidth(sizeWindow.x / 2);
                tr.addView(valueTV); // Adding textView to tablerow.



                //4.
                valueTV = new TextView(this);
                valueTV.setText(Double.toString(list_ppi_2.get(i).sum_cny));
                valueTV.setTextColor(Color.RED);
                valueTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));
                valueTV.setPadding(5, 5, 5, 5);
                valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                valueTV.setMaxWidth(sizeWindow.x / 2);
                tr.addView(valueTV); // Adding textView to tablerow.

                //4.
                valueTV = new TextView(this);
                valueTV.setText(Double.toString(list_ppi_2.get(i).sum_kurs));
                valueTV.setTextColor(Color.RED);
                valueTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));
                valueTV.setPadding(5, 5, 5, 5);
                valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                valueTV.setMaxWidth(sizeWindow.x / 2);
                tr.addView(valueTV); // Adding textView to tablerow.

                //4.
                valueTV = new TextView(this);
                valueTV.setText(Double.toString(list_ppi_2.get(i).sum_uan));
                valueTV.setTextColor(Color.RED);
                valueTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));
                valueTV.setPadding(5, 5, 5, 5);
                valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                valueTV.setMaxWidth(sizeWindow.x / 2);
                tr.addView(valueTV); // Adding textView to tablerow.


                //2.
                TextView Q = new TextView(this);
//            Q.setVisibility(View.INVISIBLE);
                Q.setText(list_ppi_2.get(i).number);
                Q.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
                Q.setPadding(5, 5, 20, 5);
                Q.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                tr.addView(Q); // Adding textView to tablerow.



                tl.addView(tr, row_params);




            }
        }







    }

}
