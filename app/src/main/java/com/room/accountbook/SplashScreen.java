package com.room.accountbook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;

public class SplashScreen extends AppCompatActivity {

    private View mainView;
    private Handler handler = new Handler();
    Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        window = getWindow();
        mainView = findViewById(R.id.mainView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void navigateToHomeScreen() {
        startActivity(new Intent(this, MainMenu.class));
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                window.setStatusBarColor(getResources().getColor(R.color.colorAccent));

                int cx = (mainView.getLeft() + mainView.getRight()) / 2;
                int cy = (mainView.getTop() + mainView.getBottom()) / 2;

                float finalRadius = (float) Math.hypot(mainView.getWidth(), mainView.getHeight());

                Animator anim = ViewAnimationUtils.createCircularReveal(mainView, cx, cy, 0, finalRadius);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
                        }
                        mainView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        navigateToHomeScreen();
                        finish();
                    }
                });

                mainView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                anim.setDuration(500);
                anim.start();
            } else {
                navigateToHomeScreen();
                finish();
            }


        }
    };
}
