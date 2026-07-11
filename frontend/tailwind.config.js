/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        display: ['"Fraunces"', 'ui-serif', 'Georgia', 'serif'],
        sans: [
          '"Manrope"',
          'ui-sans-serif',
          'system-ui',
          '-apple-system',
          'Segoe UI',
          'Roboto',
          'sans-serif',
        ],
        mono: ['"JetBrains Mono"', 'ui-monospace', 'SFMono-Regular', 'monospace'],
      },
      colors: {
        ink: {
          50:  '#f6f7fb',
          100: '#e9ecf4',
          200: '#c6cddf',
          300: '#94a1c0',
          400: '#5d6d95',
          500: '#3e4e75',
          600: '#2d3a5b',
          700: '#1d2643',
          800: '#131a31',
          900: '#0a0f22',
        },
        accent: {
          DEFAULT: '#e2b23d',
          soft:    '#f4d68a',
          deep:    '#a8801e',
        },
        moss: {
          DEFAULT: '#5b8a5a',
          soft:    '#c1d9c0',
          deep:    '#345b34',
        },
      },
      boxShadow: {
        card: '0 1px 2px rgba(15,23,42,0.04), 0 8px 24px rgba(15,23,42,0.06)',
        pop: '0 20px 60px -20px rgba(15,23,42,0.35)',
      },
      backgroundImage: {
        'noise':
          "url(\"data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='120' height='120'><filter id='n'><feTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='2' stitchTiles='stitch'/></filter><rect width='100%25' height='100%25' filter='url(%23n)' opacity='0.06'/></svg>\")",
      },
      keyframes: {
        floatIn: {
          '0%': { opacity: 0, transform: 'translateY(8px)' },
          '100%': { opacity: 1, transform: 'translateY(0)' },
        },
      },
      animation: {
        'float-in': 'floatIn 240ms ease-out both',
      },
    },
  },
  plugins: [],
};
