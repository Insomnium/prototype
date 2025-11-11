import { useEffect } from "react";
import Contact from "./Contact.jsx";
import { useDispatch, useSelector } from "react-redux";
import { fetchContactAggregates, getAllContacts, setSelectedContact } from "../store/contactListSlice.js";
import { getFakeUserId } from "../store/fakeAuthSlice.js";

const ContactListContainer = () => {

    const dispatch = useDispatch()
    const fakeUserId = useSelector(getFakeUserId)
    const contacts = useSelector(getAllContacts)

    useEffect(() => {
        const userId = fakeUserId;
        dispatch(fetchContactAggregates(userId))
    }, [dispatch, fakeUserId]);

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
