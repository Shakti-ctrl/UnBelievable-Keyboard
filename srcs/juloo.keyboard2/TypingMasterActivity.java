package juloo.keyboard2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TypingMasterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#121212"));
        root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Header
        TextView header = new TextView(this);
        header.setText("ELITE MOBILE TYPING COACH");
        header.setTextSize(22);
        header.setTextColor(Color.parseColor("#00E5FF"));
        header.setTypeface(Typeface.DEFAULT_BOLD);
        header.setGravity(Gravity.CENTER);
        header.setPadding(0, 50, 0, 50);
        root.addView(header);

        // Content
        ExpandableListView expandableListView = new ExpandableListView(this);
        expandableListView.setGroupIndicator(null);
        expandableListView.setDividerHeight(1);
        expandableListView.setDivider(null);
        
        List<String> listDataHeader = new ArrayList<>();
        HashMap<String, List<String>> listDataChild = new HashMap<>();

        // 1. Foundation
        listDataHeader.add("1️⃣ FOUNDATION (STARTUP LEVEL)");
        List<String> sec1 = new ArrayList<>();
        sec1.add("• Mechanics: Thumb-only vs Hybrid styles.");
        sec1.add("• Placement: Precise QWERTY thumb zones.");
        sec1.add("• Grip: Scaling reach based on phone size.");
        sec1.add("• Warm-up: 30s thumb stretch & finger flex.");
        listDataChild.put(listDataHeader.get(0), sec1);

        // 2. Placement
        listDataHeader.add("2️⃣ FINGER & THUMB PLACEMENT");
        List<String> sec2 = new ArrayList<>();
        sec2.add("• Home Zones: Neutral resting positions.");
        sec2.add("• Arc Training: Short vs Diagonal reach.");
        sec2.add("• Edge Keys: Efficient Q, P, Z, M targeting.");
        sec2.add("• Mastery: Backspace & Punctuation flow.");
        listDataChild.put(listDataHeader.get(1), sec2);

        // 3. Speed Phases
        listDataHeader.add("3️⃣ SPEED BUILDING PHASES");
        List<String> sec3 = new ArrayList<>();
        sec3.add("• L1 Accuracy: 20 WPM / 98% Acc (Slow Drills)");
        sec3.add("• L2 Rhythm: 35 WPM / 95% Acc (Flow Typing)");
        sec3.add("• L3 Bursts: 50 WPM / 90% Acc (Timed Sprints)");
        sec3.add("• L4 Fatigue: 65 WPM / 90% Acc (Endurance)");
        sec3.add("• L5 Elite: 80+ WPM / 85% Acc (Chat Sims)");
        listDataChild.put(listDataHeader.get(2), sec3);

        // 4. Hardcore Drills
        listDataHeader.add("4️⃣ HARDCORE DRILLS (ADVANCED)");
        List<String> sec4 = new ArrayList<>();
        sec4.add("• Bigrams/Trigrams: 'th', 'ing', 'ion', 'est'.");
        sec4.add("• Alternating: Left-Right thumb switch speed.");
        sec4.add("• No-Look: Muscle memory blind drills.");
        sec4.add("• Chaos: Random string recovery speed.");
        listDataChild.put(listDataHeader.get(3), sec4);

        // 5. Pro Techniques
        listDataHeader.add("5️⃣ THUMB TECHNIQUES (PRO LEVEL)");
        List<String> sec5 = new ArrayList<>();
        sec5.add("• Minimization: Tiny micro-movements only.");
        sec5.add("• Predictive: Pre-positioning for next key.");
        sec5.add("• Bounce: Kinetic energy transfer taps.");
        sec5.add("• Glide vs Tap: Strategic speed switching.");
        listDataChild.put(listDataHeader.get(4), sec5);

        // 6. Metrics
        listDataHeader.add("6️⃣ TESTING & METRICS");
        List<String> sec6 = new ArrayList<>();
        sec6.add("• Formula: (Chars/5) / (Time/60) = WPM.");
        sec6.add("• Real Acc: Zero-penalty error tracking.");
        sec6.add("• Fatigue: Speed drop-off index analysis.");
        listDataChild.put(listDataHeader.get(5), sec6);

        // 7. Real-World
        listDataHeader.add("7️⃣ REAL-WORLD APPLICATION");
        List<String> sec7 = new ArrayList<>();
        sec7.add("• Simulation: WhatsApp & Telegram chat flow.");
        sec7.add("• Coding: Brackets & Special char speed.");
        sec7.add("• Exams: Long-form structural typing.");
        listDataChild.put(listDataHeader.get(6), sec7);

        // 8. Mental
        listDataHeader.add("8️⃣ MENTAL & NEURO TRAINING");
        List<String> sec8 = new ArrayList<>();
        sec8.add("• Focus: Tunnel vision typing concentration.");
        sec8.add("• Neuro: 21-Day habit formation ritual.");
        sec8.add("• Motor: Synaptic link speed building.");
        listDataChild.put(listDataHeader.get(7), sec8);

        // 9. Advanced Tips
        listDataHeader.add("9️⃣ ADVANCED TIPS & SECRETS");
        List<String> sec9 = new ArrayList<>();
        sec9.add("• Height: Vertical layout optimization.");
        sec9.add("• Haptics: Audio/Vibration feedback sync.");
        sec9.add("• Posture: Wrist angle for 0-strain speed.");
        listDataChild.put(listDataHeader.get(8), sec9);

        expandableListView.setAdapter(new ExpandableListAdapter(this, listDataHeader, listDataChild));
        root.addView(expandableListView);

        setContentView(root);
    }

    class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private List<String> listDataHeader;
        private HashMap<String, List<String>> listDataChild;

        public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
            this.context = context;
            this.listDataHeader = listDataHeader;
            this.listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final String childText = (String) getChild(groupPosition, childPosition);
            if (convertView == null) {
                TextView textView = new TextView(this.context);
                textView.setPadding(60, 20, 40, 20);
                textView.setTextSize(15);
                textView.setTextColor(Color.parseColor("#BBBBBB"));
                convertView = textView;
            }
            ((TextView) convertView).setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LinearLayout groupLayout = new LinearLayout(this.context);
                groupLayout.setOrientation(LinearLayout.VERTICAL);
                groupLayout.setPadding(40, 30, 40, 30);
                
                TextView textView = new TextView(this.context);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setTextSize(17);
                textView.setTextColor(Color.parseColor("#00E5FF"));
                groupLayout.addView(textView);
                
                View divider = new View(this.context);
                divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
                divider.setBackgroundColor(Color.parseColor("#333333"));
                groupLayout.addView(divider);
                
                convertView = groupLayout;
            }
            ((TextView) ((LinearLayout)convertView).getChildAt(0)).setText(headerTitle);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
