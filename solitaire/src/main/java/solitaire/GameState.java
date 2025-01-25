package solitaire;

import java.util.ArrayList;
import java.util.Stack;

public class GameState {
    private Stack<Card> deck; // Full deck of cards
    private Stack<Card>[] gamePiles; // Seven piles on the tableau
    private Stack<Card> visibleCards; // Stack for visible cards
    private Stack<Card> discardedCards; // Discard pile
    private Stack<Card>[] foundationPiles; // Four foundation piles

    @SuppressWarnings("unchecked")
    public GameState() {
        // Initialize the game state
        deck = new Stack<>();
        gamePiles = new Stack[7]; // Array of 7 stacks
        visibleCards = new Stack<>();
        discardedCards = new Stack<>();

        // Initialize each game pile
        for (int i = 0; i < gamePiles.length; i++) {
            gamePiles[i] = new Stack<>();
        }
        foundationPiles = new Stack[4];
        for (int i = 0; i < foundationPiles.length; i++) {
            foundationPiles[i] = new Stack<>();
        }

        initializeDeck();
        shuffleDeck();
        dealInitialCards();
    }

    //REPLACE THE FOLLOWING 4 functions with your code from part 2

    // Creates a full deck of cards with all combinations of suits and ranks
    private void initializeDeck() {
      //USE IMPLEMENTATION FROM PART 2
      System.out.println("TRYING TO CREATE DECK");
        for (Suit suit : Suit.values()){
            for (Rank rank : Rank.values()){
                Card newCard = new Card(suit,rank);
                System.out.println(newCard);
                deck.push(newCard);
            }
        }
        System.out.println("DONE CREATING DECK");

    }

    // Shuffles the deck
    private void shuffleDeck() {
        java.util.Collections.shuffle(deck);
    }

    // Deals cards to the 7 game piles
    private void dealInitialCards() {
        //USE IMPLEMENTATION FROM PART 2
        for (int i = 0; i < 7; i++){
            for (int in = 0; in < i+1; in++){
                gamePiles[i].push(deck.pop());
            }
            gamePiles[i].peek().flip();
        }
    }

    // Draws up to three cards from the deck into visibleCards
    public void drawFromDeck() {
        //USE IMPLEMENTATION FROM PART 2
        discardCards();
        for(int i = 0; i<3; i++){
            visibleCards.push(deck.pop());
            visibleCards.peek().flip();
        }
        if(deck.size()==0){
            for(int i = discardedCards.size(); i>0;i--){
                deck.push(discardedCards.pop());
            }
        }
    }

    public void discardCards() {
        //takes whatever cards are remaining in the visibleCards pile and moves them to the discardPiles
        while (!visibleCards.isEmpty()) {
            discardedCards.push(visibleCards.pop());
        }
    }

    // new methods from part 3

    public boolean canCardMove(Card card, int toPile){
        /*a card can be moved from the visible cards to a pile if 
            A) The card is the opposite color and its rank is ONE smaller than the card it will be placed on
            B) The pile is empty and the card is a King
        */
        Card other = gamePiles[toPile].peek();
        if (gamePiles.length == 0 && card.getRank() == Rank.KING){
            return true;
        }
        if (card.getColor() != other.getColor()){
            if (card.getRank().ordinal()+1 == other.getRank().ordinal()){
                return true;
            }
        }

        return false;
    }
    // attempts to move top card from visible card stack to the toPileIndex
    // returns true if successful and false if unsuccessful
    public boolean moveCardFromVisibleCardsToPile(int toPileIndex) {
        /* 
            If a card can be moved, it should be popped from the visible cards pile and pushed to the pile it is added to
            hints: use peek() and ordinal() to determine whether or not a card can be moved. 
            USE the method you just made, canCardMove
        */
        System.out.println("inside move from visible card to pile, canCardMove is " + canCardMove(visibleCards.peek(), toPileIndex));
        if (canCardMove(visibleCards.peek(), toPileIndex) == true){
            gamePiles[toPileIndex].push(visibleCards.pop());
            return true;
        }
        return false;
    }

