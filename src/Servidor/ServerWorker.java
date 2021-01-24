package Servidor;

import Classes.Localizacao;
import Classes.MapasAplicacao;
import Classes.Utilizador;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

class ServerWorker implements Runnable {

    private final Socket socket;
    private final DataOutputStream dos;
    private final DataInputStream dis;
    private MapasAplicacao mapasAplicacao;
    private Utilizador userAtual = null;

    public ServerWorker (Socket socket, MapasAplicacao mapasAplicacao) throws IOException {
        this.socket = socket;
        this.mapasAplicacao = mapasAplicacao;
        this.dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
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

        dos.writeBoolean(mapasAplicacao.getMapaUtilizadores().containsKey(username) && mapasAplicacao.getMapaUtilizadores().get(username).getPassword().equals(password));
        dos.flush();

        userAtual = mapasAplicacao.getMapaUtilizadores().get(username);
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

        if (mapasAplicacao.getMapaUtilizadores().containsKey(username)) {
            dos.writeBoolean(false);
            dos.flush();
        }
        else {
            mapasAplicacao.getMapaUtilizadores().put(username, new Utilizador(username, password, Servidor.dimensao));
            userAtual = mapasAplicacao.getMapaUtilizadores().get(username);
            userAtual.login();

            Localizacao localizacao = mapasAplicacao.getLocalizacao(userAtual.getLocalizacaoX(), userAtual.getLocalizacaoY(), Servidor.dimensao);
            localizacao.adicionaUtilizadorLocalizacao(userAtual);

            dos.writeBoolean(true);
            dos.flush();
        }
    }
}
