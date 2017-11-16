import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
*  This class implements several methods relating  to the Boggle game using depth first search.
*  @author Mark Shannon
*/

public class DfsBoggle implements WordSearchGame {
   
   private String[][] board;
  
   private int width;
   private int height;
  
   private boolean[][] visited;
   
   TreeSet<String> lex = null;
   
   SortedSet<String> validWords;
   
   private static final int MAX_NEIGHBORS = 8;
   
   private List<Position> correctRoute = new ArrayList<Position>();
   
   /**
   *  Constructor for setting up a boggle game with a default board of 
   *  length 4.
   */
   public DfsBoggle( ) {
      board = new String[][]{{"E", "E", "C", "A"}, {"A", "L", "E", "P"},
         {"H", "N", "B", "O"}, {"Q", "T", "T", "Y"}};
      
      width = 4;
      height = 4;
      
      markAllUnvisited();
   }
   
   /**
   *  Takes in a file of words and puts them in a treeset.
   *  @param filename      the string of the name of the file that holds the dictionary
   */ 
   public void loadLexicon( String filename ) {
      if (filename == null) {
         throw new IllegalArgumentException();
      }
      // instantiate your lexicon object here
      lex = new TreeSet<String>();
      try {
         Scanner s = 
            new Scanner(new BufferedReader(new FileReader(new File(filename))));
         while (s.hasNext()) {
            String str = s.next();
            // add str to your lexicon object here
            lex.add(str);
            s.nextLine();
         }
      }
      catch (Exception e) {
         throw new IllegalArgumentException("Error loading word list: " + filename + ": " + e);
      }
   }
   
   /**
   *  Assigns strings to positions on an NxN board for boggle.
   *  @param letterArray      string array of letters that is the board
   */
   public void setBoard(String[] letterArray) {
   
      if (letterArray == null || Math.sqrt(letterArray.length) % 1 != 0) {
         throw new IllegalArgumentException();
      }
   
      int boardLength = (int) Math.sqrt(letterArray.length);
      width = boardLength;
      height = boardLength;
      
      board = new String[boardLength][boardLength];
      for (int row = 0; row < boardLength; row++) {
         for (int col = 0; col < boardLength; col++) {
            board[row][col] = letterArray[row * boardLength + col];
         }
         
      }
      
      markAllUnvisited();
   }
   
   /**
   *  Returns what is in the board as a string.
   *  @return boardString     A string representation of what's in the board positions
   */
   public String getBoard() {
      String boardString = "";
       
      for (int i = 0; i < width; i++) {
         for (int j = 0; j < height; j++) {
            if (i == width - 1 && j == height - 1) {
               boardString += board[i][j];
            }
            else {
               boardString += board[i][j] + ", ";
            }
         }
      }
      
      return boardString;
   }
   
   /**
   * Gets all words that satisfy the rules of Boggle on the board
   * @param minimumWordLength    int that defines the required word length
   * @return java.util.SortedSet which contains all the words of minimum length
   *     found on the game board and in the lexicon.
   */
   public SortedSet<String> getAllValidWords( int minimumWordLength) {
   
      if (minimumWordLength < 1) {
         throw new IllegalArgumentException();
      }
     
      if (lex == null) {
         throw new IllegalStateException();
      }
      
      markAllUnvisited();
      validWords = new TreeSet<String>();
      Position start;
      
      for (int i = 0; i < board.length; i++) {
         for (int j = 0; j < board.length; j++) {
            markAllUnvisited();
            start = new Position(i,j);
            dfs(board[start.x][start.y], start, minimumWordLength); 
         }
      }
      
      return validWords;
   }
   
   /**
   * recursive depth first search to get all valid words.
   * @param word String containing the current word from 
   *     the positions that you've traversed so far
   * @param p Your current position on the board
   * @param minWordLength required length of a word
   */
   private void dfs(String word,Position p, int minWordLength) {
      String wordConcat = word;
     
      
      if (!isValidPrefix(word)) {
         return;
      }
      // true if all neighbors have been visited, no where else to go
      if (isVisited(p)) {
         return;
      }
      
      if (word.length() >= minWordLength && isValidWord(word)) {
         validWords.add(word);
      }
     // true if you have found an actual word, 
      
      
      visit(p);
      
      // send each of the neighbors of the current position
      for (Position neighbor : p.neighbors()) {
         // recursive call, 
         dfs( wordConcat + board[neighbor.x][neighbor.y], neighbor, minWordLength);
         
         // unvisit to clean up as I backtrack (come up out of recursion)
      }
      
      unvisit(p);
      
   }
   /**
   * Will calculate the score of a group of words based on a set of words.
   * @param words SortedSet of Strings, the group of words to calculate the score
   * @param minimumWordLength minimum required word length 
   * @return total score of a set of words
   */
   
   public int getScoreForWords(SortedSet<String> words, int minimumWordLength) {
      int score = 0;
      SortedSet<String> allValidWords = getAllValidWords(minimumWordLength);
      for (String element : words) {
         for (String validWord : allValidWords) {
            if (element == validWord && element.length() >= minimumWordLength) {
               score = 1 + element.length() - minimumWordLength;
            }
         }
      }
      
      return score;
   }
   
