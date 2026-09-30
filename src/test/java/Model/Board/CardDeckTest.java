package Model.Board;

import Model.Board.AdventureCards.AdventureCardFilip;
import Model.Board.AdventureCards.OpenSpace;
import Model.Enums.CardLevel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the CardDeck class.
 * Tests all constructors, methods, and edge cases for 100% coverage.
 */
public class CardDeckTest {

    /**
     * Tests default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        CardDeck deck = new CardDeck();
        assertEquals(0, deck.peekCards().size());
        assertTrue(deck.peekCards().isEmpty());
    }

    /**
     * Tests constructor with List parameter.
     */
    @Test
    public void testConstructorWithList() {
        List<AdventureCardFilip> cards = new ArrayList<>();
        cards.add(new OpenSpace(1, CardLevel.LEARNER));
        cards.add(new OpenSpace(2, CardLevel.LEVEL_ONE));
        
        CardDeck deck = new CardDeck(cards);
        assertEquals(2, deck.peekCards().size());
        assertFalse(deck.peekCards().isEmpty());
    }

    /**
     * Tests constructor with Array parameter.
     */
    @Test
    public void testConstructorWithArray() {
        AdventureCardFilip[] cards = {
            new OpenSpace(1, CardLevel.LEARNER),
            new OpenSpace(2, CardLevel.LEVEL_ONE),
            new OpenSpace(3, CardLevel.LEVEL_TWO)
        };
        
        CardDeck deck = new CardDeck(cards);
        assertEquals(3, deck.peekCards().size());
    }
    
    /**
     * Tests constructor with empty list.
     */
    @Test
    public void testConstructorWithEmptyList() {
        CardDeck deck = new CardDeck(new ArrayList<>());
        assertEquals(0, deck.peekCards().size());
        assertTrue(deck.peekCards().isEmpty());
    }
    
    /**
     * Tests popCard method.
     */
    @Test
    public void testPopCard() {
        List<AdventureCardFilip> cards = new ArrayList<>();
        OpenSpace card1 = new OpenSpace(1, CardLevel.LEARNER);
        OpenSpace card2 = new OpenSpace(2, CardLevel.LEVEL_ONE);
        cards.add(card1);
        cards.add(card2);
        
        CardDeck deck = new CardDeck(cards);
        AdventureCardFilip drawn = deck.popCard();
        
        assertEquals(1, deck.peekCards().size());
        assertNotNull(drawn);
        assertEquals(card1, drawn); // Should be first card (removeFirst)
    }
    
    /**
     * Tests popCard from empty deck throws exception.
     */
    @Test
    public void testPopFromEmptyDeck() {
        CardDeck deck = new CardDeck();
        assertThrows(Exception.class, deck::popCard);
    }

    /**
     * Tests pushCard method.
     */
    @Test
    public void testPushCard() {
        CardDeck deck = new CardDeck();
        OpenSpace card = new OpenSpace(1, CardLevel.LEARNER);
        
        deck.pushCard(card);
        assertEquals(1, deck.peekCards().size());
        
        AdventureCardFilip popped = deck.popCard();
        assertEquals(card, popped);
    }

    /**
     * Tests pushCard and popCard order (LIFO).
     */
    @Test
    public void testPushPopOrder() {
        CardDeck deck = new CardDeck();
        OpenSpace card1 = new OpenSpace(1, CardLevel.LEARNER);
        OpenSpace card2 = new OpenSpace(2, CardLevel.LEVEL_ONE);
        
        deck.pushCard(card1);
        deck.pushCard(card2);
        
        assertEquals(card2, deck.popCard()); // Last pushed, first popped
        assertEquals(card1, deck.popCard());
    }

    /**
     * Tests shuffleDeck method creates new deck.
     */
    @Test
    public void testShuffleDeck() {
        List<AdventureCardFilip> cards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cards.add(new OpenSpace(i, CardLevel.LEARNER));
        }
        
        CardDeck originalDeck = new CardDeck(cards);
        CardDeck shuffledDeck = originalDeck.shuffleDeck();
        
        // Should be different instances
        assertNotSame(originalDeck, shuffledDeck);
        
        // Should have same size
        assertEquals(originalDeck.peekCards().size(), shuffledDeck.peekCards().size());
        
