package re.battle.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cell {
    private int x;
    private int y;
    private String status;

    public Cell(int x, int y, String status) {
        this.x = x;
        this.y = y;
        this.status = status;
    }

    public Cell(){

    }

    //    public enum Status {
//        DESTROYED, MISSED, PENDING, RESERVED
//    }
}
