package ua.sunstones.sunstones_accounts_2;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AuthorizationActivity extends AppCompatActivity {

    public TextView text_view_error;
    public EditText edit_login;
    public EditText edit_password;
    public CheckBox mCheckBoxRequestPassword;
    public Boolean mAutorizationSucces;

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private int REQUEST_CODE_PERMISSIONS                = 121;

    Query1C query;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);


        if(!allPermissionsGranted()){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        mAutorizationSucces = false;
        text_view_error = (TextView) findViewById(R.id.autorization_text_view_error);
        edit_login      = (EditText) findViewById(R.id.autorization_edit_login);
        edit_password   = (EditText) findViewById(R.id.autorization_edit_password);

        query = new Query1C(this);
        db = DataBase.getInstance(this);
        String login = db.getLogin();
        edit_login.setText(login);

        mCheckBoxRequestPassword = findViewById(R.id.checkBoxRequestPassword);
        String RequestPassword = db.getSessionVariable("RequestPassword");
        mCheckBoxRequestPassword.setChecked( RequestPassword.equals("true"));


         if(!mCheckBoxRequestPassword.isChecked()){
             String password = db.getLogin();
             edit_password.setText(password);
        }else{
             edit_password.setText("");
         }


        Intent intent = getIntent();
        Boolean open_authorization = intent.getBooleanExtra("open_authorization", false);
        if(!open_authorization && HaveTheRight() ){

            if(mCheckBoxRequestPassword.isChecked() && mAutorizationSucces){
                mAutorizationSucces = false;
            }
            if(mCheckBoxRequestPassword.isChecked() && !mAutorizationSucces){
                return;
            }

            Intent intent2 = new Intent(this, OneAccountActivity.class);
            startActivity(intent2);


        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setTitle("АВТОРИЗАЦИЯ");
        return true;
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
////        Boolean rezalt = MainMenu.onOptionsItemSelected(item, this);
////        if(rezalt)
////            return true;
////        else
////            return super.onOptionsItemSelected(item);
//
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(!allPermissionsGranted()){
//                showError("Permission not granted");
            }
        }
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }


    public void onClickButtonlogin(View view){
        new Query1C(this).execute(Query1C.TASK_AUTORIZATION);
    }


    public void openOffers(){
        Intent intent = new Intent(this, OneAccountActivity.class);
        startActivity(intent);
        finish();
    }

    public void showError(String erorr){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error!")
                .setMessage(erorr)
                .setIcon(R.drawable.delete)
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

    public boolean HaveTheRight(){

        boolean is = true;

        String authorization_status = db.getAuthorizationStatus();
        if(! new String(authorization_status).equals("success")){
            Toast toast = Toast.makeText(getApplicationContext(), authorization_status, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            is = false ;
        }

        return  is;
    }

}