        // Original deck should be unchanged
        assertEquals(10, originalDeck.peekCards().size());
        assertEquals(10, shuffledDeck.peekCards().size());
    }

    /**
     * Tests shuffle method modifies current deck.
     */
    @Test
    public void testShuffle() {
        List<AdventureCardFilip> cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cards.add(new OpenSpace(i, CardLevel.LEARNER));
        }
        
        CardDeck deck = new CardDeck(cards);
        int originalSize = deck.peekCards().size();
        
        deck.shuffle();
        
        // Size should remain the same
        assertEquals(originalSize, deck.peekCards().size());
    }

    /**
     * Tests peekCards returns copy.
     */
    @Test
    public void testPeekCardsReturnsCopy() {
        List<AdventureCardFilip> cards = new ArrayList<>();
        cards.add(new OpenSpace(1, CardLevel.LEARNER));
        
        CardDeck deck = new CardDeck(cards);
        List<AdventureCardFilip> peeked1 = deck.peekCards();
        List<AdventureCardFilip> peeked2 = deck.peekCards();
        
        // Should be different instances (copies)
        assertNotSame(peeked1, peeked2);
        
        // But should have same content
        assertEquals(peeked1.size(), peeked2.size());
        assertEquals(peeked1.get(0), peeked2.get(0));
    }

    /**
     * Tests iterator functionality.
     */
    @Test
    public void testIterator() {
        List<AdventureCardFilip> cards = new ArrayList<>();
        OpenSpace card1 = new OpenSpace(1, CardLevel.LEARNER);
        OpenSpace card2 = new OpenSpace(2, CardLevel.LEVEL_ONE);
        cards.add(card1);
        cards.add(card2);
        
        CardDeck deck = new CardDeck(cards);
        
        int count = 0;
        for (AdventureCardFilip card : deck) {
            count++;
            assertTrue(card == card1 || card == card2);
        }
        assertEquals(2, count);
    }

    /**
     * Tests iterator on empty deck.
     */
    @Test
    public void testIteratorEmptyDeck() {
        CardDeck deck = new CardDeck();
        
        int count = 0;
        for (AdventureCardFilip card : deck) {
            count++;
        }
        assertEquals(0, count);
    }

    /**
     * Tests iterator hasNext and next methods.
     */
    @Test
    public void testIteratorMethods() {
        List<AdventureCardFilip> cards = new ArrayList<>();
        cards.add(new OpenSpace(1, CardLevel.LEARNER));
        
        CardDeck deck = new CardDeck(cards);
        Iterator<AdventureCardFilip> iterator = deck.iterator();
        
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Tests multiple operations together.
     */
    @Test
    public void testMultipleOperations() {
        CardDeck deck = new CardDeck();
        
        // Push some cards
        deck.pushCard(new OpenSpace(1, CardLevel.LEARNER));
        deck.pushCard(new OpenSpace(2, CardLevel.LEVEL_ONE));
        assertEquals(2, deck.peekCards().size());
        
        // Pop one card
        deck.popCard();
        assertEquals(1, deck.peekCards().size());
        
        // Shuffle
        deck.shuffle();
        assertEquals(1, deck.peekCards().size());
        
        // Create shuffled copy
        CardDeck shuffled = deck.shuffleDeck();
        assertEquals(1, shuffled.peekCards().size());
        assertEquals(1, deck.peekCards().size()); // Original unchanged
    }

    /**
     * Tests deck with single card.
     */
    @Test
    public void testSingleCard() {
        OpenSpace card = new OpenSpace(1, CardLevel.LEARNER);
        CardDeck deck = new CardDeck();
        
        deck.pushCard(card);
        assertEquals(1, deck.peekCards().size());
        
        AdventureCardFilip popped = deck.popCard();
        assertEquals(card, popped);
        assertEquals(0, deck.peekCards().size());
    }

    /**
     * Tests render method with empty deck.
     */
    @Test
    public void testRenderEmptyDeck() {
        CardDeck deck = new CardDeck();
        assertDoesNotThrow(() -> deck.render(null));
        assertDoesNotThrow(() -> deck.render(new ArrayList<>()));
    }

    /**
     * Tests render method with cards.
     */
    @Test
    public void testRenderWithCards() {
        List<AdventureCardFilip> cards = new ArrayList<>();
        cards.add(new OpenSpace(1, CardLevel.LEARNER));
        cards.add(new OpenSpace(2, CardLevel.LEVEL_ONE));
        
        CardDeck deck = new CardDeck();
        assertDoesNotThrow(() -> deck.render(cards));
    }
}