    // Move a card from one pile to another
    public boolean moveCards(int fromPileIndex, int cardIndex, int toPileIndex) {
        Stack<Card> fromPile = gamePiles[fromPileIndex];

        // Create a sub-stack of cards to move
        ArrayList<Card> cardsToMove = new ArrayList<>(fromPile.subList(cardIndex, fromPile.size()));

        Card bottomCard = cardsToMove.get(0); // the bottom card to be moved

        // Check if bottomCard can be moved to the toPile
        // if we can move the cards, add cardsToMove to the toPile and remove them from the fromPile
        // Then, flip the next card in the fromPile stack
        if (canCardMove(bottomCard, toPileIndex)){
            gamePiles[toPileIndex].push(bottomCard);
            fromPile.peek().flip();
            return true;
        }
        //return true if successful, false if unsuccessful

        return false;
    }
    private boolean canMoveToFoundation(Card card, int foundationIndex){
        //The foundation piles are the 4 piles that you have to build to win the game. 
        //In order for a card to be added to the pile, it needs to be one larger than the 
        //current top card of the foundation pile. It needs to be the same suit. 
        //If the foundation pile is empty, the new card must be an ace

        //This method should return true if a card can be moved to the foundation, and false otherwise. 
        
        //hint: another good time to use peek() and ordinal()
        if (gamePiles[foundationIndex].isEmpty()) {
            // If the foundation pile is empty, the card must be an Ace
            return card.getRank() == Rank.ACE;
        } else {
            Card topCard = gamePiles[foundationIndex].peek();
    
            // Check if the card is one rank higher and of the same suit as the top card
            return card.getSuit() == topCard.getSuit() && 
                   card.getRank().ordinal() == topCard.getRank().ordinal() + 1;
        }
    }
    public boolean moveToFoundation(int fromPileIndex, int foundationIndex) {
        //check if we can move the top card of the fromPile to the foundation at foundationIndex
        
        //remember to flip the new top card if it is face down

        //return true if successful, false otherwise
            if (gamePiles[fromPileIndex].isEmpty()) {
        return false; // Cannot move from an empty pile
    }

    // Get the top card from the fromPile
    Card topCard = gamePiles[fromPileIndex].peek();

    // Check if we can move the top card to the foundation
    if (canMoveToFoundation(topCard, foundationIndex)) {
        // Move the card to the foundation pile
        gamePiles[foundationIndex].push(gamePiles[fromPileIndex].pop());

        // Flip the new top card if it is face down
        if (!gamePiles[fromPileIndex].isEmpty() && !gamePiles[fromPileIndex].peek().isFaceUp()) {
            gamePiles[fromPileIndex].peek().flip(); // Assuming flip() method changes the card's face-up status
        }

        return true; // Move was successful
    }
        return false;
    }

    public boolean moveToFoundationFromVisibleCards(int foundationIndex) {
        //similar to the above method, 
        //move the top card from the visible cards to the foundation pile with index foundationIndex if possible
    
        //return true if successful, false otherwise. 
            if (visibleCards.isEmpty()) {
        return false; // Cannot move from an empty pile
    }

    // Get the top card from the visibleCards pile
    Card topCard = visibleCards.peek();

    // Check if we can move the top card to the foundation
    if (canMoveToFoundation(topCard, foundationIndex)) {
        // Move the card to the foundation pile
        gamePiles[foundationIndex].push(visibleCards.pop());

        return true; // Move was successful
    }
        return false;
    }

    

    // Don't change this, used for testing
    public void printState() {
        System.out.println("Deck size: " + deck.size());

        System.out.print("Visible cards: ");
        if (visibleCards.isEmpty()) {
            System.out.println("None");
        } else {
            for (Card card : visibleCards) {
                System.out.print(card + " ");
            }
            System.out.println();
        }

        System.out.println("Discarded cards: " + discardedCards.size());

        System.out.println("Game piles:");
        for (int i = 0; i < gamePiles.length; i++) {
            System.out.print("Pile " + (i + 1) + ": ");
            if (gamePiles[i].isEmpty()) {
                System.out.println("Empty");
            } else {
                for (Card card : gamePiles[i]) {
                    System.out.print(card + " ");
                }
                System.out.println();
            }
        }
    }

    // getters
    public Stack<Card> getGamePile(int index) {
        return gamePiles[index];
    }

    public Stack<Card> getFoundationPile(int index) {
        return foundationPiles[index];
    }

    public Stack<Card> getDeck() {
        return deck;
    }

    public Stack<Card> getVisibleCards() {
        return visibleCards;
    }
}