package ua.sunstones.sunstones_accounts_2;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Query1C extends AsyncTask<String,Void,Void> {

    public static final String TASK_AUTORIZATION            = "TASK_AUTORIZATION";
    public static final String TASK_SYNCHRONIZATION         = "TASK_SYNCHRONIZATION";
    public static final String TASK_GET_ACCOUNT_TURNOVER    = "TASK_GET_ACCOUNT_TURNOVER";
    public static final String TASK_GET_ACCOUNT_HISTORY     = "TASK_GET_ACCOUNT_HISTORY";
    public static final String TASK_POST_SAVE_PPI           = "TASK_POST_SAVE_PPI";
    public static final String TASK_GET_NOT_CONFIRMED_PPI   = "TASK_GET_NOT_CONFIRMED_PPI";
    public static final String TASK_POST_CONFIRM_PPI        = "TASK_POST_CONFIRM_PPI";


    private List<Account> list_accounts = new ArrayList<Account>();
    private List<Account> list_history_account = new ArrayList<Account>();
    private List<PPI> list_not_confirmed_ppi = new ArrayList<PPI>();

    private Activity act;
    private final String NameBase1C = "7777777";
    private final String NameURI = "https://localhost/";
    private String answer = "";
    private String current_task;
    private int synchronization_progress_bar_current_max;
    private int synchronization_progress_bar_current_progress;
    private int progress_bar_total_max;
    private int progress_bar_total_progress;
    private String synchronization_text_rezalt = "";

    private String synchronization_text_view_current;
    DataBase data;
    private String error = "";

    public Query1C(Activity act){
        this.act  = act;
        this.data = DataBase.getInstance(act);
    }

    @Override
    protected Void doInBackground(String... params) {

        switch(params[0]) {

            case TASK_AUTORIZATION:
                current_task = TASK_AUTORIZATION;
                Authorization();
                break;

            case TASK_SYNCHRONIZATION:
                current_task = TASK_SYNCHRONIZATION;
                Synchronization();
                break;

            case TASK_GET_ACCOUNT_TURNOVER:
                current_task = TASK_GET_ACCOUNT_TURNOVER;
                Get_account_turnover();

                break;

             case TASK_GET_ACCOUNT_HISTORY:
                current_task = TASK_GET_ACCOUNT_HISTORY;
                String account_number = ((OneAccountActivity)act).last_selected_account.account_number;
                Get_account_history(account_number);
                break;

            case TASK_POST_SAVE_PPI:
                current_task = TASK_POST_SAVE_PPI;

                PPI ppi = ((PPIActivity)act).ppi;
                Post_save_ppi(ppi);
                break;

            case TASK_POST_CONFIRM_PPI:
                current_task = TASK_POST_CONFIRM_PPI;

                Post_confirm_ppi(((ConfirmPPIActivity)act).list_ppi_number,  ((ConfirmPPIActivity)act).list_ppi_delete);

                break;

            case TASK_GET_NOT_CONFIRMED_PPI:
                current_task = TASK_GET_NOT_CONFIRMED_PPI;
                Get_not_confirmed_ppi();

                break;


            default:
                break;

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        switch(current_task) {

            case TASK_AUTORIZATION:
                if(answer.equals("Authorization success")){
                    ((AuthorizationActivity)act).openOffers();
                }else{
                    ((AuthorizationActivity)act).text_view_error.setText(answer);
                }


                break;


            case TASK_GET_ACCOUNT_TURNOVER:
                ((OneAccountActivity)act).update_spiner_account();
                ((OneAccountActivity)act).update();

                break;

            case TASK_GET_ACCOUNT_HISTORY:
                ((OneAccountActivity)act).list_account_history = list_history_account;
                ((OneAccountActivity)act).update_table_history();
                ((OneAccountActivity)act).gifImage_downloding.setVisibility(View.INVISIBLE);

                break;

            case TASK_POST_SAVE_PPI:

                    if(error.isEmpty()) {
                        ((PPIActivity)act).callback_save_ppi(true, "");
                    }else{
                        ((PPIActivity)act).callback_save_ppi(false, error);
                    }

                break;

            case TASK_POST_CONFIRM_PPI:

                if(error.isEmpty()){
                    ((ConfirmPPIActivity)act).end_confirm(true, "");
                }else{
                    ((ConfirmPPIActivity)act).end_confirm(false, error);
                }

                break;


            case TASK_GET_NOT_CONFIRMED_PPI:

                ((ConfirmPPIActivity)act).list_ppi = list_not_confirmed_ppi;
                ((ConfirmPPIActivity)act).update();

                break;



            default:
                break;

        }


    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

        switch(current_task) {

            case TASK_AUTORIZATION:
                ((AuthorizationActivity)act).text_view_error.setText(answer);
                break;




            default:
                break;

        }
    }


    private String GET(String urlSpec, String login, String password) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();


        String userCredentials = login + ":" + password;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
        connection.setRequestProperty ("Authorization", basicAuth);


        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);

            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return new String(out.toByteArray());
        }
        finally {
            connection.disconnect();
        }
    }

    private String POST(String urlSpec, String post_data, String login, String password) throws IOException {

        URL url;
        String response = "";
        try {
            url = new URL(urlSpec);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(15000);
//            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            String userCredentials = login + ":" + password;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
            conn.setRequestProperty ("Authorization", basicAuth);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(post_data);
            writer.flush();
            writer.close();
            os.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="error " + String.valueOf(responseCode) ;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }


    private void Authorization(){

        String AnswerJsonString = "";
//        DataBase data = new DataBase(act);

        try {

            String login    = ((AuthorizationActivity)act).edit_login.getText().toString();
            String password = ((AuthorizationActivity)act).edit_password.getText().toString();
            CheckBox mCheckBoxRequestPassword = ((AuthorizationActivity)act).mCheckBoxRequestPassword;
            data.setLogin(login);

            data.setPassword(password);

//            if(!mCheckBoxRequestPassword.isChecked()){
//                data.setPassword(password);
//            }else{
//                data.setPassword("");
//            }

            data.setSessionVariable("RequestPassword", mCheckBoxRequestPassword.isChecked() ? "true" : "false");




            String url = Uri.parse(NameURI+NameBase1C+"/hs/mobile/authorization")
                    .buildUpon()
//                    .appendQueryParameter("method", "flickr.photos.getRecent")
//                    .appendQueryParameter("api_key", API_KEY)
//                    .appendQueryParameter("format", "json")
//                    .appendQueryParameter("nojsoncallback", "1")
//                    .appendQueryParameter("extras", "url_s")
                    .build().toString();

            AnswerJsonString = GET(url, login, password);
            Log.i("TEST", "Received JSON: " + AnswerJsonString);

        } catch (IOException ioe) {
            Log.e("TEST", "Authorization Failed", ioe);
            answer = "Authorization Failed";
        }




        JSONObject obj = null;
        try {

            obj = new JSONObject(AnswerJsonString);

            String error            = obj.optString ( "error" );
//            JSONObject result       = obj.getJSONObject("result");
//            String name_user        = result.getString("name");

            if(error.isEmpty() || error == null){
                answer = "Authorization success";
                data.setAuthorizationStatus("success");
            } else{
                answer = "Authorization error. " + error;
                data.setAuthorizationStatus("error");
            }

        } catch (JSONException e) {
            answer = "Authorization error...";
            e.printStackTrace();
        }

    }

    private void Synchronization(){

        synchronization_text_rezalt         = "";
        synchronization_text_view_current   = "";
        progress_bar_total_max              = 6;
        progress_bar_total_progress         = 0;
        publishProgress();


       /* //1/
        uploadPhotos();
        progress_bar_total_progress = 1;
        publishProgress();

        //2.
        uploadProducts();
        progress_bar_total_progress = 2;
        publishProgress();


        //4.
        uploadOffers();
        progress_bar_total_progress = 4;
        publishProgress();

        //5.
        uploadAssembly();
        progress_bar_total_progress = 5;
        publishProgress();

        //6.
        downloadOrders();
        progress_bar_total_progress = 6;
*/

        synchronization_text_view_current = "sync done";
        synchronization_progress_bar_current_max = 1;
        synchronization_progress_bar_current_progress = 1;
        publishProgress();

    }

    private void Get_account_turnover(){

        String AnswerJsonString = "";

        try {

            String login    = data.getLogin();
            String password = data.getPassword();

            String url = Uri.parse(NameURI+NameBase1C+"/hs/mobile/account_turnover")
                    .buildUpon()
//                    .appendQueryParameter("method", "flickr.photos.getRecent")
//                    .appendQueryParameter("api_key", API_KEY)
//                    .appendQueryParameter("format", "json")
//                    .appendQueryParameter("nojsoncallback", "1")
//                    .appendQueryParameter("extras", "url_s")
                    .build().toString();

            AnswerJsonString = GET(url, login, password);
//            Log.i("TEST", "Received JSON: " + AnswerJsonString);

        } catch (IOException ioe) {
//            Log.e("TEST", "Authorization Failed", ioe);
            answer = "Authorization Failed";
        }



        JSONObject obj = null;
        try {
            obj = new JSONObject(AnswerJsonString);
            JSONArray arr = obj.getJSONArray("result");

            for (int i = 0; i < arr.length(); i++)
            {
                Account account = new Account();
                account.name             = arr.getJSONObject(i).getString("name");
                account.account_number   = arr.getJSONObject(i).getString("account_number");
                account.sum_turnover     = Double.parseDouble(arr.getJSONObject(i).getString("sum_turnover"));
                account.sum_plus         = Double.parseDouble(arr.getJSONObject(i).getString("sum_plus"));
                account.sum_minus        = Double.parseDouble(arr.getJSONObject(i).getString("sum_minus"));

                data.add_account(account);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Get_account_history(String p_account_number){

        String AnswerJsonString = "";

        try {

            String login    = data.getLogin();
            String password = data.getPassword();

            String url = Uri.parse(NameURI+NameBase1C+"/hs/mobile/account_history?account_number=" + p_account_number)
                    .buildUpon()
//                    .appendQueryParameter("method", "flickr.photos.getRecent")
//                    .appendQueryParameter("api_key", API_KEY)
                    .build().toString();

            AnswerJsonString = GET(url, login, password);
//            Log.i("TEST", "Received JSON: " + AnswerJsonString);

        } catch (IOException ioe) {
//            Log.e("TEST", "Authorization Failed", ioe);
            answer = "Authorization Failed";
        }




            JSONObject obj = null;
            try {

                obj = new JSONObject(AnswerJsonString);
                JSONObject result = obj.getJSONObject("result");
                String account_number   = result.getString("account_number");;
                JSONArray arr           = result.getJSONArray("history");

                list_history_account.clear();

                for (int i = 0; i < arr.length(); i++)
                {
                    Account account = new Account();
                    account.name             = account_number;
                    account.account_number   = account_number;
                    account.date_time        = arr.getJSONObject(i).getString("date");
                    account.sum_turnover     = Double.parseDouble(arr.getJSONObject(i).getString("sum_uan"));
                    account.sum_plus         = (account.sum_turnover > 0) ? account.sum_turnover : 0;
                    account.sum_minus        = (account.sum_turnover < 0) ? account.sum_turnover : 0;
                    account.kurs             = Double.parseDouble(arr.getJSONObject(i).getString("sum_kurs"));
                    account.sum_uan          = Double.parseDouble(arr.getJSONObject(i).getString("sum_uan"));
                    account.sum_kurs         = Double.parseDouble(arr.getJSONObject(i).getString("sum_kurs"));
                    account.sum_cny          = Double.parseDouble(arr.getJSONObject(i).getString("sum_cny"));


                    list_history_account.add(account);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

    private void Post_save_ppi(PPI ppi){

        error = "";
        String login            = data.getLogin();
        String password         = data.getPassword();

        String url = Uri.parse(NameURI+NameBase1C+"/hs/mobile/create_ppi")
                .buildUpon()
//                .appendQueryParameter("articul_barcode", photo.articul_barcode)
                .build().toString();


        String json = ppi.get_json().toString();
        try {
            answer = POST(url, json, login, password);
        } catch (IOException e) {

        }

        String error = "";

        JSONObject obj = null;
        try {
            obj = new JSONObject(answer);
            error = obj.getString("error");

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private Bitmap downloadPhoto(String photo_uid){

        String login     = data.getLogin();
        String password  = data.getPassword();

        String url = Uri.parse(NameURI+NameBase1C+"/hs/mobile/photo")
                .buildUpon()
                .appendQueryParameter("photo_uid", photo_uid)
                .build()
                .toString();

        try{
            answer = GET(url, login, password);
        } catch (IOException e) {
            return null;
        }



        try {

            JSONObject obj          = new JSONObject(answer);
            String error            = obj.optString ( "error" );
            JSONObject result       = obj.getJSONObject("result");

            if(error.isEmpty() || error == null){
                String encodedImage = result.optString("original");
                byte[] decodedString = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                return decodedByte;
            }

        } catch (JSONException e) {

        }

        return null;
    }

    private String readJSON(String body_json, String name_field) throws IOException {

        JSONObject obj = null;
        try {
            obj = new JSONObject(body_json);
//            JSONArray arr = obj.getJSONArray(name_field);
//            arr.getJSONObject(i).getString("name");

            return  obj.getString(name_field);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void Get_not_confirmed_ppi(){

        String AnswerJsonString = "";

        try {

            String login    = data.getLogin();
            String password = data.getPassword();

            String url = Uri.parse(NameURI+NameBase1C+"/hs/mobile/not_confirmed_ppi")
                    .buildUpon()
//                    .appendQueryParameter("method", "flickr.photos.getRecent")
//                    .appendQueryParameter("api_key", API_KEY)
                    .build().toString();

            AnswerJsonString = GET(url, login, password);
//            Log.i("TEST", "Received JSON: " + AnswerJsonString);

        } catch (IOException ioe) {
//            Log.e("TEST", "Authorization Failed", ioe);
            answer = "Authorization Failed";
        }


        JSONObject obj = null;
        try {

            obj = new JSONObject(AnswerJsonString);
            JSONObject result       = obj.getJSONObject("result");
            String account_number   = result.getString("account_number");;
            JSONArray arr           = result.getJSONArray("not_confirmed_ppi");

            list_not_confirmed_ppi.clear();
            for (int i = 0; i < arr.length(); i++)
            {
                PPI account = new PPI();
                account.account_name     = arr.getJSONObject(i).getString("account_name");
                account.account_number   = arr.getJSONObject(i).getString("account_number");
                account.date_time        = arr.getJSONObject(i).getString("date");
                account.sum_uan          = Double.parseDouble(arr.getJSONObject(i).getString("sum_uan"));
                account.sum_cny          = Double.parseDouble(arr.getJSONObject(i).getString("sum_cny"));
                account.sum_kurs         = Double.parseDouble(arr.getJSONObject(i).getString("sum_kurs"));
                account.number           = arr.getJSONObject(i).getString("number");

                list_not_confirmed_ppi.add(account);
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void Post_confirm_ppi(List<String> list_ppi_number, List<String> list_ppi_delete){

        error = "";
        String login            = data.getLogin();
        String password         = data.getPassword();

        String url = Uri.parse(NameURI+NameBase1C+"/hs/mobile/confirm_ppi")
                .buildUpon()
//                .appendQueryParameter("articul_barcode", photo.articul_barcode)
                .build().toString();


        JSONObject json_ob   = new JSONObject();

        try {
            JSONArray js_array_confirm = new JSONArray();
            for (String ppi_number : list_ppi_number) {
                js_array_confirm.put(ppi_number);
            }
            json_ob.put("confirm", js_array_confirm);


            JSONArray js_array_delete = new JSONArray();
            for (String ppi_number : list_ppi_delete) {
                js_array_delete.put(ppi_number);
            }
            json_ob.put("delete", js_array_delete);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String string_json = json_ob.toString();

        try {
            answer = POST(url, string_json, login, password);
        } catch (IOException e) {

        }

        String error = "";

        JSONObject obj = null;
        try {
            obj = new JSONObject(answer);
            error = obj.getString("error");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}

