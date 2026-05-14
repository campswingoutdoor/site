/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/main/resources/templates/**/*.html',
    './src/main/resources/static/js/**/*.js'
  ],
  theme: {
    extend: {
      colors: {
        bg: {
          primary: '#0B1426',
          secondary: '#0F1A2E',
          card: '#16243D'
        },
        gold: {
          DEFAULT: '#C9A35B',
          light: '#D4B36A'
        },
        glow: '#F2C078',
        text: {
          DEFAULT: '#F8EFD8',
          muted: '#9A8C6E'
        },
        cta: {
          DEFAULT: '#2F5D3A',
          hover: '#3C7A4A'
        }
      },
      fontFamily: {
        display: ['Cinzel', 'Playfair Display', 'serif'],
        heading: ['Pretendard', 'Noto Sans KR', 'sans-serif'],
        body: ['Pretendard', 'Noto Sans KR', 'sans-serif'],
        en: ['Inter', 'sans-serif']
      },
      boxShadow: {
        glow: '0 0 20px rgba(242, 192, 120, 0.3)',
        card: '0 8px 32px rgba(0, 0, 0, 0.4)'
      },
      backgroundImage: {
        'hero-main': "linear-gradient(rgba(11,20,38,0.55), rgba(11,20,38,0.95)), url('/img/hero-main.jpeg')",
        'hero-full': "linear-gradient(rgba(11,20,38,0.55), rgba(11,20,38,0.95)), url('/img/hero-full.jpeg')"
      }
    }
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('@tailwindcss/typography')
  ]
};
