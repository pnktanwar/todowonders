package info.todowonders.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import info.todowonders.activities.ReminderActivity;
import info.todowonders.adapter.DbAdapter;


/**
 * Created by ptanwar on 16/05/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        long taskId = intent.getExtras().getLong(DbAdapter.KEY_ID);
        //Toast.makeText(ctx, "I'm running : " + taskId, Toast.LENGTH_LONG).show();
        Log.e("Received HERE", "Received alarm here : " + taskId);
        Intent reminderActivityIntent = new Intent(ctx, ReminderActivity.class);
        reminderActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle extras = reminderActivityIntent.getExtras();
        reminderActivityIntent.putExtra(DbAdapter.KEY_ID, taskId);
        ctx.startActivity(reminderActivityIntent);
    }
}