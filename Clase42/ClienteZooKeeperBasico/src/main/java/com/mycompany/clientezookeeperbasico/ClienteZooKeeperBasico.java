/*
 *  MIT License
 *
 *  Copyright (c) 2019 Michael Pogrebinsky - Distributed Systems & Cloud Computing with Java
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.mycompany.clientezookeeperbasico;

import org.apache.zookeeper.*;
import java.io.IOException;
/*
    Nuestro cliente debe implementar la interfaz Watcher para obtener el método process
    el cual permite capturar los eventos provenientes del servidor
*/
public class ClienteZooKeeperBasico implements Watcher {

    private static final String ZOOKEEPER_ADDRESS = "localhost:2181"; // Especificamos dirección y puerto 
    private static final int SESSION_TIMEOUT = 3000; // Timeout, el cliente espera máximo 3 segundos
    private ZooKeeper zooKeeper; // Declaramos un objeto zookeeper para interactuar con el servidor

    public static void main(String[] arg) throws IOException, InterruptedException, KeeperException { // IOExeption para manejo de excepciones, InterruptedException para trabajar con eventos sobre hilos zookeeper, Keeperexception para excepciones propias de zookeeper
        ClienteZooKeeperBasico clienteBasico = new ClienteZooKeeperBasico(); // Creamos una instancia de nuestra clase
        clienteBasico.connectToZookeeper(); // Conexión con servidor Zookeeper
        clienteBasico.run();
        clienteBasico.close();
        System.out.println("Desconectado del servidor Zookeeper. Terminando la aplicación cliente.");
    }

    public void connectToZookeeper() throws IOException {
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this); // INstancia de objeto zookeeper con info del servidor
    }

    private void run() throws InterruptedException {
        synchronized (zooKeeper) {
            zooKeeper.wait(); // Deja al hilo en espera hasta que no llegue alguna notificación
        }
    }

    private void close() throws InterruptedException {
        this.zooKeeper.close();
    }

    @Override
    public void process(WatchedEvent event) {   // Recepción de eventos producidos por el servidor
        switch (event.getType()) {  // Obtenemos tipo de evento (Conexión o desconexión)
            case None:
                if (event.getState() == Event.KeeperState.SyncConnected) {  // Servidor conectado
                    System.out.println("Conectado exitosamente a Zookeeper");
                } else {
                    synchronized (zooKeeper) {  // Servidor 
                        System.out.println("Desconectando de Zookeeper...");
                        zooKeeper.notifyAll();  // Notificar que el servidor se cerró
                    }
                }
        }
    }
}
