package Cliente;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Client {

    private final BufferedReader systemIn;
    private ClientStub clientStub;
    private ClientListener clientListener;
    private boolean admin;
    private boolean infetado;

    public Client() throws IOException {
        this.systemIn = new BufferedReader(new InputStreamReader(System.in));
        this.clientStub = new ClientStub();
        this.clientListener = new ClientListener();
        this.admin = false;
        this.infetado = false;
    }

    public static void main(String[] args) {
        try {
            new Client().executaMenuLogin();
        }
        catch (Exception e) {
            System.out.println("\nPrograma terminou com erro\n" + e.getMessage());
        }
    }



    // Execução Menus
    private void executaMenuLogin() throws IOException, InterruptedException {
        boolean continua = true;

        while (continua) {
            try {
                showMenuLogin();
                int opcao = Integer.parseInt(systemIn.readLine());

                switch (opcao) {
                    case 0: // Sair
                        continua = false;
                        clientStub.endSession();
                        clientListener.endSession();
                        System.out.println("Programa terminado com sucesso.");
                        break;

                    case 1: // Login
                        int respostaLogin = login();

                        if (respostaLogin != -1) {
                            System.out.println("Login com sucesso.");
                            if (respostaLogin / 10 == 1) admin = true;
                            if (respostaLogin % 2 == 1) infetado = true;

                            clientListener.run();
                            executaMenuPrincipal();
                        }
                        else {
                            System.out.println("Login sem sucesso.");
                        }
                        break;

                    case 2: // Registar
                        boolean respostaRegisto = registar();

                        if (respostaRegisto) {
                            System.out.println("Registo com sucesso.");

                            clientListener.run();
                            executaMenuPrincipal();
                        }
                        else {
                            System.out.println("Registo sem sucesso.");
                        }
                        break;

                    default:
                        System.out.println("Input incorreto.");
                        break;
                }
            }
            catch (NumberFormatException e) {
                System.out.println("Input inválido.");
            }
        }
    }

    private void executaMenuPrincipal() throws IOException {
        boolean continua = true;

        while (continua) {
            try {
                showMenuPrincipal();
                int opcao = Integer.parseInt(systemIn.readLine());
                if (infetado && opcao != 0) opcao = -1;

                switch (opcao) {
                    case -1:
                        System.out.println(
                                "\n╔═════════════════════════════════════════════════════════╗" +
                                "\n║  Não pode comunicar com o servidor pois está infetado!  ║" +
                                "\n╚═════════════════════════════════════════════════════════╝");
                        break;

                    case 0: // Logout
                        boolean respostaLogout = clientStub.logout();

                        if (respostaLogout) {
                            continua = false;
                            System.out.println("Logout efetuado com sucesso.");
                            admin = false;
                            infetado = false;
                        }
                        else {
                            System.out.println("Logout efetuado sem sucesso.");
                        }
                        break;

                    case 1: // Atualizar localização
                        boolean respostaAtualizarLocalizacao = atualizarLocalizacao();

                        if (respostaAtualizarLocalizacao) {
                            System.out.println("Localização atualizada com sucesso.");
                        } else {
                            System.out.println("Localização não atualizada.");
                        }
                        break;

                    case 2: // Consulta Numero de Pessoas numa Localização
                        int numeroPessoasLocalizacao = consultaNumeroPessoasLocalizacao();

                        if (numeroPessoasLocalizacao != -1) {
                            System.out.println("-> Localização pedida possui " + numeroPessoasLocalizacao + " Utilizador(es) no momento!");
                        } else {
                            System.out.println("Localização pedida não pode ser verificada.");
                        }
                        break;

                    case 3: // Consulta quando uma Localização fica livre
                        boolean consultouLivreLocalizacao = consultaLocalizacaoLivre();
                        if (!consultouLivreLocalizacao) {
                            System.out.println("Localização não pode ser verificada");
                        }
                        break;

                    case 4: // Notificar infeção
                        boolean notificouInfecao = notificarInfecao();
                        if (notificouInfecao) {
                            infetado = true; // ═ ║ ╔ ╗ ╚ ╝ ╠ ╣
                            System.out.println(
                                    "\n╔═══════════════════════════════════════════════════════╗" +
                                    "\n║  ESTÁ INFETADO!!! Não poderá usar mais o sistema!!!!  ║" +
                                    "\n╚═══════════════════════════════════════════════════════╝");
                        }
                        else {
                            System.out.println("Infeção não foi notificada corretamente.");
                        }
                        break;

                    case 6: // Consulta Mapa de Localizações
                        if (!admin) {
                            System.out.println("Input incorreto.");
                            break;
                        }
                        String mapaLocalizacoesUtilizadores = consultarMapaLocalizacoes();
                        showMapaLocalizacoes(mapaLocalizacoesUtilizadores);
                        break;

                    default:
                        System.out.println("Input incorreto.");
                        break;
                }
            }
            catch (NumberFormatException e) {
                System.out.println("Input inválido.");
            }
        }
    }

    // Opções Menu Principal
    private boolean atualizarLocalizacao() throws IOException, NumberFormatException {
        System.out.println("Nova Localização:");
        System.out.print("X > ");
        int x = Integer.parseInt(systemIn.readLine());
        System.out.print("Y > ");
        int y = Integer.parseInt(systemIn.readLine());

        return (clientStub.atualizarLocalizacao(x, y));
    }

    private int consultaNumeroPessoasLocalizacao() throws IOException, NumberFormatException {
        System.out.println("Localização a verificar:");
        System.out.print("X > ");
        int x = Integer.parseInt(systemIn.readLine());
        System.out.print("Y > ");
        int y = Integer.parseInt(systemIn.readLine());

        return (clientStub.consultaNumeroPessoasLocalizacao(x, y));
    }

    private String consultarMapaLocalizacoes() throws IOException {
        return (clientStub.consultarMapaLocalizacoes());
    }

    private void showMapaLocalizacoes (String mapa) throws NumberFormatException {

        List<String> informacoesMapa = Arrays.asList(mapa.split(":"));
        int dimensao = Integer.parseInt(informacoesMapa.get(0));

        int largura = dimensao*3 + (dimensao)*2 + 5;

        String menu = "";

        menu += "\n┌"; for (int i = 0; i<largura-1; i++) { menu += "─"; } menu += "┐\n";
        menu += "│ Numero Utilizadores | Numero Infetados"; for (int i = 39; i<largura-1; i++) {  menu += " ";} menu += "│\n" ;
        menu += "├"; for (int i = 0; i<largura-1; i++) { menu += "─"; } menu += "┤\n";
        menu += "│   │"; for (int i = 0; i<dimensao; i++) { menu += "  " + i + "  "; } menu +="│\n";
        menu += "│───┼"; for (int i = 4; i<largura-1; i++) { menu += "─"; } menu += "│\n";

        for (int linha = 0; linha<dimensao; linha++) {

            menu += "│ " + linha + " │";

            for (int coluna = 0; coluna<dimensao; coluna++) {

                List<String> informacoesIndice = Arrays.asList(informacoesMapa.get(linha*dimensao+coluna+1).split("-"));
                int nrUtilizadores = Integer.parseInt(informacoesIndice.get(0));
                int nrInfetados = Integer.parseInt(informacoesIndice.get(1));

                menu += " " + nrUtilizadores + "|" + nrInfetados + " ";

            }
            menu += "│\n";
        }

        menu += "└"; for (int i = 0; i<largura-1; i++) { menu += "─"; } menu += "┘\n";

        System.out.println(menu);
    }

    private boolean consultaLocalizacaoLivre () throws IOException, NumberFormatException {
        System.out.println("Localização a verificar se está livre");
        System.out.print("X > ");
        int x = Integer.parseInt(systemIn.readLine());
        System.out.print("Y > ");
        int y = Integer.parseInt(systemIn.readLine());

        return (clientStub.consultaLocalizacaoLivre(x, y));
    }

    public boolean notificarInfecao() throws IOException {
        return (clientStub.notificarInfecao());
    }



    // Opções Menu Login
    private int login() throws IOException {
        System.out.print("Nome de utilizador: ");
        String user = systemIn.readLine();

        System.out.print("Password: ");
        String password = systemIn.readLine();

        return (clientStub.login(user, password));
    }

    private boolean registar() throws IOException, NumberFormatException {
        System.out.print("Nome de utilizador: ");
        String user = systemIn.readLine();

        System.out.print("Password: ");
        String password = systemIn.readLine();

        System.out.println("Localização");
        System.out.print("X > ");
        int x = Integer.parseInt(systemIn.readLine());
        System.out.print("Y > ");
        int y = Integer.parseInt(systemIn.readLine());

        return (clientStub.registar(user, password, x, y));
    }



    // Show Menus
    private void showMenuLogin() {
        System.out.print(  "\n┌────────────────────────────┐" +
                           "\n│     *** Menu Login ***     │" +
                           "\n├────────────────────────────┤" +
                           "\n│ .1 - Login                 │" +
                           "\n│ .2 - Registar              │" +
                           "\n│ .0 - Sair                  │" +
                           "\n└────────────────────────────┘" +
                           "\nOpção > ");
    }

    private void showMenuPrincipal() {

        String temPermissao = "";
        if (admin)
            temPermissao = "\n│ .6 - Consulta Mapa de Localizações               │";

        System.out.print(
                "\n┌──────────────────────────────────────────────────┐" +
                "\n│              *** Menu Principal ***              │" +
                "\n├──────────────────────────────────────────────────┤" +
                "\n│ .1 - Atualizar Localização                       │" +
                "\n│ .2 - Consulta Número de Pessoas numa Localizacao │" +
                "\n│ .3 - Consulta quando uma Localização fica livre  │" +
                "\n│ .4 - Notificar infeção                           │" +
                temPermissao +
                "\n│ .0 - Logout                                      │" +
                "\n└──────────────────────────────────────────────────┘" +
                "\nOpção > ");
    }

    // ─ │ ┌ ┐ └ ┘ ├ ┤
}
