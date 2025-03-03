import React from 'react';
import '../../css/index.css';

function ProductCard(props) {

    return (
        <div style={{height: props.height, width: props.width}} className="grid grid-cols-6 grid-rows-4 justify-center
        bg-gray-100 hover:scale-105 hover:drop-shadow-xl rounded-lg p-3">
            <img src={props.imageSrc} alt="" className={"w-full h-full col-span-6 row-span-2 rounded-xl"} />
            <div className={"flex w-full h-full col-span-3 row-span-2 justify-items-start"}>
                <p className={"ml-4 mt-3 h-3 text-black font-semibold"}>{props.name}</p>
            </div>
            <div className={"flex w-full h-full col-span-3 row-span-2 justify-end items-end"}>
                <p className={"h-7 text-xl text-black font-light mr-4 mb-1"}>{props.price}$</p>
            </div>
        </div>
    )
}

export default ProductCard;