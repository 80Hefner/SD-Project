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
                out.writeUTF(opcao);
                out.flush();

                switch (opcao) {
                    case "END":
                        System.out.println("É o fim para mim");
                        continua = false;
                        resposta = in.readUTF();
                        System.out.println("SERVIDOR: " + resposta);
                        break;

                    case "1":
                        System.out.printf("");
                        resposta = in.readUTF();
                        System.out.print(resposta);
                        opcao = systemIn.readLine();
                        out.writeUTF(opcao);
                        out.flush();

                        resposta = in.readUTF();
                        System.out.print(resposta);
                        opcao = systemIn.readLine();
                        out.writeUTF(opcao);
                        out.flush();

                        resposta = in.readUTF();
                        System.out.print(resposta);
                        break;

                    default:
                        resposta = in.readUTF();
                        System.out.print(resposta);
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
        System.out.println("1 - Logar");
        System.out.println("2 - Registar");
        System.out.printf("OPCAO: ");
    }
}
