package Bombman;

public class Miniman {
  int team;
  int row;
  int col;

  Miniman(int team, int row, int col) {
    this.team = team;
    this.row = row;
    this.col = col;
  }

  public int getTeam() {
    return team;
  }

  public void setTeam(int team) {
    this.team = team;
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
}
