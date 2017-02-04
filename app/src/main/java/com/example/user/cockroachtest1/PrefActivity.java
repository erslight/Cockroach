package com.example.user.cockroachtest1;

/**
 * Created by User on 26.01.2017.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;

public class PrefActivity extends PreferenceActivity {

    final String UPDATE_ALL_WIDGETS = "update_all_widgets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        Preference button = (Preference) getPreferenceManager().findPreference("reset");
        if (button != null) {
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    MainActivity.setCounter(0);
                    MainActivity.sPref.edit().putString(
                            MainActivity.COUNTER, "0").apply();
                    Intent intent = new Intent(getApplicationContext(), MyWidget.class);
                    intent.setAction(UPDATE_ALL_WIDGETS);
                    PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                            .getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pIntent);
                    finish();
                    return true;
                }
            });
        }

    }
}