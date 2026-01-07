package juloo.keyboard2;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import android.preference.Preference;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

public class SettingsActivity extends PreferenceActivity
{
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    // The preferences can't be read when in direct-boot mode. Avoid crashing
    // and don't allow changing the settings.
    // Run the config migration on this prefs as it might be different from the
    // one used by the keyboard, which have been migrated.
    try
    {
      Config.migrate(getPreferenceManager().getSharedPreferences());
    }
    catch (Exception _e) { fallbackEncrypted(); return; }
    addPreferencesFromResource(R.xml.settings);

    findPreference("backup_data").setOnPreferenceClickListener(p -> {
        String backup = BackupRestoreSystem.createBackup(this);
        if (backup != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, backup);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getString(R.string.pref_backup_title)));
            Toast.makeText(this, R.string.backup_success, Toast.LENGTH_SHORT).show();
        }
        return true;
    });

    findPreference("restore_data").setOnPreferenceClickListener(p -> {
        // In a real app we'd use a file picker, but to keep it simple and within Replit constraints
        // we'll use a dialog to paste the backup string or just show how it would be done.
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.pref_restore_title);
        final android.widget.EditText input = new android.widget.EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Restore", (dialog, which) -> {
            if (BackupRestoreSystem.restoreBackup(this, input.getText().toString())) {
                Toast.makeText(this, R.string.restore_success, Toast.LENGTH_LONG).show();
                // Refresh activity to reflect changes
                recreate();
            } else {
                Toast.makeText(this, R.string.restore_failed, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
        return true;
    });

    findPreference("theme_import").setOnPreferenceClickListener(p -> {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.pref_theme_import);
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Paste theme code here");
        builder.setView(input);
        builder.setPositiveButton("Import", (dialog, which) -> {
            String themeData = input.getText().toString();
            try {
                org.json.JSONObject backup = new org.json.JSONObject(themeData);
                org.json.JSONObject settingsJson = backup.optJSONObject("settings");
                if (settingsJson == null) settingsJson = backup; // Handle both full backup and just settings object

                SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
                
                // Keys related to visual theme/style
                String[] themeKeys = {
                    "theme", "label_brightness", "keyboard_opacity", "key_opacity", 
                    "key_activated_opacity", "border_config", "custom_border_radius", 
                    "custom_border_line_width", "character_size", "key_vertical_margin", 
                    "key_horizontal_margin"
                };

                for (String key : themeKeys) {
                    if (settingsJson.has(key)) {
                        Object value = settingsJson.get(key);
                        if (value instanceof Boolean) editor.putBoolean(key, (Boolean) value);
                        else if (value instanceof Integer) editor.putInt(key, (Integer) value);
                        else if (value instanceof Long) editor.putLong(key, (Long) value);
                        else if (value instanceof Double) editor.putFloat(key, ((Double) value).floatValue());
                        else if (value instanceof String) editor.putString(key, (String) value);
                    }
                }
                editor.apply();
                Toast.makeText(this, R.string.theme_import_success, Toast.LENGTH_SHORT).show();
                recreate();
            } catch (Exception e) {
                Toast.makeText(this, R.string.theme_import_failed, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
        return true;
    });

    boolean foldableDevice = FoldStateTracker.isFoldableDevice(this);
    findPreference("margin_bottom_portrait_unfolded").setEnabled(foldableDevice);
    findPreference("margin_bottom_landscape_unfolded").setEnabled(foldableDevice);
    findPreference("horizontal_margin_portrait_unfolded").setEnabled(foldableDevice);
    findPreference("horizontal_margin_landscape_unfolded").setEnabled(foldableDevice);
    findPreference("keyboard_height_unfolded").setEnabled(foldableDevice);
    findPreference("keyboard_height_landscape_unfolded").setEnabled(foldableDevice);
  }

  void fallbackEncrypted()
  {
    // Can't communicate with the user here.
    finish();
  }

  protected void onStop()
  {
    DirectBootAwarePreferences
      .copy_preferences_to_protected_storage(this,
          getPreferenceManager().getSharedPreferences());
    super.onStop();
  }
}
