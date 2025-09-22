import {useEffect, useState} from "react";
import Contact from "./Contact.jsx";

const ContactListContainer = ({onContactSelected, selectedContactId}) => {

    const [contacts, setContacts] = useState([]);
    const sampleContacts = [
        {id: 1, name: "John Doe", lastMessage: "Hey, how are you?", time: "10:30 AM", avatar: "JD", status: "Online"},
        {id: 2, name: "Jane Smith", lastMessage: "Can we meet tomorrow?", time: "Yesterday", avatar: "JS", status: "Online"},
        {id: 3, name: "Robert Johnson", lastMessage: "I sent you the files", time: "Monday", avatar: "RJ", status: "Away"},
        {id: 4, name: "Emily Davis", lastMessage: "Thanks for your help!", time: "Sunday", avatar: "ED", status: "Offline"},
        {id: 5, name: "Michael Wilson", lastMessage: "Let's schedule a call", time: "Last week", avatar: "MW", status: "Online"},
        {id: 6, name: "Sarah Brown", lastMessage: "The meeting is at 3 PM", time: "Last week", avatar: "SB", status: "Away"},
        {id: 7, name: "David Miller", lastMessage: "Check out this article", time: "2 weeks ago", avatar: "DM", status: "Offline"},
        {id: 8, name: "Lisa Taylor", lastMessage: "Happy birthday!", time: "3 weeks ago", avatar: "LT", status: "Online"},
        {id: 9, name: "James Anderson", lastMessage: "See you soon!", time: "1 month ago", avatar: "JA", status: "Offline"},
        {id: 10, name: "Jennifer Thomas", lastMessage: "I'll get back to you", time: "2 months ago", avatar: "JT", status: "Away"}
    ];

    useEffect(() => {
        setContacts(sampleContacts)
    }, []);

    const handleContactClick = (contactId) => {
        console.log(`Selected contact: ${contactId}`)
        onContactSelected(contactId)
    }

    return (
        <>
            <div className="contacts-container" id="contacts-container">
                {contacts.map(contact => (
                    <Contact key={contact.id} contact={contact} onClick={() => handleContactClick(contact.id)} />
                ))}
            </div>
        </>
    )
}

export default ContactListContainer;
