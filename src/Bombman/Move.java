package Bombman;

public class Move {
  int player;
  int playerPos;
  int curr_row;
  int curr_col;
  int next_row;
  int next_col;
  int bomb = 0;
  int direction;
  
  public int getPlayerPos() {
    return playerPos;
  }

  public void setPlayerPos(int playerPos) {
    this.playerPos = playerPos;
  }

  public int getDirection() {
    return direction;
  }

  public void setDirection(int direction) {
    this.direction = direction;
  }

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