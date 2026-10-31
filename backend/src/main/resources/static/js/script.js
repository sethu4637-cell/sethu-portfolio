/**
 * =============================================
 *  J. SETHURAMAN – PORTFOLIO JAVASCRIPT
 *  Clean, modular, well-commented JavaScript
 *  No external frameworks or libraries used
 * =============================================
 */

"use strict";

/* =============================================
   1. DOM READY HELPER
   ============================================= */
document.addEventListener("DOMContentLoaded", function () {
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
  const loadingScreen = document.getElementById("loading-screen");
  const loaderBar = document.getElementById("loader-bar");

  if (!loadingScreen || !loaderBar) return;

  // Lock body scroll during loading
  document.body.classList.add("loading");

  // Animate the progress bar
  let progress = 0;
  const interval = setInterval(function () {
    progress += Math.random() * 18 + 5;
    if (progress >= 100) {
      progress = 100;
      clearInterval(interval);
      loaderBar.style.width = "100%";

      // Hide loading screen after a short delay
      setTimeout(function () {
        loadingScreen.classList.add("hidden");
        document.body.classList.remove("loading");

        // Trigger initial animations after load
        triggerHeroAnimation();
      }, 500);
    } else {
      loaderBar.style.width = progress + "%";
    }
  }, 120);
}

// Animate hero elements after loading
function triggerHeroAnimation() {
  const heroElements = document.querySelectorAll(".hero-section [data-reveal]");
  heroElements.forEach(function (el, i) {
    setTimeout(function () {
      el.classList.add("revealed");
    }, i * 200);
  });
}

/* =============================================
   3. PARTICLE BACKGROUND
   ============================================= */
function initParticles() {
  const canvas = document.getElementById("particles-canvas");
  if (!canvas) return;

  // Skip heavy animation if user prefers reduced motion
  if (window.matchMedia("(prefers-reduced-motion: reduce)").matches) return;

  const ctx = canvas.getContext("2d");
  let particles = [];
  let animationId;

  // Resize canvas to match viewport
  function resizeCanvas() {
    canvas.width = window.innerWidth;
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
      color: ["#3b82f6", "#8b5cf6", "#06b6d4"][Math.floor(Math.random() * 3)],
    };
  }

  function initParticleArray() {
    // Limit particle count for performance
    const count = Math.min(
      Math.floor((canvas.width * canvas.height) / 12000),
      80,
    );
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
      if (p.x < 0) p.x = canvas.width;
      if (p.x > canvas.width) p.x = 0;
      if (p.y < 0) p.y = canvas.height;
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
          ctx.strokeStyle = "rgba(59,130,246," + 0.08 * (1 - dist / 120) + ")";
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
  window.addEventListener("resize", function () {
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
  const navbar = document.getElementById("navbar");
  if (!navbar) return;

  const navLinks = document.querySelectorAll(".nav-link");
  const sections = document.querySelectorAll("section[id]");

  // Add scrolled class when page is scrolled past 60px
  function handleNavScroll() {
    if (window.scrollY > 60) {
      navbar.classList.add("scrolled");
    } else {
      navbar.classList.remove("scrolled");
    }

    // Update active nav link based on current section in view
    updateActiveNavLink(sections, navLinks);
  }

  window.addEventListener("scroll", handleNavScroll, { passive: true });
  handleNavScroll(); // Run on init
}

// Highlight the nav link matching the section currently in the viewport
function updateActiveNavLink(sections, navLinks) {
  let currentSection = "";
  const scrollY = window.scrollY + 120; // offset for navbar height

  sections.forEach(function (section) {
    const sectionTop = section.offsetTop;
    const sectionHeight = section.offsetHeight;

    if (scrollY >= sectionTop && scrollY < sectionTop + sectionHeight) {
      currentSection = section.getAttribute("id");
    }
  });

  navLinks.forEach(function (link) {
    link.classList.remove("active");
    if (link.getAttribute("data-section") === currentSection) {
      link.classList.add("active");
    }
  });
}

/* =============================================
   5. HAMBURGER MOBILE MENU
   ============================================= */
function initHamburgerMenu() {
  const hamburger = document.getElementById("hamburger");
  const navLinksList = document.getElementById("nav-links");
  const overlay = document.getElementById("mobile-overlay");

  if (!hamburger || !navLinksList) return;

  function openMenu() {
    hamburger.classList.add("active");
    navLinksList.classList.add("open");
    if (overlay) {
      overlay.classList.add("active");
      overlay.removeAttribute("aria-hidden");
    }
    hamburger.setAttribute("aria-expanded", "true");
    document.body.style.overflow = "hidden";
  }

  function closeMenu() {
    hamburger.classList.remove("active");
    navLinksList.classList.remove("open");
    if (overlay) {
      overlay.classList.remove("active");
      overlay.setAttribute("aria-hidden", "true");
    }
    hamburger.setAttribute("aria-expanded", "false");
    document.body.style.overflow = "";
  }

  // Toggle on hamburger click
  hamburger.addEventListener("click", function () {
    const isOpen = navLinksList.classList.contains("open");
    if (isOpen) {
      closeMenu();
    } else {
      openMenu();
    }
  });

  // Close when a nav link is clicked
  navLinksList.querySelectorAll(".nav-link").forEach(function (link) {
    link.addEventListener("click", function () {
      closeMenu();
    });
  });

  // Close on overlay click
  if (overlay) {
    overlay.addEventListener("click", closeMenu);
  }

  // Close on Escape key
  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape" && navLinksList.classList.contains("open")) {
      closeMenu();
      hamburger.focus();
    }
  });
}

