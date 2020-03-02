package ua.sunstones.sunstones_accounts_2;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class PPI {

    public String account_number;
    public String account_name;
    public String coment;
    public String photo;
    public String date_time;
    public String number;

    public Double sum_cny;
    public Double sum_uan;
    public Double sum_kurs;

    public PPI(){}

    public JSONObject get_json(){

        try {
            JSONObject json = new JSONObject();
            json.put("account_number", account_number);
            json.put("account_name", account_name);
            json.put("sum_cny", sum_cny);
            json.put("sum_uan", sum_uan);
            json.put("sum_kurs", sum_kurs);
            json.put("coment", coment);
            json.put("photo", photo);

            return json;
        }

        catch(JSONException ex) {
            ex.printStackTrace();
        }

        return  new JSONObject();
    }
}
