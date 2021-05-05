package com.github.sembravaqualcuno.client;

import com.github.sembravaqualcuno.client.SembravaQualcuno;
import com.github.sembravaqualcuno.client.TablutClient;
import com.github.sembravaqualcuno.domain.*;
import com.github.sembravaqualcuno.domain.State.Turn;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author A. Piretti, Andrea Galassi
 */
public class TablutRandomClient extends TablutClient {
	public static final String CLIENT_NAME = "random-player";

	public TablutRandomClient(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
    }

    public TablutRandomClient(String player, int timeout, String ipAddress) throws UnknownHostException, IOException {
        this(player, "random", timeout, ipAddress);
    }

    public TablutRandomClient(String player) throws UnknownHostException, IOException {
        this(player, "random", 60, "localhost");
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
  		if (args.length != 3) {
			System.out.println("Usage: randomClient <role> <timeout> <ipAddress>");
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

		TablutRandomClient client = null;
		try {
			client = new TablutRandomClient(role, CLIENT_NAME, timeout, ipAddress);
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

		State state = new StateTablut();
		state.setTurn(State.Turn.WHITE);
		Game rules = new GameAshtonTablut(this.getPlayer(), 99, 0, "garbage", "fake", "fake");

		System.out.println("Ashton Tablut game");

        List<int[]> pawns = new ArrayList<>();
        List<int[]> empty = new ArrayList<>();

        System.out.println("You are player " + this.getPlayer() + "!");

        while (true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                e1.printStackTrace();
                System.exit(1);
            }

			state = this.getCurrentState();

            System.out.println("Current state:");
            System.out.println(state);

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
				if(this.getPlayer().equals(Turn.WHITE)) {
					int[] buf;
					for (int i = 0; i < state.getBoard().length; i++) {
						for (int j = 0; j < state.getBoard().length; j++) {
							if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
									|| state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
								buf = new int[2];
								buf[0] = i;
								buf[1] = j;
								pawns.add(buf);
							} else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
								buf = new int[2];
								buf[0] = i;
								buf[1] = j;
								empty.add(buf);
							}
						}
					}

					int[] selected = null;

					boolean found = false;
					Action a = null;
					try {
						a = new Action("z0", "z0", State.Turn.WHITE);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					while (!found) {
						if (pawns.size() > 1) {
							selected = pawns.get(new Random().nextInt(pawns.size() - 1));
						} else {
							selected = pawns.get(0);
						}

						String from = this.getCurrentState().getBox(selected[0], selected[1]);

						selected = empty.get(new Random().nextInt(empty.size() - 1));
						String to = this.getCurrentState().getBox(selected[0], selected[1]);

						try {
							a = new Action(from, to, State.Turn.WHITE);
							rules.checkMove(state, a);
							found = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					System.out.println("Mossa scelta: " + a.toString());
					try {
						this.write(a);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				else {
					int[] buf;
					for (int i = 0; i < state.getBoard().length; i++) {
						for (int j = 0; j < state.getBoard().length; j++) {
							if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
								buf = new int[2];
								buf[0] = i;
								buf[1] = j;
								pawns.add(buf);
							} else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
								buf = new int[2];
								buf[0] = i;
								buf[1] = j;
								empty.add(buf);
							}
						}
					}

					int[] selected = null;

					boolean found = false;
					Action a = null;
					try {
						a = new Action("z0", "z0", State.Turn.BLACK);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					while (!found) {
						selected = pawns.get(new Random().nextInt(pawns.size() - 1));
						String from = this.getCurrentState().getBox(selected[0], selected[1]);

						selected = empty.get(new Random().nextInt(empty.size() - 1));
						String to = this.getCurrentState().getBox(selected[0], selected[1]);

						try {
							a = new Action(from, to, State.Turn.BLACK);
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						System.out.println("try: " + a);
						try {
							rules.checkMove(state, a);
							found = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					System.out.println("Mossa scelta: " + a.toString());
					try {
						this.write(a);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				pawns.clear();
				empty.clear();
			}
			// Adversary turn
			else {
				System.out.println("Waiting for your opponent move... ");
			}
        }
    }
}
