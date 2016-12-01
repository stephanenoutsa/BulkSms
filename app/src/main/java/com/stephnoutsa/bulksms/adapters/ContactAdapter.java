package com.stephnoutsa.bulksms.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.stephnoutsa.bulksms.R;

import java.util.List;

/**
 * Created by stephnoutsa on 12/1/16.
 */

public class ContactAdapter extends ArrayAdapter<String[]> {

    Context context;

    public ContactAdapter(Context context, List<String[]> contacts) {
        super(context, R.layout.contact_row, contacts);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.contact_row, parent, false);

        String[] contact = getItem(position);
        String sn = contact[0];
        String phone = contact[1];

        TextView snRow = (TextView) customView.findViewById(R.id.snRow);
        snRow.setText(sn);

        TextView numRow = (TextView) customView.findViewById(R.id.numRow);
        numRow.setText(phone);

        return customView;
    }
}
