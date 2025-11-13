package prop;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import prop.visual.ChatClientVisual;

public class App {
    public static void main(String[] args) {
        String clienteID = JOptionPane.showInputDialog("Digite seu ID/Nome de Cliente:");
        if (clienteID == null || clienteID.trim().isEmpty()) {
            System.out.println("ID de cliente não fornecido. Encerrando.");
            return;
        }
        SwingUtilities.invokeLater(() -> new ChatClientVisual(clienteID.trim()));
    }
}
