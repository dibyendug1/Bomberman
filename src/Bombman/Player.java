package Bombman;

import java.util.ArrayList;

public class Player {

  int[][] board = new int[MAX_ROW][MAX_COL];
  // Our team identity
  int identity = api_whoami() == GRID_LEFT ? GRID_LEFT : GRID_RIGHT;
  final int opponent = api_whoami() == GRID_LEFT ? GRID_RIGHT : GRID_LEFT;
  // playerOne is our team
  int[][] playerOne = new int[NUM_PLAYER][2];
  int[][] playerTwo = new int[NUM_PLAYER][2];
  static ArrayList<Bomb> bombList = new ArrayList<>();
  // four possible move direction for miniman
  final int[][] moveDirections = { { 1, 0 }, { -1, 0 }, { 0, -1 }, { 0, 1 } };
  // if bomb present at radius 3
  final int[][] bombZone =
      { { 1, 0 }, { 2, 0 }, { 3, 0 }, { -1, 0 }, { -2, 0 }, { -3, 0 },
          { 0, -1 }, { 0, -2 }, { 0, -3 }, { 0, 1 }, { 0, 2 }, { 0, 3 } };
  // if bomb present at radius 2
  final int[][] bombArea =
      { { 1, 0 }, { 2, 0 }, { -1, 0 }, { -2, 0 }, { 0, -1 }, { 0, -2 },
          { 0, 1 }, { 0, 2 } };

  int num_playerOne = 0;
  int num_playerTwo = 0;

  public void play() {
    num_playerOne = 0;
    num_playerTwo = 0;
    decreseBombTime();
    getBoardInfo(board, identity, playerOne, playerTwo);
    ArrayList<Move> movesPlayerOne = new ArrayList<>();
    ArrayList<Move> movesPlayerTwo = new ArrayList<>();
    genAllMoves(movesPlayerOne, playerOne, identity);
    genAllMoves(movesPlayerTwo, playerTwo, opponent);

    for (Move move : movesPlayerOne) {
      makeMove(move);
    }

    clearMemory();
  }

  public void getBoardInfo(int[][] board, int identity, int[][] playerOne,
      int[][] playerTwo) {
    int p1Count = 0;
    int p2Count = 0;
    for (int i = 0; i < MAX_ROW; i++) {
      for (int j = 0; j < MAX_COL; j++) {
        int cell = api_getGridInfo(i, j);
        board[i][j] = cell;
        if (cell == identity) {
          playerOne[p1Count][0] = i;
          playerOne[p1Count][1] = j;
          num_playerOne++;
          p1Count++;
        } else if (cell == opponent) {
          playerTwo[p2Count][0] = i;
          playerTwo[p2Count][1] = j;
          num_playerTwo++;
          p2Count++;
        }
        if (cell == GRID_BOMB1 || cell == GRID_BOMB2) {
          if (!isBombPresent(i, j)) {
            Bomb b = new Bomb();
            b.setType(cell);
            b.setRow(i);
            b.setCol(j);
            bombList.add(b);
          }
        }
      }
    }
  }

  private void genAllMoves(ArrayList<Move> moves, int[][] players, int who) {
    for (int playerPos = 0; playerPos < players.length; playerPos++) {
      int[] player = players[playerPos];
      for (int i = 0; i < moveDirections.length; i++) {
        int[] direction = moveDirections[i];
        if (isValidMove(player, direction)) {
          Move mv = new Move();
          mv.setCurr_row(player[0]);
          mv.setCurr_col(player[1]);
          mv.setNext_row(direction[0] + player[0]);
          mv.setNext_col(direction[1] + player[1]);
          mv.setDirection(i);
          mv.setPlayer(who);
          mv.setPlayerPos(playerPos);
          int bomb = 0;
          if (!isOurPlayerInBombRadius(player) && hasEscapeRuote(player, i)) {
            if (isOpponentInRadius4(player)) {
              bomb = GRID_BOMB1;
            }
            if (isOpponentInRadius5(player)) {
              bomb = GRID_BOMB2;
            }
            mv.setBomb(bomb);
            moves.add(mv);
          }
        }
      }
    }
  }

  private void makeMove(Move move) {
    if (move.getPlayer() == identity) {
      playerOne[move.getPlayerPos()][0] = move.getNext_row();
      playerOne[move.getPlayerPos()][1] = move.getNext_col();
      board[move.getNext_row()][move.getNext_col()] = identity;
      board[move.getCurr_row()][move.getCurr_col()] = move.getBomb();
    }
  }

  private int genCost() {
    return 0;
  }

  private boolean isBombPresent(int i, int j) {
    for (Bomb b : bombList) {
      if (b.getRow() == i && b.getCol() == j) {
        return true;
      }
    }
    return false;
  }

  private void decreseBombTime() {
    for (Bomb b : bombList) {
      b.decrTime();
      if (b.getTime() == 0) {
        bombList.remove(b);
      }
    }
  }

  private boolean hasEscapeRuote(int[] player, int direction) {
    if (direction == DIR_UP) {
      if (board[player[0] + 1][player[1] + 1] == GRID_EMPTY
          || board[player[0] - 1][player[1] + 1] == GRID_EMPTY) {
        return true;
      }
    } else if (direction == DIR_DOWN) {
      if (board[player[0] + 1][player[1] - 1] == GRID_EMPTY
          || board[player[0] - 1][player[1] - 1] == GRID_EMPTY) {
        return true;
      }
    } else if (direction == DIR_LEFT) {
      if (board[player[0] - 1][player[1] + 1] == GRID_EMPTY
          || board[player[0] - 1][player[1] - 1] == GRID_EMPTY) {
        return true;
      }
    } else if (direction == DIR_RIGHT) {
      if (board[player[0] + 1][player[1] + 1] == GRID_EMPTY
          || board[player[0] + 1][player[1] - 1] == GRID_EMPTY) {
        return true;
      }
    }
    return false;
  }

  private boolean isOurPlayerInBombRadius(int[] player) {
    for (int[] cell : bombArea) {
      if (board[player[0] + cell[0]][player[1] + cell[1]] == identity) {
        return true;
      }
    }
    return false;
  }

  private boolean isOpponentInRadius5(int[] player) {
    for (int i = -5; i <= 5; i++) {
      int absI = i < 0 ? i * -1 : i;
      if (i == -5 || i == 5) {
        if (board[player[0] + i][player[1]] == opponent) {
          return true;
        }
      } else {
        int j = -5 + absI;
        if (board[player[0] + i][player[1] + j] == opponent) {
          return true;
        }
        j = 5 - absI;
        if (board[player[0] + i][player[1] + j] == opponent) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isOpponentInRadius4(int[] player) {
    for (int i = -4; i <= 4; i++) {
      int absI = i < 0 ? i * -1 : i;
      for (int j = -4 + absI; j <= 4 - absI; j++) {
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
      // no need to check the cell which is other side of the fence
      if (board[cell[0]][cell[1]] == GRID_FENCE) {
        if (cell[0] == -1 || cell[0] == 1 || cell[1] == -1 || cell[1] == 1) {
          i += 3;
        }
      }
      if (board[cell[0]][cell[1]] == GRID_FENCE) {
        if (cell[0] == -2 || cell[0] == 2 || cell[1] == -2 || cell[1] == 2) {
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

  private void clearMemory() {
    board = null;
    playerOne = null;
    playerTwo = null;
    bombList.clear();
    System.gc();
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