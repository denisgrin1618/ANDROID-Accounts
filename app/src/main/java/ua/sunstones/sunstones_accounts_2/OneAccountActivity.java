package ua.sunstones.sunstones_accounts_2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OneAccountActivity extends AppCompatActivity {

    public TextView text_view_account_plus;
    public TextView text_view_account_minus;
    public TextView text_view_account_balance;
    public Button button_ceate_ppi;
    public Spinner current_account;
    public GifImageView gifImage_downloding;
    public String account_name ="";
    public String account_number ="";
    public Account last_selected_account;
    DataBase data;

    TableLayout tl;
    public List<Account> list_account_history = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_account);

        tl = (TableLayout) findViewById(R.id.table_account_history);


        this.data = DataBase.getInstance(this);
        text_view_account_plus      = findViewById(R.id.text_view_account_plus);
        text_view_account_minus     = findViewById(R.id.text_view_account_minus);
        text_view_account_balance   = findViewById(R.id.text_view_account_balance);
        button_ceate_ppi            = findViewById(R.id.button_ceate_ppi);

        gifImage_downloding = (GifImageView) findViewById(R.id.gifImage_downloding);
        gifImage_downloding.setGifImageResource(R.drawable.downloding_5);



        get_accounts_from_1c();


        current_account = (Spinner) findViewById(R.id.current_account);
        update_spiner_account();



        Bundle intent_params = getIntent().getExtras();
        if(intent_params != null){
            account_number  = intent_params.getString("account_number", "");

            last_selected_account = data.get_account(account_number);
            if(last_selected_account != null){
                int position = ((SpinAccountAdapter)current_account.getAdapter()).get_position_by_namber_accaunt(account_number);
                current_account.setSelection(position);
            }


            String message = intent_params.getString("message", "");
            if(!message.isEmpty()){
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Создание расхода!")
                        .setMessage(message)
                        .setCancelable(false)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }

        }else{
            current_account.setSelection(0);
        }





        current_account.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                last_selected_account = (Account)current_account.getSelectedItem();
                update();
                get_account_history_from_1c();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



        update();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setTitle("СЧЕТА");
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

    public void update_spiner_account(){

        List<Account> list_accounts = data.get_accounts();
        Account[] array_accounts = new Account[list_accounts.size()];
        list_accounts.toArray(array_accounts);

        ArrayAdapter<Account> adapter_account = new SpinAccountAdapter(this, android.R.layout.simple_spinner_dropdown_item, array_accounts);
        adapter_account.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        current_account.setAdapter(adapter_account);

        if(last_selected_account != null){
            //int position = ((ArrayAdapter<Account>)current_account.getAdapter()).getPosition(last_selected_account);
            int position = ((SpinAccountAdapter)current_account.getAdapter()).get_position_by_namber_accaunt(last_selected_account.account_number);
            current_account.setSelection(position);
        }
    }

    public void update(){

//        Account account = data.get_account("4731185614516745");

        if(last_selected_account != null) {
            text_view_account_plus.setText(String.format("%.2f", last_selected_account.sum_plus) + "₴");
            text_view_account_minus.setText(String.format("%.2f", last_selected_account.sum_minus) + "₴");
            text_view_account_balance.setText(String.format("%.2f", last_selected_account.sum_plus - last_selected_account.sum_minus) + "₴");
            account_number = last_selected_account.account_number;
            account_name = last_selected_account.name;
        }


    }

    private void get_accounts_from_1c(){
        new Query1C(this).execute(Query1C.TASK_GET_ACCOUNT_TURNOVER);
    }

    private void get_account_history_from_1c(){
        gifImage_downloding.setVisibility(View.VISIBLE);
        new Query1C(this).execute(Query1C.TASK_GET_ACCOUNT_HISTORY);


    }

    public void update_table_history(){

//        Display display = getWindowManager().getDefaultDisplay();
//        Point sizeWindow = new Point();
//        display.getSize(sizeWindow);


        tl.removeAllViews();

        TableRow.LayoutParams l_params = new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        l_params.setMargins(1,0,0,0);

//        TableLayout.LayoutParams l_params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);






        for (int i = 0; i < list_account_history.size(); i++) {


            /** Create a TableRow dynamically **/


            TableRow tr = new TableRow(this);
//            tr.setLayoutParams(row_params);
            tr.setPadding(0, 10, 0, 0);
            tr.setBackgroundColor(Color.parseColor("#ffffff"));


            //1.
            TextView valueTV = new TextView(this);
            valueTV.setText(list_account_history.get(i).date_time);
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 0, 0, 0);
            valueTV.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
            valueTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
//            valueTV.setBackgroundColor(Color.WHITE);
            tr.addView(valueTV); // Adding textView to tablerow.



            //2.
            valueTV = new TextView(this);
            if(list_account_history.get(i).sum_uan  > 0.0) {
                valueTV.setText(Double.toString(list_account_history.get(i).sum_uan) + "₴");
            }
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 0, 0, 0);
            valueTV.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
            valueTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            valueTV.setTextColor(Color.GREEN);
            tr.addView(valueTV); // Adding textView to tablerow.


            //3.
            valueTV = new TextView(this);
            if(list_account_history.get(i).sum_uan  < 0.0) {
                valueTV.setText(Double.toString(list_account_history.get(i).sum_uan) + "₴");
            }
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 0, 0, 0);
            valueTV.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
            valueTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            valueTV.setTextColor(Color.RED);
            tr.addView(valueTV); // Adding textView to tablerow.


            TableLayout.LayoutParams row_params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            row_params.weight = 0;
            row_params.setMargins(0,2,0,0);
            tl.addView(tr, row_params);



            ////////////////////////////////////////////////////////////////
            //Second row


            tr = new TableRow(this);
