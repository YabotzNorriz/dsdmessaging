package prop.visual;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ChatClientVisual extends JFrame implements MessageListener {
    private JTextArea areaMensagensRecebidas;
    private JTextField campoMensagemEnviar;
    private JTextField campoDestinatario;
    private JButton botaoEnviar;
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;
    private String nomeCliente;

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_DESTINATION = "chat.publico";

    public ChatClientVisual(String nomeCliente) {
        this.nomeCliente = nomeCliente;
        setTitle("Chat JMS - " + nomeCliente);

        setupGUI();

        try {
            setupJMS();
        } catch (JMSException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar a JMS.", "Erro de Conexão",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
    }

    private void setupGUI() {
        areaMensagensRecebidas = new JTextArea();
        areaMensagensRecebidas.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaMensagensRecebidas);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelInput = new JPanel(new BorderLayout());
        campoMensagemEnviar = new JTextField(); // Item (a)
        campoDestinatario = new JTextField(10); // Item (b)
        botaoEnviar = new JButton("Enviar"); // Item (d)

        JPanel painelDestinatario = new JPanel(new BorderLayout());
        painelDestinatario.add(new JLabel("Destinatário (código): "), BorderLayout.WEST);
        painelDestinatario.add(campoDestinatario, BorderLayout.CENTER);

        JPanel painelAcoes = new JPanel(new BorderLayout());
        painelAcoes.add(campoMensagemEnviar, BorderLayout.CENTER);
        painelAcoes.add(botaoEnviar, BorderLayout.EAST);
        painelInput.add(painelDestinatario, BorderLayout.NORTH);
        painelInput.add(painelAcoes, BorderLayout.SOUTH);
        add(painelInput, BorderLayout.SOUTH);

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

    private void setupJMS() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        connection = connectionFactory.createConnection();
        connection.setClientID(nomeCliente);
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createTopic(TOPIC_DESTINATION);
        producer = session.createProducer(destination);

        consumer = session.createConsumer(destination);
        consumer.setMessageListener(this);

        areaMensagensRecebidas.append("Conectado ao Broker JMS com ID: " + nomeCliente + "\n");
    }

    private void enviarMensagem() {
        String texto = campoMensagemEnviar.getText().trim();
        String destinatario = campoDestinatario.getText().trim();

        if (texto.isEmpty()) {
            return;
        }

        try {
            TextMessage message = session.createTextMessage(texto);
            message.setStringProperty("sender", nomeCliente);

            if (!destinatario.isEmpty()) {
                message.setStringProperty("recipient", destinatario);
                areaMensagensRecebidas.append("[Mensagem Privada Enviada para " + destinatario + "]\n");
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

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                TextMessage textMessage = (TextMessage) message;
                String remetente = textMessage.getStringProperty("sender");
                String destinatario = textMessage.getStringProperty("recipient");

                if (destinatario == null || destinatario.equalsIgnoreCase(nomeCliente)) {
                    String texto = textMessage.getText();

                    String prefixo = (destinatario != null) ? "[PRIVADO de " + remetente + "]: "
                            : "[" + remetente + "]: ";

                    areaMensagensRecebidas.append(prefixo + texto + "\n");
                    areaMensagensRecebidas.setCaretPosition(areaMensagensRecebidas.getDocument().getLength());
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

}
