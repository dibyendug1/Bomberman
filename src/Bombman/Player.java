package Bombman;

import java.util.ArrayList;
import java.util.Collections;

public class Player {

  int[][] board = new int[MAX_ROW][MAX_COL];
  // Our team identity
  final int identity = api_whoami() == GRID_LEFT ? GRID_LEFT : GRID_RIGHT;
  final int opponent = api_whoami() == GRID_LEFT ? GRID_RIGHT : GRID_LEFT;
  // playerOne is our team
  ArrayList<Miniman> playerOne = new ArrayList<>();
  // list to store bomb locations
  static ArrayList<Bomb> bombList = new ArrayList<>();
  // four possible move direction for miniman
  final int[][] moveDirections = { { 1, 0 }, { -1, 0 }, { 0, -1 }, { 0, 1 } };
  // if bomb present at radius 2
  final int[][] bombArea =
      { { 1, 0 }, { 2, 0 }, { -1, 0 }, { -2, 0 }, { 0, -1 }, { 0, -2 },
          { 0, 1 }, { 0, 2 } };

  static int num_playerOne = 0;
  static int num_playerTwo = 0;
  static int mod_numPlayerOne = 0;
  static int mod_numPlayerTwo = 0;

  // define weight for different actions
  final int MOVEFROMBOMB = 1000;
  final int BOMBONE = 900;
  final int BOMBTWO = 900;
  final int SUICIDALBOMB = 700;
  final int NOBOMB = 0;
  final int MOVETOOPPONENET = 500;
  final int SELFSIDEMOVE = 100;
  final int ESCAPEROUTE_4 = 500;
  final int ESCAPEROUTE_3 = 300;
  final int ESCAPEROUTE_2 = 100;
  final int ESCAPEROUTE_1 = -100;
  final int ESCAPEROUTE_0 = -500;

  public void play() {
    num_playerOne = 0;
    num_playerTwo = 0;
    //decrease bomb time by 1
    decreaseBombTime(bombList);
    //get board information
    getBoardInfo(board, playerOne);
    ArrayList<Miniman> playerInBombArea = new ArrayList<>();
    getPlayerInBombArea(playerInBombArea, bombList);
    //set num of playes in temporary variable for internal states
    mod_numPlayerOne = num_playerOne;
    mod_numPlayerTwo = num_playerTwo;
    // lists to store moves of the playes
    ArrayList<Move> movesPlayerOne = new ArrayList<>();
    // generate moves for players
    genAllMoves(movesPlayerOne, playerOne, playerInBombArea, identity,
        num_playerOne);

    Collections.sort(movesPlayerOne);

    Move finalMove = movesPlayerOne.get(0);
    api_walk(finalMove.getCurr_row(), finalMove.getCurr_col(),
        finalMove.getDirection(), finalMove.getBomb());
    removeExplodedBomb(bombList);
  }
  //TO-DO implement select move based on timing

  // Store board informations
  public void getBoardInfo(int[][] board, ArrayList<Miniman> playerOne) {
    for (int i = 0; i < MAX_ROW; i++) {
      for (int j = 0; j < MAX_COL; j++) {
        int cell = api_getGridInfo(i, j);
        board[i][j] = cell;
        if (cell == identity) {
          playerOne.add(new Miniman(identity, i, j));
          num_playerOne++;
        } else if (cell == opponent) {
          num_playerTwo++;
        }
        if (cell == GRID_BOMB1 || cell == GRID_BOMB2) {
          if (!isBombPresent(i, j)) {
            bombList.add(new Bomb(cell, i, j));
          }
        }
      }
    }
  }

  // Get list of players in bomb explosion ares
  private void getPlayerInBombArea(ArrayList<Miniman> playerInBombArea,
      ArrayList<Bomb> bombList) {
    for (Bomb b : bombList) {
      int r = b.getRow();
      int c = b.getCol();
      for (int[] cell : bombArea) {
        int row = r + cell[0];
        int col = c + cell[1];
        if (board[row][col] == identity) {
          playerInBombArea.add(new Miniman(identity, row, col));
        }
      }
    }
  }

