/**
 * =============================================
 *  J. SETHURAMAN – PORTFOLIO JAVASCRIPT
 *  Clean, modular, well-commented JavaScript
 *  No external frameworks or libraries used
 * =============================================
 */

'use strict';

/* =============================================
   1. DOM READY HELPER
   ============================================= */
document.addEventListener('DOMContentLoaded', function () {
  initLoadingScreen();
  initParticles();
  initNavbar();
  initHamburgerMenu();
  initTypingAnimation();
  initScrollReveal();
  initSkillBars();
  initContactForm();
  initBackToTop();
  initSmoothScroll();
});

/* =============================================
   2. LOADING SCREEN
   ============================================= */
function initLoadingScreen() {
  const loadingScreen = document.getElementById('loading-screen');
  const loaderBar = document.getElementById('loader-bar');

  if (!loadingScreen || !loaderBar) return;

  // Lock body scroll during loading
  document.body.classList.add('loading');

  // Animate the progress bar
  let progress = 0;
  const interval = setInterval(function () {
    progress += Math.random() * 18 + 5;
    if (progress >= 100) {
      progress = 100;
      clearInterval(interval);
      loaderBar.style.width = '100%';

      // Hide loading screen after a short delay
      setTimeout(function () {
        loadingScreen.classList.add('hidden');
        document.body.classList.remove('loading');

        // Trigger initial animations after load
        triggerHeroAnimation();
      }, 500);
    } else {
      loaderBar.style.width = progress + '%';
    }
  }, 120);
}

// Animate hero elements after loading
function triggerHeroAnimation() {
  const heroElements = document.querySelectorAll('.hero-section [data-reveal]');
  heroElements.forEach(function (el, i) {
    setTimeout(function () {
      el.classList.add('revealed');
    }, i * 200);
  });
}

/* =============================================
   3. PARTICLE BACKGROUND
   ============================================= */
function initParticles() {
  const canvas = document.getElementById('particles-canvas');
  if (!canvas) return;

  // Skip heavy animation if user prefers reduced motion
  if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;

  const ctx = canvas.getContext('2d');
  let particles = [];
  let animationId;

  // Resize canvas to match viewport
  function resizeCanvas() {
    canvas.width  = window.innerWidth;
    canvas.height = window.innerHeight;
  }

  // Particle constructor
  function createParticle() {
    return {
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      radius: Math.random() * 1.5 + 0.3,
      vx: (Math.random() - 0.5) * 0.4,
      vy: (Math.random() - 0.5) * 0.4,
      opacity: Math.random() * 0.5 + 0.1,
      color: ['#3b82f6', '#8b5cf6', '#06b6d4'][Math.floor(Math.random() * 3)]
    };
  }

  function initParticleArray() {
    // Limit particle count for performance
    const count = Math.min(Math.floor((canvas.width * canvas.height) / 12000), 80);
    particles = [];
    for (let i = 0; i < count; i++) {
      particles.push(createParticle());
    }
  }

  function drawParticles() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    particles.forEach(function (p) {
      // Update position
      p.x += p.vx;
      p.y += p.vy;

      // Wrap around edges
      if (p.x < 0)             p.x = canvas.width;
      if (p.x > canvas.width)  p.x = 0;
      if (p.y < 0)             p.y = canvas.height;
      if (p.y > canvas.height) p.y = 0;

      // Draw particle
      ctx.beginPath();
      ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
      ctx.fillStyle = p.color;
      ctx.globalAlpha = p.opacity;
      ctx.fill();
    });

    // Draw connecting lines between nearby particles
    ctx.globalAlpha = 1;
    for (let i = 0; i < particles.length; i++) {
      for (let j = i + 1; j < particles.length; j++) {
        const dx = particles[i].x - particles[j].x;
        const dy = particles[i].y - particles[j].y;
        const dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < 120) {
          ctx.beginPath();
          ctx.moveTo(particles[i].x, particles[i].y);
          ctx.lineTo(particles[j].x, particles[j].y);
          ctx.strokeStyle = 'rgba(59,130,246,' + (0.08 * (1 - dist / 120)) + ')';
          ctx.lineWidth = 0.5;
          ctx.stroke();
        }
      }
    }

    animationId = requestAnimationFrame(drawParticles);
  }

  // Initialize
  resizeCanvas();
  initParticleArray();
  drawParticles();

  // Handle resize (debounced)
  let resizeTimer;
  window.addEventListener('resize', function () {
    clearTimeout(resizeTimer);
    resizeTimer = setTimeout(function () {
      cancelAnimationFrame(animationId);
      resizeCanvas();
      initParticleArray();
      drawParticles();
    }, 250);
  });
}

