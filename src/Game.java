import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    ArrayList<Riddle> riddles = new ArrayList<>();
    final int numberOfRiddles;

    public Game(int numberOfRiddles){
        this.numberOfRiddles = numberOfRiddles;
        try{
            Random random = new Random();
            long numberOfLines =
                    Files.lines(Paths.get("./data/able.txt")).count();
            System.out.println("Found "+ numberOfLines+" words");
            int maximumSpacing = (int) numberOfLines/numberOfRiddles;
            BufferedReader bufferedReader =
                    new BufferedReader(new FileReader("./data/able.txt"));
            for (int i=0;i<numberOfRiddles;i++){
                int spacing = random.nextInt(maximumSpacing);
                for (int j=0;j<spacing;j++){
                    bufferedReader.readLine();
                }
                riddles.add(
                            new GraphicalRiddle(bufferedReader.readLine(),7));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            System.out.println("Been there, done that");
        }
    }

    void display(){
        for (Riddle r : riddles){System.out.println(r);}
    }
}
