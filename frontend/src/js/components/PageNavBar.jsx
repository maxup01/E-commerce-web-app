import React from 'react';

function PageNavBar(props) {

    const divStyle = {
        width: '64px',
        height: '64px'
    }

    const [currentPage, setCurrentPage] = React.useState(1);

    function handleLeftArrowClick() {

        if((currentPage > 1) && (props.handleMoveBack())) {
            setCurrentPage(prevState => prevState - 1);
        }
    }

    function handleRightArrowClick() {

        if(props.handleMoveForward()) {
            setCurrentPage(prevState => prevState + 1);
        }
    }

    return (
        <div className={"grid grid-rows-2 grid-cols-2"} style={divStyle}>
            <img src={"/images/left-arrow.png"} alt="left-arrow" onClick={handleLeftArrowClick}
                 className={"col-span-1 row-span-1 col-start-1 row-start-1"} />
            <img src={"/images/right-arrow.png"} alt="right-arrow" onClick={handleRightArrowClick}
                 className={"col-span-1 row-span-1 col-start-2 row-start-1"} />
            <div className={"flex items-center justify-center col-start-1 row-start-2 col-span-2"}>
                <h5>Page: {currentPage}</h5>
            </div>
        </div>
    );
}

export default PageNavBar;