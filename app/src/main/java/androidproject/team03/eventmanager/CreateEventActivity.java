package androidproject.team03.eventmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {
    EditText date_time_in;

    private Button createEventBtn, cancelCreateBtn, repeatButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        date_time_in =findViewById(R.id.Starttime);
        createEventBtn=findViewById(R.id.button3);
        cancelCreateBtn=findViewById(R.id.button4);
        repeatButton=findViewById(R.id.Repeat);

        date_time_in.setInputType(InputType.TYPE_NULL);

        createEventBtn.setOnClickListener(v -> {
            startActivity(new Intent(CreateEventActivity.this, EventHomeActivity.class));
        });
        cancelCreateBtn.setOnClickListener(v -> {
            startActivity(new Intent(CreateEventActivity.this, EventHomeActivity.class));
        });

        repeatButton.setOnClickListener(v -> {
            startActivity(new Intent(CreateEventActivity.this, CustomRepeatActivity.class));
        });

        date_time_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeDialog(date_time_in);
            }
        });

    }
    private void showDateTimeDialog(EditText date_time_in){
        Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener= new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));

                    }
                };
                new TimePickerDialog(CreateEventActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();

            }
        };

        new DatePickerDialog(CreateEventActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}