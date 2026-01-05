package juloo.keyboard2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    private boolean isDarkMode = true;
    private LinearLayout root, container;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setupUI();
    }

    private void setupUI() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 60, 40, 40);
        
        tvTitle = new TextView(this);
        tvTitle.setText("ADVANCED SETTINGS");
        tvTitle.setTextSize(28);
        tvTitle.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        tvTitle.setPadding(20, 0, 0, 60);
        root.addView(tvTitle);

        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        root.addView(container);

        addSettingItem("Dark Mode", true);
        addSettingItem("Haptic Feedback", true);
        addSettingItem("Sound on Keypress", false);
        addSettingItem("Auto-Capitalization", true);
        addSettingItem("Typing Coach Level", true);

        applyTheme();
        setContentView(root);
    }

    private void addSettingItem(final String title, boolean enabled) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPadding(40, 40, 40, 40);
        
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(isDarkMode ? "#1E1E1E" : "#F5F5F5"));
        gd.setCornerRadius(30f);
        item.setBackground(gd);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 30);
        item.setLayoutParams(params);

        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextSize(16);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        item.addView(tv);

        Switch sw = new Switch(this);
        sw.setChecked(enabled);
        if (title.equals("Dark Mode")) {
            sw.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    isDarkMode = isChecked;
                    applyTheme();
                }
            });
        }
        item.addView(sw);

        container.addView(item);
    }

    private void applyTheme() {
        int bgColor = isDarkMode ? Color.parseColor("#121212") : Color.WHITE;
        int textColor = isDarkMode ? Color.WHITE : Color.BLACK;
        int cardColor = isDarkMode ? Color.parseColor("#1E1E1E") : Color.parseColor("#F5F5F5");

        root.setBackgroundColor(bgColor);
        tvTitle.setTextColor(Color.parseColor("#00E5FF"));

        for (int i = 0; i < container.getChildCount(); i++) {
            LinearLayout item = (LinearLayout) container.getChildAt(i);
            GradientDrawable gd = (GradientDrawable) item.getBackground();
            gd.setColor(cardColor);
            
            TextView tv = (TextView) item.getChildAt(0);
            tv.setTextColor(textColor);
        }
    }
}
