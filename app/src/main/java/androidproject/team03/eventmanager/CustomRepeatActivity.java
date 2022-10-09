package androidproject.team03.eventmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class CustomRepeatActivity extends AppCompatActivity {

    private Button repeatCancelBtn, repeatDoneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_repeat);

        repeatCancelBtn=findViewById(R.id.btnRepeatCancel);
        repeatDoneBtn=findViewById(R.id.btnRepeatDone);

        repeatCancelBtn.setOnClickListener(v -> {
            startActivity(new Intent(CustomRepeatActivity.this, CreateEventActivity.class));
        });
        repeatDoneBtn.setOnClickListener(v -> {
            startActivity(new Intent(CustomRepeatActivity.this, CreateEventActivity.class));
        });
    }
}