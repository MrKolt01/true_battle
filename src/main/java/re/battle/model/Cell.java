package re.battle.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cell {
    private int x;
    private int y;
    private String status;

//    public enum Status {
//        DESTROYED, MISSED, PENDING, RESERVED
//    }
}
