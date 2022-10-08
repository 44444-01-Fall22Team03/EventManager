package androidproject.team03.eventmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_btn = findViewById(R.id.loginBtn);

        login_btn.setOnClickListener(v -> {
            startActivity(new Intent(this,EventHomeActivity.class));
        });

    }
}