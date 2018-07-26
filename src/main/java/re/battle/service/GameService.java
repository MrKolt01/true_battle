package re.battle.service;

import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.stereotype.Service;
import re.battle.model.*;

@Service
public class GameService {
    public Game game = new Game();

    public Player addPlayer(String playerName){
        Player player = new Player();
        player.setName(playerName);

        game.getPlayers().add(player);

        return player;
    }

    public Game addShip(String playerName, Cell shipPosition){

        Player player = game.getPlayers().get(0);

        if(player.getName() == playerName){
            player = game.getPlayers().get(0);
        } else {
            player = game.getPlayers().get(1);
        }

        if(!player.isFull()) {
            Ship ship = new Ship(shipPosition);
            player.getShips().add(ship);
        }
        return game;
    }

    public DoShotMessage doShot(DoShotMessage doShotMessage){

        String playerName = doShotMessage.getPlayerName();
        Cell target = doShotMessage.getTarget();

        Player player0 = game.getPlayers().get(0);
        Player player1 = game.getPlayers().get(1);

        Player enemy;

        if(player0.getName() == playerName){
            enemy = player1;
        } else {
            enemy = player0;
        }

        //Some exception
//        for(Ship enemyShip : enemy.getShips()){
//            if ((enemyShip.getPosition().getX() == target.getX())
//                    && (enemyShip.getPosition().getY() == target.getY())){
//                target.setStatus("DESTROYED");
//                return doShotMessage;
//            }
//        }
        target.setStatus("MISSED");

        if(target.getStatus() != "DESTROYED"){
            target.setStatus("MISSED");
        }
        return doShotMessage;
    }

}
