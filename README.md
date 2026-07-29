# J. Sethuraman – Personal Portfolio Website

> **A premium, modern, fully responsive personal portfolio website for J. Sethuraman, Junior Full Stack Developer & 2026 B.E. CSE Graduate.**

---

## 🚀 Live Preview

Open `index.html` directly in any modern browser. No build tools or server setup required.

---

## 📁 Project Structure

```
portfolio/
├── index.html              ← Main HTML file (all sections)
├── css/
│   └── style.css           ← Complete stylesheet (CSS variables, responsive)
├── js/
│   └── script.js           ← All JavaScript features (no libraries)
├── assets/
│   ├── resume.pdf          ← ⚠️ Place your resume PDF here
│   └── images/
│       ├── favicon.png     ← ⚠️ Place your favicon here (32×32 PNG)
│       └── og-image.png    ← ⚠️ Place Open Graph image here (1200×630)
└── README.md               ← This file
```

---

## ✅ Features

### Design
- 🎨 **Premium Dark Theme** – Deep black/navy with blue-purple-cyan gradient accents
- 💎 **Glassmorphism Cards** – Frosted-glass effect with subtle borders
- ✨ **Soft Glow Effects** – Neon-style glows on hover and focus states
- 🖋️ **Modern Typography** – Inter + JetBrains Mono fonts from Google Fonts
- 📐 **CSS Grid & Flexbox** – Clean, professional, responsive layouts
- 🎨 **CSS Custom Properties** – Unified design token system for easy theming

### Sections
| # | Section | Description |
|---|---------|-------------|
| 1 | Loading Screen | Animated progress bar with logo |
| 2 | Navigation Bar | Fixed glassmorphism nav with active link |
| 3 | Hero Section | Typing animation, code card visual, particle BG |
| 4 | About Me | Personal info cards + download resume |
| 5 | Skills | Animated skill bars grouped by category |
| 6 | Projects | Featured project cards with GitHub/Demo links |
| 7 | Professional Journey | Vertical timeline with milestones |
| 8 | Career Objective | CTA section with Hire Me button |
| 9 | Contact | Contact form + social links |
| 10 | Footer | Copyright + back-to-top button |

### JavaScript Features
- ⌛ Loading screen with animated progress bar
- 🔤 Typing animation cycling through 4 roles
- 🎯 Scroll-triggered reveal animations
- 📊 Animated skill progress bars (IntersectionObserver)
- 📱 Mobile hamburger menu with overlay
- 🔗 Active nav link highlighting on scroll
- ✅ Full contact form validation (empty, email, min-length)
- 🎉 Success message after form submit
- ⬆️ Back-to-top button with smooth scroll
- 🎨 3D tilt effect on skill cards and project cards
- 🌌 Canvas particle background animation

### Performance & Accessibility
- ⚡ `prefers-reduced-motion` respected (all heavy animations disabled for users who prefer it)
- ♿ Semantic HTML5 elements throughout
- 🏷️ ARIA labels, roles, and `aria-live` regions
- ⌨️ Keyboard-friendly (`:focus-visible` styles)
- 📱 Fully responsive: Desktop → Tablet → Mobile → Small screens
- 🖨️ Print-friendly styles

---

## 🔧 Setup & Customization

### 1. Add Your Resume
Place your resume PDF at:
```
assets/resume.pdf
```

### 2. Add Your Favicon
Place a 32×32 PNG at:
```
assets/images/favicon.png
```

### 3. Update Project Links
In `index.html`, search for:
```html
<!-- DEVELOPER NOTE: Replace '#' with your actual GitHub repo URL -->
```
Replace `href="#"` with your real GitHub and Live Demo URLs for each project.

### 4. Enable Real Email (Contact Form)
The form currently uses **front-end validation only** and does not send real emails.

To enable real email sending, choose one:

**Option A – Formspree (easiest, free tier available)**
```html
<form id="contact-form" action="https://formspree.io/f/YOUR_FORM_ID" method="POST">
```
Sign up at [formspree.io](https://formspree.io) to get your form ID.

**Option B – EmailJS**
Add to `<head>`:
```html
<script src="https://cdn.emailjs.com/dist/email.min.js"></script>
<script>emailjs.init("YOUR_USER_ID");</script>
```
Then in `script.js`, replace the `setTimeout` in `initContactForm()` with:
```js
emailjs.sendForm('YOUR_SERVICE', 'YOUR_TEMPLATE', form)
  .then(() => { /* show success */ })
  .catch(err => { console.error(err); });
```

**Option C – Your Own Backend**
```js
fetch('/api/contact', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ name, email, subject, message })
});
```

---

## 🎨 Color Customization

All colors are defined as CSS variables in `css/style.css` under `:root {}`.

To change the accent color scheme, edit:
```css
:root {
  --accent-blue:   #3b82f6;  /* Primary accent */
  --accent-purple: #8b5cf6;  /* Secondary accent */
  --accent-cyan:   #06b6d4;  /* Tertiary accent */
}
```

---

## 🌐 Browser Support

| Browser | Support |
|---------|---------|
| Chrome 90+ | ✅ Full |
| Firefox 88+ | ✅ Full |
| Edge 90+ | ✅ Full |
| Safari 14+ | ✅ Full |
| Opera 76+ | ✅ Full |

---

## 📱 Responsive Breakpoints

| Breakpoint | Width | Layout |
|-----------|-------|--------|
| Desktop | > 1024px | 2-column grid layouts |
| Tablet | 768px – 1024px | 1-column, centered |
| Mobile | 480px – 768px | Single column + hamburger menu |
| Small Mobile | < 480px | Compact layout, reduced spacing |

---

## 👤 Personal Details

| Field | Value |
|-------|-------|
| Name | J. Sethuraman |
| Role | Junior Full Stack Developer |
| Email | sethu4637@gmail.com |
| Phone | +91 9345659273 |
| LinkedIn | [sethuraman-j-093b2129b](https://www.linkedin.com/in/sethuraman-j-093b2129b) |
| GitHub | [sethu4637-cell](https://github.com/sethu4637-cell) |
| College | R.V.S. College of Engineering |
| Degree | B.E. Computer Science & Engineering |
| Graduation | 2026 |

---

## 📄 License

This project is personal and proprietary. All content belongs to **J. Sethuraman**.

---

*Designed and Developed by J. Sethuraman · © 2026*
