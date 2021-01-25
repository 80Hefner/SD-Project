package Classes;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe Localização
 */
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


    /**
     * Construtor por cópia da classe Utilizador
     * @param utilizador        Utilizador a ser copiado
     */
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

    /**
     * Construtor da classe Utilizador
     * @param username          Username do utilizador
     * @param password          Password do utilizador
     * @param admin             Boolean que indica se o utilizador é admin do sistema
     * @param localizacaoX      Linha da localização do utilizador no mapa
     * @param localizacaoY      Coluna da localização do utilizador no mapa
     */
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


    /**
     * Método que retorna o username do utilizador
     * @return      Username do utilizador
     */
    public String getUsername() {
        lockUtilizador.lock();
        try {
            return username;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que retorna a password do utilizador
     * @return      Password do utilizador
     */
    public String getPassword() {
        lockUtilizador.lock();
        try {
            return password;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que retorna a linha da localização do utilizador no mapa
     * @return      Linha da localização do utilizador no mapa
     */
    public int getLocalizacaoX() {
        lockUtilizador.lock();
        try {
            return localizacaoX;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que retorna a coluna da localização do utilizador no mapa
     * @return      Coluna da localização do utilizador no mapa
     */
    public int getLocalizacaoY() {
        lockUtilizador.lock();
        try {
            return localizacaoY;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que retorna um boolean que indica se o utilizador é admin do sistema
     * @return      Boolean que indica se o utilizador é admin do sistema
     */
    public boolean isAdmin() {
        lockUtilizador.lock();
        try {
            return admin;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que retorna um boolean que indica se o utilizador está logado no sistema
     * @return      Boolean que indica se o utilizador está logado no sistema
     */
    public boolean isLogado() {
        lockUtilizador.lock();
        try {
            return logado;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que retorna um boolean que indica se o utilizador está infetado
     * @return      Boolean que indica se o utilizador está infetado
     */
    public boolean isInfetado() {
        lockUtilizador.lock();
        try {
            return infetado;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que retorna um boolean que indica se o utilizador precisa de ser avisado de um contacto com alguém infetado
     * @return      Boolean que indica se o utilizador precisa de ser avisado de um contacto com alguém infetado
     */
    public boolean isAvisaContactoInfetado() {
        lockUtilizador.lock();
        try {
            return avisaContactoInfetado;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que retorna o Lock associado ao utilizador
     * @return      Lock associado ao utilizador
     */
    public ReentrantLock getLockUtilizador() {
        return lockUtilizador;
    }

    /**
     * Método que retorna a Condition do Lock associado ao utilizador
     * @return      Condition do Lock associado ao utilizador
     */
    public Condition getCondUtilizador() {
        lockUtilizador.lock();
        try {
            return condUtilizador;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que retorna um Map com os utilizadores com quem o utilizador contactou
     * @return      Map com os utilizadores com quem o utilizador contactou
     */
    public Map<String, Utilizador> getUtilizadoresComQuemContactou() {
        lockUtilizador.lock();
        try {
            return utilizadoresComQuemContactou;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que coloca a variável 'logado' a true
     */
    public void login() {
        lockUtilizador.lock();
        try {
            this.logado = true;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que coloca a variável 'logado' a false
     */
    public void logout() {
        lockUtilizador.lock();
        try {
            this.logado = false;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que coloca a variável 'infetado' a true
     */
    public void setInfetado() {
        lockUtilizador.lock();
        try {
            this.infetado = true;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que coloca a variável 'avisaContactoInfetado' a true
     */
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

    /**
     * Método que adiciona um utilizador aos utilizadores com quem o utilizador contactou
     * @param utilizador        Utilizador a ser adicionado
     */
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

    /**
     * Método que atualiza a localização do utilizador
     * @param novoX         Linha da nova localização do utilizador no mapa
     * @param novoY         Coluna da nova localização do utilizador no mapa
     */
    public void atualizaLocalizacao(Integer novoX, Integer novoY) {
        lockUtilizador.lock();
        try {
            this.localizacaoX = novoX;
            this.localizacaoY = novoY;
        } finally {
            lockUtilizador.unlock();
        }
    }

    /**
     * Método que avisa os utilizadores com quem o utilizador contactou que este se encontra infetado
     */
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

    /**
     * Método que retorna uma cópia do utilizador
     * @return      Cópia do utilizador
     */
    public Utilizador clone () {
        lockUtilizador.lock();
        try {
            return new Utilizador(this);
        } finally {
            lockUtilizador.unlock();
        }
    }
}
