package de.kbecker.game;

import de.kbecker.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class GameSession {

    enum Direction{
        CLOCKWISE(1), COUNTERCLOCKWISE(-1);

        private int dir;
        Direction(int dir) {
            this.dir = dir;
        }
        public int toNumber(){
            return dir;
        }
    }

    private ArrayList<GameInstance.Player> players;
    private int turn;
    private Direction direction;
    private Stack<Card> deck;
    private Stack<Card> cardStack;
    private int currentPlayerIndex;
    public GameSession(){
        turn = 1;
        this.players = new ArrayList<>();
        cardStack = new Stack<>();
        currentPlayerIndex = 0;
        direction = Direction.CLOCKWISE;
        fillDeck();
    }

    public GameSession(ArrayList<GameInstance.Player> players){
        turn = 1;
        this.players = players;
        currentPlayerIndex = 0;
        cardStack = new Stack<>();
        direction = Direction.CLOCKWISE;
        fillDeck();
    }

    public GameInstance.Player getCurrentPlayer(){
        return players.get(currentPlayerIndex);
    }

    public void addPlayer(GameInstance.Player p){

    }

    public ArrayList<GameInstance.Player> getPlayers() {
        return players;
    }

    /**
     * Creates a new deck and fills it with all 108 cards, which are:
     * 19 blue cards from 0-9
     * 19 red cards from 0-9
     * 19 green cards from 0-9
     * 19 yellow cards from 0-9
     */
    private void fillDeck(){
        deck = new Stack<>();
        Card.CardColor currentColor = Card.CardColor.BLUE;
        deck.add(new Card(currentColor,0));
        for(int i = 1; i<10;i++){
            deck.add(new Card(currentColor,i));
            deck.add(new Card(currentColor,i));
        }
        deck.add(new Card(currentColor, Card.CardType.DRAW2));
        deck.add(new Card(currentColor, Card.CardType.DRAW2));
        deck.add(new Card(currentColor, Card.CardType.SKIP));
        deck.add(new Card(currentColor, Card.CardType.SKIP));
        deck.add(new Card(currentColor, Card.CardType.REVERSE));
        deck.add(new Card(currentColor, Card.CardType.REVERSE));



        currentColor = Card.CardColor.RED;
        deck.add(new Card(currentColor,0));
        for(int i = 1; i<10;i++){
            deck.add(new Card(currentColor,i));
            deck.add(new Card(currentColor,i));
        }
        deck.add(new Card(currentColor, Card.CardType.DRAW2));
        deck.add(new Card(currentColor, Card.CardType.DRAW2));
        deck.add(new Card(currentColor, Card.CardType.SKIP));
        deck.add(new Card(currentColor, Card.CardType.SKIP));
        deck.add(new Card(currentColor, Card.CardType.REVERSE));
        deck.add(new Card(currentColor, Card.CardType.REVERSE));

        currentColor = Card.CardColor.GREEN;
        deck.add(new Card(currentColor,0));
        for(int i = 1; i<10;i++){
            deck.add(new Card(currentColor,i));
            deck.add(new Card(currentColor,i));
        }
        deck.add(new Card(currentColor, Card.CardType.DRAW2));
        deck.add(new Card(currentColor, Card.CardType.DRAW2));
        deck.add(new Card(currentColor, Card.CardType.SKIP));
        deck.add(new Card(currentColor, Card.CardType.SKIP));
        deck.add(new Card(currentColor, Card.CardType.REVERSE));
        deck.add(new Card(currentColor, Card.CardType.REVERSE));

        currentColor = Card.CardColor.YELLOW;
        deck.add(new Card(currentColor,0));
        for(int i = 1; i<10;i++){
            deck.add(new Card(currentColor,i));
            deck.add(new Card(currentColor,i));
        }
        deck.add(new Card(currentColor, Card.CardType.DRAW2));
        deck.add(new Card(currentColor, Card.CardType.DRAW2));
        deck.add(new Card(currentColor, Card.CardType.SKIP));
        deck.add(new Card(currentColor, Card.CardType.SKIP));
        deck.add(new Card(currentColor, Card.CardType.REVERSE));
        deck.add(new Card(currentColor, Card.CardType.REVERSE));

        // Add the wild cards
        currentColor = Card.CardColor.BLACK;
        for(int i = 0; i<4;i++){
            deck.add(new Card(currentColor, Card.CardType.WILD));
            deck.add(new Card(currentColor, Card.CardType.WILD4));
        }
        shuffle();
    }

    /**
     * Shuffles the deck of cards
     */
    private void shuffle(){
        Collections.shuffle(deck);
    }

    /**
     * takes the upper card of the deck and adds it to the stack
     */
    public void layCardFromDeck(){
        Card card = deck.get(0);
        deck.remove(0);
        cardStack.add(card);
    }

    /**
     * deals 7 cards to every player, only needs to be called at the beginning of the game
     */
    public void dealSevenCards(){
        for(GameInstance.Player p: players){
            for(int i = 0; i< 7;i++) {
                p.getCards().add(deck.pop());
            }
        }
    }

    public Card getCurrentCard(){
        return cardStack.peek();
    }

    public int getTurn() {
        return turn;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
}
