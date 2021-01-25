package Classes;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Classe Localização
 */
public class Localizacao {

    private int localizacaoX;
    private int localizacaoY;
    private Map<String,Utilizador> utilizadoresAtuais;
    private Map<String,Utilizador> utilizadoresPassados;

    private ReentrantLock lockLocalizacao = new ReentrantLock();
    private Condition condLocalizacao = lockLocalizacao.newCondition();

    /**
     * Construtor da classe Localizacao
     * @param localizacaoX      Linha da localização no mapa
     * @param localizacaoY      Coluna da localização no mapa
     */
    public Localizacao(int localizacaoX, int localizacaoY) {
        this.localizacaoX = localizacaoX;
        this.localizacaoY = localizacaoY;
        this.utilizadoresAtuais = new TreeMap<>();
        this.utilizadoresPassados = new TreeMap<>();
    }

    /**
     * Método que retorna o Lock associado à localização
     * @return      Lock associado à localização
     */
    public ReentrantLock getLockLocalizacao() {
        return lockLocalizacao;
    }

    /**
     * Método que retorna a Condition do Lock associado à localização
     * @return      Condition do Lock associado à localização
     */
    public Condition getCondLocalizacao() {
        lockLocalizacao.lock();
        try {
            return condLocalizacao;
        } finally {
            lockLocalizacao.unlock();
        }
    }

    /**
     * Método que retorna a linha da localização no mapa
     * @return      Linha da localização no mapa
     */
    public int getLocalizacaoX() {
        lockLocalizacao.lock();
        try {
            return localizacaoX;
        } finally {
            lockLocalizacao.unlock();
        }
    }

    /**
     * Método que retorna a coluna da localização no mapa
     * @return      Coluna da localização no mapa
     */
    public int getLocalizacaoY() {
        lockLocalizacao.lock();
        try {
            return localizacaoY;
        } finally {
            lockLocalizacao.unlock();
        }
    }


    /**
     * Método que adiciona um utilizador à localização
     * @param utilizador        Utilizador que entrou na localização
     */
    public void adicionaUtilizadorLocalizacao (Utilizador utilizador) {
        String username = utilizador.getUsername();

        lockLocalizacao.lock();
        try {
            this.utilizadoresAtuais.put(username, utilizador);
            if (!this.utilizadoresPassados.containsKey(username)) {
                this.utilizadoresPassados.put(username, utilizador);
            }

            for (Utilizador u : this.utilizadoresAtuais.values()) {
                if ( !u.getUsername().equals(username) ) {
                    utilizador.adicionaUtilizador(u);
                    u.adicionaUtilizador(utilizador);
                }
            }
        } finally {
            lockLocalizacao.unlock();
        }

    }


    /**
     * Método que remove um utilizador da localização, pois este se moveu para outra localização
     * @param utilizador        Utilizador que saiu da localização
     */
    public void removeUtilizadorAtual (Utilizador utilizador) {
        lockLocalizacao.lock();
        try {
            String username = utilizador.getUsername();
            this.utilizadoresAtuais.remove(username);

            if (this.utilizadoresAtuais.size() == 0)
                condLocalizacao.signalAll();
        } finally {
            lockLocalizacao.unlock();
        }
    }


    /**
     * Método que retorna o número de utilizadores atualmente na localização
     * @return      Número de utilizadores atualmente na localização
     */
    public int consultaNumeroAtualUtilizadores () {
        lockLocalizacao.lock();
        try {
            return this.utilizadoresAtuais.size();
        } finally {
            lockLocalizacao.unlock();
        }
    }


    /**
     * Método que retorna os utilizadores que já passaram na localização
     * @return      Utilizadores que já passaram na localização
     */
    public Set<Utilizador> getUtilizadoresPassados() {
        lockLocalizacao.lock();
        try {
            return utilizadoresPassados.values().stream().map(Utilizador::clone).collect(Collectors.toSet());
        } finally {
            lockLocalizacao.unlock();
        }
    }


    /**
     * Método que envia um ping a todas as threads que deram await() na Condition do Lock associado à localização
     */
    public void enviaPing() {
        lockLocalizacao.lock();
            condLocalizacao.signalAll();
        lockLocalizacao.unlock();
    }
}

