package re.battle.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddShipMessage {
    private String playerName;
//    private Cell shipPosition;
    private int x;
    private int y;
    private String status;
}
