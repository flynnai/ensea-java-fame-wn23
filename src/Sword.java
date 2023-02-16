public class Sword {
    private final String name;
    private double attack;
    private double healthPoint;
    private static int numberOfSwords = 0;

    public Sword(String name, double attack, double healthPoint) {
        this.name = name;
        this.attack = attack;
        this.healthPoint = healthPoint;
        numberOfSwords++;
    }

    public static int getNumberOfSwords() {
        return numberOfSwords;
    }

    @Override
    public String toString() {
        return "Sword{" +
                "name='" + name + '\'' +
                ", attack=" + attack +
                ", healthPoint=" + healthPoint +
                '}';
    }

    public void finalize(){
        numberOfSwords--;
    }

    public void useSword(){
        this.healthPoint--;
    }
}
