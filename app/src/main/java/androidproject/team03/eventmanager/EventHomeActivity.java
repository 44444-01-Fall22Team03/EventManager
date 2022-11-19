package androidproject.team03.eventmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventHomeActivity extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private ImageView logoutBtn, ivCreatebtn, ivSearchDate, ivSearchEvent;
    private ProgressDialog progressDialog;

    private EventModel myModel1 = EventModel.getSingleton();
    private EventRAdapter eventServer = null;
    private RecyclerView eventRecycler= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_home);
        setUpEventModels();
        initDatePicker();
        progressDialog = new ProgressDialog(EventHomeActivity.this);
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());
        ivCreatebtn = findViewById(R.id.ivCreateEvent);
        logoutBtn = findViewById(R.id.ivlogout);

        ivSearchDate= findViewById(R.id.ivDateSearch);
        ivSearchEvent= findViewById(R.id.ivSearch);


        ivSearchEvent.setOnClickListener(v -> {
            EditText searchEventET = findViewById(R.id.etSearchtext);
            String searchText = searchEventET.getText().toString();
            if(searchText.matches("")){
                Toast.makeText(EventHomeActivity.this, "Search field is Empty.\nPlease input text and try again", Toast.LENGTH_LONG).show();
            }else{
                nameSearchDataSetup(searchText);
            }
        });

        ivCreatebtn.setOnClickListener(v -> {
            startActivity(new Intent(EventHomeActivity.this, CreateEventActivity.class));
        });

        logoutBtn.setOnClickListener(v ->{
            progressDialog.show();
            // logging out of Parse
            ParseUser.logOutInBackground(e -> {
                progressDialog.dismiss();
                if (e == null)
                    showAlert("So, you're going...", "Ok...Bye-bye...!");
            });
        });
    }

    private void setUpEventModels() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        Log.v("setUpEventModels", "fetched data");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        String formattedDate = df.format(date);

        Date dateToday = null;
        try {
            dateToday = new SimpleDateFormat("MM-dd-yyyy").parse(formattedDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Log.v("today's Date: ", dateToday.toString());
        calendar.setTime(dateToday);
        calendar.add(Calendar.DATE, 1);
        Date dateTomorrow = calendar.getTime();
        Log.v("tomorrow's Date: ", dateTomorrow.toString());
        query.whereGreaterThanOrEqualTo("eventStartDt", dateToday).whereLessThan("eventStartDt", dateTomorrow);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject event : objects){
                    if (e == null) {
                        String event_ID= event.getString("eventID");
                        String event_Name= event.getString("eventName");
                        String event_Description= event.getString("eventDescription");
                        String event_Location= event.getString("eventLocation");
                        String event_Recurrence=event.getString("eventRecurrence");
                        String event_Repeat_Every=event.getString("eventRepeatEvery");
                        boolean is_Sun = event.getBoolean("isSun");
                        boolean is_Mon= event.getBoolean("isMon");
                        boolean is_Tues= event.getBoolean("isTues");
                        boolean is_Wed= event.getBoolean("isWed");
                        boolean is_Thurs= event.getBoolean("isThurs");
                        boolean is_Fri= event.getBoolean("isFri");
                        boolean is_Sat= event.getBoolean("isSat");
                        String event_End_Dt = String.valueOf(event.getDate("eventEndDt"));
                        String event_Start_Dt= String.valueOf(event.getDate("eventStartDt"));
                        String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                        myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                        Log.v("description", String.valueOf(event.getString("eventDescription")));

                        Log.v("Size", String.valueOf(myModel1.eventsList.size()));

                    } else {
                        Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    eventRecycler = findViewById(R.id.mRecyclerView);


                    eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);

                    Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                    eventRecycler.setAdapter(eventServer);
                    eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                }
            }
        });
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
        return "Jan";
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EventHomeActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        // don't forget to change the line below with the names of your Activities
                        Intent intent = new Intent(EventHomeActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void nameSearchDataSetup(String searchText) {
        myModel1.eventsList.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        Log.v("nameSearchDataSetup: ", "fetching data");

        query.whereContains("eventName", searchText);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject event : objects){
                    if (e == null) {
                        String event_ID= event.getString("eventID");
                        String event_Name= event.getString("eventName");
                        String event_Description= event.getString("eventDescription");
                        String event_Location= event.getString("eventLocation");
                        String event_Recurrence=event.getString("eventRecurrence");
                        String event_Repeat_Every=event.getString("eventRepeatEvery");
                        boolean is_Sun = event.getBoolean("isSun");
                        boolean is_Mon= event.getBoolean("isMon");
                        boolean is_Tues= event.getBoolean("isTues");
                        boolean is_Wed= event.getBoolean("isWed");
                        boolean is_Thurs= event.getBoolean("isThurs");
                        boolean is_Fri= event.getBoolean("isFri");
                        boolean is_Sat= event.getBoolean("isSat");
                        String event_End_Dt = String.valueOf(event.getDate("eventEndDt"));
                        String event_Start_Dt= String.valueOf(event.getDate("eventStartDt"));
                        String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                        myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                        Log.v("description", String.valueOf(event.getString("eventDescription")));

                        Log.v("Size", String.valueOf(myModel1.eventsList.size()));

                    } else {
                        Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    eventRecycler = findViewById(R.id.mRecyclerView);


                    eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);

                    Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                    eventRecycler.setAdapter(eventServer);
                    eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                }
            }
        });
    }
}