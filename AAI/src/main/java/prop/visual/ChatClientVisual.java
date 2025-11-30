package prop.visual;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Interface Gráfica do Cliente de Chat e lógica JMS.
 * Implementa MessageListener para receber mensagens de forma assíncrona.
 */
public class ChatClientVisual extends JFrame implements MessageListener {
    // Componentes da Interface Gráfica
    private JTextArea areaMensagensRecebidas;
    private JTextField campoMensagemEnviar;
    private JTextField campoDestinatario;
    private JButton botaoEnviar;

    // Componentes JMS (Java Message Service)
    private Connection connection;
    private Session session;
    private MessageProducer producer; // Para enviar mensagens
    private MessageConsumer consumer; // Para receber mensagens
    private String nomeCliente;

    // Configurações do ActiveMQ
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_DESTINATION = "chat.publico";

    // Inicia própriamente o chat (necessita do servidor ligado)
    public static void initChat() {

        // Solicita o ID ao utilizador via caixa de diálogo
        String inputID = JOptionPane.showInputDialog("Digite seu ID/Nome de Cliente:");
        String clienteIDFinal;

        // Validação: Se o utilizador não digitar nada ou cancelar, gera um ID aleatório
        if (inputID == null || inputID.trim().isEmpty()) {
            int numeroAleatorio = new Random().nextInt(90000) + 1000;
            clienteIDFinal = "Usuario-" + numeroAleatorio;
            System.out.println("ID não fornecido. Gerado ID automático: " + clienteIDFinal);
        } else {
            clienteIDFinal = inputID.trim();
        }

        // Variável final necessária para uso dentro da expressão lambda abaixo
        final String idParaInterface = clienteIDFinal;

        // Inicia a Interface Gráfica (GUI) na Thread de Eventos do Swing (EDT)
        SwingUtilities.invokeLater(() -> new ChatClientVisual(idParaInterface));
    }

