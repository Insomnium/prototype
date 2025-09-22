const ChatWindow = ({ selectedContactId }) => {
    return (
        <>
            <div className="chat-window">
                <div className="chat-header">
                    <div className="chat-header-avatar" id="current-contact-avatar">JD</div>
                    <div className="chat-header-info">
                        <h3 id="current-contact-name">Select a contact to start chatting (debug: selectedContactId: {selectedContactId})</h3>
                        <p id="current-contact-status">Online</p>
                    </div>
                </div>

                <div className="chat-messages" id="chat-messages">
                    <div className="welcome-message">
                        <p>Select a contact from the list to view your conversation</p>
                    </div>
                </div>

                <div className="chat-input-container">
                    <textarea className="chat-input" id="message-input" placeholder="Type a message..." rows="1"></textarea>
                    <button className="send-button" id="send-button" disabled>
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M22 2L11 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                  stroke-linejoin="round"/>
                        </svg>
                    </button>
                </div>
            </div>
        </>
    )
}

export default ChatWindow;
