package juloo.keyboard2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.List;

public class ClipboardHistoryActivity extends Activity {
    private ListView listView;
    private ClipboardHistoryService service;
    private ClipboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // Apply a basic material theme to ensure UI consistency
            setTheme(android.R.style.Theme_Material_Light_NoActionBar);
            
            setContentView(R.layout.clipboard_history_activity);
            
            // Check if service is initialized. If not, try to initialize it.
            service = ClipboardHistoryService.get_service(this);
            if (service == null) {
                // If the service is null, it usually means VERSION.SDK_INT <= 11 or internal state issue.
                // We attempt to trigger initialization manually for the activity context.
                ClipboardHistoryService.on_startup(getApplicationContext(), null);
                service = ClipboardHistoryService.get_service(this);
            }
            
            if (service == null) {
                showError("Clipboard Service could not be initialized.\n\nPlease ensure Unexpected Keyboard is enabled in Settings > System > Languages & Input.");
                return;
            }

            listView = findViewById(R.id.clipboard_history_list);
            if (listView == null) {
                showError("UI Error: ListView not found in layout.");
                return;
            }
            
            updateList();

            View btnAdd = findViewById(R.id.btn_add_new);
            if (btnAdd != null) btnAdd.setOnClickListener(v -> showAddDialog());
            
            View btnExport = findViewById(R.id.btn_export_history);
            if (btnExport != null) btnExport.setOnClickListener(v -> exportHistory());
            
        } catch (Throwable t) {
            // Catching Throwable to include Errors and RuntimeExceptions
            String errorMsg = "Critical failure on startup.\n\nType: " + t.getClass().getName() + "\nMessage: " + t.getMessage();
            android.util.Log.e("ClipboardActivity", errorMsg, t);
            showError(errorMsg + "\n\nStack Trace:\n" + android.util.Log.getStackTraceString(t));
        }
    }

    private void showError(String message) {
        android.util.Log.e("ClipboardActivity", "Showing error UI: " + message);
        
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(0xFFFFFFFF); // White background
        ll.setPadding(48, 48, 48, 48);
        
        TextView title = new TextView(this);
        title.setText("Error Details");
        title.setTextSize(22);
        title.setTextColor(0xFFFF0000); // Red
        title.setPadding(0, 0, 0, 32);
        ll.addView(title);

        Button copyBtn = new Button(this);
        copyBtn.setText("Copy Error to Clipboard");
        copyBtn.setOnClickListener(v -> {
            android.content.ClipboardManager cm = (android.content.ClipboardManager)getSystemService(android.content.Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(android.content.ClipData.newPlainText("Error Log", message));
                Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
            }
        });
        ll.addView(copyBtn);

        ScrollView sv = new ScrollView(this);
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextSize(14);
        tv.setTextColor(0xFF333333); // Dark Gray
        tv.setPadding(0, 32, 0, 0);
        tv.setTextIsSelectable(true);
        sv.addView(tv);
        
        ll.addView(sv);
        
        setContentView(ll);
    }

    private void updateList() {
        if (service == null) return;
        List<ClipboardHistoryService.HistoryEntry> history = service.get_history_entries();
        adapter = new ClipboardAdapter(history);
        listView.setAdapter(adapter);
    }

    class ClipboardAdapter extends BaseAdapter {
        List<ClipboardHistoryService.HistoryEntry> items;
        ClipboardAdapter(List<ClipboardHistoryService.HistoryEntry> items) { this.items = items; }
        @Override public int getCount() { return items.size(); }
        @Override public Object getItem(int p) { return items.get(p); }
        @Override public long getItemId(int p) { return p; }
        @Override public View getView(int p, View v, ViewGroup prnt) {
            if (v == null) {
                LinearLayout ll = new LinearLayout(ClipboardHistoryActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setPadding(32, 24, 32, 24);
                
                TextView text = new TextView(ClipboardHistoryActivity.this);
                text.setId(android.R.id.text1);
                text.setTextSize(16);
                text.setTextColor(0xFF000000);
                
                TextView sub = new TextView(ClipboardHistoryActivity.this);
                sub.setId(android.R.id.text2);
                sub.setTextSize(12);
                sub.setTextColor(0xFF666666);
                
                ll.addView(text);
                ll.addView(sub);
                v = ll;
            }
            ClipboardHistoryService.HistoryEntry ent = items.get(p);
            ((TextView)v.findViewById(android.R.id.text1)).setText(ent.content);
            String info = ent.timestamp + (ent.version.isEmpty() ? "" : " | Ver: " + ent.version) + (ent.description.isEmpty() ? "" : "\n" + ent.description);
            ((TextView)v.findViewById(android.R.id.text2)).setText(info);
            
            v.setOnClickListener(view -> showEditDialog(ent));
            return v;
        }
    }

    private void showEditDialog(ClipboardHistoryService.HistoryEntry ent) {
        EditText edit = new EditText(this);
        edit.setText(ent.content);
        edit.setHint("Content");
        
        EditText desc = new EditText(this);
        desc.setText(ent.description);
        desc.setHint("Description");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);
        layout.addView(edit);
        layout.addView(desc);

        new AlertDialog.Builder(this)
            .setTitle("Edit Entry")
            .setView(layout)
            .setPositiveButton("Save as Ver 2", (d, w) -> {
                service.add_clip_with_metadata(edit.getText().toString(), desc.getText().toString(), "2");
                updateList();
            })
            .setNegativeButton("Delete", (d, w) -> {
                service.remove_history_entry(ent.content);
                updateList();
            })
            .setNeutralButton("Share", (d, w) -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, ent.content);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, null));
            })
            .show();
    }

    private void showAddDialog() {
        EditText input = new EditText(this);
        input.setHint("Content");
        EditText descInput = new EditText(this);
        descInput.setHint("Description");
        
        Spinner spinner = new Spinner(this);
        String[] formats = {".txt", ".pdf", ".js", ".java", ".html"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, formats);
        spinner.setAdapter(spinAdapter);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);
        layout.addView(input);
        layout.addView(descInput);
        layout.addView(spinner);

        new AlertDialog.Builder(this)
            .setTitle("Add New Note")
            .setView(layout)
            .setPositiveButton("Save", (d, w) -> {
                String text = input.getText().toString();
                String desc = descInput.getText().toString();
                String ext = spinner.getSelectedItem().toString();
                
                service.add_clip_with_metadata(text, desc, "1");
                updateList();
                
                Toast.makeText(this, "Saved as " + ext, Toast.LENGTH_SHORT).show();
            })
            .show();
    }

    private void exportHistory() {
        List<ClipboardHistoryService.HistoryEntry> history = service.get_history_entries();
        if (history.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        for (ClipboardHistoryService.HistoryEntry entry : history) {
            sb.append("Time: ").append(entry.timestamp).append("\n");
            sb.append("Version: ").append(entry.version).append("\n");
            sb.append("Description: ").append(entry.description).append("\n");
            sb.append("Content: ").append(entry.content).append("\n");
            sb.append("---\n");
        }

        try {
            java.io.File file = new java.io.File(getExternalFilesDir(null), "clipboard_export_" + System.currentTimeMillis() + ".txt");
            java.io.FileWriter writer = new java.io.FileWriter(file);
            writer.write(sb.toString());
            writer.close();

            android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Export History"));
        } catch (Exception e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
