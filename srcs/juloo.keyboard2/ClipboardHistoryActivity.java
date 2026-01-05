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
        List<String> history = service.clear_expired_and_get_history();
        adapter = new ClipboardAdapter(history);
        listView.setAdapter(adapter);
    }

    private void showAddDialog() {
        EditText input = new EditText(this);
        Spinner spinner = new Spinner(this);
        String[] formats = {".txt", ".pdf", ".js", ".java", ".html"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, formats);
        spinner.setAdapter(spinAdapter);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);
        layout.addView(spinner);

        new AlertDialog.Builder(this)
            .setTitle("Add New Note")
            .setView(layout)
            .setPositiveButton("Save", (d, w) -> {
                String text = input.getText().toString();
                String ext = spinner.getSelectedItem().toString();
                // Permanent save through the service
                service.add_clip(text); 
                updateList();
            })
            .show();
    }

    private void exportHistory() {
        StringBuilder sb = new StringBuilder();
        List<String> history = service.clear_expired_and_get_history();
        for (String entry : history) {
            sb.append(entry).append("\n---\n");
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Export History"));
    }

    class ClipboardAdapter extends BaseAdapter {
        List<String> items;
        ClipboardAdapter(List<String> items) { this.items = items; }
        @Override public int getCount() { return items.size(); }
        @Override public Object getItem(int p) { return items.get(p); }
        @Override public long getItemId(int p) { return p; }
        @Override public View getView(int p, View v, ViewGroup prnt) {
            if (v == null) v = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, prnt, false);
            ((TextView)v.findViewById(android.R.id.text1)).setText(items.get(p));
            v.setOnClickListener(view -> showEditDialog(items.get(p)));
            return v;
        }
    }

    private void showEditDialog(String content) {
        EditText edit = new EditText(this);
        edit.setText(content);
        new AlertDialog.Builder(this)
            .setTitle("Edit Entry")
            .setView(edit)
            .setPositiveButton("Save", (d, w) -> {
                service.remove_history_entry(content);
                service.add_clip(edit.getText().toString());
                updateList();
            })
            .setNegativeButton("Delete", (d, w) -> {
                service.remove_history_entry(content);
                updateList();
            })
            .setNeutralButton("Share", (d, w) -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, content);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, null));
            })
            .show();
    }
}
