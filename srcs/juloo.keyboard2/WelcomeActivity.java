package juloo.keyboard2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WelcomeActivity extends Activity {

    private boolean isDarkMode = true;
    private LinearLayout root;
    private TextView tvTitle, tvSub;
    private Button btnStart, btnSettings, btnToggle;

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
        root.setGravity(Gravity.CENTER);
        root.setPadding(60, 60, 60, 60);
        
        applyTheme();

        tvTitle = new TextView(this);
        tvTitle.setText("TYPING\nMASTER PRO");
        tvTitle.setTextSize(42);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD));
        root.addView(tvTitle);

        tvSub = new TextView(this);
        tvSub.setText("ELITE MOBILE COACH SYSTEM");
        tvSub.setTextSize(14);
        tvSub.setLetterSpacing(0.2f);
        tvSub.setPadding(0, 20, 0, 100);
        root.addView(tvSub);

        btnStart = createStyledButton("START TRAINING", true);
        btnStart.setOnClickListener(v -> startActivity(new Intent(this, TypingMasterActivity.class)));
        root.addView(btnStart);

        btnSettings = createStyledButton("SETTINGS", false);
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        root.addView(btnSettings);

        btnToggle = new Button(this);
        btnToggle.setText("TOGGLE THEME");
        btnToggle.setOnClickListener(v -> {
            isDarkMode = !isDarkMode;
            applyTheme();
        });
        root.addView(btnToggle);

        setContentView(root);
    }

    private void applyTheme() {
        int bgColor = isDarkMode ? Color.parseColor("#121212") : Color.WHITE;
        int accentColor = Color.parseColor("#00E5FF");
        int textColor = isDarkMode ? Color.WHITE : Color.BLACK;

        root.setBackgroundColor(bgColor);
        if (tvTitle != null) tvTitle.setTextColor(accentColor);
        if (tvSub != null) tvSub.setTextColor(isDarkMode ? Color.GRAY : Color.DKGRAY);
        if (btnStart != null) updateButtonStyle(btnStart, true);
        if (btnSettings != null) updateButtonStyle(btnSettings, false);
    }

    private Button createStyledButton(String text, boolean primary) {
        Button b = new Button(this);
        b.setText(text);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 160);
        params.setMargins(0, 20, 0, 20);
        b.setLayoutParams(params);
        updateButtonStyle(b, primary);
        return b;
    }

    private void updateButtonStyle(Button b, boolean primary) {
        GradientDrawable gd = new GradientDrawable();
        if (primary) {
            gd.setColor(Color.parseColor("#00E5FF"));
            b.setTextColor(Color.BLACK);
        } else {
            gd.setColor(Color.TRANSPARENT);
            gd.setStroke(3, Color.parseColor(isDarkMode ? "#333333" : "#DDDDDD"));
            b.setTextColor(isDarkMode ? Color.WHITE : Color.BLACK);
        }
        gd.setCornerRadius(80f);
        b.setBackground(gd);
    }
}
