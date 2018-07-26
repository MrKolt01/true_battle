package re.battle.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import re.battle.model.ChatMessage;

@Controller
public class ChatController {

    @MessageMapping("chat/send")
    @SendTo("/topic/chat")
    public ChatMessage send(ChatMessage message) {
        return message;
    }
}
