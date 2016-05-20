package picklenostra.user_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import picklenostra.user_app.helper.UserSessionManager;
import picklenostra.user_app.helper.VolleyController;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton facebookLoginButton;

    private String name, email, birthday, token, facebookId;
    private String urlCheckComplete = "";
    private final String PIC_URL_CONSTANTS = "/picture?type=large&redirect=false&width=200&height=200";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
//        printHashKey();
        urlCheckComplete = getResources().getString(R.string.API_URL) + "/login/isComplete";

        //Initialize all vars
        progressBar = (ProgressBar) findViewById(R.id.login_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF80CBC4, android.graphics.PorterDuff.Mode.SRC_ATOP);
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);

        //Facebook Login
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile","email"));

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();
                progressBar.setVisibility(View.VISIBLE);
                facebookLoginButton.setVisibility(View.GONE);
                GraphRequest request = GraphRequest.newMeRequest(accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    facebookId = object.optString("id").toString();
                                    name = object.optString("name").toString();
                                    email = object.optString("email").toString();
                                    birthday = object.optString("birthday").toString();
                                    String picture = "http://graph.facebook.com/" + facebookId + PIC_URL_CONSTANTS;
                                    volleyRequestFBPhoto(picture);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Tidak terhubung ke internet", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void volleyRequestCheckComplete(final String email, final String picture, final String token){
        StringRequest checkComplete =  new StringRequest(Request.Method.POST, urlCheckComplete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getBoolean("data")) {
                        SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("email", email);
                        editor.commit();

                        Intent in = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(in);
                        finish();
                    } else {
                        // to do: pindah ke complete form
                        SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("nama", name);
                        editor.putString("email", email);
                        editor.commit();

                        Intent in = new Intent(LoginActivity.this, CompleteFormActivity.class);
                        in.putExtra("facebookPhoto", picture);
                        in.putExtra("facebookToken", token);

                        startActivity(in);
                        finish();
                    }
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Crashlytics.logException(error.getCause());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };
        VolleyController.getInstance().addToRequestQueue(checkComplete);
    }

    private void printHashKey() {
        String keyhash = "default";
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "picklenostra.user_app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyhash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("KeyHash:", keyhash);
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        Log.e("keyhash: ", keyhash);
    }

    private void volleyRequestFBPhoto(final String picUrl){
        StringRequest request =  new StringRequest(Request.Method.GET, picUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String theRealUrl = "";
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONObject datas = responseObject.getJSONObject("data");
                    theRealUrl = datas.getString("url");
//                    Log.e("real url", theRealUrl);
                    token = AccessToken.getCurrentAccessToken().getToken();
                    volleyRequestCheckComplete(email, theRealUrl, token);
                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

        };
        VolleyController.getInstance().addToRequestQueue(request);
    }
}
