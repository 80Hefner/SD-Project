package Servidor;

import Classes.Localizacao;
import Classes.MapasAplicacao;
import Classes.Utilizador;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

                    case "atualizarLocalizacao":
                        atualizaLocalizacao();
                        break;

                    case "consultaNumeroPessoasLocalizacao":
                        consultaNumeroPessoasLocalizacao();
                        break;

                    case "consultarMapaLocalizacoes":
                        consultarMapaLocalizacoes();
                        break;

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

        boolean validaLogin = mapasAplicacao.getMapaUtilizadores().containsKey(username) && mapasAplicacao.getMapaUtilizadores().get(username).getPassword().equals(password);

        if (validaLogin) {

            userAtual = mapasAplicacao.getMapaUtilizadores().get(username);
            userAtual.login();

            if (userAtual.isAdmin()) dos.writeInt(1);
            else dos.writeInt(0);

        } else {
            dos.writeInt(-1);
        }

        dos.flush();
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
        int locX = dis.readInt();
        int locY = dis.readInt();

        boolean validaEspaco = ( locX < Servidor.dimensao && locX >= 0 && locY < Servidor.dimensao && locY >= 0 );

        //todo meter locks necessários
        if (mapasAplicacao.getMapaUtilizadores().containsKey(username) || !validaEspaco) {
            dos.writeBoolean(false);
            dos.flush();
        }
        else {
            mapasAplicacao.getMapaUtilizadores().put(username, new Utilizador(username, password, false, locX, locY));
            userAtual = mapasAplicacao.getMapaUtilizadores().get(username);
            userAtual.login();

            Localizacao localizacao = mapasAplicacao.getLocalizacao(locX, locY, Servidor.dimensao);
            localizacao.adicionaUtilizadorLocalizacao(userAtual);

            dos.writeBoolean(true);
            dos.flush();
        }
    }

    private void atualizaLocalizacao() throws IOException {
        Integer antigoX = userAtual.getLocalizacaoX();
        Integer antigoY = userAtual.getLocalizacaoY();

        Integer novoX = dis.readInt();
        Integer novoY = dis.readInt();

        boolean novaPosicaoValidada = validaNovaLocalizacao (novoX, novoY, userAtual);

        if (novaPosicaoValidada) {
            userAtual.atualizaLocalizacao(novoX, novoY);
            this.mapasAplicacao.getLocalizacao(antigoX, antigoY, Servidor.dimensao).removeUtilizadorAtual(userAtual);
            this.mapasAplicacao.getLocalizacao(novoX, novoY, Servidor.dimensao).adicionaUtilizadorLocalizacao(userAtual);
        }

        dos.writeBoolean( novaPosicaoValidada );
        dos.flush();
    }


    private boolean validaNovaLocalizacao (int locX, int locY, Utilizador user) {

        boolean validaEspaco = ( locX < Servidor.dimensao && locX >= 0 && locY < Servidor.dimensao && locY >= 0 );
        boolean validaNovaLocalizacao = ( userAtual.getLocalizacaoX() != locX || userAtual.getLocalizacaoY() != locY );

        return (validaNovaLocalizacao && validaEspaco);
    }


    private void consultaNumeroPessoasLocalizacao () throws IOException {
        Integer locX = dis.readInt();
        Integer locY = dis.readInt();

        int numeroPessoas;

        boolean validaEspaco = ( locX < Servidor.dimensao && locX >= 0 && locY < Servidor.dimensao && locY >= 0 );

        if (!validaEspaco) {
            numeroPessoas = -1;
        }
        else {
            numeroPessoas = this.mapasAplicacao.getLocalizacao(locX, locY, Servidor.dimensao).consultaNumeroAtualUtilizadores();
        }

        dos.writeInt(numeroPessoas);
        dos.flush();
    }

    private void consultarMapaLocalizacoes() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(Servidor.dimensao);

        //todo meter locks necessários
        for (int linha = 0; linha<Servidor.dimensao; linha++) {
            for (int coluna = 0; coluna<Servidor.dimensao; coluna++) {

                sb.append(":");

                Set<Utilizador> setUtilizadores= this.mapasAplicacao.getLocalizacao(linha, coluna, Servidor.dimensao).getUtilizadoresPassados();
                int nrUtilizadores = setUtilizadores.size();
                int nrInfetados = (int) setUtilizadores.stream().filter(Utilizador::isInfetado).count();

                sb.append(nrUtilizadores).append("-").append(nrInfetados);
            }
        }

        dos.writeUTF(sb.toString());
        dos.flush();
    }
}
