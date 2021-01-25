package Classes;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Localizacao {

    private int localizacaoX;
    private int localizacaoY;
    private Map<String,Utilizador> utilizadoresAtuais;
    private Map<String,Utilizador> utilizadoresPassados;

    private ReentrantLock lockLocalizacao = new ReentrantLock();
    private Condition condLocalizacao = lockLocalizacao.newCondition();

    public Localizacao(int localizacaoX, int localizacaoY) {
        this.localizacaoX = localizacaoX;
        this.localizacaoY = localizacaoY;
        this.utilizadoresAtuais = new TreeMap<String,Utilizador>();
        this.utilizadoresPassados = new TreeMap<String,Utilizador>();
    }

    public ReentrantLock getLockLocalizacao() {
        return lockLocalizacao;
    }

    public Condition getCondLocalizacao() {
        return condLocalizacao;
    }

    public int getLocalizacaoX() {
        return localizacaoX;
    }

    public int getLocalizacaoY() {
        return localizacaoY;
    }

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


    public int consultaNumeroAtualUtilizadores () {
        lockLocalizacao.lock();
        try {
            return this.utilizadoresAtuais.size();
        } finally {
            lockLocalizacao.unlock();
        }
    }


    public Set<Utilizador> getUtilizadoresPassados() {
        lockLocalizacao.lock();
        try {
            return utilizadoresPassados.values().stream().map(Utilizador::clone).collect(Collectors.toSet());
        } finally {
            lockLocalizacao.unlock();
        }
    }
}

