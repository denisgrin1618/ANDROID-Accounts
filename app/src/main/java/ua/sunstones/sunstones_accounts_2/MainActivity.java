package ua.sunstones.sunstones_accounts_2;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    DataBase data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        data = DataBase.getInstance(this);

        Intent intent = new Intent(this, OneAccountActivity.class);
        String status = data.getAuthorizationStatus();
        String RequestPassword = data.getSessionVariable("RequestPassword");

        if(RequestPassword.equals("true") || !status.equals("success")){
            intent = new Intent(this, AuthorizationActivity.class);
        }
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

}
