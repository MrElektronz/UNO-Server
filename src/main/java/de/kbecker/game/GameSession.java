package de.kbecker.game;

import de.kbecker.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class GameSession {



    private ArrayList<GameInstance.Player> players;
    private int turn;
    private int direction;
    private Stack<Card> deck;
    private Stack<Card> cardStack;
    private int currentPlayerIndex;
    private boolean waitingForWildCard;
    private Card.CardColor wildCardColor;

    public GameSession(){
        waitingForWildCard = false;
        turn = 0;
        this.players = new ArrayList<>();
        cardStack = new Stack<>();
        currentPlayerIndex = 0;
        direction = 1;
        fillDeck();
    }

    public GameSession(ArrayList<GameInstance.Player> players){
        waitingForWildCard = false;
        turn = 0;
        this.players = players;
        currentPlayerIndex = 0;
        cardStack = new Stack<>();
        direction = 1;
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
     * Gets called whenever a turn ends and the next one should begin
     */
    private void nextTurn(){
        turn++;
        System.out.println("Turn: "+turn);
        currentPlayerIndex = turn%players.size();
        if(direction<0){
            currentPlayerIndex = players.size()-(currentPlayerIndex)-1;
        }
        System.out.println("CurrentPlayerIndex: "+currentPlayerIndex);

    }

    /**
     * Shuffles the deck of cards
     */
    private void shuffle(){
        Collections.shuffle(deck);
    }

    /**
     * takes the upper card of the deck and adds it to the stack
     * @param normalOnly If 'true' only colored numbers will be set from the deck
     */
    public void layCardFromDeck(boolean normalOnly){
        if(normalOnly){
            int index = 0;
            while(!deck.get(index).getType().equals(Card.CardType.NUMBER)){
                index++;
                if(index>deck.size()-1){
                    break;
                }
            }
            Card card = deck.get(index);
            deck.remove(index);
            cardStack.add(card);

        }else{
            Card card = deck.get(0);
            deck.remove(0);
            cardStack.add(card);
        }
    }


    /**
     * deals 7 cards to every player, only needs to be called at the beginning of the game
     */
    public void dealSevenCards(){
        for(GameInstance.Player p: players){
            for(int i = 0; i< 7; i++) {
                p.getCards().add(deck.pop());
            }
        }
    }

    /**
     *
     * @param sessionID
     * @param card
     * @return 'true' if card was set, return 'false' otherwise
     */
    public boolean setCard(String sessionID, Card card){
        for(GameInstance.Player p: players) {
            if(p.getSessionID().equals(sessionID) && (card.canPlayCard(getCurrentCard()) || wildCardColor == card.getColor()) && currentPlayerIndex==players.indexOf(p)){
                wildCardColor = null;
                if(card.getColor().equals(Card.CardColor.BLACK)){
                    waitingForWildCard = true;
                }else{
                    // If skip card was used, skip this turn
                    if(card.getType().equals(Card.CardType.SKIP)){
                        nextTurn();
                    }else if(card.getType().equals(Card.CardType.REVERSE)){
                        direction*=-1;
                    }
                    // Next turn
                    nextTurn();
                    if(card.getType().equals(Card.CardType.DRAW2)){
                        drawCard(players.get(currentPlayerIndex).getSessionID());
                        drawCard(players.get(currentPlayerIndex).getSessionID());
                    }
                }

                p.getCards().remove(card);
                cardStack.add(card);
                return true;
            }
        }
        return false;
    }

    public boolean setColorForWildcard(String sessionID, String color){
        if(waitingForWildCard){
            waitingForWildCard = false;
            //green = 0x008000ff, yellow = 0xffff00ff, red = 0xff0000ff, blue = 0x0000ffff
            System.out.println("Color: "+color);
            //TODO: Color logic
            switch(color){
                case "0x008000ff":
                    wildCardColor = Card.CardColor.GREEN;
                    break;
                case "0xffff00ff":
                    wildCardColor = Card.CardColor.YELLOW;
                    break;
                case "0xff0000ff":
                    wildCardColor = Card.CardColor.RED;
                    break;
                case "0x0000ffff":
                    wildCardColor = Card.CardColor.BLUE;
                    break;
                default:
                    wildCardColor = null;
                    break;
            }
            nextTurn();

            // If wild4
            if(cardStack.peek().getType().equals(Card.CardType.WILD4)){
                for(int i = 0; i<4; i++){
                    drawCard(players.get(currentPlayerIndex).getSessionID());
                }
            }

            return true;
        }
        return false;
    }

    public boolean isWaitingForWildCard() {
        return waitingForWildCard;
    }

    public void drawCard(String sessionID){
        for(GameInstance.Player p: players) {
            if(p.getSessionID().equals(sessionID)){
                p.getCards().add(deck.pop());

                // Sort already used cards back into the deck
                if(deck.size()==0){
                    // Skip the first (upper) card
                    for(int i = 1; i<cardStack.size();i++){
                        deck.add(cardStack.remove(i));
                    }
                    shuffle();
                }
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
