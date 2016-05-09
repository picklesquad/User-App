package picklenostra.user_app;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import picklenostra.user_app.helper.UserSessionManager;

import static picklenostra.user_app.R.layout.activity_splash_screen;

public class SplashScreen extends AppCompatActivity {

    private final int DELAY_SPLASHSCREEN = 3000;
    UserSessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_splash_screen);

        session = new UserSessionManager(getApplicationContext());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                if (session.checkLogin()) {
                    intent = new Intent(SplashScreen.this, DashboardActivity.class);
                } else {
                    intent = new Intent(SplashScreen.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
                finish();
            }
        }, DELAY_SPLASHSCREEN);
    }
}
