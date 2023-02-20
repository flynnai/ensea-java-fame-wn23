import java.util.ArrayList;
import java.util.Locale;

public class Riddle {
    private final String answer;
    private final int maxNumberOfFail;
    private int numberOfFails;
    private ArrayList<String> triedLetters;
    private StringBuilder displayedString;

    public Riddle(String answer, int maxNumberOfFail) {
        this.answer = answer.toUpperCase(Locale.ROOT);
        this.maxNumberOfFail = maxNumberOfFail;
        this.numberOfFails=0;
        this.triedLetters= new ArrayList<>();
        displayedString=new StringBuilder();
        for (char c : answer.toCharArray()){
            displayedString.append("-");
        }
    }

    public boolean guess(char triedLetter){
        boolean ret=false;
        if (numberOfFails<=maxNumberOfFail){
        triedLetter= triedLetter<='z'?
                (char) (triedLetter + 'A' - 'a') :triedLetter;
        triedLetters.add(""+triedLetter);
        int start = 0;
        while((start=answer.indexOf(triedLetter,start))!=-1){
            displayedString.replace(start,start+1,""+triedLetter);
            start++;
            ret=true;
        }
        numberOfFails=ret?numberOfFails:numberOfFails+1;
        }
        return ret;
    }

    @Override
    public String toString(){
        return displayedString.toString();
    }
}
