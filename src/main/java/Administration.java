import java.util.*;
import java.io.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

// Creez comparator - compararea anului definitiilor dictionarelor
class YearComparator implements Comparator<Definition> {
    // override la metoda compare()
    @Override
    public int compare(Definition d1, Definition d2) {
        return Integer.compare(d1.year, d2.year);
    }
}

// Creez  comparator - compararea cuvintelor dictionarului
class WordComparator implements Comparator<Word> {
    // override la metoda compare()
    @Override
    public int compare(Word w1, Word w2) {
        return w1.word.compareTo(w2.word);
    }
}

public class Administration {
    // Creez dictionarul = un obiect HashMap HMdictionar
    public static HashMap<String, Word> HMdictionar = new HashMap<>();

    public static boolean addWord(Word word, String language) {
        if(HMdictionar.containsKey(language + word.word))
            return false;
        else {
            HMdictionar.put(language + word.word, word);
            return true;
        }
    }

    public static boolean removeWord(String word, String language) {
        if(HMdictionar.containsKey(language + word)) {
            HMdictionar.remove(language + word);
            return true;
        }
        else
            return false;
    }

    public static boolean addDefinitionForWord(String word, String language, Definition definition) {
        int i, j;

        if(HMdictionar.containsKey(language + word)) {
            for (i = 0; i < HMdictionar.get(language + word).getDefinition().size(); i++) {
                if ((HMdictionar.get(language + word).getDefinition()).get(i).dict.equals(definition.dict))
                    if ((HMdictionar.get(language + word).getDefinition()).get(i).dictType.equals(definition.dictType))
                        if ( (HMdictionar.get(language + word).getDefinition()).get(i).year == definition.year ) {
                            for (j = 0; j < (HMdictionar.get(language + word).getDefinition()).get(i).getText().size(); j++) {
                                if ((HMdictionar.get(language + word).getDefinition()).get(i).getText().get(j).equals(definition.text.get(0))) {
                                    return false;
                                }
                            }
                            (HMdictionar.get(language + word).getDefinition().get(i)).getText().add(definition.getText().get(0));
                            return true;
                        }
            }
        }
        return false;
    }

    public static boolean removeDefinition(String word, String language, String dict) {
         if(HMdictionar.containsKey(language+word)) {
            for ( int i = 0; i < HMdictionar.get(language + word).getDefinition().size(); i++) {
                if ((HMdictionar.get(language + word).getDefinition()).get(i).dict.equals(dict)) {
                        (HMdictionar.get(language + word).getDefinition()).remove((HMdictionar.get(language + word).getDefinition()).get(i));
                        return true;
                }
            }
         }
        return false;
    }

public static String translateWord(String word, String fromLanguage, String toLanguage) {
    if(HMdictionar.containsKey(fromLanguage+word))
        return HMdictionar.get(fromLanguage+word).word_en;
    else
        return word;
}

public static String translateSentence(String sentence, String fromLanguage, String toLanguage) {
    String[] rezultat = sentence.split("\\s");
    StringBuilder traducere = new StringBuilder();

    // Traduc cuvint cu cuvint si concatenez in traducere
    for (String s : rezultat)
        traducere.append(translateWord(s, fromLanguage, toLanguage)).append(" ");

    return traducere.toString();
}

public static ArrayList<String> translateSentences(String sentence, String fromLanguage, String toLanguage) {
        String[] rezultat = sentence.split("\\s");
        ArrayList<String> ListaSinonime  = new ArrayList<>();

        // Traduc propozitia initiala
        StringBuilder traducere = new StringBuilder();
        traducere.append(translateWord(rezultat[0], fromLanguage, toLanguage)).append(" ");
        for (int i = 1; i < rezultat.length; i++)
            traducere.append(translateWord(rezultat[i], fromLanguage, toLanguage)).append(" ");

        ListaSinonime.add(traducere.toString());

        // Furnizez traducerea sinonimelor substantivului din propozitia initiala
        String noun = rezultat[0];
        int i, k, l;

        if(HMdictionar.containsKey(fromLanguage+noun)) {

            for (i = 0; i < HMdictionar.get(fromLanguage+noun).getDefinition().size(); i++) {

                if ((HMdictionar.get(fromLanguage + noun).getDefinition().get(i).dictType).equals("synonyms")) { // Am gasit dictionarul cu sinonime
                    // Aleg primele 3 variante

                    k = Math.min(HMdictionar.get(fromLanguage + noun).getDefinition().get(i).getText().size(), 2);
                    for (int j = 0; j < k; j++) {
                        String word = HMdictionar.get(fromLanguage + noun).getDefinition().get(i).getText().get(j);
                        traducere = new StringBuilder();
                        traducere.append(translateWord(word, fromLanguage, toLanguage)).append(" ");
                        for (l = 1; l < rezultat.length; l++) {
                            traducere.append(translateWord(rezultat[l], fromLanguage, toLanguage)).append(" ");
                        }
                        ListaSinonime.add(traducere.toString());
                    }
                }
            }
        }

    return ListaSinonime;
    }

