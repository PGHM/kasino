package kasinoGUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import kasino.Card;
import kasino.CorruptedSaveFileException;


public class KasinoGUI {

    private kasino.Game game; // We have to have access to Game's information and methods

    private JFrame frame; // main frame
    
    //  Backroundcolor
    private final Color background = new Color(0,150,0);    

    // determines if there is over 12 tablecards
    
    private boolean isExtra;
    
    // if the game is ended and you can't touch any of the buttons

    private boolean gameEnded;
    
    // panels for the mainframe

    private JPanel left;
    private JPanel right;
    private JPanel bottom;
    private JPanel center;
    private JPanel top;
    
    // containers for selected cards
    
    private ArrayList<kasino.Card> selectedTableCards = new ArrayList<kasino.Card>();
    private kasino.Card selectedPlayerCard;
    
    // players cards

    private JToggleButton playerCard0;
    private JToggleButton playerCard1;
    private JToggleButton playerCard2;
    private JToggleButton playerCard3;

    private JToggleButton[] playerCards;

    // tablecards

    private JToggleButton tableCard0 = new JToggleButton();
    private JToggleButton tableCard1 = new JToggleButton();
    private JToggleButton tableCard2 = new JToggleButton();
    private JToggleButton tableCard3 = new JToggleButton();
    private JToggleButton tableCard4 = new JToggleButton();
    private JToggleButton tableCard5 = new JToggleButton();
    private JToggleButton tableCard6 = new JToggleButton();
    private JToggleButton tableCard7 = new JToggleButton();
    private JToggleButton tableCard8 = new JToggleButton();
    private JToggleButton tableCard9 = new JToggleButton();
    private JToggleButton tableCard10 = new JToggleButton();
    private JToggleButton tableCard11 = new JToggleButton();

    private JToggleButton[] tableButtons;

    // Components that need to be refered to when updating the view

    private JScrollPane opponents; // list of opponents
    private JLabel turn; // labels whose turn it is
    private JScrollPane extraCards; // extra tablecards
    private JList extraList; // list for previous
    private JScrollPane selectedExtraCards; // list of selected extracards
    private JList selectedExtraList; // list for previous
    private Object[] tempCards; // temporary array to keep track of cards which were previously selected from extralist
    
    // buttons for left panel
    
    private JButton newGame; // makes new game
    private JButton loadGame; // loads and old game
    private JButton saveGame; // saves this situation to a text file
    private JButton table; // takes cards from table
    private JButton cards; // puts one card to a table
    
    // tablecard panels
    private JPanel tableCards;
    private JPanel firstRow; 
    private JPanel secondRow;
    private JPanel thirdRow;
    // deckcount labels
    private JLabel deckCount;
    private JLabel roundCount;
    private JLabel plrDeckCount;
    private JLabel roundNumber;
    private JLabel deck;
    private JLabel playerDeck;
    // filechooser for save/load
    private JFileChooser fileChooser;
    // components for log
    private JFrame logFrame; // log frame
    private JTextArea log;
    private JScrollPane logScroll;
    
    public KasinoGUI() {

        // new "main"frame
        this.frame = new JFrame("Kasino-game");

        //make the frame visible and set the program to close when the frame is closed
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 850);

        // Set the window to the right position
        Toolkit toolkit = frame.getToolkit();
        Dimension size = toolkit.getScreenSize();
        // there is a little bit of a magic number 85 in the end, its decided through testing which
        // would be the right position for the window
        frame.setLocation(size.width/2 - frame.getWidth()/2, size.height/2 - frame.getHeight()/2 - 85);
        
        // set main frame
        setMainFrameComponents();
        
        // set extracards
        setExtraCards();
        
        // set left side
        setLeftSide();

        // listener for newGame
        newGame.addActionListener(new newGameListener());

        // set up the fileChooser for text files
        fileChooser = new JFileChooser();
        FileNameExtensionFilter textFilter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.addChoosableFileFilter(textFilter);

        // listener for loadgame
        loadGame.addActionListener(new loadGameListener());

        // listener for savegame
        saveGame.addActionListener(new saveGameListener());
        
        // listener for put Cards
        table.addActionListener(new putTableListener());
        
        // listener to take Cards
        cards.addActionListener(new takeCardsListener());
        
        // listener for extralist
        extraList.addListSelectionListener(new extraListListener());
        
        // format playerCards
        formatPlayerCards();
        
        // set bottom
        setBottom();

        // set right side
        setRightSide();

