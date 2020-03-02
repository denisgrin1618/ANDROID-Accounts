package ua.sunstones.sunstones_accounts_2;


import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

public class MainMenu {


    public static Boolean onOptionsItemSelected(MenuItem item, Activity act){

        switch (item.getItemId()) {

            case R.id.menu_main_authorization:
                Intent intent = new Intent(act, AuthorizationActivity.class);
                intent.putExtra("open_authorization", true);
                act.startActivity(intent);
                return true;

            case R.id.menu_main_one_account:
                act.startActivity(new Intent(act, OneAccountActivity.class));
                return true;

            case R.id.menu_main_ppi:
                act.startActivity(new Intent(act, ConfirmPPIActivity.class));
                return true;


        }

        return false;
    }
}
