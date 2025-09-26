import ContactSearchContainer from "./component/ContactSearchContainer.jsx";
import ContactListContainer from "./component/ContactListContainer.jsx";
import ChatWindow from "./component/ChatWindow.jsx";
import { Provider } from "react-redux";
import store from "./store/store.js";

const Prototype = () => {

    return (
        <>
            <Provider store={store} >
                <div className="contact-list">
                    <div className="contact-list-header">
                        <h2>Chats</h2>
                        <button id="new-chat-btn">New Chat</button>
                    </div>

                    <ContactSearchContainer />

                    <ContactListContainer />
                </div>

                <ChatWindow />
            </Provider>
        </>
    )
};

export default Prototype;
