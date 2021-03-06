package com.example.remind;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    static public int reminderId=0;
    private int checked=0;
    private String reminderText;
    private RemindersDbAdapter DB;
    private RemindersSimpleCursorAdapter RS;
    long updatedDeletedId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DB = new RemindersDbAdapter(MainActivity.this);
        DB.open();
        Cursor cursor1 = DB.fetchAllReminders();
        RS = new RemindersSimpleCursorAdapter(MainActivity.this, cursor1);
        RS.changeCursor(cursor1);
        final ListView list = findViewById(R.id.list);
        list.setAdapter(RS);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                    Reminder item = DB.fetchReminderById((int)id);
//                    final String content = item.getContent();
//                    final int isImportant = item.getImportant();

                    //if item in list view is clicked
                    final Cursor cursor = (Cursor) list.getItemAtPosition(position);
                    final String clickedItem = cursor.getString(1);
                    updatedDeletedId= id;
                    View mview = getLayoutInflater().inflate(R.layout.dialog_custom_delete_edit,null);
                    final ListView delete_edit_list = mview.findViewById(R.id.delete_edit_list);

                    //creating the alert dialog of edit, delete
                    AlertDialog.Builder mbuilderEditDelet= new AlertDialog.Builder(MainActivity.this);
                    ArrayAdapter<String> delete_edit_arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, new String[]{"Edit","Delete"});
                    delete_edit_list.setAdapter(delete_edit_arrayAdapter);
                    delete_edit_arrayAdapter.notifyDataSetChanged();
                    mbuilderEditDelet.setView(mview);

                    final AlertDialog dialog2= mbuilderEditDelet.create();
                    dialog2.show();

                    delete_edit_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
                            dialog2.dismiss();
                            String itemSelected = (String) delete_edit_list.getItemAtPosition(position1);
                            //choose from dialog
                            if (itemSelected == "Edit") {
                                AlertDialog.Builder mbuilderEdit= new AlertDialog.Builder(MainActivity.this);
                                View mview = getLayoutInflater().inflate(R.layout.custom_dialog,null);
                                mbuilderEdit.setView(mview);

                                final AlertDialog dialog3= mbuilderEdit.create();
                                final EditText reminder= mview.findViewById(R.id.ReminderText);
                                reminder.setText(clickedItem);
                                CheckBox important= mview.findViewById(R.id.importantcheck);

        //                        if(isImportant == 1)
        //                            important.setChecked(true);
        //                        else
       //                             important.setChecked(false);


                                //if the item wanna be edited is important, set its checker
                                int importantIndex = cursor.getColumnIndexOrThrow(RemindersDbAdapter.COL_IMPORTANT);
                                if(cursor.getInt(importantIndex) > 0) {
                                    important.setChecked(true);
                                    checked=1;
                                }

                                Button cancel= mview.findViewById(R.id.cancelbutton);
                                Button commit=mview.findViewById(R.id.commitbutton);
                                dialog3.show();
                                TextView tv2= (TextView) mview.findViewById(R.id.editreminderview);
                                tv2.setVisibility(View.VISIBLE);

                                important.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                         @Override
                                         public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                             if(isChecked){
                                                 checked=1;
                                             }
                                             else
                                                 checked=0;
                                         }
                                     }
                                );

                                reminder.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        reminderText=s.toString();
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                    }
                                });

                                commit.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        if(reminder.getText().length() > 0) {


                                            Reminder RE = new Reminder((int)updatedDeletedId , reminderText, checked);
                                            checked=0;
                                            DB.updateReminder(RE);
                                            dialog3.dismiss();

                                            Cursor cursor_edit = DB.fetchAllReminders();
                                            RS.changeCursor(cursor_edit);
                                            RS.notifyDataSetChanged();
                                            ListView list = findViewById(R.id.list);
                                            list.setAdapter(RS);





                                        }
                                        else{
                                            System.out.print(reminderText);
                                            Toast.makeText(MainActivity.this,"Error: no entered reminders",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        reminderText=null;
                                        checked=0;
                                        dialog3.dismiss();
                                    }
                                });

                            }
                            else {
                                // he chooses delete


                                    DB.deleteReminderById((int)updatedDeletedId);
                                   // dialog.dismiss();
                                    Cursor cursor_d = DB.fetchAllReminders();
                                    RS.changeCursor(cursor_d);
                                    ListView list = findViewById(R.id.list);
                                    list.setAdapter(RS);

                            }
                        }
                    });

                }
            });


///////////////////////////////////////////////////////////////////////////////////////
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == R.id.action_newReminder) {
//            return true;
            AlertDialog.Builder mbuilder= new AlertDialog.Builder(MainActivity.this);
            View mview = getLayoutInflater().inflate(R.layout.custom_dialog,null);
            mbuilder.setView(mview);

            final AlertDialog dialog= mbuilder.create();



            dialog.show();
            final EditText reminder= mview.findViewById(R.id.ReminderText);
            CheckBox important= mview.findViewById(R.id.importantcheck);
            Button cancel= mview.findViewById(R.id.cancelbutton);
            Button commit=mview.findViewById(R.id.commitbutton);

            //make text view 1 visible
            TextView tv1= (TextView) mview.findViewById(R.id.reminderview);
            tv1.setVisibility(View.VISIBLE);

            important.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if(isChecked){
                        checked=1;
                    }
               }
           }
            );

            reminder.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    reminderText=s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            commit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(reminder.getText().length() > 0) {
                        Reminder rem = new Reminder(reminderId, reminderText, checked);
                        checked=0;
                        reminderId++;

                        DB.createReminder(rem);

                        dialog.dismiss();
                        Cursor cursor_add = DB.fetchAllReminders();

                        RS.changeCursor(cursor_add);
                        ListView list = findViewById(R.id.list);
                        list.setAdapter(RS);

                    }
                    else{
                        Toast.makeText(MainActivity.this,"Error: no entered reminders",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reminderText=null;
                    checked=0;
                    dialog.dismiss();
                }
            });
        }
        if (id == R.id.action_exit) {
            finish();
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCheckboxClicked(View view) {
    }
}