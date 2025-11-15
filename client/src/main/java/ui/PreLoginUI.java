package ui;

import requests.LoginRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.RegisterResponse;
import server.ServerFacade;

import java.util.Scanner;

public class PreLoginUI {
    public static final Scanner scanner = new Scanner(System.in);

    public static void help() {
        System.out.println("Register: create an account");
        System.out.println("Login: play chess");
        System.out.println("Quit: exit the program");
        System.out.println("Help: see a list of possible actions");
    }

    public static void quit() {
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
            System.out.println("Invalid username/password");
            return null;
        }
    }

    private static String verifyValidInput(String field) {
        while (true) {
            System.out.print("Enter " + field + " for registration: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.print(field + " cannot be blank. Please provide a valid " + field);
                continue;
            }
            return input;
        }
    }

    private static RegisterResponse validateRegistration(ServerFacade facade, RegisterRequest request) {
        try {
            return facade.register(new RegisterRequest(request.username(), request.password(), request.email()));
        } catch (Exception e) {
            if (e.getMessage().contains("already taken")) {
                System.out.println("Username already taken. Please try again.");
                return null;
            }
            System.out.println("Unable to successfully register: " + e.getMessage());
            return null;
        }
    }
}
