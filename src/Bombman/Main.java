package Bombman;

import java.util.ArrayList;
import java.util.Collections;

public class Main {
  static final int[][] bombArea =
      { { 1, 0 }, { 2, 0 }, { -1, 0 }, { -2, 0 }, { 0, -1 }, { 0, -2 },
          { 0, 1 }, { 0, 2 } };

  public static void main(String[] args) {
    ArrayList<Bomb> bombs = new ArrayList<>();
    Bomb b = new Bomb();
    b.setRow(1);
    b.setCol(1);
    bombs.add(b);
    b = new Bomb();
    b.setRow(6);
    b.setCol(5);
    bombs.add(b);
    b = new Bomb();
    b.setRow(1);
    b.setCol(3);
    bombs.add(b);
    b = new Bomb();
    b.setRow(9);
    b.setCol(9);
    bombs.add(b);

    ArrayList<Bomb> tmpBombList = new ArrayList<>();
    tmpBombList.addAll(bombs);
    Collections.copy(tmpBombList, bombs);

    //tmpBombList.get(0).setTime(-1);
    //removeExplodedBomb(tmpBombList);
    int listSize = tmpBombList.size();
    for (int i = 0; i < listSize; i++) {
      Bomb b1 = tmpBombList.get(i);
      if (b1.getTime() == 0) {
        tmpBombList.remove(b1);
        blowBomb(b1, tmpBombList);
        i--;
        listSize = tmpBombList.size();
      }
    }
    //blowBomb(bombs.get(0), tmpBombList);
    System.out.println("Done");
  }

  private static void blowBomb(Bomb b, ArrayList<Bomb> tmpList) {
    if (tmpList.isEmpty()) {
      return;
    }
    //killPlayers(b);
    int size = tmpList.size();
    for (int i = 0; i < size; i++) {
      Bomb t_b = tmpList.get(i);
      if (isInRadius(b, t_b)) {
        tmpList.remove(t_b);
        blowBomb(t_b, tmpList);
        i--;
        size = tmpList.size();
      }
    }
  }

  private static void removeExplodedBomb(ArrayList<Bomb> tmpBombList) {
    int size = tmpBombList.size();
    for (int i = 0; i < size; i++) {
      Bomb tt = tmpBombList.get(i);
      if (tt.getTime() < 0) {
        tmpBombList.remove(tt);
        size--;
      }
    }
  }

  private static boolean isInRadius(Bomb b, Bomb t_b) {
    for (int[] cell : bombArea) {
      if ((b.getRow() + cell[0]) == t_b.getRow()
          && (b.getCol() + cell[1]) == t_b.getCol()) {
        return true;
      }
    }
    return false;
  }

}
