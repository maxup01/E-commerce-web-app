import React from 'react';
import '../../css/index.css';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import HomePage from "./HomePage";

function App() {

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<HomePage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App;