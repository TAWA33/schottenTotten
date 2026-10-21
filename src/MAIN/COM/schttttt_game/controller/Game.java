package MAIN.COM.schttttt_game.controller;

import java.util.Scanner;

import MAIN.COM.schttttt_game.ai.Bot;
import MAIN.COM.schttttt_game.model.*;
import MAIN.COM.schttttt_game.view.*;

public class Game {
    private Game_State state;
    private Scanner scanner;

    public Game() {
        state = new Game_State();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome on Schotten Totten !");
        int choice = Menu.showMenu(scanner);

        String p1Name = askPlayerName("Player 1");
        Player p1 = new Player(p1Name, false);

        Player p2;
        if (choice == 2) {
            p2 = new Bot("AI");
        } else {
            String p2Name = askPlayerName("Player 2");
            p2 = new Player(p2Name, false);
        }
        state.addPlayer(p1);
        state.addPlayer(p2);

        // Deck init
        Deck deck = new Deck();
        deck.shuffle();
        state.setDeck(deck);

        // Init of the 9 limits
        for (int i = 0; i < 9; i++) {
            state.addLimit(new Limit(i));
        }

        // Fill the hand of each player
        for (int i = 0; i < 6; i++) {
            p1.addCardToHand(deck.drawCard());
            p2.addCardToHand(deck.drawCard());
        }

        // Game loop
        while (!state.isGameOver()) {
            Player current = state.getCurrentPlayer();
            Grid_Display.clearScreen();

            if (current.isAI()) {
                // We don't display hand of the AI.
                Bot bot = (Bot) current;
                int[] move = bot.makeRandomMove(state);
                if (move[0] == -1) {
                    System.out.println("AI cannot play. Turn passed.");
                } else {
                    playMove(current, move[0], move[1]);
                }
            } else {
                // Human turn
                Grid_Display.display(state, state.getCurrentPlayerIndex());
                Grid_Display.displayHand(current);
                Grid_Display.displayLegend();

                System.out.print("Enter the number of the card (0-5) or 'q' to quit: ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("q")) {
                    System.out.println("Thank you for playing !");
                    return;
                }
                int cardIndex;
                try {
                    cardIndex = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid entry.");
                    continue;
                }
                if (cardIndex < 0 || cardIndex >= current.getHand().getNbCards()) {
                    System.out.println("Invalid card index.");
                    continue;
                }
                System.out.print("Enter the number of the limit (1-9): ");
                int limitIndex;
                try {
                    limitIndex = Integer.parseInt(scanner.nextLine()) - 1;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid entry.");
                    continue;
                }
                if (limitIndex < 0 || limitIndex >= state.getLimits().size()) {
                    System.out.println("Invalid limit index.");
                    continue;
                }
                if (playMove(current, cardIndex, limitIndex) == 0){
                    continue;
                }
            }

            checkWin();

            // Change player if game is not finished
            if (!state.isGameOver()) {
                state.switchPlayer();
            }
        }

        System.out.println("Good Game ! Winner: " + state.getWinner().getName() + " !!");
    }

    private String askPlayerName(String defaultName) {
        System.out.print("Enter player name (" + defaultName + ") : ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            return defaultName;
        }
        return name;
    }

    private int playMove(Player player, int cardIndex, int limitIndex) {
        Card card = player.removeCardFromHand(cardIndex);
        int playerIndex = state.getPlayers().indexOf(player) + 1; // 1 or 2
        Limit limit = state.getLimits().get(limitIndex);
        if (limit.getControl() != 0) {
            System.out.println("This limit is already claimed !");
            player.addCardToHand(card);
            return 0;
        }
        if (limit.isFull(playerIndex)) {
            System.out.println("This zone is full !");
            player.addCardToHand(card); // put the card back
            return 0; // Player have to play again
        }
        limit.addCard(playerIndex, card);
        limit.evaluate();

        if (!state.getDeck().isEmpty()) {
            player.addCardToHand(state.getDeck().drawCard());
        } else {
            System.out.println("Empty Deck.");
        }
        System.out.println(player.getName() + " plays " + card + " on limit " + (limitIndex + 1));
        state.updateClaims();
        return 1;
    }

    private void checkWin() {
        // Victory if 3 adjacent limits or 5limits in total for a player
        int control1 = 0, control2 = 0;
        int consecutive1 = 0, consecutive2 = 0;
        int maxConsecutive1 = 0, maxConsecutive2 = 0;

        for (Limit l : state.getLimits()) {
            if (l.getControl() == 1) {
                control1++;
                consecutive1++;
                if (consecutive1 > maxConsecutive1){
                    maxConsecutive1 = consecutive1;
                }
            } else {
                consecutive1 = 0;
            }
            if (l.getControl() == 2) {
                control2++;
                consecutive2++;
                if (consecutive2 > maxConsecutive2){
                    maxConsecutive2 = consecutive2;
                }
            } else {
                consecutive2 = 0;
            }
        }

        if (control1 >= 5 || maxConsecutive1 >= 3) {
            state.setGameOver(true);
            state.setWinner(1);
        } else if (control2 >= 5 || maxConsecutive2 >= 3) {
            state.setGameOver(true);
            state.setWinner(2);
        }
    }
}
