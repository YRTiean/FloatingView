package com.ydev.floatingview;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnFloatingButton;
    private static  final int ID_DRAW_OVER_OTHER_APPS_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFloatingButton = (Button) findViewById(R.id.btn_floatingbtn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:"+getPackageName()));
            startActivity(intent);
        }else{
            floatTheViewOnTheScreen();
        }
    }

    private void floatTheViewOnTheScreen() {

        btnFloatingButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_floatingbtn){
            startService(new Intent(MainActivity.this, FloatingViewService.class));
            //finish();
        }
    }
}
