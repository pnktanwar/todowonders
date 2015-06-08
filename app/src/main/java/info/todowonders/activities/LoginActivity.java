package info.todowonders.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import info.todowonders.R;
import info.todowonders.utils.MyProfile;

/**
 * Created by ptanwar on 08/06/15.
 */
public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getName();

    LoginButton loginButton;
    AccessTokenTracker accessTokenTracker;
    CallbackManager callbackManager;
    AccessToken accessToken;

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void gotoMainActivity() {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        try {
                            Log.e(TAG, response.getRawResponse());
                            JSONObject responseObj = response.getJSONObject();
                            MyProfile.setName(responseObj.getString("name"));
                            MyProfile.setProfileImg("http://graph.facebook.com/" + responseObj.getString("id") +"/picture?type=small");
                        } catch(Exception ex) {
                            Log.e(TAG, "error while trying to fetch user profile :: " + ex);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // App code
                Log.e(TAG, "Access token generated.");
                accessToken = currentAccessToken;
                gotoMainActivity();
            }
        };
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if ( accessToken == null ) {
            Log.e(TAG, "Setting login activity layout.");
            setContentView(R.layout.activity_login);
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions("user_friends");

            // Callback registration
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    Log.e(TAG, "Login success");
                    gotoMainActivity();
                }

                @Override
                public void onCancel() {
                    // App code
                    Log.e(TAG, "Login canceled");
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Log.e(TAG, "Login error :: " + exception + exception.toString());
                }
            });
        } else {
            gotoMainActivity();
        }
    }
}
