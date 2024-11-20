package solitaire;

public class App {
    public static void main(String[] args) { 
      GameState gameState = new GameState();
      new SolitaireGUI(gameState);

    }
}