        // add textfield to the top to show who's turn it is
        turn = new JLabel();
        turn.setFont(new Font("default",Font.BOLD, 30));
        top.add(turn);

        // set the center area
        setCenterArea();
        
        //format tablecards
        formatTableCards();
        
        // set center cardrows
        setCenterRows();
        
        //finally set log frame
        setLogFrame();
        
    }

    // FOLLOWING ARE LISTENER CLASSES FOR THE BUTTONS (names are selfexplatonary)
    
    class newGameListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // new game
            newGame();
            while(true) {
                // set players
                try {
                    // get the number of players
                    String players  = JOptionPane.showInputDialog(frame,"How many players? (0-N)"
                        ,"New game",JOptionPane.QUESTION_MESSAGE);
                    // if cancel was pushed or window closed, don't do anything
                    if (players == null) {
                        return;
                    }
                    int count = Integer.parseInt(players);
                    if ( count < 0) {
                        // invalid number
                        throw new NumberFormatException();
                    }
                    // get number of computerPlayers
                    String compPlayers  = JOptionPane.showInputDialog(frame,"How many computerplayers? (0-N)"
                        ,"New game",JOptionPane.QUESTION_MESSAGE);
                    // if cancel was pushed or window closed, set value to 0
                    if (compPlayers == null) {
                        compPlayers = "0";
                    }
                    int compCount = Integer.parseInt(compPlayers);
                    if (compCount + count < 2) {
                        JOptionPane.showMessageDialog(frame, "There must be atleast 2 players!");
                        continue;
                    }
                    if (compCount == 0 && count < 2) {
                        // there must be more than 1 human player if there ain't any comp. players
                        JOptionPane.showMessageDialog(frame, "You must give a number between 2 and N if there is no computerplayers");
                        continue;
                    } else {
                        // add the players to the game
                        ArrayList<kasino.Player> playerList = new ArrayList<kasino.Player>();
                        for (int i = 0; i < count; i++) {
                            String player = JOptionPane.showInputDialog(frame, "Set name for player" + 
                                " " + (i+1) +":", "New Game", JOptionPane.QUESTION_MESSAGE);
                            playerList.add(new kasino.HumanPlayer(player));
                        }
                        // if necessary, set the computer players
                        if (compCount > 0) {
                            for (int i = 0; i < compCount; i++) {
                                playerList.add(new kasino.ComputerPlayer("CompPlayer" + Integer.toString(i + 1)));
                            }
                        }
                        game.setPlayers(playerList);
                        break;
                    }   
                } catch (NumberFormatException e) {
                    // if the input is not a number a new input will be asked
                    JOptionPane.showMessageDialog(frame, "You must give a number between 0 and N");
                    continue;
                }
            }
            // start the game with new players!
            game.formatRound();
            // update the view
            setGameFieldVisible();
            updateView();
            // if the computerplayer starts the game
            if (game.getPlayers().get(game.getTurn()).getClass().equals(kasino.ComputerPlayer.class)) {
                computerTurn();
                nextTurn();
            }
            gameEnded = false;
        }
    }
    
    class loadGameListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // open a dialog to the user to select the file, if a file is selected
            // there is an fileChooser.APPROVE_SELECTION in the int
            while(true) {
                int temp = fileChooser.showDialog(frame, "Select file to load from");
                if(temp == JFileChooser.APPROVE_OPTION) {
                    File loadText = fileChooser.getSelectedFile();
                    try {
                    // get the path of the file
                    String path = loadText.getAbsolutePath();
                    // check if it is a text file
                    if (path.endsWith(".txt")) {
                        game = kasino.SaveLoad.loadGame(path);
                        // update game table
                        makeOpponentList();
                        setGameFieldVisible();
                        updateView();
                        log.setText("");
                        gameEnded = false;
                        break;
                    } else if (path == null) {
                        // if the user hits cancel/exit buttons, close the chooser
                        break;
                    } else {
                        // if it aint a text file make a error report
                        JOptionPane.showMessageDialog(frame, "Must be a text file!",
                            "Error in saving file", JOptionPane.ERROR_MESSAGE);
                    }
                    } catch (FileNotFoundException e) {
                        printLoadError(e);
                    } catch (IOException e) {
                        printLoadError(e);
                    } catch (CorruptedSaveFileException e) {
                        printLoadError(e);
                    }
                } else {
                    // if user hits cancel/exit close the window
                    break;
                }
            } 
        }
    }
    
    class saveGameListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (game == null) {
                JOptionPane.showMessageDialog(frame,"Start a new game before you save it!",
                    "There is nothing to save",JOptionPane.WARNING_MESSAGE);
            } else {
                while(true) {
                    // open a dialog to the user to select the file, if a file is selected
                    // there is an fileChooser.APPROVE_SELECTION in the int
                    int temp = fileChooser.showDialog(frame, "Save");
                    if(temp == JFileChooser.APPROVE_OPTION) {
                        // get the file selected
                        File saveText = fileChooser.getSelectedFile();
                        try {
                            // get the path of the file
                            String path = saveText.getAbsolutePath();
                            // check if it is a text file
                            if (path.endsWith(".txt")) {
                                kasino.SaveLoad.saveGame(game, path);
                                JOptionPane.showMessageDialog(frame, "Game Saved successfully",
                                    "Game saved",JOptionPane.INFORMATION_MESSAGE);
                                break;
                            } else {
                                // if it aint a text file make a error report
                                JOptionPane.showMessageDialog(frame, "Must be a text file!",
                                    "Error in saving game", JOptionPane.ERROR_MESSAGE);
                            }
                            // catch the potential problems and print errordialog, then try again
                        } catch (IllegalArgumentException e) {
                            printSaveError(e);
                        } catch (FileNotFoundException f) {
                            printSaveError(f);
                        } catch (IOException i) {
                            printSaveError(i);
                        }
                    } else {
                        // if user hits cancel/exit, close the window
                        return;
                    }
                }
            }
        }
    }
    
    class putTableListener implements ActionListener {
        public void actionPerformed(ActionEvent Event) {
            // puts the selected card to table
            putCardToTable();
        }
    }
    
    class tableKeyListener implements KeyListener {
        // if 'a' is pressed then put selectedPlayerCard in to the table
        public void keyTyped(KeyEvent event) {
            if(event.getKeyChar() == 'a') {
                putCardToTable();
            }
        }
        // these are here just because they must be
        public void keyPressed(KeyEvent event) {}
        public void keyReleased(KeyEvent event) {}
    }

    class cardsKeyListener implements KeyListener {
        // if 'd' is pressed then take selected cards with selected player card
        public void keyTyped(KeyEvent event) {
            if(event.getKeyChar() == 'd') {
                takeCardsFromTable();
            }
        }
        // these are here just because they must be
        public void keyPressed(KeyEvent event) {}
        public void keyReleased(KeyEvent event) {}
    }
    
    class takeCardsListener implements ActionListener {
        public void actionPerformed(ActionEvent Event) {
            // tries to take cards from table
            takeCardsFromTable();
        }
    }
    
    class extraListListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            // updates the selected extracards field
            Object[] selectedCards = extraList.getSelectedValues();
            selectedExtraList.setListData(selectedCards);
            selectedExtraCards.getViewport().add(selectedExtraList);
            // removes the previous selections from selected table cards if there is any
            if (tempCards != null) {
                for(int i = 0; i < tempCards.length; i++) {
                    selectedTableCards.remove(tempCards[i]);
                }
            }
            // adds the selected cards to selected table cards
            for(int j = 0; j < selectedCards.length; j++) {
                selectedTableCards.add((kasino.Card)selectedCards[j]);
            }
            // save the cards that were added now so they can be removed when the next action fires
            tempCards = selectedCards;
        }
    }
    
    class playerCardListener implements ActionListener {
        public void actionPerformed(ActionEvent Event) {
            // If the game is ended you can't continue to play
            if(gameEnded) {
                printGameEndedWarning();
            }
            // get the index of the right button in the list of our buttons (little bit cheezy)
            JToggleButton pressed = (JToggleButton)Event.getSource();
            int i = Integer.parseInt(pressed.getName());
            // if the button is already selected, then just remove the selection and the current card
            // why the !isSelected is because java makes the toggle first before this actionevent
            if (!playerCards[i].isSelected()) {
                playerCards[i].setSelected(false);
                selectedPlayerCard = null;
            } else {
                // deselect previous cards, and select the new one
                for(int j = 0; j < 4; j++) {
                    playerCards[j].setSelected(false);
                }
                playerCards[i].setSelected(true);
                // get the right card from players hand
                selectedPlayerCard = game.getPlayers().get(game.getTurn())
                    .drawCardFromHand(i);
            }
        }
    }
    
    class tableListener implements ActionListener {
        public void actionPerformed(ActionEvent Event) {
            // If the game is ended you can't continue to play
            if(gameEnded) {
                printGameEndedWarning();
            }
            // get the index of the right button in the list of our buttons (little bit cheezy)
            JToggleButton pressed = (JToggleButton)Event.getSource();
            int i = Integer.parseInt(pressed.getName());
            if (pressed.isSelected()) {
                // gets the card from the button that was selected and adds it to the selected cards
                selectedTableCards.add(game.getTableCards().get(i));
            } else {
                // removes the card from the button which was deselected from the seleceted cards
                selectedTableCards.remove(game.getTableCards().get(i));
            }
        }
    }
    
    // FOLLOWING METHODS ARE HELPERS FOR CREATING THE GUI (names are selfexplatonary)
    
    private void setLogFrame() {
        // create log frame
        logFrame = new JFrame("Log");
        logScroll = new JScrollPane();
        logFrame.add(logScroll);
        log = new JTextArea();
        log.setEditable(false);
        logScroll.getViewport().add(log);
        logFrame.setVisible(true);
        logFrame.setPreferredSize(new Dimension(1100, 135));
        logFrame.pack();
        
        // Move the window below main frame
        Point moveTo = frame.getLocation();
        moveTo.setLocation(moveTo.getX(), moveTo.getY()+frame.getHeight());
        logFrame.setLocation(moveTo);
    }
    
    
    private void setCenterRows() {
        // add the cards
        firstRow.add(Box.createHorizontalGlue());
        firstRow.add(tableCard8);
        firstRow.add(Box.createRigidArea(new Dimension(30,0)));
        firstRow.add(tableCard9);
        firstRow.add(Box.createRigidArea(new Dimension(30,0)));
        firstRow.add(tableCard10);
        firstRow.add(Box.createRigidArea(new Dimension(30,0)));
        firstRow.add(tableCard11);
        firstRow.add(Box.createHorizontalGlue());
        tableCards.add(Box.createRigidArea(new Dimension(0,20)));
        secondRow.add(Box.createHorizontalGlue());
        secondRow.add(tableCard4);
        secondRow.add(Box.createRigidArea(new Dimension(30,0)));
        secondRow.add(tableCard5);
        secondRow.add(Box.createRigidArea(new Dimension(30,0)));
        secondRow.add(tableCard6);
        secondRow.add(Box.createRigidArea(new Dimension(30,0)));
        secondRow.add(tableCard7);
        secondRow.add(Box.createHorizontalGlue());
        tableCards.add(Box.createRigidArea(new Dimension(0,20)));
        thirdRow.add(Box.createHorizontalGlue());
        thirdRow.add(tableCard0);
        thirdRow.add(Box.createRigidArea(new Dimension(30,0)));
        thirdRow.add(tableCard1);
        thirdRow.add(Box.createRigidArea(new Dimension(30,0)));
        thirdRow.add(tableCard2);
        thirdRow.add(Box.createRigidArea(new Dimension(30,0)));
        thirdRow.add(tableCard3);
        thirdRow.add(Box.createHorizontalGlue());
    }
    
    private void formatTableCards() {
        // add tablecards to the list
        
        this.tableButtons = new JToggleButton[] {tableCard0,tableCard1,tableCard2,tableCard3,tableCard4,
            tableCard5,tableCard6,tableCard7,tableCard8,tableCard9,tableCard10,tableCard11 };
        
        // format tableCards
        
        for(int i = 0; i < 12; i++) {
            tableButtons[i].setName(Integer.toString(i));
            tableButtons[i].setMargin(new Insets(1,1,1,1));
            tableButtons[i].setBackground(Color.BLACK);
            tableButtons[i].setVisible(false);
            tableButtons[i].addActionListener(new tableListener());
            tableButtons[i].addKeyListener(new tableKeyListener());
            tableButtons[i].addKeyListener(new cardsKeyListener());
        }
    }
    
    private void setCenterArea() {
        tableCards = new JPanel();
        
        tableCards.setLayout(new BoxLayout(tableCards, BoxLayout.Y_AXIS));
        tableCards.setBackground(background);
        center.add(tableCards, BorderLayout.CENTER);
        
        // three rows for cards on the table and change backround colors to match the surroundings
        firstRow = new JPanel();
        secondRow = new JPanel();
        thirdRow = new JPanel();
        
        firstRow.setLayout(new BoxLayout(firstRow,BoxLayout.X_AXIS));
        secondRow.setLayout(new BoxLayout(secondRow,BoxLayout.X_AXIS));
        thirdRow.setLayout(new BoxLayout(thirdRow,BoxLayout.X_AXIS));
        
        firstRow.setBackground(background);
        secondRow.setBackground(background);
        thirdRow.setBackground(background);
        
        // add rows to the main tablecards panel
        tableCards.add(Box.createVerticalGlue());
        tableCards.add(firstRow);
        tableCards.add(Box.createRigidArea(new Dimension(0,20)));
        tableCards.add(secondRow);
        tableCards.add(Box.createRigidArea(new Dimension(0,20)));
        tableCards.add(thirdRow);
        
        // make the tablecards section little bit clearer by adding borders
        
        tableCards.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLACK, 3), "Cards in the table"));
    }
    
    private void setRightSide() {
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));
        
        // create three labels for two decks and roundnumber
        
        roundNumber = new JLabel();
        roundNumber.setLayout(new FlowLayout());
        roundNumber.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 3),"Round"));
        roundNumber.setVisible(false);
        
        deck = new JLabel();
        deck.setLayout(new FlowLayout());
        deck.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 3),"Cards in the deck"));
        deck.setVisible(false);
        
        playerDeck = new JLabel();
        playerDeck.setLayout(new FlowLayout());
        playerDeck.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 3),"Acquired cards"));
        playerDeck.setVisible(false);
        
        // These sizings work really strangely, well these values work for me...
        
        roundNumber.setMaximumSize(new Dimension(160,100));
        roundNumber.setPreferredSize(new Dimension(150,60));
        deck.setMaximumSize(new Dimension(160,100));
        deck.setPreferredSize(new Dimension(150,60));
        playerDeck.setMaximumSize(new Dimension(160,100));
        playerDeck.setPreferredSize(new Dimension(150,60));
        
        // Number for the decks and roundNumber
        
        roundCount = new JLabel("1");
        roundCount.setFont(new Font("default",Font.BOLD, 20));
        roundNumber.add(roundCount);
        
        deckCount = new JLabel();
        deckCount.setFont(new Font("default",Font.BOLD, 20));
        deck.add(deckCount);
        
        plrDeckCount = new JLabel();
        plrDeckCount.setFont(new Font("default",Font.BOLD, 20));
        playerDeck.add(plrDeckCount);
        
        // add panels
        
        right.add(roundNumber);
        right.add(Box.createVerticalGlue());
        right.add(deck);       
        right.add(Box.createVerticalGlue());
        right.add(playerDeck);
    }
    
    private void setBottom() {
        // BoxLayout for bottom too
        
        bottom.setLayout(new BoxLayout(bottom,BoxLayout.X_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        
        // set cards to layout
        
        bottom.add(Box.createHorizontalGlue());
        bottom.add(playerCard0);
        bottom.add(Box.createRigidArea(new Dimension(30,0)));
        bottom.add(playerCard1);
        bottom.add(Box.createRigidArea(new Dimension(30,0)));
        bottom.add(playerCard2);
        bottom.add(Box.createRigidArea(new Dimension(30,0)));
        bottom.add(playerCard3);
        bottom.add(Box.createHorizontalGlue());
        
        // borders for bottom
        
        bottom.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 3), "Your cards"));
    }
    
    private void formatPlayerCards() {
        // create four buttons for player's own cards
        
        playerCard0 = new JToggleButton(); 
        playerCard1 = new JToggleButton();
        playerCard2 = new JToggleButton();
        playerCard3 = new JToggleButton();
        
        // add the buttons to an array (this is needed for updateView())
        playerCards = new JToggleButton[] { playerCard0, playerCard1, playerCard2, playerCard3 };
        
        // format the cards
        
        for(int i = 0; i < playerCards.length; i++) {
            // this is necessary to get the corresponding cards from the buttons
            playerCards[i].setName(Integer.toString(i));
            // Margins to one to make it look right and colors to make the borders clearer
            playerCards[i].setBackground(Color.BLACK);
            playerCards[i].setMargin(new Insets(1,1,1,1));
            // make the cards invisible for now
            playerCards[i].setVisible(false);
            // set listeners for playercards
            playerCards[i].addActionListener(new playerCardListener());
            playerCards[i].addKeyListener(new tableKeyListener());
            playerCards[i].addKeyListener(new cardsKeyListener());
        }
    }
    
    private void setLeftSide() {
        // we will use box layout for the left side with thin borders

        left.setLayout(new BoxLayout(left,BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(20,5,5,5));

        // create buttons and list for the left panel, Load, Save, Start a new game,
        // Take Cards, Put to Table and the list of the opponents

        newGame = new JButton("New Game");
        loadGame = new JButton("Load Game");
        saveGame = new JButton("Save Game");
        table = new JButton("Put To Table (a)");
        cards = new JButton("Take Cards (d)");

        opponents = new JScrollPane();
        JList list  = new JList();
        list.setBackground(background);
        opponents.getViewport().add(list);

        // beatify a bit

        opponents.setBackground(background);
        opponents.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 3), "Opponents"));

        // hide for now, will be visible in the game
        // add the buttons to the panel

        left.add(Box.createRigidArea(new Dimension(0,50)));
        left.add(newGame);
        left.add(Box.createRigidArea(new Dimension(0,10)));
        left.add(loadGame);
        left.add(Box.createRigidArea(new Dimension(0,10)));
        left.add(saveGame);
        left.add(Box.createVerticalGlue());
        left.add(opponents);
        left.add(Box.createRigidArea(new Dimension(0,70)));
        left.add(table);
        left.add(Box.createRigidArea(new Dimension(0,10)));
        left.add(cards);
        left.add(Box.createRigidArea(new Dimension(0,70)));
    }

    private void setExtraCards() {
        // a list for extra tablecards if the centerarea is too small (n > 12)

        extraCards = new JScrollPane();
        extraList = new JList();
        extraList.setBackground(background);
        extraCards.setMaximumSize(new Dimension(130,150));
        extraCards.setPreferredSize(new Dimension(130,150));
        extraCards.setBackground(background);
        extraCards.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 3)
            , "Extra tablecards"));
        
        // a list for selected extra tablecards
        
        selectedExtraCards = new JScrollPane();
        selectedExtraList = new JList();
        selectedExtraList.setBackground(background);
        selectedExtraCards.setMaximumSize(new Dimension(130,150));
        selectedExtraCards.setPreferredSize(new Dimension(130,150));
        selectedExtraCards.getViewport().add(selectedExtraList);
        selectedExtraCards.setBackground(background);
        selectedExtraCards.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 3)
            , "Selected Extracards"));
    }

    private void setMainFrameComponents() {
        // lets start by making the mainlayout with BorderLayout
        frame.setLayout(new BorderLayout());
        // create all the panels to the layout, backround color green
        left = new JPanel();
        left.setPreferredSize(new Dimension(150,800));
        left.setBackground(background);
        top = new JPanel();
        top.setPreferredSize(new Dimension(200,50));
        top.setBackground(background);
        bottom = new JPanel();
        bottom.setPreferredSize(new Dimension(700,200));
        bottom.setBackground(background);
        right = new JPanel();
        right.setPreferredSize(new Dimension(160,300));
        right.setBackground(background);
        center = new JPanel();
        center.setBackground(background);
        // make new layout to the center part to get the desired result
        center.setLayout(new BorderLayout());
        // add panels to the layout
        center.add(top, BorderLayout.NORTH);
        center.add(bottom, BorderLayout.SOUTH);
        center.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        frame.add(left, BorderLayout.WEST);
        frame.add(right, BorderLayout.EAST);
        frame.add(center, BorderLayout.CENTER);
    }
    
    //ENDS HERE ^
    
    /*
     * Sets a new game
     */
    private void newGame() {
        // new game with empty list of players
        this.game = new kasino.Game(new ArrayList<kasino.Player>());
        log.setText("");
    }

    /*
     * Makes a list of the opponents and updates it to the GUI, 
     * this is done only once when the game starts
     */

    private void makeOpponentList() {
        JList list  = new JList(game.getPlayers().toArray());
        list.setBackground(background);
        opponents.getViewport().add(list); // adds the list to a scroll pane
    }

    /*
     * Updates the view on the GUI, this is done usually when the turn ends and new one begins
     */

    private void updateView() {

        // update the roundnumber
        roundCount.setText(Integer.toString(game.getRound()));

        // update whose turn it is
        turn.setText(game.getPlayers().get(game.getTurn()).toString());

        // update the decks
        deckCount.setText(Integer.toString(game.getDeck().countCards()));
        plrDeckCount.setText(Integer.toString(game.getPlayers().get(game.getTurn()).getDeck().countCards()));

        // update players cards
        kasino.Card handCard = null;
        for(int i = 0; i < 4; i++) {
            // checks if there is less cards then 4
            if (game.getPlayers().get(game.getTurn()).getHand().size() > i) {
                // gets the card from players hand
                handCard = game.getPlayers().get(game.getTurn()).getHand().get(i);
            }
            if(handCard == null) {
                // if there is no card hide the button
                playerCards[i].setVisible(false);
            } else {
                // fetches and updates the picture
                playerCards[i].setIcon(getPicture(handCard));
                handCard = null;
            }
        }
        
        // update tablecards
        ArrayList<kasino.Card> cardss = game.getTableCards();
        for(int i = 0; i < 12; i++) {
            // if there is a card for that slot, set it visible and fetch the image
            if (i < (cardss.size())) {
                tableButtons[i].setVisible(true);
                tableButtons[i].setIcon(getPicture(cardss.get(i)));
            } else {
                // hide the card if there is no card for that slot
                tableButtons[i].setVisible(false);
                continue;
            }
        }
        
        if(game.getTableCards().size() > 12) {
            // if there is over 12 tablecards we need to list the extra cards in a separate list
            isExtra = true;
            // adds the list to the GUI
            firstRow.add(extraCards);
            Object[] cards = cardss.subList(12, cardss.size()).toArray();
            extraList.setListData(cards);
            extraCards.getViewport().add(extraList);
            // adds the list of selected extra cards to the GUI
            secondRow.add(selectedExtraCards);
        } else {
            // if the extratable is visible, remove it
            if (isExtra) {
                firstRow.remove(extraCards);
                secondRow.remove(selectedExtraCards);
                isExtra = false;
            }
        }
    }

    /*
     * Fetches the picture of the card given
     */

    private ImageIcon getPicture(kasino.Card card) {

        // makes imgURL out of the cards address    
        java.net.URL imgURL = getClass().getResource("/Cards/" + card.getSuit().toString().charAt(0) + 
            card.getValueInTable() + ".JPG");

        ImageIcon image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL));
        return image;

    }
    /*
     * Resets all selections of buttons ands resets the selected cards on the table and hand
     */
    
    private void resetSelections() {
        for(int j = 0; j < 4; j++) {
            playerCards[j].setSelected(false);
        }
        for(int i = 0; i < 12;i++) {
            tableButtons[i].setSelected(false);
        }
        selectedTableCards.clear();
        selectedPlayerCard = null;
    }
    
    /*
     * If the game is won, announces the winner and the points
     */
    private void gameEnded() {
        JOptionPane.showMessageDialog(frame, "Game has been won!\nThe Winner is: " + 
            game.getLeader().toString(), "Win!", JOptionPane.INFORMATION_MESSAGE);
        // clears the game, waiting for new one to begin!
        gameEnded = true;
        updateView();
        makeOpponentList();
    }
    
    /*
     * Two methods which print error messages for Save and Load 
     */
    private void printSaveError(Exception e) {
        JOptionPane.showMessageDialog(frame, e.getMessage(),
            "Error in saving game", JOptionPane.ERROR_MESSAGE);
    }
    
    private void printLoadError(Exception e) {
        JOptionPane.showMessageDialog(frame, e.getMessage(),
            "Error in loading game", JOptionPane.ERROR_MESSAGE);
    }
    
    /*
     * Makes the game field visible when a new game is started
     */
    
    private void setGameFieldVisible() {
        // update the GUI view, first add the list of players to left
        makeOpponentList();
        // make elements visible
        playerCard0.setVisible(true);
        playerCard1.setVisible(true);
        playerCard2.setVisible(true);
        playerCard3.setVisible(true);
        roundNumber.setVisible(true);
        playerDeck.setVisible(true);
        deck.setVisible(true);
    }
    
    /*
     * When game ends, the table should be clear
     */
    
    private void printGameEndedWarning() {
        JOptionPane.showMessageDialog(frame, "Game has ended, start a new one or load a game",
            "Game has ended!", JOptionPane.WARNING_MESSAGE);
    }
    
    
    /*
     * Moves the turn, if the round ends, begins a new one, if the game ends, winner will be announced
     */
    private void nextTurn() {
        // moves turn and checks if the round ends
        if (game.moveTurn()) {
            // if it does end, checks if there is a winner
            if (game.endRound()) {
                // if there is a winner, it will be announced
                gameEnded();
            } else {
                // announces new round has begun and the current leader
                JOptionPane.showMessageDialog(frame, "New round begins.\nThe current leader is: " + 
                    game.getLeader().toString(), "New round", JOptionPane.INFORMATION_MESSAGE);
                // if the game continues, formats a new round
                game.formatRound();
                setNewRound();
            }
        }
        // If the next opponent is computer opponent, we need to call its methods and then change turn
        if (game.getPlayers().get(game.getTurn()).getClass().equals(kasino.ComputerPlayer.class) && !gameEnded) {
            computerTurn();
            nextTurn();
        }
        // updates the view
        resetSelections();
        updateView();
    }
    
    /*
     * Makes the moves that AI decides
     */
    private void computerTurn() {
        // first try to take cards
        kasino.ComputerPlayer comp = (kasino.ComputerPlayer)game.getPlayers().get(game.getTurn());
        kasino.ValueWeight best = null;
        // we have to remember which handCard we are going to give
        int handCardIndex = 0;
        // determine the best of the four (or less) handCards
        for (int i = 0; i < comp.getHand().size(); i++) {
            // here we count take the best cards with this card
            kasino.ValueWeight cards = comp.bestCards(comp.countPossibilities(comp.getHand().get(i), 
                game.getTableCards()), comp.getHand().get(i), game.getTableCards().size());
            // if there was a possibility we check if its the best thy far, then save the index of the picking card
            if (cards != null) {
                if (best == null || cards.getValue() > best.getValue()) {
                    handCardIndex = i;
                    best = cards;
                }
            }
        }
        // if there was cards to take, lets take 'em!
        if (best != null) {
            Card handCard = comp.getHand().get(handCardIndex);
            ArrayList<Card> cards = game.cloneList(best.getCards());
            game.takeCards(cards, handCard);
            // lets inform the player what the comp did
            writeLog(comp.getName() + " took these cards: " + cards.toString()
                + " with this card: " + handCard.toString());
        } else {
            // if there wasn't, we have to figure which card to put away
            Card bestCard = comp.bestCard(comp.getHand(), game.getTableCards());
            game.putCardTable(bestCard);
            // lets inform the player what the comp did
            writeLog(comp.getName() + " put this card to the table: " + bestCard.toString());
        }
    }
    
    /*
     * Starts a new round in a kasino-game, updating the view as it should
     */
    private void setNewRound() {
        setGameFieldVisible();
        updateView();
        makeOpponentList();
        log.setText("");
    }
    
    /*
     * Writes turn informations to the logArea
     */
    private void writeLog(String msg) {
    	log.setText(log.getText() + "\n" + msg);
    }
    
    /*
     * Puts a single card to the table
     */
    private void putCardToTable() {
		// If the game is ended you can't continue to play
           if(gameEnded) {
               printGameEndedWarning();
               return;
           }
           
           if (selectedPlayerCard == null) {
               // if no card is selected, demand a selection
               JOptionPane.showMessageDialog(frame, "You must select one card to put to the table",
                    "Select a card?", JOptionPane.INFORMATION_MESSAGE);
           } else {
               // put the card to the table, update the log and move round
               game.putCardTable(selectedPlayerCard);
               writeLog(game.getPlayers().get(game.getTurn()).getName() + " put this card to the table: " + selectedPlayerCard.toString());
               nextTurn();
           }
    }

    /*
     * Take Cards from the table
     */
    
	private void takeCardsFromTable() {
		//  If the game is ended you can't continue to play
           if(gameEnded) {
               printGameEndedWarning();
               return;
           }
           // if all the needed components are not selected, ask the player to select em first
           if (selectedPlayerCard == null && selectedTableCards.isEmpty()) {
               JOptionPane.showMessageDialog(frame, "Select a picking card and cards you want to take first",
                   "Pick cards", JOptionPane.INFORMATION_MESSAGE);
           } else if (selectedPlayerCard == null && !selectedTableCards.isEmpty()) {
               JOptionPane.showMessageDialog(frame, "Select a picking card first",
                   "Select a picking card", JOptionPane.INFORMATION_MESSAGE);
           } else if (selectedPlayerCard != null && selectedTableCards.isEmpty()) {
               JOptionPane.showMessageDialog(frame, "Select cards you want to take first",
                   "Select cards you want to take", JOptionPane.INFORMATION_MESSAGE);
           } else {
               // if cards are selected then test if the move is valid
               if(game.takeCards(selectedTableCards, selectedPlayerCard)) {
                   // when the move is valid, move turn,update log and update the game field
            	   writeLog(game.getPlayers().get(game.getTurn()).getName() +" took these cards: " + selectedTableCards.toString() 
            			   + " with this card: " + selectedPlayerCard.toString());
                   nextTurn();
               } else {
                   // if the move ain't valid, print that it's not
                   JOptionPane.showMessageDialog(frame, "You can't take these card with this card, select other ones",
                       "Move ain't valid", JOptionPane.WARNING_MESSAGE);
               }
           }
	}
    
    public static void main(String[] args) {
        new KasinoGUI();
        
    }
    
}
