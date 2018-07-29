var stompClient = null;
var gameReady = "NOTREADY";
var playerReady = "NOTREADY";

jQuery('document').ready(function () {
    var space = 1;
    for (var i = 0; i <= 10; i++) {
        var col = "";
        for (var j = 0; j <= 10; j++) {
            if (i === 0 && j !== 0) {
                col += "<td data-pos='" + space + "' align='center'>" + j + "</td>";
            } else if (j === 0 && i !== 0) {
                col += "<td data-pos='" + space + "' align='center'>" + i + "</td>";
            } else {
                col += "<td data-pos='" + space + "'></td>";
            }
            space++;
        }
        jQuery('#board').append("<tr>" + col + "</tr>");
        jQuery('#enemy-board').append("<tr>" + col + "</tr>");

    }
});

function setConnected(connected) {
    if (connected) {
        $("#main").show();
    } else {
        $("#main").hide();
    }
}

function connect() {
    var socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        setConnected(true);
        stompClient.subscribe("/topic/game", onStartGameReceived);
        stompClient.subscribe("/topic/ready", onPlayerReady);
        stompClient.subscribe("/topic/shot", onShotReceived);
        stompClient.subscribe("/topic/chat", onMessageReceived);
        stompClient.subscribe("/topic/players", onPlayerReceived);

        var player = {
            name: $("#name").val()
        };
        stompClient.send("/battle/players/add/player", {}, JSON.stringify(player));
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect()
    }
    setConnected(false);
}

function onPlayerReceived(player) {
    // var newPlayer = JSON.parse(player.body);
    // alert('Player '+newPlayer.name + ' connected!');
}

function onPlayerReady(player) {
    var player = JSON.parse(player.body);
    if (player.name === $("#name").val()){
        playerReady = "READY";
    }
}

function onStartGameReceived(startGameMessage) {
    var startGameMessage = JSON.parse(startGameMessage.body);
    gameReady = startGameMessage.status;
    if (gameReady === "READY"){
        jQuery("#board").toggleClass('noActiveField');
        jQuery("#enemy-board").toggleClass('activeField');
    }
}

function onMessageReceived(message) {
    var receivedMessage = JSON.parse(message.body);

    $("#chat-messages").append("<li><b>" + receivedMessage.sender + "</b>: " + receivedMessage.message + "</li>");
}

function onShotReceived(doShotMessage) {
    var receivedShot = JSON.parse(doShotMessage.body);
    var shootingPlayer = receivedShot.playerName;
    var targetX, targetY, shotStatus;
    targetX = receivedShot.target.x;
    targetY = receivedShot.target.y;
    shotStatus = receivedShot.target.status;
    var cell;
    if ($("#name").val() === shootingPlayer) {
        cell = $("#enemy-board").children("tr").eq(targetY+1).children("td").eq(targetX+1);
    } else {
        cell = $("#board").children("tr").eq(targetY+1).children("td").eq(targetX+1);
    }
    if (shotStatus === "DESTROYED"){
        cell.toggleClass('destroyed');
    } else {
        cell.toggleClass('missed');
    }
}

function sendMessage() {
    var message = {
        sender: $("#name").val(),
        message: $("#text-message").val()
    };
    stompClient.send("/battle/chat/send", {}, JSON.stringify(message));
}

function doShot(playerName, target){
    var doShotMessage = {
        playerName: playerName,
        target: target
    };
    stompClient.send("/battle/game/shot", {}, JSON.stringify(doShotMessage));
}

function addShip(playerName, x, y){
    if (gameReady !== "READY") {
        var addShipMessage = {
            playerName: playerName,
            x: x,
            y: y,
            status: "PENDING"
        };
        stompClient.send("/battle/game/add/ships", {}, JSON.stringify(addShipMessage));
    }
}

function ready(playerName){
    var startGameMessage = {
        playerName: playerName,
        status: "UNKNOWN"
    };
    stompClient.send("/battle/game/ready", {}, JSON.stringify(startGameMessage));
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    $("#connect").click(function () {
        connect();
    });

    $("#disconnect").click(function () {
        disconnect();
    });

    $("#send-message").click(function() {
        sendMessage();
    });

    $("#board td").click(function () {

        if (playerReady !== "READY") {
            var minX = 0, minY = 0;
            var x = parseInt($(this).index()) - 1;
            var y = parseInt($(this).parent().index() - 1);

            var playerName = $("#name").val();

            if ((x >= minX) && (y >= minY)) {
                addShip(playerName, x, y);
                $(this).toggleClass('activeCell');
            }
        }
    });

    $("#enemy-board td").click(function () {
        if (gameReady === "READY") {
            var minX = 0, minY = 0;
            var x = parseInt($(this).index()) - 1;
            var y = parseInt($(this).parent().index() - 1);
            var playerName = $("#name").val();
            var target = {
                x: x,
                y: y,
                status: "PENDING"
            };
            if ((x >= minX) && (y >= minY)) {
                doShot(playerName, target);
            }
        }
    });
});