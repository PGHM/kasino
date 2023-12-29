package kasino;


 /* 
 * Card represents a single card in a Kasino-game. It may have different values in table and in player's hand.
 */

public class Card {

    private int valueInTable; // Value in table and the number of the card

    private int valueInHand; // Value in hand

    private Suit suit; // Suit of the card

    public Card(int valueInTable, Suit suit) {
        this.valueInTable = valueInTable;
        this.suit = suit;
        // set the special cards's handValues
        if (this.suit.equals(Card.Suit.DIAMONDS) && this.valueInTable == 10) {
            this.valueInHand = 16;
        } else if (this.suit.equals(Card.Suit.SPADES) && this.valueInTable == 2) {
            this.valueInHand = 15;
        } else if ( this.valueInTable == 1 ) {
            this.valueInHand = 14;
        } else {
            this.valueInHand = this.valueInTable;
        }
    }
    // There are only 4 suits in traditional Deck so let's set an enumeration for them
    public enum Suit { SPADES, HEARTS, DIAMONDS, CLUBS }

    /*
     * Setters & getters
     */
    public int getValueInHand() { return valueInHand; }

    public int getValueInTable() { return valueInTable; }

    public Suit getSuit() { return suit; }

    public void setValueInHand(int value) { valueInHand = value; }

    public String toString() {
        return "" + this.suit + " " + this.valueInTable;
    }

}
