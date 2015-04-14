package info.todowonders.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import info.todowonders.activities.model.SpinnerNavItem;
import info.todowonders.adapter.DbAdapter;
import info.todowonders.info.actionbar.adapter.TitleNavigationAdapter;

public class MainActivity extends Activity implements
		ActionBar.OnNavigationListener, ActionMode.Callback {

	// action bar
	private ActionBar actionBar;

    private int selectedCount = 0;

    // Selection Mode or default mode.
    protected ActionMode mActionMode;

	// Title navigation Spinner data
	private ArrayList<SpinnerNavItem> navSpinner;

	// Navigation adapter
	private TitleNavigationAdapter adapter;

	// Refresh menu item
	private MenuItem refreshMenuItem;

    // DB Adapter
    private DbAdapter mDbHelper;

    ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		actionBar = getActionBar();

		// Hide the action bar title
		actionBar.setDisplayShowTitleEnabled(false);

		// Enabling Spinner dropdown navigation
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Spinner title navigation data
		navSpinner = new ArrayList<SpinnerNavItem>();
		navSpinner.add(new SpinnerNavItem("Local", R.drawable.ic_location));
		navSpinner
				.add(new SpinnerNavItem("My Places", R.drawable.ic_my_places));
		navSpinner.add(new SpinnerNavItem("Checkins", R.drawable.ic_checkin));
		navSpinner.add(new SpinnerNavItem("Latitude", R.drawable.ic_latitude));

		// title drop down adapter
		adapter = new TitleNavigationAdapter(getApplicationContext(),
				navSpinner);

		// assigning the spinner navigation
		actionBar.setListNavigationCallbacks(adapter, this);

        mDbHelper = new DbAdapter(this);
        mDbHelper.open();

        // Initialize ToDo list from DB.
        initializeToDoList();

		// Changing the action bar icon
		// actionBar.setIcon(R.drawable.ico_actionbar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_search:
			// search action
			return true;
		case R.id.action_location_found:
			// location found
			LocationFound();
			return true;
		case R.id.action_refresh:
			// refresh
			refreshMenuItem = item;
			// load the data from server
			new SyncData().execute();
			return true;
		case R.id.action_help:
			// help action
			return true;
		case R.id.action_check_updates:
			// check for updates action
			return true;

        case R.id.create_new_todo:
            Intent intent = new Intent(this, CreateToDo.class);
            //intent.putExtra("task_id", task_id);
            startActivity(intent);
            return true;

        case R.id.delete_todo:
            Log.e("dsaf", "sdafsaddfsfd9999");
            return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Launching new activity
	 * */
	private void LocationFound() {
		Intent i = new Intent(MainActivity.this, LocationFound.class);
		startActivity(i);
	}

	/*
	 * Actionbar navigation item select listener
	 */
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// Action to be taken after selecting a spinner item
		return false;
	}


    /*
     * Initialize the list from DB.
     */
    private void initializeToDoList() {
        // Get all of the notes from the database and create the item list
        Cursor c = mDbHelper.fetchAllNotes();
        listView = (ListView) findViewById(R.id.listView);
        final ListView lv = listView;
        startManagingCursor(c);
        String[] from = new String[] { DbAdapter.KEY_TITLE, DbAdapter.KEY_BODY };
        int[] to = new int[] { R.id.firstLineTitle, R.id.secondLineDesc };
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.todo_list, c, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lv.setAdapter(notes);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( mActionMode == null ) {
                    SQLiteCursor listItem = (SQLiteCursor)lv.getItemAtPosition(position);
                    Intent intent = new Intent(MainActivity.this, CreateToDo.class);
                    Bundle extras = intent.getExtras();
                    intent.putExtra(DbAdapter.KEY_ID, listItem.getInt(listItem.getColumnIndex(DbAdapter.KEY_ID)));
                    intent.putExtra(DbAdapter.KEY_TITLE, listItem.getString(listItem.getColumnIndex(DbAdapter.KEY_TITLE)));
                    intent.putExtra(DbAdapter.KEY_BODY, listItem.getString(listItem.getColumnIndex(DbAdapter.KEY_BODY)));
                    startActivity(intent);
                } else {
                    toggleViewSelection(view, id, position);
                }
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = MainActivity.this.startActionMode(MainActivity.this);
                toggleViewSelection(view, id, position);
                return true;
            }
        });
    }













    /*
     * ActionMode callbacks implementation
     */
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        mode.getMenuInflater().inflate(R.menu.todo_selection_mode, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        destroyActionMode();
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub

        mode.setTitle(getString(R.string.tasks_selected, selectedCount));
        return false;
    }


    private void setSelectionCount(int count) {
        if ( mActionMode != null ) {
            this.selectedCount = count;
            mActionMode.setTitle(getString(R.string.tasks_selected, selectedCount));
        }
    }

    private void toggleViewSelection(View view, long id, int position) {
        long[] checkedIds = listView.getCheckedItemIds();
        setSelectionCount(checkedIds.length);
    }


    public void deleteToDos(MenuItem menuItem) {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        for (int i = 0; i < listView.getCount(); i++) {
            if ( checked.get(i) ) {
                SQLiteCursor listItem = (SQLiteCursor)listView.getItemAtPosition(i);
                int recId = listItem.getInt(listItem.getColumnIndex(DbAdapter.KEY_ID));
                mDbHelper.deleteNote(recId);
            }
        }
        initializeToDoList();
        mActionMode.finish();
        destroyActionMode();
    }



    private void destroyActionMode() {
        mActionMode = null;
        selectedCount = 0;
        listView.invalidateViews();
        listView.clearChoices();
        for (int i = 0; i < listView.getCount(); i++) {
            listView.setItemChecked(i, false);
        }
    }





	/**
	 * Async task to load the data from server
	 * **/
	private class SyncData extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// set the progress bar view
			refreshMenuItem.setActionView(R.layout.action_progressbar);

			refreshMenuItem.expandActionView();
		}

		@Override
		protected String doInBackground(String... params) {
			// not making real request in this demo
			// for now we use a timer to wait for sometime
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			refreshMenuItem.collapseActionView();
			// remove the progress bar view
			refreshMenuItem.setActionView(null);
		}
	};

}
