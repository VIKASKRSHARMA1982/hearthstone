package hertz.hertz.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import hertz.hertz.R;
import hertz.hertz.model.Car;

public class CRideSedanFragment extends Fragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpCarDataSedan();
    }


    ArrayList<Car> sedans;
    int counter = 0;


    public void setUpCarDataSedan(){


        sedans = new ArrayList<Car>();

        String sedanDesc = getResources().getString(R.string.car_sedan);
        String sedanDescAuto = getResources().getString(R.string.car_sedan_auto);

        sedans.add(new Car("Nissan Sentra 1.3",1750,3400,550,R.drawable.car2, sedanDesc));
        sedans.add(new Car("Nissan Sentra 1.6 (Manual)",1950,3450,550,R.drawable.car2, sedanDesc));
        sedans.add(new Car("Nissan Sentra 1.6 (Automatic)", 1750, 3400, 550, R.drawable.car2, sedanDescAuto));

        CChooseRideActivity.currentCar = sedans.get(counter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_vehicle_sedan, null);

        ImageButton aLeft = (ImageButton)view.findViewById(R.id.btn_left);
        ImageButton aRight = (ImageButton)view.findViewById(R.id.btn_right);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View viewListener) {

                switch(viewListener.getId()){
                    case R.id.btn_left:
                        if(counter > 0)
                            counter--;
                        break;
                    case R.id.btn_right:
                        if(counter < sedans.size()-1)
                            counter++;
                        break;
                }

                CChooseRideActivity.currentCar = sedans.get(counter);
                refreshView(view, counter);
            }
        };

        aRight.setOnClickListener(listener);
        aLeft.setOnClickListener(listener);

        CChooseRideActivity.currentCar = sedans.get(counter);
        refreshView(view, counter);
        return view;
    }

    private void refreshView(View view, int counter){
        TextView tvCarName = (TextView) view.findViewById(R.id.tv_car_name);
        tvCarName.setText(CChooseRideActivity.currentCar.getName());
        TextView tvCarDesc = (TextView) view.findViewById(R.id.tv_desc);
        tvCarDesc.setText(CChooseRideActivity.currentCar.getDesc());

        ImageView img = (ImageView) view.findViewById(R.id.img_car);
        img.setImageResource(CChooseRideActivity.currentCar.getImage());

        final TextView tvPrice = (TextView) view.findViewById(R.id.hourprice);


        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String strPrice = "P";
                switch (position) {
                    case 0:
                        strPrice += CChooseRideActivity.currentCar.getPrice3Hours();
                        break;
                    case 1:
                        strPrice += CChooseRideActivity.currentCar.getPrice5Hours();
                        break;
                }
                tvPrice.setText(strPrice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvPrice.setText("P "+CChooseRideActivity.currentCar.getPrice3Hours());

    }
}
