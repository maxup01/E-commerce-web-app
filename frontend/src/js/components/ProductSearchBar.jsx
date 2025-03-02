import React, {useEffect} from 'react';
import axios from 'axios';


function ProductSearchBar(props) {

    const [types, setTypes] = React.useState([]);

    useEffect(() => {
        axios
            .get('http://127.0.0.1:8080/all-product-types')
            .then(res => {
                setTypes(res.data);
            });
    });

    const divStyle = {
        width: props.width,
        height: props.height
    }

    return (
        <div style={divStyle}>
            <select id={"type"} className={"bg-cyan-50 w-1/5"}>
                {types.map(type => (
                    <option key={type} value={type}>{type}</option>
                ))}
            </select>
            <input type={"text"} className={"bg-cyan-50 w-4/5 placeholder-gray-700"} placeholder={"Search"} />
        </div>
    );
}

export default ProductSearchBar;