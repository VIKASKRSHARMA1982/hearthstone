package hertz.hertz.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hertz.hertz.R;
import hertz.hertz.helpers.AppConstants;
import hertz.hertz.loginregister.MainActivity;
import hertz.hertz.services.ServerRequests;
import hertz.hertz.model.User;
import hertz.hertz.services.UserLocalStore;

public class CLoginActivity extends BaseActivity {

    @Bind(R.id.etEmail) EditText etEmail;
    @Bind(R.id.etPassword) EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_2);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnLogin)
    public void login() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        /** validations */
        if (!isNetworkAvailable()) {
            showSweetDialog(AppConstants.ERR_CONNECTION, "error", false, null, null);
        } else if (email.isEmpty()) {
            setError(etEmail, AppConstants.WARN_FIELD_REQUIRED);
        } else if (password.isEmpty()) {
            setError(etPassword, AppConstants.WARN_FIELD_REQUIRED);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setError(etEmail, AppConstants.WARN_INVALID_EMAIL_FORMAT);
        } else {
            /** authenticate user's credentials */
            showProgressDialog(AppConstants.LOAD_LOGIN);
            ParseUser.logInInBackground(email, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    dismissProgressDialog();
                    if (e == null) {
                        showSweetDialog("Welcome " + user.getString("firstName") + " " +
                        user.getString("lastName"),"success",false,null,null);
                    } else {
                        showSweetDialog(e.getMessage(),"error",false,null,null);
                    }
                }
            });
        }
    }

    public interface GetUserCallback {
        public abstract void done(User returnedUser);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        animateToRight(this);
    }
}
