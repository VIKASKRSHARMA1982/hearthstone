package hertz.hertz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.RadioGroup;

import hertz.hertz.R;

public class CReservationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creservation);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.pick_car);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.rbPickUp:
                        startActivity(new Intent(CReservationActivity.this, AvailableDriversActivity.class));
                        animateToLeft(CReservationActivity.this);
                        break;
                    case R.id.rbCarHire:
                        animateToLeft(CReservationActivity.this);
                        break;
                }
            }
        });
    }
}
