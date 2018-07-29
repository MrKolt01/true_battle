package re.battle.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StartGameMessage {
    String playerName;
    String status;
}
