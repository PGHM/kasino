package kasino;

import java.util.ArrayList;

/*
 * Computer controlled player, AI determines the moves that this player will make
 */
public class ComputerPlayer extends Player {

    private Game game; // to be able to use game's methods

    public ComputerPlayer(String name) {
        super(name);
        game = new Game(new ArrayList<Player>());
    }

    /*
     * This is one of the main units of computerplayers AI, it lists all the possibilities that you can take
     * with one card and adds them to the possibilities field on players memory
     *          !!!(this method is not designed to be effective due to lack of time)!!!
     */
    public ArrayList<ArrayList<Card>> countPossibilities(Card takingCard, ArrayList<Card> candidates) {
        // make a new list and check possibilities then return them
        ArrayList<ArrayList<Card>> temp = new ArrayList<ArrayList<Card>>();
        checkPossibilities(takingCard, candidates, temp);
        return temp;
    }
    
    /*
     * Method that does the checking for countPossibilities
     */
    private ArrayList<ArrayList<Card>> checkPossibilities(Card takingCard,
        ArrayList<Card> candidates,ArrayList<ArrayList<Card>> possibilities ) {
        // lets use similar technique as in IsValid, separate this list in to smaller lists and then
        // check for each list if its a possible move with the taking card
        if(game.isValid(candidates, takingCard)) {
            // check the uniqueness of this combination
            String temp = candidates.toString();
            boolean flag = true;
            if (possibilities != null) {
                for(int i = 0; i < possibilities.size(); i++) {
                    if (possibilities.get(i).toString().equals(temp)) {
                        flag = false;
                        break;
                    }
                }
                if(flag) {
                    // if it is unique, add it to the possibilities
                    possibilities.add(candidates);
                }
            }
        }
        
        // stop the recursion when the groups are smaller then two
        if(candidates.size() >= 2) {
            for (int i = 0; i < candidates.size(); i++) {
                // divide the list in to smaller ones and check them recursively
                ArrayList<Card> cloneList = game.cloneList(candidates);
                cloneList.remove(i);
                checkPossibilities(takingCard, cloneList, possibilities);
            }
        }
        return possibilities;
    }
    
    /*
     * This is the main method which selects which possibility is chosen to be used by the AI for one takingCard, the bext takingCard will be determined in the GUI
     */

    public ValueWeight bestCards(ArrayList<ArrayList<Card>> possibilities, Card takingCard,  int cardsInTable) {
        // if there are no possibilities, there are no best cards
        if (possibilities.isEmpty()) {
            return null;
        }
        // list to hold all the values
        ArrayList<ValueWeight> valueList = new ArrayList<ValueWeight>();
        // temporary values for the Value with most spades, cards and value
        ArrayList<ValueWeight> mostSpades = new ArrayList<ValueWeight>(); // because there can be many values that have same amount
        ArrayList<ValueWeight> mostCards = new ArrayList<ValueWeight>(); // same here
        ValueWeight mostValue = null;
        int biggestValue = 0;
        // make values for all of the possibilities
        for (int i = 0; i < possibilities.size(); i++) {
            ValueWeight temp = countCardValues(possibilities.get(i), takingCard);
            // check if this possibility will make a cabin if it's taken
            if (possibilities.get(i).size() == cardsInTable) {
                temp.increaseValue(4);
            }
            // if the move will leave only one card to the table it has a negative effect because the next player is likely to get a cabin
            if (cardsInTable - (possibilities.get(i).size()) == 1) {
                temp.decreaseValue(-2);
            }
            // check if this one has most cards or spades, first if the list is new (empty)
            if (mostSpades.isEmpty()) {
                mostSpades.add(temp);
            } else if (temp.getSpades() == mostSpades.get(0).getSpades()) {
                // if the spadeAmount is the same, just add the value to the list
                mostSpades.add(temp);
            } else if(temp.getSpades() > mostSpades.get(0).getSpades()) {
                // if its more, new value "takes the list" on his own
                mostSpades.clear();
                mostSpades.add(temp);
            }
            // and now for cards
            if ( mostCards.isEmpty()) {
                mostCards.add(temp);
            } else if (temp.getCardAmount() == mostCards.get(0).getCardAmount()) {
                // if the cardAmount is the same, just add the value to the list
                mostCards.add(temp);
            } else if(temp.getCardAmount() > mostCards.get(0).getCardAmount()){
                // if its more, new value "takes the list" on his own
                mostCards.clear();
                mostCards.add(temp);
            }
            valueList.add(temp);
        }
        // increase the value of these winners
        for (int x = 0; x < mostSpades.size(); x++) {
            mostSpades.get(x).increaseValue(2);
        }
        for (int z = 0; z < mostCards.size(); z++) {
            mostCards.get(z).increaseValue(1);
        }
        // determine which is the maximum value
        for (int j = 0; j < valueList.size(); j++) {
            if(valueList.get(j).getValue() > biggestValue) {
                biggestValue = valueList.get(j).getValue();
            }
        }
        // when we know this, we will check all the possibilities with this value, and check which of them has the most spades, if none, then cards
        int spades = 0;
        for(int x = 0; x < valueList.size(); x++) {
            ValueWeight temp = valueList.get(x);
            if(temp.getValue() == biggestValue) {
                if (temp.getSpades() > spades) {
                    mostValue = temp;
                }
            }
        }
        // if there was no spades, lets count the cards
        if (mostValue == null) {
            int cards = 0;
            for (int z = 0; z < valueList.size(); z++) {
                ValueWeight temp = valueList.get(z);
                if(temp.getValue() == biggestValue) {
                    if (temp.getCardAmount() > cards) {
                        mostValue = temp;
                    }
                }
            }
        }
        return mostValue;
    }

