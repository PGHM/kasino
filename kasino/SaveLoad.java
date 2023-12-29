package kasino;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/*
 * Kasino-game has a Save/Load feature that allows you to save your game in to a file, and load any
 * of those saved files at any time.
 */
public abstract class SaveLoad {


    /*
     * Constructs a string with the whole data from given Game, every part is separated with "#"
     * The syntax is:
     * 1. Round number
     * 2. Index of the dealer 
     * 3. Index of the turn 
     * 4. Index of the last player who took cards 
     * 5. Cards on the table (lenght 5., lenght of a one card info is 3 eg. H12 or S02)
     * 6. Cards in the deck, bottom first ( lenght 7., syntax as in table)
     * 7. Players until the end of file 
     *      *Syntax: 
     *      *name
     *      *human/computer 
     *      *points 
     *      *cabins
     *      *cards in the hand (same cardsyntax)
     *      *cards in the deck (same cardsyntax)
     */
    public static void saveGame(Game game, String filePath) throws IllegalArgumentException, 
        FileNotFoundException, IOException {
        // storage for data
        StringBuilder data = new StringBuilder();
        // lets gather the main information about the game first
        data.append(game.getRound() + "#");
        data.append(game.getDealer() + "#");
        data.append(game.getTurn() + "#");
        data.append(game.getLastTake() + "#");
        // then list all the cards on the table
        ArrayList<Card> tableCards = game.getTableCards();
        for ( int i = 0; i < tableCards.size(); i++) {
            Card card = tableCards.get(i);
            addCard(data, card);
        }
        data.append("#");
        // Parse the deck, this is the right order (see the load function)
        Deck deck = game.getDeck().copyDeck();
        while(!deck.isEmpty()) {
            addCard(data,deck.getCard());
        }
        // loop to add all the players
        ArrayList<Player> players = game.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            // Basic info
            data.append("#" + player.getName());
            data.append("#" + player.getClass().toString());
            data.append("#" + player.getPoints());
            data.append("#" + player.getCabins() + "#");
            // Cards in the hand
            for(int j = 0; j < player.getHand().size(); j++) {
                addCard(data,player.getHand().get(j));
            }
            data.append("#");
            // Cards in the deck
            Deck playerDeck = player.getDeck().copyDeck();
            while (!playerDeck.isEmpty()) {
                addCard(data,playerDeck.getCard());
            }
        }
        // End marker
        data.append("##");
        String dataString = data.toString();

