package re.battle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import re.battle.controller.GameController;
import re.battle.model.*;

@Service
public class GameService {
    private Game game;
    private final SimpMessageSendingOperations sendingOperations;

    @Autowired
    public GameService(SimpMessageSendingOperations sendingOperations) {
        this.game = new Game();
        this.sendingOperations = sendingOperations;
    }

    public Player addPlayer(String playerName){
        Player player = new Player();
        player.setName(playerName);
        player.setReadyStatus(false);
        player.setMaxShipsAmount(10);

        game.getPlayers().add(player);

        return player;
    }

    public void addShip(AddShipMessage addShipMessage){

        Player player = game.getPlayers().get(0);

        String playerName = addShipMessage.getPlayerName();
        int x = addShipMessage.getX();
        int y = addShipMessage.getY();
        String status = addShipMessage.getStatus();

        Player player0 = game.getPlayers().get(0);
        Player player1 = game.getPlayers().get(1);

        if(player.getName().equals(playerName)){
            player = player0;
        } else {
            player = player1;
        }
        if(!player.isFull()) {
            Cell shipPosition = new Cell(x, y, status);
            Ship ship = new Ship(shipPosition);
            player.getShips().add(ship);
        }

        if(player0.isFull() && (player1.isFull())) {
            sendingOperations.convertAndSend("/topic/game", StartGameMessage.builder()
                    .status("READY")
                    .build());
        } else if(player.isFull()){
            sendingOperations.convertAndSend("/topic/ready", ReadyPlayerMessage.builder()
                    .name(player.getName())
                    .build());
        }
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