    /*
     * Helper method which counts the value of a single combination of cards
     * The valueweights are as follows:
     * 1. If the combination holds 10 of Diamonds, add 8 weight
     * 2. if the combination holds 2 of spades or an Ace, or it will result a cabin(this is done in the main method), add 4 weight
     * 3. if the combination holds most spades add 2 weight (this is done in the main method)
     * 4. if the combination holds most cards add 1 weight (this is done in the main method)
     * 5. if the combination will leave only one card to the table, decrease 2 weight (this is done in the main method)
     * 			!These weights are purely the calculation from the own experience of the author and the points that these cards give! 
     * 			(its not higher mathematic or anything)
     */
    
    public ValueWeight countCardValues (ArrayList<Card> possibility, Card takingCard) {
    	// add the takingcard to the possibility list to make the counting easier
    	possibility.add(takingCard);
    	// make a new ValueWeight for these cards
    	ValueWeight value = new ValueWeight(possibility);
    	// temporary values for the amount of spades and set the amount of cards
    	int spades = 0;
    	value.setCardAmount(possibility.size());
    	// check if the cards have 10 of diamonds, 2 of spades or Aces and count the amount of spades and cards
    	for (int i = 0; i < possibility.size(); i++) {
    		Card temp = possibility.get(i);
    		// check if this card is 10 of diamonds (its the only card which's value in hand is 16)
    		if(temp.getValueInHand() == 16) {
    			value.increaseValue(8);
    		}
    		// then check for aces and 2 of spades
    		if (temp.getValueInHand() == 14 || temp.getValueInHand() == 15) {
    			value.increaseValue(4);
    		}
    		// increase the spade counter
    		if (temp.getSuit().equals(Card.Suit.SPADES)) {
    			spades++;
    		}
    	}
    	// set the amount of spades and revokes the changes
    	value.setSpades(spades);
        possibility.remove(takingCard);
    	return value;
    }

    /*
     *  There are no possibilities to take cards, we have to put card on a table, this method determines the best one
     */
    
    public Card bestCard(ArrayList<Card> yourCards, ArrayList<Card> tableCards) {
    	// if there is an empty table, just put something as long it's not valuable
    	if (tableCards.isEmpty()) {
    		return determineLowestValueCard(yourCards);
    	}
    	// usually there is something on the table, so lets check if we can combine anything from the table with our own cards to get something in the next round
    	Card temp = nextRoundCheck(tableCards,yourCards);
    	if(temp != null) {
    		return temp;
    	} else {
    		// just put something low value in there
    		return determineLowestValueCard(yourCards);
    	}
    	
    }

    /*
     * Helper method for bestCard, determines the card with lowest value from given cards
     */
    
	public Card determineLowestValueCard(ArrayList<Card> yourCards) {
		// search for low value cards
		for (int i = 0; i < yourCards.size(); i++) {
			Card temp = yourCards.get(i);
			int value = yourCards.get(i).getValueInHand();
			if (value < 8 && !temp.getSuit().equals(Card.Suit.SPADES)) {
				return temp;
			}
		}
		// if this doesnt work lets widen the search
		for (int j = 0; j < yourCards.size(); j++) {
			Card temp = yourCards.get(j);
			int value = yourCards.get(j).getValueInHand();
			if (value < 14) {
				return temp;
			}
		}
		// if there is no unvaluable cards, put the first card in
		return yourCards.get(0);
	}

    /*
     * Helper method for bestCard, checks if there is anything in the table that we can combine with our own cards to get something in the next round
     */
    
    public Card nextRoundCheck (ArrayList<Card> tableCards, ArrayList<Card> handCards) {
    	// lets check for every own card if they have possibilities with the other own cards
    	for (int i = 0; i < handCards.size(); i++) {
    		Card temp = handCards.get(i);
    		// lets split the card that we want to examine and add it to the tablecards, simulating the next move
    		tableCards.add(handCards.remove(i));
    		// then check if the sums with this card and all the table cards will do any good for us in the next round
    		for (int j = 0; j < handCards.size(); j++) {
    			ArrayList<ArrayList<Card>> check = countPossibilities(handCards.get(j), tableCards);
    			// if there is anything that will match, put the card on the table
    			if (!check.isEmpty()) {
                    // reverse changes and return
                    handCards.add(i,tableCards.remove(tableCards.size() -1));
                    return temp;
    			}
    		}
    		// if there ain't anything, reverse the changes
    		handCards.add(i,tableCards.remove(tableCards.size() -1));
    	}
    	// if there is no combinations at all return null
    	return null;
    }
}
