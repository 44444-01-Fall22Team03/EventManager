package androidproject.team03.eventmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class EventHomeActivity extends AppCompatActivity {

    ArrayList<EventModel> eventModels1 = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_home);

        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);
        setUpEventModels();

        EventRAdapter adapter = new EventRAdapter(this, eventModels1);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void setUpEventModels(){
        String[] event_names = getResources().getStringArray(R.array.eventNames);
        String[] event_st_dts = getResources().getStringArray(R.array.eventStartDates);
        String[] event_end_dts = getResources().getStringArray(R.array.eventEndDates);
        String[] event_locs = getResources().getStringArray(R.array.eventLocations);
        String[] event_descs = getResources().getStringArray(R.array.eventDesc);
        String[] event_notes = getResources().getStringArray(R.array.eventNotes);

        for( int i = 0; i<event_names.length;i++){
            eventModels1.add(new EventModel(event_names[i], event_st_dts[i],event_end_dts[i],event_locs[i],event_descs[i],event_notes[i]));
        }
    }
}