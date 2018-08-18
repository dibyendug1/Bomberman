package Bombman;

public class Bomb {
  public static final int GRID_BOMB1 = 201;
  public static final int GRID_BOMB2 = 202;
  int type;
  int row;
  int col;
  int time;

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
    if (type == GRID_BOMB1) {
      setTime(1);
    } else if (type == GRID_BOMB2) {
      setTime(2);
    }
  }

  public int getRow() {
    return row;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public int getCol() {
    return col;
  }

  public void setCol(int col) {
    this.col = col;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public void decrTime() {
    this.time -= 1;
  }
}
