import React, {useEffect} from 'react';
import axios from 'axios';

function ProductSearchBar(props) {

    const divStyle = {
        width: props.width,
        height: props.height
    }

    const [types, setTypes] = React.useState([]);

    useEffect(() => {
        axios
            .get('http://127.0.0.1:8080/all-product-types')
            .then(res => {
                setTypes(res.data);
            });
    });

    function handleTypeChange(event) {
        props.typeSetter(event.target.value !== "" ? event.target.value : null);
    }

    function handleInputChange(event) {
        props.inputDataSetter(event.target.value !== "" ? event.target.value : null);
    }

    function handleKeyPress(event) {

        if(event.key === 'Enter') {
            props.searchMethod();
        }
    }

    function handleMinimalValueChange(event) {
        props.minimalPriceSetter(event.target.value < 0 ? 0 : event.target.value);
    }

    function handleMaximalValueChange(event) {
        props.maximalPriceSetter(event.target.value < 0 ? 0 : event.target.value);
    }

    return (
        <div className={"grid grid-cols-6 grid-rows-2"} style={divStyle}>
            <select onChange={handleTypeChange} id={"type"} name={"type"}
                    className={"bg-cyan-50 row-start-1 row-span-1 col-start-1 col-span-2 "}>
                {types.map(type => (
                    <option key={type} value={type}>{type}</option>
                ))}
            </select>
            <input type={"text"} placeholder={"Search"} onChange={handleInputChange} onKeyDown={handleKeyPress}
                   className={"bg-cyan-50 placeholder-gray-700 row-start-1 row-span-1 col-start-3 col-span-3"} />
            <input type={"number"} placeholder={"Minimal price"} onChange={handleMinimalValueChange}
                   className={"bg-cyan-50 placeholder-gray-700 row-start-2 row-span-1 col-start-2 col-span-2"} />
            <input type={"number"} placeholder={"Maximal price"} onChange={handleMaximalValueChange}
                   className={"bg-cyan-50 placeholder-gray-700 row-start-2 row-span-1 col-start-3 col-span-2"} />
        </div>
    );
}

export default ProductSearchBar;