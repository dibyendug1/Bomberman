package Bombman;

import java.util.ArrayList;

public class Player {

  int[][] board = new int[MAX_ROW][MAX_COL];
  String identity = api_whoami() == GRID_LEFT ? "left" : "right";
  final int opponent = api_whoami() == GRID_LEFT ? GRID_RIGHT : GRID_LEFT;
  int[][] playerOne = new int[NUM_PLAYER][2];
  int[][] playerTwo = new int[NUM_PLAYER][2];
  ArrayList<int[]> bomb1 = new ArrayList<>();
  ArrayList<int[]> bomb2 = new ArrayList<>();
  final int[][] moveDirections = { { 1, 0 }, { -1, 0 }, { 0, -1 }, { 0, 1 } };
  final int[][] bombZone =
      { { 1, 0 }, { 2, 0 }, { -1, 0 }, { -2, 0 }, { 0, -1 }, { 0, -2 },
          { 0, 1 }, { 0, 2 } };
  static int num_playerOne = 0;
  static int num_playerTwo = 0;

  public void play() {
    getBoardInfo(board, identity, playerOne, playerTwo);
    ArrayList<Move> moves = new ArrayList<>();
    genAllMoves(moves);
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

  private void genAllMoves(ArrayList<Move> moves) {
    for (int[] player : playerOne) {
      for (int i = 0; i < moveDirections.length; i++) {
        int[] direction = moveDirections[i];
        if (isValidMove(player, direction)) {
          Move mv = new Move();
          mv.setCurr_row(player[0]);
          mv.setCurr_col(player[1]);
          mv.setNext_row(direction[0] + player[0]);
          mv.setNext_col(direction[1] + player[1]);
          mv.setDirection(i);
          mv.setPlayer(identity.equals("right") ? PLAYER_R : PLAYER_L);
          int bomb = 0;
          if (isOpponentInRadius2(player)) {
            bomb = GRID_BOMB1;
          }
          if (isOpponentInRadius2_4(player)) {
            bomb = GRID_BOMB2;
          }
          mv.setBomb(bomb);
          moves.add(mv);
        }
      }
    }
  }

  private boolean isOpponentInRadius2_4(int[] player) {
    for (int i = -4; i <= 4; i++) {
      for (int j = -4; j <= 4; j++) {
        if (i >= -2 && i <= 2 && j >= -2 && j <= 2) {
          continue;
        }
        if (board[player[0] + i][player[1] + j] == opponent) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isOpponentInRadius2(int[] player) {
    for (int i = -2; i <= 2; i++) {
      for (int j = -2; j <= 2; j++) {
        if (i == 0 && j == 0) {
          continue;
        }
        if (board[player[0] + i][player[1] + j] == opponent) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isValidMove(int[] player, int[] direction) {
    int row = player[0] + direction[0];
    int col = player[1] + direction[1];
    if (row < 0 || row >= MAX_ROW) {
      return false;
    }
    if (col < 0 || col >= MAX_COL) {
      return false;
    }
    /*
    if (board[row][col] == GRID_FENCE || board[row][col] == GRID_BOMB1
        || board[row][col] == GRID_BOMB2 || board[row][col] == GRID_LEFT
        || board[row][col] == GRID_RIGHT) {
    }*/

    if (board[row][col] == GRID_EMPTY) {
      if (isBombZone()) {
        return false;
      }
      return true;
    }
    return false;
  }

  private boolean isBombZone() {
    for (int i = 0; i < bombZone.length; i++) {
      int[] cell = bombZone[i];
      if (board[cell[0]][cell[1]] == GRID_FENCE) {
        if (cell[0] == -1 || cell[0] == 1 || cell[1] == -1 || cell[1] == 1) {
          i += 2;
        }
      }
      if (board[cell[0]][cell[1]] == GRID_BOMB1
          || board[cell[0]][cell[1]] == GRID_BOMB2) {
        return true;
      }
    }
    return false;
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

  public int getDirection() {
    return direction;
  }

  public void setDirection(int direction) {
    this.direction = direction;
  }

  int direction;

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