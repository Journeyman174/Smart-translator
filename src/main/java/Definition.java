import java.util.*;

public class Definition {
    String dict;
    String dictType;
    int year;
    List<String> text;

    public Definition(String dict, String dictType, int year) {
        this.dict = dict;
        this.dictType = dictType;
        this.year = year;
        this.text = new ArrayList<>();
    }

    public void setText(String text)
    {
        this.text.add(text);
    }

    public ArrayList<String> getText(){
        return (ArrayList<String>) text;
    }

    @Override
    public String toString() {
        return "<Definition>" + "\n" + "dict = " + dict + "\n" + "dictType = " + dictType + "\n" + "an = " + year + "\n" +
                "text = " + text + "\n" + "/<Definition>";
    }
}