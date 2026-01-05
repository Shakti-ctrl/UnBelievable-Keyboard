package juloo.keyboard2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class TypingMasterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xFF121212);
        
        TextView title = new TextView(this);
        title.setText("Elite Mobile Typing Coach");
        title.setTextSize(24);
        title.setTextColor(0xFF00E5FF);
        title.setPadding(20, 40, 20, 40);
        title.setGravity(android.view.Gravity.CENTER);
        layout.addView(title);

        ListView listView = new ListView(this);
        List<Section> sections = getSections();
        SectionAdapter adapter = new SectionAdapter(this, sections);
        listView.setAdapter(adapter);
        
        layout.addView(listView);
        setContentView(layout);
    }

    private List<Section> getSections() {
        List<Section> sections = new ArrayList<>();
        sections.add(new Section("1️⃣ FOUNDATION", "Mechanics: thumb-only, hybrid.\nPlacement: Zones, reach, grip.\nExercises: Warm-ups."));
        sections.add(new Section("2️⃣ FINGER & THUMB PLACEMENT", "Home zones, 1 vs 2 thumbs.\nArc movement, edge keys.\nSpace, backspace mastery."));
        sections.add(new Section("3️⃣ SPEED PHASES", "Level 1: 20 WPM (Acc)\nLevel 2: 35 WPM (Rhythm)\nLevel 3: 50 WPM (Burst)\nLevel 4: 65 WPM (Endurance)\nLevel 5: 80+ WPM (Elite)"));
        sections.add(new Section("4️⃣ HARDCORE DRILLS", "Bigrams (th, ing).\nAlternating thumbs.\nNo-look typing.\nChaos drills."));
        sections.add(new Section("5️⃣ PRO THUMB TECHNIQUES", "Movement minimization.\nPredictive movement.\nThumb bounce.\nGlide vs Tap."));
        sections.add(new Section("6️⃣ TESTING & METRICS", "WPM calculation.\nReal accuracy.\nConsistency score.\nFatigue index."));
        sections.add(new Section("7️⃣ REAL-WORLD APP", "Chat simulations.\nCoding drills.\nExam typing.\nNote-taking."));
        sections.add(new Section("8️⃣ MENTAL & NEURO", "Focus training.\nCognitive load.\nMuscle memory.\n21-day plan."));
        sections.add(new Section("9️⃣ ADVANCED TIPS", "Height optimization.\nHaptics usage.\nPosture tips.\nPlateau breaking."));
        return sections;
    }

    private static class Section {
        String title;
        String desc;
        Section(String t, String d) { title = t; desc = d; }
    }

    private static class SectionAdapter extends ArrayAdapter<Section> {
        public SectionAdapter(Context context, List<Section> sections) {
            super(context, 0, sections);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new LinearLayout(getContext());
                ((LinearLayout)convertView).setOrientation(LinearLayout.VERTICAL);
                convertView.setPadding(30, 30, 30, 30);
                
                TextView t = new TextView(getContext());
                t.setId(android.R.id.text1);
                t.setTextColor(0xFF00E5FF);
                t.setTextSize(18);
                t.setTypeface(null, android.graphics.Typeface.BOLD);
                ((LinearLayout)convertView).addView(t);
                
                TextView d = new TextView(getContext());
                d.setId(android.R.id.text2);
                d.setTextColor(0xFFE0E0E0);
                d.setTextSize(14);
                d.setPadding(0, 10, 0, 0);
                ((LinearLayout)convertView).addView(d);
            }
            
            Section section = getItem(position);
            TextView t = convertView.findViewById(android.R.id.text1);
            TextView d = convertView.findViewById(android.R.id.text2);
            t.setText(section.title);
            d.setText(section.desc);
            
            return convertView;
        }
    }
}
