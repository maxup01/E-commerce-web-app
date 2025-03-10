import React, {useEffect} from 'react';
import axios from 'axios';
import OrderTransactionSearchBar from "./OrderTransactionSearchBar";
import TransactionCard from "../TransactionCard";
import PageNavBar from "../PageNavBar";

function OrderTransactionSearchPage() {

    const divStyle = {

        width: '100%',
        height: '100%',
        gridTemplateRows: 'repeat(20, 50px)',
        gridTemplateColumns: "repeat(2, 50%)",
        rowGap: "5px",
        columnGap: "5px"
    }

    const [isLoading, setIsLoading] = React.useState(true);

    const [idsOfPreviouslyLoadedTransactions, setIdsOfPreviouslyLoadedTransactions] = React.useState([]);
    const [currentLoadedTransactions, setCurrentLoadedTransactions] = React.useState([]);

    const [startingDate, setStartingDate] = React.useState(null);
    const [endingDate, setEndingDate] = React.useState(null);
    const [deliveryProviderName, setDeliveryProviderName] = React.useState(null);
    const [paymentMethodName, setPaymentMethodName] = React.useState(null);
    const [userEmail, setUserEmail] = React.useState(null);

    useEffect(() => {

        setIsLoading(true);

        axios.get('http://127.0.0.1:8080/orders-by-search', {
            params: {
                startingDate: startingDate,
                endingDate: endingDate,
                paymentMethodName: paymentMethodName,
                deliveryProviderName: deliveryProviderName,
                userEmail: userEmail,
                forbiddenOrderTransactionIds: idsOfPreviouslyLoadedTransactions
            }})
            .then(response => {

                setIdsOfPreviouslyLoadedTransactions([]);
                setCurrentLoadedTransactions(response.data);
                setIsLoading(false);
            })
            .catch((error) => {

                console.error("Error during fetching order transactions", error);
            });
    });

    function searchWhenClickingSearchButtonHandler() {

        setIsLoading(true);

        axios.get('http://127.0.0.1:8080/orders-by-search', {
            params: {
                startingDate: startingDate,
                endingDate: endingDate,
                paymentMethodName: paymentMethodName,
                deliveryProviderName: deliveryProviderName,
                userEmail: userEmail,
                forbiddenOrderTransactionIds: idsOfPreviouslyLoadedTransactions
            }
            })
            .then((response) => {

                setIdsOfPreviouslyLoadedTransactions([]);
                setCurrentLoadedTransactions(response.data);
                setIsLoading(false);
            })
            .catch((error) => {

                console.error("Error during fetching order transactions", error);
            });
    }

    function handleMoveBack() {

        if(idsOfPreviouslyLoadedTransactions.length > 24){

            return false;
        }

        setIsLoading(true);

        let returnValue = true;

        let ids24 = [];

        for(let i = idsOfPreviouslyLoadedTransactions.length - 1;
            (i >= idsOfPreviouslyLoadedTransactions.length - 24) && (i >= 0) ; i--){

            ids24.push(idsOfPreviouslyLoadedTransactions[i]);
            idsOfPreviouslyLoadedTransactions.pop();
        }

        axios.get('http://127.0.0.1:8080/orders-by-ids', {
            params: {
                ids: ids24
            }
        }).then(response => {
            setCurrentLoadedTransactions(response.data);
            setIsLoading(false);
        }).catch(error => {

            returnValue = false;
            console.error("Error during fetching order transactions:", error);
        });

        return returnValue;
    }

    function handleMoveForward() {

        if(currentLoadedTransactions.length < 24){
            return false;
        }

        setIsLoading(true);

        let returnValue = true;

        axios.get('http://127.0.0.1:8080/orders-by-search', {
            params: {
                startingDate: startingDate,
                endingDate: endingDate,
                paymentMethodName: paymentMethodName,
                deliveryProviderName: deliveryProviderName,
                userEmail: userEmail,
                forbiddenOrderTransactionIds: idsOfPreviouslyLoadedTransactions
            }
            })
            .then((response) => {

                setCurrentLoadedTransactions(response.data);

                let arrayOfOrdersIdsOnLastPage = [];

                currentLoadedTransactions.forEach(orderTransaction => {
                    arrayOfOrdersIdsOnLastPage.push(orderTransaction['id']);
                });

                setIdsOfPreviouslyLoadedTransactions(prevState => {
                    prevState.push(...arrayOfOrdersIdsOnLastPage);
                });

                setIsLoading(false);
            })
            .catch((error) => {

                returnValue = false;
                console.error("Error during fetching order transactions", error);
            });

        return returnValue;
    }

    return (
        <div className={"grid"} style={divStyle}>
            {!isLoading ? (
                <>
                    <div className={"row-start-1 row-span-4 col-start-1 col-span-2 flex items-center justify-items-center"}>
                        <OrderTransactionSearchBar width={"50%"} height={"80%"} startingDateSetter={setStartingDate}
                                                   endingDateSetter={setEndingDate}
                                                   deliveryProviderNameSetter={setDeliveryProviderName}
                                                   paymentMethodNameSetter={setPaymentMethodName}
                                                   userEmailSetter={setUserEmail}
                                                   onSearchButtonClick={searchWhenClickingSearchButtonHandler}/>
                    </div>
                    {currentLoadedTransactions.map(orderTransaction => {
                        return (
                            <div className={"row-span-1 col-span-1"}>
                                <TransactionCard width={"100%"} height={"100%"}
                                                 transactionIdAsString={orderTransaction['id'].toString()}
                                                 transactionStatus={orderTransaction['transactionStatus']} />
                            </div>
                        )
                    })}
                    <div className={"row-span-4 col-span-2"}>
                        <PageNavBar handleMoveBack={handleMoveBack} handleMoveForward={handleMoveForward} />
                    </div>
                </>
            ) : (
                <div className={"row-start-1 row-span-12 col-span-2 flex items-center justify-items-center"}>
                    <p className={"h-4 text-black font-semibold"}>Loading...</p>
                </div>
            )}
        </div>
    );
}

export default OrderTransactionSearchPage;