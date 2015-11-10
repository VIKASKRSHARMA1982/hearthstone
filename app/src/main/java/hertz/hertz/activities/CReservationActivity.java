package hertz.hertz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.RadioGroup;

import hertz.hertz.R;

public class CReservationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creservation);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.pick_car);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

               // Intent i = new Intent(CReservationActivity.this, CChooseRideActivity.class);
                switch(checkedId){
                    case R.id.radio_pickup:

                        /*i.putExtra("reserve","pickup");
                        startActivity(i);
                        finish();*/
                        startActivity(new Intent(CReservationActivity.this, CChooseDriverActivity.class));
                        break;
                    case R.id.radio_carhire:
                        /*i.putExtra("reserve","rent");
                        startActivity(i);
                        finish();*/
                        startActivity(new Intent(CReservationActivity.this, CChooseRideActivity.class));
                        break;
                }


            }
        });
    }



}
