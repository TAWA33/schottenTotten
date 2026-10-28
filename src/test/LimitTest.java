package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import main.com.schttttt_game.model.Card;
import main.com.schttttt_game.model.ClanCard;
import main.com.schttttt_game.model.Card_Combinaison;
import main.com.schttttt_game.model.Limit;

public class LimitTest {

    @Test
    public void testConstructor() {
        Limit l = new Limit(3);
        assertEquals(3, l.getIndex());
        assertEquals(0, l.getControl());
        assertEquals(0, l.getFirstToComplete());
    }

    @Test
    public void testGetZone() {
        Limit l = new Limit(0);
        assertNotNull(l.getZone(1));
        assertNotNull(l.getZone(2));
        assertEquals(0, l.getZone(1).getNbCards());
    }

    @Test
    public void testSetControl() {
        Limit l = new Limit(0);
        l.setControl(2);
        assertEquals(2, l.getControl());
    }

    @Test
    public void testIsFull() {
        Limit l = new Limit(0);
        assertFalse(l.isFull(1));
        l.addCard(1, new ClanCard(1, "Blue"));
        l.addCard(1, new ClanCard(2, "Blue"));
        assertFalse(l.isFull(1));
        l.addCard(1, new ClanCard(3, "Blue"));
        assertTrue(l.isFull(1));
        assertFalse(l.isFull(2));
    }

    @Test(expected = IllegalStateException.class)
    public void testAddCardWhenFullThrows() {
        Limit l = new Limit(0);
        l.addCard(1, new ClanCard(1, "Blue"));
        l.addCard(1, new ClanCard(2, "Blue"));
        l.addCard(1, new ClanCard(3, "Blue"));
        l.addCard(1, new ClanCard(4, "Blue")); // -> exception Full
    }

    @Test
    public void testFirstToCompleteIsRecorded() {
        Limit l = new Limit(0);
        l.addCard(1, new ClanCard(1, "Blue"));
        l.addCard(1, new ClanCard(2, "Blue"));
        l.addCard(1, new ClanCard(3, "Blue"));
        assertEquals(1, l.getFirstToComplete());

        l.addCard(2, new ClanCard(4, "Red"));
        l.addCard(2, new ClanCard(5, "Red"));
        l.addCard(2, new ClanCard(6, "Red"));
        assertEquals(1, l.getFirstToComplete()); // still player 1
    }


    @Test
    public void testEvaluateCombinationQuinteFlush() {
        Card_Combinaison cc = new Card_Combinaison();
        cc.addCard(new ClanCard(1, "Blue"));
        cc.addCard(new ClanCard(2, "Blue"));
        cc.addCard(new ClanCard(3, "Blue"));
        assertEquals(5006, Limit.evaluateCombination(cc)); // 5000 + 1+2+3
    }

    @Test
    public void testEvaluateCombinationBrelan() {
        Card_Combinaison cc = new Card_Combinaison();
        cc.addCard(new ClanCard(5, "Blue"));
        cc.addCard(new ClanCard(5, "Red"));
        cc.addCard(new ClanCard(5, "Green"));
        assertEquals(4015, Limit.evaluateCombination(cc)); // 4000 + 5*3
    }

    @Test
    public void testEvaluateCombinationCouleur() {
        Card_Combinaison cc = new Card_Combinaison();
        cc.addCard(new ClanCard(1, "Blue"));
        cc.addCard(new ClanCard(5, "Blue"));
        cc.addCard(new ClanCard(9, "Blue"));
        assertEquals(3015, Limit.evaluateCombination(cc)); // 3000 + 15
    }

    @Test
    public void testEvaluateCombinationSuite() {
        Card_Combinaison cc = new Card_Combinaison();
        cc.addCard(new ClanCard(1, "Blue"));
        cc.addCard(new ClanCard(2, "Red"));
        cc.addCard(new ClanCard(3, "Green"));
        assertEquals(2006, Limit.evaluateCombination(cc)); // 2000 + 6
    }

    @Test
    public void testEvaluateCombinationSum() {
        Card_Combinaison cc = new Card_Combinaison();
        cc.addCard(new ClanCard(1, "Blue"));
        cc.addCard(new ClanCard(5, "Red"));
        cc.addCard(new ClanCard(9, "Green"));
        assertEquals(1015, Limit.evaluateCombination(cc)); // 1000 + 15
    }

    @Test
    public void testEvaluateCombinationNotEnoughCards() {
        Card_Combinaison cc = new Card_Combinaison();
        cc.addCard(new ClanCard(1, "Blue"));
        assertEquals(0, Limit.evaluateCombination(cc));
    }

    @Test
    public void testEvaluateOnlyOneSideFullKeepsControlZero() {
        Limit l = new Limit(0);
        l.addCard(1, new ClanCard(5, "Blue"));
        l.addCard(1, new ClanCard(5, "Red"));
        l.addCard(1, new ClanCard(5, "Green"));
        l.evaluate();
        assertEquals(0, l.getControl()); 
        // we cannot choose (we didn't create the deck, so no claim possible). control didn't change
    }

