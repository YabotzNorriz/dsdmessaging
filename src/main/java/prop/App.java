/** Descrição do programma
 * 1. ARQUITETURA E FLUXO:
 * - A aplicação baseia-se no padrão JMS (Java Message Service) utilizando o ActiveMQ como Broker.
 * - O sistema é centralizado na classe 'MenuPrincipalVisual', que atua como um hub, permitindo
 * iniciar tanto o Servidor (Broker) quanto múltiplas instâncias do Cliente (Chat).
 * - O uso de Threads separadas para o servidor e para cada cliente garante que a interface gráfica
 * (Swing) permaneça responsiva, evitando congelamentos durante operações de rede.
 * 2. SERVIDOR (BROKER EMBUTIDO):
 * - O Broker ActiveMQ é instanciado via código (Embedded Broker) na classe 'ServerApp', eliminando
 * a necessidade de instalação externa.
 * - O servidor roda na porta padrão 61616 (TCP) e é configurado como não persistente (apenas RAM)
 * para maior agilidade.
 * - Existe uma verificação de segurança via Socket para checar se a porta já está em uso, impedindo
 * conflitos caso o usuário tente iniciar o servidor duas vezes.
 * 3. MENSAGERIA E PRIVACIDADE (O DIFERENCIAL):
 * - Modelo Pub/Sub: Utilizamos Tópicos ('chat.publico') em vez de Filas, permitindo que todos
 * os clientes recebam mensagens simultaneamente.
 * - Filtragem Inteligente (JMS Selectors): A privacidade não é tratada apenas visualmente, mas
 * no nível do protocolo.
 * > Mensagens Públicas: Enviadas sem propriedade 'recipient'.
 * > Mensagens Privadas: Enviadas com a propriedade 'recipient' definida.
 * - O consumidor de cada cliente utiliza um Seletor SQL ("recipient IS NULL OR recipient = 'meuID'")
 * para garantir que o Broker só entregue mensagens que são públicas ou destinadas especificamente
 * a ele.
 */
package prop;

import prop.visual.MenuPrincipalVisual;

/**
 * Apenas invoca a classe MenuPricipalVisual
 */
public class App {
    public static void main(String[] args) {
        System.out.println(
                "Chat iniciado\n Trabalho feito por João Pedro Andrade Paes Pimentel Barbosa e Samuel Lucas Passos Santos");
        MenuPrincipalVisual.main(args);
    }
}