/* =============================================
   4. NAVBAR – SCROLL BEHAVIOR & ACTIVE LINKS
   ============================================= */
function initNavbar() {
  const navbar = document.getElementById('navbar');
  if (!navbar) return;

  const navLinks = document.querySelectorAll('.nav-link');
  const sections = document.querySelectorAll('section[id]');

  // Add scrolled class when page is scrolled past 60px
  function handleNavScroll() {
    if (window.scrollY > 60) {
      navbar.classList.add('scrolled');
    } else {
      navbar.classList.remove('scrolled');
    }

    // Update active nav link based on current section in view
    updateActiveNavLink(sections, navLinks);
  }

  window.addEventListener('scroll', handleNavScroll, { passive: true });
  handleNavScroll(); // Run on init
}

// Highlight the nav link matching the section currently in the viewport
function updateActiveNavLink(sections, navLinks) {
  let currentSection = '';
  const scrollY = window.scrollY + 120; // offset for navbar height

  sections.forEach(function (section) {
    const sectionTop    = section.offsetTop;
    const sectionHeight = section.offsetHeight;

    if (scrollY >= sectionTop && scrollY < sectionTop + sectionHeight) {
      currentSection = section.getAttribute('id');
    }
  });

  navLinks.forEach(function (link) {
    link.classList.remove('active');
    if (link.getAttribute('data-section') === currentSection) {
      link.classList.add('active');
    }
  });
}

/* =============================================
   5. HAMBURGER MOBILE MENU
   ============================================= */
function initHamburgerMenu() {
  const hamburger    = document.getElementById('hamburger');
  const navLinksList = document.getElementById('nav-links');
  const overlay      = document.getElementById('mobile-overlay');

  if (!hamburger || !navLinksList) return;

  function openMenu() {
    hamburger.classList.add('active');
    navLinksList.classList.add('open');
    if (overlay) {
      overlay.classList.add('active');
      overlay.removeAttribute('aria-hidden');
    }
    hamburger.setAttribute('aria-expanded', 'true');
    document.body.style.overflow = 'hidden';
  }

  function closeMenu() {
    hamburger.classList.remove('active');
    navLinksList.classList.remove('open');
    if (overlay) {
      overlay.classList.remove('active');
      overlay.setAttribute('aria-hidden', 'true');
    }
    hamburger.setAttribute('aria-expanded', 'false');
    document.body.style.overflow = '';
  }

  // Toggle on hamburger click
  hamburger.addEventListener('click', function () {
    const isOpen = navLinksList.classList.contains('open');
    if (isOpen) { closeMenu(); } else { openMenu(); }
  });

  // Close when a nav link is clicked
  navLinksList.querySelectorAll('.nav-link').forEach(function (link) {
    link.addEventListener('click', function () {
      closeMenu();
    });
  });

  // Close on overlay click
  if (overlay) {
    overlay.addEventListener('click', closeMenu);
  }

  // Close on Escape key
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape' && navLinksList.classList.contains('open')) {
      closeMenu();
      hamburger.focus();
    }
  });
}

/* =============================================
   6. TYPING ANIMATION (Hero Section)
   ============================================= */
