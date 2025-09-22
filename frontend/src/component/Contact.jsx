const Contact = ({contact, onClick}) => {

    const handleClick = () => {
        onClick(contact.id)
    }

    return (
        <>
            <div className='contact' data-id={contact.id} onClick={handleClick}>
                <div className='contact-avatar'>{contact.avatar}</div>
                <div className='contact-info'>
                    <div className='contact-name'>{contact.name}</div>
                    <div className='contact-last-message'>{contact.lastMessage}</div>
                </div>
                <div className='contact-time'>{contact.time}</div>
            </div>
        </>
    )
}

export default Contact;
