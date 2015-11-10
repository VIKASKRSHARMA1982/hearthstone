package hertz.hertz.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import hertz.hertz.activities.CLoginActivity;
import hertz.hertz.activities.CReservationActivity;
import hertz.hertz.R;
import hertz.hertz.model.User;
import hertz.hertz.services.UserLocalStore;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Button bLogout, bBook;
    EditText etName;

    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        bBook = (Button) findViewById(R.id.bBook);
        bLogout = (Button) findViewById(R.id.bLogout);

        bLogout.setOnClickListener(this);
        bBook.setOnClickListener(this);

        userLocalStore = new UserLocalStore(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (authenticate() == true){
            displayUserDetails();
        }
        else{
            startActivity(new Intent(MainActivity.this, CLoginActivity.class) );
        }
    }

    private boolean authenticate(){
        return userLocalStore.getUserLoggedIn();
    }


    private void displayUserDetails(){
        User user = userLocalStore.getLoggedInUser();

        etName.setText(user.getName());

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bLogout:
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);

                startActivity(new Intent(this, CLoginActivity.class));

                break;


            case R.id.bBook:
                userLocalStore.getLoggedInUser();
                userLocalStore.setUserLoggedIn(true);


                startActivity(new Intent(this, CReservationActivity.class));


                //startActivity(new Intent(this, CMapsActivity.class));
              break;
        }
    }
    //    public void onClick1 (View v) {
     //   startActivity(new Intent(this, MapsActivity.class));

 //   }
}

