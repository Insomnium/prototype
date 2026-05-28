
import { Provider } from "react-redux";
import store from "./store/store.js";
import Chat from "./component/Chat.jsx";

const Prototype = () => {

    return (
        <>
            <Provider store={store} >
                <Chat/>
            </Provider>
        </>
    )
};

export default Prototype;
