package TicTacToeLLD;
/**
 * Design a Tic-Tac-Toe game using object-oriented design.
 * 
 * The game should support:
 * 
 * A configurable board size N × N
 * Two players
 * Players taking turns placing their symbols (X / O)
 * Detecting when a player wins
 * Detecting a draw
 * Preventing invalid moves
 * Starting a new game
 * 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * Entities:
 * - Board
 * - Player
 * - Piece
 * - GameManager
 */

enum PieceType{
    X,
    O
}

class Piece{
    private PieceType picPieceType;

    public Piece(PieceType pieceType){
        this.picPieceType=pieceType;
    }

    public PieceType getPicPieceType() {
        return picPieceType;
    }
}

class Player{
    private Piece piece;
    private boolean isWinner=false;

    public Player(Piece piece){
        this.piece=piece;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setWinner(boolean isWinner) {
        this.isWinner = isWinner;
    }

    public boolean getIsWinner(){
        return isWinner;
    }

    @Override
    public String toString(){
        return piece.getPicPieceType().toString();
    }
}

class Board{
    Piece board[][];
    int size;

    public Board(int size){
        this.size=size;
        board=new Piece[size][size];
    }

    public boolean isBoardFull(){
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(board[i][j]==null){
                    return false;
                }
            }
        }
        return true;
    }

    public void printBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != null) {
                    System.out.print(board[i][j].getPicPieceType()+" ");
                }
                else{
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
    }

    public boolean putPieceInBoard(int row, int col, Piece piece){
        if(row>=size || col>=size){
            System.out.println("Invalid row or column input");
            return false;
        }
        if(board[row][col]!=null){
            System.out.println("Position already occupied");
            return false;
        }

        board[row][col]=piece;
        return true;
    }

    public boolean getWinner(int row,int col, Piece piece){
        boolean rowMatch=true;
        boolean colMatch=true;
        boolean diagMatch=true;
        boolean antiDiagMatch=true;

        for(int i=0;i<size;i++){
            if(board[row][i]==null || board[row][i]!=piece){
                rowMatch=false;
                break;
            }
        }

        for (int i = 0; i < size; i++) {
            if (board[i][col] == null || board[i][col] != piece) {
                colMatch = false;
                break;
            }
        }

        for (int i = 0; i < size; i++) {
            if (board[i][i] == null || board[i][i] != piece) {
                diagMatch = false;
                break;
            }
        }

        for (int i = 0; i < size; i++) {
            if (board[i][size-i-1] == null || board[i][size - i - 1] != piece) {
                antiDiagMatch = false;
                break;
            }
        }
        
        return (rowMatch || colMatch || diagMatch || antiDiagMatch);
    }
}

class GameManager{
    Board board;

    List<Player> players;

    Scanner sc;

    public GameManager(int size, List<Player> players){
        board = new Board(size);
        this.players = players;
        sc=new Scanner(System.in);
    }

    private Integer[] getUserInputRowAndColForPiece() {
        String inputLine = sc.nextLine();

        Integer[] splitInput = Arrays
                .stream(inputLine.split(" "))
                .map((item) -> Integer.parseInt(item)).toArray(Integer[]::new);

        if (splitInput.length != 2) {
            System.out.println("Invalid input: Enter row and column, 2 numbers");
            return null;
        }

        return splitInput;

    }

    void startGame(){

        int currentPlayerIndex=0;

        boolean noWinner=true;

        while(noWinner){
            board.printBoard();

            Player currentPlayer=players.get(currentPlayerIndex);

            System.out.println("Enter row and column for player "+currentPlayer);

            Integer userInput[]=getUserInputRowAndColForPiece();

            if(userInput==null){
                continue;
            }

            int row=userInput[0];
            int col=userInput[1];

            boolean isPiecePlaced=board.putPieceInBoard(row, col,currentPlayer.getPiece());

            if(!isPiecePlaced){
                continue;
            }

            boolean isGameWon=board.getWinner(row, col, currentPlayer.getPiece());

            if(isGameWon){
                System.out.println("Winner of the game is "+ currentPlayer);
                currentPlayer.setWinner(true);
                break;
            }

            boolean isBoardFull = board.isBoardFull();

            if (isBoardFull) {
                System.out.println("Tie");
                break;
            }

            currentPlayerIndex++;
            currentPlayerIndex%=players.size();

        }
    }

}


public class TicTacToe{
    public static void main(String[] args) {
        Piece xPiece=new Piece(PieceType.X);
        Piece oPiece=new Piece(PieceType.O);

        Player first=new Player(xPiece);
        Player second=new Player(oPiece);

        List<Player> players=List.of(first,second);

        GameManager manager=new GameManager(3, players);

        manager.startGame();
    }
}