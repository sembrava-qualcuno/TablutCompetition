package com.github.sembravaqualcuno.client;

import com.github.sembravaqualcuno.domain.Action;
import com.github.sembravaqualcuno.domain.State;
import com.github.sembravaqualcuno.domain.StateTablut;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

/**
 * @author A. Piretti, Andrea Galassi
 */
public class TablutHumanClient extends TablutClient {

    public TablutHumanClient(String player) throws UnknownHostException, IOException {
        super(player, "humanInterface");
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        if (args.length == 0) {
            System.out.println("You must specify which player you are (WHITE or BLACK)!");
            System.exit(-1);
        }
        System.out.println("Selected this: " + args[0]);

        TablutClient client = new TablutHumanClient(args[0]);
        client.run();
    }

    @Override
    public void run() {
        System.out.println("You are player " + this.getPlayer() + "!");
        String actionStringFrom = "";
        String actionStringTo = "";
        Action action;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("You are player " + this.getPlayer() + "!");
        while (true) {
            try {
                this.read();

                System.out.println("Current state:");
                System.out.println(this.getCurrentState());

                // I won
                if (this.getPlayer().equals(State.Turn.WHITE) && this.getCurrentState().getTurn().equals(State.Turn.WHITEWIN) ||
                        this.getPlayer().equals(State.Turn.BLACK) && this.getCurrentState().getTurn().equals(State.Turn.BLACKWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                }
                // I lose
                else if (this.getPlayer().equals(State.Turn.WHITE) && this.getCurrentState().getTurn().equals(State.Turn.BLACKWIN) ||
                        this.getPlayer().equals(State.Turn.BLACK) && this.getCurrentState().getTurn().equals(State.Turn.WHITEWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                }
                // Draw
                else if (this.getCurrentState().getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }
                // My turn
                else if (this.getPlayer().equals(this.getCurrentState().getTurn())) {
                    System.out.println("Player " + this.getPlayer() + ", do your move: ");
                    System.out.println("From: ");
                    actionStringFrom = in.readLine();
                    System.out.println("To: ");
                    actionStringTo = in.readLine();
                    action = new Action(actionStringFrom, actionStringTo, this.getPlayer());
                    this.write(action);
                }
                // Adversary turn
                else {
                    System.out.println("Waiting for your opponent move... ");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
