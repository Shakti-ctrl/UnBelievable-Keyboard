package juloo.keyboard2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class ClipboardHistoryService
{
  /** Start the service on startup and start listening to clipboard changes. */
  public static void on_startup(Context ctx, ClipboardPasteCallback cb)
  {
    get_service(ctx);
    _paste_callback = cb;
  }

  /** Start the service if it hasn't been started before. Returns [null] if the
      feature is unsupported. */
  public static ClipboardHistoryService get_service(Context ctx)
  {
    if (VERSION.SDK_INT <= 11)
      return null;
    if (_service == null)
      _service = new ClipboardHistoryService(ctx);
    return _service;
  }

  public static void set_history_enabled(boolean e)
  {
    Config.globalConfig().set_clipboard_history_enabled(e);
    if (_service == null)
      return;
    if (e)
      _service.add_current_clip();
    else
      _service.clear_history();
  }

  /** Send the given string to the editor. */
  public static void paste(String clip)
  {
    if (_paste_callback != null)
      _paste_callback.paste_from_clipboard_pane(clip);
  }

  /** The maximum size limits the amount of user data stored in memory but also
      gives a sense to the user that the history is not persisted and can be
      forgotten as soon as the app stops. */
  public static final int MAX_HISTORY_SIZE = Integer.MAX_VALUE;

  static ClipboardHistoryService _service = null;
  static ClipboardPasteCallback _paste_callback = null;

  ClipboardManager _cm;
  List<HistoryEntry> _history;
  OnClipboardHistoryChange _listener = null;

  public interface OnClipboardHistoryChange
  {
    public void on_clipboard_history_change();
  }

  private final class SystemListener implements ClipboardManager.OnPrimaryClipChangedListener
  {
    @Override
    public void onPrimaryClipChanged()
    {
      add_current_clip();
    }
  }

  public synchronized void add_current_clip()
  {
    if (_cm.hasPrimaryClip())
    {
      ClipData cd = _cm.getPrimaryClip();
      if (cd.getItemCount() > 0)
      {
        CharSequence text = cd.getItemAt(0).getText();
        if (text != null)
          add_clip(text.toString());
      }
    }
  }

  public synchronized void clear_history()
  {
    _history.clear();
    save_history_to_prefs(juloo.keyboard2.Config.globalConfig().getContext());
    if (_listener != null)
      _listener.on_clipboard_history_change();
  }

  public void set_on_clipboard_history_change(OnClipboardHistoryChange l)
  {
    _listener = l;
  }

  ClipboardHistoryService(Context ctx)
  {
    _history = new ArrayList<HistoryEntry>();
    _cm = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
    _cm.addPrimaryClipChangedListener(new SystemListener());
    load_history_from_prefs(ctx);
  }

  public synchronized List<HistoryEntry> get_history_entries() {
    return new ArrayList<>(_history);
  }

  public synchronized List<String> clear_expired_and_get_history()
  {
    List<String> dst = new ArrayList<String>();
    for (HistoryEntry ent : _history) {
      dst.add(ent.content);
    }
    return dst;
  }

  public synchronized void remove_history_entry(String clip)
  {
    int last_pos = _history.size() - 1;
    boolean last_pos_changed = false;
    for (int pos = last_pos; pos >= 0; pos--)
    {
      if (!_history.get(pos).content.equals(clip))
        continue;
      if (pos == last_pos)
        last_pos_changed = true;
      _history.remove(pos);
    }
    if (last_pos_changed)
    {
      if (VERSION.SDK_INT >= 28)
        _cm.clearPrimaryClip();
      else
        _cm.setText("");
    }
    save_history_to_prefs(juloo.keyboard2.Config.globalConfig().getContext());
    if (_listener != null)
      _listener.on_clipboard_history_change();
  }

  public synchronized void add_clip(String clip) {
    // Check for duplicates
    for (HistoryEntry ent : _history) {
      if (ent.content.equals(clip)) return;
    }
    add_clip_with_metadata(clip, "", "");
  }

  public synchronized void add_clip_with_metadata(String clip, String description, String version) {
    if (!Config.globalConfig().clipboard_history_enabled)
      return;
    if (clip.equals(""))
      return;
    
    String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
    _history.add(new HistoryEntry(clip, timestamp, description, version));
    
    save_history_to_prefs(juloo.keyboard2.Config.globalConfig().getContext());
    if (_listener != null)
      _listener.on_clipboard_history_change();
  }

  private void load_history_from_prefs(Context ctx) {
    android.content.SharedPreferences prefs = ctx.getSharedPreferences("clipboard_history_v2", Context.MODE_PRIVATE);
    int size = prefs.getInt("size", 0);
    for (int i = 0; i < size; i++) {
      String content = prefs.getString("item_" + i, null);
      String time = prefs.getString("time_" + i, "");
      String desc = prefs.getString("desc_" + i, "");
      String ver = prefs.getString("ver_" + i, "");
      if (content != null) {
        _history.add(new HistoryEntry(content, time, desc, ver));
      }
    }
  }

  private void save_history_to_prefs(Context ctx) {
    if (ctx == null) return;
    android.content.SharedPreferences.Editor editor = ctx.getSharedPreferences("clipboard_history_v2", Context.MODE_PRIVATE).edit();
    editor.clear();
    editor.putInt("size", _history.size());
    for (int i = 0; i < _history.size(); i++) {
      HistoryEntry ent = _history.get(i);
      editor.putString("item_" + i, ent.content);
      editor.putString("time_" + i, ent.timestamp);
      editor.putString("desc_" + i, ent.description);
      editor.putString("ver_" + i, ent.version);
    }
    editor.apply();
  }

  static final class HistoryEntry
  {
    public final String content;
    public final String timestamp;
    public final String description;
    public final String version;
    public final long expiry_timestamp;

    public HistoryEntry(String c, String time, String desc, String ver)
    {
      content = c;
      timestamp = time;
      description = desc;
      version = ver;
      expiry_timestamp = Long.MAX_VALUE;
    }
  }

  public interface ClipboardPasteCallback
  {
    public void paste_from_clipboard_pane(String content);
  }
}