  // If a player in bomb explosion zone, move that player
  // Avoid going into bomb zone
  // Drop bomb if opponent is in bomb zone
  // Drop suicidal bomb if num(opponent) > num(own)
  // Move towards opponent
  // TO-DO more weight to corner players
  // TO_DO Bomb weight x num player can kill
  private void genAllMoves(ArrayList<Move> moves, ArrayList<Miniman> players,
      ArrayList<Miniman> playerInBombArea, int who, int numPlayers) {
    int num_playerInBomb = playerInBombArea.size();
    if (num_playerInBomb > 0) {
      genMoves(moves, playerInBombArea, who, num_playerInBomb, true);
    } else {
      for (int playerPos = 0; playerPos < numPlayers; playerPos++) {
        genMoves(moves, players, who, numPlayers, false);
      }
    }
  }

  private void genMoves(ArrayList<Move> moves, ArrayList<Miniman> players,
      int who, int num_playerInBomb, boolean moveFromBomb) {
    for (int playerPos = 0; playerPos < num_playerInBomb; playerPos++) {
      Miniman player = players.get(playerPos);
      for (int i = 0; i < moveDirections.length; i++) {
        int[] direction = moveDirections[i];
        if (isValidMove(player, direction)) {
          Move mv = new Move();
          mv.setCurr_row(player.getRow());
          mv.setCurr_col(player.getCol());
          mv.setNext_row(direction[0] + player.getRow());
          mv.setNext_col(direction[1] + player.getCol());
          mv.setDirection(i);
          mv.setPlayer(who);
          mv.setPlayerPos(playerPos);
          int bomb = 0;
          boolean suicidal = false;
          // get number of our and opponent player in bombradius
          // TO-DO need to modify
          int[] playerInBombCount = isOurPlayerInBombRadius(player);
          if (playerInBombCount[0] < playerInBombCount[1]) {
            if (hasEscapeRuote(player, i)) {
              if (isOpponentInRadius4(player)) {
                bomb = GRID_BOMB1;
              }
              if (isOpponentInRadius5(player)) {
                bomb = GRID_BOMB2;
              }
            } else if (playerInBombCount[1] - playerInBombCount[0] > 1) {
              if (isOpponentInRadius4(player)) {
                bomb = GRID_BOMB1;
              }
              if (isOpponentInRadius5(player)) {
                bomb = GRID_BOMB2;
              }
            }
            if (bomb != 0 && playerInBombCount[0] > 0) {
              suicidal = true;
            }
          }
          mv.setBomb(bomb);
          mv.setCost(getCost(moveFromBomb, suicidal, mv));
          moves.add(mv);
        }
      }
    }
  }

  private int getCost(boolean moveFromBomb, boolean suicidal, Move move) {
    int cost = 0;
    // Weight for saving bomb
    if (moveFromBomb) {
      cost += MOVEFROMBOMB;
    }

    // Weight for placing bomb
    switch (move.getBomb()) {
    case GRID_BOMB1:
      cost += BOMBONE;
      break;
    case GRID_BOMB2:
      cost += BOMBTWO;
      break;
    case GRID_EMPTY:
      cost += NOBOMB;
    }

    // Weight on suiciding
    if (suicidal) {
      cost += SUICIDALBOMB;
    }

    // Prefer move towards opponent
    int direction = move.getDirection();
    if (identity == PLAYER_L) {
      if (direction == DIR_RIGHT || direction == DIR_DOWN) {
        cost += MOVETOOPPONENET;
      } else {
        cost += SELFSIDEMOVE;
      }
    } else if (identity == PLAYER_R) {
      if (direction == DIR_LEFT || direction == DIR_UP) {
        cost += MOVETOOPPONENET;
      } else {
        cost += SELFSIDEMOVE;
      }
    }

    // weight based on the available path in next step
    int escapeRoute = getEscapeRouteSize(move);
    switch (escapeRoute) {
    case 0:
      cost = cost + ESCAPEROUTE_0;
      break;
    case 1:
      cost = cost + ESCAPEROUTE_1;
      break;
    case 2:
      cost = cost + ESCAPEROUTE_2;
      break;
    case 3:
      cost = cost + ESCAPEROUTE_3;
      break;
    case 4:
      cost = cost + ESCAPEROUTE_4;
    }

    return cost;
  }

  // Find number of possible moves in next step
  private int getEscapeRouteSize(Move move) {
    int row = move.getNext_row();
    int col = move.getNext_col();
    int clearRoute = 0;
    for (int[] d : moveDirections) {
      if (board[row + d[0]][col + d[1]] == GRID_EMPTY) {
        clearRoute++;
      }
    }
    return clearRoute;
  }

  // if the board contains bomb
  private boolean isBombPresent(int i, int j) {
    for (Bomb b : bombList) {
      if (b.getRow() == i && b.getCol() == j) {
        return true;
      }
    }
    return false;
  }

