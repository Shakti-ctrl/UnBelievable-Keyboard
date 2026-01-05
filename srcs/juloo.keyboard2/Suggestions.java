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

  public void currently_typed_word(String word)
  {
    if (word.equals(""))
    {
      _callback.set_suggestions(NO_SUGGESTIONS);
    }
    else
    {
      // Mocking advanced suggestions for now since OpenBoard classes aren't present
      // In a real scenario, we would link to LatinIME's Suggest engine here.
      List<String> mockSuggestions = new java.util.ArrayList<>();
      mockSuggestions.add(word);
      if (word.length() > 1) {
          mockSuggestions.add(word + "s");
          mockSuggestions.add(word + "ing");
          mockSuggestions.add(word + "ed");
      }
      _callback.set_suggestions(mockSuggestions);
    }
  }

  static final List<String> NO_SUGGESTIONS = Arrays.asList();

  public static interface Callback
  {
    public void set_suggestions(List<String> suggestions);
  }
}
