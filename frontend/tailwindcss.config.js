module.exports = {
    content: [
        "./src/*.{js,jsx,ts,tsx}",
    ],
    theme: {
        extend: {
            animation: {
                'bounce-limited': 'bounce 2s ease-in-out 2' // Bounces twice, then stops
            },
            boxShadow: {
                neon: "0 0 5px theme('colors.purple.200'), 0 0 20 px theme('colors.white.100')"
            }
        }
    },
    plugins: [],
}