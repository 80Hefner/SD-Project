package Cliente;

import java.io.*;

public class Client {

    private final BufferedReader systemIn;
    private ClientStub currentUser;

    public Client() throws IOException {
        this.systemIn = new BufferedReader(new InputStreamReader(System.in));
        this.currentUser = null;
    }

    public static void main(String[] args) {
        try {
            new Client().executaMenuLogin();
        }
        catch (Exception e) {
            System.out.println("Programa terminou com erro\n" + e.getMessage());
        }
    }



    // Execução Menus
    private void executaMenuLogin() throws IOException {
        boolean continua = true;

        while (continua) {
            try {
                showMenuLogin();
                int opcao = Integer.parseInt(systemIn.readLine());

                switch (opcao) {
                    case 0: // Sair
                        continua = false;
                        System.out.println("Programa terminado com sucesso.");
                        break;

                    case 1: // Login
                        currentUser = new ClientStub();
                        boolean respostaLogin = login();

                        if (respostaLogin) {
                            System.out.println("Login com sucesso.");
                            executaMenuPrincipal();
                        }
                        else {
                            System.out.println("Login sem sucesso.");
                            currentUser.endSession();
                            currentUser = null;
                        }
                        break;

                    case 2: // Registar
                        currentUser = new ClientStub();
                        boolean respostaRegisto = registar();

                        if (respostaRegisto) {
                            System.out.println("Registo com sucesso.");
                            executaMenuPrincipal();
                        }
                        else {
                            System.out.println("Registo sem sucesso.");
                            currentUser.endSession();
                            currentUser = null;
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
        // todo boolean infetado

        while (continua) {
            try {
                showMenuPrincipal();
                int opcao = Integer.parseInt(systemIn.readLine());

                switch (opcao) {
                    case 0: // Logout
                        boolean respostaLogout = currentUser.logout();

                        if (respostaLogout) {
                            continua = false;
                            System.out.println("Logout efetuado com sucesso.");
                            currentUser.endSession();
                            currentUser = null;
                        }
                        else {
                            System.out.println("Logout efetuado sem sucesso.");
                        }
                        break;

                    case 1: // Atualizar localização
                        boolean respostaAtualizarLocalizacao = atualizarLocalizacao();

                        if (respostaAtualizarLocalizacao) {
                            System.out.println("Localização atualizada com sucesso.");
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

    // Opções Menu Principal
    private boolean atualizarLocalizacao() throws IOException {
        System.out.println("Nova Localização");
        System.out.print("X: ");
        int x = Integer.parseInt(systemIn.readLine());
        System.out.print("Y: ");
        int y = Integer.parseInt(systemIn.readLine());

        return (currentUser.atualizarLocalizacao(x, y));
    }

    // Opções Menu Login
    private boolean login() throws IOException {
        System.out.print("Nome de utilizador: ");
        String user = systemIn.readLine();

        System.out.print("Password: ");
        String password = systemIn.readLine();

        return (currentUser.login(user, password));
    }

    private boolean registar() throws IOException {
        System.out.print("Nome de utilizador: ");
        String user = systemIn.readLine();

        System.out.print("Password: ");
        String password = systemIn.readLine();

        return (currentUser.registar(user, password));
    }



    // Show Menus
    private void showMenuLogin() {
        System.out.println("\n *** Menu Login*** ");
        System.out.println("1 - Login");
        System.out.println("2 - Registar");
        System.out.println("0 - Sair");
        System.out.print("Opção: ");
    }

    private void showMenuPrincipal() {
        System.out.println("\n *** Menu Principal*** ");
        System.out.println("1 - Atualizar Localização");
        System.out.println("0 - Logout");
        System.out.print("Opção: ");
    }

}
