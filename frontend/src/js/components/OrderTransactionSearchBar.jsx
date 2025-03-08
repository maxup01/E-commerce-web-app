import React, {useEffect, useState} from 'react';
import axios from 'axios';

function OrderTransactionSearchBar(props) {

    const divStyle = {

        width: props.width,
        height: props.height,
    }

    const [deliveryProviders, setDeliveryProviders] = useState([]);
    const [paymentMethods, setPaymentMethods] = useState([]);

    useEffect(() => {

        axios.get('http://127.0.0.1:8080/all-delivery-provider-names')
            .then(response => {
                setDeliveryProviders(response.data);
        });

        axios.get('http://127.0.0.1:8080/all-payment-method-names')
            .then(response => {
                setPaymentMethods(response.data);
            });
    });

    return (
        <div className={"grid grid-cols-6 grid-rows-2"} style={divStyle}>
            <input type={"date"} placeholder={"Starting date"} onChange={props.handleStartingDateChange}
                   className={"bg-cyan-50 placeholder-gray-700 row-start-1 row-span-1 col-start-2 col-span-2"} />
            <input type={"date"} placeholder={"Ending date"} onChange={props.handleEndingDateChange}
                   className={"bg-cyan-50 placeholder-gray-700 row-start-1 row-span-1 col-start-4 col-span-2"} />
            <select onChange={props.handleDeliveryProviderChange}
                    className={"bg-cyan-50 row-start-2 row-span-1 col-start-1 col-span-2 "}>
                {deliveryProviders.map(deliveryProvider => (
                    <option key={deliveryProvider} value={deliveryProvider}>{deliveryProvider}</option>
                ))}
            </select>
            <select onChange={props.handlePaymentMethodName}
                    className={"bg-cyan-50 row-start-2 row-span-1 col-start-3 col-span-2 "}>
                {paymentMethods.map(paymentMethod => (
                    <option key={paymentMethod} value={paymentMethod}>{paymentMethod}</option>
                ))}
            </select>
            <input type={"email"} placeholder={"Email"} onChange={props.handleEmailChange}
                   className={"bg-cyan-50 placeholder-gray-700 row-start-1 row-span-1 col-start-5 col-span-2"} />
        </div>
    );
}