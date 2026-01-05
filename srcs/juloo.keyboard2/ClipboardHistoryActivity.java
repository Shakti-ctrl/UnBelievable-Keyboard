package juloo.keyboard2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ClipboardHistoryActivity extends Activity {
    private ListView listView;
    private ClipboardHistoryService service;
    private ClipboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setTheme(android.R.style.Theme_Material_Light_NoActionBar);
            
            LinearLayout mainLayout = new LinearLayout(this);
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            mainLayout.setPadding(32, 32, 32, 32);
            mainLayout.setBackgroundColor(0xFFEEEEEE);
            
            EditText searchBar = new EditText(this);
            searchBar.setHint("Search clips...");
            searchBar.setPadding(32, 32, 32, 32);
            android.graphics.drawable.GradientDrawable searchBg = new android.graphics.drawable.GradientDrawable();
            searchBg.setColor(0xFFFFFFFF);
            searchBg.setCornerRadius(16);
            searchBar.setBackground(searchBg);
            searchBar.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (adapter != null) adapter.filter(s.toString());
                }
                @Override public void afterTextChanged(android.text.Editable s) {}
            });
            mainLayout.addView(searchBar);
            
            listView = new ListView(this);
            listView.setDivider(null);
            listView.setDividerHeight(16);
            LinearLayout.LayoutParams listLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
            listLp.setMargins(0, 32, 0, 0);
            listView.setLayoutParams(listLp);
            mainLayout.addView(listView);
            
            setContentView(mainLayout);
            
            service = ClipboardHistoryService.get_service(this);
            if (service == null) {
                ClipboardHistoryService.on_startup(getApplicationContext(), null);
                service = ClipboardHistoryService.get_service(this);
            }
            
            if (service == null) {
                showError("Clipboard Service could not be initialized.");
                return;
            }
            
            updateList();
        } catch (Throwable t) {
            showError(t.getMessage());
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void updateList() {
        if (service == null) return;
        List<ClipboardHistoryService.HistoryEntry> history = new ArrayList<>(service.get_history_entries());
        Collections.reverse(history);
        adapter = new ClipboardAdapter(history);
        listView.setAdapter(adapter);
    }

    class ClipboardAdapter extends BaseAdapter {
        List<ClipboardHistoryService.HistoryEntry> allItems;
        List<ClipboardHistoryService.HistoryEntry> filteredItems;
        
        ClipboardAdapter(List<ClipboardHistoryService.HistoryEntry> items) { 
            this.allItems = items; 
            this.filteredItems = new ArrayList<>(items);
        }
        
        void filter(String query) {
            filteredItems.clear();
            if (query.isEmpty()) {
                filteredItems.addAll(allItems);
            } else {
                for (ClipboardHistoryService.HistoryEntry item : allItems) {
                    if (item.content.toLowerCase().contains(query.toLowerCase())) {
                        filteredItems.add(item);
                    }
                }
            }
            notifyDataSetChanged();
        }
        
        @Override public int getCount() { return filteredItems.size(); }
        @Override public Object getItem(int p) { return filteredItems.get(p); }
        @Override public long getItemId(int p) { return p; }
        @Override public View getView(int p, View v, ViewGroup prnt) {
            if (v == null) {
                LinearLayout ll = new LinearLayout(ClipboardHistoryActivity.this);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setPadding(32, 32, 32, 32);
                
                android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
                shape.setCornerRadius(24);
                shape.setColor(0xFFFFFFFF);
                ll.setBackground(shape);
                
                TextView num = new TextView(ClipboardHistoryActivity.this);
                num.setId(android.R.id.text1);
                num.setTextSize(18);
                num.setTypeface(null, android.graphics.Typeface.BOLD);
                num.setTextColor(0xFF2196F3);
                num.setPadding(0, 0, 32, 0);
                
                LinearLayout vll = new LinearLayout(ClipboardHistoryActivity.this);
                vll.setOrientation(LinearLayout.VERTICAL);
                
                TextView text = new TextView(ClipboardHistoryActivity.this);
                text.setId(android.R.id.text2);
                text.setTextSize(16);
                text.setTextColor(0xFF212121);
                text.setMaxLines(3);
                text.setEllipsize(android.text.TextUtils.TruncateAt.END);
                
                TextView sub = new TextView(ClipboardHistoryActivity.this);
                sub.setId(android.R.id.hint);
                sub.setTextSize(11);
                sub.setTextColor(0xFF9E9E9E);
                sub.setPadding(0, 8, 0, 0);
                
                vll.addView(text);
                vll.addView(sub);
                ll.addView(num);
                ll.addView(vll);
                v = ll;
            }
            ClipboardHistoryService.HistoryEntry ent = filteredItems.get(p);
            ((TextView)v.findViewById(android.R.id.text1)).setText("#" + (allItems.indexOf(ent) + 1));
            ((TextView)v.findViewById(android.R.id.text2)).setText(ent.content);
            String info = ent.timestamp + (ent.version.isEmpty() ? "" : " • V" + ent.version);
            ((TextView)v.findViewById(android.R.id.hint)).setText(info);
            
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
                
                if (ext.equals(".pdf")) {
                    exportAsPdf(text, desc);
                } else if (ext.equals(".txt")) {
                    exportAsTxt(text, desc);
                } else {
                    exportAsGenericText(text, desc, ext);
                }
            })
            .show();
    }

    private void exportAsGenericText(String text, String desc, String ext) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
        String content = "Time: " + timestamp + "\nDescription: " + desc + "\nContent:\n" + text;
        try {
            java.io.File file = new java.io.File(getExternalFilesDir(null), "note_" + System.currentTimeMillis() + ext);
            java.io.FileWriter writer = new java.io.FileWriter(file);
            writer.write(content);
            writer.close();
            shareFile(file, "text/plain");
        } catch (Exception e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void exportAsTxt(String text, String desc) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
        String content = "Time: " + timestamp + "\nDescription: " + desc + "\nContent: " + text;
        try {
            java.io.File file = new java.io.File(getExternalFilesDir(null), "note_" + System.currentTimeMillis() + ".txt");
            java.io.FileWriter writer = new java.io.FileWriter(file);
            writer.write(content);
            writer.close();
            shareFile(file, "text/plain");
        } catch (Exception e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void exportAsPdf(String text, String desc) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            Toast.makeText(this, "PDF export requires Android 4.4+", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            android.graphics.pdf.PdfDocument document = new android.graphics.pdf.PdfDocument();
            android.graphics.pdf.PdfDocument.PageInfo pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create();
            android.graphics.pdf.PdfDocument.Page page = document.startPage(pageInfo);
            android.graphics.Canvas canvas = page.getCanvas();
            android.graphics.Paint paint = new android.graphics.Paint();
            paint.setTextSize(12);
            
            int x = 50, y = 50;
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
            
            canvas.drawText("Time: " + timestamp, x, y, paint); y += 20;
            canvas.drawText("Description: " + desc, x, y, paint); y += 30;
            
            paint.setTextSize(10);
            String[] lines = text.split("\n");
            for (String line : lines) {
                canvas.drawText(line, x, y, paint);
                y += 15;
                if (y > 800) break; 
            }
            
            document.finishPage(page);
            java.io.File file = new java.io.File(getExternalFilesDir(null), "note_" + System.currentTimeMillis() + ".pdf");
            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            shareFile(file, "application/pdf");
        } catch (Exception e) {
            Toast.makeText(this, "PDF Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareFile(java.io.File file, String mimeType) {
        android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(this, "juloo.keyboard2.provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Download/Export Note"));
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

            android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(this, "juloo.keyboard2.provider", file);
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
