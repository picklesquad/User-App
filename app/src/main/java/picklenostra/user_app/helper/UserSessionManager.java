package picklenostra.user_app.helper;

/**
 * Created by Edwin on 4/21/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import picklenostra.user_app.LoginActivity;

/**
 * Created by Edwin on 3/10/2016.
 */
public class UserSessionManager {
    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context mContext;

    private static final String KEY_SHARED_PREF = "PICKLEUSER";
    private static final int PRIVATE_MODE = 0;
    private static final String IS_USER_LOGIN = "ISUSERLOGIN";

    public UserSessionManager(Context context){
        this.mContext = context;
        pref = mContext.getSharedPreferences(KEY_SHARED_PREF, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLogin(String email, String password){
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString("EMAIL", email);
        editor.putString("PASSWORD", password);
        editor.commit();
    }


    public boolean checkLogin(){
        if(this.isUserLogin()){
            return true;
        }
        else{
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return false;
        }
    }


    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public boolean isUserLogin(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();

        user.put("EMAIL", pref.getString("EMAIL", null));
        user.put("PASSWORD", pref.getString("PASSWORD", null));
        return user;
    }

}
