import { useEffect } from "react";
import Contact from "./Contact.jsx";
import { useDispatch, useSelector } from "react-redux";
import { fetchContactAggregates, getAllContacts, setSelectedContact } from "../store/contactListSlice.js";

const ContactListContainer = () => {

    const dispatch = useDispatch()
    const contacts = useSelector(getAllContacts)

    useEffect(() => {
        dispatch(fetchContactAggregates())
    }, [dispatch]);

    return (
        <>
            <div className="contacts-container" id="contacts-container">
                {contacts.map(contact => (
                    <Contact key={contact.id} contact={contact} onClick={() => dispatch(setSelectedContact(contact))} />
                ))}
            </div>
        </>
    )
}

export default ContactListContainer;