   /**
   * Checks if a word is in the lexicon.
   * @param wordToCheck String that you're checking 
   * @return boolean if word is in lexicon or not
   */
   public boolean isValidWord( String wordToCheck) {
      
      if (wordToCheck == null) {
         throw new IllegalArgumentException();
      }
      
      if (lex == null) {
         throw new IllegalStateException();
      }
      if (lex.contains(wordToCheck.toLowerCase()) || lex.contains(wordToCheck.toUpperCase())) {
         return true;
      }
      return false;
   }
   
   /**
   * Checks if a string is part of the beginning of a word (prefix).
   * @param  prefixToCheck String that is being checked 
   * @return boolean if string is a valid prefix or not
   */
   public boolean isValidPrefix(String prefixToCheck) {
      if ( prefixToCheck == null) {
         throw new IllegalArgumentException();
      }
      
      if (lex == null) {
         throw new IllegalStateException();
      }
      
      if (isValidWord(prefixToCheck.toLowerCase())) {
         return true;
      }
      if (lex.ceiling(prefixToCheck.toLowerCase()) == null) {
         return false;
      }
      
      return lex.ceiling(prefixToCheck.toLowerCase()).startsWith(prefixToCheck.toLowerCase());
   }
   
   /**
   * Locates for a word on a boggle board.
   * @param wordToCheck String that you're looking for on the board
   * @return List of integers containing the positions of the word if found
   */
   public List<Integer> isOnBoard(String wordToCheck) {
      
      if (wordToCheck == null) {
         throw new IllegalArgumentException();
      }
      
      if (lex == null) {
         throw new IllegalStateException();
      }  
      
      List<Position> route = new ArrayList<Position>();
      
      Position start;
      for (int i = 0; i < board.length; i++) {
         for (int j = 0; j < board.length; j++) {
            if (board[i][j].charAt(0) == wordToCheck.charAt(0)) {
               markAllUnvisited();
               start = new Position(i,j);
               locateWord(board[start.x][start.y], start, route, wordToCheck); 
            }
         }
      } 
      
      List<Integer> positionToLocation = new ArrayList<Integer>();
      for (Position element : correctRoute) {
         positionToLocation.add(element.x * board.length + element.y);
      }
      
      correctRoute.clear();
      return positionToLocation;
   } 
   
   /**
   * Recursive depth first search method to find a word.
   */ 
   public void locateWord(String currentWord,Position p, List<Position> route, String wordToCheck) {
      if (!(isValidPrefix(currentWord))) {
         return;
      }
      if (isVisited(p) || currentWord.length() > wordToCheck.length()) {
         return;
      }
      visit(p);
      route.add(p);
      
      if (currentWord.equals(wordToCheck) && correctRoute.size() != wordToCheck.length()) {
         correctRoute.addAll(route);
         return;
      }
      for (Position neighbor : p.neighbors()) {
         // recursive call, 
         locateWord( currentWord + board[neighbor.x][neighbor.y], neighbor, route, wordToCheck);
         
         // unvisit to clean up as I backtrack (come up out of recursion)
      }
      
      unvisit(p);
      route.remove(route.size() - 1);
      
   }
   
   /**
   *  Makes every boolean position on the boggle board false.
   *  Everything is unvisited
   */
   public void markAllUnvisited() {
      visited = new boolean[width][height];
      for (boolean[] row : visited) {
         Arrays.fill(row, false);
      }
   
   }
   
   /*
   *  Data class for the boggle positions
   */
   private class Position {
      int x;
      int y;
   
      /** Constructs a Position with coordinates (x,y). */
      public Position(int x, int y) {
         this.x = x;
         this.y = y;
      }
   
      /** Returns a string representation of this Position. */
      @Override
      public String toString() {
         return "(" + x + ", " + y + ")";
      }
   
      /** Returns all the neighbors of this Position. */
      public Position[] neighbors() {
         Position[] nbrs = new Position[MAX_NEIGHBORS];
         int count = 0;
         Position p;
         // generate all eight neighbor positions
         // add to return value if valid
         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               if (!((i == 0) && (j == 0))) {
                  p = new Position(x + i, y + j);
                  if (isValid(p)) {
                     nbrs[count++] = p;
                  }
               }
            }
         }
         return Arrays.copyOf(nbrs, count);
      }
   }

   /**
    * Is this position valid in the search area.
    */
   private boolean isValid(Position p) {
      return (p.x >= 0) && (p.x < width) && (p.y >= 0) && (p.y < height);
   }

   /**
    * Has this valid position been visited.
    */
   private boolean isVisited(Position p) {
      return visited[p.x][p.y];
   }

   /**
    * Mark this valid position as having been visited.
    */
   private void visit(Position p) {
      visited[p.x][p.y] = true;
   }

   private void unvisit(Position p) {
      visited[p.x][p.y] = false;
   }
   
}