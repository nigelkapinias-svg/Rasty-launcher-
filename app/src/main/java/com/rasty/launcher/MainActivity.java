package com.rasty.launcher;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.content.pm.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    private LinearLayout root, appList;
    private int cyan = Color.rgb(36,215,255);
    private int white = Color.WHITE;
    private int bg = Color.rgb(7,11,24);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        showHome();
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(radius); return d;
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setPadding(4, 5, 4, 5);
        return t;
    }

    private void base(String title) {
        ScrollView scroll = new ScrollView(this);
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 28, 20, 24); root.setBackgroundColor(bg);
        scroll.addView(root); setContentView(scroll);
        TextView brand = text("RASTY", 30, cyan); brand.setTypeface(null, Typeface.BOLD);
        root.addView(brand);
        root.addView(text(title, 15, Color.LTGRAY));
    }

    private void tile(String label, String sub, Runnable action) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL); card.setPadding(16, 13, 16, 13);
        card.setBackground(shape(Color.rgb(19,29,52), 22));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 9, 0, 3); root.addView(card, p);
        TextView h = text(label, 18, white); h.setTypeface(null, Typeface.BOLD);
        card.addView(h); card.addView(text(sub, 13, Color.LTGRAY));
        card.setOnClickListener(v -> action.run());
    }

    private void showHome() {
        base("GAMING LAUNCHER  •  YOUR ANDROID, YOUR STYLE");
        TextView clock = text(new java.text.SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(new Date()), 16, white);
        root.addView(clock);
        tile("🎮  Gaming tools", "Quick settings and game session tips", () -> showTools());
        tile("🌌  Wallpapers & themes", "Choose a wallpaper for your device", () -> {
            Intent i = new Intent(Intent.ACTION_SET_WALLPAPER);
            try { startActivity(i); } catch(Exception e) { toast("Wallpaper picker unavailable"); }
        });
        tile("🎵  Music player", "Choose music from your device", () -> {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT); i.setType("audio/*");
            try { startActivityForResult(i, 21); } catch(Exception e) { toast("No music picker found"); }
        });
        tile("🔒  Lock-screen style", "Preview a Rasty-inspired lock screen", () -> showLockPreview());
        tile("📲  Share Rasty", "Tell friends and family about the launcher", () -> {
            Intent s = new Intent(Intent.ACTION_SEND); s.setType("text/plain");
            s.putExtra(Intent.EXTRA_TEXT, "Check out Rasty Launcher — my gaming-style Android home screen!");
            startActivity(Intent.createChooser(s, "Share Rasty Launcher"));
        });
        TextView heading = text("YOUR APPS", 17, cyan); heading.setTypeface(null, Typeface.BOLD);
        root.addView(heading);
        appList = new LinearLayout(this); appList.setOrientation(LinearLayout.VERTICAL); root.addView(appList);
        loadApps();
        tile("↻  Refresh apps", "Reload your installed applications", () -> loadApps());
        root.addView(text("Rasty Launcher • Version 1.0", 12, Color.GRAY));
    }

    private void loadApps() {
        appList.removeAllViews();
        PackageManager pm = getPackageManager();
        Intent query = new Intent(Intent.ACTION_MAIN, null); query.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(query, 0);
        Collections.sort(apps, (a,b) -> String.valueOf(a.loadLabel(pm)).compareToIgnoreCase(String.valueOf(b.loadLabel(pm))));
        for (ResolveInfo info : apps) {
            String name = String.valueOf(info.loadLabel(pm));
            String pkg = info.activityInfo.packageName;
            LinearLayout row = new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(12, 9, 12, 9); row.setBackground(shape(Color.rgb(15,22,40), 14));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2); p.setMargins(0,4,0,4); appList.addView(row,p);
            ImageView icon = new ImageView(this); icon.setImageDrawable(info.loadIcon(pm));
            row.addView(icon, new LinearLayout.LayoutParams(42,42));
            TextView label = text(name, 16, white); label.setPadding(12,4,4,4); row.addView(label, new LinearLayout.LayoutParams(0,-2,1));
            row.setOnClickListener(v -> {
                Intent launch = pm.getLaunchIntentForPackage(pkg);
                if (launch != null) startActivity(launch);
            });
        }
    }

    private void showTools() {
        new AlertDialog.Builder(this).setTitle("Rasty Gaming Tools")
            .setItems(new String[]{"Open display settings","Open sound settings","Open battery settings","Open Wi-Fi settings","Gaming tip: close unused apps before playing"}, (d, which) -> {
                if (which == 4) { toast("Tip: close background apps and enable Do Not Disturb before gaming."); return; }
                String[] actions = {android.provider.Settings.ACTION_DISPLAY_SETTINGS, android.provider.Settings.ACTION_SOUND_SETTINGS, android.provider.Settings.ACTION_BATTERY_SAVER_SETTINGS, android.provider.Settings.ACTION_WIFI_SETTINGS};
                try { startActivity(new Intent(actions[which])); } catch(Exception e) { toast("Setting unavailable on this device"); }
            }).show();
    }

    private void showLockPreview() {
        base("RASTY • LOCK SCREEN PREVIEW");
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.addView(text(new java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()), 64, white));
        root.addView(text(new java.text.SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(new Date()), 18, cyan));
        root.addView(text("Swipe-style preview", 14, Color.LTGRAY));
        tile("← Back to Rasty home", "This is a visual preview only", () -> showHome());
        root.addView(text("Note: Android controls the real secure lock screen. Rasty cannot replace your phone's security lock screen from this preview.", 13, Color.LTGRAY));
    }

    private void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_LONG).show(); }
    @Override public void onBackPressed() { showHome(); }
}