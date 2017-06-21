package com.smartmanageragent.application;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;


class MeetingArrayAdapter extends ArrayAdapter<Map<String, String>> {


    private Context context;
    private List<Map<String, String>> meetings;

    //constructor, call on creation
    MeetingArrayAdapter(Context context, int resource, List<Map<String, String>> p_meetings) {
        super(context, resource, p_meetings);

        this.context = context;
        this.meetings = p_meetings;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Map<String, String> meetingTest = meetings.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // TODO see about View Holder pattern
        View view = inflater.inflate(R.layout.meeting_layout, null);

        TextView description = (TextView) view.findViewById(R.id.frag_description);
        TextView attendees = (TextView) view.findViewById(R.id.frag_attendees);
        TextView date = (TextView) view.findViewById(R.id.frag_date);

        description.setText(meetingTest.get("description"));
        attendees.setText(meetingTest.get("attendees"));
        date.setText(meetingTest.get("date"));

        return view;
    }
}