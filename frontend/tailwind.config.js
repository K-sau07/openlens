/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        bg: '#100806',
        'bg-2': '#160C08',
        'bg-3': '#1A0E0A',
        text: '#ECD9B8',
        accent: '#AA5535',
        'accent-2': '#C4714D',
      },
      fontFamily: {
        serif: ['Playfair Display', 'Georgia', 'serif'],
        mono: ['JetBrains Mono', 'monospace'],
        sans: ['DM Sans', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
