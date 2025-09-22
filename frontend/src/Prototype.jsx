import ContactSearchContainer from "./component/ContactSearchContainer.jsx";
import ContactListContainer from "./component/ContactListContainer.jsx";
import ChatWindow from "./component/ChatWindow.jsx";
import {useState} from "react";

const Prototype = () => {

    const [selectedContactId, setSelectedContactId] = useState(null)

    const handleContactSelected = (contactId) => {
        setSelectedContactId(contactId)
    }

    return (
        <>
            <div className="contact-list">
                <div className="contact-list-header">
                    <h2>Chats</h2>
                    <button id="new-chat-btn">New Chat</button>
                </div>

                <ContactSearchContainer />

                <ContactListContainer onContactSelected={handleContactSelected} selectedContactId={selectedContactId} />
            </div>

            <ChatWindow selectedContactId={selectedContactId} />
        </>
    )
};

export default Prototype;