/* =============================================
   6. TYPING ANIMATION (Hero Section)
   ============================================= */
function initTypingAnimation() {
  const typingEl = document.getElementById("typing-text");
  if (!typingEl) return;

  // Words to rotate through
  const words = ["Full Stack Developer", "Java Developer", "Problem Solver"];

  let wordIndex = 0;
  let charIndex = 0;
  let isDeleting = false;
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
        isDeleting = false;
        wordIndex = (wordIndex + 1) % words.length;
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
  const revealElements = document.querySelectorAll("[data-reveal]");
  if (!revealElements.length) return;

  // Skip hero elements – they are triggered after loading screen
  const nonHeroReveal = Array.from(revealElements).filter(function (el) {
    return !el.closest(".hero-section");
  });

  const observer = new IntersectionObserver(
    function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          entry.target.classList.add("revealed");
          observer.unobserve(entry.target); // Only animate once
        }
      });
    },
    {
      threshold: 0.12,
      rootMargin: "0px 0px -40px 0px",
    },
  );

  nonHeroReveal.forEach(function (el) {
    observer.observe(el);
  });
}

/* =============================================
   8. ANIMATED SKILL BARS
   ============================================= */
function initSkillBars() {
  const skillBars = document.querySelectorAll(".skill-bar");
  if (!skillBars.length) return;

  const observer = new IntersectionObserver(
    function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          // Small delay for stagger effect
          setTimeout(function () {
            entry.target.classList.add("animated");
          }, 200);
          observer.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.3 },
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
    anchor.addEventListener("click", function (e) {
      const targetId = this.getAttribute("href");
      if (!targetId || targetId === "#") return;

      const targetEl = document.querySelector(targetId);
      if (!targetEl) return;

      e.preventDefault();

      const navbarHeight = document.getElementById("navbar")
        ? document.getElementById("navbar").offsetHeight
        : 80;

      const targetTop =
        targetEl.getBoundingClientRect().top + window.scrollY - navbarHeight;

      window.scrollTo({
        top: targetTop,
        behavior: "smooth",
      });
    });
  });
}

/* =============================================
   10. CONTACT FORM VALIDATION
   ============================================= */
