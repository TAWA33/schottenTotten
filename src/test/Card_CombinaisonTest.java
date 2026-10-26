package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import main.com.schttttt_game.model.Card;
import main.com.schttttt_game.model.Card_Combinaison;

public class Card_CombinaisonTest {

    @Test
    public void testEmptyConstructor() {
        Card_Combinaison cc = new Card_Combinaison();
        assertEquals(0, cc.getNbCards());
        assertTrue(cc.isEmpty());
    }

    @Test
    public void testListConstructorCopiesList() {
        List<Card> src = new ArrayList<>();
        src.add(new Card(1, "Blue"));
        Card_Combinaison cc = new Card_Combinaison(src);
        src.add(new Card(2, "Red")); // ne doit pas affecter cc
        assertEquals(1, cc.getNbCards());
    }

    @Test
    public void testAddCard() {
        Card_Combinaison cc = new Card_Combinaison();
        Card c = new Card(3, "Blue");
        cc.addCard(c);
        assertEquals(1, cc.getNbCards());
        assertSame(c, cc.getCard(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullCard() {
        new Card_Combinaison().addCard(null);
    }

    @Test
    public void testRemoveCard() {
        Card_Combinaison cc = new Card_Combinaison();
        Card c = new Card(3, "Blue");
        cc.addCard(c);
        cc.removeCard(c);
        assertTrue(cc.isEmpty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetCardInvalidIndex() {
        new Card_Combinaison().getCard(0);
    }

    @Test
    public void testGetCardsReturnsDefensiveCopy() {
        Card_Combinaison cc = new Card_Combinaison();
        cc.addCard(new Card(1, "Blue"));
        List<Card> copy = cc.getCards();
        copy.clear();
        assertEquals(1, cc.getNbCards());
    }

    @Test
    public void testGetCombinaisonIsImmutable() {
        Card_Combinaison cc = new Card_Combinaison();
        cc.addCard(new Card(1, "Blue"));
        try {
            cc.getCombinaison().add(new Card(2, "Red"));
            fail("Should be unmodifiable");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testClear() {
        Card_Combinaison cc = new Card_Combinaison();
        cc.addCard(new Card(1, "Blue"));
        cc.addCard(new Card(2, "Red"));
        cc.clear();
        assertTrue(cc.isEmpty());
    }

    @Test
    public void testToStringEmpty() {
        assertEquals("Cards : []", new Card_Combinaison().toString());
    }

    @Test
    public void testToStringWithCards() {
        Card_Combinaison cc = new Card_Combinaison();
        cc.addCard(new Card(1, "Blue"));
        cc.addCard(new Card(2, "Red"));
        String s = cc.toString();
        assertTrue(s.contains("Blue 1"));
        assertTrue(s.contains("Red 2"));
        assertTrue(s.contains(";"));
    }
}