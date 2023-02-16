import java.util.ArrayList;
import java.util.Locale;

public class Riddle {
    private final String answer;
    private final int maxNumberOfFail;
    private int numberOfFails;
    private ArrayList triedLetters;
    private String displayedString;

    public Riddle(String answer, int maxNumberOfFail) {
        this.answer = answer.toUpperCase(Locale.ROOT);
        this.maxNumberOfFail = maxNumberOfFail;
        this.numberOfFails=0;
        this.triedLetters= new ArrayList();
        displayedString="";
        for (char c : answer.toCharArray()){
            displayedString+="-";
        }
    }

    public boolean guess(char triedLetter){
        boolean ret=false;
        if (numberOfFails<=maxNumberOfFail){
        triedLetter= triedLetter<='z'?
                (char) (triedLetter + 'A' - 'a') :triedLetter;
        triedLetters.add(triedLetter);
        for (int i=0;i<answer.length();i++){
            if (answer.toCharArray()[i]==triedLetter){
                ret=true;
                displayedString=
                        displayedString.substring(0,i)+triedLetter+
                        displayedString.substring(i+1);
            }
        }
        numberOfFails=ret?numberOfFails:numberOfFails+1;
        }
        return ret;
    }

    @Override
    public String toString(){
        return displayedString;
    }
}
