package hertz.hertz.activities;

import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import hertz.hertz.R;

/**
 * Created by rsbulanon on 12/6/15.
 */
public class SuperAdminActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnCar)
    public void manageCards() {
        startActivity(new Intent(this, CarManagementActivity.class));
        animateToLeft(this);
    }

    @OnClick(R.id.btnDriver)
    public void manageDrivers() {
        startActivity(new Intent(this,DriverManagementActivity.class));
        animateToLeft(this);
    }
}
