public class Inciso12 {
    public static void main(String[] args){
        int suma, divisor;
        for(int i = 1; i <= 10000; i++){
            suma = 0;
            for(divisor = 1; divisor <= i/2; divisor++){
                if(i % divisor == 0)
                    suma += divisor;
            }
            if(suma == i)
                System.out.printf("%d es un numero perfecto\n", i);
        }
    }
}
