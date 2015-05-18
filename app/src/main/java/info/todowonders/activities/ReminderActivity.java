package info.todowonders.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import info.todowonders.R;
import info.todowonders.adapter.DbAdapter;

/**
 * Created by ptanwar on 17/05/15.
 */
public class ReminderActivity extends Activity {

    // DB Adapter
    private DbAdapter mDbHelper;
    private Intent intent;
    private String title;
    private String body;
    private long taskId;
    private Dialog dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        initializeDbHelper();
        initializeTaskDetails();
        initializeDialog();
    }

    private void initializeDbHelper() {
        this.mDbHelper = new DbAdapter(this);
        this.mDbHelper.open();
    }


    private void initializeTaskDetails() {
        Bundle intentExtras = this.intent.getExtras();
        this.taskId = intent.getExtras().getLong(DbAdapter.KEY_ID);
        Cursor rowCursor = mDbHelper.fetchNote(taskId);
        this.title = rowCursor.getString(rowCursor.getColumnIndex(DbAdapter.KEY_TITLE));
        this.body = rowCursor.getString(rowCursor.getColumnIndex(DbAdapter.KEY_BODY));
    }


    private void initializeDialog() {
        dlg = new Dialog(this);
        dlg.setContentView(R.layout.reminder_dialog);
        dlg.setTitle(R.string.reminderLbl);
        ((TextView)dlg.findViewById(R.id.title)).setText(this.title);
        ((TextView)dlg.findViewById(R.id.desc)).setText(this.body);
        dlg.show();
    }
}
