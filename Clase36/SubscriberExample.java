import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

public class SubscriberExample {

    public static void main(String... args) throws Exception {
        String projectId = "ID de tu proyecto";
        String subscriptionId = "ID de la suscripción";

        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(projectId, subscriptionId);

        // Receptor que procesa cada mensaje entrante
        MessageReceiver receiver = (PubsubMessage message, AckReplyConsumer consumer) -> {
            String contenido = message.getData().toStringUtf8();
            System.out.println("Mensaje recibido: " + contenido);

            // Confirmar recepción para evitar reenvíos
            consumer.ack();
        };

        // Crear el subscriber
        Subscriber subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();

        // Iniciar de forma asíncrona
        subscriber.startAsync().awaitRunning();
        System.out.println("Subscriber activo. Esperando mensajes (Ctrl + C para salir)...");

        // Manejo de apagado limpio al presionar Ctrl + C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n Cerrando subscriber...");
            subscriber.stopAsync();
            System.out.println(" Subscriber detenido correctamente.");
        }));

        // Mantiene el hilo principal vivo mientras se reciben mensajes
        Thread.currentThread().join();
    }
}