function initContactForm() {
  const form = document.getElementById("contact-form");

  if (!form) return;

  const nameInput = document.getElementById("contact-name");
  const emailInput = document.getElementById("contact-email");
  const subjectInput = document.getElementById("contact-subject");
  const messageInput = document.getElementById("contact-message");

  const nameError = document.getElementById("name-error");
  const emailError = document.getElementById("email-error");
  const subjectError = document.getElementById("subject-error");
  const messageError = document.getElementById("message-error");

  const submitBtn = document.getElementById("form-submit-btn");

  const MIN_MSG_LENGTH = 20;

  /* ================================
     EMAIL VALIDATION
  ================================= */

  function isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim());
  }

  /* ================================
     SHOW ERROR
  ================================= */

  function showError(input, errorElement, message) {
    if (errorElement) {
      errorElement.textContent = message;
    }

    input.classList.add("error");

    input.setAttribute("aria-invalid", "true");
  }

  /* ================================
     CLEAR ERROR
  ================================= */

  function clearError(input, errorElement) {
    if (errorElement) {
      errorElement.textContent = "";
    }

    input.classList.remove("error");

    input.setAttribute("aria-invalid", "false");
  }

  /* ================================
     VALIDATE FIELD
  ================================= */

  function validateField(input, errorElement) {
    if (!input) return true;

    const value = input.value.trim();

    /* NAME */

    if (input === nameInput) {
      if (!value) {
        showError(input, errorElement, "Full name is required.");

        return false;
      }
    }

    /* EMAIL */

    if (input === emailInput) {
      if (!value) {
        showError(input, errorElement, "Email address is required.");

        return false;
      }

      if (!isValidEmail(value)) {
        showError(input, errorElement, "Please enter a valid email address.");

        return false;
      }
    }

    /* SUBJECT */

    if (input === subjectInput) {
      if (!value) {
        showError(input, errorElement, "Subject is required.");

        return false;
      }
    }

    /* MESSAGE */

    if (input === messageInput) {
      if (!value) {
        showError(input, errorElement, "Message is required.");

        return false;
      }

      if (value.length < MIN_MSG_LENGTH) {
        showError(
          input,
          errorElement,
          "Message must be at least 20 characters.",
        );

        return false;
      }
    }

    clearError(input, errorElement);

    return true;
  }

  /* ================================
     LIVE VALIDATION
  ================================= */

  [
    [nameInput, nameError],
    [emailInput, emailError],
    [subjectInput, subjectError],
    [messageInput, messageError],
  ].forEach(function ([input, error]) {
    if (!input) return;

    input.addEventListener("blur", function () {
      validateField(input, error);
    });

    input.addEventListener("input", function () {
      if (input.classList.contains("error")) {
        validateField(input, error);
      }
    });
  });

  /* ================================
     FORM SUBMIT
  ================================= */
  let isSubmitting = false;

  form.addEventListener("submit", function (event) {
    /*
     * First stop the normal submit
     * so validation can happen.
     */
    event.preventDefault();

    // Prevent duplicate submissions while in-flight
    if (isSubmitting) return;

    const nameValid = validateField(nameInput, nameError);
    const emailValid = validateField(emailInput, emailError);
    const subjectValid = validateField(subjectInput, subjectError);
    const messageValid = validateField(messageInput, messageError);

    /* ================================
         VALIDATION FAILED
      ================================= */
    if (!nameValid || !emailValid || !subjectValid || !messageValid) {
      if (!nameValid) {
        nameInput.focus();
      } else if (!emailValid) {
        emailInput.focus();
      } else if (!subjectValid) {
        subjectInput.focus();
      } else if (!messageValid) {
        messageInput.focus();
      }
      return;
    }

    /* ================================
       DIRECT BACKEND SUBMISSION
    ================================= */
    isSubmitting = true;
    const submitBtn = document.getElementById("form-submit-btn");
    const buttonText = submitBtn ? submitBtn.querySelector("span") : null;
    const originalText = buttonText ? buttonText.textContent : "Send Message";

    if (submitBtn) {
      submitBtn.disabled = true;
      submitBtn.style.opacity = "0.75";
      submitBtn.style.cursor = "not-allowed";
      if (buttonText) buttonText.textContent = "Sending Email...";
    }

    const errDiv = document.getElementById("form-error");
    const successDiv = document.getElementById("form-success");
    if (errDiv) errDiv.hidden = true;
    if (successDiv) successDiv.hidden = true;

    const payload = {
      name: nameInput.value.trim(),
      email: emailInput.value.trim(),
      subject: subjectInput.value.trim(),
      message: messageInput.value.trim()
    };

    const apiBase = (window.location.protocol === "file:") ? "http://localhost:8080/api" : "/api";

    fetch(apiBase + "/contact", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
      credentials: "include"
    })
      .then(function (resp) {
        return resp.json().then(function (data) { return { ok: resp.ok, status: resp.status, data: data }; });
      })
      .then(function (result) {
        if (result.ok && result.data && result.data.success) {
          form.reset();
          if (errDiv) errDiv.hidden = true;
          if (successDiv) {
            const p = successDiv.querySelector("p");
            if (p && result.data.message) {
              p.textContent = result.data.message;
            }
            successDiv.hidden = false;
            successDiv.scrollIntoView({ behavior: "smooth", block: "nearest" });
            setTimeout(function () { successDiv.hidden = true; }, 8000);
          }
        } else {
          if (successDiv) successDiv.hidden = true;
          let msg = (result.data && result.data.message) || "Failed to send email. Please try again later.";
          if (result.data && result.data.errors) {
            const fieldErrorStrings = [];
            for (const key in result.data.errors) {
              fieldErrorStrings.push(result.data.errors[key]);
            }
            if (fieldErrorStrings.length > 0) {
              msg = msg + " (" + fieldErrorStrings.join(", ") + ")";
            }
          }
          if (errDiv) {
            errDiv.textContent = msg;
            errDiv.hidden = false;
            errDiv.scrollIntoView({ behavior: "smooth", block: "nearest" });
            setTimeout(function () { errDiv.hidden = true; }, 8000);
          }
        }
      })
      .catch(function (err) {
        console.error("Contact form submission error:", err);
        if (successDiv) successDiv.hidden = true;
        if (errDiv) {
          errDiv.textContent = "Could not connect to the backend server. Please make sure the Spring Boot service is running on port 8080.";
          errDiv.hidden = false;
          setTimeout(function () { errDiv.hidden = true; }, 8000);
        }
      })
      .finally(function () {
        isSubmitting = false;
        if (submitBtn) {
          submitBtn.disabled = false;
          submitBtn.style.opacity = "";
          submitBtn.style.cursor = "";
          if (buttonText) buttonText.textContent = originalText;
        }
      });
  });
}
/* =============================================
   11. BACK TO TOP BUTTON
   ============================================= */
