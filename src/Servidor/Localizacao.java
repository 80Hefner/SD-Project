package Servidor;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Localizacao {

    private int localizacaoX;
    private int localizacaoY;
    private Set<String> utilizadoresAtuais;
    private Set<String> utilizadoresPassados;
    private Set<String> utilizadoresInfetados;

    private ReentrantLock lockLocalizacao = new ReentrantLock();
    private Condition condLocalizacao = lockLocalizacao.newCondition();

    public Localizacao(int localizacaoX, int localizacaoY) {
        this.localizacaoX = localizacaoX;
        this.localizacaoY = localizacaoY;
        this.utilizadoresAtuais = new TreeSet<String>();
        this.utilizadoresPassados = new TreeSet<String>();
        this.utilizadoresInfetados = new TreeSet<String>();
    }

    public void adicionaUtilizador (String utilizador) {
        this.utilizadoresAtuais.add(utilizador);
        this.utilizadoresPassados.add(utilizador);
    }

}

