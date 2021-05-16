package com.prakriti.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.developers.smartytoast.SmartyToast;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtSignupNumber, edtProfileName, edtProfileStatus;
    private Button btnSignUp, btnGoToLogin, btnSubmitProfile;
    private LinearLayout llProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtSignupNumber = findViewById(R.id.edtSignupNumber);
        edtProfileName = findViewById(R.id.edtProfileName);
        edtProfileStatus = findViewById(R.id.edtProfileStatus);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSubmitProfile = findViewById(R.id.btn_submitProfile);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);

        llProfile = findViewById(R.id.llProfile);

        btnSignUp.setOnClickListener(this);
        btnSubmitProfile.setOnClickListener(this);
        btnGoToLogin.setOnClickListener(this);

        // also create a listener for enter button on device keyboard
        edtSignupNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // checking if user has pressed key
                    onClick(btnSignUp); // button is a view
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
            case R.id.btnSignUp:
                if(AllAccessClass.isFieldNull(edtSignupNumber)) {
                    return;
                }
                else {
                    llProfile.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_submitProfile:
                if(AllAccessClass.isFieldNull(edtProfileName)) {
                    return;
                }
                else {
                    signUpNewUser();
                }
                break;
            case R.id.btnGoToLogin:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void signUpNewUser() {
        // put parse codes in try-catch block for input errors
            try {
                String userNumber = edtSignupNumber.getText().toString().trim();
                String profileName = edtProfileName.getText().toString().trim();
                String profileStatus;

                if(edtProfileStatus.getText().toString().trim().equalsIgnoreCase("")) {
                    profileStatus = "Hello, I'm using WhatsApp!";
                }
                else {
                    profileStatus = edtProfileStatus.getText().toString().trim();
                }
                // also write code for checking repetition of usernames in db

                // User class that already exists in Parse Server
                ParseUser appUser = new ParseUser();
                appUser.setUsername(profileName);
                appUser.setPassword(userNumber);
                appUser.put("status", profileStatus);

                SmartyToast.makeText(MainActivity.this, "Signing Up...", SmartyToast.LENGTH_SHORT, SmartyToast.UPDATE).show();

                appUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            edtSignupNumber.setText("");
                            edtProfileName.setText("");
                            edtProfileStatus.setText("");
                            SmartyToast.makeText(MainActivity.this, "Welcome " + profileName + "!",
                                    SmartyToast.LENGTH_SHORT, SmartyToast.DONE).show();

                            startActivity(new Intent(MainActivity.this, UsersList.class));
                            // once logged in, user shouldn't log out by pressing back button
                            finish(); // finishing the Sign up activity so user cannot transition back to it
                        }
                        else {
                            SmartyToast.makeText(MainActivity.this, "Unable to sign up\n" + e.getMessage() + "\nPlease try again",
                                    SmartyToast.LENGTH_SHORT, SmartyToast.ERROR).show();
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                SmartyToast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT, SmartyToast.ERROR).show();
                e.printStackTrace();
            }
        }
    }