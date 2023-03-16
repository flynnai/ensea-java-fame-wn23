import java.util.ArrayList;

public class YetAnotherTestPolymophism {
    public static void main(String[] args){
        ArrayList<Riddle> demonstration = new ArrayList<>();
        demonstration.add(new Riddle("Antoine",5));
        demonstration.add(new GraphicalRiddle("Tauvel",5));
        demonstration.add(new Riddle("Ensea",5));
        demonstration.add(new GraphicalRiddle("Fame",5));

        demonstration.get(0).guess('t');
        demonstration.get(1).guess('t');


        for (Riddle r : demonstration){
            System.out.println(r);
        }
    }
}
