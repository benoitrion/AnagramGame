package AnagrammeDivers;

import java.io.Serializable;

public class AnagramMsg implements Serializable {

    public static final int PORT_NUM = 54321;

    public enum Type {

        CLT_LOGIN,          // Client login
        CLT_LOGOUT,         // Client logout
        CLT_NEW,            // New game
        CLT_PROPOSE,        // Proposition
        CLT_STOP,           // Client stop the game
        SRV_WORD,           // Server send the word to find
        SRV_ANAGRAM,        // Server send the anagram of the word to find.
        SRV_WORD_FOUND,     // Message says that the word is found
        SRV_WORD_NOT_FOUND, // Message says that the word is not found
        SRV_ERROR           // Error message
    }

    private Type type;
    private String word;
    private String anagram;
    private String error;

    public AnagramMsg(Type type, String word, String anagram, String error) {
        this.type = type;
        this.word = word;
        this.anagram = anagram;
        this.error = error;
    }

    public Type getType() {
        return type;
    }

    public String getWord() {
        return word;
    }

    public String getAnagram() {
        return anagram;
    }

    public String getError() {
        return error;
    }
    
    

}