function initTypingAnimation() {
  const typingEl = document.getElementById('typing-text');
  if (!typingEl) return;

  // Words to rotate through
  const words = [
    'Java Developer',
    'Full Stack Developer',
    'Problem Solver',
    'Spring Boot Dev'
  ];

  let wordIndex   = 0;
  let charIndex   = 0;
  let isDeleting  = false;
  let typingSpeed = 100; // ms per character

  function type() {
    const currentWord = words[wordIndex];

    if (!isDeleting) {
      // Typing forward
      typingEl.textContent = currentWord.slice(0, charIndex + 1);
      charIndex++;

      if (charIndex === currentWord.length) {
        // Pause at end of word before deleting
        isDeleting = true;
        typingSpeed = 1800; // hold pause
      } else {
        typingSpeed = 90 + Math.random() * 40; // variable speed for realism
      }
    } else {
      // Deleting
      typingEl.textContent = currentWord.slice(0, charIndex - 1);
      charIndex--;

      if (charIndex === 0) {
        isDeleting  = false;
        wordIndex   = (wordIndex + 1) % words.length;
        typingSpeed = 300; // pause before typing new word
      } else {
        typingSpeed = 50 + Math.random() * 20;
      }
    }

    setTimeout(type, typingSpeed);
  }

  // Start typing after a short delay
  setTimeout(type, 1200);
}

/* =============================================
   7. SCROLL REVEAL ANIMATIONS
   ============================================= */
function initScrollReveal() {
  const revealElements = document.querySelectorAll('[data-reveal]');
  if (!revealElements.length) return;

  // Skip hero elements – they are triggered after loading screen
  const nonHeroReveal = Array.from(revealElements).filter(function (el) {
    return !el.closest('.hero-section');
  });

  const observer = new IntersectionObserver(
    function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          entry.target.classList.add('revealed');
          observer.unobserve(entry.target); // Only animate once
        }
      });
    },
    {
      threshold: 0.12,
      rootMargin: '0px 0px -40px 0px'
    }
  );

  nonHeroReveal.forEach(function (el) {
    observer.observe(el);
  });
}

/* =============================================
   8. ANIMATED SKILL BARS
   ============================================= */
function initSkillBars() {
  const skillBars = document.querySelectorAll('.skill-bar');
  if (!skillBars.length) return;

  const observer = new IntersectionObserver(
    function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          // Small delay for stagger effect
          setTimeout(function () {
            entry.target.classList.add('animated');
          }, 200);
          observer.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.3 }
  );

  skillBars.forEach(function (bar) {
    observer.observe(bar);
  });
}

/* =============================================
   9. SMOOTH SCROLL (for anchor links)
   ============================================= */
function initSmoothScroll() {
  // The native html { scroll-behavior: smooth } handles most cases.
  // This function provides a JS fallback with navbar offset compensation.
  document.querySelectorAll('a[href^="#"]').forEach(function (anchor) {
    anchor.addEventListener('click', function (e) {
      const targetId = this.getAttribute('href');
      if (!targetId || targetId === '#') return;

      const targetEl = document.querySelector(targetId);
      if (!targetEl) return;

      e.preventDefault();

      const navbarHeight = document.getElementById('navbar')
        ? document.getElementById('navbar').offsetHeight
        : 80;

      const targetTop = targetEl.getBoundingClientRect().top + window.scrollY - navbarHeight;

      window.scrollTo({
        top: targetTop,
        behavior: 'smooth'
      });
    });
  });
}

/* =============================================
   10. CONTACT FORM VALIDATION
   ============================================= */
