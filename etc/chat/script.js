// var socket = new SockJS('http://localhost:8082/ws')
// var stompClient = Stomp.over(socket)

var socket = null
var stompClient = null
var fakeUserId = null

function connect() {
    socket = socket || new SockJS('http://localhost:8082/ws')
    stompClient = stompClient || Stomp.over(socket)

    fakeUserId = document.getElementById('fake_hardcoded_user_id').value;

    stompClient.connect({ 'X-sender-id': fakeUserId }, frame => {
        console.log('Connected: ' + frame)

        stompClient.subscribe('/topic/messages', message => {
            showMessage(JSON.parse(message.body)['content'], false)
        })
    })
}

function sendMessage() {
    let messageContent = document.getElementById('inputMessage').value
    let receiverId = document.getElementById('receiver_user_id').value || alert('Enter receiver user id first')
    if (messageContent) {
        showMessage(messageContent, true)
        // stompClient.send('/app/chat', {}, messageContent)
        let headers = {
            'X-sender-id': fakeUserId,
            'X-receiver-id': receiverId,
        }
        stompClient.send('/app/chat', headers, JSON.stringify({ 'content': messageContent }))
        document.getElementById('inputMessage').value = ''
    }
}

function showMessage(message, isClient) {
    let messageContainer = document.createElement('div')
    messageContainer.classList.add('message')

    let messageElement = document.createElement('div')
    messageContainer.appendChild(messageElement)

    let text = document.createElement('p')
    let date = document.createElement('span')
    let dateValue = new Date()
    date.innerText = dateValue.getHours() + ':' + dateValue.getMinutes() + ':' + dateValue.getSeconds()
    text.innerText = message
    messageElement.appendChild(text)
    messageElement.appendChild(date)
    if (isClient) {
        messageContainer.classList.add('sent')
    } else {
        messageContainer.classList.add('received')
    }
    document.getElementById('messages').appendChild(messageContainer)
}

function onInputEnter(e) {
    if (event.key === 'Enter') {
        sendMessage()
    }
}
