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
        setContentView(R.layout.clipboard_history_activity);
        
        service = ClipboardHistoryService.get_service(this);
        listView = findViewById(R.id.clipboard_history_list);
        
        updateList();

        findViewById(R.id.btn_add_new).setOnClickListener(v -> showAddDialog());
        findViewById(R.id.btn_export_history).setOnClickListener(v -> exportHistory());
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
