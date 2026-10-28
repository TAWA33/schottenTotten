package main.com.schttttt_game.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.com.schttttt_game.model.*;

public class Bot extends Player {
    private Random random = new Random();

    public Bot(String name, int maxHandSize) {
        super(name, true, maxHandSize); // true = AI
    }

    public Bot(String name) { // Only for tests !
        super(name, true, 6);
    }

    public Deck chooseDeck(Game_State state) {
        Deck clan = state.getClanDeck();
        Deck tactic = state.getTacticDeck();
        if (tactic == null || tactic.isEmpty()){
            return clan;
        }
        if (clan == null || clan.isEmpty()){
            return tactic;
        }
        return random.nextBoolean() ? clan : tactic;
    }

    public int[] makeRandomMove(Game_State state) {
        // return {indexCarte, indexBorne}
        Player current = state.getCurrentPlayer();
        int handSize = current.getHand().getNbCards();
        if (handSize == 0) return new int[]{-1, -1};

        int playerIndex = state.getCurrentPlayerIndex() + 1;
        List<Integer> validLimits = state.getValidLimits(playerIndex);
        if (validLimits.isEmpty()){
            return new int[]{-1, -1};
        }

        int cardIndex = random.nextInt(handSize);
        int limitIndex = validLimits.get(random.nextInt(validLimits.size()));
        return new int[]{cardIndex, limitIndex};
    }

    

}