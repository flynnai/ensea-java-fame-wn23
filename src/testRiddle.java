public class testRiddle {
    public static void main(String[] args){
        Riddle riddle = new Riddle("Computer",7);
        riddle.guess('a');
        System.out.println(riddle);
        riddle.guess('u');
        System.out.println(riddle);
        riddle.guess('o');
        System.out.println(riddle);
        riddle.guess('c');
        System.out.println(riddle);
    }
}


