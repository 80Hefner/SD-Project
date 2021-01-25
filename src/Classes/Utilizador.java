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
        lockUtilizador.lock();
        try {
            return username;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public String getPassword() {
        lockUtilizador.lock();
        try {
            return password;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public int getLocalizacaoX() {
        lockUtilizador.lock();
        try {
            return localizacaoX;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public int getLocalizacaoY() {
        lockUtilizador.lock();
        try {
            return localizacaoY;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public boolean isAdmin() {
        lockUtilizador.lock();
        try {
            return admin;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public boolean isLogado() {
        lockUtilizador.lock();
        try {
            return logado;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public boolean isInfetado() {
        lockUtilizador.lock();
        try {
            return infetado;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public boolean isAvisaContactoInfetado() {
        lockUtilizador.lock();
        try {
            return avisaContactoInfetado;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public ReentrantLock getLockUtilizador() {
        return lockUtilizador;
    }

    public Condition getCondUtilizador() {
        lockUtilizador.lock();
        try {
            return condUtilizador;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public Map<String, Utilizador> getUtilizadoresComQuemContactou() {
        lockUtilizador.lock();
        try {
            return utilizadoresComQuemContactou;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public void login() {
        lockUtilizador.lock();
        try {
            this.logado = true;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public void logout() {
        lockUtilizador.lock();
        try {
            this.logado = false;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public void setInfetado() {
        lockUtilizador.lock();
        try {
            this.infetado = false;
        } finally {
            lockUtilizador.unlock();
        }
    }

    public void setAvisaContactoInfetado (boolean avisaContactoInfetado) {
        lockUtilizador.lock();
        try {
            this.avisaContactoInfetado = avisaContactoInfetado;
            if (avisaContactoInfetado)
                this.getCondUtilizador().signalAll();
        } finally {
            lockUtilizador.unlock();
        }
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
        lockUtilizador.lock();
        try {
            return new Utilizador(this);
        } finally {
            lockUtilizador.unlock();
        }
    }
}
