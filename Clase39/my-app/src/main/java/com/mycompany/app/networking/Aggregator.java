package networking;

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
        for (int i = 0; i < tasks.size(); i++) {
            pendingTasks.add(i);
        }
        
        // Asignar tareas iniciales a los servidores disponibles
        for (int i = 0; i < Math.min(workersAddresses.size(), tasks.size()); i++) {
            final int serverIndex = i;
            final int taskIndex = i;
            
            if (!pendingTasks.isEmpty() && pendingTasks.peek() == taskIndex) {
                pendingTasks.poll();
                
                System.out.println("Al servidor " + workersAddresses.get(serverIndex) + 
                                 " se le asigna la tarea: " + tasks.get(taskIndex));
                
                serverFutures[serverIndex] = webClient.sendTask(
                    workersAddresses.get(serverIndex), 
                    tasks.get(taskIndex).getBytes()
                ).thenApply(result -> {
                    processResult(taskIndex, result, tasks.get(taskIndex), 
                                workersAddresses.get(serverIndex), results);
                    assignNextTask(serverIndex, serverFutures, pendingTasks, 
                                 tasks, workersAddresses, results);
                    return result;
                });
            }
        }
        
        // Esperar a que todas las tareas se completen
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(serverFutures);
        allFutures.join();
        
        return results;
    }
    
    private void processResult(int taskIndex, String result, String task, 
                             String workerAddress, List<String> results) {
        // Para factorial, la respuesta es directamente el producto parcial
        results.set(taskIndex, result.trim());
        System.out.println("\tEl servidor " + workerAddress + 
                         " completó la tarea " + task + 
                         " -> Resultado: " + result.trim());
    }
    
    private void assignNextTask(int serverIndex, CompletableFuture<String>[] serverFutures, 
                               ConcurrentLinkedQueue<Integer> pendingTasks, List<String> tasks, 
                               List<String> workersAddresses, List<String> results) {
        Integer nextTaskIndex = pendingTasks.poll();
        if (nextTaskIndex != null) {
            final int taskIndex = nextTaskIndex;
            System.out.println("Al servidor " + workersAddresses.get(serverIndex) + 
                             " se le asigna la tarea: " + tasks.get(taskIndex));
            serverFutures[serverIndex] = webClient.sendTask(
                workersAddresses.get(serverIndex), 
                tasks.get(taskIndex).getBytes()
            ).thenApply(result -> {
                processResult(taskIndex, result, tasks.get(taskIndex), 
                            workersAddresses.get(serverIndex), results);
                assignNextTask(serverIndex, serverFutures, pendingTasks, 
                             tasks, workersAddresses, results);
                return result;
            });
        }
    }
}