package com.example.user.cockroachtest1;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //public SharedPreferences sp;
    public static SharedPreferences sPref;

    private static Integer counter = 0;
    private static TextView counterView;
    private static String savedText;

    private ShareActionProvider shareActionProvider = null;
    public static final String COUNTER = "counter";
    final String UPDATE_ALL_WIDGETS = "update_all_widgets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sPref = getPreferences(MODE_PRIVATE);
        //sp = PreferenceManager.getDefaultSharedPreferences(this);

        /**опрокидование уведомления
         *

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartNotify();
            }
        });*/


        FloatingActionButton oneup = (FloatingActionButton) findViewById(R.id.oneup);
        counterView = (TextView) findViewById(R.id.counter);
        oneup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                counterView.setText(counter.toString());
                Snackbar.make(view, "Killed one more!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                if (counter == 100000) {
                    itsOverNineThousand();
                    counter = 0;
                    counterView.setText("0");
                }
                saveText();

                Intent intent = new Intent(getApplicationContext(), MyWidget.class);
                intent.setAction(UPDATE_ALL_WIDGETS);
                PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                        .getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pIntent);
            }
        });
        loadText();
    }

    /**
     * сохранение состояния
     */
    @Override
    public void onPause() {
        saveText();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return (super.onCreateOptionsMenu(menu));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            item.setIntent(new Intent(this, PrefActivity.class));
        }
        if (id == R.id.menu_item_share) {
            shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            startActivity(createShareIntent());
        }
        if (id == R.id.about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * отправка уведомления
     */
    private void restartNotify() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TimeNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, 0);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, pendingIntent);
        Toast.makeText(getApplicationContext(), "started?", Toast.LENGTH_SHORT).show();
    }

    /**
     * сохранение состояния счетчика
     */
    private void saveText() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(COUNTER, counterView.getText().toString());
        ed.apply();
    }


    /**
     * загрузка состояния счетчика
     */
    private void loadText() {
        //sPref = getPreferences(MODE_PRIVATE);
        savedText = sPref.getString(COUNTER, "");
        if (savedText.equals("")) {
            counter = 0;
            counterView.setText(counter.toString());
        } else {
            counterView.setText(savedText);
            counter = Integer.valueOf(savedText);
        }
    }

    /**
     * проверка на 100.000
     * и выведение  Alert Dialog
     */
    private void itsOverNineThousand() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("You got this!");
        builder.setMessage("You achieved 100.000 victims, you are monster.\n" +
                "Start over from the beginning.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * установка интента с текстом и отправка его
     *
     * @return Intent
     */
    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "I killed " + getCounter() + " cockroaches. \n" +
                        "Shared via cockroach killer. \n" + "Join us in this war.");
        return shareIntent;
    }

    /**
     * установка счетчика
     *
     * @param count
     */
    public static void setCounter(Integer count) {
        counter = count;
        counterView.setText(counter.toString());
        try {
            new MainActivity().saveText();
        } catch (Exception e) {

        }
    }

    public Integer getCounter() {
        return counter;
    }
}
