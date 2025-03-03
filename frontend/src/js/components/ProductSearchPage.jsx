import React, {useEffect, useState} from 'react';
import axios from 'axios';
import ProductSearchBar from "./ProductSearchBar";
import ProductCard from "./ProductCard";

function ProductSearchPage() {

    const [isLoading, setIsLoading] = useState(true);
    const [type, setType] = useState('');
    const [inputData, setInputData] = useState('');

    const [currentProducts, setCurrentProducts] = React.useState([]);

    useEffect(() => {
        axios.get('http://127.0.0.1:8080/search-products'/*TODO Method for fetching 24 products 'backend module'*/)
            .then(response => {
                setCurrentProducts(response.data);
                setIsLoading(false);
            })
            .catch(err => console.error("Error fetching products:", err));
    })

    function searchProducts() {
        setIsLoading(true);
        axios.get('http://127.0.0.1:8080/search-products'
            /*TODO Method for fetching 24 different products than previously searched 'backend module'*/)
            .then(response => {
                setCurrentProducts(response.data);
                setIsLoading(false);
            })
            .catch(err => console.error("Error fetching products:", err));
    }

    const mainDivStyle = {
        width: "100%",
        height: "100%",
        gridTemplateRows: "repeat(10, 350px)",
        gridTemplateColumns: "repeat(3, 250px)",
        rowGap: "10px",
        columnGap: "20px"
    }

    return (
        <div style={mainDivStyle} className="grid">
            {!isLoading ?
                (
                    <>
                        <div className="flex col-span-3 row-span-1">
                            <div className={"w-2/3 h-1/4 self-center justify-self-center"}>
                                <ProductSearchBar typeSetter={setType} inputDataSetter={setInputData}
                                                  searchMethod={searchProducts}
                                                  width={"100%"} height={"100%"}/>
                            </div>
                        </div>
                        {currentProducts.map(product => {
                            return (
                                <div className={"col-span-1 row-span-1"}>
                                    <ProductCard width={"100%"} height={"100%"} imageSrc={product.image} name={product.name}
                                                 price={product.currentPrice}/>
                                </div>
                            );
                        })}
                    </>
                ):(
                    <div className={"col-span-3 row-span-10 flex items-center justify-items-center font-semibold text-lg"}>
                        Loading...
                    </div>
                )
            }
        </div>
    );
}

export default ProductSearchPage;