package kasino;

import java.util.ArrayList;

/*
 *  Represents a value of combination of cards, is needed to determine the best combination of cards in ComputerPlayers AI
 */

public class ValueWeight {
	
	private ArrayList<Card> cards; // cards that this ValueWeight represents
	
	private int value; // the value of these cards
	
	private int spades; // amount of spades
	
	private int cardAmount; // amount of cards
	
	public ValueWeight(ArrayList<Card> cards) {
		this.cards = cards;
        this.value = 0;
        this.spades = 0;
        this.cardAmount = 0;
	}
	
	// returns the cards that this ValueWeight represents
	
	public ArrayList<Card> getCards() { return cards; }
	
	// increases the value a given amount
	
	public void increaseValue(int amount) { value += amount; }
	
	// decreases the value a given amount
	
	public void decreaseValue(int amount) { value -= amount; }
	
	// returns the value of this ValueWeight
	
	public int getValue() { return value; }
	
	// returns the amount of spades
	
	public int getSpades() { return spades; }
	
	// returns the amount of cards
	
	public int getCardAmount() { return cardAmount; }

	// few setters
	
	public void setSpades(int amount) { spades = amount; }
	
	public void setCardAmount( int amount) { cardAmount = amount; }
	
}
