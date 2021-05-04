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
    public static final int MAX_DEPTH = 4;
    public static final long MAX_TIME = 100000000;

    private SearchStrategy searchStrategy;

    public SembravaQualcuno(String player, String name, int timeout, String ipAddress) throws IOException {
        super(player, name, timeout, ipAddress);

        Game rules = new GameAshtonTablut(this.getPlayer(), 99, 0, "garbage", "fake", "fake");
        searchStrategy = new AlphaBetaStrategy(rules, MAX_DEPTH, MAX_TIME);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            //TODO Usage
            System.out.println("Usage");
            System.exit(1);
        }

        String role = args[0];
        int timeout = 0;
        try {
            timeout = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("The timeout has to be an integer");
            System.exit(1);
        }
        String ipAddress = args[2];

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
            e.printStackTrace();
            System.exit(1);
        }
        client.run();
    }

    @Override
    public void run() {
        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        State state;

        System.out.println("Ashton Tablut game");
        System.out.println("You are player " + this.getPlayer() + "!");

        while (true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                e1.printStackTrace();
                System.exit(1);
            }
            System.out.println("Current state:");
            state = this.getCurrentState();
            System.out.println(state);

            //TODO Perché?
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

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

                //TODO Decidere cosa fare in caso di una qualsiasi eccezione
                try {
                    chosenMove = this.searchStrategy.choseMove(this.getCurrentState());
                    System.out.println("Chosen move: " + chosenMove);
                    this.write(chosenMove);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (PawnException e) {
                    e.printStackTrace();
                } catch (DiagonalException e) {
                    e.printStackTrace();
                } catch (ClimbingException e) {
                    e.printStackTrace();
                } catch (ActionException e) {
                    e.printStackTrace();
                } catch (CitadelException e) {
                    e.printStackTrace();
                } catch (StopException e) {
                    e.printStackTrace();
                } catch (OccupiedException e) {
                    e.printStackTrace();
                } catch (BoardException e) {
                    e.printStackTrace();
                } catch (ClimbingCitadelException e) {
                    e.printStackTrace();
                } catch (ThroneException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            // Adversary turn
            else {
                System.out.println("Waiting for your opponent move... ");
            }
        }
    }
}
