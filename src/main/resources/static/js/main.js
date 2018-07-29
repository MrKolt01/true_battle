var stompClient = null;
var gameReady = "NOTREADY";

function setConnected(connected) {
    if (connected) {
        $("#field").show();
    } else {
        $("#field").hide();
    }
}

function connect() {
    var socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        setConnected(true);
        stompClient.subscribe("/topic/game", onStartGameReceived);
        stompClient.subscribe("/topic/shot", onShotReceived);
        stompClient.subscribe("/topic/chat", onMessageReceived);
        stompClient.subscribe("/topic/players", onPlayerReceived);

        var message = {
            sender: "Иванов",
            message: "Это тестовое сообщение при connect()"
        };
        stompClient.send("/battle/chat/send", {}, JSON.stringify(message));

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
    var newPlayer = JSON.parse(player.body);
    // alert('Player '+newPlayer.name + ' connected!');
}

function onStartGameReceived(startGameMessage) {
    var startGameMessage = JSON.parse(startGameMessage.body);
    gameReady = startGameMessage.status;
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

function addShip(playerName, x, y, status){
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

    $("#readyButton").click(function () {
        var playerName = $("#name").val();
        ready(playerName);
    });

    $("#send-message").click(function() {
        sendMessage();
    });

    $("#board td").click(function () {
        if (gameReady !== "READY") {
            var minX = 0, minY = 0;
            var x = parseInt($(this).index()) - 1;
            var y = parseInt($(this).parent().index() - 1);

            var playerName = $("#name").val();

            if ((x >= minX) && (y >= minY)) {
                addShip(playerName, x, y);
                $(this).toggleClass('active');
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