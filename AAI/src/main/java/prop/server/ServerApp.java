/* * Trabalho feito por:
 * Samuel Lucas Passos Santos - Email: a281012579@fumec.br - RA: 1A280921724
 * João Pedro Andrade Paes Pimentel Barbosa - Email: a280921724@fumec.br - RA: 1A280921724
 */
package prop.server;

import java.net.Socket;
import org.apache.activemq.broker.BrokerService;

/**
 * Classe responsável por iniciar o servidor ActiveMQ embutido (Embedded
 * Broker).
 */
public class ServerApp {
    // Endereço do broker (protocolo TCP, localhost, porta padrão do ActiveMQ 61616)
    private static final String BROKER_URL = "tcp://localhost:61616";

    public ServerApp() {
        System.out.println("Trabalho feito por João Pedro Andrade Paes Pimentel Barbosa e Samuel Lucas Passos Santos");
        System.out.println("Classe --ServerApp-- invocada");
    }

    public static void main(String[] args) {
        tryStartServer();
    }

    /**
     * Tenta iniciar o servidor JMS.
     * Verifica primeiro se a porta já está em uso para evitar conflitos.
     */
    public static void tryStartServer() {
        // Verifica se já existe algo a rodar na porta 61616
        if (isPortBusy(61616)) {
            System.out.println(">>> O Servidor JMS já parece estar a rodar na porta 61616.");
            System.out.println(">>> A iniciar apenas como CLIENTE.");
        } else {
            System.out.println(">>> Porta 61616 livre. A iniciar SERVIDOR JMS embutido");

            // Inicia o Broker numa thread separada para não bloquear a aplicação principal
            new Thread(() -> {
                try {
                    BrokerService broker = new BrokerService();
                    broker.addConnector(BROKER_URL);
                    broker.setPersistent(false); // Não guarda mensagens em disco (apenas memória RAM)
                    broker.setBrokerName("ChatBrokerAuto");
                    broker.start();
                    broker.waitUntilStopped(); // Mantém a thread viva enquanto o broker rodar
                } catch (Exception e) {
                    System.err.println("Erro ao tentar subir o servidor automático: " + e.getMessage());
                }
            }).start();

            // Pequena pausa para garantir que o broker tenha tempo de start antes dos clientes
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println("Não era para isso acontecer... Erro na thread");
            }
        }
    }

    /**
     * Método utilitário para verificar se uma porta TCP está ocupada.
     * Tenta criar um Socket; se conseguir, a porta está ocupada.
     */
    private static boolean isPortBusy(int port) {
        try (Socket s = new Socket("localhost", port)) {
            return true; // Conexão bem-sucedida = porta ocupada
        } catch (Exception e) {
            return false; // Falha na conexão = porta livre (provavelmente)
        }
    }
}