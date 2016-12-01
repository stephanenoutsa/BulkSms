package com.stephnoutsa.bulksms;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.stephnoutsa.bulksms.models.Contact;
import com.stephnoutsa.bulksms.utils.MyDBHandler;

public class AddContact extends AppCompatActivity {

    MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
    EditText phoneField;
    String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phoneField = (EditText) findViewById(R.id.phoneField);
    }

    public void onClickSave(View view) {
        phone = phoneField.getText().toString();
        if (phone.equals("")) {
            phoneField.setError(getString(R.string.empty_field));
        } else if (phone.length() < 9) {
            phoneField.setError(getString(R.string.invalid_phone));
        } else {
            Contact contact = new Contact(phone, "no");
            boolean ok = dbHandler.addContact(contact);
            if (ok) {
                Toast.makeText(this, getString(R.string.contact_saved), Toast.LENGTH_SHORT).show();
                phone = "";
                phoneField.setText("");
            }
        }
    }

}
