/*
    Proyecto #2
    Rodríguez Gutiérrez Miguel Francisco
    7CM4
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;


public class Proyecto2 extends JFrame {
    private SpacePanel panel;
    private int n; // Numero de asteriodes
    private Nave nave;
    private ArrayList<Asteroide> asteroides;
    private Timer timer;
    private boolean gameOver;
    private double progress;
    private Random random;

    public Proyecto2(int n) {
        this.n = n;
        this.gameOver = false;
        this.progress = 0;
        this.random = new Random();
        
        setTitle("Simulación Espacial - Proyecto 2");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        panel = new SpacePanel();
        add(panel);
        
        initializeGame();
        
        // Timer para la animacion
        timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    updateGame();
                    panel.repaint();
                }
            }
        });
    }
    
    private void initializeGame() {
        // Inicializar la nave en un punto aleatorio en el lado izquierdo
        int startY = random.nextInt(600) + 60;
        nave = new Nave(0, startY);
        
        // Inicializar asteroides
        asteroides = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            asteroides.add(crearAsteroide());
        }
    }
    
    private Asteroide crearAsteroide() {
        int tam = random.nextInt(30) + 20;
        int x = random.nextInt(1000) + 50;
        int y = random.nextInt(500);
        double vel = random.nextDouble() * 2 + 0.5;
        double dir = random.nextDouble() * 2 * Math.PI;
        
        return new Asteroide(x, y, tam, vel, dir);
    }
    
    private void updateGame() {
        // Actualizar la nave
        nave.update();
        
        // Actualizar progreso
        progress = Math.min(100, nave.getX() / 1230f * 100);
        
        // Verificar asteroides cercanos y activar evitacion si es necesario
        for (Asteroide a : asteroides) {
            // Calcular distancia entre nave y asteroide
            double distancia = (Math.sqrt(Math.pow(nave.getX() - a.getX(), 2) + Math.pow(nave.getY() - a.getY(), 2))) + a.tam/2;
            
            // Si el asteroide está cerca y en el camino, activar evitacion
            if (distancia < 150 && a.getX() > nave.getX() && a.getX() < nave.getX() + 400) {
                nave.evitando = true;
                break;
            }
        }
        
        
        // Actualizar asteroides
        for (int i = 0; i < asteroides.size(); i++) {
            Asteroide a = asteroides.get(i);
            a.update();
            
            // Verificar colisiones
            if (nave.collidesWith(a)) {
                gameOver = true;
                timer.stop();
                JOptionPane.showMessageDialog(this, 
                    "¡Colision! Porcentaje de avance: " + String.format("%.2f", progress) + "%");
                return;
            }
        }
        
        // Verificar si la nave llego al final
        if (nave.getX() > 1280) {
            gameOver = true;
            timer.stop();
            JOptionPane.showMessageDialog(this, "¡Mision cumplida! Llegaste al 100%");
        }
    }
    
    public void startSimulation() {
        timer.start();
    }
    
    private class SpacePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Fondo negro
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            
            // Dibujar asteroides
            for (Asteroide a : asteroides) {
                a.draw(g);
            }
            
            // Dibujar nave
            nave.draw(g);
            
            // Dibujar informacion de progreso
            g.setColor(Color.WHITE);
            g.setFont(new Font("Consolas", Font.PLAIN, 14));
            g.drawString("Progreso: " + String.format("%.2f", progress) + "%", 20, 20);
            
            // Dibujar mensaje de game over si es necesario
            if (gameOver) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Consolas", Font.PLAIN, 36));
                g.drawString("GAME OVER", 500, 360);
            }
        }
    }
    
    // Clase para la nave espacial
    private class Nave {
        private double x, y;
        private double velocidad;
        private double aceleracion;
        private Polygon shape;
        private boolean evitando;
        
        public Nave(int x, int y) {
            this.x = x;
            this.y = y;
            this.velocidad = 0;
            this.aceleracion = 0;
            this.evitando = false;
            
            // Crear forma de nave
            this.shape = new Polygon();
            shape.addPoint(0, -10);
            shape.addPoint(0, 10);
            shape.addPoint(25, 0);
        }
        
        public void update(){
            if (!evitando)
                aceleracion = 0.1;  // Si no esta evitando, aceleracion positiva
            else
                aceleracion = -0.3; // Si esta evitando, aceleracion negativa
            
            velocidad += aceleracion;
            // Limitar velocidades
            if (velocidad > 8) 
                velocidad = 8;
            else if (velocidad < 0)
                velocidad = 0;
            // Mover nave
            x += velocidad;
            
            // Pequenas variaciones en Y para simular movimiento realista
            y += (random.nextFloat() - 0.5f) * 2;
            
            // Mantener dentro de los limites verticales
            if (y < 20) y = 20;
            if (y > 700) y = 700;
            
            // Reiniciar estado de evitación
            evitando = false;
        }

        public void evitarColision() {
            this.evitando = true;
        }
        
        public void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(x, y);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawPolygon(shape);
            g2d.dispose();
        }
        
        public boolean collidesWith(Asteroide asteroid) {
            // Deteccion simple de colision basada en distancia
            double distance = Math.sqrt(Math.pow(x - asteroid.getX(), 2) + Math.pow(y - asteroid.getY(), 2));
            return distance < (15 + asteroid.getSize() / 2);
        }
        
        public double getX() {
            return x;
        }
        public double getY() {
            return y;
        }
    }
    
    // Clase para los asteroides
    private class Asteroide {
        private double x, y;
        private int tam;
        private double vel;
        private double dir;
        private Polygon shape;
        private Color color;
        
        public Asteroide(double x, double y, int tam, double vel, double dir) {
            this.x = x;
            this.y = y;
            this.tam = tam;
            this.vel = vel;
            this.dir = dir;
            this.color = new Color(150, 150, 150);
            
            // Crear forma irregular de asteroide
            this.shape = crearFormaAsteroide(tam);
        }
        
        private Polygon crearFormaAsteroide(int tam) {
            Polygon polygon = new Polygon();
            int verts = 8; // Numero de vertices para hacerlo irregular
            
            for (int i = 0; i < verts; i++) {
                double ang = 2 * Math.PI * i / verts;
                // Variar el radio para hacerlo irregular
                int radio = tam / 2 + random.nextInt(tam / 4);
                int px = (int) (radio * Math.cos(ang));
                int py = (int) (radio * Math.sin(ang));
                polygon.addPoint(px, py);
            }
            
            return polygon;
        }
        
        public void update() {
            // Mover asteroide segun su direccion y velocidad
            x += vel * Math.cos(dir);
            y += vel * Math.sin(dir);
            
            // Rebotar en los bordes con un angulo de reflexion
            if (x <= 0) {
                x = 0;
                dir = Math.PI - dir;
            } else if (x >= 1280) {
                x = 1280;
                dir = Math.PI - dir;
            }
            
            if (y <= 0) {
                y = 0;
                dir = -dir;
            } else if (y >= 720) {
                y = 720;
                dir = -dir;
            }
            
            // Asegurar que el angulo este en el rango [0, 2π]
            while (dir < 0) dir += 2 * Math.PI;
            while (dir >= 2 * Math.PI) dir -= 2 * Math.PI;
        }
        
        public void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(x, y);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawPolygon(shape);
            g2d.dispose();
        }
        
        public double getX() {
            return x;
        }
        
        public double getY() {
            return y;
        }
        
        public int getSize() {
            return tam;
        }
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java Proyecto2 <numero de asteroides>");
            return;
        }
        
        try {
            int n = Integer.parseInt(args[0]);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Proyecto2 simulation = new Proyecto2(n);
                    simulation.setVisible(true);
                    simulation.startSimulation();
                }
            });
        } catch (NumberFormatException e) {
            System.out.println("Por favor ingrese un numero valido de asteroides");
        }
    }
}