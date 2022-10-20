package androidproject.team03.eventmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends AppCompatActivity {

    private EditText regEmail, regUsername, regPassword, regConfirmPassword;
    private Button registerBtn;
    private TextView regToLoginLink;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(MainActivity.this);

        regEmail=findViewById(R.id.etEmail);
        regUsername=findViewById(R.id.etUsername);
        regPassword=findViewById(R.id.etPassword);
        registerBtn=findViewById(R.id.btnRegister);
        regToLoginLink = findViewById(R.id.tvRedirect);
        regConfirmPassword = findViewById(R.id.etConfirmPassword);

        NewUser[] nUser = new NewUser[1];

        registerBtn.setOnClickListener(v -> {
            if (regPassword.getText().toString().equals(regConfirmPassword.getText().toString()) && !TextUtils.isEmpty(regUsername.getText().toString())) {
                nUser[0] = new NewUser(regUsername.getText().toString(),regEmail.getText().toString(), regPassword.getText().toString());
                login(nUser[0]);
            }
            else{
                Toast.makeText(this, "Make sure that the values you entered are correct.", Toast.LENGTH_SHORT).show();
            }
        });

        regToLoginLink.setOnClickListener( v -> {
            startActivity(new Intent(this,LoginActivity.class));
        });

    }

    private void login(NewUser newUser) {
        progressDialog.show();
        ParseUser user = new ParseUser();
        // Set the user's username and password, which can be obtained by a forms
        user.setUsername(newUser.getUserName());
        user.setPassword(newUser.getPassword());
        user.setEmail(newUser.getEmail());

        user.signUpInBackground(e -> {
            progressDialog.dismiss();
            if (e == null) {
                showAlert("Successful Sign Up ! You are now logged in...\n", "Welcome " + user.getUsername() + " !");
            } else {
                ParseUser.logOut();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        // don't forget to change the line below with the names of your Activities
                        Intent intent = new Intent(MainActivity.this, EventHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

}