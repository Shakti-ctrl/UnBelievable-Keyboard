package juloo.keyboard2;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

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
        try {
            String json = juloo.keyboard2.backup.BackupRestoreManager.createBackupJson(this);
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            intent.putExtra(android.content.Intent.EXTRA_TITLE, "typing_master_backup.json");
            startActivityForResult(intent, 1001);
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Backup failed", android.widget.Toast.LENGTH_SHORT).show();
        }
        return true;
    });

    findPreference("restore_data").setOnPreferenceClickListener(p -> {
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        startActivityForResult(intent, 1002);
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
    if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
    android.net.Uri uri = data.getData();
    if (requestCode == 1001) {
      try {
        String json = juloo.keyboard2.backup.BackupRestoreManager.createBackupJson(this);
        android.os.ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
        java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(pfd.getFileDescriptor());
        fileOutputStream.write(json.getBytes());
        fileOutputStream.close();
        pfd.close();
        android.widget.Toast.makeText(this, "Backup successful", android.widget.Toast.LENGTH_SHORT).show();
      } catch (Exception e) {
        android.widget.Toast.makeText(this, "Backup failed: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
      }
    } else if (requestCode == 1002) {
      if (juloo.keyboard2.backup.BackupRestoreManager.restoreBackup(this, uri)) {
        android.widget.Toast.makeText(this, "Restore successful! Restarting keyboard...", android.widget.Toast.LENGTH_SHORT).show();
        // Notify keyboard to refresh
        Config.globalConfig().refresh(getResources(), FoldStateTracker.isFoldableDevice(this));
      } else {
        android.widget.Toast.makeText(this, "Restore failed", android.widget.Toast.LENGTH_SHORT).show();
      }
    }
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