  // decrease bomb time
  private void decreaseBombTime(ArrayList<Bomb> tmpList) {
    int size = tmpList.size();
    for (int i = 0; i < size; i++) {
      Bomb b = tmpList.get(i);
      b.decrTime();
      if (b.getTime() < 0) {
        tmpList.remove(b);
        size--;
      }
    }
  }

  // Remove exploded bombs from the list
  private void removeExplodedBomb(ArrayList<Bomb> tmpList) {
    int size = tmpList.size();
    for (int i = 0; i < size; i++) {
      Bomb b = tmpList.get(i);
      if (b.getTime() < 0) {
        tmpList.remove(b);
        size--;
      }
    }
  }

  // If escape router present after placing bomb
  private boolean hasEscapeRuote(Miniman player, int direction) {
    int row = player.getRow();
    int col = player.getCol();
    if (direction == DIR_UP) {
      if (board[row + 1][col + 1] == GRID_EMPTY
          || board[row - 1][col + 1] == GRID_EMPTY) {
        return true;
      }
    } else if (direction == DIR_DOWN) {
      if (board[row + 1][col - 1] == GRID_EMPTY
          || board[row - 1][col - 1] == GRID_EMPTY) {
        return true;
      }
    } else if (direction == DIR_LEFT) {
      if (board[row - 1][col + 1] == GRID_EMPTY
          || board[row - 1][col - 1] == GRID_EMPTY) {
        return true;
      }
    } else if (direction == DIR_RIGHT) {
      if (board[row + 1][col + 1] == GRID_EMPTY
          || board[row + 1][col - 1] == GRID_EMPTY) {
        return true;
      }
    }
    return false;
  }

  private int[] isOurPlayerInBombRadius(Miniman player) {
    int[] res = new int[2];
    int countOur = 0;
    int countOpp = 0;
    for (int[] cell : bombArea) {
      if (board[player.getRow() + cell[0]][player.getCol() + cell[1]]
          == identity) {
        countOur++;
      } else if (board[player.getRow() + cell[0]][player.getCol() + cell[1]]
          == opponent) {
        countOpp++;
      }
    }
    res[0] = countOur;
    res[1] = countOpp;
    return res;
  }

  // get opponent player in bomb 2 radius
  private boolean isOpponentInRadius5(Miniman player) {
    int row = player.getRow();
    int col = player.getCol();
    for (int i = -5; i <= 5; i++) {
      int absI = i < 0 ? i * -1 : i;
      if (i == -5 || i == 5) {
        if (board[row + i][col] == opponent) {
          return true;
        }
      } else {
        int j = -5 + absI;
        if (board[row + i][col + j] == opponent) {
          return true;
        }
        j = 5 - absI;
        if (board[row + i][col + j] == opponent) {
          return true;
        }
      }
    }
    return false;
  }

  // opponent players in bomb 1 radius
  private boolean isOpponentInRadius4(Miniman player) {
    for (int i = -4; i <= 4; i++) {
      int absI = i < 0 ? i * -1 : i;
      for (int j = -4 + absI; j <= 4 - absI; j++) {
        if (board[player.getRow() + i][player.getCol() + j] == opponent) {
          return true;
        }
      }
    }
    return false;
  }

  // Check if the move is valid
  private boolean isValidMove(Miniman player, int[] direction) {
    int row = player.getRow() + direction[0];
    int col = player.getCol() + direction[1];
    if (row < 0 || row >= MAX_ROW) {
      return false;
    }
    if (col < 0 || col >= MAX_COL) {
      return false;
    }
    if (board[row][col] == GRID_EMPTY) {
      if (isBombArea(row, col)) {
        return false;
      }
      return true;
    }
    return false;
  }

  // check if the cell is in bomb explosion area
  private boolean isBombArea(int row, int col) {
    for (int i = 0; i < bombArea.length; i++) {
      int[] cell = bombArea[i];
      int rindex = row + cell[0];
      int cindex = col + cell[1];
      // no need to check the cell which is other side of the fence
      if (board[rindex][cindex] == GRID_FENCE) {
        if (rindex == -2 || rindex == 2 || cindex == -2 || cindex == 2) {
          i += 2;
        }
      }
      if (board[rindex][cindex] == GRID_BOMB1
          || board[rindex][cindex] == GRID_BOMB2) {
        return true;
      }
    }
    return false;
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