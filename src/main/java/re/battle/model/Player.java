package re.battle.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Player {
    private String name;
    private List<Ship> ships;
    private int maxShipsAmount;
    private boolean readyStatus;

    public Player(){
        ships = new ArrayList<>(maxShipsAmount);
    }

    public boolean isFull(){
        return ships.size() == maxShipsAmount;
    }

    public boolean getReadyStatus() {
        return readyStatus;
    }
}
