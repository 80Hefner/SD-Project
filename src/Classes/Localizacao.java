package Classes;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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

    public void adicionaUtilizadorLocalizacao (Utilizador utilizador) {
        String username = utilizador.getUsername();

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
    }



}

