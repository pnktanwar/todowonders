package info.todowonders.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import info.todowonders.adapter.DbAdapter;

/**
 * Created by ptanwar on 12/04/15.
 */
public class CreateToDo extends Activity implements ActionBar.OnNavigationListener {

    // action bar
    private ActionBar actionBar;

    // DB Adapter
    private DbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_todo);

        actionBar = getActionBar();

        // Hide the action bar title
        actionBar.setDisplayShowTitleEnabled(false);

        // Enabling Spinner dropdown navigation
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

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
        mDbHelper.createNote(title, desc);
    }


    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
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
