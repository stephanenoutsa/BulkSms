package com.stephnoutsa.bulksms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.stephnoutsa.bulksms.adapters.ContactAdapter;
import com.stephnoutsa.bulksms.models.Contact;
import com.stephnoutsa.bulksms.utils.MyDBHandler;

import java.util.ArrayList;
import java.util.List;

public class Contacts extends AppCompatActivity {

    Context context = this;
    MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
    List<Contact> contacts = new ArrayList<>();
    List<String[]> list = new ArrayList<>();
    ListView listView;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listView);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                contacts = dbHandler.getAllContacts();

                for (int i = 0; i < contacts.size(); i++) {
                    int sn = i + 1;
                    String snStr = String.valueOf(sn);

                    String phone = contacts.get(i).getPhone();

                    String[] contact = {snStr, phone};
                    list.add(contact);
                }

                listAdapter = new ContactAdapter(context, list);
                listView.setAdapter(listAdapter);

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String[] contact = (String[]) parent.getItemAtPosition(position);
                        final String phone = contact[1];

                        new AlertDialog.Builder(context)
                                .setIcon(android.R.drawable.ic_menu_delete)
                                .setTitle(getString(R.string.delete_title))
                                .setMessage(getString(R.string.delete_warning))
                                .setPositiveButton(getString(R.string.delete_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dbHandler.deleteContact(phone);
                                        Intent i = new Intent(Contacts.this, Contacts.class);
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(i);
                                        overridePendingTransition(0, 0);
                                    }
                                })
                                .setNegativeButton(getString(R.string.delete_cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();

                        return false;
                    }
                });
            }
        };

        Runnable r = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

}
