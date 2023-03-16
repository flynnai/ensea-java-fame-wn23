public class GraphicalRiddle extends Riddle{
    public GraphicalRiddle(String answer, int maxNumberOfFails){
        super(answer,maxNumberOfFails);
    }
    @Override
    public String toString(){
        return answer;
    }
}
