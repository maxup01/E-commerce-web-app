import React from 'react';

function TransactionCard(props) {

    const divStyle = {

        width: props.width,
        height: props.height,
    }

    const transactionStatusColors = {

        RETURN_ACCEPTED: "#bfab34",
        PAID: '#bfab34',
        PREPARED: '#f3901c',
        SENT: '#8ef896',
        DELIVERED: '#66c81b'
    }

    return (
        <div style={divStyle} className="grid grid-cols-6 grid-rows-2 justify-center
                bg-gray-100 hover:scale-105 hover:drop-shadow-xl rounded-lg p-3">
            <div className={"flex col-start-1 col-span-3 row-start-1 justify-center items-center"}>
                <p className={"ml-4 mt-3 h-3 text-black font-semibold "}>ID: {props.transactionIdAsString}</p>
            </div>
            <div className={"flex col-start-1 col-span-3 row-start-2 row-span-1 justify-center items-center"}>
                <p className={"h-7 text-xl text-black font-light mr-4 mb-1"}>
                    Transaction status: {props.transactionStatus}
                </p>
            </div>
            <div className={"flex col-start-4 col-span-3 row-start-1 row-span-2 justify-center items-center"}>
                <p style={{color: transactionStatusColors[props.transactionStatus]}}>{props.transactionStatus}</p>
            </div>
        </div>
    );
}

export default TransactionCard;