//            tr.setLayoutParams(row_params_2);
            tr.setBackgroundColor(Color.parseColor("#ffffff"));


            //1.
            valueTV = new TextView(this);
            valueTV.setText("курс: " + list_account_history.get(i).kurs);
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 0, 0, 0);
            valueTV.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
            valueTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
//            valueTV.setBackgroundColor(Color.WHITE);
            tr.addView(valueTV); // Adding textView to tablerow.



            //2.
            valueTV = new TextView(this);
            if(list_account_history.get(i).sum_cny  > 0.0) {
                valueTV.setText(Double.toString(list_account_history.get(i).sum_cny) + "¥");
            }
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 0, 0, 0);
            valueTV.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
            valueTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            valueTV.setTextColor(Color.GREEN);
            tr.addView(valueTV); // Adding textView to tablerow.


            //3.
            valueTV = new TextView(this);
            if(list_account_history.get(i).sum_cny  < 0.0) {
                valueTV.setText(Double.toString(list_account_history.get(i).sum_cny) + "¥");
            }
            valueTV.setLayoutParams(l_params);
            valueTV.setPadding(5, 0, 0, 0);
            valueTV.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
            valueTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            valueTV.setTextColor(Color.RED);
            tr.addView(valueTV); // Adding textView to tablerow.


            TableLayout.LayoutParams row_params_2 = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            row_params_2.weight = 0;
            row_params_2.setMargins(0,0,0,0);
            tl.addView(tr, row_params_2);


        }



    }


    public void onClickButtonUpdate(View view) {

        gifImage_downloding.setVisibility(View.VISIBLE);
        last_selected_account = (Account)current_account.getSelectedItem();
        get_accounts_from_1c();
//        update_spiner_account();
        update();


        new Query1C(this).execute(Query1C.TASK_GET_ACCOUNT_HISTORY);



    }

    public void onClickButtonCreatePPI(View view){

        Intent intent = new Intent(this, PPIActivity.class);
        intent.putExtra("account_number", this.account_number);
        intent.putExtra("account_name", this.account_name);

        startActivity(intent);

    }

    public void onClickButtonConfirmPPI(View view){

        Intent intent = new Intent(this, PPIActivity.class);
        intent.putExtra("account_number", this.account_number);
        intent.putExtra("account_name", this.account_name);

        startActivity(intent);

    }

    public void onClickButtonOpenHistory(View view) {

//        Intent intent = new Intent(this, AccountHistoryActivity.class);
//        intent.putExtra("account_number", this.account_number);
//
//        startActivity(intent);

    }



    public class SpinAccountAdapter extends ArrayAdapter<Account>{

        private Context context;
        private Account[] values;

        public SpinAccountAdapter(Context context, int textViewResourceId, Account[] values) {
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public int getCount(){
            return values.length;
        }

        @Override
        public Account getItem(int position){
            return values[position];
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label = (TextView) super.getView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            label.setText(values[position].name + "(" + values[position].account_number + ")");

            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            label.setText(values[position].name + "(" + values[position].account_number + ")");

            return label;
        }


        public int get_position_by_namber_accaunt(String account_number){
            for (int i = 0; i < values.length; i++) {
                if(account_number.equals(values[i].account_number) ){
                    return i;
                }
            }

            return 0;
        }
    }


}




