import React from 'react';
import '../../css/index.css';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import HomePage from "./HomePage";
import ProductSearchPage from "./product/ProductSearchPage";
import OrderTransactionSearchPage from "./order_transaction/OrderTransactionsSearchPage";

//TODO change mappings in the future so that they will be appropriate for each route
function App() {

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/products/search" element={<ProductSearchPage />} />
                <Route path="/create-product" element={<ProductSearchPage />} />
                <Route path="/order-transactions" element={<OrderTransactionSearchPage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App;