class EANValidator {

    static validate(ean) {

        // Regular expression to check if it's exactly 8 or 13 digits
        const regex = /^(?:\d{8}|\d{13})$/;

        return regex.test(ean);
    }
}

export default EANValidator;