package re.battle.service;

import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import re.battle.controller.GameController;
import re.battle.model.*;

@Service
public class GameService {
    private Game game = new Game();

    public Player addPlayer(String playerName){
        Player player = new Player();
        player.setName(playerName);
        player.setReadyStatus(false);
        player.setMaxShipsAmount(10);

        game.getPlayers().add(player);

        return player;
    }

    public Game addShip(AddShipMessage addShipMessage){

        Player player = game.getPlayers().get(0);

        String playerName = addShipMessage.getPlayerName();
        int x = addShipMessage.getX();
        int y = addShipMessage.getY();
        String status = addShipMessage.getStatus();

        if(player.getName().equals(playerName)){
            player = game.getPlayers().get(0);
            System.out.println("added 0");
        } else {
            player = game.getPlayers().get(1);
            System.out.println("added 1");
        }
        Cell shipPosition = new Cell(x,y,status);
//        if(!player.isFull()) {
//        }
        Ship ship = new Ship(shipPosition);
        player.getShips().add(ship);
        return game;
    }

    public DoShotMessage doShot(DoShotMessage doShotMessage){

        String playerName = doShotMessage.getPlayerName();
        Cell target = doShotMessage.getTarget();

        Player player0 = game.getPlayers().get(0);
        Player player1 = game.getPlayers().get(1);

        Player enemy;

        if(player0.getName().equals(playerName)){
            enemy = player1;
        } else {
            enemy = player0;
        }

        for(Ship enemyShip : enemy.getShips()){
            if ((enemyShip.getPosition().getX() == target.getX())
                    && (enemyShip.getPosition().getY() == target.getY())){
                target.setStatus("DESTROYED");
                return doShotMessage;
            }
        }
        target.setStatus("MISSED");

        if(!target.getStatus().equals("DESTROYED")){
            target.setStatus("MISSED");
        }
        return doShotMessage;
    }

    public StartGameMessage startGame(StartGameMessage startGameMessage){
        String playerName = startGameMessage.getPlayerName();

        Player player0 = game.getPlayers().get(0);
        Player player1 = game.getPlayers().get(1);

        if(playerName.equals(player0.getName())){
            player0.setReadyStatus(true);
        }else if(playerName.equals(player1.getName())){
            player1.setReadyStatus(true);
        }

        if(player0.getReadyStatus() && player1.getReadyStatus()){
            startGameMessage.setStatus("READY");
        } else {
            startGameMessage.setStatus("NOTREADY");
        }
        return startGameMessage;
    }

}
