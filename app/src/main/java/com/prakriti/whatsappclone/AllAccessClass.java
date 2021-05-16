package com.prakriti.whatsappclone;

import android.widget.EditText;

public class AllAccessClass {

    // check for empty fields submitted
    public static boolean isFieldNull(EditText field) {
        if (field.getText().toString().trim().equalsIgnoreCase("")) {
            field.setError("This field cannot be blank");
            field.requestFocus();
            return true;
        }
        return false;
        // equals() compares contents, == compares objects
    }
}
