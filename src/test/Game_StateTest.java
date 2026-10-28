package test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import main.com.schttttt_game.model.ClanCard;
import main.com.schttttt_game.model.Deck;
import main.com.schttttt_game.model.Game_State;
import main.com.schttttt_game.model.Limit;
import main.com.schttttt_game.model.Player;

public class Game_StateTest {

    private Game_State newStateWithLimits(int n) {
        Game_State s = new Game_State();
        s.addPlayer(new Player("Titi", false));
        s.addPlayer(new Player("Toto", false));
        for (int i = 0; i < n; i++) {
            s.addLimit(new Limit(i));
        }
        return s;
    }

    private void setControl(Game_State s, int limitIndex, int player) {
        s.getLimits().get(limitIndex).setControl(player);
    }

    @Test
    public void testConstructorInitialState() {
        Game_State s = new Game_State();
        assertTrue(s.getPlayers().isEmpty());
        assertTrue(s.getLimits().isEmpty());
        assertEquals(0, s.getCurrentPlayerIndex());
        assertFalse(s.isGameOver());
        assertNull(s.getDeck());
    }

    @Test
    public void testAddPlayer() {
        Game_State s = new Game_State();
        Player p = new Player("Titi", false);
        s.addPlayer(p);
        assertEquals(1, s.getPlayers().size());
        assertSame(p, s.getPlayers().get(0));
    }

    @Test
    public void testAddLimit() {
        Game_State s = new Game_State();
        Limit l = new Limit(0);
        s.addLimit(l);
        assertEquals(1, s.getLimits().size());
        assertSame(l, s.getLimits().get(0));
    }

    @Test
    public void testSetAndGetDeck() {
        Game_State s = new Game_State();
        Deck d = new Deck();
        s.setDeck(d);
        assertSame(d, s.getDeck());
    }

    @Test
    public void testGetCurrentPlayerInitial() {
        Game_State s = newStateWithLimits(0);
        assertEquals("Titi", s.getCurrentPlayer().getName());
    }

    @Test
    public void testSwitchPlayer() {
        Game_State s = newStateWithLimits(0);
        assertEquals(0, s.getCurrentPlayerIndex());
        s.switchPlayer();
        assertEquals(1, s.getCurrentPlayerIndex());
        assertEquals("Toto", s.getCurrentPlayer().getName());
        s.switchPlayer();
        assertEquals(0, s.getCurrentPlayerIndex());
    }

    @Test
    public void testGetValidLimitsAllEmpty() {
        Game_State s = newStateWithLimits(3);
        List<Integer> valid = s.getValidLimits(1);
        assertEquals(3, valid.size());
        assertTrue(valid.contains(0));
        assertTrue(valid.contains(1));
        assertTrue(valid.contains(2));
    }

    @Test
    public void testGetValidLimitsExcludesFullZone() {
        Game_State s = newStateWithLimits(2);
        Limit l0 = s.getLimits().get(0);
        l0.addCard(1, new ClanCard(1, "Blue"));
        l0.addCard(1, new ClanCard(2, "Blue"));
        l0.addCard(1, new ClanCard(3, "Blue"));

        List<Integer> forP1 = s.getValidLimits(1);
        assertEquals(1, forP1.size());
        assertEquals(Integer.valueOf(1), forP1.get(0));

        List<Integer> forP2 = s.getValidLimits(2);
        assertEquals(2, forP2.size());
    }

    @Test
    public void testGetValidLimitsExcludesControlledLimit() {
        Game_State s = newStateWithLimits(2);
        s.getLimits().get(0).setControl(1);

        List<Integer> forP1 = s.getValidLimits(1);
        assertEquals(1, forP1.size());
        assertEquals(Integer.valueOf(1), forP1.get(0));

        List<Integer> forP2 = s.getValidLimits(2);
        assertEquals(1, forP2.size());
        assertEquals(Integer.valueOf(1), forP2.get(0));
    }

    @Test
    public void testGetValidLimitsEmptyWhenAllControlledOrFull() {
        Game_State s = newStateWithLimits(2);
        s.getLimits().get(0).setControl(1);
        Limit l1 = s.getLimits().get(1);
        l1.addCard(1, new ClanCard(1, "Blue"));
        l1.addCard(1, new ClanCard(2, "Blue"));
        l1.addCard(1, new ClanCard(3, "Blue"));

        assertTrue(s.getValidLimits(1).isEmpty());
        assertEquals(1, s.getValidLimits(2).size());
    }

