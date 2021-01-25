package Classes;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Utilizador {

    private String username;
    private String password;
    private boolean admin;
    private int localizacaoX;
    private int localizacaoY;
    private boolean logado;
    private boolean infetado;
    private boolean avisaContactoInfetado;
    private Map<String,Utilizador> utilizadoresComQuemContactou;

    private ReentrantLock lockUtilizador;
    private Condition condUtilizador;


    public Utilizador (Utilizador utilizador) {
        this.admin = utilizador.isAdmin();
        this.username = utilizador.getUsername();
        this.password = utilizador.getPassword();
        this.localizacaoX = utilizador.getLocalizacaoX();
        this.localizacaoY = utilizador.getLocalizacaoY();
        this.utilizadoresComQuemContactou = utilizador.getUtilizadoresComQuemContactou();
        this.logado = utilizador.isLogado();
        this.infetado = utilizador.isInfetado();
        this.avisaContactoInfetado = utilizador.isAvisaContactoInfetado();
        this.lockUtilizador = new ReentrantLock();
        this.condUtilizador = lockUtilizador.newCondition();
    }

    public Utilizador(String username, String password, boolean admin, int localizacaoX, int localizacaoY) {
        this.admin = admin;
        this.username = username;
        this.password = password;
        this.localizacaoX = localizacaoX;
        this.localizacaoY = localizacaoY;
        this.utilizadoresComQuemContactou = new TreeMap<String,Utilizador>();
        this.logado = false;
        this.infetado = false;
        this.avisaContactoInfetado = false;
        this.lockUtilizador = new ReentrantLock();
        this.condUtilizador = lockUtilizador.newCondition();
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getLocalizacaoX() {
        return localizacaoX;
    }

    public int getLocalizacaoY() {
        return localizacaoY;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isLogado() {
        return logado;
    }

    public boolean isInfetado() {
        return infetado;
    }

    public boolean isAvisaContactoInfetado() {
        return avisaContactoInfetado;
    }

    public ReentrantLock getLockUtilizador() {
        return lockUtilizador;
    }

    public Condition getCondUtilizador() {
        return condUtilizador;
    }

    public Map<String, Utilizador> getUtilizadoresComQuemContactou() {
        return utilizadoresComQuemContactou;
    }

    public void login() {this.logado = true;}

    public void logout() {this.logado = false;}

    public void setInfetado() {
        this.infetado = true;
    }

    public void setAvisaContactoInfetado (boolean avisaContactoInfetado) {
        this.getLockUtilizador().lock();
        this.avisaContactoInfetado = avisaContactoInfetado;
        if (avisaContactoInfetado)
            this.getCondUtilizador().signalAll();
        this.getLockUtilizador().unlock();
    }

    public void adicionaUtilizador (Utilizador utilizador) {
        lockUtilizador.lock();
        try {
            if ( !this.utilizadoresComQuemContactou.containsKey( utilizador.getUsername()) ) {
                this.utilizadoresComQuemContactou.put( utilizador.getUsername(), utilizador );
            }
        } finally {
            lockUtilizador.unlock();
        }
    }

    public void atualizaLocalizacao(Integer novoX, Integer novoY) {
        lockUtilizador.lock();
        try {
            this.localizacaoX = novoX;
            this.localizacaoY = novoY;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public void avisaContactos() {
        lockUtilizador.lock();
        try {
            for (Utilizador u : utilizadoresComQuemContactou.values()) {
                u.setAvisaContactoInfetado(true);
            }
        } finally {
            lockUtilizador.unlock();
        }
    }

    public Utilizador clone () {
        return new Utilizador(this);
    }
}
