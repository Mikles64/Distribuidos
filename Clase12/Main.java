    import java.util.*;
 
    class Main
    {
        public static void main(String[] args)
        {
            int num = Integer.parseInt(args[0]);
            int numCURPs = Integer.parseInt(args[1]);
 
            ArrayList<ArrayList<String>> ListaCURPs = new ArrayList<>();
 
            // Generar m listas con n CURPs aleatorias cada una
            for (int i = 0; i < numCURPs; i++) {
                ArrayList<String> curps = new ArrayList<>();
                for (int j = 0; j < num; j++) {
                    curps.add(getCURP());
                }
                ListaCURPs.add(curps);  
            }
 
            // Imprimir desordenadas
            System.out.println("----CURPs normalitas---");
            for (int i = 0; i < ListaCURPs.size(); i++) {
                System.out.println("ArrayList " + (i + 1) + ":");
                for (String curp : ListaCURPs.get(i)) {
                    System.out.println(curp);
                }
                System.out.println();
            }
 
            // imprimir ordenadas
            System.out.println("----CURPs ordenadas----");
            for (int i = 0; i < ListaCURPs.size(); i++) {
                System.out.println("ArrayList " + (i + 1) + " ordenado:");
                ordenarEImprimir(ListaCURPs.get(i));
                System.out.println();
            }
        }
 
        // ordenar con burbuja
        private static void ordenarEImprimir(ArrayList<String> curps) {
            // arraylist temporar para ordenar
            ArrayList<String> temporal = new ArrayList<>(curps);
            
            // ordenar usando las primeras 4 letras
            for (int i = 0; i < temporal.size() - 1; i++) {
                for (int j = 0; j < temporal.size() - i - 1; j++) {
                    String curp1 = temporal.get(j);
                    String curp2 = temporal.get(j + 1);
                    
                    if (curp1.substring(0, 4).compareTo(curp2.substring(0, 4)) > 0) {
 
                        temporal.set(j, curp2);
                        temporal.set(j + 1, curp1);
                    }
                }
            }
            
            // imprimir ordenado el arraylist temporal
            for (String curp : temporal) {
                System.out.println(curp);
            }
        }
 
        private static String getCURP()
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
            
            return sb.toString();
        }           
    }
 