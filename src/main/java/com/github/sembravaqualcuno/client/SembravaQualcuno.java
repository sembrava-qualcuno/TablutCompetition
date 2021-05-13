package com.github.sembravaqualcuno.client;

import com.github.sembravaqualcuno.domain.*;
import com.github.sembravaqualcuno.exceptions.*;
import com.github.sembravaqualcuno.searchstrategy.AlphaBetaStrategy;
import com.github.sembravaqualcuno.searchstrategy.SearchStrategy;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;

public class SembravaQualcuno extends TablutClient {
    public static final String CLIENT_NAME = "sembrava_qualcuno";
    public static final int MAX_DEPTH = 4; //TODO Decidere se renderla dinamica
    public static final long MAX_TIME = 100000000; //TODO Decidere se tenerlo
    public static final int WHITE_PORT = 5800;
    public static final int BLACK_PORT = 5801;

    private SearchStrategy searchStrategy;

    public SembravaQualcuno(String player, String name, int timeout, String ipAddress) throws IOException {
        super(player, name, timeout, ipAddress);

        Game rules = new GameAshtonTablut(this.getPlayer(), 2, 2, "garbage", "fake", "fake");
        searchStrategy = new AlphaBetaStrategy(rules, MAX_DEPTH, MAX_TIME);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: sembrava_qualcuno <role> <timeout> <ipAddress>");
            System.exit(1);
        }

        String role = args[0];
        if (!role.equalsIgnoreCase("white") && !role.equalsIgnoreCase("black")) {
            System.out.println("Player role must be BLACK or WHITE");
            System.exit(1);
        }
        int timeout = 0;
        try {
            timeout = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("The timeout has to be an integer");
            System.exit(1);
        }
        String ipAddress = args[2];

        printGreetings();

        System.out.println("Connecting to the server...");
        SembravaQualcuno client = null;
        try {
            client = new SembravaQualcuno(role, CLIENT_NAME, timeout, ipAddress);
        } catch (InvalidParameterException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + ipAddress);
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Connected");
        client.run();
    }

    @Override
    public void run() {
        try {
            System.out.println("Declaring name...");
            this.declareName();
        } catch (Exception e) {
            System.out.println("Error declaring name: " + e.getMessage());
            System.exit(1);
        }

        State state;

        System.out.println("You are player " + this.getPlayer());
        System.out.println("Start of the match!");

        while (true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                System.out.println("Error reading the state: " + e1.getMessage());
                System.exit(1);
            }

            state = this.getCurrentState();

            // I won
            if (this.getPlayer().equals(State.Turn.WHITE) && state.getTurn().equals(State.Turn.WHITEWIN) ||
                    this.getPlayer().equals(State.Turn.BLACK) && state.getTurn().equals(State.Turn.BLACKWIN)) {
                System.out.println("YOU WIN!");
                System.exit(0);
            }
            // I lose
            else if (this.getPlayer().equals(State.Turn.WHITE) && state.getTurn().equals(State.Turn.BLACKWIN) ||
                    this.getPlayer().equals(State.Turn.BLACK) && state.getTurn().equals(State.Turn.WHITEWIN)) {
                System.out.println("YOU LOSE!");
                System.exit(0);
            }
            // Draw
            else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                System.out.println("DRAW!");
                System.exit(0);
            }
            // My turn
            else if (this.getPlayer().equals(this.getCurrentState().getTurn())) {
                Action chosenMove = null;
                try {
                    System.out.println("I am thinking...");
                    chosenMove = this.searchStrategy.choseMove(state);
                    System.out.println("Chosen move: " + chosenMove);

                } catch (IOException | ActionException e) {
                    System.out.println("Error choosing the move: " + e.getMessage());
                    //TODO Effettuare una mossa a caso?
                }
                try {
                    this.write(chosenMove);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error sending the move: " + e.getMessage());
                    System.exit(1);
                }
            }
            // Adversary turn
            else {
                System.out.println("Waiting for your opponent move...");
            }
        }
    }

    private static void printGreetings() {
        System.out.println("                        __                                                 __                          __\n" +
                "   ________  ____ ___  / /_  _________ __   ______ _    ____ ___  ______ _/ /______  ______  ____     / /\n" +
                "  / ___/ _ \\/ __ `__ \\/ __ \\/ ___/ __ `/ | / / __ `/   / __ `/ / / / __ `/ / ___/ / / / __ \\/ __ \\   / / \n" +
                " (__  )  __/ / / / / / /_/ / /  / /_/ /| |/ / /_/ /   / /_/ / /_/ / /_/ / / /__/ /_/ / / / / /_/ /  /_/  \n" +
                "/____/\\___/_/ /_/ /_/_.___/_/   \\__,_/ |___/\\__,_/____\\__, /\\__,_/\\__,_/_/\\___/\\__,_/_/ /_/\\____/  (_)   \n" +
                "                                                /_____/ /_/                                              \n");
    }
}
