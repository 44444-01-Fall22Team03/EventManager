package androidproject.team03.eventmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class CreateEventActivity extends AppCompatActivity {

    private Button createEventBtn, cancelCreateBtn, repeatButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        createEventBtn=findViewById(R.id.button3);
        cancelCreateBtn=findViewById(R.id.button4);
        repeatButton=findViewById(R.id.Repeat);

        createEventBtn.setOnClickListener(v -> {
            startActivity(new Intent(CreateEventActivity.this, EventHomeActivity.class));
        });
        cancelCreateBtn.setOnClickListener(v -> {
            startActivity(new Intent(CreateEventActivity.this, EventHomeActivity.class));
        });

        repeatButton.setOnClickListener(v -> {
            startActivity(new Intent(CreateEventActivity.this, CustomRepeatActivity.class));
        });

    }
}