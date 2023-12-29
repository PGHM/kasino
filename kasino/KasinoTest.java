package kasino;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

/*
 *  A test class for Kasino-game
 */

public class KasinoTest {

    private Deck deck = new Deck();

    @Test
    // Testing if creating a single deck, and then multiple decks, works
    public void testDeckCreation() {    
        int counter = 0;
        this.deck = new Deck(3);
        while(!this.deck.isEmpty()) {
            System.out.println(deck.peekCard().getSuit() + "\n" + deck.getCard().getValueInTable());
            counter++;
        }
        assertEquals(52, counter);
        counter = 0;
        this.deck = new Deck(13);
        while(!this.deck.isEmpty()) {
            System.out.println(deck.peekCard().getSuit() + "\n" + deck.getCard().getValueInTable());
            counter++;
        }
        // if the right amount of cards were created, printing for sufflecheck
        assertEquals(208, counter);
    }


    @Test
    public void testDate() {
        System.out.println(deck.getDateTime());
        // Just testing if this really works
    }

    @Test
    public void testPoints() {
        // make few players and add decks to them (one half full and one with
        // two full decks, thw winner should be clear
        ArrayList<Player> list = new ArrayList<Player>();
        list.add(new HumanPlayer("Jussi"));
        list.add(new HumanPlayer("Jaakko"));
        Deck test = new Deck(1);
        for(int i = 0; i < 26; i++) {test.getCard();}
        Game testGame = new Game(list);
        testGame.getPlayers().get(0).setDeck(test);
        testGame.getPlayers().get(1).setDeck(new Deck(5));
        assertEquals(list.get(1),testGame.countRoundPoints(list));
    }

    @Test
    public void testValidation() {
        // testcards
        Game test = new Game(new ArrayList<Player>());
        ArrayList<Card> test1 = new ArrayList<Card>();
        test1.add(new Card(10,Card.Suit.DIAMONDS));
        test1.add(new Card(3,Card.Suit.CLUBS));

        ArrayList<Card> test2 = new ArrayList<Card>();
        test2.add(new Card(1,Card.Suit.DIAMONDS));
        test2.add(new Card(13,Card.Suit.CLUBS));

        ArrayList<Card> test3 = new ArrayList<Card>();
        test3.add(new Card(3,Card.Suit.CLUBS));
        test3.add(new Card(13,Card.Suit.DIAMONDS));
        test3.add(new Card(10,Card.Suit.DIAMONDS));
        test3.add(new Card(7,Card.Suit.DIAMONDS));
        test3.add(new Card(6,Card.Suit.HEARTS));

        ArrayList<Card> test4 = new ArrayList<Card>();
        test4.add(new Card(10,Card.Suit.DIAMONDS));
        test4.add(new Card(3,Card.Suit.CLUBS));
        test4.add(new Card(3,Card.Suit.HEARTS));
        test4.add(new Card(7,Card.Suit.DIAMONDS));
        test4.add(new Card(6,Card.Suit.HEARTS));
        test4.add(new Card(3,Card.Suit.CLUBS));

        ArrayList<Card> test5 = new ArrayList<Card>();
        test5.add(new Card(3,Card.Suit.CLUBS));
        test5.add(new Card(13,Card.Suit.DIAMONDS));
        test5.add(new Card(10,Card.Suit.DIAMONDS));
        test5.add(new Card(7,Card.Suit.DIAMONDS));
        test5.add(new Card(6,Card.Suit.HEARTS));

        Card kortti = new Card(13,Card.Suit.SPADES);
        Card kortti2 = new Card(1,Card.Suit.HEARTS);
        Card kortti3 = new Card(10,Card.Suit.DIAMONDS);
        //tests for validation
        assertTrue(test.isValid(test1, kortti));
        assertFalse(test.isValid(test1, kortti2));
        assertTrue(test.isValid(test2, kortti2));
        assertTrue(test.isValid(test3, kortti));
        assertTrue(test.isValid(test4, kortti3));
        assertFalse(test.isValid(test5, kortti3));

        // It is working!

    }

