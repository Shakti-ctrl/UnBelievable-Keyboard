package juloo.keyboard2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Random;

public class TypingMasterActivity extends Activity {

    private TextView tvTarget, tvStats, tvLevelInfo;
    private EditText etInput;
    private int currentLevel = 1;
    private int wordsTyped = 0;
    private long startTime = 0;
    private String currentTargetWord = "";
    private int mistakes = 0;
    private boolean isTestRunning = false;

    private static final String[][] LEVEL_WORDS = {
        {"the", "be", "to", "of", "and", "in", "that", "have"}, // Level 1
        {"rhythm", "flow", "typing", "master", "speed", "quick"}, // Level 2
        {"complex", "challenge", "accuracy", "practice", "mobile"}, // Level 3
        {"performance", "consistency", "neuroscience", "keyboard"}, // Level 4
        {"competitive", "excellence", "professional", "optimization"} // Level 5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTrainingUI();
    }

    private void setupTrainingUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#121212"));
        root.setPadding(40, 40, 40, 40);

        tvLevelInfo = new TextView(this);
        tvLevelInfo.setTextColor(Color.parseColor("#00E5FF"));
        tvLevelInfo.setTextSize(18);
        tvLevelInfo.setGravity(Gravity.CENTER);
        updateLevelDisplay();
        root.addView(tvLevelInfo);

        tvStats = new TextView(this);
        tvStats.setTextColor(Color.WHITE);
        tvStats.setTextSize(16);
        tvStats.setGravity(Gravity.CENTER);
        tvStats.setText("WPM: 0 | Accuracy: 100%");
        root.addView(tvStats);

        tvTarget = new TextView(this);
        tvTarget.setTextColor(Color.parseColor("#00E5FF"));
        tvTarget.setTextSize(32);
        tvTarget.setTypeface(null, Typeface.BOLD);
        tvTarget.setGravity(Gravity.CENTER);
        tvTarget.setPadding(0, 100, 0, 100);
        root.addView(tvTarget);

        etInput = new EditText(this);
        etInput.setHint("Type here...");
        etInput.setHintTextColor(Color.GRAY);
        etInput.setTextColor(Color.WHITE);
        etInput.setGravity(Gravity.CENTER);
        etInput.setBackgroundColor(Color.parseColor("#1E1E1E"));
        etInput.setPadding(20, 40, 20, 40);
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

        Button btnRestart = new Button(this);
        btnRestart.setText("RESTART LEVEL");
        btnRestart.setBackgroundColor(Color.parseColor("#00E5FF"));
        btnRestart.setTextColor(Color.BLACK);
        btnRestart.setOnClickListener(v -> startTest());
        root.addView(btnRestart);

        setContentView(root);
        startTest();
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
        tvTarget.setText(currentTargetWord);
    }

    private void checkInput(String input) {
        if (!isTestRunning) return;

        if (input.equals(currentTargetWord + " ") || input.equals(currentTargetWord)) {
            if (input.endsWith(" ") || input.length() == currentTargetWord.length()) {
                wordsTyped++;
                updateStats();
                etInput.setText("");
                if (wordsTyped >= 10) {
                    completeLevel();
                } else {
                    nextWord();
                }
            }
        } else if (!currentTargetWord.startsWith(input)) {
            mistakes++;
            updateStats();
        }
    }

    private void updateStats() {
        long elapsed = System.currentTimeMillis() - startTime;
        double minutes = elapsed / 60000.0;
        int wpm = (int) (wordsTyped / (minutes > 0 ? minutes : 1.0));
        int accuracy = (int) Math.max(0, 100 - (mistakes * 2));
        tvStats.setText("WPM: " + wpm + " | Accuracy: " + accuracy + "%");
    }

    private void updateLevelDisplay() {
        String info = "LEVEL " + currentLevel + ": ";
        switch (currentLevel) {
            case 1: info += "Foundation Drills"; break;
            case 2: info += "Rhythm Flow"; break;
            case 3: info += "Speed Burst"; break;
            case 4: info += "Endurance Pro"; break;
            case 5: info += "Elite Competitive"; break;
        }
        tvLevelInfo.setText(info);
    }

    private void completeLevel() {
        isTestRunning = false;
        if (currentLevel < 5) {
            currentLevel++;
            updateLevelDisplay();
            tvTarget.setText("LEVEL COMPLETE!");
            new Handler().postDelayed(this::startTest, 2000);
        } else {
            tvTarget.setText("ELITE MASTER ACHIEVED!");
        }
    }
}