    @Test
    public void testEvaluateQuinteFlushBeatsBrelan() {
        Limit l = new Limit(0);
        // P1 : brelan of 5 => 4015
        l.addCard(1, new ClanCard(5, "Blue"));
        l.addCard(1, new ClanCard(5, "Red"));
        l.addCard(1, new ClanCard(5, "Green"));
        // P2 : quinte flush 1-2-3 red => 5006
        l.addCard(2, new ClanCard(1, "Red"));
        l.addCard(2, new ClanCard(2, "Red"));
        l.addCard(2, new ClanCard(3, "Red"));
        l.evaluate();
        assertEquals(2, l.getControl());
    }

    @Test
    public void testEvaluateTieGoesToFirstToComplete() {
        Limit l = new Limit(0);
        // P1 first to complete (brelan of 5)
        l.addCard(1, new ClanCard(5, "Blue"));
        l.addCard(1, new ClanCard(5, "Red"));
        l.addCard(1, new ClanCard(5, "Green"));
        // P2 after (brelan of 5 same score)
        l.addCard(2, new ClanCard(5, "Yellow"));
        l.addCard(2, new ClanCard(5, "Purple"));
        l.addCard(2, new ClanCard(5, "Grey"));
        l.evaluate();
        assertEquals(1, l.getControl());
    }

    @Test
    public void testCanClaimFalseWhenNotFull() {
        Limit l = new Limit(0);
        l.addCard(1, new ClanCard(1, "Blue"));
        assertFalse(l.canClaim(1, new ArrayList<>()));
    }

    @Test
    public void testCanClaimBothFullHigherScoreWins() {
        Limit l = new Limit(0);
        // P1 : brelan of 5 => 4015
        l.addCard(1, new ClanCard(5, "Blue"));
        l.addCard(1, new ClanCard(5, "Red"));
        l.addCard(1, new ClanCard(5, "Green"));
        // P2 : sum => 1007
        l.addCard(2, new ClanCard(1, "Blue"));
        l.addCard(2, new ClanCard(2, "Red"));
        l.addCard(2, new ClanCard(4, "Green"));

        assertTrue(l.canClaim(1, new ArrayList<>()));
        assertFalse(l.canClaim(2, new ArrayList<>()));
    }

    @Test
    public void testCanClaimTrueWhenOpponentCannotBeat() {
        Limit l = new Limit(0);
        // P1 : brelan of 5 => 4015
        l.addCard(1, new ClanCard(5, "Blue"));
        l.addCard(1, new ClanCard(5, "Red"));
        l.addCard(1, new ClanCard(5, "Green"));
        // P2 : 2 cartes (missing 1 card), cannot beat the brelan with this pool
        l.addCard(2, new ClanCard(1, "Blue"));
        l.addCard(2, new ClanCard(2, "Blue"));

        List<Card> pool = new ArrayList<>();
        pool.add(new ClanCard(3, "Red"));   // suite with different colors => 2006
        pool.add(new ClanCard(4, "Green")); // sum => 1007

        assertTrue(l.canClaim(1, pool));
    }

    @Test
    public void testCannotClaimWhenOpponentCanBeat() {
        Limit l = new Limit(0);
        // P1 : brelan of 5 => 4015
        l.addCard(1, new ClanCard(5, "Blue"));
        l.addCard(1, new ClanCard(5, "Red"));
        l.addCard(1, new ClanCard(5, "Green"));
        // P2 : 1B + 2B
        l.addCard(2, new ClanCard(1, "Blue"));
        l.addCard(2, new ClanCard(2, "Blue"));

        // pool contains 3B => P2 can do quinte flush 5006 > 4015
        List<Card> pool = new ArrayList<>();
        pool.add(new ClanCard(3, "Blue"));

        assertFalse(l.canClaim(1, pool));
    }

    @Test
    public void testClaimSetsControl() {
        Limit l = new Limit(0);
        l.addCard(1, new ClanCard(5, "Blue"));
        l.addCard(1, new ClanCard(5, "Red"));
        l.addCard(1, new ClanCard(5, "Green"));
        l.addCard(2, new ClanCard(1, "Blue"));
        l.addCard(2, new ClanCard(2, "Red"));
        l.addCard(2, new ClanCard(4, "Green"));

        assertTrue(l.claim(1, new ArrayList<>()));
        assertEquals(1, l.getControl());
    }

    @Test
    public void testClaimFailsWhenNotEligible() {
        Limit l = new Limit(0);
        l.addCard(1, new ClanCard(1, "Blue"));
        assertFalse(l.claim(1, new ArrayList<>()));
        assertEquals(0, l.getControl());
    }
}