    @Test
    public void testGameEngine() {
        // There is plenty of random tests scattered around to ensure the working of the turns
        // Players
        Player jussi = new HumanPlayer("Jussi");
        Player matti = new HumanPlayer("Matti");
        Player pekka = new HumanPlayer("Pekka");
        // add players to list
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(jussi);
        players.add(matti);
        players.add(pekka);
        // cards for this game
        Card card1 = new Card(1,Card.Suit.HEARTS);
        Card card2 = new Card(2,Card.Suit.HEARTS);
        Card card3 = new Card(7,Card.Suit.DIAMONDS);
        Card card4 = new Card(8,Card.Suit.DIAMONDS);
        Card card6 = new Card(12,Card.Suit.DIAMONDS);
        Card card7 = new Card(11,Card.Suit.DIAMONDS);
        Card card8 = new Card(10,Card.Suit.DIAMONDS);
        Card card9 = new Card(2,Card.Suit.SPADES);
        Card card10 = new Card(3,Card.Suit.SPADES);
        Card card11 = new Card(4,Card.Suit.SPADES);

        // new game with players
        Game game = new Game(players);
        game.formatRound();
        // because the deck in the game is random, lets manipulate the setup a bit
        game.clearHands();
        // set cards for player
        game.getPlayers().get(0).addHand(card1);
        game.getPlayers().get(0).addHand(card3);
        game.getPlayers().get(0).addHand(card4);
        game.getPlayers().get(0).addHand(card9);

        game.getPlayers().get(1).addHand(card7);
        game.getPlayers().get(1).addHand(card6);
        game.getPlayers().get(1).addHand(card2);
        game.getPlayers().get(1).addHand(card1);

        game.getPlayers().get(2).addHand(card11);
        game.getPlayers().get(2).addHand(card8);
        game.getPlayers().get(2).addHand(card7);
        game.getPlayers().get(2).addHand(card4);
        // set cards to table
        ArrayList<Card> table = new ArrayList<Card>();
        table.add(card10);
        table.add(card3);
        table.add(card4);
        table.add(card2);
        game.setTableCards(table);
        // some tests to verify these changes have made the game like we wanted to
        assertEquals(4,game.getTableCards().size());
        assertEquals(3,game.getPlayers().size());
        assertEquals(4,game.getPlayers().get(1).getHand().size());
        // lets play, we have to make list of cards that are being played every turn, GUI will make this
        // when we play with it and not with this test, kinda simulation of what GUI will do
        // (move turn after every round)
        // first turn
        ArrayList<Card> turn1 = new ArrayList<Card>(); // make list for the cards to be taken
        turn1.add(card10); // add cards to the list
        turn1.add(card2);
        turn1.add(card3);
        game.takeCards(turn1, card6); // take cards
        game.moveTurn(); // move turn
        assertEquals(4,game.getPlayers().get(1).getDeck().countCards());
        assertEquals(2,game.getTurn());
        // second turn, same procedures here
        game.putCardTable(card11);
        game.moveTurn();
        assertEquals(0,game.getTurn());
        // third turn
        game.putCardTable(card3);
        game.moveTurn();
        // fourth turn
        game.putCardTable(card7);
        game.moveTurn();
        assertEquals(4,game.getTableCards().size());
        // fifth turn
        ArrayList<Card> turn5 = new ArrayList<Card>();
        turn5.add(card7);
        turn5.add(card11);
        turn5.add(card3);
        game.takeCards(turn5, card7);
        game.moveTurn();
        // sixth turn
        ArrayList<Card> turn6 = new ArrayList<Card>();
        turn6.add(card4);
        game.takeCards(turn6, card4);
        game.moveTurn();
        assertEquals(1,game.getPlayers().get(0).getCabins());
        assertEquals(0,game.getTableCards().size());
        // These work fine, so lets clear the deck and players hands, artificially end the game
        game.clearHands();
        game.setDeck(new Deck());
        players.get(1).addHand(card1);
        game.putCardTable(card1);
        // the round should end
        if (game.moveTurn()) {
            game.endRound();
            game.formatRound();
        }
        // these values are true if the round formatted correctly
        assertEquals(2,game.getRound());
        assertEquals(1,game.getDealer());
        assertEquals(4,game.getPlayers().get(0).getHand().size());
        assertTrue(game.getPlayers().get(1).getDeck().isEmpty());
        assertEquals(2,game.getPlayers().get(0).getPoints());
        assertEquals("Matti",game.getPlayers().get(1).getName());
        assertEquals(0,game.getLastTake());
        // here we test if the saving and loading works correctly, meaning that the information after saving once and loading once must be the same
        // if the saving or loading fails there will be an error due to exceptionhandlings assertTrue(false);
        try {
            SaveLoad.saveGame(game, "test.txt");
            game = SaveLoad.loadGame("test.txt");
        } catch (IllegalArgumentException e) {
            // there was an error so game wasn't saved correctly
            assertTrue(false);
        } catch (FileNotFoundException f) {
            // file was not found
            assertTrue(false);
        } catch (IOException e) {
            // problems with IO
            assertTrue(false);
        } catch (CorruptedSaveFileException e) {
            // file corrupted
            assertTrue(false);
        }
        // same tests here to assure the information was read correctly 
        assertEquals(2,game.getRound());
        assertEquals(1,game.getDealer());
        assertEquals(4,game.getPlayers().get(0).getHand().size());
        assertTrue(game.getPlayers().get(1).getDeck().isEmpty());
        assertEquals(2,game.getPlayers().get(0).getPoints());
        assertEquals("Matti",game.getPlayers().get(1).getName());
        assertEquals(0,game.getLastTake());
    }

