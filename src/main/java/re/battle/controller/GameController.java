package re.battle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import re.battle.model.*;
import re.battle.service.GameService;

@RequiredArgsConstructor
@Controller
public class GameController {

    private final GameService gameService;

    @MessageMapping("/game/add/ships")
    @SendTo("/topic/game")
    public Game addShips(AddShipMessage addShipsMessage) {
        return gameService.addShip(addShipsMessage.getPlayerName(), addShipsMessage.getShipPosition());
    }

    @MessageMapping("/game/shot")
    @SendTo("/topic/shot")
    public DoShotMessage doShot(DoShotMessage doShotMessage) {
        System.out.println(doShotMessage.getTarget().getX()+" "+doShotMessage.getTarget().getY());

        gameService.doShot(doShotMessage);
        return doShotMessage;
    }

    @MessageMapping("/players/add/player")
    @SendTo("/topic/players")
    public AddPlayerMessage addPlayer(AddPlayerMessage addPlayerMessage) {
        gameService.addPlayer(addPlayerMessage.getName());
        return addPlayerMessage;
    }
}