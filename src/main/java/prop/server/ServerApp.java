package prop.server;

import java.net.Socket;

import org.apache.activemq.broker.BrokerService;

public class ServerApp {
    private static final String BROKER_URL = "tcp://localhost:61616";

    public ServerApp() {
        System.out.println("Classe --ServerApp-- invocada");
    }

    public static void main(String[] args) {
        tryStartServer();
    }

    public static void tryStartServer() {
        if (isPortBusy(61616)) {
            System.out.println(">>> O Servidor JMS já parece estar rodando na porta 61616.");
            System.out.println(">>> Iniciando apenas como CLIENTE.");
        } else {
            System.out.println(">>> Porta 61616 livre. Iniciando SERVIDOR JMS embutido");
            new Thread(() -> {
                try {
                    BrokerService broker = new BrokerService();
                    broker.addConnector(BROKER_URL);
                    broker.setPersistent(false);
                    broker.setBrokerName("ChatBrokerAuto");
                    broker.start();
                    broker.waitUntilStopped();
                } catch (Exception e) {
                    System.err.println("Erro ao tentar subir o servidor automático: " + e.getMessage());
                }
            }).start();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println("Não era para isso acontecer... Erro na thread");
            }
        }
    }

    private static boolean isPortBusy(int port) {
        try (Socket s = new Socket("localhost", port)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
