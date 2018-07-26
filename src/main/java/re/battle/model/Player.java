package re.battle.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Player {
    private String name;
    private List<Ship> ships;
    private int maxShipsAmount;

    public boolean isFull(){
        if(ships.size() == maxShipsAmount){
            return true;
        } else {
            return false;
        }
    }
}
