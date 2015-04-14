package info.todowonders.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import info.todowonders.adapter.DbAdapter;

/**
 * Created by ptanwar on 12/04/15.
 */
public class CreateToDo extends Activity implements ActionBar.OnNavigationListener {

    // action bar
    private ActionBar actionBar;

    // DB Adapter
    private DbAdapter mDbHelper;

    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_todo);
        Intent intent = getIntent();
        this.taskId = intent.getIntExtra(DbAdapter.KEY_ID, -1);
        Bundle intentExtras = intent.getExtras();
        if ( intentExtras != null ) {
            String taskTitle = intent.getExtras().getString(DbAdapter.KEY_TITLE);
            String taskDesc = intent.getExtras().getString(DbAdapter.KEY_BODY);

            if (this.taskId != -1) {
                TextView titleView = (TextView) findViewById(R.id.titleText);
                TextView descView = (TextView) findViewById(R.id.descText);
                titleView.setText(taskTitle);
                descView.setText(taskDesc);
            }
        }

        actionBar = getActionBar();

        // Hide the action bar title
        actionBar.setDisplayShowTitleEnabled(true);

        actionBar.setDisplayHomeAsUpEnabled(true);

        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
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
        if ( title.trim().length() == 0 ) {
            Toast.makeText(getBaseContext(), getString(R.string.title_field_req), Toast.LENGTH_LONG).show();
        } else {
            long retVal = -1;
            if ( this.taskId != -1 ) {
                retVal = mDbHelper.updateNote(this.taskId, title, desc) ? 0 : -1;
            } else {
                retVal = mDbHelper.createNote(title, desc);
            }
            if ( retVal == -1 ) {
                // Save failed case.
                Toast.makeText(getBaseContext(), getString(R.string.create_todo_error), Toast.LENGTH_LONG).show();
            } else {
                // Save successful.
                Toast.makeText(getBaseContext(), getString(R.string.create_todo_success), Toast.LENGTH_LONG).show();
                this.taskId = -1;
                titleView.setText("");
                descView.setText("");
            }
        }
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
}
