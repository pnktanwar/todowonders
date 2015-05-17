package info.todowonders.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import info.todowonders.R;
import info.todowonders.adapter.DbAdapter;
import info.todowonders.receivers.AlarmReceiver;

/**
 * Created by ptanwar on 12/04/15.
 */
public class CreateToDo extends Activity implements ActionBar.OnNavigationListener {

    // action bar
    private ActionBar actionBar;

    // DB Adapter
    private DbAdapter mDbHelper;

    private int taskId = -1;

    static final int TIME_DIALOG_ID = 999;

    private CheckBox chkDateTimeReminder;
    private Dialog dateTimeDialog;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_todo);
        alarmManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
        initializeDateTimeDialog();
        addListenerForSettingDateTime();
        Intent intent = getIntent();
        this.taskId = intent.getIntExtra(DbAdapter.KEY_ID, -1);
        Bundle intentExtras = intent.getExtras();
        if ( intentExtras != null ) {
            String taskTitle = intent.getExtras().getString(DbAdapter.KEY_TITLE);
            String taskDesc = intent.getExtras().getString(DbAdapter.KEY_BODY);
            String reminderAt = intent.getExtras().getString(DbAdapter.KEY_REMINDER_AT);

            if (this.taskId != -1) {
                Log.e("Task Id", "Task Id = " + this.taskId);
                TextView titleView = (TextView) findViewById(R.id.titleText);
                TextView descView = (TextView) findViewById(R.id.descText);
                titleView.setText(taskTitle);
                descView.setText(taskDesc);
                setReminderDateTime(reminderAt);
            }
        }

        actionBar = getActionBar();

        // Hide the action bar title
        actionBar.setDisplayShowTitleEnabled(true);

        actionBar.setDisplayHomeAsUpEnabled(true);

        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
    }


    private void initializeDateTimeDialog() {
        dateTimeDialog = new Dialog(this);
        dateTimeDialog.setContentView(R.layout.date_time_picker);
        dateTimeDialog.setTitle(R.string.dateTimeDialogTitle);
        dateTimeDialog.setOnDismissListener(new Dialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setReminderDateTimeLbl();
            }
        });
        //DatePicker dp = (DatePicker)dateTimeDialog.findViewById(R.id.datePickerDlg);
        //TimePicker tp = (TimePicker)dateTimeDialog.findViewById(R.id.timePickerDlg);
        //Calendar curr = Calendar.getInstance();
        //dp.init(curr.get(Calendar.YEAR), curr.get(Calendar.MONTH)+1, curr.get(Calendar.DAY_OF_MONTH), datePickerListener);
        //tp.setOnTimeChangedListener(timePickerListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_create_todo_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Save the ToDo data.
     */
    private void saveToDoItem() {
        TextView titleView = (TextView)findViewById(R.id.titleText);
        TextView descView = (TextView)findViewById(R.id.descText);
        String title = titleView.getText().toString();
        String desc = descView.getText().toString();
        Calendar reminderAt = getReminderDateTime();
        boolean isScheduled = chkDateTimeReminder.isChecked();
        if ( title.trim().length() == 0 ) {
            Toast.makeText(getBaseContext(), getString(R.string.title_field_req), Toast.LENGTH_LONG).show();
        } else {
            long retVal = -1;
            if ( this.taskId != -1 ) {
                retVal = mDbHelper.updateNote(this.taskId, title, desc, reminderAt) ? 0 : -1;
            } else {
                retVal = mDbHelper.createNote(title, desc, reminderAt);
            }
            if ( retVal == -1 ) {
                // Save failed case.
                Toast.makeText(getBaseContext(), getString(R.string.create_todo_error), Toast.LENGTH_LONG).show();
            } else {
                // Save successful.
                Toast.makeText(getBaseContext(), getString(R.string.create_todo_success), Toast.LENGTH_LONG).show();
                titleView.setText("");
                descView.setText("");
                chkDateTimeReminder.setChecked(false);
                TextView remindAtLbl = ((TextView) findViewById(R.id.remind_at_lbl));
                remindAtLbl.setText("");
                if ( isScheduled ) {
                    scheduleAlarm(this.taskId == -1 ? retVal : this.taskId, reminderAt);
                }
                this.taskId = -1;
            }
        }
    }


    private void scheduleAlarm(long taskId, Calendar reminderAt) {
        Intent alarmIntent = new Intent(CreateToDo.this, AlarmReceiver.class);
        Bundle extras = alarmIntent.getExtras();
        alarmIntent.putExtra(DbAdapter.KEY_ID, taskId);
        PendingIntent pi = PendingIntent.getBroadcast( this, 0, alarmIntent, 0 );
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderAt.getTimeInMillis(), pi);
    }


    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_create_todo:
                saveToDoItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
	 * Actionbar navigation item select listener
	 */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        // Action to be taken after selecting a spinner item
        return false;
    }


    private void addListenerForSettingDateTime() {
        chkDateTimeReminder = (CheckBox) findViewById(R.id.chkDateTimeReminder);
        chkDateTimeReminder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox chk = ((CheckBox) v);
                TextView remindAtLbl = ((TextView) findViewById(R.id.remind_at_lbl));
                if (chk.isChecked()) {
                    dateTimeDialog.show();
                } else {
                    remindAtLbl.setText("");
                }
            }
        });
    }

    private Calendar getReminderDateTime() {
        if ( chkDateTimeReminder.isChecked() ) {
            DatePicker dp = (DatePicker)dateTimeDialog.findViewById(R.id.datePickerDlg);
            TimePicker tp = (TimePicker)dateTimeDialog.findViewById(R.id.timePickerDlg);
            return new GregorianCalendar(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
        }
        return null;
    }


    private void setReminderDateTime(String calStr) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        TextView remindAtLbl = ((TextView) findViewById(R.id.remind_at_lbl));
        try {
            cal.setTime(sdf.parse(calStr));
        } catch(ParseException ex) {
            return;
        }
        if ( cal != null ) {
            DatePicker dp = (DatePicker) dateTimeDialog.findViewById(R.id.datePickerDlg);
            TimePicker tp = (TimePicker) dateTimeDialog.findViewById(R.id.timePickerDlg);
            dp.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            tp.setCurrentHour(cal.get(Calendar.HOUR));
            tp.setCurrentMinute(cal.get(Calendar.MINUTE));
            remindAtLbl.setText(calStr);
            chkDateTimeReminder.setChecked(true);
        } else {
            remindAtLbl.setText("");
        }
    }


    private void setReminderDateTimeLbl() {
        Calendar cal = getReminderDateTime();
        TextView remindAtLbl = ((TextView) findViewById(R.id.remind_at_lbl));
        remindAtLbl.setText(cal.getTime().toString());
    }
}