function initBackToTop() {
  const backToTopBtn = document.getElementById("back-to-top");
  if (!backToTopBtn) return;

  // Show/hide based on scroll position
  window.addEventListener(
    "scroll",
    function () {
      if (window.scrollY > 400) {
        backToTopBtn.hidden = false;
        // Use rAF to ensure display is set before adding class
        requestAnimationFrame(function () {
          backToTopBtn.classList.add("visible");
        });
      } else {
        backToTopBtn.classList.remove("visible");
        // Remove hidden after transition ends
        backToTopBtn.addEventListener("transitionend", function handler() {
          if (!backToTopBtn.classList.contains("visible")) {
            backToTopBtn.hidden = true;
          }
          backToTopBtn.removeEventListener("transitionend", handler);
        });
      }
    },
    { passive: true },
  );

  // Scroll to top on click
  backToTopBtn.addEventListener("click", function () {
    window.scrollTo({ top: 0, behavior: "smooth" });
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
  const skillCards = document.querySelectorAll(".skill-card");

  skillCards.forEach(function (card) {
    card.addEventListener("mousemove", function (e) {
      const rect = card.getBoundingClientRect();
      const x = ((e.clientX - rect.left) / rect.width - 0.5) * 10;
      const y = ((e.clientY - rect.top) / rect.height - 0.5) * 10;
      card.style.transform =
        "translateY(-6px) rotateX(" + -y + "deg) rotateY(" + x + "deg)";
    });

    card.addEventListener("mouseleave", function () {
      card.style.transform = "";
    });
  });
})();

/* =============================================
   14. PROJECT CARD TILT EFFECT
   ============================================= */
(function () {
  const projectCards = document.querySelectorAll(".project-card");

  projectCards.forEach(function (card) {
    card.addEventListener("mousemove", function (e) {
      const rect = card.getBoundingClientRect();
      const x = ((e.clientX - rect.left) / rect.width - 0.5) * 6;
      const y = ((e.clientY - rect.top) / rect.height - 0.5) * 6;
      card.style.transform =
        "translateY(-8px) rotateX(" + -y + "deg) rotateY(" + x + "deg)";
    });

    card.addEventListener("mouseleave", function () {
      card.style.transform = "";
    });
  });
})();

/* =============================================
   15. PERFORMANCE: Only run animations when
       the hero section is in the viewport
   ============================================= */
(function () {
  const heroSection = document.getElementById("home");
  const canvas = document.getElementById("particles-canvas");
  if (!heroSection || !canvas) return;

  const observer = new IntersectionObserver(
    function (entries) {
      entries.forEach(function (entry) {
        // Stop canvas animation when hero is out of view to save CPU
        if (entry.isIntersecting) {
          canvas.style.opacity = "1";
        } else {
          canvas.style.opacity = "0";
        }
      });
    },
    { threshold: 0 },
  );

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
// ==========================================
// BACKEND INTEGRATION
// ==========================================

/**
 * Visitor Counter — Backend Integration
 *
 * Records a visit on portfolio page load.
 * Gracefully handles backend errors — never breaks the portfolio.
 * Admin dashboard visits are excluded by the backend (X-Admin-Dashboard header).
 */
(function () {
  document.addEventListener("DOMContentLoaded", function () {
    // Use a session-flag to avoid double-counting on React-style remounts
    if (sessionStorage.getItem("portfolio_visit_recorded")) return;

    var apiBase = (window.location.protocol === "file:")
      ? "http://localhost:8080/api"
      : "/api";

    fetch(apiBase + "/visitors", {
      method: "POST",
      credentials: "include"
    })
    .then(function (resp) {
      if (!resp.ok) throw new Error("Visitor API error");
      return resp.json();
    })
    .then(function (data) {
      sessionStorage.setItem("portfolio_visit_recorded", "1");
      // Optionally display visitor count in a badge (if element exists in HTML)
      var counterEl = document.getElementById("visitor-count");
      if (counterEl && data.totalVisits) {
        counterEl.textContent = data.totalVisits.toLocaleString();
      }
    })
    .catch(function () {
      // Silently fail — never break the portfolio UI
    });
  });
})();

