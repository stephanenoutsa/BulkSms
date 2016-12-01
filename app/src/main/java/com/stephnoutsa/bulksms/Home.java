package com.stephnoutsa.bulksms;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.stephnoutsa.bulksms.models.Contact;
import com.stephnoutsa.bulksms.utils.BulkSmsService;
import com.stephnoutsa.bulksms.utils.MyDBHandler;
import com.stephnoutsa.bulksms.utils.RetrofitHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context = this;
    MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
    Contact[] contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void onClickSend(View view) {
        Intent i = new Intent(this, SendMessages.class);
        startActivity(i);
    }

    public void onClickAdd(View view) {
        Intent i = new Intent(this, AddContact.class);
        startActivity(i);
    }

    public void onClickRetrieve(View view) {
        // Check if user is connected
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    /** Get contacts from server */
                    RetrofitHandler handler = new RetrofitHandler();

                    BulkSmsService smsService = handler.create();

                    Call<Contact[]> call = smsService.getAllContacts();
                    call.clone().enqueue(new retrofit2.Callback<Contact[]>() {
                        @Override
                        public void onResponse(Call<Contact[]> call, Response<Contact[]> response) {
                            int statusCode = response.code();
                            if (statusCode == 200) {
                                // Retrieve the contacts
                                contacts = response.body();

                                // Notify user
                                if (contacts.length != 0) {
                                    Toast.makeText(context, getString(R.string.retrieve_ok), Toast.LENGTH_SHORT).show();

                                    // Add the contacts to local database
                                    dbHandler.addContacts(contacts);
                                } else {
                                    Toast.makeText(context, getString(R.string.no_contacts), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, getString(R.string.server_failure), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Contact[]> call, Throwable t) {
                            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
        } else {
            Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickBackup(View view) {
        // Check if user is connected
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // Retrieve contacts to be backed up from local database
                    List<Contact> contactList = dbHandler.getContactsToBackup();
                    contacts = contactList.toArray(new Contact[contactList.size()]);

                    if (contactList.isEmpty()) {
                        Toast.makeText(context, getString(R.string.up_to_date), Toast.LENGTH_SHORT).show();
                    } else {
                        /** Add contacts to server */
                        RetrofitHandler handler = new RetrofitHandler();

                        BulkSmsService smsService = handler.create();

                        Call<Contact[]> call = smsService.addContacts(contacts);
                        call.clone().enqueue(new retrofit2.Callback<Contact[]>() {
                            @Override
                            public void onResponse(Call<Contact[]> call, Response<Contact[]> response) {
                                int statusCode = response.code();
                                if (statusCode == 200) {
                                    for (Contact c : contacts) {
                                        dbHandler.updateContact(c);
                                    }

                                    Toast.makeText(context, getString(R.string.backup_ok), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, getString(R.string.server_failure), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Contact[]> call, Throwable t) {
                                Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            }
                        });
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
            Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_send) {
            Intent i = new Intent(this, SendMessages.class);
            startActivity(i);
        } else if (id == R.id.nav_contacts) {
            Intent i = new Intent(this, Contacts.class);
            startActivity(i);
        } else if (id == R.id.nav_check) {
            String url = getString(R.string.balance_url);
            Uri uriUrl = Uri.parse(url);
            Intent i = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
