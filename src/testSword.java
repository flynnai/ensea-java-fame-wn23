public class testSword {
    public static void main (String[] args){
        Sword s1= new Sword("Dard",20, 100);
        Sword s2= new Sword("Excalibur",100, 100);
        s1= new Sword("Anduril",25, 100);
        s1= new Sword("Keyblade",20, 100);

        System.gc();

        Sword s3=s1;
        s3.useSword();
        s1.useSword();

        System.out.println(s1.toString());
        System.out.println(s2.toString());

        System.out.println("Number of swords : "+s1.getNumberOfSwords());

    }
}
