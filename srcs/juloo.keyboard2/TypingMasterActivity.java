package juloo.keyboard2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Random;

public class TypingMasterActivity extends Activity {

    private TextView tvTarget, tvStats, tvLevelInfo, tvHeader;
    private EditText etInput;
    private int currentLevel = 1;
    private int wordsTyped = 0;
    private long startTime = 0;
    private String currentTargetWord = "";
    private int mistakes = 0;
    private boolean isTestRunning = false;

    private static final String ACCENT_COLOR = "#00E5FF";
    private static final String BG_COLOR = "#121212";
    private static final String CARD_BG = "#1E1E1E";

    private static final String[][] LEVEL_WORDS = {
        {"the", "be", "to", "of", "and", "in", "that", "have"},
        {"rhythm", "flow", "typing", "master", "speed", "quick"},
        {"complex", "challenge", "accuracy", "practice", "mobile"},
        {"performance", "consistency", "neuroscience", "keyboard"},
        {"competitive", "excellence", "professional", "optimization"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen dark theme
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setupAdvancedUI();
    }

    private void setupAdvancedUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor(BG_COLOR));
        root.setPadding(40, 60, 40, 40);

        // Advanced Header with Neon Glow effect simulation
        tvHeader = new TextView(this);
        tvHeader.setText("TYPING MASTER PRO");
        tvHeader.setTextColor(Color.parseColor(ACCENT_COLOR));
        tvHeader.setTextSize(26);
        tvHeader.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        tvHeader.setGravity(Gravity.CENTER);
        tvHeader.setPadding(0, 0, 0, 40);
        root.addView(tvHeader);

        // Level Status Card
        LinearLayout levelCard = createStyledCard();
        tvLevelInfo = new TextView(this);
        tvLevelInfo.setTextColor(Color.WHITE);
        tvLevelInfo.setTextSize(14);
        tvLevelInfo.setGravity(Gravity.CENTER);
        updateLevelDisplay();
        levelCard.addView(tvLevelInfo);
        root.addView(levelCard);

        // Stats Bar
        LinearLayout statsBar = new LinearLayout(this);
        statsBar.setOrientation(LinearLayout.HORIZONTAL);
        statsBar.setGravity(Gravity.CENTER);
        statsBar.setPadding(0, 40, 0, 40);
        
        tvStats = new TextView(this);
        tvStats.setTextColor(Color.parseColor("#888888"));
        tvStats.setTextSize(16);
        tvStats.setText("SPEED: 0 WPM  |  ACC: 100%");
        statsBar.addView(tvStats);
        root.addView(statsBar);

        // Main Target Area (Neon Cyan)
        tvTarget = new TextView(this);
        tvTarget.setTextColor(Color.parseColor(ACCENT_COLOR));
        tvTarget.setTextSize(48);
        tvTarget.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        tvTarget.setGravity(Gravity.CENTER);
        tvTarget.setPadding(0, 80, 0, 80);
        root.addView(tvTarget);

        // Input Field with Custom Design
        etInput = new EditText(this);
        etInput.setHint("TYPE WORD HERE");
        etInput.setHintTextColor(Color.parseColor("#444444"));
        etInput.setTextColor(Color.WHITE);
        etInput.setGravity(Gravity.CENTER);
        etInput.setTextSize(20);
        etInput.setBackground(createInputDrawable());
        etInput.setPadding(40, 40, 40, 40);
        
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputParams.setMargins(20, 40, 20, 80);
        etInput.setLayoutParams(inputParams);
        root.addView(etInput);

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Advanced Control Button
        Button btnRestart = new Button(this);
        btnRestart.setText("RESET TRAINING");
        btnRestart.setTextColor(Color.BLACK);
        btnRestart.setTypeface(Typeface.DEFAULT_BOLD);
        btnRestart.setBackground(createButtonDrawable());
        btnRestart.setOnClickListener(v -> startTest());
        
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 140);
        btnParams.setMargins(60, 40, 60, 0);
        btnRestart.setLayoutParams(btnParams);
        root.addView(btnRestart);

        setContentView(root);
        startTest();
    }

    private LinearLayout createStyledCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(30, 20, 30, 20);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(CARD_BG));
        gd.setCornerRadius(20f);
        gd.setStroke(2, Color.parseColor("#333333"));
        card.setBackground(gd);
        return card;
    }

    private GradientDrawable createInputDrawable() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.BLACK);
        gd.setCornerRadius(10f);
        gd.setStroke(3, Color.parseColor("#333333"));
        return gd;
    }

    private GradientDrawable createButtonDrawable() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(ACCENT_COLOR));
        gd.setCornerRadius(70f);
        return gd;
    }

    private void startTest() {
        isTestRunning = true;
        wordsTyped = 0;
        mistakes = 0;
        startTime = System.currentTimeMillis();
        nextWord();
        etInput.setText("");
        etInput.requestFocus();
    }

    private void nextWord() {
        String[] words = LEVEL_WORDS[currentLevel - 1];
        currentTargetWord = words[new Random().nextInt(words.length)];
        tvTarget.setText(currentTargetWord.toUpperCase());
    }

    private void checkInput(String input) {
        if (!isTestRunning) return;
        String cleanInput = input.trim();

        if (cleanInput.equalsIgnoreCase(currentTargetWord)) {
            if (input.endsWith(" ") || input.length() >= currentTargetWord.length()) {
                wordsTyped++;
                updateStats();
                etInput.setText("");
                if (wordsTyped >= 10) {
                    completeLevel();
                } else {
                    nextWord();
                }
            }
        } else if (input.length() > 0 && !currentTargetWord.toLowerCase().startsWith(input.toLowerCase())) {
            mistakes++;
            updateStats();
        }
    }

    private void updateStats() {
        long elapsed = System.currentTimeMillis() - startTime;
        double minutes = elapsed / 60000.0;
        int wpm = (int) (wordsTyped / (minutes > 0 ? minutes : 1.0));
        int accuracy = (int) Math.max(0, 100 - (mistakes * 2));
        tvStats.setText("SPEED: " + wpm + " WPM  |  ACC: " + accuracy + "%");
    }

    private void updateLevelDisplay() {
        String info = "MODE: ELITE COACH  |  RANK: ";
        switch (currentLevel) {
            case 1: info += "NOVICE"; break;
            case 2: info += "TRAINEE"; break;
            case 3: info += "SPECIALIST"; break;
            case 4: info += "EXPERT"; break;
            case 5: info += "ELITE MASTER"; break;
        }
        tvLevelInfo.setText(info);
    }

    private void completeLevel() {
        isTestRunning = false;
        if (currentLevel < 5) {
            currentLevel++;
            updateLevelDisplay();
            tvTarget.setText("SUCCESS");
            new Handler().postDelayed(this::startTest, 1500);
        } else {
            tvTarget.setText("MAX RANK");
            tvTarget.setTextColor(Color.YELLOW);
        }
    }
}
