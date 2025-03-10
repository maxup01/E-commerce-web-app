import React, {useEffect, useState} from 'react';
import axios from "axios";
import EANValidator from "../../util/EanCodeValidator";

//TODO add redirect after product creation
function ProductCreationPage() {

    const divStyle = {

        width: '100%',
        height: '100%'
    }

    const [eanCode, setEanCode] = React.useState(null);
    const [name, setName] = React.useState(null);
    const [type, setType] = React.useState(null);
    const [price, setPrice] = React.useState(null);
    const [description, setDescription] = React.useState(null);
    const [width, setWidth] = React.useState(null);
    const [height, setHeight] = React.useState(null);
    const [imageBytes, setImageBytes] = React.useState([]);

    const [isEanCorrect, setIsEanCorrect] = React.useState(true);
    const [isNameCorrect, setIsNameCorrect] = React.useState(true);
    const [isTypeCorrect, setIsTypeCorrect] = React.useState(true);
    const [isPriceCorrect, setIsPriceCorrect] = React.useState(true);
    const [isDescriptionCorrect, setIsDescriptionCorrect] = React.useState(true);
    const [isWidthCorrect, setIsWidthCorrect] = React.useState(true);
    const [isHeightCorrect, setIsHeightCorrect] = React.useState(true);
    const [isImageCorrect, setIsImageCorrect] = React.useState(true);

    const [types, setTypes] = useState([]);
    const [preview, setPreview] = React.useState(null);

    useEffect(() => {
        axios
            .get('http://127.0.0.1:8080/all-product-types')
            .then(res => {
                setTypes(res.data);
            });
    });

    function handleEanCodeChange(event) {

        setEanCode(event.target.value);

        if(!EANValidator.validate(event.target.value)) {

            setIsEanCorrect(false);
        }
        else {

            setIsEanCorrect(true);
        }
    }

    function handleNameChange(event) {

        setName(event.target.value);

        if(event.target.value.toString().trim().length === 0) {

            setIsNameCorrect(false);
        }
        else{
            setIsNameCorrect(true);
        }
    }

    function handleTypeChange(event) {

        setType(event.target.value);
        setIsTypeCorrect(true);
    }

    function handlePriceChange(event) {

        setPrice(event.target.value);

        if(event.target.value <= 0) {
            setIsPriceCorrect(false);
        }
        else {
            setIsPriceCorrect(true);
        }
    }

    function handleDescriptionChange(event) {

        setDescription(event.target.value);

        if(event.target.value.toString().trim().length === 0) {
            setIsDescriptionCorrect(false);
        }
        else {
            setIsDescriptionCorrect(true);
        }
    }

    function handleWidthChange(event) {

        setWidth(event.target.value);

        if(event.target.value <= 0) {
            setIsWidthCorrect(false);
        } else {
            setIsWidthCorrect(true);
        }
    }

    function handleHeightChange(event) {

        setHeight(event.target.value);

        if(event.target.value <= 0) {
            setIsHeightCorrect(false);
        }
        else {
            setIsHeightCorrect(true);
        }
    }

    function handleImageChange(event) {

        const file = event.target.files[0];

        if (file) {

            setPreview(URL.createObjectURL(file));

            const reader = new FileReader();
            reader.readAsArrayBuffer(file);

            reader.onload = () => {
                const byteArray = new Uint8Array(reader.result);
                setImageBytes(Array.from(byteArray));
            };

            setIsImageCorrect(true);
        }
    }

    function handleSubmit(){

        if((eanCode !== null) && (isEanCorrect) && (name !== null) && (isNameCorrect)
            && (type !== null) && (isTypeCorrect) && (price !== null) && (isPriceCorrect)
            && (description !== null) && (isDescriptionCorrect) && (width !== null) && (isWidthCorrect)
            && (height !== null) && (isHeightCorrect) && (imageBytes !== null) && (isImageCorrect) ) {

            axios.post('http://127.0.0.1:8080/manager/create-product', {
                EANCode: eanCode,
                name: name,
                type: type,
                description: description,
                width: width,
                height: height,
                regularPrice: price,
                currentPrice: price,
                mainImage: imageBytes
            })
                .then(response => {

                })
                .catch(err => {

                    console.log("Error creating product:", err);
                });
        }
    }

    return  (
        <div className={"grid grid-rows-6 grid-cols-2"} style={divStyle}>
            <div className={"row-start-1 row-span-1 col-start-1 col-span-2 flex items-center justify-center p-2"}>
                <h1 className={"font-bold"}>PRODUCT CREATOR</h1>
            </div>
            <div className={"row-start-2 row-span-4 col-start-1 col-span-1 p-2"}>
                <div className={" grid grid-cols-1 grid-rows-9"}>
                    <div className={"row-start-1 row-span-1 col-span-1"}>
                        <input id={"eanCode"} name={"eanCode"} onChange={handleEanCodeChange}
                               className={"bg-cyan-50 w-1/2 h-full"} placeholder={"8 or 13 digit EAN code"} />
                        {!isEanCorrect && (
                            <p className={"font-bold text-red-800 ml-1 w-1/2 h-full"}>Incorrect EAN code!!!</p>
                        )}
                    </div>
                    <div className={"row-start-2 row-span-2 col-span-1"}>
                        <input id={"name"} name={"name"} onChange={handleNameChange}
                               className={"bg-cyan-50 w-1/2 h-full"} />
                        {!isNameCorrect && (
                            <p className={"font-bold text-red-800 ml-1 w-1/2 h-full"}>Incorrect name!!!</p>
                        )}
                    </div>
                    <div className={"row-start-3 row-span-1 col-start-1 col-span-1"}>
                        <select id={"type"} name={"type"} onChange={handleTypeChange}
                                className={"bg-cyan-50 w-1/4 h-full hover:accent-gray-400"}>
                            {types.map(type => (
                                <option key={type} value={type}>{type}</option>
                            ))}
                        </select>
                        <input id={"price"} name={"price"} onChange={handlePriceChange}
                               className={"bg-cyan-50 w-1/4 h-full"} />
                        {!isTypeCorrect || !isPriceCorrect && (
                            <p className={"font-bold text-red-800 ml-1 w-1/2 h-full"}>One or more fields here are incorrect!!!</p>
                        )}
                    </div>
                    <div className={"row-start-4 row-span-1 col-start-1 col-span-1"}>
                        <input id={"width"} name={"width"} className={"bg-cyan-50 w-1/4 h-full"}
                               onChange={handleWidthChange}/>
                        <input id={"height"} name={"height"} className={"bg-cyan-50 w-1/4 h-full"}
                               onChange={handleHeightChange}/>
                        {!isWidthCorrect || !isHeightCorrect && (
                            <p className={"font-bold text-red-800 ml-1 w-1/2 h-full"}>Incorrect size!!!</p>
                        )}
                    </div>
                    <input id={"description"} name={"description"} onChange={handleDescriptionChange}
                           className={"bg-cyan-50 row-start-5 row-span-4 col-start-1 col-span-1"} />
                    <div className="row-start-9 row-span-1 col-start-1 col-span-1">
                        <input type="file" accept=".png, .jpg, .jpeg" onChange={handleImageChange}
                               className="bg-cyan-50 w-1/2 h-full" />
                        {!isImageCorrect && (
                            <p className={"font-bold text-red-800 ml-1 w-1/2 h-full"}>Image needs to be uploaded!!!</p>
                        )}
                    </div>
                </div>
            </div>
            <div className={"row-start-2 row-span-4 col-start-2 col-span-1 flex items-center justify-items-center p-2"}>
                {preview !== null && (
                    <img src={preview} alt="Preview" className="w-1/3 h-1/2 object-cover" />
                )}
            </div>
            <div className={"row-start-6 row-span-1 col-start-1 col-span-2 flex p-2"}>
                <button className={"accent-green-500 rounded-xl font-semibold text-center hover:accent-green-700"}
                        onClick={handleSubmit}>
                    Create
                </button>
            </div>
        </div>
    );
}

export default ProductCreationPage;