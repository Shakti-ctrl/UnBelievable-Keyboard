package juloo.keyboard2;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

public class DefaultSettingsApplier {
    private static final String DEFAULT_JSON = "{\"settings\":{\"extra_key_combining_caron\":false,\"longpress_interval\":12,\"extra_key_combining_shaddah\":false,\"margin_bottom_portrait_unfolded\":7,\"extra_key_page_up\":true,\"key_horizontal_margin\":2,\"extra_key_cut\":true,\"circle_sensitivity\":\"2\",\"extra_key_capslock\":true,\"extra_key_combining_fatha\":false,\"layouts\":\"[\\\"{\\\\\\\"kind\\\\\\\":\\\\\\\"system\\\\\\\"}\\\"]\",\"extra_key_†\":false,\"key_activated_opacity\":100,\"extra_key_combining_grave\":false,\"extra_key_accent_trema\":false,\"extra_key_nbsp\":false,\"extra_key_accent_breve\":false,\"number_entry_layout\":\"pin\",\"keyboard_opacity\":100,\"border_config\":true,\"extra_key_combining_sukun\":false,\"extra_key_delete_word\":false,\"extra_key_combining_hamza_above\":false,\"character_size\":1.15,\"extra_key_combining_kasratan\":false,\"extra_key_accent_double_grave\":false,\"extra_key_accent_slash\":false,\"extra_key_combining_double_aigu\":false,\"custom_border_line_width\":0,\"extra_key_combining_horn\":false,\"version\":3,\"swipe_dist\":\"15\",\"extra_key_switch_greekmath\":false,\"extra_key_combining_payerok\":false,\"extra_key_switch_clipboard\":true,\"extra_key_accent_tilde\":false,\"keyboard_height_landscape\":50,\"keyboard_height_unfolded\":35,\"extra_key_accent_dot_above\":false,\"extra_key_combining_inverted_breve\":false,\"extra_key_combining_ogonek\":false,\"lock_double_tap\":true,\"extra_key_shareText\":true,\"extra_key_combining_slavonic_psili\":false,\"extra_key_combining_vzmet\":false,\"number_row\":\"no_number_row\",\"extra_key_combining_titlo\":false,\"keyboard_height\":26,\"extra_key_compose\":true,\"extra_key_undo\":true,\"extra_key_scroll_lock\":true,\"extra_key_accent_cedille\":false,\"extra_key_ß\":false,\"extra_key_accent_grave\":false,\"horizontal_margin_portrait\":3,\"extra_key_esc\":true,\"extra_key_combining_tilde\":false,\"margin_bottom_portrait\":7,\"theme\":\"jungle\"}}";

    public static void apply(Context context) {
        try {
            JSONObject backup = new JSONObject(DEFAULT_JSON);
            JSONObject settingsJson = backup.getJSONObject("settings");
            SharedPreferences prefs = DirectBootAwarePreferences.get_shared_preferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            
            Iterator<String> keys = settingsJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = settingsJson.get(key);
                
                if (value instanceof Boolean) editor.putBoolean(key, (Boolean) value);
                else if (value instanceof Integer) editor.putInt(key, (Integer) value);
                else if (value instanceof Long) editor.putLong(key, (Long) value);
                else if (value instanceof Double) editor.putFloat(key, ((Double) value).floatValue());
                else if (value instanceof String) {
                    String s = (String) value;
                    if (s.startsWith("[") && s.endsWith("]")) {
                        try {
                            JSONArray array = new JSONArray(s);
                            Set<String> set = new HashSet<>();
                            for (int i = 0; i < array.length(); i++) {
                                set.add(array.getString(i));
                            }
                            editor.putStringSet(key, set);
                        } catch (Exception e) {
                            editor.putString(key, s);
                        }
                    } else {
                        editor.putString(key, s);
                    }
                }
            }
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
