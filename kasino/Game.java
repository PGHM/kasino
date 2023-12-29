package kasino;

import java.util.ArrayList;


/*
 * The actual game engine, all the things that are done during a turn are handled here, and Game also communicates with the GUI.
 */

public class Game {

    private ArrayList<Player> players; // all the players in the game

    private ArrayList<Card> tableCards; // cards which are on the table

    private Deck deck; // the gaming deck

    private int dealer; // the index of the dealer

    private int turn; // index who's turn it is

    private int round; // number of the round

    private boolean flag; // helper for isValid
    
    private int lastTake; // keeps track of Player which got cards the last, this player gets all the
    // cards in the table when the game ends

    
    /*
     * Makes a new game
     */
    public Game(ArrayList<Player> players) {
        this.players = players;
        this.deck = null; // game.formatRound() will format this
        this.dealer = -1;
        this.tableCards = new ArrayList<Card>();
        this.turn = 1;
        this.round = 0;
        this.flag = false;
        this.lastTake = 0;
    }
    
    /*
     * Formats the round, deals card for next round
     */

    public void formatRound() {        
        // suffles deck (makes new)
        deck = new Deck(players.size());
        // deals cards
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < players.size(); j++) {
                players.get(j).addHand(deck.getCard());
            }
            tableCards.add(deck.getCard());
        }
        // updates dealer, roundnumber, turn and reset lastTake
        round++;
        if(dealer < players.size() - 1) {
            dealer++;
        } else {
            dealer = 0;
        }
        if (dealer != players.size() - 1) {
            turn = dealer + 1;
        } else {
            turn = 0;
        }

        this.lastTake = 0;
        // empty players decks
        for(int i = 0; i < players.size();i++) {
            players.get(i).setDeck(new Deck());
        }
    }

    /*
     * Checks if the move is legal eg. checks if the value of the card which player has chosen matches
     * to those cards he is going to acquire
     */
    public boolean isValid(ArrayList<Card> candidates, Card takingCard) {
        int value = 0;
        // calculating the value of the cards which are candidates to take away from the table
        for(int i = 0; i < candidates.size(); i++) {
            value += candidates.get(i).getValueInTable();
        }
        if (value < takingCard.getValueInHand()) {return false; }
        if (value == takingCard.getValueInHand()) { return true; }
        else {
            // this part is for cases player tries to take multiple combos of cards at the same time, will use recursive
            // algorithm to check different combos of cards from the given list
            boolean temp = comboCheck(candidates, takingCard, cloneList(candidates));
            // change the flag back for next calls
            this.flag = false;
            return temp;
        }


    }
    /*
     * helper Method for isValid which will determine if multiple combos of cards can be taken
     */
    public boolean comboCheck(ArrayList<Card> candidates, Card takingCard, ArrayList<Card> original) {
        // flag if the move was legal
        boolean finalStatement = false;
        // first check are these cards legimate
        if (valueMatch(candidates, takingCard)) {
            // If they are remove them from the original list
            for (Card current : candidates) {
                original.remove(current);
            }
            // if the original list is empty all the cards are taken and the whole move is legimitive
            // if its not empty, call the method again with the cards that are left to check if the
            // rest of the cards can be removed too with same logic
            if (original.isEmpty()) {
                finalStatement = true;
                //flag on so unneseccary calls are not made from previous method calls anymore
                this.flag = true;
            } else {
                finalStatement = comboCheck(original, takingCard, cloneList(original));
                this.flag = true;
            }
        } else {
            // if this group of cards cannot be removed, split the cards in to smaller groups until they are in groups of 2 or 1
            if (candidates.size() >= 2) {
                // makes n-1 new sublists with n-1 cards
                for (int i = 0; i < candidates.size(); i++) {
                    // Makes a new sublist with all the cards except for one
                    ArrayList<Card> subList = cloneList(candidates);
                    subList.remove(i);
                    // if the whole move was legal, or the call was altered (flag is on) we can break from the method
                    if (comboCheck(subList, takingCard,original)) {
                        finalStatement = true;
                        break;
                    }
                    if (flag) {
                        break;
                    }
                }
            }
        }
        return finalStatement;
    }
    /*
     * Helpermethod for comboCheck which determines if these cards can be taken without combos
     */
    public boolean valueMatch(ArrayList<Card> candidates, Card takingCard) {
        int value = 0;
        // calculating the value of the cards which are candidates to take away from the table
        for(int i = 0; i < candidates.size(); i++) {
            value += candidates.get(i).getValueInTable();
        }
        if (value == takingCard.getValueInHand()) {
            return true;
        } else {
            return false;
        }
    }
    /*
     * Helper method for comboCheck that clones Arraylists
     */
    public ArrayList<Card> cloneList(ArrayList<Card> list) {
        ArrayList<Card> newList = new ArrayList<Card>();
        for (int i = 0; i < list.size(); i++) {
            newList.add(list.get(i));
        }
        return newList;
    }
    /*
     * Who's the winner of this game!
     */
    public Player countRoundPoints(ArrayList<Player> players) {

        Player winner = null;   // if some has crosses 16 cards this is not null
        Player mostCards = null; // Player with most cards
        Player mostSpades = null;// Player with most spades at the end of the round
        int highestSpadeCount = 0;
        int highestCardCount = 0;
        // counts the amount of spades of the each player and determines who has most
        for (Player c : players) {
            int spades = c.getDeck().countSpades();
            if (spades > highestSpadeCount) {
                mostSpades = c;
                highestSpadeCount = spades;
            }
        }
        mostSpades.increasePoints(2);
        // same for cards
        for (Player c2 : players) {
            int cards = c2.getDeck().countCards();
            if (cards > highestCardCount) {
                mostCards = c2;
                highestCardCount = cards;
            }
        }
        mostCards.increasePoints(1);
        for (Player current : players) {
            // increase the players points
            current.increasePoints(current.getDeck().countPoints());
            current.increasePoints(current.getCabins());
            current.resetCabins();
            // checks if this player has crossed 16 points
            if (current.getPoints() >= 16) {
                // in case we have many players crossing the line of 16 in the same round
                if (winner == null || current.getPoints() > winner.getPoints()) {
                    winner = current;
                }
            }

        }
        return winner;
    }

    /*
     * Ends the round and makes the necessary things for the next round
     */

    public boolean endRound() {
        // gives the last player who got cards the rest of the cards from the table
        players.get(lastTake).getDeck().addCards(tableCards);
        tableCards.clear();
        // first counts points and checks if someone has won
        Player winner = countRoundPoints(players);
        if (winner != null) {
            // if there is winner, the game will be over
            return true;
        }
        // if nobody wins new round can start
        return false;
    }

    /*
     *Tries to take selected cards from the table 
     */
    public boolean takeCards(ArrayList<Card> candidates, Card takingCard) {
        if (isValid(candidates, takingCard)) {
            // adds cards to the players deck and draws new card from hand
            players.get(turn).getDeck().addCards(candidates);
            players.get(turn).getDeck().addCard(takingCard);
            players.get(turn).removeHand(takingCard);
            // gives player a new card if the dec ain't empty
            if (!deck.isEmpty()) {
                players.get(turn).addHand(deck.getCard());
            }
            // removes the taken cards from table
            for (int i = 0; i < candidates.size(); i++) {
                Card removable = candidates.get(i);
                tableCards.remove(removable);
            }
            // checks if a cabin was made
            if (tableCards.isEmpty()) {
                players.get(turn).addCabin();
            }
            // updates the last player which got cards
            lastTake = turn;
            return true;
        } else {
            return false;
        }
    }
    /*
     * Puts one card to the table
     */
    public void putCardTable(Card card) {
        tableCards.add(card);
        players.get(turn).removeHand(card);
        // gives player a new card if the dec ain't empty
        if (!deck.isEmpty()) {
            players.get(turn).addHand(deck.getCard());
        }
    }
    /*
     * Changes the turn to the next player
     */
    public boolean moveTurn() {
        // if the round ain't over yet, change turn
    	if (!roundOver()) {
            if (turn < players.size() - 1) {
                turn++;
            } else {
                turn = 0;
            } 
            return false;
        } else {
            // if there are no more cards, returns true, which means GUI will end the round
            return true;
        }
    }

    /*
     * Determines if the round is over, checking if the players hands are empty
     */

    public boolean roundOver() {
        boolean isOver = true;
        // if even a single hand has cards the game can still go on
        for (Player current : players) {
            if(!current.isHandEmpty()) {
                isOver = false;
                break;
            }
        }
        return isOver;
    }

    // FOR SAVE/LOAD and TESTING PURPOSES (MIXED)

    public Player getLeader() {
        // determine the leader by checking on every loop if this player has more then the previous ones
        Player leader = null;
        for (int i = 0; i < players.size();i++) {
            Player temp = players.get(i);
            // for the first loop to avoid nullpointerexception
            if ( leader == null) {
                leader = temp;
                continue;
            }
            if (leader.getPoints() < temp.getPoints()) {
                leader = temp;
            }
        }
        return leader;
    }
    
    public int getRound() { return this.round; }

    public int getTurn() { return this.turn; }

    public int getDealer() {  return this.dealer; }

    public int getLastTake() { return this.lastTake; }

    public void setPlayers(ArrayList<Player> players) { this.players = players; }

    public void setLastTake(int lastTake) { this.lastTake = lastTake; }

    public void setDealer(int dealer) {  this.dealer = dealer; }

    public void setRound(int round) {  this.round = round; }

    public void setTurn(int turn) {  this.turn = turn; }

    public void setDeck(Deck deck) { this.deck = deck; }
    
    public void setTableCards(ArrayList<Card> cards) { this.tableCards = cards; }

    public ArrayList<Player> getPlayers() { return players; }

    public ArrayList<Card> getTableCards() {  return tableCards; }

    public Deck getDeck() { return deck; }
    
    // clears the hands of all players
    
    public void clearHands() {
    	for(int i = 0; i < players.size(); i++) {
    		for(int j = 0; j < 4; j++) {
    			players.get(i).removeHand(0);
    		}
    	}
    	for(int j = 0; j < tableCards.size(); j++) {
    		tableCards.remove(0);
    	}
    }
    
}
