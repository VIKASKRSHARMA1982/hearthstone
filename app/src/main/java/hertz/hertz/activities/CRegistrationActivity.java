package hertz.hertz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import hertz.hertz.R;
import hertz.hertz.model.User;
import hertz.hertz.services.ServerRequests;


public class CRegistrationActivity extends ActionBarActivity implements View.OnClickListener {

    Button bRegister;
    EditText etName, etAge, etUsername, etPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.bRegister:
                String name = etName.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String strAge = etAge.getText().toString();
                int age = (strAge.trim().isEmpty())?0:Integer.parseInt(strAge);

                User user = new User(name, age, username, password);


                registerUser(user);

                break;
        }
    }


    private void  registerUser(User user) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.storeUserDataInBackground(user, new  CLoginActivity.GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                startActivity(new Intent(CRegistrationActivity.this, CLoginActivity.class));
                finish();
            }
        });
    }

}
