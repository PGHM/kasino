package kasino;

import java.util.Random;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/*
 * A deck in a Kasino-game, can hold infinite amount of cards, directly related to the amount of players at the start of the game
 */
public class Deck {

    private Stack<Card> deck; // The actual deck
    
    public Deck (int players) {
        this.deck = new Stack<Card>();

        int i = (players / 4) + 1; // if more than 4 players play the deck must have "more decks" in order
        int j = 0;                         // for the game to be enjoyable
        do {
            ArrayList<Card> temp = new ArrayList<Card>(); // to help suffle the deck
            // Fill the deck with cards from all of the four suits
            setSuit(temp, Card.Suit.CLUBS);
            setSuit(temp, Card.Suit.DIAMONDS);
            setSuit(temp, Card.Suit.SPADES);
            setSuit(temp, Card.Suit.HEARTS);

            Random randomizer = new Random(Integer.parseInt(getDateTime()));
            for(int k=52; k > 0; k--) {
                // Randomly select a index to get the card from the temp list, simulate suffling
                // the deck. After this the card is removed
                int index = randomizer.nextInt(k);
                deck.push(temp.remove(index));
            }
            i--;
        } while (j < i);
    }

    // Creates and empty deck
    public Deck() {     
        this.deck = new Stack<Card>();   
    }

    /*
     * Counts the points of given deck, uses the rules of a normal Kasino.
     */
    public int countPoints() {
        int score = 0;       
        for(Card current : deck) {
            // pretty ugly implementation but couldn't think of anything else
            // checks if the current card is one of the special, pointgiving ones
            if (current.getValueInTable() == 1) {
                score++;
                continue;
            }
            if (current.getSuit().equals(Card.Suit.SPADES) && current.getValueInTable() == 2) {
                score++;
                continue;
            }
            if (current.getSuit().equals(Card.Suit.DIAMONDS) && current.getValueInTable() == 10) {
                score = score + 2;
                continue;
            }
        }
        return score;
    }
    
    /*
     * Counts the number of spades cards in a deck
     */
    public int countSpades() {
        int counter = 0;
        for (Card current : deck) {
            if (current.getSuit().equals(Card.Suit.SPADES)) {
                counter++;
            }
        }
        return counter;
    }
    /*
     * Counts the number of cards in a deck
     */
    public int countCards() {
        int counter = 0;
        for (Card current : deck) {
            counter++;
        }
        return counter;
    }
    /*
     * Fills the deck with all the 13 cards of a one given suit and returns it
     */
    public ArrayList<Card> setSuit(ArrayList<Card> deck, Card.Suit suit)    {
        for(int i = 1; i < 14; i++)     {
            deck.add(new Card(i,suit));
        }
        return deck;
    }
    /*
     * Returns the current time in format "HoursMinutesSecondsmMilliSeconds"
     * This method is mainly to help provide reliable seed to the sufflealgorithm
     */
    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("kkmmssSS");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    /*
     * Makes a copy of this deck, crucial for saveLoad
     */
    
    public Deck copyDeck() {
    	Deck temp = new Deck();
    	Deck temp2 = new Deck();
    	while (!this.isEmpty()) {
    		temp.addCard(this.getCard());
    	}
    	// return the cards to the original deck and add them to the copied deck
    	while (!temp.isEmpty()) {
    		temp2.addCard(temp.peekCard());
    		this.addCard(temp.getCard());
    	}
    	return temp2;
    }
    
    /*
     * Methods to check out the cards in the deck
     */
    public Card getCard() { return deck.pop(); }

    public boolean isEmpty() { return deck.isEmpty(); }
        
    public Card peekCard() { return deck.peek(); } 
    
    /*
     * Methods to add cards in the deck
     */
    public void addCard(Card card) { deck.push(card); }
        
    public void addCards(ArrayList<Card> cards)  {       
        for(int i = 0; i < cards.size(); i++) {
            deck.push(cards.get(i));
        }
    }
}
