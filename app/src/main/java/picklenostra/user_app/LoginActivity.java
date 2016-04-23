package picklenostra.user_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    private TextView tvDetailText;
    private UserSessionManager session;

    private String name, email, birthday, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        //Initialize all vars
        session = new UserSessionManager(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (LoginButton)findViewById(R.id.facebook_login_button);
        tvDetailText = (TextView)findViewById(R.id.text_details_facebook);

        //Facebook Login
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile","email"));
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        name = object.optString("name").toString();
                        email = object.optString("email").toString();
                        birthday = object.optString("birthday").toString();
                        token = AccessToken.getCurrentAccessToken().toString();

                        SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("name", name);
                        editor.putString("email", email);
                        editor.putString("birthday", birthday);
                        editor.commit();
                        session.createUserLogin(email, token);

                        /**
                         * Sementara lempar ke MainActivity dulu
                         */
                        Intent in = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(in);
                        finish();
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                tvDetailText.setText("Login Attempt Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                tvDetailText.setText(error.getMessage().toString());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