    public ChatClientVisual(String nomeCliente) {
        boolean isConnected = false;
        this.nomeCliente = nomeCliente;
        setTitle("Chat JMS - " + nomeCliente);

        // 1. Configura a interface visual
        setupGUI();

        // 2. Configura a conexão JMS
        try {
            setupJMS();
            isConnected = true;
        } catch (JMSException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar a JMS.", "Erro de Conexão",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            isConnected = false;
        }

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Listener para fechar a conexão JMS corretamente ao fechar a janela
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                closeConnection();
            }
        });

        setSize(600, 400);
        setVisible(isConnected);
    }

    /**
     * Constrói os painéis, botões e campos de texto da aplicação.
     */
    private void setupGUI() {
        JPanel painelTopo = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        JButton botaoNovaJanela = new JButton("Nova Janela");

        // Permite abrir uma nova janela para utilizar outro usuário na mesma instância
        botaoNovaJanela.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    prop.App.main(new String[] {});
                }).start();
            }
        });

        painelTopo.add(botaoNovaJanela);
        add(painelTopo, BorderLayout.NORTH);

        // Área central onde as mensagens aparecem
        areaMensagensRecebidas = new JTextArea();
        areaMensagensRecebidas.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaMensagensRecebidas);
        add(scrollPane, BorderLayout.CENTER);

        // Painel inferior para envio de mensagens
        JPanel painelInput = new JPanel(new BorderLayout());
        campoMensagemEnviar = new JTextField();
        campoDestinatario = new JTextField(10);
        botaoEnviar = new JButton("Enviar");

        JPanel painelDestinatario = new JPanel(new BorderLayout());
        painelDestinatario.add(new JLabel("Destinatário (código): "), BorderLayout.WEST);
        painelDestinatario.add(campoDestinatario, BorderLayout.CENTER);

        JPanel painelAcoes = new JPanel(new BorderLayout());
        painelAcoes.add(campoMensagemEnviar, BorderLayout.CENTER);
        painelAcoes.add(botaoEnviar, BorderLayout.EAST);
        painelInput.add(painelDestinatario, BorderLayout.NORTH);
        painelInput.add(painelAcoes, BorderLayout.SOUTH);
        add(painelInput, BorderLayout.SOUTH);

        // Ação de envio (ao clicar no botão ou dar Enter nos campos)
        ActionListener enviarListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensagem();
            }
        };

        botaoEnviar.addActionListener(enviarListener);
        campoMensagemEnviar.addActionListener(enviarListener);
        campoDestinatario.addActionListener(enviarListener);
    }

    /**
     * Configura a lógica JMS (Conexão, Sessão, Produtor e Consumidor).
     */
    private void setupJMS() throws JMSException {
        // Cria a fábrica de conexões apontando para o servidor ActiveMQ
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        connection = connectionFactory.createConnection();
        connection.setClientID(nomeCliente); // Identifica este cliente no broker
        connection.start(); // Inicia o recebimento de mensagens

        // Cria sessão sem transações (false) e com confirmação automática
        // (AUTO_ACKNOWLEDGE)
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Define o Tópico (Topic) que será usado (Padrão Pub/Sub)
        Destination destination = session.createTopic(TOPIC_DESTINATION);

        // Cria o produtor para enviar mensagens para este tópico
        producer = session.createProducer(destination);

        // O seletor SQL determina quais mensagens este consumidor vai receber.
        // "recipient IS NULL" -> Mensagens Públicas (sem destinatário específico)
        // "OR recipient = 'meuNome'" -> Mensagens Privadas destinadas a mim
        String selector = "recipient IS NULL OR recipient = '" + nomeCliente + "'";

        consumer = session.createConsumer(destination, selector);
        consumer.setMessageListener(this); // Define esta classe como o ouvinte (callback)

        areaMensagensRecebidas.append("Conectado ao Broker JMS com ID: " + nomeCliente + "\n");
    }

    /**
     * Envia a mensagem para o tópico com propriedades personalizadas.
     */
    private void enviarMensagem() {
        String texto = campoMensagemEnviar.getText().trim();
        String destinatario = campoDestinatario.getText().trim();

        if (texto.isEmpty()) {
            return;
        }

        try {
            TextMessage message = session.createTextMessage(texto);
            // Define propriedades de cabeçalho personalizadas na mensagem
            message.setStringProperty("sender", nomeCliente);

            if (!destinatario.isEmpty()) {
                // Se houver destinatário, define a propriedade 'recipient'.
                // Apenas consumidores com o selector correspondente receberão.
                message.setStringProperty("recipient", destinatario);
                areaMensagensRecebidas
                        .append("[Mensagem Privada Enviada para " + destinatario + "]: " + message.getText() + "\n");
            } else {
                areaMensagensRecebidas.append("[Público Enviado]\n");
            }

            producer.send(message);
            campoMensagemEnviar.setText("");

        } catch (JMSException e) {
            areaMensagensRecebidas.append("Erro ao enviar mensagem: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    /**
     * Método chamado automaticamente (Assincronamente) quando uma mensagem chega.
     */
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                TextMessage textMessage = (TextMessage) message;

                // Lê as propriedades personalizadas para formatar a saída
                String remetente = textMessage.getStringProperty("sender");
                String destinatarioMsg = null;
                if (textMessage.propertyExists("recipient")) {
                    destinatarioMsg = textMessage.getStringProperty("recipient");
                }

                String texto = textMessage.getText();
                String prefixo;

                // Formatação visual diferente para mensagens privadas vs públicas
                if (destinatarioMsg != null) {
                    prefixo = "[PRIVADO de " + remetente + "]: ";
                } else {
                    prefixo = "[" + remetente + "]: ";
                }

                areaMensagensRecebidas.append(prefixo + texto + "\n");
                // Auto-scroll para o final da área de texto
                areaMensagensRecebidas.setCaretPosition(areaMensagensRecebidas.getDocument().getLength());

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fecha os recursos JMS para liberar memória e sockets.
     */
    private void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Cliente " + nomeCliente + " desconectado.");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}