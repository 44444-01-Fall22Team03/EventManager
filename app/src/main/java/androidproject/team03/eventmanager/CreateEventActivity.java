package androidproject.team03.eventmanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {
    EditText date_time_in;
    EditText date_time_end;

    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    ActivityResultLauncher<Intent> getResultContent= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result != null && result.getResultCode()== RESULT_OK){
                try {
                    //  Block of code to try
                    Log.v("info", "using the result");

                }
                catch(Exception e) {
                    Toast.makeText(CreateEventActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        }
    });

    private Button createEventBtn, cancelCreateBtn, repeatButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        date_time_in =findViewById(R.id.Starttime);
        date_time_end =findViewById(R.id.Endtime);
        createEventBtn=findViewById(R.id.button3);
        cancelCreateBtn=findViewById(R.id.button4);
        repeatButton=findViewById(R.id.Repeat);

        initDatePicker();
        date_time_in.setInputType(InputType.TYPE_NULL);
        date_time_end.setInputType(InputType.TYPE_NULL);
        dateButton = findViewById(R.id.btnRepeatEndDate);

        createEventBtn.setOnClickListener(v -> {
            startActivity(new Intent(CreateEventActivity.this, EventHomeActivity.class));
        });
        cancelCreateBtn.setOnClickListener(v -> {
            startActivity(new Intent(CreateEventActivity.this, EventHomeActivity.class));
        });

        repeatButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomRepeatActivity.class);
            getResultContent.launch(intent);
        });

        date_time_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeDialog(date_time_in);
            }
        });

        date_time_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeDialog(date_time_end);
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

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }
    private void initDatePicker()    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, android.R.style.Holo_Light_ButtonBar_AlertDialog, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }
    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getTodaysDate()    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }
    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "Jan";
        if(month == 2)
            return "Feb";
        if(month == 3)
            return "Mar";
        if(month == 4)
            return "Apr";
        if(month == 5)
            return "May";
        if(month == 6)
            return "Jun";
        if(month == 7)
            return "Jul";
        if(month == 8)
            return "Aug";
        if(month == 9)
            return "Sep";
        if(month == 10)
            return "Oct";
        if(month == 11)
            return "Nov";
        if(month == 12)
            return "Dec";

        //default should never happen
        return "JAN";
    }

}