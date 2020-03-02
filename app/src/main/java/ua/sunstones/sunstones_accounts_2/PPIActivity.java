package ua.sunstones.sunstones_accounts_2;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PPIActivity extends AppCompatActivity {

    String currentPhotoPath;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_CAPTURE_IMAGE      = 100;
    private static final int REQUEST_GALLERY            = 22;
    private int REQUEST_CODE_PERMISSIONS                = 121;

    private TextView edit_text_account;
    private TextView edit_text_name;
    private TextView edit_text_sum;
    private TextView edit_text_sum_val;
    private TextView edit_text_kurs;
    private TextView edit_text_coment;
    private ImageView ivPhoto;
    private EditText current_account;
    private Account account;
    private String account_number="";
    private String account_name="";


    public PPI ppi;
    DataBase data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppi);

        current_account = findViewById(R.id.current_account);
        edit_text_sum_val = findViewById(R.id.edit_text_sum_val);
        edit_text_kurs = findViewById(R.id.edit_text_kurs);
        edit_text_sum = findViewById(R.id.edit_text_sum);
        edit_text_coment = findViewById(R.id.edit_text_coment);

        if(!allPermissionsGranted()){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        account_number   = getIntent().getExtras().getString("account_number");
        account_name     = getIntent().getExtras().getString("account_name");

        //

        current_account.setText(account_number);

        data = DataBase.getInstance(this);
        account = data.get_account(account_number);




        /*current_account = (Spinner) findViewById(R.id.current_account);
        List<Account> list_accounts = data.get_accounts();
        Account[] array_accounts = new Account[list_accounts.size()];
        list_accounts.toArray(array_accounts);

        ArrayAdapter<Account> adapter_account = new SpinAccountAdapter(this, android.R.layout.simple_spinner_dropdown_item, array_accounts);
        adapter_account.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        current_account.setAdapter(adapter_account);
        current_account.setSelection(0);*/


        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        //createDirectory();


        edit_text_kurs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Calculate_text_sum();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edit_text_sum_val.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Calculate_text_sum();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setTitle("РАСХОД");
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

    void copyFiles(Uri sourceLocation, File targetLocation){

        try{

//            File sourceLocation = new File (source);
//
//            // make sure your target location folder exists!
//            File targetLocation = new File (target);

//            if(sourceLocation.exists()){

//                InputStream in = new FileInputStream(sourceLocation);
            InputStream in = getContentResolver().openInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

            Log.v("test", "Copy file successful.");

//            }else{
//                Log.v("test", "Copy file failed. Source file missing.");
//            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        ///////////////////////////////////////////////////////////////////////////
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {

            File imgFile = new  File(currentPhotoPath);
            if(imgFile.exists()) {
                ivPhoto.setImageURI(Uri.fromFile(imgFile));
            }
        }


        ///////////////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////////////////////////////
        try {
            // When an Image is picked
            if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && null != data) {

                if(data.getData()!=null){

                    File targetLocation     = createImageFile();
                    copyFiles(data.getData(), targetLocation);

                    currentPhotoPath  = targetLocation.getAbsolutePath();



                }
                else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();

                            File targetLocation     = createImageFile();
                            copyFiles(uri, targetLocation);

                            currentPhotoPath = targetLocation.getAbsolutePath();


                        }

                    }
                }


                File imgFile = new  File(currentPhotoPath);
                if(imgFile.exists()) {
                    ivPhoto.setImageURI(Uri.fromFile(imgFile));
                }

//                grid_photo_view.setAdapter(new GridPhotoViewAdapter(this, offer.Photos));

            } else {
//                showError("Не выбрано ни одно фото");
            }
        } catch (Exception e) {
            showError("Что то пошло не так");
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(!allPermissionsGranted()){
                showError("Нет прав на работу с камерой");
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

    private void Calculate_text_sum(){

        double kur      = 0;
        double sum_val  = 0;
        double sum      = 0;



        if (edit_text_kurs.getText().toString().equals("")) {
            kur = 1;
        } else {
            kur = Double.valueOf(edit_text_kurs.getText().toString());
        }

        if (edit_text_sum_val.getText().toString().equals("")) {
            sum_val = 0;
        } else {
            sum_val = Double.valueOf(edit_text_sum_val.getText().toString());
        }

        kur = (kur == 0) ? 1 : kur;
        sum = sum_val * kur;

        edit_text_sum.setText(String.valueOf(sum));



    }

    public void show_alert(String title, String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }



    public void showError(String erorr){


//        final Dialog dialog_erorr = new Dialog(this);
//        dialog_erorr.setContentView(R.layout.dialog_erorr);
//
//
//        TextView text_view_erorr = (TextView) dialog_erorr.findViewById(R.id.text_view_erorr);
//        text_view_erorr.setText(erorr);
//
//        Button button_ok = (Button) dialog_erorr.findViewById(R.id.button_ok);
//        button_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog_erorr.dismiss();
//            }
//        });
//
//        dialog_erorr.setCancelable(false);
//        dialog_erorr.setTitle("ERORR");
//        dialog_erorr.show();



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ошибка!")
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


    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SunStonesAccount_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;

    }


    public void onClickButtonCamera(View view) {


        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(cameraIntent, REQUEST_CAPTURE_IMAGE);

            File pictureFile = null;
            try {
                pictureFile = createImageFile();
            } catch (IOException ex) {
                showError("Нельзя использовать камеру. Попробуйте еще раз.");
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "ua.sunstones.sunstones_accounts_2.fileprovider", pictureFile);

//                Log.v("Photo", "photoURI: " + photoURI.toString());

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    public void onClickButtonGalary(View view){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_GALLERY);

    }

    public void onClickButtonSavePPI(View view){

        //account = (Account)current_account.getSelectedItem();

        if(account == null){
            show_alert("ОШИБКА", "Заполните счет");
            return;
        }

        if(edit_text_sum.getText().toString().isEmpty()){
            show_alert("ОШИБКА", "Заполните сумму в грн");
            return;
        }

        if(edit_text_sum_val.getText().toString().isEmpty()){
            show_alert("ОШИБКА", "Заполните сумму в юанях");
            return;
        }

        if(edit_text_kurs.getText().toString().isEmpty()){
            show_alert("ОШИБКА", "Заполните курс");
            return;
        }

        ppi = new PPI();
        ppi.account_name    = account.account_number;
        ppi.account_number  = account.account_number;
        ppi.coment          = edit_text_coment.getText().toString();
        ppi.sum_uan         = Double.parseDouble(edit_text_sum.getText().toString());
        ppi.sum_cny         = Double.parseDouble(edit_text_sum_val.getText().toString());
        ppi.sum_kurs        = Double.parseDouble(edit_text_kurs.getText().toString());
        ppi.photo           = get_img_Base64(currentPhotoPath);


        new Query1C(this).execute(Query1C.TASK_POST_SAVE_PPI);

    }

    public void callback_save_ppi(boolean succes, String error) {

        if(succes){
//            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
//            builder.setTitle("Создание расхода!")
//                    .setMessage("Документ успешно создан!")
//                    .setCancelable(false)
//                    .setNegativeButton("ОК",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//
//                                }
//                            });
//            android.app.AlertDialog alert = builder.create();
//            alert.show();

            Intent intent = new Intent(this, OneAccountActivity.class);
            intent.putExtra("account_number", this.account_number);
            intent.putExtra("message", "Расход успешно создан!");
            startActivity(intent);
            finish();
        }


        else{
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Создание расхода!")
                    .setMessage("ОШИБКА:" + error)
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void onClickButtonClose(View view){
        finish();
    }



    public String get_img_Base64(String  filePath){

//        ivPhoto.buildDrawingCache();
//        Bitmap bitmap = ivPhoto.getDrawingCache();
//        String img_str = "";
//
//        if(bitmap != null){
//            ByteArrayOutputStream stream=new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
//            byte[] image=stream.toByteArray();
//            img_str = Base64.encodeToString(image, 0);
//        }
//
//        return img_str;

        if(filePath == null || filePath.isEmpty()){
            return "";
        }

        String img_str = "";
        File imgFile = new File(filePath);
        if (imgFile.exists() && imgFile.length() > 0) {
            Bitmap bm = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bOut);
            img_str =  android.util.Base64.encodeToString(bOut.toByteArray(),  android.util.Base64.DEFAULT);
        }

        return img_str;
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
            label.setText(values[position].account_number);

            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            label.setText(values[position].account_number);

            return label;
        }
    }

}
