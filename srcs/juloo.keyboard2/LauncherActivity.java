package juloo.keyboard2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class LauncherActivity extends Activity implements Handler.Callback {
    private TextView _tryhere_text;
    private EditText _tryhere_area;
    private List<Animatable> _animations;
    private Handler _handler;

    private static final String ACCENT_COLOR = "#00E5FF";
    private static final String BG_COLOR = "#121212";
    private static final String CARD_BG = "#1E1E1E";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setupAdvancedUI();

        _handler = new Handler(getMainLooper(), this);
        if (VERSION.SDK_INT >= 28) {
            _tryhere_area.addOnUnhandledKeyEventListener(new Tryhere_OnUnhandledKeyEventListener());
        }
    }

    private void setupAdvancedUI() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(Color.parseColor(BG_COLOR));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 60, 40, 60);

        // Header
        TextView title = new TextView(this);
        title.setText("ELITE KEYBOARD PRO");
        title.setTextColor(Color.parseColor(ACCENT_COLOR));
        title.setTextSize(28);
        title.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 60);
        root.addView(title);

        // Setup Cards
        root.addView(createActionCard("STEP 1: ENABLE", "Activate the keyboard in system settings", v -> launch_imesettings(v)));
        root.addView(createActionCard("STEP 2: SELECT", "Choose Elite Keyboard as active method", v -> launch_imepicker(v)));

        // Typing Master Button (Elite Upgrade)
        Button btnMaster = createStyledButton("ELITE TYPING MASTER", true);
        btnMaster.setOnClickListener(v -> startActivity(new Intent(this, TypingMasterActivity.class)));
        root.addView(btnMaster);

        // Try Area Card
        LinearLayout tryCard = createStyledCard();
        TextView tryTitle = new TextView(this);
        tryTitle.setText("LIVE TYPING PREVIEW");
        tryTitle.setTextColor(Color.GRAY);
        tryTitle.setTextSize(12);
        tryTitle.setPadding(0, 0, 0, 20);
        tryCard.addView(tryTitle);

        _tryhere_text = new TextView(this);
        _tryhere_text.setText("Key Events Appear Here");
        _tryhere_text.setTextColor(Color.parseColor(ACCENT_COLOR));
        _tryhere_text.setTextSize(18);
        tryCard.addView(_tryhere_text);

        _tryhere_area = new EditText(this);
        _tryhere_area.setHint("Type here to test...");
        _tryhere_area.setHintTextColor(Color.DKGRAY);
        _tryhere_area.setTextColor(Color.WHITE);
        _tryhere_area.setBackground(createInputDrawable());
        _tryhere_area.setPadding(30, 30, 30, 30);
        LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etParams.setMargins(0, 30, 0, 0);
        _tryhere_area.setLayoutParams(etParams);
        tryCard.addView(_tryhere_area);
        root.addView(tryCard);

        // Github/Source Section
        TextView sourceText = new TextView(this);
        sourceText.setText("BUILD THE FUTURE");
        sourceText.setTextColor(Color.GRAY);
        sourceText.setTextSize(12);
        sourceText.setGravity(Gravity.CENTER);
        sourceText.setPadding(0, 60, 0, 20);
        root.addView(sourceText);

        TextView gitLink = new TextView(this);
        gitLink.setText("GITHUB SOURCE CODE");
        gitLink.setTextColor(Color.parseColor(ACCENT_COLOR));
        gitLink.setTextSize(14);
        gitLink.setTypeface(null, Typeface.BOLD);
        gitLink.setGravity(Gravity.CENTER);
        gitLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/juloo/keyboard2"));
            startActivity(intent);
        });
        root.addView(gitLink);

        scrollView.addView(root);
        setContentView(scrollView);
    }

    private LinearLayout createActionCard(String title, String desc, View.OnClickListener listener) {
        LinearLayout card = createStyledCard();
        card.setOnClickListener(listener);
        
        TextView t = new TextView(this);
        t.setText(title);
        t.setTextColor(Color.parseColor(ACCENT_COLOR));
        t.setTextSize(16);
        t.setTypeface(null, Typeface.BOLD);
        card.addView(t);

        TextView d = new TextView(this);
        d.setText(desc);
        d.setTextColor(Color.LTGRAY);
        d.setTextSize(13);
        card.addView(d);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) card.getLayoutParams();
        if (lp == null) lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 30);
        card.setLayoutParams(lp);

        return card;
    }

    private LinearLayout createStyledCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(40, 40, 40, 40);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(CARD_BG));
        gd.setCornerRadius(24f);
        gd.setStroke(2, Color.parseColor("#333333"));
        card.setBackground(gd);
        return card;
    }

    private GradientDrawable createInputDrawable() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.BLACK);
        gd.setCornerRadius(12f);
        gd.setStroke(2, Color.parseColor("#444444"));
        return gd;
    }

    private Button createStyledButton(String text, boolean primary) {
        Button b = new Button(this);
        b.setText(text);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(primary ? ACCENT_COLOR : CARD_BG));
        gd.setCornerRadius(80f);
        if (!primary) gd.setStroke(3, Color.parseColor("#333333"));
        b.setBackground(gd);
        b.setTextColor(primary ? Color.BLACK : Color.WHITE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
        lp.setMargins(20, 40, 20, 40);
        b.setLayoutParams(lp);
        return b;
    }

    @Override
    public void onStart() {
        super.onStart();
        _animations = new ArrayList<>();
        // In a real app we'd find the anims from the layout, but here we're dynamic
        _handler.removeMessages(0);
        _handler.sendEmptyMessageDelayed(0, 500);
    }

    @Override
    public boolean handleMessage(Message _msg) {
        for (Animatable anim : _animations) anim.start();
        _handler.sendEmptyMessageDelayed(0, 3000);
        return true;
    }

    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.launcher_menu, menu);
        return true;
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btnLaunchSettingsActivity) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void launch_imesettings(View _btn) {
        startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
    }

    public void launch_imepicker(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showInputMethodPicker();
    }

    final class Tryhere_OnUnhandledKeyEventListener implements View.OnUnhandledKeyEventListener {
        public boolean onUnhandledKeyEvent(View v, KeyEvent ev) {
            if (ev.getKeyCode() == KeyEvent.KEYCODE_BACK) return false;
            if (KeyEvent.isModifierKey(ev.getKeyCode())) return false;
            StringBuilder s = new StringBuilder();
            if (ev.isAltPressed()) s.append("Alt+");
            if (ev.isShiftPressed()) s.append("Shift+");
            if (ev.isCtrlPressed()) s.append("Ctrl+");
            if (ev.isMetaPressed()) s.append("Meta+");
            String kc = KeyEvent.keyCodeToString(ev.getKeyCode());
            s.append(kc.replaceFirst("^KEYCODE_", ""));
            _tryhere_text.setText(s.toString());
            return false;
        }
    }
}
