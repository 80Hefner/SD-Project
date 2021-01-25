package Classes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe MapasAplicacao
 */
public class MapasAplicacao {

    private ConcurrentMap<Integer, Localizacao> mapaLocalizacoes;
    private ConcurrentMap<String, Utilizador> mapaUtilizadores;
    private ReentrantLock lockMapasUtilizadores;
    private ReentrantLock lockMapasLocalizacoes;

    /**
     * Construtor da classe MapasAplicacao
     * @param dimensao      Dimensão do mapa a ser criado
     */
    public MapasAplicacao (int dimensao) {

        this.mapaLocalizacoes = new ConcurrentHashMap<>();
        for (int linha = 0; linha<dimensao; linha++) {
            for (int coluna = 0; coluna<dimensao; coluna++) {
                int indiceMapa = linha*dimensao + coluna;
                Localizacao loc = new Localizacao (linha, coluna);
                mapaLocalizacoes.put(indiceMapa, loc);
            }
        }

        this.mapaUtilizadores = new ConcurrentHashMap<>();

        Utilizador admin1 = new Utilizador("admin1","admin1", true, 0, 0);
        mapaUtilizadores.put (admin1.getUsername(), admin1);
        mapaLocalizacoes.get(0).adicionaUtilizadorLocalizacao(admin1);

        Utilizador user1 = new Utilizador("user1","user1", false, 2, 2);
        mapaUtilizadores.put (user1.getUsername(), user1);
        mapaLocalizacoes.get(2*dimensao + 2).adicionaUtilizadorLocalizacao(user1);

        Utilizador user2 = new Utilizador("user2","user2", false, 5, 5);
        mapaUtilizadores.put (user2.getUsername(), user2);
        mapaLocalizacoes.get(5*dimensao + 5).adicionaUtilizadorLocalizacao(user2);

        Utilizador admin2 = new Utilizador("admin2","admin2", true, 9, 9);
        mapaUtilizadores.put (admin2.getUsername(), admin2);
        mapaLocalizacoes.get(5*dimensao + 5).adicionaUtilizadorLocalizacao(admin2);

        this.lockMapasUtilizadores = new ReentrantLock();
        this.lockMapasLocalizacoes = new ReentrantLock();
    }


    /**
     * Método que retorna o Map das localizações
     * @return      Map das localizações
     */
    public Map<Integer, Localizacao> getMapaLocalizacoes() {
        return mapaLocalizacoes;
    }

    /**
     * Método que retorna o Map dos utilizadores
     * @return      Map dos utilizadores
     */
    public Map<String, Utilizador> getMapaUtilizadores() {
        return mapaUtilizadores;
    }

    /**
     * Método que retorna o Lock associado ao Map dos utilizadores
     * @return      Lock associado ao Map dos utilizadores
     */
    public ReentrantLock getLockMapasUtilizadores() {
        return lockMapasUtilizadores;
    }

    /**
     * Método que retorna o Lock associado ao Map das localizações
     * @return      Lock associado ao Map das localizações
     */
    public ReentrantLock getLockMapasLocalizacoes() {
        return lockMapasLocalizacoes;
    }


    /**
     * Método que retorna a instância 'Localizacao' associada a uma dada linha e coluna
     * @param locX          Linha da localização
     * @param locY          Coluna da localização
     * @param dimensao      Dimensão do mapa
     * @return              Instância 'Localizacao' associada à linha 'locX' e à coluna 'locY'
     */
    public Localizacao getLocalizacao (int locX, int locY, int dimensao) {
        lockMapasLocalizacoes.lock();
        try {
            return mapaLocalizacoes.get(locX*dimensao + locY);
        } finally {
            lockMapasLocalizacoes.unlock();
        }
    }

    /**
     * Método que envia um ping o todas as threads que deram await() nas Conditions associadas aos Locks de todas as localizações
     */
    public void pingTodasLocalizacoes() {
        lockMapasLocalizacoes.lock();
        try {
            for (Localizacao loc : mapaLocalizacoes.values())
                loc.enviaPing();
        } finally {
            lockMapasLocalizacoes.unlock();
        }
    }
}
