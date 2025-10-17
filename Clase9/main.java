    import java.util.*;

    class Main2
    {
        private static ArrayList<String> CURPs = new ArrayList<>();  
        public static void main(String[] args)
        {
            int num = Integer.parseInt(args[0]);

            for (int i = 0; i < num; i++) {
                String curp = getCURP();
                // Insertar en orden ascendente
                int pos = 0;
                Iterator<String> it = CURPs.iterator();
                while (it.hasNext()) {
                    String actual = it.next();
                    if (curp.substring(0, 4).compareTo(actual.substring(0, 4)) > 0) {
                        pos++;
                    } else {
                        break;
                    }
                }

                CURPs.add(pos, curp);
                // Imprimir lista por cada curp
                System.out.println("Lista tras insertar CURP " + (i+1) + ":");
                for (String c : CURPs) {
                    System.out.println(c);
                }
                System.out.println();
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