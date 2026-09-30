package Model.Board;

import Model.Board.AdventureCards.AdventureCardFilip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Basically a wrapper of {@code List<AdventureCard>}.
 *
 * <p>Originally intended as an abstraction to be
 * further expanded upon, it is currently only a
 * wrapper.
 *
 * <p>Would benefit if further specialized into
 * {@code HiddenCardDeck}, {@code PeekableCardDeck}
 * and {@code UpcomingCardDeck}. Why? Mostly because
 * not every method should be usable for each type
 * of deck (and even if so they should not all
 * behave the same way).
 *
 * @see List
 * @see AdventureCardFilip
 */
public class CardDeck implements Iterable<AdventureCardFilip> , Serializable, Cloneable {
    /**
     * {@code List} of {@code AdventureCard}
     *
     * @see List
     * @see AdventureCardFilip
     */
    private final List<AdventureCardFilip> cards;

    /**
     * Default {@code CardDeck} constructor.
     *
     * <p>Sets {@code this.cards} to be an empty
     * {@code ArrayList} of {@code AdventureCard}s.
     */
    public CardDeck() {
        this.cards = new ArrayList<>();
    }

    /**
     * {@code CardDeck} constructor given a {@code List}
     * of {@code AdventureCard}s.
     *
     * <p>Sets {@code this.cards} as an {@code ArrayList}
     * copy of the {@code cards} parameter.
     *
     * @param cards {@code List} of {@code AdventureCards}
     *              to be copied
     */
    public CardDeck(List<AdventureCardFilip> cards) {
        this();

        this.cards.addAll(cards);
    }

    /**
     * {@code CardDeck} constructor given an {@code Array}
     * of {@code AdventureCard}s.
     *
     * <p>Sets {@code this.cards} as an {@code ArrayList}
     * copy of the {@code cards} parameter.
     *
     * @param cards {@code Array} of {@code AventureCard}s
     *              to be copied
     */
    public CardDeck(AdventureCardFilip[] cards) {
        this(Arrays.asList(cards));
    }

    /**
     * {@code CardDeck} suffler: creates new {@code CardDeck}
     * with shuffled {@code AdventureCards}. Does
     * <i>not</i> change {@code this} deck's {@code List}
     * of {@code AdventureCards}.
     *
     * @return new {@code CardDeck} with elements of
     * {@code this}, but shuffled
     */
    public CardDeck shuffleDeck() {
        CardDeck other = new CardDeck(this.cards);

        Collections.shuffle(other.cards);

        return other;
    }

    /**
     * Removes the first {@code AdventureCard}
     * from the deck and it returns it
     *
     * @return card {@code AdventureCard} that
     * was on top of the deck
     */
    public AdventureCardFilip popCard() {
        return cards.removeFirst();
    }

    /**
     * Puts {@code AdventureCard} {@code card} at the top
     * of the deck.
     *
     * <p>Any call to {@code popCard} will return the
     * content of the latest {@code pushCard} call.
     *
     * @param card Card to be placed on top of the deck
     */
    public void pushCard(AdventureCardFilip card) {
        cards.addFirst(card);
    }

    /**
     * This method creates a copy of the cards
     * present in the deck and returns them as
     * a List.
     *
     * @return copy of {@code this.cards} list
     */
    public List<AdventureCardFilip> peekCards() {
        ArrayList<AdventureCardFilip> cards = new ArrayList<>();

        for (AdventureCardFilip card : this.cards) {
            cards.add(card);
        }

        return cards;
    }

    @Override
    public Iterator<AdventureCardFilip> iterator() {
        return peekCards().iterator();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public static void render(List<AdventureCardFilip> mazzo) {
        if (mazzo == null || mazzo.isEmpty()) {
            System.out.println("Empty Deck");
            return;
        }

        // Otteniamo tutte le visualizzazioni delle carte
        String[][] visualizzazioni = new String[mazzo.size()][];
        int maxAltezza = 0;

        for (int i = 0; i < mazzo.size(); i++) {
            visualizzazioni[i] = mazzo.get(i).visualizeString();
            maxAltezza = Math.max(maxAltezza, visualizzazioni[i].length);
        }

        // Stampiamo riga per riga
        for (int riga = 0; riga < maxAltezza; riga++) {
            for (int carta = 0; carta < mazzo.size(); carta++) {
                if (riga < visualizzazioni[carta].length) {
                    System.out.printf("%-45s", visualizzazioni[carta][riga]); // 45 caratteri per carta
                } else {
                    System.out.printf("%-45s", ""); // Spazio vuoto se la carta è più corta
                }
            }
            System.out.println();
        }
    }

    @Override
    public CardDeck clone() {
        CardDeck clone = new CardDeck();

        for(AdventureCardFilip card : cards) {
            clone.cards.add(card.clone());
        }

        return clone;
    }
}
