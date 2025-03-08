import React, {useEffect, useState} from 'react';
import axios from 'axios';
import ProductSearchBar from "./ProductSearchBar";
import ProductCard from "./ProductCard";
import PageNavBar from "./PageNavBar";

function ProductSearchPage() {

    const mainDivStyle = {
        width: "100%",
        height: "100%",
        gridTemplateRows: "repeat(10, 350px)",
        gridTemplateColumns: "repeat(3, 250px)",
        rowGap: "5px",
        columnGap: "5px"
    }

    const [isLoading, setIsLoading] = useState(true);
    const [eanCodesOfPreviouslyLoadedProducts, setEanCodesOfPreviouslyLoadedProducts]
        = useState([]);
    const [minimalPrice, setMinimalPrice] = useState(0);
    const [maximalPrice, setMaximalPrice] = useState(0);
    const [type, setType] = useState('');
    const [inputData, setInputData] = useState('');
    const [currentProducts, setCurrentProducts] = React.useState([]);

    useEffect(() => {
        axios.get('http://127.0.0.1:8080/search-products', {
            params: {
                type: null,
                phrase: null,
                minPrice: null,
                maxPrice: null,
                forbiddenEanCodes: []
            }
        })
            .then(response => {

                setCurrentProducts(response.data);
                setIsLoading(false);
            })
            .catch(err => console.error("Error fetching products:", err));
    });

    function searchProductsByHittingEnter() {

        setIsLoading(true);
        setEanCodesOfPreviouslyLoadedProducts([]);

        let pomType = type.trim().length === 0 ? null : type;
        let pomPhrase = inputData.trim().length === 0 ? null : inputData;
        let pomMax = maximalPrice === 0 ? null : maximalPrice;

        axios.get('http://127.0.0.1:8080/search-products', {
            params: {
                type: pomType,
                phrase: pomPhrase,
                minPrice: minimalPrice,
                maxPrice: pomMax,
                forbiddenEanCodes: []
            }
        })
            .then(response => {

                setCurrentProducts(response.data);
                setIsLoading(false);
            })
            .catch(err => console.error("Error fetching products:", err));
    }

    function moveToPreviousPage(){

        setIsLoading(true);

        let arrayOfLast24EanCodesInList = [];

        for(let i = eanCodesOfPreviouslyLoadedProducts.length - 1;
            (i > eanCodesOfPreviouslyLoadedProducts.length - 1 - 24)
            && (eanCodesOfPreviouslyLoadedProducts .length - i >= 0); i++){

            arrayOfLast24EanCodesInList.push(eanCodesOfPreviouslyLoadedProducts[i]);
        }

        axios.get("http://127.0.0.1:8080/products-by-ean-codes", {
            params: {
                eanCodes: arrayOfLast24EanCodesInList
            }
        }).then(response => {

            setIsLoading(false);

            setCurrentProducts(response.data);
            setEanCodesOfPreviouslyLoadedProducts(prevState => {

                for(let i = 0; i < 24; i++){

                    prevState.pop();
                }

                return prevState;
            });

            return true;

        }).catch(err => {

            console.error("Error fetching products:", err);

            return false;
        });
    }

    function moveToNextPage(){

        setIsLoading(true);

        if(currentProducts.length !== 24)
            return false;

        let pomType = type.trim().length === 0 ? null : type;
        let pomPhrase = inputData.trim().length === 0 ? null : inputData;
        let pomMin = minimalPrice === 0 ? null : minimalPrice;
        let pomMax = maximalPrice === 0 ? null : maximalPrice;

        axios.get('http://127.0.0.1:8080/search-products', {
            params: {
                type: pomType,
                phrase: pomPhrase,
                minPrice: pomMin,
                maxPrice: pomMax,
                forbiddenEanCodes: eanCodesOfPreviouslyLoadedProducts
            }
        })
            .then(response => {

                setCurrentProducts(response.data);
                setIsLoading(false);

                let eanCodesOfCurrentlyLoadedProducts = [];

                currentProducts.forEach(product => {

                    eanCodesOfCurrentlyLoadedProducts.push(product['EANCode']);
                });

                setEanCodesOfPreviouslyLoadedProducts(prevState => {

                    prevState.push(...eanCodesOfCurrentlyLoadedProducts);

                    return prevState;
                });

                return true;

            })
            .catch(err => {

                console.error("Error fetching products:", err);

                return false;
            });
    }

    return (
        <div style={mainDivStyle} className="grid">
            {!isLoading ?
                (
                    <>
                        <div className="flex col-span-3 row-span-1">
                            <div className={"w-2/3 h-1/4 self-center justify-self-center"}>
                                <ProductSearchBar width={"100%"} height={"100%"}
                                                  typeSetter={setType} inputDataSetter={setInputData}
                                                  searchMethod={searchProductsByHittingEnter}
                                                  minimalPriceSetter={setMinimalPrice}
                                                  maximalPriceSetter={setMaximalPrice} />
                            </div>
                        </div>
                        {currentProducts.map(product => {
                            return (
                                <div className={"col-span-1 row-span-1"}>
                                    <ProductCard width={"100%"} height={"100%"} imageSrc={product['mainImage']}
                                                 name={product['name']} price={product['currentPrice']}/>
                                </div>
                            );
                        })}
                        <div className={"col-span-3 row-span-1 flex items-center justify-items-center"}>
                            <PageNavBar handleMoveBack={moveToPreviousPage} handleMoveForward={moveToNextPage}/>
                        </div>
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