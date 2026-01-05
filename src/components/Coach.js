import React from 'react';

const Coach = () => {
  const sections = [
    {
      id: 1,
      title: "1️⃣ FOUNDATION (STARTUP LEVEL)",
      content: [
        "Explain mobile typing mechanics (thumb-only, thumb+index, hybrid styles).",
        "Exact thumb placement zones on QWERTY layout.",
        "How phone size affects thumb reach.",
        "Optimal grip styles (small phone, large phone, tablet).",
        "Common beginner mistakes and how to eliminate them.",
        "Warm-up finger & thumb exercises (before typing)."
      ]
    },
    {
      id: 2,
      title: "2️⃣ FINGER & THUMB PLACEMENT SYSTEM",
      content: [
        "Precise thumb home zones (left thumb, right thumb).",
        "When to use one thumb vs two thumbs.",
        "Thumb arc movement training (short taps, long reach, diagonal reach).",
        "How to hit edge keys (Q, P, Z, M) efficiently.",
        "Spacebar, backspace, shift, and punctuation mastery.",
        "One-hand typing optimization."
      ]
    },
    {
      id: 3,
      title: "3️⃣ SPEED BUILDING PHASES (LEVEL-WISE)",
      content: [
        "Level 1: Accuracy-first slow drills (Target: 20 WPM, 98% Acc)",
        "Level 2: Rhythm typing (flow-based typing) (Target: 35 WPM, 95% Acc)",
        "Level 3: Speed bursts (short timed sprints) (Target: 50 WPM, 90% Acc)",
        "Level 4: Sustained speed typing (fatigue resistance) (Target: 65 WPM, 90% Acc)",
        "Level 5: Extreme speed challenge (real-world chat simulation) (Target: 80+ WPM, 85% Acc)"
      ]
    },
    {
      id: 4,
      title: "4️⃣ HARDCORE DRILLS (ADVANCED)",
      content: [
        "Bigram and trigram drills (th, ing, ion, est).",
        "Alternating-thumb drills.",
        "Error-recovery drills (fix mistakes fast without breaking flow).",
        "No-look typing drills (visual deprivation).",
        "Random word chaos drills.",
        "Sentence complexity drills.",
        "Long paragraph endurance typing."
      ]
    },
    {
      id: 5,
      title: "5️⃣ THUMB TECHNIQUES (PRO LEVEL)",
      content: [
        "Micro-movement minimization.",
        "Predictive finger movement without AI suggestions.",
        "Thumb bounce technique.",
        "Glide vs tap strategy comparison.",
        "When to intentionally slow down to go faster long-term."
      ]
    },
    {
      id: 6,
      title: "6️⃣ TESTING & METRICS",
      content: [
        "WPM calculation system.",
        "Real accuracy (not fake accuracy).",
        "Consistency score.",
        "Fatigue index.",
        "Improvement graph logic.",
        "Daily / weekly / monthly assessments."
      ]
    },
    {
      id: 7,
      title: "7️⃣ REAL-WORLD APPLICATION TRAINING",
      content: [
        "WhatsApp / Telegram chat simulation.",
        "Coding-like text drills.",
        "Exam typing drills.",
        "Note-taking speed training.",
        "Long message typing without errors."
      ]
    },
    {
      id: 8,
      title: "8️⃣ MENTAL & NEURO TRAINING",
      content: [
        "Focus training for typing.",
        "Reducing cognitive load while typing.",
        "Muscle memory reinforcement techniques.",
        "Habit formation system (21-day, 45-day plans)."
      ]
    },
    {
      id: 9,
      title: "9️⃣ ADVANCED TIPS & SECRETS",
      content: [
        "Keyboard height & layout optimization.",
        "Sound & haptic feedback usage.",
        "Best typing posture while sitting, standing, lying.",
        "How elite mobile typists train.",
        "How to break speed plateaus."
      ]
    }
  ];

  return (
    <div style={{ padding: '20px', backgroundColor: '#121212', color: '#e0e0e0', minHeight: '100vh' }}>
      <h1 style={{ textAlign: 'center', color: '#00e5ff', marginBottom: '30px' }}>Elite Mobile Typing Coach</h1>
      <div className="accordion" id="coachAccordion">
        {sections.map((section) => (
          <div key={section.id} className="card" style={{ backgroundColor: '#1e1e1e', border: '1px solid #333', marginBottom: '10px' }}>
            <div className="card-header" id={`heading${section.id}`} style={{ backgroundColor: '#252525' }}>
              <h2 className="mb-0">
                <button 
                  className="btn btn-link btn-block text-left" 
                  type="button" 
                  data-toggle="collapse" 
                  data-target={`#collapse${section.id}`} 
                  aria-expanded="true" 
                  aria-controls={`collapse${section.id}`}
                  style={{ color: '#00e5ff', fontWeight: 'bold', textDecoration: 'none' }}
                >
                  {section.title}
                </button>
              </h2>
            </div>

            <div id={`collapse${section.id}`} className="collapse" aria-labelledby={`heading${section.id}`} data-parent="#coachAccordion">
              <div className="card-body">
                <ul style={{ listStyleType: 'square' }}>
                  {section.content.map((item, index) => (
                    <li key={index} style={{ marginBottom: '10px' }}>{item}</li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Coach;
