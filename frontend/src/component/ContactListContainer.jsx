import { useEffect } from "react";
import Contact from "./Contact.jsx";
import { useDispatch, useSelector } from "react-redux";
import { fetchContactAggregates, getAllContacts, setSelectedContact, receiveMessage } from "../store/contactListSlice.js";
import { getFakeUserId } from "../store/fakeAuthSlice.js";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import { Client } from "stompjs";


const ContactListContainer = () => {

    const dispatch = useDispatch()
    const fakeUserId = useSelector(getFakeUserId)
    const contacts = useSelector(getAllContacts)

    useEffect(() => {
        const userId = fakeUserId;
        dispatch(fetchContactAggregates(userId))

        let stompClient = null;

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
            <div className="contacts-container" id="contacts-container">
                {contacts.map(contact => (
                    <Contact key={contact.contactId} contact={contact} onClick={() => dispatch(setSelectedContact(contact))} />
                ))}
            </div>
        </>
    )
}

export default ContactListContainer;
