package hertz.hertz.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import hertz.hertz.activities.CMapsAssignedDriverActivity;
import hertz.hertz.R;

public class CAssignedDriver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_driver);


        String driverName, carType;
        driverName = this.getIntent().getStringExtra("driver");
        carType = this.getIntent().getStringExtra("carType");

        TextView tvName = (TextView)findViewById(R.id.tv_name);
        tvName.setText(driverName+" is on his way to pick you up at Pasong Tamo, Makati City");

        Button btn = (Button) findViewById(R.id.btn_next_driver);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CAssignedDriver.this, CMapsAssignedDriverActivity.class);

                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_assigned_driver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
