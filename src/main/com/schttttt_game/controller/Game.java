package main.com.schttttt_game.controller;

import java.util.Scanner;

import main.com.schttttt_game.ai.Bot;
import main.com.schttttt_game.model.*;
import main.com.schttttt_game.view.*;

public class Game {
    private Game_State state;
    private Scanner scanner;

    public Game() {
        state = new Game_State();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome on Schotten Totten !");
        int variant_choice = Menu.showMenu(scanner);
        GameVariant variant = GameFactory.createVariant(variant_choice);

        int mode_choice = Menu.showPlayerMode(scanner);

        String p1Name = askPlayerName("Player 1");
        Player p1 = new Player(p1Name, false, variant.getHandSize());

        Player p2;
        if (mode_choice == 2) {
            p2 = new Bot("AI", variant.getHandSize());
        } else {
            String p2Name = askPlayerName("Player 2");
            p2 = new Player(p2Name, false, variant.getHandSize());
        }
        state.addPlayer(p1);
        state.addPlayer(p2);

        // Deck init
        Deck clanDeck = variant.createClanDeck();
        Deck tacticDeck = variant.createTacticDeck();
        clanDeck.shuffle();
        if (!tacticDeck.isEmpty()){
            tacticDeck.shuffle();
        }
        state.setClanDeck(clanDeck);
        state.setTacticDeck(tacticDeck);

        // Init of the 9 limits
        for (int i = 0; i < 9; i++) {
            state.addLimit(new Limit(i));
        }

        // Fill the hand of each player
        int handSize = variant.getHandSize();
        for (int i = 0; i < handSize; i++) {
            p1.addCardToHand(clanDeck.drawCard());
            p2.addCardToHand(clanDeck.drawCard());
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

                System.out.print("Enter the number of the card (1-" + current.getHand().getNbCards() +") or 'q' to quit: ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("q")) {
                    System.out.println("Thank you for playing !");
                    return;
                }
                int cardIndex;
                try {
                    cardIndex = Integer.parseInt(input)-1;
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

    private void drawAfterPlay(Player player) {
        Deck clan = state.getClanDeck();
        Deck tactic = state.getTacticDeck();
        boolean clanEmpty = clan == null || clan.isEmpty();
        boolean tacticEmpty = tactic == null || tactic.isEmpty();

        if (clanEmpty && tacticEmpty) {
            System.out.println("Both decks are empty, no draw.");
            return;
        }

        Deck chosen;
        if (clanEmpty) {
            chosen = tactic;
        } else if (tacticEmpty) {
            chosen = clan;
        } else if (player.isAI()) {
            chosen = ((Bot) player).chooseDeck(state);
        } else {
            System.out.println("Clan deck: " + clan.size() + " cards | Tactic deck: " + tactic.size() + " cards");
            System.out.print("Draw from (1 = Clan, 2 = Tactic): ");
            try {
                int c = Integer.parseInt(scanner.nextLine());
                chosen = (c == 2) ? tactic : clan;
            } catch (NumberFormatException e) {
                System.out.println("Invalid entry, drawing from Clan deck.");
                chosen = clan;
            }
        }
        player.addCardToHand(chosen.drawCard());
    }

    private int playMove(Player player, int cardIndex, int limitIndex) {
        Card card = player.removeCardFromHand(cardIndex);
        int playerIndex = state.getPlayers().indexOf(player) + 1;
        Limit limit = state.getLimits().get(limitIndex);

        if (limit.getControl() != 0) {
            System.out.println("This limit is already claimed !");
            player.addCardToHand(card);
            return 0;
        }

        // Combat Mode (MU / BL)
        if (card instanceof TacticCard) {
            TacticCard tc = (TacticCard) card;
            if (tc.getType() == TacticType.MUD_FIGHT || tc.getType() == TacticType.BLIND_MAN_BLUFF) {
                if (limit.getCombatModeCard() != null) {
                    System.out.println("This limit already has a combat mode card !");
                    player.addCardToHand(card);
                    return 0;
                }
                if (!state.canPlayTactic(playerIndex)) {
                    System.out.println("You cannot play another Tactic Card.");
                    player.addCardToHand(card);
                    return 0;
                }
                limit.setCombatModeCard(tc);
                state.incrementTacticsPlayed(playerIndex);
                drawAfterPlay(player);
                System.out.println(player.getName() + " plays " + tc + " on limit " + (limitIndex + 1) + " (combat mode)");
                state.updateClaims();
                return 1;
            }

            // (Joker, Spy, ShieldBearer, ruses)
            if (tc.isRuse()) {
                System.out.println("Ruse cards are not implemented yet.");
                player.addCardToHand(card);
                return 0;
            }
            if (!state.canPlayTactic(playerIndex)) {
                System.out.println("You cannot play another Tactic Card.");
                player.addCardToHand(card);
                return 0;
            }
            state.incrementTacticsPlayed(playerIndex);
        }

        // Clan Card
        if (limit.isFull(playerIndex)) {
            System.out.println("This zone is full !");
            player.addCardToHand(card);
            return 0;
        }
        limit.addCard(playerIndex, card);
        limit.evaluate();

        drawAfterPlay(player);
        System.out.println(player.getName() + " plays " + card + " on limit " + (limitIndex + 1));
        state.updateClaims();
        return 1;
    }

    private void checkWin() {
        int w = state.computeWinner();
        if (w != 0) {
            state.setGameOver(true);
            state.setWinner(w);
        }
    }
}
