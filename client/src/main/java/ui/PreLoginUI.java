package ui;

import requests.LoginRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.RegisterResponse;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginUI {
    private static final Scanner scanner = new Scanner(System.in);

    public static void help() {
        System.out.println();
        System.out.print(SET_TEXT_COLOR_BLUE + "REGISTER:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" create an account");
        System.out.print(SET_TEXT_COLOR_BLUE + "LOGIN:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" play chess");
        System.out.print(SET_TEXT_COLOR_BLUE + "HELP:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" see a list of possible actions");
        System.out.print(SET_TEXT_COLOR_BLUE + "QUIT:");
        System.out.print(RESET_TEXT_COLOR);
        System.out.println(" exit the program");
    }

    public static void quit() {
        System.out.println("Goodbye!");
        System.exit(0);
    }

    public static RegisterResponse register(ServerFacade facade) {
        RegisterResponse response = null;
        while (response == null) {
            String username = verifyValidInput("Username");
            String password = verifyValidInput("Password");
            String email = verifyValidInput("Email");
            response = validateRegistration(facade, new RegisterRequest(username, password, email));
        }
        return response;
    }

    public static LoginResponse login(ServerFacade facade) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        try {
            return facade.login(new LoginRequest(username, password));
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            if (e.getMessage().contains("400")) {
                System.out.println("Invalid request. Please try again.");
            }
            if (e.getMessage().contains("401")) {
                System.out.println("Invalid username/password");
            }
            else {
                System.out.println("Unable to login");
            }
            System.out.print(RESET_TEXT_COLOR);
            return null;
        }
    }

    private static String verifyValidInput(String field) {
        while (true) {
            System.out.print("Enter " + field.toLowerCase() + " for registration: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println(field + " cannot be blank. Please provide a valid " + field.toLowerCase() + ".");
                System.out.print(RESET_TEXT_COLOR);
                continue;
            }
            return input;
        }
    }

    private static RegisterResponse validateRegistration(ServerFacade facade, RegisterRequest request) {
        try {
            return facade.register(new RegisterRequest(request.username(), request.password(), request.email()));
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            if (e.getMessage().contains("400")) {
                System.out.println("Invalid request. Please try again.");
            }
            if (e.getMessage().contains("403")) {
                System.out.println("Username already taken. Please try again.");
            }
            else {
                System.out.println("Unable to register");
            }
            System.out.print(RESET_TEXT_COLOR);
            return null;
        }
    }
}
