import { useState } from "react";
import { useDispatch } from "react-redux";
import { setUserId } from "../store/fakeAuthSlice";

const FakeAuth = () => {

    const [inputValue, setInputValue] = useState("");
    const dispatch = useDispatch();

    const onFakeAuth = () => {
        const trimmed = inputValue.trim();
        if (trimmed) {
            console.log(`>>> Faking auth: ${trimmed}`);
            dispatch(setUserId(trimmed));
        }
    };

    return (
        <>
            <div>
                <input
                    id="fake_auth_user_id"
                    type="text"
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                />
                <button id="fake_auth_btn" onClick={onFakeAuth}>
                    Fake Auth
                </button>
            </div>
        </>
    );
};

export default FakeAuth;