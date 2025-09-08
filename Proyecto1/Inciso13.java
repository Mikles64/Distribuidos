
public class Inciso13 {
    public static void main(String[] args) {
        double ahorro = 0;
        for(int i = 1; i <= 20; i++){
            ahorro += 10000;
            ahorro = ahorro * 1.05;
            System.out.printf("Ahorro al final del año %d: $%.2f\n", i, ahorro);
        }
        System.out.printf("Ahorro total después de 20 años: $%.2f\n", ahorro);
    }
}
