package hertz.hertz.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import hertz.hertz.R;
import hertz.hertz.model.Driver;
import hertz.hertz.views.CAssignedDriver;

public class CChooseDriverActivity extends ActionBarActivity implements View.OnClickListener{


    ListView lvMain;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_driver);
        lvMain = (ListView) findViewById(R.id.lv_driver_list);
        DriverListAdapter adapter = new DriverListAdapter();
        lvMain.setAdapter(adapter);
        inflater = this.getLayoutInflater();
        lvMain.invalidate();
    }

    @Override
    public void onClick(View v) {


    }

    public class DriverListAdapter extends BaseAdapter{


        ArrayList<Driver> drivers = new ArrayList<Driver>();


        public DriverListAdapter(){
            drivers.add(new Driver("Steve","Jobs","0999999999","Nissan Sentra"));
            drivers.add(new Driver("Bill","Gates","0911111111", "Toyota Vios"));
            drivers.add(new Driver("Ada","Lovelace","09991234819", "Toyota Innova"));
            drivers.add(new Driver("Mark","Zuckerberg","09129209314", "Fortuner"));
            drivers.add(new Driver("Dennis","Ritchie","0987394029", "Nissan Sentra"));
            drivers.add(new Driver("Massimo","Banzi","09090909090", "Toyota Vios"));
        }

        @Override
        public int getCount() {
            return drivers.size();
        }

        @Override
        public Object getItem(int position) {
            return drivers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = inflater.inflate(R.layout.activity_choose_driver_item, null);

            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            TextView tvNumber = (TextView) view.findViewById(R.id.tv_number);
            tvName.setText(drivers.get(position).getfName() + " " + drivers.get(position).getlName());
            tvNumber.setText(drivers.get(position).getCarType());

            Button btnHire = (Button) view.findViewById(R.id.btn_hire);
            btnHire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(CChooseDriverActivity.this, CAssignedDriver.class);

                    SharedPreferences.Editor editor = getSharedPreferences("placeids", MODE_PRIVATE).edit();
                    editor.putString("driver", drivers.get(position).getfName());
                    editor.putString("carType", drivers.get(position).getCarType());
                    editor.commit();
                    startActivity(intent);

                }
            });
            return view;
        }
    }


}
