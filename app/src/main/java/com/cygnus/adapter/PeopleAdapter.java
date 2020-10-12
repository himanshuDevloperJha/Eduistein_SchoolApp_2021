package com.cygnus.adapter;

import android.content.Context;
import android.provider.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.cygnus.R;
import com.cygnus.model.ChapterSpinner;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends ArrayAdapter<ChapterSpinner> {

    Context context;
    int resource, textViewResourceId;
    List<ChapterSpinner> items, tempItems, suggestions;

    public PeopleAdapter(Context context, int resource, int textViewResourceId, List<ChapterSpinner> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<ChapterSpinner>(items); // this makes the difference.
        suggestions = new ArrayList<ChapterSpinner>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_spinner_sub, parent, false);
        }
        ChapterSpinner people = items.get(position);
        if (people != null) {
            TextView lblName = (TextView) view.findViewById(R.id.tv_chh);
            if (lblName != null)
                lblName.setText(people.getStudentname()+" - "+people.getStudentRollno());
        }
        return view;
    }

}
