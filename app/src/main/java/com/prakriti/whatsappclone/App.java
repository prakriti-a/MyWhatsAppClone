package com.prakriti.whatsappclone;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("AVgVLygnW0UTVZvRGPOj0ATDXcQEkRfrA23p477S")
                .clientKey("On9h5PguyxuVyR4mcmZe8d2oe692mmgGKw1U4q0r")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
