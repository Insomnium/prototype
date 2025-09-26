import { useSelector } from "react-redux";
import { getSelectedContact } from "../store/contactListSlice";
import { useState } from "react";
import { format } from "date-fns";

const ChatWindow = () => {

    const [ messages, setMessages ] = useState([])    
    const [ input, setInput ] = useState('')
    const selectedContact = useSelector(getSelectedContact)

    return (
        <>
            <div className="chat-window">
                <ChatMainArea selectedContact={selectedContact} messages={messages} />
                <ChatInput 
                    selectedContact={selectedContact} 
                    messages={messages} 
                    setMessages={setMessages}
                    input={input}
                    setInput={setInput} />
            </div>
        </>
    )
};

const ChatMessage = ({ text, timestamp, isClient }) => {
    const className = 'message ' + (isClient ? 'sent' : 'received')
    return (
        <>
            <div className={className} >
                <div className="message-text">{text}</div>
                <div className="message-time">{timestamp}</div>
            </div>
        </>
    )
};

const ChatInput = ({ selectedContact, messages, setMessages, input, setInput }) => {
    const [ submitionDisabled, setSubmitionDisabled ] = useState(true)

    const onChange = (event) => {
        const value = event.target.value
        setSubmitionDisabled(value == null || value == '')
        setInput(value)
    }

    const handleSubmit = () => {
        console.log(`>>> [to ${selectedContact.id}]: ${input}`)
        setSubmitionDisabled(true)
        setInput('')
        setMessages([...messages, { text: input, timestamp: new Date().getTime(), isClient: true }])
    }

    const interceptKeyDown = (event) => {
        if (event.ctrlKey && event.key == 'Enter') {
            event.preventDefault();
            handleSubmit();
        }
    }

    return (
        <>
        <div className="chat-input-container">
            <textarea id="message-input" className="chat-input" placeholder="Type a message..." rows="1" 
                value={input} 
                onChange={onChange}
                onKeyDown={interceptKeyDown}
                />
            <button className="send-button" id="send-button" disabled={submitionDisabled} onClick={handleSubmit}>
                <ChatSubmitButtonSvg />
            </button>
        </div>
        </>
    )
};

const ChatMainArea = ({ selectedContact, messages }) => {
    return (
        <>
        <div className="chat-header">
            <div className="chat-header-avatar" id="current-contact-avatar">{selectedContact?.avatar}</div>
            <div className="chat-header-info">
                <ChatHeaderContactPlate selectedContact={selectedContact} />
            </div>
        </div>
        <div className="chat-messages" id="chat-messages">
            {
                messages.map(m => (
                    <ChatMessage key={m.timestamp} isClient={m.isClient} text={m.text} timestamp={format(m.timestamp, 'MM/dd/yyyy HH:mm:ss')} />
                ))
            }
            <div className="welcome-message">
                <ChatMessageAreaHint selectedContact={selectedContact} />
            </div>
        </div>
        </>
    )
}

const ChatMessageAreaHint = ({ selectedContact }) => 
    selectedContact == null ? <p>Select a contact from the list to view your conversation</p> : null

const ChatHeaderStatus = ({status}) => {
    if (status.toLowerCase() == 'online') {
        return (
            <div className="status-container" >
                <p id="current-contact-status">{status}</p>
                <span id="status-indicator" className="status-indicator-online" />
            </div>
        )
    }

    return (
        <div className="status-container" >
            <p id="current-contact-status">{status}</p>
        </div>
    )
};

const ChatHeaderContactPlate = ({ selectedContact }) => {
    if (selectedContact != null) {
        return (
            <>
                <h3 id="current-contact-name">{selectedContact.name}</h3>
                <ChatHeaderStatus status={selectedContact.status} />
            </>
        )
    }
    return (
        <h3 id="current-contact-name">Select a contact to start chatting</h3>
    )
};

const ChatSubmitButtonSvg = () => {
    return (
        <>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22 2L11 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                        stroke-linejoin="round"/>
            </svg>
        </>
    )
};

export default ChatWindow;
