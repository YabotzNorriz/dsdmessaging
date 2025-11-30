package prop;

import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import prop.visual.ChatClientVisual;

public class App {
    public static void main(String[] args) {

        String inputID = JOptionPane.showInputDialog("Digite seu ID/Nome de Cliente:");
        String clienteIDFinal;

        if (inputID == null || inputID.trim().isEmpty()) {
            int numeroAleatorio = new Random().nextInt(90000) + 1000;
            clienteIDFinal = "Usuario-" + numeroAleatorio;
            System.out.println("ID não fornecido. Gerado ID automático: " + clienteIDFinal);
        } else {
            clienteIDFinal = inputID.trim();
        }

        final String idParaInterface = clienteIDFinal;

        SwingUtilities.invokeLater(() -> new ChatClientVisual(idParaInterface));
    }
}
