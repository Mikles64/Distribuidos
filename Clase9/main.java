import java.util.*;

class Main
{
    private static ArrayList<String> CURPs = new ArrayList<>();  
    public static void main(String[] args)
    {
        int num = Integer.parseInt(args[0]); //num de curps
        String sex = args[1]; //h o m

        ArrayList<String> curps = new ArrayList<>();
        for(int i = 0; i < num; i++) {
            curps.add(getCURP());
        }
        Iterator<String> it = curps.iterator();
        while (it.hasNext()) {
            String curp = it.next();
            if (curp.charAt(10) == sex.charAt(0)) {
                it.remove();
            }
        }
        System.out.println(sex.toUpperCase());
        for (String c : curps) {
            System.out.println(c);
        }
    }
    static String getCURP()
    {
        String Letra = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Numero = "0123456789";
        String Sexo = "HM";
        String Entidad[] = {"AS", "BC", "BS", "CC", "CS", "CH", "CL", "CM", "DF", "DG", "GT", "GR", "HG", "JC", "MC", "MN", "MS", "NT", "NL", "OC", "PL", "QT", "QR", "SP", "SL", "SR", "TC", "TL", "TS", "VZ", "YN", "ZS"};
        int indice;
        
        StringBuilder sb = new StringBuilder(18);
        
        for (int i = 1; i < 5; i++) {
            indice = (int) (Letra.length()* Math.random());
            sb.append(Letra.charAt(indice));        
        }
        
        for (int i = 5; i < 11; i++) {
            indice = (int) (Numero.length()* Math.random());
            sb.append(Numero.charAt(indice));        
        }
        indice = (int) (Sexo.length()* Math.random());
        sb.append(Sexo.charAt(indice));        
        
        sb.append(Entidad[(int)(Math.random()*32)]);
        for (int i = 14; i < 17; i++) {
            indice = (int) (Letra.length()* Math.random());
            sb.append(Letra.charAt(indice));        
        }
        for (int i = 17; i < 19; i++) {
            indice = (int) (Numero.length()* Math.random());
            sb.append(Numero.charAt(indice));        
        }
        CURPs.add(sb.toString());
        return sb.toString();

        
    }           
}
