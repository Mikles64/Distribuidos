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

import networking.WebClient;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Aggregator {
    private WebClient webClient;

    public Aggregator() {
        this.webClient = new WebClient();
    }

    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks) {
        CompletableFuture<String>[] serverFutures = new CompletableFuture[workersAddresses.size()];
        List<String> results = new ArrayList<>(tasks.size());
        
        // Inicializar lista de resultados
        for (int i = 0; i < tasks.size(); i++) {
            results.add("");
        }
        
        // Cola de tareas pendientes
        ConcurrentLinkedQueue<Integer> pendingTasks = new ConcurrentLinkedQueue<>();
        for (int i = 2; i < tasks.size(); i++) {
            pendingTasks.add(i);
        }
        
        // Asignación inicial
        System.out.println("Al servidor " + workersAddresses.get(0) + " se le asigna la tarea: " + tasks.get(0));
        serverFutures[0] = webClient.sendTask(workersAddresses.get(0), tasks.get(0).getBytes())
                .thenApply(result -> {
                    processResult(0, result, tasks.get(0), workersAddresses.get(0), results);
                    assignNextTask(0, serverFutures, pendingTasks, tasks, workersAddresses, results);
                    return result;
                });
        
        System.out.println("Al servidor " + workersAddresses.get(1) + " se le asigna la tarea: " + tasks.get(1));
        serverFutures[1] = webClient.sendTask(workersAddresses.get(1), tasks.get(1).getBytes())
                .thenApply(result -> {
                    processResult(1, result, tasks.get(1), workersAddresses.get(1), results);
                    assignNextTask(1, serverFutures, pendingTasks, tasks, workersAddresses, results);
                    return result;
                });
        
        // Esperar a que todas las tareas se completen
        CompletableFuture.allOf(serverFutures[0], serverFutures[1]).join();
        
        // Procesar cualquier tarea pendiente que pueda haberse quedado
        while (!pendingTasks.isEmpty()) {
            Integer taskIndex = pendingTasks.poll();
            if (taskIndex != null) {
                // Asignar a cualquier servidor disponible
                for (int i = 0; i < serverFutures.length; i++) {
                    if (serverFutures[i].isDone()) {
                        final int serverIndex = i;
                        final int finalTaskIndex = taskIndex;
                        System.out.println("Al servidor " + workersAddresses.get(serverIndex) + " se le asigna la tarea: " + tasks.get(finalTaskIndex));
                        serverFutures[serverIndex] = webClient.sendTask(workersAddresses.get(serverIndex), tasks.get(finalTaskIndex).getBytes())
                                .thenApply(result -> {
                                    processResult(serverIndex, result, tasks.get(finalTaskIndex), workersAddresses.get(serverIndex), results);
                                    return result;
                                });
                        break;
                    }
                }
            }
        }
        
        // Esperar a que se completen todas las tareas finales
        CompletableFuture.allOf(serverFutures).join();
        
        return results;
    }
    
    private void processResult(int serverIndex, String result, String task, String workerAddress, List<String> results) {
        // Extraer solo el numero de la respuesta
        String[] lines = result.split("\n");
        for (String line : lines) {
            if (line.contains("Numero de veces encontrada: ")) {
                String count = line.replace("Numero de veces encontrada: ", "").trim();
                // Buscar el indice de la tarea en los resultados
                for (int i = 0; i < results.size(); i++) {
                    if (results.get(i).equals("")){
                        results.set(i, count);
                        break;
                    }
                }
                break;
            }
        }
        System.out.println("\tEl servidor " + workerAddress + " completo la tarea " + task);
    }
    
    private void assignNextTask(int serverIndex, CompletableFuture<String>[] serverFutures, 
                               ConcurrentLinkedQueue<Integer> pendingTasks, List<String> tasks, 
                               List<String> workersAddresses, List<String> results) {
        Integer nextTaskIndex = pendingTasks.poll();
        if (nextTaskIndex != null) {
            final int taskIndex = nextTaskIndex;
            System.out.println("Al servidor " + workersAddresses.get(serverIndex) + " se le asigna la tarea: " + tasks.get(taskIndex));
            serverFutures[serverIndex] = webClient.sendTask(workersAddresses.get(serverIndex), tasks.get(taskIndex).getBytes())
                    .thenApply(result -> {
                        processResult(serverIndex, result, tasks.get(taskIndex), workersAddresses.get(serverIndex), results);
                        assignNextTask(serverIndex, serverFutures, pendingTasks, tasks, workersAddresses, results);
                        return result;
                    });
        }
    }
}