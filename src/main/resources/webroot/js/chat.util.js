
$(document).ready(function () {
    // alert("jQuery已生效");
    if(!window.WebSocket) {
        window.WebSocket = Window.MozWebSocket;
    }

    if(window.WebSocket) {
        alert("支持WebSocket");
    } else {
        alert("不支持WebSocket");
    }
});