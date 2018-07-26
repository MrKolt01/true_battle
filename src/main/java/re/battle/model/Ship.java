package re.battle.model;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Ship {
    private Cell position;

    public Ship(Cell position) {
        this.position = position;
    }
}
