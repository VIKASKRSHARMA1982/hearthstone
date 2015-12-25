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
    public void addCar() {
        startActivity(new Intent(this,CarManagementActivity.class));
        animateToLeft(this);
    }
}
