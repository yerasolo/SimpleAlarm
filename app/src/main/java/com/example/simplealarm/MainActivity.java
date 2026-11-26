package com.example.simplealarm;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.*;
import java.util.Calendar;

public class MainActivity extends Activity {
    private TimePicker timePicker;
    private TextView status;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 36, 28, 28);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(0xFFF7F8FC);

        TextView title = new TextView(this);
        title.setText("Simple Alarm");
        title.setTextSize(30);
        title.setTextColor(0xFF172554);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView hint = new TextView(this);
        hint.setText("Choose when you want to wake up");
        hint.setTextSize(16);
        hint.setPadding(0, 20, 0, 12);
        root.addView(hint);

        timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        root.addView(timePicker);

        Button set = new Button(this);
        set.setText("Set alarm");
        root.addView(set);
        status = new TextView(this);
        status.setTextSize(16);
        status.setPadding(0, 20, 0, 0);
        root.addView(status);
        set.setOnClickListener(v -> scheduleAlarm());
        setContentView(root);

        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission("android.permission.POST_NOTIFICATIONS") != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 10);
        }
        if (android.os.Build.VERSION.SDK_INT >= 31) {
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            if (!am.canScheduleExactAlarms()) {
                status.setText("Allow exact alarms in the next system screen for reliable timing.");
                Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                try { startActivity(i); } catch (Exception ignored) {}
            }
        }
    }

    private void scheduleAlarm() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        c.set(Calendar.MINUTE, timePicker.getMinute());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (c.getTimeInMillis() <= System.currentTimeMillis()) c.add(Calendar.DAY_OF_YEAR, 1);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 1001, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (android.os.Build.VERSION.SDK_INT < 31 || am.canScheduleExactAlarms())
                am.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), pi), pi);
            else {
                status.setText("Exact alarm access is not enabled. Allow it in Android Settings and try again.");
                return;
            }
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
        }
        status.setText("Alarm set for " + String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute()));
    }
}