        File file = new File(filePath);
        file.createNewFile();
        // Idea for this part and the implementation is greatly taken from
        // http://www.javapractices.com/topic/TopicAction.do?Id=42#Example2
        // I don't know if these situations are possible with the JFilechooser, but to be sure
        if (file == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!file.exists()) {
            throw new FileNotFoundException ("File does not exist: " + file);
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("File Should not be a directory: " + file);
        }
        if (!file.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + file);
        }

        //use buffering
        Writer output = new BufferedWriter(new FileWriter(file));
        //FileWriter always assumes default encoding is OK!
        output.write( dataString );
        output.close();
    }
    
    /*
     * Loads a game from a correctly encoded .txt file
     */
    
    public static Game loadGame(String filePath) throws IOException, FileNotFoundException,
        CorruptedSaveFileException {
        
        // new raw game
        Game game = new Game(new ArrayList<Player>());
        
        // make a new reader for the file
        BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
        
        //read the basic infos
        ArrayList<Character> info = SaveLoad.readUntilStop(reader);
        
        // ...round
        game.setRound(SaveLoad.parseCharListToNumber(info));
        info = SaveLoad.readUntilStop(reader);
        
        // ...dealer
        game.setDealer(SaveLoad.parseCharListToNumber(info));
        info = SaveLoad.readUntilStop(reader);
        
        // ...turn
        game.setTurn(SaveLoad.parseCharListToNumber(info));
        info = SaveLoad.readUntilStop(reader);
        
        // ...lastTake
        game.setLastTake(SaveLoad.parseCharListToNumber(info));
        info = SaveLoad.readUntilStop(reader);
        
        // now we have the cards on the table, lets add them    
        game.setTableCards(SaveLoad.parseCards(info));
        info = SaveLoad.readUntilStop(reader);
        
        // same for the deck, adding to the deck from the list one by one
        Deck temp = new Deck();
        ArrayList<Card> parsed = SaveLoad.parseCards(info);
        
        // decreasing to get the right order
        for (int i = parsed.size() - 1; i >= 0; i--) {
            temp.addCard(parsed.get(i));
        }
        
        // then sets the deck to the parsed one
        game.setDeck(temp);
        
        // set players
        info = SaveLoad.readUntilStop(reader);
        while (info.size() != 0) {
            setPlayer(game,reader,info);
            info = SaveLoad.readUntilStop(reader);
        }
        // close the stream
        reader.close();
        return game;
    }
    // helper method for writing the cards to the file
    private static void addCard(StringBuilder data, Card card) {
        Card.Suit suit = card.getSuit();
        int value = card.getValueInTable();
        // sets the suit, just one letter
        if (suit.equals(Card.Suit.CLUBS)) {
            data.append('C');
        } else if (suit.equals(Card.Suit.DIAMONDS)) {
            data.append('D');
        } else if (suit.equals(Card.Suit.HEARTS)) {
            data.append('H');
        } else if (suit.equals(Card.Suit.SPADES)) {
            data.append('S');
        }
        // This is done to match the syntax
        if (value < 10) {
            data.append("0" + value);
        } else {
            data.append(value);
        }
    }
    
    /*
     * Helper methods for loading the games things
     */
    
    public static ArrayList<Character> readUntilStop(BufferedReader reader) throws IOException,
        CorruptedSaveFileException {
        // checker if the file ends before it should, it receives "-1" if it does
        int checker = 0;
        char aChar = (char)reader.read();
        ArrayList<Character> result = new ArrayList<Character>(); 
        // until new boundary (#) is found read the input to an array
        while (aChar != '#') {
            result.add(aChar);
            checker = reader.read();
            // checks if the file was corrupted
            if (checker == -1) {
                throw new CorruptedSaveFileException("Corrupted file");
            } else {
                aChar = (char)checker;
            }
        }
        return result;
    }
    
    /*
     * Helper method for loadGame, parses character list to a number
     */
    
    public static int parseCharListToNumber(ArrayList<Character> list) {
        int number = 0;
        // make the number
        for(int i = 0; i < list.size(); i++) {
            // for every number we must make it the right size with the exponent
            int a = list.get(i) - '0';
            int exponent = (list.size() - (i + 1));
            double b = Math.pow(10,exponent);
            number += a * b;
            
        }
        return number;
    }
    
    // same method  with different kind of list as a parameter
    
    public static int parseCharListToNumber(List<Character> list) {
    	int number = 0;
        // make the number
        for(int i = 0; i < list.size(); i++) {
            // for every number we must make it the right size with the exponent
            int a = list.get(i) - '0';
            int exponent = (list.size() - (i + 1));
            double b = Math.pow(10,exponent);
            number += a * b;
            
        }
        return number;
    }

    public static ArrayList<Card> parseCards(ArrayList<Character> list) 
        throws CorruptedSaveFileException {
        ArrayList<Card> cards = new ArrayList<Card>();
        try {
            for(int i = 0; i < list.size(); i = i + 3) {
                // parse suit
                Card.Suit suit = null;
                if (list.get(i) == 'S') {
                    suit = Card.Suit.SPADES;
                } else if (list.get(i) == 'C') {
                    suit = Card.Suit.CLUBS;
                } else if (list.get(i) == 'H') {
                    suit = Card.Suit.HEARTS;
                } else if (list.get(i) == 'D') {
                    suit = Card.Suit.DIAMONDS;
                }
                // parse value
                List<Character> temp = list.subList(i+1, i+3);
                int value = parseCharListToNumber(temp);
                // make the new Card and add it to the list
                cards.add(new Card(value,suit));
            }
        } catch (Exception e) {
            throw new CorruptedSaveFileException("Corrupted file");
        }
        return cards;
    }
    
    /*
     * Helper method for LoadGame, sets one player from data given
     */
    public static void setPlayer(Game game, BufferedReader reader, ArrayList<Character> info) 
        throws IOException, CorruptedSaveFileException {
        // set name from the already read info (outside this method), (somehow toString doesnt work)
        String name = "";
        for(int i = 0; i < info.size(); i++) {
            name += info.get(i);
        }
        // playerType
        String type = SaveLoad.readUntilStop(reader).toString();
        // points and cabins
        info = SaveLoad.readUntilStop(reader);
        int points = SaveLoad.parseCharListToNumber(info);
        info = SaveLoad.readUntilStop(reader);
        int cabins = SaveLoad.parseCharListToNumber(info);
        // now make a player and set all kinds of things
        Player player;
        if (type.equals("[c, l, a, s, s,  , k, a, s, i, n, o, ., H, u, m, a, n, P, l, a, y, e, r]")) {
            player = new HumanPlayer(name);
        } else {
            player = new ComputerPlayer(name);
        }
        player.setPoints(points);
        player.setCabins(cabins);
        // parse the cards in his/her hand
        info = SaveLoad.readUntilStop(reader);
        player.setHand(parseCards(info));
        // aaaand on his/her deck
        Deck temp = new Deck();
        info = SaveLoad.readUntilStop(reader);
        ArrayList<Card> parsed = SaveLoad.parseCards(info);
        for (int i = parsed.size() - 1; i >= 0; i--) {
            temp.addCard(parsed.get(i));
        }
        player.setDeck(temp);
        // add the newly made player
        game.getPlayers().add(player);
    }

}