    @Test
    public void testLoadExceptions() {
        // Make a game to save
        Player jussi = new HumanPlayer("Jussi");
        Player matti = new HumanPlayer("Matti");
        Player pekka = new HumanPlayer("Pekka");
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(jussi);
        players.add(matti);
        players.add(pekka);
        Game game = new Game(players);
        game.formatRound();
        // Save the game
        int error = 0;
        try {
            game = SaveLoad.loadGame("test5.txt");
        } catch (CorruptedSaveFileException e) {
            // file corrupted
            error = 1;
        } catch (FileNotFoundException f) {
            // file was not found
            error = 2;
        } catch (IOException e) {
            // problems with IO
            error = 3;
        }
        // this should be true because the file we are trying to get does not exist
        assertEquals(2, error);
        try {
            game = SaveLoad.loadGame("test2.txt");
        } catch (CorruptedSaveFileException e) {
            // file corrupted
            error = 1;
        } catch (FileNotFoundException f) {
            // file was not found
            error = 2;
        } catch (IOException e) {
            // problems with IO
            error = 3;
        }
//      this should be true because the file we are trying to get is tempered with
        assertEquals(1, error);
    }

    @Test
    public void testFormatRound() {
        // new Game
        Player jussi = new HumanPlayer("Jussi");
        Player matti = new HumanPlayer("Matti");
        Player pekka = new HumanPlayer("Pekka");
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(jussi);
        players.add(matti);
        players.add(pekka);
        Game game = new Game(players);
        game.formatRound();
        // everyone got 4 cards and the table has 4 cards
        assertEquals(4,game.getPlayers().get(0).getHand().size());
        assertEquals(4,game.getPlayers().get(1).getHand().size());
        assertEquals(4,game.getPlayers().get(2).getHand().size());
        assertEquals(4,game.getTableCards().size());
        // round is 1, dealer is 0, turn is 1
        assertEquals(1,game.getRound());
        assertEquals(0,game.getDealer());
        assertEquals(1,game.getTurn());
        // player decks are empty
        assertTrue(game.getPlayers().get(0).getDeck().isEmpty());
    }
    
