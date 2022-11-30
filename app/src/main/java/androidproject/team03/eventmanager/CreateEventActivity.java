package androidproject.team03.eventmanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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

import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {
    EditText date_time_in;
    EditText date_time_end;
    private ProgressDialog progressDialog;

    private String eName, eLocation, eDescription, eRecurrence="none", eRepeatEvery="none";
    private Date eStartDt, eRepeatEndDt,eEndDt;
    boolean booleanIsSun=Boolean.FALSE, booleanIsMon=Boolean.FALSE, booleanIsTues=Boolean.FALSE, booleanIsWed=Boolean.FALSE, booleanIsThurs=Boolean.FALSE, booleanIsFri=Boolean.FALSE, booleanIsSat=Boolean.FALSE;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    ActivityResultLauncher<Intent> getResultContent= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result != null && result.getResultCode()== RESULT_OK){
                try {
                    //  Block of code to try
                    eRecurrence =result.getData().getStringExtra("eventRecurrence");
                    eRepeatEvery =result.getData().getStringExtra("eventRepeatEvery");
                    booleanIsSun=result.getData().getBooleanExtra("isSun",Boolean.FALSE);
                    booleanIsMon=result.getData().getBooleanExtra("isMon", Boolean.FALSE);
                    booleanIsTues=result.getData().getBooleanExtra("isTues", Boolean.FALSE);
                    booleanIsWed=result.getData().getBooleanExtra("isWed", Boolean.FALSE);
                    booleanIsThurs=result.getData().getBooleanExtra("isThus", Boolean.FALSE);
                    booleanIsFri=result.getData().getBooleanExtra("isFri", Boolean.FALSE);
                    booleanIsSat=result.getData().getBooleanExtra("isSat", Boolean.FALSE);
                    Log.v("info", "using the result"+result.getData().toString());

                }
                catch(Exception e) {
                    Toast.makeText(CreateEventActivity.this, "ResultsOK "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        }
    });

    private Button createEventBtn, cancelCreateBtn, repeatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        progressDialog = new ProgressDialog(this);
        date_time_in =findViewById(R.id.Starttime);
        date_time_end =findViewById(R.id.Endtime);
        createEventBtn=findViewById(R.id.button3);
        cancelCreateBtn=findViewById(R.id.button4);
        repeatButton=findViewById(R.id.Repeat);

        initDatePicker();
        date_time_in.setInputType(InputType.TYPE_NULL);
        date_time_end.setInputType(InputType.TYPE_NULL);
        dateButton = findViewById(R.id.btnRepeatEndDate);

        dateButton.setText(getTodaysDate());

        createEventBtn.setOnClickListener(v -> {
            boolean flagChk =true;
            EditText eNameET, eStartDtET, eEndDtET, eLocationET, eDescriptionET;
            Button eRepeatEndDtET;
            eNameET = findViewById(R.id.EventName);
            eLocationET = findViewById(R.id.Eventlocation);
            eDescriptionET = findViewById(R.id.Description);
            eStartDtET = findViewById(R.id.Starttime);
            eEndDtET = findViewById(R.id.Endtime);
            eRepeatEndDtET = findViewById(R.id.btnRepeatEndDate);

            String strStartDtET, strEndDtET, strRepeatEndDtET;

            eName= eNameET.getText().toString();
            strStartDtET= eStartDtET.getText().toString();
            strEndDtET= eEndDtET.getText().toString();
            eLocation= eLocationET.getText().toString();
            eDescription= eDescriptionET.getText().toString();
            strRepeatEndDtET= eRepeatEndDtET.getText().toString();

            if(eName.matches("")){
                flagChk= Boolean.FALSE;
                Log.v("strName:","is Empty");
            }
            if(strStartDtET.matches("")){
                flagChk= Boolean.FALSE;
                Log.v("strStartDtET:","is Empty");
            }
            if(strEndDtET.matches("")){
                flagChk= Boolean.FALSE;
                Log.v("strEndDtET:","is Empty");
            }
            if(eLocation.matches("")){
                flagChk= Boolean.FALSE;
                Log.v("strLocation:","is Empty");
            }
            if(eDescription.matches("")){
                flagChk= Boolean.FALSE;
                Log.v("strName:","is Empty");
            }
            if(strRepeatEndDtET.matches("")){
                flagChk= Boolean.FALSE;
                Log.v("strName:","is Empty");
            }

            if(flagChk){
                try {
                    eStartDt=new SimpleDateFormat("MM-dd-yyyy HH:mm").parse(strStartDtET);
                    eRepeatEndDt =new SimpleDateFormat("MMM dd yyyy").parse(strRepeatEndDtET);
                    eEndDt=new SimpleDateFormat("MM-dd-yyyy HH:mm").parse(strEndDtET);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try{
                    ParseObject eventObj= new ParseObject("Events");
                    eventObj.put("eventName",eName);
                    eventObj.put("eventDescription",eDescription);
                    eventObj.put("eventLocation",eLocation);
                    eventObj.put("eventRecurrence",eRecurrence);
                    eventObj.put("eventRepeatEvery",eRepeatEvery);
                    eventObj.put("isSun",booleanIsSun);
                    eventObj.put("isMon",booleanIsMon);
                    eventObj.put("isTues",booleanIsTues);
                    eventObj.put("isWed",booleanIsWed);
                    eventObj.put("isThurs",booleanIsThurs);
                    eventObj.put("isFri",booleanIsFri);
                    eventObj.put("isSat",booleanIsSat);
                    eventObj.put("eventEndDt",eEndDt);
                    eventObj.put("eventStartDt", eStartDt);
                    eventObj.put("eventRepeatEndDt",eRepeatEndDt);

                    String str= "eventName"+eName+ "eventDescription"+eDescription+ "eventLocation"+eLocation+ "eventRecurrence"+eRecurrence+ "eventRepeatEvery"+eRepeatEvery+ "isSun"+booleanIsSun+ "isMon"+booleanIsMon+ "isTues"+booleanIsTues+ "isWed"+booleanIsWed+ "isThurs"+booleanIsThurs+ "isFri"+booleanIsFri+ "isSat"+booleanIsSat+ "eventEndDt"+eEndDt+ "eventStartDt"+ eStartDt+ "eventRepeatEndDt"+eRepeatEndDt;

                    Log.v("String Data:",str);
                    eventObj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            progressDialog.dismiss();
                            if (e == null) {
                                // Success
                                showAlert("Successfully saved..!\n", "Your object has been saved in Events class.");
                            }else {
                                // Error
                                Toast.makeText(CreateEventActivity.this, "SaveInBackground "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });



                }catch (Exception e){
                    Toast.makeText(CreateEventActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                }

                startActivity(new Intent(CreateEventActivity.this, EventHomeActivity.class));
            }else{
                Toast.makeText(CreateEventActivity.this, "Fill in all Fields", Toast.LENGTH_LONG).show();
            }

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

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        // don't forget to change the line below with the names of your Activities
                        Intent intent = new Intent(CreateEventActivity.this, EventHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}