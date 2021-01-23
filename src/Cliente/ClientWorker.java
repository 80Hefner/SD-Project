package Cliente;

import java.io.*;
import java.net.Socket;

public class ClientWorker implements Runnable {

    private Socket socket;

    public ClientWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            printOpcoes();
            String opcao;
            String resposta;
            boolean continua = true;

            while (continua) {

                opcao = systemIn.readLine();

                switch (opcao) {
                    case "END":
                        out.writeUTF(opcao);
                        out.flush();
                        System.out.println("É o fim para mim");
                        continua = false;
                        resposta = in.readUTF();
                        System.out.println("SERVIDOR: " + resposta);
                        break;

                    default:
                        System.out.println("Opção inválida\n");
                        printOpcoes();
                        break;
                }

            }

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void printOpcoes () {
        System.out.println("END - SAIR");
        System.out.println("0 - Logar");
        System.out.println("1 - Registar");
        System.out.printf("OPCAO: ");
    }
}
