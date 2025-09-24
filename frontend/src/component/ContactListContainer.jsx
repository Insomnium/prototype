import {useEffect, useState} from "react";
import Contact from "./Contact.jsx";
import { useDispatch, useSelector } from "react-redux";
import { fetchContacts, getAllContacts, selectSearchTerm } from "../store/contactListSlice.js";

const ContactListContainer = ({onContactSelected, selectedContactId}) => {

    const dispatch = useDispatch()
    const searchTerm = useSelector(selectSearchTerm)
    const contacts = useSelector(getAllContacts)

    useEffect(() => {
        dispatch(fetchContacts())
    }, [dispatch]);

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
