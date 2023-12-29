package kasino;

import java.util.ArrayList;

/*
 * Represents a Player in a Kasino-Game, can be either Computer- or HumanPlayer. Player has a name, cards in his hand and can have points if he earns them
 */

public abstract class Player {

    private ArrayList<Card> hand; // cards currently in players hand

    private Deck deck; // deck which has cards player has acquired

    private String name;

    private int cabins; // number of cabins player has

    private int points; // number of points player has

    public Player (String name) {
        this.deck = new Deck(); // empty deck until player acquires cards
        this.name = name;
        this.hand = new ArrayList<Card>();
    }


    /*
     * You can get cabins in a game of casino, they give one point
     */
    public void addCabin() {
        this.cabins++;
    }
    /*
     * Increases players points by the amount given
     */
    public void increasePoints(int amount) {
        points = points + amount;
    }
    /*
     * With this you can draw one card from your hand
     */
    public Card drawCardFromHand(int index) {
        if (hand.get(index) != null) {
            return hand.get(index);
        } else {
            return null;
        }

    }

    /*
     * Helper for Game's method roundOver, checks if players hand is empty
     */
    public boolean isHandEmpty() {
        return this.hand.isEmpty();
    }

    /*
     * Getters / setters
     */
    
    public String toString() { return this.name + "   " + this.points; }
            
    public String getName() { return name; }

    public void setDeck(Deck deck) {  this.deck = deck; }

    public Deck getDeck() {return deck; }

    public int getPoints() { return points; }
        
    public int getCabins() {  return cabins; }
    
    public void setHand(ArrayList<Card> hand) { this.hand = hand; }
    
    public void setPoints(int points) { this.points = points; }
    
    public void setCabins(int cabins) { this.cabins = cabins; }
    
    public ArrayList<Card> getHand() { return this.hand; }
    
    public void resetCabins() { cabins = 0; }

    public void addHand(Card card) { hand.add(card); }
    
    public void removeHand(Card card) { hand.remove(card); }

    public void removeHand(int card) { hand.remove(card); }
        
}