    @Test
    public void testGameOverFlag() {
        Game_State s = new Game_State();
        assertFalse(s.isGameOver());
        s.setGameOver(true);
        assertTrue(s.isGameOver());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetWinnerWhenNoWinnerThrows() {
        new Game_State().getWinner();
    }

    @Test
    public void testSetWinnerAndGetWinner() {
        Game_State s = newStateWithLimits(0);
        s.setWinner(2);
        assertEquals("Toto", s.getWinner().getName());
    }

    @Test
    public void testComputeWinnerEmptyBoard() {
        Game_State s = newStateWithLimits(9);
        assertEquals(0, s.computeWinner());
    }

    @Test
    public void testComputeWinnerOneControl() {
        Game_State s = newStateWithLimits(9);
        setControl(s, 0, 1);
        assertEquals(0, s.computeWinner());
    }

    @Test
    public void testComputeWinnerTwoNonAdjacentControls() {
        Game_State s = newStateWithLimits(9);
        setControl(s, 0, 1);
        setControl(s, 5, 1);
        assertEquals(0, s.computeWinner());
    }

    @Test
    public void testComputeWinnerThreeAdjacentStart() {
        Game_State s = newStateWithLimits(9);
        setControl(s, 0, 1);
        setControl(s, 1, 1);
        setControl(s, 2, 1);
        assertEquals(1, s.computeWinner());
    }

    @Test
    public void testComputeWinnerThreeAdjacentEnd() {
        Game_State s = newStateWithLimits(9);
        setControl(s, 6, 1);
        setControl(s, 7, 1);
        setControl(s, 8, 1);
        assertEquals(1, s.computeWinner());
    }

    @Test
    public void testComputeWinnerThreeAdjacentMiddle() {
        Game_State s = newStateWithLimits(9);
        setControl(s, 3, 1);
        setControl(s, 4, 1);
        setControl(s, 5, 1);
        assertEquals(1, s.computeWinner());
    }

    @Test
    public void testComputeWinnerNotThreeAdjacentBecauseOfGap() {
        Game_State s = newStateWithLimits(9);
        setControl(s, 0, 1);
        setControl(s, 1, 1);
        setControl(s, 3, 1); // hole in 2
        assertEquals(0, s.computeWinner());
    }

    @Test
    public void testComputeWinnerThreeNonAdjacent() {
        Game_State s = newStateWithLimits(9);
        setControl(s, 0, 1);
        setControl(s, 2, 1);
        setControl(s, 4, 1);
        assertEquals(0, s.computeWinner());
    }

    @Test
    public void testComputeWinnerFiveNonAdjacent() {
        Game_State s = newStateWithLimits(9);
        setControl(s, 0, 1);
        setControl(s, 2, 1);
        setControl(s, 4, 1);
        setControl(s, 6, 1);
        setControl(s, 8, 1);
        assertEquals(1, s.computeWinner());
    }

    @Test
    public void testComputeWinnerFourNonAdjacentNotEnough() {
        Game_State s = newStateWithLimits(9);
        setControl(s, 0, 1);
        setControl(s, 2, 1);
        setControl(s, 4, 1);
        setControl(s, 6, 1);
        assertEquals(0, s.computeWinner());
    }


    @Test
    public void testComputeWinnerPlayer1FiveAndPlayer2Three() {
        Game_State s = newStateWithLimits(9);
        setControl(s, 0, 1);
        setControl(s, 2, 1);
        setControl(s, 4, 1);
        setControl(s, 6, 1);
        setControl(s, 8, 1); // 5 non adjacents -> P1
        setControl(s, 3, 2);
        setControl(s, 5, 2);
        setControl(s, 7, 2); // 3 non adjacents (3,5,7)
        assertEquals(1, s.computeWinner());
    }

    @Test
    public void testComputeWinnerPlayer2ThreeAdjacentWhilePlayer1Four() {
        Game_State s = newStateWithLimits(9);
        // P1 control 4 limits non adjacents
        setControl(s, 0, 1);
        setControl(s, 2, 1);
        setControl(s, 7, 1);
        setControl(s, 6, 1);
        // P2 control 3 adjacents
        setControl(s, 3, 2);
        setControl(s, 5, 2);
        setControl(s, 4, 2);

        assertEquals(2, s.computeWinner());
    }

    @Test
    public void testComputeWinnerFourEachNobodyWins() {
        Game_State s = newStateWithLimits(9);
        setControl(s, 0, 1);
        setControl(s, 2, 1);
        setControl(s, 4, 1);
        setControl(s, 6, 1);
        setControl(s, 1, 2);
        setControl(s, 3, 2);
        setControl(s, 5, 2);
        setControl(s, 7, 2);
        // limit 8 without control
        assertEquals(0, s.computeWinner());
    }

    @Test
    public void testComputeWinnerBothQualifyPlayer1Wins() {
        Game_State s = newStateWithLimits(9);
        
        for (int i = 0; i < 9; i++) s.getLimits().get(i).setControl(0);
        setControl(s, 0, 1);
        setControl(s, 1, 1);
        setControl(s, 2, 1); // P1 : 3 adjacents
        setControl(s, 3, 2);
        setControl(s, 4, 2);
        setControl(s, 5, 2); // P2 : 3 adjacents
        assertEquals(1, s.computeWinner()); // P1 first to complete
    }
}