    public static ArrayList<Definition> getDefinitionsForWord(String word, String language) {
        ArrayList<Definition> ListaDefinitii  = new ArrayList<>();
        if(HMdictionar.containsKey(language+word)) {
            ListaDefinitii.addAll(HMdictionar.get(language + word).getDefinition());
        }

        return ListaDefinitii;
    }

public static void exportDictionary(String language) {
    ArrayList<Word> ListaW = new ArrayList<>();

    HMdictionar.forEach((key, value) -> {
        if ((key.substring(0, 2)).equals(language))
            ListaW.add(value);
    });

    ListaW.sort(new WordComparator());

    for (Word w : ListaW )
        w.getDefinition().sort(new YearComparator());

    String output = language + "_dictionar.json";
    try (Writer writer = new FileWriter(output)) {
        Gson gson = new GsonBuilder().create();
        gson.toJson(ListaW, writer);
    }  catch (IOException e) {
        e.printStackTrace();
    }
}

// +++++++++++++++++++++++++++++++ MAIN +++++++++++++++++++++++++++++++

    public static void main(String[] args) throws Exception {

        /* Citeste numele fisierului */

        File folder = new File(".//dictionare");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {
            if (listOfFiles[i].isFile()) {
                String fileName = listOfFiles[i].getName();
                String limba = fileName.substring(0, 2);

                BufferedReader br = new BufferedReader(new FileReader(".//dictionare/" + fileName));

                List<Word> cuvinte  = new Gson().fromJson(br, new TypeToken<List<Word>>() {}.getType());

                // Pune fiecare valoare din lista in Map
                for (Word cuvint : cuvinte) {
                    HMdictionar.put(limba + cuvint.getWord(), cuvint);
                }
            }
        }

        // Afisez hashmap dictionar
        HMdictionar.forEach((key, value) -> System.out.println(key + " " + value.word));

        // testare 1 - functionalitate 2
        System.out.println("testare 1 - functionalitate 2");
        System.out.println("-----------------------------");

        Word cuvint_nou = new Word("chien","dog","noun");
        cuvint_nou.setSingular("chien");
        cuvint_nou.setPlural("chiens");
        Definition def_nou = new Definition("Larousse", "synonyms", 2000);
            def_nou.setText("greffier");
            def_nou.setText("mistigri");
            def_nou.setText("matou");
            def_nou.setText("minet");
        cuvint_nou.setDefinitions(def_nou);

        Definition def_nou1 = new Definition("Roman", "synonyms", 2000);
        def_nou.setText("Mammifère carnivore (félidé), sauvage ou domestique, au museau court et arrondi");
        def_nou.setText("Familier. Terme d'affection, de tendresse adressé à quelqu'un");
        def_nou.setText("Jeu d'enfants dans lequel un des joueurs, le chat, poursuit les autres et tente de les toucher");
        cuvint_nou.setDefinitions(def_nou1);

        if (addWord(cuvint_nou,"fr")) {
            // Afiseaza map
            System.out.println("---------------------------------------------------");
            System.out.println("Dictionar  : " + HMdictionar);
        }
        else {
            // Afiseaza map
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Cuvintul exista !");
        }
        // testare 2 - functionalitate 2
        System.out.println("testare 2 - functionalitate 2");
        System.out.println("-----------------------------");

        Word cuvint_nou1 = new Word("chat","cat","noun");
        if (addWord(cuvint_nou1,"fr")) {
            // Afiseaza map
            System.out.println("---------------------------------------------------");
            System.out.println("Dictionar  : " + HMdictionar);
        }
        else {
            // Afiseaza map
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Cuvintul exista !");
        }

        // testare 1 - functionalitate 3
        System.out.println("testare 1 - functionalitate 3");
        System.out.println("-----------------------------");

        if (removeWord("jeu","fr"))
            System.out.println("S-a sters ! Cuvintul exista !");
        else
            System.out.println("Cuvintul NU exista !");

        // testare 2 - functionalitate 3
        System.out.println("testare 2 - functionalitate 3");
        System.out.println("-----------------------------");

        if (removeWord("jeu","fr"))
            System.out.println("S-a sters ! Cuvintul exista !");
        else
            System.out.println("Cuvintul NU exista !");

        // testare 1 - functionalitate 4
        System.out.println("testare 1 - functionalitate 4");
        System.out.println("-----------------------------");

        Definition def_nou3 = new Definition("Larousse", "synonyms", 2000);
        def_nou3.setText("definitie noua");
        if (addDefinitionForWord("chien","fr", def_nou3))
            System.out.println("S-a adaugat definitia !");
        else
            System.out.println("Definitia exista !");

        // testare 2 - functionalitate 4
        System.out.println("testare 2 - functionalitate 4");
        System.out.println("-----------------------------");

        Definition def_nou4= new Definition("Larousse", "synonyms", 2000);
        def_nou4.setText("definitie noua");
        if (addDefinitionForWord("chien","fr",def_nou4))
            System.out.println("S-a adaugat definitia !");
        else
            System.out.println("Definitia exista !");

        // testare 1 - functionalitate 5
        System.out.println("testare 1 - functionalitate 5");
        System.out.println("-----------------------------");

        if (removeDefinition("chien","fr","Larousse"))
            System.out.println("S-a sters definitia din dictionar !");
        else
            System.out.println("Definitia NU exista !");

        // testare 2 - functionalitate 5
        System.out.println("testare 2 - functionalitate 5");
        System.out.println("-----------------------------");

        if (removeDefinition("chien","fr","Larousse"))
            System.out.println("S-a sters definitia din dictionar !");
        else
            System.out.println("Definitia NU exista !");

        // testare 1 - functionalitate 6
        System.out.println("testare 1 - functionalitate 6");
        System.out.println("-----------------------------");

        System.out.println(translateWord("jeu","fr","en"));

        // testare 2 - functionalitate 6
        System.out.println("testare 2 - functionalitate 6");
        System.out.println("-----------------------------");

        System.out.println(translateWord("pisică","ro","en"));

        // testare 1 - functionalitate 7
        System.out.println("testare 1 - functionalitate 7");
        System.out.println("-----------------------------");

        String sentence = "pisica merge";
        System.out.println(translateSentence(sentence,"ro","en"));

        // testare 2 - functionalitate 7
        System.out.println("testare 2 - functionalitate 7");
        System.out.println("-----------------------------");
        String sentence1 = "chat mange";
        System.out.println(translateSentence(sentence1,"fr","en"));

        // testare 1 - functionalitate 8
        System.out.println("testare 1 - functionalitate 8");
        System.out.println("-----------------------------");
        String sentence3 = "pisica merge";
        (translateSentences(sentence3,"ro","en")).forEach(System.out::println);

        // testare 2 - functionalitate 8
        System.out.println("testare 2 - functionalitate 8");
        System.out.println("-----------------------------");
        String sentence4 = "chat mange";
        (translateSentences(sentence4,"fr","en")).forEach(System.out::println);

        // testare 1 - functionalitate 9
        System.out.println("testare 1 - functionalitate 9");
        System.out.println("-----------------------------");

        // Apeleaza functia de sortare
        ArrayList<Definition> d = getDefinitionsForWord("pisica","ro");
        d.sort(new YearComparator());
        d.forEach(System.out::println);

        // testare 2 - functionalitate 9
        System.out.println("testare 2 - functionalitate 9");
        System.out.println("-----------------------------");

        // Apeleaza functia de sortare
        d = getDefinitionsForWord("chat","fr");
        d.sort(new YearComparator());
        d.forEach(System.out::println);

        // testare 1 - functionalitate 10
        System.out.println("testare 1 - functionalitate 10");
        System.out.println("-----------------------------");
        exportDictionary("ro");

        // testare 2 - functionalitate 10
        System.out.println("testare 2 - functionalitate 10");
        System.out.println("-----------------------------");
        exportDictionary("fr");

        //***************** END *****************
    }
}