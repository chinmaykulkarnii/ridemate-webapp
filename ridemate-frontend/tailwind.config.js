/** @type {import('tailwindcss').Config} */
module.exports = {
  // Scan all JSX/JS files so unused classes get purged in production builds
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {},
  },
  plugins: [],
}

