package juloo.keyboard2;

import java.util.List;
import java.util.Arrays;

/** Keep track of the word being typed and provide suggestions for
    [CandidatesView]. */
public final class Suggestions
{
  Callback _callback;

  public Suggestions(Callback c)
  {
    _callback = c;
  }

  private List<String> dictionary = null;

  public void currently_typed_word(String word)
  {
    if (word.equals(""))
    {
      _callback.set_suggestions(NO_SUGGESTIONS);
      return;
    }

    if (dictionary == null) {
        load_dictionary();
    }

    List<String> matches = new java.util.ArrayList<>();
    String lowerWord = word.toLowerCase();
    
    // Prefix matching from our word list
    if (dictionary != null) {
        for (String dictWord : dictionary) {
            if (dictWord.startsWith(lowerWord)) {
                matches.add(dictWord);
                if (matches.size() >= 5) break;
            }
        }
    }

    // Always include the literal word if not already there
    if (!matches.contains(word)) {
        matches.add(0, word);
    }
    
    _callback.set_suggestions(matches);
  }

  private void load_dictionary() {
      try {
          dictionary = new java.util.ArrayList<>();
          java.io.InputStream is = Config.globalConfig().getContext().getAssets().open("dictionaries/english.txt");
          java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
          String line;
          while ((line = reader.readLine()) != null) {
              if (line.length() > 1) {
                  dictionary.add(line.toLowerCase());
              }
          }
          reader.close();
      } catch (Exception e) {
          android.util.Log.e("Suggestions", "Failed to load dictionary", e);
      }
  }

  static final List<String> NO_SUGGESTIONS = Arrays.asList();

  public static interface Callback
  {
    public void set_suggestions(List<String> suggestions);
  }
}
