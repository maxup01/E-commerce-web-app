import React from 'react';
import '../../css/index.css';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import HomePage from "./HomePage";
import ProductSearchPage from "./ProductSearchPage";

function App() {

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/products" element={<ProductSearchPage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App;