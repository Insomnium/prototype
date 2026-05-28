import { useEffect } from "react";
import ContactSearchContainer from "./ContactSearchContainer.jsx";
import ContactListContainer from "./ContactListContainer.jsx";
import ChatWindow from "./ChatWindow.jsx";
import FakeAuth from "./FakeAuth.jsx"
import { useDispatch, useSelector } from "react-redux";
import { fetchContactAggregates, getAllContacts, setSelectedContact, receiveMessage } from "../store/contactListSlice.js";
import { getFakeUserId } from "../store/fakeAuthSlice.js";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

const Chat = () => {

    const dispatch = useDispatch();
    const fakeUserId = useSelector(getFakeUserId);

    let stompClient = null;

    useEffect(() => {
        const userId = fakeUserId;
        dispatch(fetchContactAggregates(userId));

        if (userId > 0) {
            const socket = new SockJS(`http://localhost:8082/ws?userId=${userId}`);
            stompClient = Stomp.over(socket);

            stompClient.connect(
                // headers
                { 'X-sender-id': userId }, 
                // connect
                () => { 
                    console.log('Connected to WS');
                    stompClient.subscribe(`/user/topic/messages`, message => {
                        const messages = JSON.parse(message.body)['messages'];
                        console.log('Messages: ' + messages);
                        dispatch(receiveMessage(messages));
                
                });
                },
                // error
                (error) => { console.error(error); }
            );
        }        

        return () => {
            if (stompClient) {
                stompClient.disconnect();
            }
        }
    }, [dispatch, fakeUserId]);

    return (
        <>
         <FakeAuth />
            <div className="contact-list">
                <div className="contact-list-header">
                    <h2>Chats</h2>
                    <button id="new-chat-btn">New Chat</button>
                </div>

                <ContactSearchContainer />

                <ContactListContainer />
            </div>

            <ChatWindow doSendMessage={(msg, receiverId) => {
                console.log(`Sending ${msg} to ${receiverId}`);
                console.dir(msg)
                const headers = {
                    'X-sender-id': fakeUserId,
                    'X-receiver-id': receiverId,
                }
                stompClient.send('/app/chat', headers, JSON.stringify({ 'content': msg['text'] }));
            }} />
        </>
    )
};

export default Chat;