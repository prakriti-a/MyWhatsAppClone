package com.prakriti.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.developers.smartytoast.SmartyToast;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtLoginName, edtLoginNumber;
    private Button btnLogin, btnGoToSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtLoginName = findViewById(R.id.edtLoginName);
        edtLoginNumber = findViewById(R.id.edtLoginNumber);

        btnLogin = findViewById(R.id.btnLogin);
        btnGoToSignUp = findViewById(R.id.btnGoToSignUp);

        btnLogin.setOnClickListener(this);
        btnGoToSignUp.setOnClickListener(this);

        edtLoginNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // checking if user has pressed key
                    onClick(btnLogin); // button is a view
                }
                return false;
            }
        });
    }

    // to disappear keyboard when user taps on screen -> set listener for root view of associated layout file
    public void hideKeyboard(View view) {
        // if keyboard is visible, only then hide it. Or else, crash
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            view.clearFocus();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
     switch (v.getId()) {
         case R.id.btnLogin:
             if(AllAccessClass.isFieldNull(edtLoginName) || AllAccessClass.isFieldNull(edtLoginNumber)) {
                 return;
             }
             else {
                 loginUserToAccount();
             }
             break;
         case R.id.btnGoToSignUp:
             startActivity(new Intent(LoginActivity.this, MainActivity.class));
             finish();
             break;
     }
    }

    private void loginUserToAccount() {
        try {
            String loginName = edtLoginName.getText().toString().trim();
            String loginNumber = edtLoginNumber.getText().toString().trim();

            SmartyToast.makeText(this, "Logging in...", SmartyToast.LENGTH_SHORT, SmartyToast.UPDATE).show();

            ParseUser.logInInBackground(loginName, loginNumber, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user != null && e == null) {
                        SmartyToast.makeText(LoginActivity.this, "Welcome " + user.getString("username") + "!",
                                Toast.LENGTH_SHORT, SmartyToast.DONE).show();
                        edtLoginName.setText("");
                        edtLoginNumber.setText("");
                        startActivity(new Intent(LoginActivity.this, UsersList.class));
                        // finish this activity so user cannot log out by back button
                        finish();
                    }
                    else {
                        SmartyToast.makeText(LoginActivity.this, "Unable to log in\n" + e.getMessage() + "\nPlease try again",
                                Toast.LENGTH_SHORT, SmartyToast.ERROR).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e) {
            SmartyToast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT, SmartyToast.ERROR).show();
            e.printStackTrace();
        }
    }
}