import java.util.*;

public class Word {
    String word;
    String word_en;
    String type;
    final List<String> singular;
    final List<String> plural;
    final List<Definition> definitions;

    public Word(String word, String word_en, String type) {
        this.word = word;
        this.word_en = word_en;
        this.type = type;
        this.singular = new ArrayList<>();
        this.plural = new ArrayList<>();
        this.definitions = new ArrayList<>();
    }

    public String getWord() {
        return this.word;
    }

    public void setSingular(String singular)
    {
        this.singular.add(singular);
    }

    public void setPlural(String plural)
    {
        this.plural.add(plural);
    }

    public void setDefinitions(Definition definitions) {
        this.definitions.add(definitions);
    }

    public ArrayList<Definition> getDefinition() {
        return (ArrayList<Definition>) definitions;
    }

    @Override
    public String toString() {
        return "<Word>" + "\n" + "word = " + word  + "\n" + "word_en = " + word_en + "\n" +
                "type = " + type + "\n" + "singular = " + singular +"\n"+ "plural = " + plural + "\n"
                + definitions + "/<Word>";
    }
}
