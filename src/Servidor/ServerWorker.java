package Servidor;

import Classes.Localizacao;
import Classes.Utilizador;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

class ServerWorker implements Runnable {

    private final Socket socket;
    private final DataOutputStream dos;
    private final DataInputStream dis;
    private Map<String, Utilizador> mapaUtilizadores;
    private Map<Integer, Localizacao> mapaLocalizacoes;
    private final ReentrantLock lockMapaUtilizadores;
    private Utilizador userAtual = null;

    public ServerWorker(Socket socket, Map<String, Utilizador> mapaUtilizadores, Map<Integer, Localizacao> mapaLocalizacoes, ReentrantLock lockMapaUtilizadores) throws IOException {
        this.socket = socket;
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.mapaUtilizadores = mapaUtilizadores;
        this.mapaLocalizacoes = mapaLocalizacoes;
        this.lockMapaUtilizadores = lockMapaUtilizadores;
    }

    @Override
    public void run() {
        try {
            String line;
            boolean continua = true;

            while (continua) {
                line = dis.readUTF();

                switch (line) {
                    case "exit":
                        continua=false;
                        break;

                    case "login":
                        efetuaLogin();
                        break;

                    case "logout":
                        efetuaLogout();
                        break;

                    case "registar":
                        efetuaRegisto();
                        break;
//todo atualizar localizacao
                    default:
                        dos.writeUTF("Opção inválida\n\n");
                        dos.flush();
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



    private void efetuaLogin() throws IOException {
        String username = dis.readUTF();
        String password = dis.readUTF();

        dos.writeBoolean(mapaUtilizadores.containsKey(username) && mapaUtilizadores.get(username).getPassword().equals(password));
        dos.flush();

        userAtual = mapaUtilizadores.get(username);
        userAtual.login();
    }

    private void efetuaLogout() throws IOException {
        userAtual.logout();
        userAtual = null;

        dos.writeBoolean(true);
        dos.flush();
    }

    private void efetuaRegisto() throws IOException {
        String username = dis.readUTF();
        String password = dis.readUTF();

        if (mapaUtilizadores.containsKey(username)) {
            dos.writeBoolean(false);
            dos.flush();
        }
        else {
            mapaUtilizadores.put(username, new Utilizador(username, password, Servidor.dimensao)); //todo inserir no mapa localizaçoes
            userAtual = mapaUtilizadores.get(username);
            userAtual.login();

            dos.writeBoolean(true);
            dos.flush();
        }
    }
}