    @Test
    public void testAI() {
        ComputerPlayer computer = new ComputerPlayer("comp");
        
        // testcards
        
        ArrayList<Card> test2 = new ArrayList<Card>();
        test2.add(new Card(1,Card.Suit.DIAMONDS));
        test2.add(new Card(13,Card.Suit.CLUBS));

        ArrayList<Card> test5 = new ArrayList<Card>();
        test5.add(new Card(3,Card.Suit.CLUBS));
        test5.add(new Card(13,Card.Suit.DIAMONDS));
        test5.add(new Card(10,Card.Suit.DIAMONDS));
        test5.add(new Card(7,Card.Suit.DIAMONDS));
        test5.add(new Card(6,Card.Suit.HEARTS));
        
        ArrayList<Card> test4 = new ArrayList<Card>();
        test4.add(new Card(3,Card.Suit.CLUBS));
        test4.add(new Card(13,Card.Suit.DIAMONDS));
        test4.add(new Card(10,Card.Suit.DIAMONDS));
        test4.add(new Card(7,Card.Suit.DIAMONDS));
        test4.add(new Card(6,Card.Suit.HEARTS));
        test4.add(new Card(6, Card.Suit.SPADES));
        test4.add(new Card(2, Card.Suit.DIAMONDS));
        
        ArrayList<Card> test1 = new ArrayList<Card>();
        test1.add(new Card(3,Card.Suit.SPADES));
        test1.add(new Card(13,Card.Suit.SPADES));
        test1.add(new Card(10,Card.Suit.DIAMONDS));
        test1.add(new Card(7,Card.Suit.DIAMONDS));
        test1.add(new Card(1,Card.Suit.HEARTS));
        test1.add(new Card(6, Card.Suit.SPADES));
        test1.add(new Card(2, Card.Suit.DIAMONDS));
        test1.add(new Card(10, Card.Suit.SPADES));

        ArrayList<Card> test6 = new ArrayList<Card>();
        test6.add(new Card(3,Card.Suit.CLUBS));
        test6.add(new Card(13,Card.Suit.DIAMONDS));
        test6.add(new Card(10,Card.Suit.DIAMONDS));
        test6.add(new Card(7,Card.Suit.DIAMONDS));
        test6.add(new Card(6,Card.Suit.HEARTS));
        test6.add(new Card(6, Card.Suit.SPADES));
        
        ArrayList<Card> test7 = new ArrayList<Card>();
        test7.add(new Card(12,Card.Suit.CLUBS));
        test7.add(new Card(11,Card.Suit.DIAMONDS));
        test7.add(new Card(1,Card.Suit.DIAMONDS));
        test7.add(new Card(5,Card.Suit.DIAMONDS));
        
        ArrayList<Card> test8 = new ArrayList<Card>();
        test8.add(new Card(9,Card.Suit.SPADES));
        test8.add(new Card(8,Card.Suit.DIAMONDS));
        test8.add(new Card(2,Card.Suit.DIAMONDS));
        
        Card kortti = new Card(13,Card.Suit.SPADES);
        Card kortti2 = new Card(1,Card.Suit.HEARTS);
        Card kortti3 = new Card(9, Card.Suit.HEARTS);
        
        
        
        // first test, should be only one possibility with this easy set of cards
        ArrayList<ArrayList<Card>> possibilities2 = new ArrayList<ArrayList<Card>>();
        
        possibilities2 = computer.countPossibilities(kortti2, test2);
        
        assertEquals(1, possibilities2.size());
        possibilities2.clear();
        
        // some more cards
        
        possibilities2 = computer.countPossibilities(kortti, test5);
        
        assertEquals(7, possibilities2.size());
        possibilities2.clear();
        
        // and some more, the suit doesn't count here because it counts on the points (spades)
        
        possibilities2 = computer.countPossibilities(kortti, test4);
        
        assertEquals(11, possibilities2.size());
        possibilities2.clear();
        
        // with not so good card for this
        
        possibilities2 = computer.countPossibilities(kortti3, test4);
        
        assertEquals(5, possibilities2.size());
        possibilities2.clear();
        
        // now test the other methods of AI
        // try to count the value of these cards
        ValueWeight test = computer.countCardValues(test1, kortti);
        
        assertEquals(5, test.getSpades());
        assertEquals(9, test.getCardAmount());
        assertEquals(12, test.getValue());
        
        // See if the method determines the best cards right
        
        possibilities2 = computer.countPossibilities(kortti, test6);
        ArrayList<Card> bestCards = computer.bestCards(possibilities2, kortti, 6).getCards();
        
        // this should be the best combination
        test6.remove(4);
        assertEquals(test6, bestCards);
        bestCards.clear();
        possibilities2.clear();
        // test with other cards
        possibilities2 = computer.countPossibilities(kortti2, test1);
        bestCards = computer.bestCards(possibilities2, kortti2, 8).getCards();
        
        assertEquals("[SPADES 3, DIAMONDS 10, HEARTS 1]", bestCards.toString());
        
        // okay now with the single card drawn to table
        
        Card bestCard = computer.determineLowestValueCard(test6);
        
        assertEquals(3, bestCard.getValueInHand());
        
        bestCard = computer.determineLowestValueCard(test2);
        
        assertEquals(13, bestCard.getValueInHand());
        
        // little bit harder with only good cards
        
        test2.remove(1);
        test2.add(kortti2);
        
        bestCard = computer.determineLowestValueCard(test2);
        
        assertEquals(Card.Suit.DIAMONDS, bestCard.getSuit());
        
        // and now if the cards have any possibilities in next round
        
        Card nextCard = computer.nextRoundCheck(test5, test7);
        
        assertEquals(11, nextCard.getValueInHand());
        
        nextCard = computer.nextRoundCheck(test7, test8);
        
        assertEquals(8, nextCard.getValueInHand());
        
    }
    
}
