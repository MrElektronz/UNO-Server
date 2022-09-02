package de.kbecker.cards;

import com.google.gson.JsonObject;

import java.util.Objects;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class Card {

    public enum CardColor{
        RED, GREEN, YELLOW, BLUE, BLACK
    }
    public enum CardType{
        NUMBER, SKIP, DRAW2, REVERSE, WILD, WILD4
    }

    private CardColor color;
    private CardType type;
    private int number;

    public Card(CardColor color, CardType type){
        this.type = type;
        this.color = color;
        this.number = -1;
    }



    public Card(CardColor color, int number){
        this.number = number;
        this.color = color;
        this.type=CardType.NUMBER;
    }

    public CardColor getColor() {
        return color;
    }

    public CardType getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }




    @Override
    public String toString() {
        return "Card{" +
                "color=" + color +
                ", type=" + type +
                ", number=" + number +
                '}';
    }

    public boolean canPlayCard(Card current){
        //If current and this card are wild cards
        if((color.equals(CardColor.BLACK)) && current.getColor().equals(CardColor.BLACK)){
            return false;
        }
        //If current and this cards color are the same or number is the same or both are draw2, reverse or skip
        else if(color==current.getColor()||(type.equals(CardType.NUMBER) && current.getNumber()==number)
                ||(type.equals(CardType.DRAW2) ||type.equals(CardType.REVERSE)||type.equals(CardType.SKIP))
                &&type.equals(current.getType())){
            return true;
        }else if(color.equals(CardColor.BLACK) && !current.getColor().equals(CardColor.BLACK)){
            return true;
        }
        return false;
    }


    public JsonObject serialize(){
        JsonObject jobj = new JsonObject();
        jobj.addProperty("color", color.name());
        jobj.addProperty("type", type.name());
        jobj.addProperty("number", number);
        return jobj;
    }

    public static Card deserialize(JsonObject jobj){
        int number = jobj.get("number").getAsInt();
        if(number == -1){
            return new Card(CardColor.valueOf(jobj.get("color").getAsString()), CardType.valueOf(jobj.get("type").getAsString()));
        }
        return new Card(CardColor.valueOf(jobj.get("color").getAsString()), number);
    }

    /**
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return number == card.number && color == card.color && type == card.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type, number);
    }
}
