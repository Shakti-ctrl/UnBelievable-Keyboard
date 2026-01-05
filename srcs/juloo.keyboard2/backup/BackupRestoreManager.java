package juloo.keyboard2.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import juloo.keyboard2.ClipboardHistoryService;
import juloo.keyboard2.Config;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class BackupRestoreManager {

    public static String createBackupJson(Context context) {
        try {
            JSONObject backup = new JSONObject();

            // 1. Settings (SharedPreferences)
            JSONObject settings = new JSONObject();
            SharedPreferences prefs = Config.globalPrefs();
            Map<String, ?> allEntries = prefs.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                settings.put(entry.getKey(), entry.getValue());
            }
            backup.put("settings", settings);

            // 2. Clipboard History
            JSONArray clips = new JSONArray();
            ClipboardHistoryService service = ClipboardHistoryService.get_service(context);
            if (service != null) {
                List<ClipboardHistoryService.HistoryEntry> history = service.get_history_entries();
                for (ClipboardHistoryService.HistoryEntry ent : history) {
                    JSONObject clip = new JSONObject();
                    clip.put("content", ent.content);
                    clip.put("timestamp", ent.timestamp);
                    clip.put("description", ent.description);
                    clip.put("version", ent.version);
                    clips.put(clip);
                }
            }
            backup.put("clipboard", clips);

            return backup.toString(4);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean restoreBackup(Context context, Uri uri) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();

            JSONObject backup = new JSONObject(sb.toString());

            // 1. Restore Settings
            if (backup.has("settings")) {
                JSONObject settings = backup.getJSONObject("settings");
                SharedPreferences.Editor editor = Config.globalPrefs().edit();
                java.util.Iterator<String> keys = settings.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = settings.get(key);
                    if (value instanceof Boolean) editor.putBoolean(key, (Boolean) value);
                    else if (value instanceof Integer) editor.putInt(key, (Integer) value);
                    else if (value instanceof Long) editor.putLong(key, (Long) value);
                    else if (value instanceof Float) editor.putFloat(key, (Float) value);
                    else if (value instanceof String) editor.putString(key, (String) value);
                }
                editor.apply();
            }

            // 2. Restore Clipboard (Merge with existing)
            if (backup.has("clipboard")) {
                JSONArray clips = backup.getJSONArray("clipboard");
                ClipboardHistoryService service = ClipboardHistoryService.get_service(context);
                if (service != null) {
                    for (int i = 0; i < clips.length(); i++) {
                        JSONObject clip = clips.getJSONObject(i);
                        service.add_clip_with_metadata(
                            clip.getString("content"),
                            clip.optString("description", ""),
                            clip.optString("version", "")
                        );
                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}