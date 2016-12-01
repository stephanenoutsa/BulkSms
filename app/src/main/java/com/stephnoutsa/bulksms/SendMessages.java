package com.stephnoutsa.bulksms;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stephnoutsa.bulksms.models.Contact;
import com.stephnoutsa.bulksms.utils.GroupSender;
import com.stephnoutsa.bulksms.utils.MyDBHandler;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SendMessages extends AppCompatActivity {

    Context context = this;
    MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
    List<Contact> contacts = new ArrayList<>();
    TextView pageCount;
    EditText senderField, numberField, messageField;
    String sender = "", numString = "", message = "", url, standardUrl, urlPrefix, urlPostfix;
    int num, count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pageCount = (TextView) findViewById(R.id.pageCount);
        senderField = (EditText) findViewById(R.id.senderField);
        numberField = (EditText) findViewById(R.id.numberField);
        messageField = (EditText) findViewById(R.id.messageField);
        messageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                if (input.length() == 0) {
                    pageCount.setText("0");
                } else if (input.length() > 0 && input.length() <= 160) {
                    pageCount.setText("1");
                } else if (input.length() > 160 && input.length() <= 306) {
                    pageCount.setText("2");
                } else if (input.length() > 306 && input.length() <= 459) {
                    pageCount.setText("3");
                } else if (input.length() > 459 && input.length() <= 621) {
                    pageCount.setText("4");
                }
            }
        });

        urlPrefix = "http://api.wasamundi.com/v2/texto/send_sms?user=motherslove&api_key=nKjahG4T&from=";
        urlPostfix = "&to=237";

        contacts = dbHandler.getAllContacts();
        Collections.shuffle(contacts);
    }

    public void onClickSend(View view) {
        message = messageField.getText().toString();
        sender = senderField.getText().toString();

        // Check if user is connected
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (!message.equals("") && !sender.equals("")) {
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        try {
                            numString = numberField.getText().toString();
                            if (!numString.equals("")) {
                                num = Integer.parseInt(numString);
                            } else {
                                num = 0;
                            }

                            // Retrieve number of contacts in database
                            count = dbHandler.getContactsCount();

                            if (num >= count) {
                                num = -1;
                            }

                            sender = URLEncoder.encode(sender, "UTF-8");
                            standardUrl = urlPrefix + sender + urlPostfix;

                            if (count == 0) {
                                Toast.makeText(context, getString(R.string.no_contacts), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Number of contacts: " + num, Toast.LENGTH_SHORT).show();
                                if (num == 0 || num == -1) {
                                    for (Contact c : contacts) {
                                        String phone = c.getPhone();
                                        Toast.makeText(context, "Handling contact \'" + phone + "\'", Toast.LENGTH_SHORT).show();
                                        url = standardUrl + phone + "&msg=" + URLEncoder.encode(message, "UTF-8");
                                        Toast.makeText(context, url, Toast.LENGTH_LONG).show();
                                        URL url1 = new URL(url);
                                        GroupSender groupSender = new GroupSender(context, phone);
                                        groupSender.execute(url1);
                                    }
                                } else {
                                    for (int i = 0; i < num; i++) {
                                        String phone = contacts.get(i).getPhone();
                                        url = standardUrl + phone + "&msg=" + URLEncoder.encode(message, "UTF-8");
                                        Toast.makeText(context, url, Toast.LENGTH_LONG).show();
                                        URL url1 = new URL(url);
                                        GroupSender groupSender = new GroupSender(context, phone);
                                        groupSender.execute(url1);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, getString(R.string.error_sending), Toast.LENGTH_SHORT).show();
                        }
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
            } else {
                if (sender.equals("")) {
                    senderField.setError(getString(R.string.empty_field));
                }
            }
        } else {
            Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }
    }

}
