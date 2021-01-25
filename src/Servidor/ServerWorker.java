package Servidor;

import Classes.Localizacao;
import Classes.MapasAplicacao;
import Classes.Utilizador;
import Servidor.Threads.ThreadAvisaContacto;
import Servidor.Threads.ThreadEspacoVazio;

import java.io.*;
import java.net.Socket;
import java.util.Set;

/**
 * Classe responsável pela Thread que coordena o Servidor
 */
class ServerWorker implements Runnable {

    private final Socket socket;
    private final Socket socket2;
    private final DataOutputStream dos;
    private final DataInputStream dis;
    private final DataOutputStream dos2;
    private MapasAplicacao mapasAplicacao;
    private Utilizador userAtual = null;

    /**
     * Construtor de ServerWorker
     * @param socket            Socket utilizado para comunicação
     * @param socket2           Socket utilizado para enviar avisos para o Client
     * @param mapasAplicacao    Classe onde constam os dados
     * @throws IOException      Exception IO
     */
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
    /**
     * Método run
     */
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
    /**
     * Método que efetua o Login de um Utilizador
     * @throws IOException      Exception IO
     */
    
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

    /**
     * Método que efetua o Logout de um Utilizador
     * @throws IOException      Exception IO
     */
    private void efetuaLogout() throws IOException {
        userAtual.logout();
        userAtual = null;

        mapasAplicacao.pingTodasLocalizacoes();

        dos2.writeUTF("exit");
        dos2.flush();
        dos.writeBoolean(true);
        dos.flush();
    }

    /**
     * Método que efetua o registo de um Utilizador no Sistema
     * @throws IOException      Exception IO
     */
    private void efetuaRegisto() throws IOException {
        String username = dis.readUTF();
        String password = dis.readUTF();
        int locX = dis.readInt();
        int locY = dis.readInt();

        boolean validaEspaco = ( locX < Servidor.dimensao && locX >= 0 && locY < Servidor.dimensao && locY >= 0 );

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

    /**
     * Método que atualiza Localização de um Utilizador
     * @throws IOException      Exception IO
     */
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

    /**
     * Método que valida nova Localização de um Utilizador
     * @param locX      nova Localização X de um Utilizador
     * @param locY      nova Localização Y de um Utilizador
     * @param user      Utilizador em estudo
     * @return      booleano que mostra se posição nova é valida
     */
    private boolean validaNovaLocalizacao (int locX, int locY, Utilizador user) {

        boolean validaEspaco = ( locX < Servidor.dimensao && locX >= 0 && locY < Servidor.dimensao && locY >= 0 );
        boolean validaNovaLocalizacao = ( user.getLocalizacaoX() != locX || user.getLocalizacaoY() != locY );

        return (validaNovaLocalizacao && validaEspaco);
    }

    /**
     * Método que verifica quantos Utilizadores tem numa Localização
     * @throws IOException      Exception IO
     */
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

    /**
     * Método que verifica quando uma Localização fica livre
     * @throws IOException      Exception IO
     */
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

    /**
     * Método utilizado quando um Utilizador notifica que está infetado
     * @throws IOException      Exception IO
     */
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

    /**
     * Método utilizado para consultar um Mapa de Localizações e seus utilizadores e infetados
     * @throws IOException      Exception IO
     */
    private void consultarMapaLocalizacoes() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(Servidor.dimensao);

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
