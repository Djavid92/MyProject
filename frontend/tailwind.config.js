/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        primary:   '#3F3F2C',
        secondary: '#9D9167',
        income:    '#B88D6A',
        expense:   '#A05035',
        bg:        '#CEC1A8',
        surface:   '#E8DFCF',
        muted:     '#7A7260',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
