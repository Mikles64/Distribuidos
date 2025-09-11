public class Rectangulo {
    private Coordenada superiorIzq, inferiorDer;
    
    public Rectangulo(){
        superiorIzq = new Coordenada(0,0);
        inferiorDer = new Coordenada(0,0);
    }
    
    public Rectangulo(double xSupIzq, double ySupIzq, double xInfDer, double yInfDer){
        superiorIzq = new Coordenada(xSupIzq, ySupIzq);
        inferiorDer = new Coordenada(xInfDer, yInfDer);        
    }

    public Rectangulo(Coordenada supIzq, Coordenada infDer){
        this.superiorIzq = new Coordenada(supIzq.abcisa(), supIzq.ordenada());
        this.inferiorDer = new Coordenada(infDer.abcisa(), infDer.ordenada());
    }
    
    //Metodo getter de la coordenada superior izquierda
    public Coordenada superiorIzquierda( ) { return superiorIzq; }
    //Metodo getter de la coordenada inferior derecha
    public Coordenada inferiorDerecha( ) { return inferiorDer; }
    
    //Sobreescritura del método de la superclase objeto para imprimir con System.out.println( )
    @Override
    public String toString( ) {
        return "Esquina superior izquierda: " + superiorIzq + "\tEsquina inferior derecha:" + inferiorDer + "\n";
    }
}