package com.stephnoutsa.bulksms.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.stephnoutsa.bulksms.R;
import com.stephnoutsa.bulksms.models.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephnoutsa on 11/30/16.
 */

public class MyDBHandler extends SQLiteOpenHelper {

    Context context;
    SQLiteDatabase db = null;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contacts.db";
    public static final String TABLE_CONTACTS = "contacts";
    public static final String CONTACT_COLUMN_ID = "_cid";
    public static final String CONTACT_COLUMN_PHONE = "phone";
    public static final String CONTACT_COLUMN_SENT = "sent";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        String query = "CREATE TABLE " + TABLE_CONTACTS + "(" +
                CONTACT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + ", " +
                CONTACT_COLUMN_PHONE + " TEXT " + ", " +
                CONTACT_COLUMN_SENT + " DATE " +
                ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    // Add contacts to Contacts table
    public void addContacts(Contact[] contacts) {
        for (Contact c : contacts) {
            addContact(c);
        }
    }

    // Add new contact to Contacts table
    public boolean addContact(Contact contact) {
        boolean ok;

        if (!contactExists(contact)) {
            ContentValues values = new ContentValues();

            values.put(CONTACT_COLUMN_PHONE, String.valueOf(contact.getPhone()));
            values.put(CONTACT_COLUMN_SENT, String.valueOf(contact.getSent()));

            if (db == null) {
                db = getWritableDatabase();
            }

            db.insert(TABLE_CONTACTS, null, values);

            ok = true;
        } else {
            Toast.makeText(context, context.getString(R.string.contact_exists), Toast.LENGTH_SHORT).show();

            ok = false;
        }

        return ok;
    }

    // Check if contact exists
    public boolean contactExists(Contact contact) {
        if (db == null)
            db = getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + CONTACT_COLUMN_PHONE + " = " + contact.getPhone() + ";";

        Cursor c = db.rawQuery(query, null);
        if (c == null) {
            return false;
        } else {
            if (c.getCount() == 0) {
                c.close();
                return false;
            } else {
                c.close();
                return true;
            }
        }
    }

    // Get all contacts from the CONTACTS table
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_CONTACTS + ";";

        if (db == null) {
            db = getReadableDatabase();
        }
        Cursor c = db.rawQuery(query, null);

        if (c == null) {
            return contactList;
        } else {
            c.moveToFirst();
        }

        while(!c.isAfterLast()) {
            Contact contact = new Contact();

            contact.setId(Integer.parseInt(c.getString(0)));
            contact.setPhone(c.getString(1));
            contact.setSent(c.getString(2));

            contactList.add(contact);

            c.moveToNext();
        }

        try {
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contactList;
    }

    // Get contacts to be backed up
    public List<Contact> getContactsToBackup() {
        List<Contact> contactList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + CONTACT_COLUMN_SENT + " = \'no\';";

        if (db == null) {
            db = getReadableDatabase();
        }
        Cursor c = db.rawQuery(query, null);

        if (c == null) {
            return contactList;
        } else {
            c.moveToFirst();
        }

        while(!c.isAfterLast()) {
            Contact contact = new Contact();
            contact.setId(Integer.parseInt(c.getString(0)));
            contact.setPhone(c.getString(1));
            contact.setSent(c.getString(2));

            contactList.add(contact);

            c.moveToNext();
        }

        try {
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contactList;
    }

    // Update contact sent status
    public void updateContact(Contact contact) {
        ContentValues values = new ContentValues();

        values.put(CONTACT_COLUMN_PHONE, contact.getPhone());
        values.put(CONTACT_COLUMN_SENT, "yes");

        if (db == null) {
            db = getWritableDatabase();
        }

        db.update(TABLE_CONTACTS, values, CONTACT_COLUMN_PHONE + "=\'" + contact.getPhone() + "\'", null);
    }

    // Get the contacts count
    public int getContactsCount() {
        String query = "SELECT * FROM " + TABLE_CONTACTS + ";";

        if (db == null)
            db = getReadableDatabase();

        Cursor c = db.rawQuery(query, null);

        if (c == null) {
            return 0;
        } else {
            try {
                return c.getCount();
            } finally {
                c.close();
            }
        }
    }

    // Delete a contact from the CONTACTS table
    public void deleteContact(String phone) {
        if (db == null)
            db = getWritableDatabase();

        String query = "DELETE FROM " + TABLE_CONTACTS + " WHERE " + CONTACT_COLUMN_PHONE + " =\'" + phone + "\';";

        db.execSQL(query);
    }

}