function initContactForm() {
  const form          = document.getElementById('contact-form');
  const successMsg    = document.getElementById('form-success');
  const submitBtn     = document.getElementById('form-submit-btn');

  if (!form) return;

  // Input elements
  const nameInput     = document.getElementById('contact-name');
  const emailInput    = document.getElementById('contact-email');
  const subjectInput  = document.getElementById('contact-subject');
  const messageInput  = document.getElementById('contact-message');

  // Error elements
  const nameError     = document.getElementById('name-error');
  const emailError    = document.getElementById('email-error');
  const subjectError  = document.getElementById('subject-error');
  const messageError  = document.getElementById('message-error');

  // Minimum message character count
  const MIN_MSG_LENGTH = 20;

  // ---- Helper: validate email format ----
  function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email.trim());
  }

  // ---- Helper: show field error ----
  function showError(input, errorEl, message) {
    errorEl.textContent = message;
    input.classList.add('error');
    input.setAttribute('aria-invalid', 'true');
  }

  // ---- Helper: clear field error ----
  function clearError(input, errorEl) {
    errorEl.textContent = '';
    input.classList.remove('error');
    input.setAttribute('aria-invalid', 'false');
  }

  // ---- Real-time validation on input ----
  function validateField(input, errorEl) {
    const val = input.value.trim();

    if (input === emailInput) {
      if (!val) {
        showError(input, errorEl, 'Email address is required.');
        return false;
      }
      if (!isValidEmail(val)) {
        showError(input, errorEl, 'Please enter a valid email address.');
        return false;
      }
    } else if (input === messageInput) {
      if (!val) {
        showError(input, errorEl, 'Message is required.');
        return false;
      }
      if (val.length < MIN_MSG_LENGTH) {
        showError(input, errorEl, 'Message must be at least ' + MIN_MSG_LENGTH + ' characters.');
        return false;
      }
    } else {
      if (!val) {
        showError(input, errorEl, getRequiredMsg(input));
        return false;
      }
    }

    clearError(input, errorEl);
    return true;
  }

  function getRequiredMsg(input) {
    if (input === nameInput)    return 'Full name is required.';
    if (input === subjectInput) return 'Subject is required.';
    return 'This field is required.';
  }

  // Attach real-time validation (blur event)
  [
    [nameInput, nameError],
    [emailInput, emailError],
    [subjectInput, subjectError],
    [messageInput, messageError]
  ].forEach(function ([input, error]) {
    if (input) {
      input.addEventListener('blur', function () {
        validateField(input, error);
      });
      input.addEventListener('input', function () {
        if (input.classList.contains('error')) {
          validateField(input, error);
        }
      });
    }
  });

  // ---- Form Submit ----
  form.addEventListener('submit', function (e) {
    e.preventDefault();

    // Validate all fields
    const isNameValid    = validateField(nameInput,    nameError);
    const isEmailValid   = validateField(emailInput,   emailError);
    const isSubjectValid = validateField(subjectInput, subjectError);
    const isMsgValid     = validateField(messageInput, messageError);

    if (!isNameValid || !isEmailValid || !isSubjectValid || !isMsgValid) {
      // Focus the first invalid field
      if (!isNameValid)    { nameInput.focus();    return; }
      if (!isEmailValid)   { emailInput.focus();   return; }
      if (!isSubjectValid) { subjectInput.focus(); return; }
      if (!isMsgValid)     { messageInput.focus(); return; }
      return;
    }

    // All valid – simulate a short loading state
    if (submitBtn) {
      submitBtn.disabled = true;
      submitBtn.querySelector('span').textContent = 'Sending...';
    }

    // DEVELOPER NOTE:
    // This is a front-end only form.
    // To send real emails, integrate one of the following:
    //   1. Formspree:  https://formspree.io  → set action="https://formspree.io/f/YOUR_ID"
    //   2. EmailJS:    https://www.emailjs.com → call emailjs.sendForm(...)
    //   3. Your own backend REST API → fetch('/api/contact', { method:'POST', ... })
    // Replace the setTimeout below with your actual API call.

    setTimeout(function () {
      // Show success message
      form.hidden = true;
      if (successMsg) {
        successMsg.hidden = false;
        successMsg.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }

      // Reset form (in case user comes back)
      form.reset();
      if (submitBtn) {
        submitBtn.disabled = false;
        submitBtn.querySelector('span').textContent = 'Send Message';
      }
    }, 1000);
  });
}

