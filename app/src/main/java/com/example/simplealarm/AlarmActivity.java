package com.example.simplealarm;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

public class AlarmActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags( android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
            android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(32,32,32,32);
        root.setBackgroundColor(0xFF172554);
        TextView title = new TextView(this);
        title.setText("Good morning!");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(34);
        root.addView(title);
        Button stop = new Button(this);
        stop.setText("Dismiss");
        root.addView(stop);
        Button snooze = new Button(this);
        snooze.setText("Snooze 5 minutes");
        root.addView(snooze);
        stop.setOnClickListener(v -> finish());
        snooze.setOnClickListener(v -> {
            android.app.AlarmManager am = (android.app.AlarmManager)getSystemService(ALARM_SERVICE);
            android.content.Intent i = new android.content.Intent(this, AlarmReceiver.class);
            android.app.PendingIntent pi = android.app.PendingIntent.getBroadcast(this, 1001, i,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
            long at = System.currentTimeMillis() + 5 * 60 * 1000L;
            if (android.os.Build.VERSION.SDK_INT >= 23)
                am.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, at, pi);
            else am.setExact(android.app.AlarmManager.RTC_WAKEUP, at, pi);
            finish();
        });
        setContentView(root);
    }
}
