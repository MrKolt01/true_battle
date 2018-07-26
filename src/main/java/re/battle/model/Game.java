package re.battle.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Game {
    private List<Player> players;
    private String winner;
    private GameStatus status;

    public enum GameStatus {
        STARTED, FINISHED
    }

    public Game(){
        this.players =  new ArrayList<>(2);
    }
}
