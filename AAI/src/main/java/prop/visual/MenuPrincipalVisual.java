package prop.visual;

// Importações necessárias para os componentes gráficos (Swing) e gerenciamento de layout/eventos (AWT)
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import prop.server.ServerApp;

/**
 * Classe responsável pela interface gráfica do Menu Principal.
 * Estende JFrame para criar uma janela de aplicativo.
 */
public class MenuPrincipalVisual extends JFrame {

    /**
     * Construtor: Configura a janela e inicializa os componentes.
     */
    public MenuPrincipalVisual() {
        // --- Configurações Básicas da Janela ---
        setTitle("Messaging - Menu Principal");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Painel Principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1, 10, 20));

        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Criação dos Componentes
        JLabel titleLabel = new JLabel("Escolha uma opção", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Define fonte Arial, Negrito, tamanho 18

        // Botões para as ações
        JButton btnServer = new JButton("Abrir Server");
        JButton btnChat = new JButton("Entrar no Chat");
        JButton btnSair = new JButton("Sair");

        // Eventos
        // Ação para o botão "Abrir Server"
        btnServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // O servidor é iniciado em uma nova Thread.
                new Thread(() -> {
                    try {
                        // Exibe feedback visual para o usuário
                        JOptionPane.showMessageDialog(MenuPrincipalVisual.this, "Servidor Iniciado!");

                        // Chama o método main da classe do Servidor
                        ServerApp.main(new String[] {});

                    } catch (Exception ex) {
                        // Tratamento de erro caso o servidor falhe ao iniciar
                        JOptionPane.showMessageDialog(MenuPrincipalVisual.this,
                                "Erro ao iniciar Server: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }).start(); // Inicia a thread
            }
        });

        // Ação para o botão "Entrar no Chat"
        btnChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Assim como o servidor, o cliente (Chat) é iniciado em uma thread separada
                // para manter o menu responsivo ou permitir fechar o menu sem matar o chat.
                new Thread(() -> {
                    try {
                        // Chama o método initChat() da classe do Cliente (ChatClientVisual)
                        ChatClientVisual.initChat();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MenuPrincipalVisual.this,
                                "Erro ao iniciar Chat: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }).start();
            }
        });

        // Ação para o botão "Sair"
        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Adicionando Componentes ao Painel
        mainPanel.add(titleLabel);
        mainPanel.add(btnServer);
        mainPanel.add(btnChat);
        mainPanel.add(btnSair);

        // Adiciona o painel principal ao centro da janela
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Método main para testar a visualização do Menu isoladamente.
     */
    public static void main(String[] args) {
        // SwingUtilities.invokeLater garante que a GUI seja criada na Thread de Eventos
        // do Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipalVisual().setVisible(true);
        });
    }
}