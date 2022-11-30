package androidproject.team03.eventmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventHomeActivity extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private ImageView logoutBtn, ivCreatebtn, ivSearchDate, ivSearchEvent,ivMenu;
    private ProgressDialog progressDialog;

    public EventModel myModel1 = EventModel.getSingleton();
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

        ivSearchDate.setOnClickListener(v -> {
            Button searchDateET = findViewById(R.id.datePickerButton);
            String searchText = searchDateET.getText().toString();
            if(searchText.matches("")){
                Toast.makeText(EventHomeActivity.this, "Search field is Empty.\nPlease input text and try again", Toast.LENGTH_LONG).show();
            }else{
                dateSearchDataSetup(searchText);
            }
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

        ivMenu = findViewById(R.id.ivMenuIcon);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        ivMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
            navigationView.bringToFront();
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
                int id = item.getItemId();

                Log.v("Inside:","onNavigationItemSelected");
                switch (id){
                    case R.id.nav_event_home:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Toast.makeText(EventHomeActivity.this, "Event Home is Clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);break;
                    case R.id.nav_add_event:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Toast.makeText(EventHomeActivity.this, "Add Event is Clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EventHomeActivity.this, CreateEventActivity.class));
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Toast.makeText(EventHomeActivity.this, "Logout is Clicked", Toast.LENGTH_SHORT).show();
                        progressDialog.show();
                        // logging out of Parse
                        ParseUser.logOutInBackground(e -> {
                            progressDialog.dismiss();
                            if (e == null)
                                showAlert("So, you're going...", "Ok...Bye-bye...!");
                        });
                        break;
                    default:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        });
        ivCreatebtn.setOnClickListener(v -> {
            Log.v("Create Event:","is selected");
            startActivity(new Intent(EventHomeActivity.this, CreateEventActivity.class));
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
                Log.v("Entered","Current day");
                for (ParseObject event : objects){
                    if (e == null) {
                        String event_ID= event.getObjectId();
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

                        Log.v("Current date Size", String.valueOf(myModel1.eventsList.size()));

                        eventRecycler = findViewById(R.id.mRecyclerView);
                        eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                        Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                        eventRecycler.setAdapter(eventServer);
                        eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));

                    } else {
                        Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.v("Current date error", e.getMessage());
                    }
                }
            }
        });
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        Log.v("Calendar.DAY_OF_WEEK",String.valueOf(i));
        switch(i){
            case Calendar.SUNDAY:
                Log.v("Entered","Sunday");
                Calendar calendar7 = Calendar.getInstance();
                Date date7 = calendar7.getTime();
                SimpleDateFormat df7 = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String formattedDate7 = df7.format(date7);
                Date dateT7 =calendar7.getTime();
                try {
                    dateT7 = new SimpleDateFormat("MM-dd-yyyy").parse(formattedDate7);
                } catch (java.text.ParseException ex) {
                    ex.printStackTrace();
                }
                Log.v("today's Date: ", dateT7.toString());
                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Events");
                query1.whereEqualTo("isSun",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateT7).whereLessThan("eventStartDt", dateT7);
                query1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Sunday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Sunday error", e.getMessage());
                            }
                        }
                    }
                });
                break;
            case Calendar.MONDAY:
                Log.v("Entered","Monday");
                Calendar calendar6 = Calendar.getInstance();
                Date date6 = calendar6.getTime();
                SimpleDateFormat df6 = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String formattedDate6 = df6.format(date6);
                Date dateT6 =calendar6.getTime();
                try {
                    dateT6 = new SimpleDateFormat("MM-dd-yyyy").parse(formattedDate6);
                } catch (java.text.ParseException ex) {
                    ex.printStackTrace();
                }
                Log.v("today's Date: ", dateT6.toString());
                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Events");
                query2.whereEqualTo("isMon",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateT6).whereLessThan("eventStartDt", dateT6);
                query2.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Monday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Monday error", e.getMessage());
                            }
                        }
                    }
                });
                break;
            case Calendar.TUESDAY:
                Log.v("Entered","Tuesday");
                Calendar calendar5 = Calendar.getInstance();
                Date date5 = calendar5.getTime();
                SimpleDateFormat df5 = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String formattedDate5 = df5.format(date5);
                Date dateT5 =calendar5.getTime();
                try {
                    dateT5 = new SimpleDateFormat("MM-dd-yyyy").parse(formattedDate5);
                } catch (java.text.ParseException ex) {
                    ex.printStackTrace();
                }
                Log.v("today's Date: ", dateT5.toString());
                ParseQuery<ParseObject> query3 = ParseQuery.getQuery("Events");
                query3.whereEqualTo("isTues",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateT5).whereLessThan("eventStartDt", dateT5);
                query3.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Tuesday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Tuesday error", e.getMessage());
                            }
                        }
                    }
                });
                break;
            case Calendar.WEDNESDAY:
                Log.v("Entered","Wednesday");
                Calendar calendar4 = Calendar.getInstance();
                Date date4 = calendar4.getTime();
                SimpleDateFormat df4 = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String formattedDate4 = df4.format(date4);
                Date dateT4 =calendar4.getTime();
                try {
                    dateT4 = new SimpleDateFormat("MM-dd-yyyy").parse(formattedDate4);
                } catch (java.text.ParseException ex) {
                    ex.printStackTrace();
                }
                Log.v("today's Date: ", dateT4.toString());
                ParseQuery<ParseObject> query4 = ParseQuery.getQuery("Events");
                query4.whereEqualTo("isWed",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateT4).whereLessThan("eventStartDt", dateT4);
                query4.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Wednesday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Wednesday error", e.getMessage());
                            }
                        }
                    }
                });
                break;
            case Calendar.THURSDAY:
                Log.v("Entered","ThursDay");
                Calendar calendar3 = Calendar.getInstance();
                Date date3 = calendar3.getTime();
                SimpleDateFormat df3 = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String formattedDate3 = df3.format(date3);
                Date dateT3 =calendar3.getTime();
                try {
                    dateT3 = new SimpleDateFormat("MM-dd-yyyy").parse(formattedDate3);
                } catch (java.text.ParseException ex) {
                    ex.printStackTrace();
                }
                Log.v("today's Date: ", dateT3.toString());
                ParseQuery<ParseObject> query5 = ParseQuery.getQuery("Events");
                query5.whereEqualTo("isThurs",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateT3).whereLessThan("eventStartDt", dateT3);
                query5.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Thursday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Thursday error", e.getMessage());
                            }
                        }
                    }
                });
                break;
            case Calendar.FRIDAY:
                Log.v("Entered","Friday");
                Calendar calendar2 = Calendar.getInstance();
                Date date2 = calendar2.getTime();
                SimpleDateFormat df2 = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String formattedDate2 = df2.format(date2);
                Date dateT2 =calendar2.getTime();
                try {
                    dateT2 = new SimpleDateFormat("MM-dd-yyyy").parse(formattedDate2);
                } catch (java.text.ParseException ex) {
                    ex.printStackTrace();
                }
                Log.v("today's Date: ", dateT2.toString());
                ParseQuery<ParseObject> query6 = ParseQuery.getQuery("Events");
                query6.whereEqualTo("isFri",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateT2).whereLessThan("eventStartDt", dateT2);
                query6.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Friday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Friday error", e.getMessage());
                            }
                        }
                    }
                });
                break;
            case Calendar.SATURDAY:
                Log.v("Entered","Saturday");
                Calendar calendar1 = Calendar.getInstance();
                Date date1 = calendar1.getTime();
                SimpleDateFormat df1 = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                String formattedDate1 = df1.format(date1);
                Date dateT1 =calendar1.getTime();
                try {
                    dateT1 = new SimpleDateFormat("MM-dd-yyyy").parse(formattedDate1);
                } catch (java.text.ParseException ex) {
                    ex.printStackTrace();
                }
                Log.v("today's Date: ", dateT1.toString());
                ParseQuery<ParseObject> query7 = ParseQuery.getQuery("Events");
                query7.whereEqualTo("isSat",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateT1).whereLessThan("eventStartDt", dateT1);
                query7.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Saturday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Saturday error", e.getMessage());
                            }
                        }
                    }
                });
                break;
            default:
                break;
        }
        ParseQuery<ParseObject> queryYear = ParseQuery.getQuery("Events");
        queryYear.whereLessThan("eventStartDt", dateToday).whereLessThan("eventRecurrence", "Yearly");
        queryYear.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject event : objects){
                    if (e == null) {
                        Log.v("Entered","Year");
                        Calendar calendar2 = Calendar.getInstance();
                        Date date2 = calendar2.getTime();
                        SimpleDateFormat df2 = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                        String formattedDate2 = df2.format(date2);
                        Date dateT2 =calendar2.getTime();
                        try {
                            dateT2 = new SimpleDateFormat("MM-dd-yyyy").parse(formattedDate2);
                        } catch (java.text.ParseException ex) {
                            ex.printStackTrace();
                        }
                        Log.v("today's Date: ", dateT2.toString());
                        Date chkDate = event.getDate("eventStartDt");
                        DateFormat format1 = new SimpleDateFormat("MM-dd");
                        String strChkDate= format1.format(chkDate);
                        String strtodayDate= format1.format(dateT2);
                        if(strChkDate.equals(strtodayDate)){
                            String event_ID= event.getObjectId();
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
                            Date stDate = event.getDate("eventStartDt");
                            Date endDate = event.getDate("eventEndDt");
                            DateFormat format = new SimpleDateFormat("HH:mm");
                            String event_End_Dt = format.format(endDate);
                            String event_Start_Dt= format.format(stDate);
                            String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                            myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                            Log.v("description", String.valueOf(event.getString("eventDescription")));

                            Log.v("Year Size", String.valueOf(myModel1.eventsList.size()));
                            eventRecycler = findViewById(R.id.mRecyclerView);
                            eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                            Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                            eventRecycler.setAdapter(eventServer);
                            eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                        }

                    } else {
                        Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.v("Yearly error", e.getMessage());
                    }
                }
            }
        });
        ParseQuery<ParseObject> queryMonthly = ParseQuery.getQuery("Events");
        queryMonthly.whereLessThan("eventStartDt", dateToday).whereLessThan("eventRecurrence", "Monthly");
        queryMonthly.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject event : objects){
                    if (e == null) {
                        Log.v("Entered","Monthly");
                        Calendar calendar2 = Calendar.getInstance();
                        Date date2 = calendar2.getTime();
                        SimpleDateFormat df2 = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                        String formattedDate2 = df2.format(date2);
                        Date dateT2 =calendar2.getTime();
                        try {
                            dateT2 = new SimpleDateFormat("MM-dd-yyyy").parse(formattedDate2);
                        } catch (java.text.ParseException ex) {
                            ex.printStackTrace();
                        }
                        Log.v("today's Date: ", dateT2.toString());
                        Date chkDate = event.getDate("eventStartDt");
                        DateFormat format1 = new SimpleDateFormat("dd");
                        String strChkDate= format1.format(chkDate);
                        String strtodayDate= format1.format(dateT2);
                        if(strChkDate.equals(strtodayDate)){
                            String event_ID= event.getObjectId();
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
                            Date stDate = event.getDate("eventStartDt");
                            Date endDate = event.getDate("eventEndDt");
                            DateFormat format = new SimpleDateFormat("HH:mm");
                            String event_End_Dt = format.format(endDate);
                            String event_Start_Dt= format.format(stDate);
                            String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                            myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                            Log.v("description", String.valueOf(event.getString("eventDescription")));

                            Log.v("Monthly Size", String.valueOf(myModel1.eventsList.size()));
                            eventRecycler = findViewById(R.id.mRecyclerView);
                            eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                            Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                            eventRecycler.setAdapter(eventServer);
                            eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                        }

                    } else {
                        Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.v("Monthly error", e.getMessage());
                    }
                }
            }
        });
        ParseQuery<ParseObject> queryDaily = ParseQuery.getQuery("Events");
        queryDaily.whereLessThan("eventStartDt", dateToday).whereLessThan("eventRecurrence", "Daily");
        queryDaily.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.v("Entered","Daily");
                for (ParseObject event : objects){
                    if (e == null) {
                        String event_ID= event.getObjectId();
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
                        Date stDate = event.getDate("eventStartDt");
                        Date endDate = event.getDate("eventEndDt");
                        DateFormat format = new SimpleDateFormat("HH:mm");
                        String event_End_Dt = format.format(endDate);
                        String event_Start_Dt= format.format(stDate);
                        String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                        myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                        Log.v("description", String.valueOf(event.getString("eventDescription")));

                        Log.v("Daily Size", String.valueOf(myModel1.eventsList.size()));
                        eventRecycler = findViewById(R.id.mRecyclerView);
                        eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                        Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                        eventRecycler.setAdapter(eventServer);
                        eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));

                    } else {
                        Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.v("Daily error", e.getMessage());
                    }
                }
            }
        });


        Log.v("With DUplicate:",myModel1.eventsList.toString());
        Log.v("With Duplicate Size", String.valueOf(myModel1.eventsList.size()));
        myModel1.eventsList = removeDuplicates(myModel1.eventsList);
        Log.v("Without DUplicate:",myModel1.eventsList.toString());
        Log.v("Without Duplicate Size", String.valueOf(myModel1.eventsList.size()));

        eventRecycler = findViewById(R.id.mRecyclerView);
        eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
        Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
        eventRecycler.setAdapter(eventServer);
        eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));

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
                        
                        String event_ID= event.getObjectId();
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
    private void dateSearchDataSetup(String searchDate) {
        myModel1.eventsList.clear();
        Calendar calendar = Calendar.getInstance();
        Date dateINst = calendar.getTime();
        try {
            dateINst = new SimpleDateFormat("MMM dd yyyy").parse(searchDate);
        } catch (java.text.ParseException e) {
            Log.v("ParseException Date: ", e.getMessage());
        }
        Log.v("Input Date start: ", dateINst.toString());
        calendar.setTime(dateINst);
        calendar.add(Calendar.DATE, 1);
        Date dateINend = calendar.getTime();
        Log.v("Input Date end: ", dateINend.toString());

        ParseQuery<ParseObject> queryDate = ParseQuery.getQuery("Events");
        queryDate.whereGreaterThanOrEqualTo("eventStartDt", dateINst).whereLessThan("eventStartDt", dateINend);
        queryDate.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject event : objects){
                    if (e == null) {
                        String event_ID= event.getObjectId();
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
                        Log.v("Current Date Size", String.valueOf(myModel1.eventsList.size()));
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

        ParseQuery<ParseObject> queryYear1 = ParseQuery.getQuery("Events");
        queryYear1.whereLessThan("eventStartDt", dateINst).whereEqualTo("eventRecurrence", "Yearly").whereGreaterThanOrEqualTo("eventRepeatEndDt",dateINst);
        queryYear1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject event : objects){
                    if (e == null) {
                        Log.v("Entered","Year");
                        Calendar calendar2 = Calendar.getInstance();
                        Date date2 = calendar2.getTime();
                        SimpleDateFormat df2 = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                        String formattedDate2 = df2.format(date2);
                        Date dateT2 =calendar2.getTime();
                        try {
                            dateT2 = new SimpleDateFormat("MMM dd yyyy").parse(searchDate);
                        } catch (java.text.ParseException ex) {
                            ex.printStackTrace();
                        }
                        Log.v("today's Date: ", dateT2.toString());
                        Date chkDate = event.getDate("eventStartDt");
                        DateFormat format1 = new SimpleDateFormat("MM-dd");
                        String strChkDate= format1.format(chkDate);
                        String strtodayDate= format1.format(dateT2);
                        if(strChkDate.equals(strtodayDate)){
                            String event_ID= event.getObjectId();
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
                            Date stDate = event.getDate("eventStartDt");
                            Date endDate = event.getDate("eventEndDt");
                            DateFormat format = new SimpleDateFormat("HH:mm");
                            String event_End_Dt = format.format(endDate);
                            String event_Start_Dt= format.format(stDate);
                            String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                            myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                            Log.v("description", String.valueOf(event.getString("eventDescription")));

                            Log.v("Yearly Size", String.valueOf(myModel1.eventsList.size()));
                            eventRecycler = findViewById(R.id.mRecyclerView);
                            eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                            Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                            eventRecycler.setAdapter(eventServer);
                            eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));

                        }

                    } else {
                        Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.v("Yearly error", e.getMessage());

                    }
                }
            }
        });
        ParseQuery<ParseObject> queryMonthly1 = ParseQuery.getQuery("Events");
        queryMonthly1.whereLessThan("eventStartDt", dateINst).whereEqualTo("eventRecurrence", "Monthly").whereGreaterThanOrEqualTo("eventRepeatEndDt",dateINst);
        queryMonthly1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject event : objects){
                    if (e == null) {
                        Log.v("Entered","Monthly");
                        Calendar calendar2 = Calendar.getInstance();
                        Date date2 = calendar2.getTime();
                        SimpleDateFormat df2 = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                        String formattedDate2 = df2.format(date2);
                        Date dateT2 =calendar2.getTime();
                        try {
                            dateT2 = new SimpleDateFormat("MMM dd yyyy").parse(searchDate);
                        } catch (java.text.ParseException ex) {
                            ex.printStackTrace();
                        }
                        Log.v("today's Date: ", dateT2.toString());
                        Date chkDate = event.getDate("eventStartDt");
                        DateFormat format1 = new SimpleDateFormat("dd");
                        String strChkDate= format1.format(chkDate);
                        String strtodayDate= format1.format(dateT2);
                        if(strChkDate.equals(strtodayDate)){
                            String event_ID= event.getObjectId();
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
                            Date stDate = event.getDate("eventStartDt");
                            Date endDate = event.getDate("eventEndDt");
                            DateFormat format = new SimpleDateFormat("HH:mm");
                            String event_End_Dt = format.format(endDate);
                            String event_Start_Dt= format.format(stDate);
                            String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                            myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                            Log.v("description", String.valueOf(event.getString("eventDescription")));

                            Log.v("Size", String.valueOf(myModel1.eventsList.size()));
                            eventRecycler = findViewById(R.id.mRecyclerView);
                            eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                            Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                            eventRecycler.setAdapter(eventServer);
                            eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                        }

                    } else {
                        Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.v("Monthly error", e.getMessage());
                    }
                }
            }
        });
        ParseQuery<ParseObject> queryDaily1 = ParseQuery.getQuery("Events");
        queryDaily1.whereLessThan("eventStartDt", dateINst).whereEqualTo("eventRecurrence", "Daily").whereGreaterThanOrEqualTo("eventRepeatEndDt",dateINst);
        queryDaily1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.v("Entered","Daily");
                for (ParseObject event : objects){
                    if (e == null) {
                        String event_ID= event.getObjectId();
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
                        Date stDate = event.getDate("eventStartDt");
                        Date endDate = event.getDate("eventEndDt");
                        DateFormat format = new SimpleDateFormat("HH:mm");
                        String event_End_Dt = format.format(endDate);
                        String event_Start_Dt= format.format(stDate);
                        String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                        myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                        Log.v("description", String.valueOf(event.getString("eventDescription")));

                        Log.v("Daily Size", String.valueOf(myModel1.eventsList.size()));
                        eventRecycler = findViewById(R.id.mRecyclerView);
                        eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                        Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                        eventRecycler.setAdapter(eventServer);
                        eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));


                    } else {
                        Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.v("Daily error", e.getMessage());

                    }
                }
            }
        });

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateINst);
        int i = cal.get(Calendar.DAY_OF_WEEK);
        Log.v("Calendar.DAY_OF_WEEK",String.valueOf(i));
        switch(i){
            case Calendar.SUNDAY:
                Log.v("Entered","Sunday");
                Calendar calendar1 = Calendar.getInstance();
                Date dateINst1 = calendar.getTime();
                try {
                    dateINst1 = new SimpleDateFormat("MMM dd yyyy").parse(searchDate);
                } catch (java.text.ParseException e) {
                    Log.v("ParseException Date: ", e.getMessage());
                }
                Log.v("Input Date start: ", dateINst.toString());
                calendar1.setTime(dateINst);
                calendar1.add(Calendar.DATE, 1);
                Date dateINend1 = calendar.getTime();
                Log.v("Input Date end: ", dateINend.toString());
                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Events");
                query1.whereEqualTo("isSun",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateINst1).whereLessThan("eventStartDt", dateINst1);
                query1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Sunday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Sunday error", e.getMessage());

                            }
                        }
                    }
                });
                break;
            case Calendar.MONDAY:
                Log.v("Entered","Monday");
                Calendar calendar2 = Calendar.getInstance();
                Date dateINst2 = calendar.getTime();
                try {
                    dateINst2 = new SimpleDateFormat("MMM dd yyyy").parse(searchDate);
                } catch (java.text.ParseException e) {
                    Log.v("ParseException Date: ", e.getMessage());
                }
                Log.v("Input Date start: ", dateINst.toString());
                calendar2.setTime(dateINst);
                calendar2.add(Calendar.DATE, 1);
                Date dateINend2 = calendar.getTime();
                Log.v("Input Date end: ", dateINend.toString());

                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Events");
                query2.whereEqualTo("isMon",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateINst2).whereLessThan("eventStartDt", dateINst2);
                query2.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Monday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Monday error", e.getMessage());
                            }
                        }
                    }
                });
                break;
            case Calendar.TUESDAY:
                Log.v("Entered","Tuesday");
                Calendar calendar3 = Calendar.getInstance();
                Date dateINst3 = calendar.getTime();
                try {
                    dateINst3 = new SimpleDateFormat("MMM dd yyyy").parse(searchDate);
                } catch (java.text.ParseException e) {
                    Log.v("ParseException Date: ", e.getMessage());
                }
                Log.v("Input Date start: ", dateINst.toString());
                calendar3.setTime(dateINst);
                calendar3.add(Calendar.DATE, 1);
                Date dateINend3 = calendar.getTime();
                Log.v("Input Date end: ", dateINend.toString());

                ParseQuery<ParseObject> query3 = ParseQuery.getQuery("Events");
                query3.whereEqualTo("isTues",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateINst3).whereLessThan("eventStartDt", dateINst3);
                query3.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Tuesday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Tuesday error", e.getMessage());

                            }
                        }
                    }
                });
                break;
            case Calendar.WEDNESDAY:
                Log.v("Entered","Wednesday");
                Calendar calendar4 = Calendar.getInstance();
                Date dateINst4 = calendar.getTime();
                try {
                    dateINst4 = new SimpleDateFormat("MMM dd yyyy").parse(searchDate);
                } catch (java.text.ParseException e) {
                    Log.v("ParseException Date: ", e.getMessage());
                }
                Log.v("Input Date start: ", dateINst.toString());
                calendar4.setTime(dateINst);
                calendar4.add(Calendar.DATE, 1);
                Date dateINend4 = calendar.getTime();
                Log.v("Input Date end: ", dateINend.toString());

                ParseQuery<ParseObject> query4 = ParseQuery.getQuery("Events");
                query4.whereEqualTo("isWed",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateINst4).whereLessThan("eventStartDt", dateINst4);
                query4.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Wednesday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Wednesday error", e.getMessage());

                            }
                        }
                    }
                });
                break;
            case Calendar.THURSDAY:
                Log.v("Entered","ThursDay");
                Calendar calendar5 = Calendar.getInstance();
                Date dateINst5 = calendar.getTime();
                try {
                    dateINst5 = new SimpleDateFormat("MMM dd yyyy").parse(searchDate);
                } catch (java.text.ParseException e) {
                    Log.v("ParseException Date: ", e.getMessage());
                }
                Log.v("Input Date start: ", dateINst.toString());
                calendar5.setTime(dateINst);
                calendar5.add(Calendar.DATE, 1);
                Date dateINend5 = calendar.getTime();
                Log.v("Input Date end: ", dateINend.toString());

                ParseQuery<ParseObject> query5 = ParseQuery.getQuery("Events");
                query5.whereEqualTo("isThurs",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateINst5).whereLessThan("eventStartDt", dateINst5);
                query5.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Thursday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Thursday error", e.getMessage());

                            }
                        }
                    }
                });
                break;
            case Calendar.FRIDAY:
                Log.v("Entered","Friday");
                Calendar calendar6 = Calendar.getInstance();
                Date dateINst6 = calendar.getTime();
                try {
                    dateINst6 = new SimpleDateFormat("MMM dd yyyy").parse(searchDate);
                } catch (java.text.ParseException e) {
                    Log.v("ParseException Date: ", e.getMessage());
                }
                Log.v("Input Date start: ", dateINst.toString());
                calendar6.setTime(dateINst);
                calendar6.add(Calendar.DATE, 1);
                Date dateINend6 = calendar.getTime();
                Log.v("Input Date end: ", dateINend.toString());

                ParseQuery<ParseObject> query6 = ParseQuery.getQuery("Events");
                query6.whereEqualTo("isFri",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateINst6).whereLessThan("eventStartDt", dateINst6);
                query6.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Friday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Friday error", e.getMessage());

                            }
                        }
                    }
                });
                break;
            case Calendar.SATURDAY:
                Log.v("Entered","Saturday");
                Calendar calendar7 = Calendar.getInstance();
                Date dateINst7 = calendar.getTime();
                try {
                    dateINst7 = new SimpleDateFormat("MMM dd yyyy").parse(searchDate);
                } catch (java.text.ParseException e) {
                    Log.v("ParseException Date: ", e.getMessage());
                }
                Log.v("Input Date start: ", dateINst.toString());
                calendar7.setTime(dateINst);
                calendar7.add(Calendar.DATE, 1);
                Date dateINend7 = calendar.getTime();
                Log.v("Input Date end: ", dateINend.toString());

                ParseQuery<ParseObject> query7 = ParseQuery.getQuery("Events");
                query7.whereEqualTo("isSat",true).whereGreaterThanOrEqualTo("eventRepeatEndDt", dateINst7).whereLessThan("eventStartDt", dateINst7);
                query7.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        for (ParseObject event : objects){
                            if (e == null) {
                                String event_ID= event.getObjectId();
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
                                Date stDate = event.getDate("eventStartDt");
                                Date endDate = event.getDate("eventEndDt");
                                DateFormat format = new SimpleDateFormat("HH:mm");
                                String event_End_Dt = format.format(endDate);
                                String event_Start_Dt= format.format(stDate);
                                String event_Repeat_EndDt= String.valueOf(event.getDate("eventRepeatEndDt"));

                                myModel1.eventsList.add(new EventModel.Events(event_ID, event_Name, event_Start_Dt, event_End_Dt, event_Location, event_Description, event_Repeat_EndDt, event_Recurrence, event_Repeat_Every, is_Sun,  is_Mon,  is_Tues,  is_Wed,  is_Thurs,  is_Fri,  is_Sat));

                                Log.v("Saturday Size", String.valueOf(myModel1.eventsList.size()));
                                eventRecycler = findViewById(R.id.mRecyclerView);
                                eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
                                Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
                                eventRecycler.setAdapter(eventServer);
                                eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
                            } else {
                                Toast.makeText(EventHomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.v("Saturday error", e.getMessage());

                            }
                        }
                    }
                });
                break;
            default:
                break;
        }



        Log.v("With DUplicate:",myModel1.eventsList.toString());
        Log.v("With Duplicate Size", String.valueOf(myModel1.eventsList.size()));
        myModel1.eventsList = removeDuplicates(myModel1.eventsList);
        Log.v("Without DUplicate:",myModel1.eventsList.toString());
        Log.v("Without Duplicate Size", String.valueOf(myModel1.eventsList.size()));

        eventRecycler = findViewById(R.id.mRecyclerView);
        eventServer = new EventRAdapter(EventHomeActivity.this, myModel1);
        Log.v("ItemCount", String.valueOf(eventServer.getItemCount())  );
        eventRecycler.setAdapter(eventServer);
        eventRecycler.setLayoutManager(new LinearLayoutManager(EventHomeActivity.this));
    }
    // Function to remove duplicates from an ArrayList
    public static ArrayList<EventModel.Events> removeDuplicates(ArrayList<EventModel.Events> list)
    {
        // Create a new ArrayList
        ArrayList<EventModel.Events> newList = new ArrayList<EventModel.Events>();

        // Traverse through the first list
        for (EventModel.Events element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }
        // return the new list
        return newList;
    }
}