/* =============================================
   11. BACK TO TOP BUTTON
   ============================================= */
function initBackToTop() {
  const backToTopBtn = document.getElementById('back-to-top');
  if (!backToTopBtn) return;

  // Show/hide based on scroll position
  window.addEventListener('scroll', function () {
    if (window.scrollY > 400) {
      backToTopBtn.hidden = false;
      // Use rAF to ensure display is set before adding class
      requestAnimationFrame(function () {
        backToTopBtn.classList.add('visible');
      });
    } else {
      backToTopBtn.classList.remove('visible');
      // Remove hidden after transition ends
      backToTopBtn.addEventListener('transitionend', function handler() {
        if (!backToTopBtn.classList.contains('visible')) {
          backToTopBtn.hidden = true;
        }
        backToTopBtn.removeEventListener('transitionend', handler);
      });
    }
  }, { passive: true });

  // Scroll to top on click
  backToTopBtn.addEventListener('click', function () {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  });
}

/* =============================================
   12. UTILITY: DEBOUNCE
   ============================================= */
function debounce(fn, delay) {
  let timer;
  return function () {
    clearTimeout(timer);
    timer = setTimeout(fn.apply.bind(fn, this, arguments), delay);
  };
}

/* =============================================
   13. SKILL CARD HOVER GLOW (subtle enhancement)
   ============================================= */
(function () {
  const skillCards = document.querySelectorAll('.skill-card');

  skillCards.forEach(function (card) {
    card.addEventListener('mousemove', function (e) {
      const rect   = card.getBoundingClientRect();
      const x      = ((e.clientX - rect.left) / rect.width  - 0.5) * 10;
      const y      = ((e.clientY - rect.top)  / rect.height - 0.5) * 10;
      card.style.transform = 'translateY(-6px) rotateX(' + (-y) + 'deg) rotateY(' + x + 'deg)';
    });

    card.addEventListener('mouseleave', function () {
      card.style.transform = '';
    });
  });
})();

/* =============================================
   14. PROJECT CARD TILT EFFECT
   ============================================= */
(function () {
  const projectCards = document.querySelectorAll('.project-card');

  projectCards.forEach(function (card) {
    card.addEventListener('mousemove', function (e) {
      const rect = card.getBoundingClientRect();
      const x    = ((e.clientX - rect.left) / rect.width  - 0.5) * 6;
      const y    = ((e.clientY - rect.top)  / rect.height - 0.5) * 6;
      card.style.transform = 'translateY(-8px) rotateX(' + (-y) + 'deg) rotateY(' + x + 'deg)';
    });

    card.addEventListener('mouseleave', function () {
      card.style.transform = '';
    });
  });
})();

/* =============================================
   15. PERFORMANCE: Only run animations when
       the hero section is in the viewport
   ============================================= */
(function () {
  const heroSection = document.getElementById('home');
  const canvas      = document.getElementById('particles-canvas');
  if (!heroSection || !canvas) return;

  const observer = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      // Stop canvas animation when hero is out of view to save CPU
      if (entry.isIntersecting) {
        canvas.style.opacity = '1';
      } else {
        canvas.style.opacity = '0';
      }
    });
  }, { threshold: 0 });

  observer.observe(heroSection);
})();

/* =============================================
   16. CURRENT YEAR IN FOOTER (auto-update)
   ============================================= */
(function () {
  // If you ever want the year to auto-update, uncomment:
  // const yearEls = document.querySelectorAll('.footer-copy');
  // const year = new Date().getFullYear();
  // yearEls.forEach(el => {
  //   el.innerHTML = el.innerHTML.replace(/\d{4}/, year);
  // });
})();
