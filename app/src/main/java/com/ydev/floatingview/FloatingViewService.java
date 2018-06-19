package com.ydev.floatingview;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class FloatingViewService extends Service implements View.OnClickListener, View.OnTouchListener {

    View floatingView;
    WindowManager windowManager;
    View collapasedView;
    View expandedView;
    View rootView;
    WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();

        floatingView = LayoutInflater.from(FloatingViewService.this)
                .inflate(R.layout.floating_view_layout, null);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                /*NLY FOR vER < OREO*/WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 200;
        params.y = 200;

        windowManager.addView(floatingView, params);

        collapasedView = floatingView.findViewById(R.id.ll_collapsed_view);

        ImageView close = (ImageView) collapasedView.findViewById(R.id.iv_collapsed_close);
        close.setOnClickListener(this);

        ImageView open = (ImageView) collapasedView.findViewById(R.id.iv_collapsed_img);
        open.setOnClickListener(this);

        expandedView = floatingView.findViewById(R.id.ll_expanded_view);

        Button btnClose = (Button) expandedView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(this);

        Button btnOpen = (Button) expandedView.findViewById(R.id.btn_open_app);
        btnOpen.setOnClickListener(this);

        rootView = floatingView.findViewById(R.id.rl_root_view);
        rootView.setOnTouchListener(this);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.iv_collapsed_close){
            stopSelf();
            Toast.makeText(this, "Closed", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.iv_collapsed_img){
            collapasedView.setVisibility(View.GONE);
            expandedView.setVisibility(View.VISIBLE);
        } else if (id == R.id.btn_close){
            collapasedView.setVisibility(View.VISIBLE);
            expandedView.setVisibility(View.GONE);
        } else if (id == R.id.btn_open_app){
            Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            stopSelf();
        }
    }

    int startXPos, startYPos;
    float startTouchX, startTouchY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                startXPos = params.x;
                startYPos = params.y;
                startTouchX = event.getRawX();
                startTouchY = event.getRawY();
                return true;

            case MotionEvent.ACTION_UP:
                int startToEndXPos = (int) (event.getRawX() - startTouchX);
                int startToEndYPos = (int) (event.getRawY() - startTouchY);

                if (startToEndXPos < 5 && startToEndYPos < 5) {

                    if (isWidgetCollapsed()) {
                        collapasedView.setVisibility(View.GONE);
                        expandedView.setVisibility(View.VISIBLE);
                    }
                }
                return true;


            case MotionEvent.ACTION_MOVE:
                params.x = startXPos + (int) (event.getRawX() - startTouchX);
                params.y = startYPos + (int) (event.getRawY() - startTouchY);
                windowManager.updateViewLayout(floatingView, params);
                return true;

        }

        return false;
    }

    private boolean isWidgetCollapsed() {

        return collapasedView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (floatingView != null){
            windowManager.removeView(floatingView);
        }
    }
}
