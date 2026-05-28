import Contact from "./Contact.jsx";
import { useDispatch, useSelector } from "react-redux";
import { getAllContacts, setSelectedContact } from "../store/contactListSlice.js";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import { Client } from "stompjs";


const ContactListContainer = () => {

    const dispatch = useDispatch()
    const contacts = useSelector(getAllContacts);

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
