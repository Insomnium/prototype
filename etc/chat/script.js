var socket = new SockJS('http://localhost:8082/ws')
var stompClient = Stomp.over(socket)

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame)
    stompClient.subscribe('/topic/messages', function (message) {
        showMessage(JSON.parse(message.body)['content'], false)
    })
});

function sendMessage() {
    var messageContent = document.getElementById('inputMessage').value
    if (messageContent) {
        showMessage(messageContent, true)
        // stompClient.send('/app/chat', {}, messageContent)
        stompClient.send('/app/chat', {}, JSON.stringify({ 'content': messageContent }))
        document.getElementById('inputMessage').value = ''
    }
}

function showMessage(message, isClient) {
    var messageElement = document.createElement('div')
    var text = document.createElement('p')
    var date = document.createElement('span')
    var dateValue = new Date()
    date.innerText = dateValue.getHours() + ':' + dateValue.getMinutes() + ':' + dateValue.getSeconds()
    text.innerText = message
    messageElement.appendChild(text)
    messageElement.appendChild(date)
    if (isClient) {
        messageElement.classList.add('message_client')
    } else {
        messageElement.classList.add('message_server')
    }
    document.getElementById('messages').appendChild(messageElement)
}

function onInputEnter(e) {
    if (event.key === 'Enter') {
        sendMessage()
    }
}
