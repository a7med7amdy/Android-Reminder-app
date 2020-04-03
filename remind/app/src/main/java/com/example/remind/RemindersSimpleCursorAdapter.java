package com.example.remind;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

/**
 * Created by engMa_000 on 2017-04-03.
 */

public class RemindersSimpleCursorAdapter extends CursorAdapter {

    public RemindersSimpleCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }
    //to use a viewholder, you must override the following two methods and define a ViewHolder class
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.reminders_row,parent,false);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView conentView=(TextView)view.findViewById(R.id.text_content);
        int contentIndex=cursor.getColumnIndex(RemindersDbAdapter.COL_CONTENT);
        String content =cursor.getString(contentIndex);
        conentView.setText(content);
        int importantIndex = cursor.getColumnIndexOrThrow(RemindersDbAdapter.COL_IMPORTANT);
        if (cursor.getInt(importantIndex) > 0) {
            view.setBackgroundColor(ContextCompat.getColor(context,R.color.pinky));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context,R.color.black));
        }
    }


}