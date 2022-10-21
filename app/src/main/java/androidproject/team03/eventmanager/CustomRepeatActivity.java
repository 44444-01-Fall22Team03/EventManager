package androidproject.team03.eventmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CustomRepeatActivity extends AppCompatActivity {

    private Button repeatCancelBtn, repeatDoneBtn;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_repeat);
        Intent intent1 = getIntent();

        repeatCancelBtn=findViewById(R.id.btnRepeatCancel);
        repeatDoneBtn=findViewById(R.id.btnRepeatDone);

        Spinner spinnerEvery=findViewById(R.id.spinnerEvery);
        Spinner spinnerRepeatEvery=findViewById(R.id.spinnerRepeatEvery);

        ArrayAdapter<CharSequence> seAdapter=ArrayAdapter.createFromResource(this, R.array.recTypes, android.R.layout.simple_spinner_item);

        seAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        spinnerEvery.setAdapter(seAdapter);

        spinnerEvery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String option = String.valueOf(spinnerEvery.getSelectedItem());

                if (option.contentEquals("Daily")) {
                    List<String> list = new ArrayList<>();
                    list.add("EveryDay");
                    ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(CustomRepeatActivity.this, android.R.layout.simple_spinner_item, list);
                    arrayAdapter1.notifyDataSetChanged();
                    spinnerRepeatEvery.setAdapter(arrayAdapter1);
                }

                if (option.contentEquals("Weekly")) {
                    List<String> list = new ArrayList<>();
                    list.add("EveryWeek");
                    ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(CustomRepeatActivity.this, android.R.layout.simple_spinner_item, list);
                    arrayAdapter1.notifyDataSetChanged();
                    spinnerRepeatEvery.setAdapter(arrayAdapter1);
                }
                if (option.contentEquals("Monthly")) {
                    List<String> list = new ArrayList<>();
                    list.add("EveryMonth");
                    list.add("Jan");list.add("Feb");list.add("Mar");
                    list.add("Apr");list.add("May");list.add("Jun");
                    list.add("Jul");list.add("Aug");list.add("Sep");
                    list.add("Oct");list.add("Nov");list.add("Dec");
                    ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(CustomRepeatActivity.this, android.R.layout.simple_spinner_item, list);
                    arrayAdapter1.notifyDataSetChanged();
                    spinnerRepeatEvery.setAdapter(arrayAdapter1);
                }
                if (option.contentEquals("Yearly")) {
                    List<String> list = new ArrayList<>();
                    list.add("EveryYear");
                    ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(CustomRepeatActivity.this, android.R.layout.simple_spinner_item, list);
                    arrayAdapter1.notifyDataSetChanged();
                    spinnerRepeatEvery.setAdapter(arrayAdapter1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        repeatCancelBtn.setOnClickListener(v -> {
            setResult(RESULT_CANCELED,intent1);
            Log.v("Info","Cancelled Repeat options");
            CustomRepeatActivity.this.finish();
        });
        repeatDoneBtn.setOnClickListener(v -> {
            Spinner sEvery = findViewById(R.id.spinnerEvery);
            String sRecurrence = sEvery.getSelectedItem().toString();;
            Spinner sRepeatEvery = findViewById(R.id.spinnerRepeatEvery);
            String strRepeatEvery = sRepeatEvery.getSelectedItem().toString();
            CheckBox isSunChk, isMonChk, isTuesChk, isWedChk, isThursChk, isFriChk, isSatChk;
            isSunChk = findViewById(R.id.checkBoxSun);
            isMonChk = findViewById(R.id.checkBoxMon);
            isTuesChk= findViewById(R.id.checkBoxTues);
            isWedChk = findViewById(R.id.checkBoxWed);
            isThursChk= findViewById(R.id.checkBoxThurs);
            isFriChk= findViewById(R.id.checkBoxFri);
            isSatChk= findViewById(R.id.checkBoxSat);
            Log.v("strRepeatEvery",strRepeatEvery);
            if(strRepeatEvery.matches("EveryWeek")){
                if(isSunChk.isChecked() || isMonChk.isChecked() || isTuesChk.isChecked() || isWedChk.isChecked() || isThursChk.isChecked() || isFriChk.isChecked() || isSatChk.isChecked()){

                    intent1.putExtra("isSun", isSunChk.isChecked());
                    intent1.putExtra("isMon", isMonChk.isChecked());
                    intent1.putExtra("isTues", isTuesChk.isChecked());
                    intent1.putExtra("isWed", isWedChk.isChecked());
                    intent1.putExtra("isThus", isThursChk.isChecked());
                    intent1.putExtra("isFri", isFriChk.isChecked());
                    intent1.putExtra("isSat", isSatChk.isChecked());
                    intent1.putExtra("eventRecurrence", sRecurrence);
                    intent1.putExtra("eventRepeatEvery", strRepeatEvery);

                    setResult(RESULT_OK,intent1);
                    Log.v("Info","Done Repeat options");
                    CustomRepeatActivity.this.finish();
                }else{
                    Toast.makeText(CustomRepeatActivity.this, "Please select the check boxes for weekdays", Toast.LENGTH_LONG).show();
                }
            }else{
                intent1.putExtra("isSun", Boolean.FALSE);
                intent1.putExtra("isMon", Boolean.FALSE);
                intent1.putExtra("isTues", Boolean.FALSE);
                intent1.putExtra("isWed", Boolean.FALSE);
                intent1.putExtra("isThus", Boolean.FALSE);
                intent1.putExtra("isFri", Boolean.FALSE);
                intent1.putExtra("isSat", Boolean.FALSE);
                intent1.putExtra("eventRecurrence", sRecurrence);
                intent1.putExtra("eventRepeatEvery", strRepeatEvery);
                setResult(RESULT_OK,intent1);
                Log.v("Info","Done Repeat options");
                CustomRepeatActivity.this.finish();
            }

        });
    }
}