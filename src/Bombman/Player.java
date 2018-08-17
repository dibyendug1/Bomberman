package Bombman;

import java.util.ArrayList;

public class Player {

  int[][] board = new int[MAX_ROW][MAX_COL];
  String identity = api_whoami() == GRID_LEFT ? "left" : "right";
  int[][] playerOne = new int[NUM_PLAYER][2];
  int[][] playerTwo = new int[NUM_PLAYER][2];
  ArrayList<int[]> bomb1 = new ArrayList<>();
  ArrayList<int[]> bomb2 = new ArrayList<>();
  ArrayList<Move> moves = new ArrayList<>();
  static int num_playerOne = 0;
  static int num_playerTwo = 0;

  public void play() {
    getBoardInfo(board, identity, playerOne, playerTwo);
  }

  public void getBoardInfo(int[][] board, String identity, int[][] playerOne,
      int[][] playerTwo) {
    int p1Count = 0;
    int p2Count = 0;
    for (int i = 0; i < MAX_ROW; i++) {
      for (int j = 0; j < MAX_COL; j++) {
        int cell = api_getGridInfo(i, j);
        board[i][j] = cell;
        if (identity.equals("left")) {
          if (cell == GRID_LEFT) {
            playerOne[p1Count][0] = i;
            playerOne[p1Count][1] = j;
            num_playerOne++;
            p1Count++;
          } else if (cell == GRID_RIGHT) {
            playerTwo[p2Count][0] = i;
            playerTwo[p2Count][1] = j;
            num_playerTwo++;
            p2Count++;
          }
        } else if (identity.equals("right")) {
          if (cell == GRID_RIGHT) {
            playerOne[p1Count][0] = i;
            playerOne[p1Count][1] = j;
            num_playerOne++;
            p1Count++;
          } else if (cell == GRID_LEFT) {
            playerTwo[p2Count][0] = i;
            playerTwo[p2Count][1] = j;
            num_playerTwo++;
            p2Count++;
          }
        }
        if (cell == GRID_BOMB1) {
          bomb1.add(new int[] { i, j });
        } else if (cell == GRID_BOMB2) {
          bomb2.add(new int[] { i, j });
        }
      }
    }
  }

  private void genAllMoves() {
    for (int[] player : playerOne) {

    }

  }

  private void makeMove() {
  }

  private int genCost() {
    return 0;
  }

  public static native int api_whoami();

  public static native int api_getGridInfo(int row, int col);

  public static native void api_walk(int row, int col, int direction,
      int leave_bomb);

  public static native int api_getSelfScore();

  public static native int api_getOppoScore();

  public static final int MAX_ROW = 10;
  public static final int MAX_COL = 10;
  public static final int NUM_PLAYER = 9;

  public static final int GRID_EMPTY = 0;
  public static final int GRID_LEFT = 101;
  public static final int GRID_RIGHT = 102;
  public static final int GRID_BOMB1 = 201;
  public static final int GRID_BOMB2 = 202;
  public static final int GRID_FENCE = 300;

  public static final int PLAYER_L = GRID_LEFT;
  public static final int PLAYER_R = GRID_RIGHT;

  public static final int DIR_UP = 0;
  public static final int DIR_DOWN = 1;
  public static final int DIR_LEFT = 2;
  public static final int DIR_RIGHT = 3;
}

class Move {
  int player;
  int curr_row;
  int curr_col;
  int next_row;
  int next_col;
  int bomb;

  public int getPlayer() {
    return player;
  }

  public void setPlayer(int player) {
    this.player = player;
  }

  public int getCurr_row() {
    return curr_row;
  }

  public void setCurr_row(int curr_row) {
    this.curr_row = curr_row;
  }

  public int getCurr_col() {
    return curr_col;
  }

  public void setCurr_col(int curr_col) {
    this.curr_col = curr_col;
  }

  public int getNext_row() {
    return next_row;
  }

  public void setNext_row(int next_row) {
    this.next_row = next_row;
  }

  public int getNext_col() {
    return next_col;
  }

  public void setNext_col(int next_col) {
    this.next_col = next_col;
  }

  public int getBomb() {
    return bomb;
  }

  public void setBomb(int bomb) {
    this.bomb = bomb;
  }
}