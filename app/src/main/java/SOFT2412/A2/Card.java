package SOFT2412.A2;
import java.util.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Card {
    private String name;
    private String number;
    private static List<Card> cards = new ArrayList<Card>();
    // A JSONArray to store card details for reading and writing to JSON file
    private static JSONArray cardArray;

    public Card(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return this.name;
    }

    public String getNumber() {
        return this.number;
    }

    public static List<Card> getCards() {
        return cards;
    }

    public static JSONArray getCardArray() {
        return cardArray;
    }

    public static void loadCards() {
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader("./src/main/resources/creditCards.json"));
            cardArray = (JSONArray) object;
            for (Object o : cardArray) {
                JSONObject entry = (JSONObject) o;
                String name = (String) entry.get("name");
                String number = (String) entry.get("number");
                Card card = new Card(name, number);
                cards.add(card);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }

    // Adds a card to saved card list and the json file
    @SuppressWarnings("unchecked")
    public static void updateCards(Card card) {
        cards.add(card);
        // Adds card to cardArray
        JSONObject newCard = new JSONObject();
        newCard.put("name", card.getName());
        newCard.put("number", card.getNumber());
        cardArray.add(newCard);
        // Writes card to creditCards.json
        try (FileWriter file = new FileWriter("./src/main/resources/creditCards.json")) {
            file.write(Card.getCardArray().toJSONString());
            file.flush();
            file.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean checkCardDetails(String name, String number) {
        for (Card c : cards) {
            if ((name.equals(c.getName())) && (number.equals(c.getNumber()))) {
                return true;
            }
        }
        return false;
    }


    public static void defaultCards() {
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader("./src/main/resources/stableCards.json"));
            cardArray = (JSONArray) object;
            for (Object o : cardArray) {
                JSONObject entry = (JSONObject) o;
                String name = (String) entry.get("name");
                String number = (String) entry.get("number");
                Card card = new Card(name, number);
                cards.add(card);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }
}
