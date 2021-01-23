package Servidor;

import java.io.*;
import java.net.Socket;
import java.util.Map;

class ServerWorker implements Runnable {

    private Socket socket;
    private Map<String, Utilizador> mapaUtilizadores;
    private Map<Integer, Localizacao> mapaLocalizacoes;

    public ServerWorker(Socket socket, Map<String, Utilizador> mapaUtilizadores, Map<Integer, Localizacao> mapaLocalizacoes) {
        this.socket = socket;
        this.mapaUtilizadores = mapaUtilizadores;
        this.mapaLocalizacoes = mapaLocalizacoes;
    }

    @Override
    public void run() {
        try {

            DataInputStream in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));

            boolean logado = false;
            Utilizador userAtual = null;

            String line;
            boolean continua = true;

            while (continua) {
                line = in.readUTF();

                switch (line) {

                    case "END":
                        out.writeUTF("Tome cuidado");
                        out.flush();
                        continua=false;
                        break;

                    case "1":
                        userAtual = efetuaLogin(in, out);
                        if (userAtual!=null)
                            logado = true;

                    default:
                        out.writeUTF("Opção inválida\n\n");
                        out.flush();
                        break;
                }
            }

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private Utilizador efetuaLogin (DataInputStream in, DataOutputStream out) {
        String username = null;
        
        try {

            out.writeUTF("Nome de Utilizador:");
            out.flush();
            username = in.readUTF();

            out.writeUTF("Password do Utilizador:");
            out.flush();
            String password = in.readUTF();

            out.writeBoolean(!mapaUtilizadores.containsKey(username) || !mapaUtilizadores.get(username).getPassword().equals(password));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mapaUtilizadores.get(username);
    }
}
