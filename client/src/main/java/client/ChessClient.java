package client;

import responses.LoginResponse;
import responses.RegisterResponse;
import server.ServerFacade;
import ui.PostLoginUI;
import ui.PreLoginUI;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.PreLoginUI.*;
import static ui.PostLoginUI.*;

public class ChessClient {
    private final ServerFacade facade;
    private String authToken;
    private State state = State.LOGGED_OUT;
    private static final Scanner scanner = new Scanner(System.in);

    public enum State {
        LOGGED_IN,
        LOGGED_OUT
    }

    public ChessClient(String serverUrl) {
        facade = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD + "Welcome to the chess program!");
        System.out.print(RESET_TEXT_BOLD_FAINT);
        handlePreLogin("help");
        while (true) {
            try {
                if (state == State.LOGGED_OUT) {
                    System.out.println();
                    System.out.print(SET_TEXT_ITALIC + "[LOGGED OUT] >>> ");
                    System.out.print(RESET_TEXT_ITALIC);
                    String input = scanner.nextLine();
                    handlePreLogin(input);
                } else {
                    System.out.println();
                    System.out.print(SET_TEXT_ITALIC + "[LOGGED IN] >>> ");
                    System.out.print(RESET_TEXT_ITALIC);
                    String input = scanner.nextLine();
                    handlePostLogin(input);
                }
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    private void handlePreLogin(String command) {
        switch(command.toLowerCase()) {
            case "help" -> PreLoginUI.help();
            case "quit" -> quit();
            case "login" -> {
                LoginResponse response = login(facade);
                if (response != null) {
                    authToken = response.authToken();
                    state = State.LOGGED_IN;
                    System.out.print(SET_TEXT_COLOR_GREEN);
                    System.out.println("Logged in successfully");
                    System.out.print(RESET_TEXT_COLOR);
                } else {
                    System.out.print(SET_TEXT_COLOR_RED);
                    System.out.println("Login failed");
                    System.out.print(RESET_TEXT_COLOR);
                }
            }
            case "register" -> {
                RegisterResponse response = register(facade);
                if (response != null) {
                    authToken = response.authToken();
                    state = State.LOGGED_IN;
                    System.out.print(SET_TEXT_COLOR_GREEN);
                    System.out.println("Registered successfully");
                    System.out.print(RESET_TEXT_COLOR);
                } else {
                    System.out.print(SET_TEXT_COLOR_RED);
                    System.out.println("Registration failed");
                    System.out.print(RESET_TEXT_COLOR);
                }
            }
            default -> System.out.println("Unknown command. Type 'help' to see a list of available commands.");
        }
    }

    private void handlePostLogin(String command) throws Exception {
        switch(command.toLowerCase()) {
            case "list" -> listGames(facade, authToken);
            case "create" -> createGame(facade, authToken);
            case "play" -> playGame(facade, authToken);
            case "observe" -> observeGame(facade, authToken);
            case "logout" -> {
                logout(facade, authToken);
                authToken = null;
                state = State.LOGGED_OUT;
                System.out.print(SET_TEXT_COLOR_GREEN);
                System.out.println("Logged out successfully");
                System.out.print(RESET_TEXT_COLOR);
            }
            case "help" -> PostLoginUI.help();
            default -> System.out.println("Unknown command. Type 'help' to see a list of available commands.");
        }
    }
}
