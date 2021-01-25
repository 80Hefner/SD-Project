package Servidor;

import Classes.Localizacao;
import Classes.MapasAplicacao;
import Classes.Utilizador;
import Servidor.Threads.ThreadAvisaContacto;
import Servidor.Threads.ThreadEspacoVazio;

import java.io.*;
import java.net.Socket;
import java.util.Set;

class ServerWorker implements Runnable {

    private final Socket socket;
    private final Socket socket2;
    private final DataOutputStream dos;
    private final DataInputStream dis;
    private final DataOutputStream dos2;
    private MapasAplicacao mapasAplicacao;
    private Utilizador userAtual = null;

    public ServerWorker (Socket socket, Socket socket2, MapasAplicacao mapasAplicacao) throws IOException {
        this.socket = socket;
        this.socket2 = socket2;
        this.mapasAplicacao = mapasAplicacao;
        this.dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.socket2.shutdownInput();
        this.dos2 = new DataOutputStream((new BufferedOutputStream(socket2.getOutputStream())));
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
                        dos2.writeUTF("exit");
                        dos2.flush();
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

                    case "consultaLocalizacaoLivre":
                        consultaLocalizacaoLivre();
                        break;

                    case "notificarInfecao":
                        notificarInfecao();
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
            socket2.shutdownOutput();
            socket2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void efetuaLogin() throws IOException {
        String username = dis.readUTF();
        String password = dis.readUTF();
        int res = 0;

        boolean validaLogin = mapasAplicacao.getMapaUtilizadores().containsKey(username) &&
                mapasAplicacao.getMapaUtilizadores().get(username).getPassword().equals(password) &&
                !mapasAplicacao.getMapaUtilizadores().get(username).isLogado();

        if (validaLogin) {

            userAtual = mapasAplicacao.getMapaUtilizadores().get(username);
            userAtual.login();

            Thread threadAvisaContacto = new Thread(new ThreadAvisaContacto(socket2, userAtual));
            threadAvisaContacto.start();

            if (userAtual.isAdmin()) res += 10;
            if (userAtual.isInfetado()) res += 1;

        } else {
            res = -1;
        }

        dos.writeInt(res);
        dos.flush();
    }

    private void efetuaLogout() throws IOException {
        userAtual.logout();
        userAtual = null;

        mapasAplicacao.pingTodasLocalizacoes();

        dos2.writeUTF("exit");
        dos2.flush();
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
        int antigoX = userAtual.getLocalizacaoX();
        int antigoY = userAtual.getLocalizacaoY();

        int novoX = dis.readInt();
        int novoY = dis.readInt();

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
        int locX = dis.readInt();
        int locY = dis.readInt();

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

    private void consultaLocalizacaoLivre() throws IOException {
        int locX = dis.readInt();
        int locY = dis.readInt();

        boolean validaEspaco = ( locX < Servidor.dimensao && locX >= 0 && locY < Servidor.dimensao && locY >= 0 );

        if (validaEspaco) {
            Localizacao loc = this.mapasAplicacao.getLocalizacao(locX, locY, Servidor.dimensao);
            Thread verificaEspacoVazio = new Thread(new ThreadEspacoVazio(socket2, loc, userAtual));
            verificaEspacoVazio.start();
        }

        dos.writeBoolean(validaEspaco);
        dos.flush();
    }

    private void notificarInfecao() throws IOException {

        if (userAtual.isInfetado()) {
            dos.writeBoolean(false);
            dos.flush();
        }
        else {
            userAtual.setInfetado();
            userAtual.avisaContactos();
            dos.writeBoolean(true);
            dos.flush();
        }
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
