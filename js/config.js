/**
 * J. Sethuraman Portfolio — Centralized Configuration
 *
 * Handles API Base URL routing across:
 *  1. Local development (file:// or localhost)
 *  2. Bundled / reverse-proxied deployments (/api)
 *  3. GitHub Pages deployment connected to Spring Boot backend on Render
 */
(function (root) {
  "use strict";

  // Default Spring Boot backend URL deployed on Render
  // Change this if your Render service has a specific custom domain or service name
  var DEFAULT_RENDER_BACKEND = "https://sethu-portfolio-backend.onrender.com";

  var STORAGE_KEY = "sethu_portfolio_backend_url";

  /**
   * Returns the stored custom backend URL from localStorage if set.
   */
  function getCustomBackendUrl() {
    try {
      if (typeof window !== "undefined" && window.localStorage) {
        var val = window.localStorage.getItem(STORAGE_KEY);
        if (val && val.trim().length > 0) {
          return val.trim();
        }
      }
    } catch (e) {
      // localStorage may be restricted in private/sandboxed mode
    }
    return null;
  }

  /**
   * Sets or clears a custom backend URL in localStorage.
   */
  function setCustomBackendUrl(url) {
    try {
      if (typeof window !== "undefined" && window.localStorage) {
        if (!url || url.trim().length === 0) {
          window.localStorage.removeItem(STORAGE_KEY);
        } else {
          var clean = url.trim().replace(/\/+$/, "");
          window.localStorage.setItem(STORAGE_KEY, clean);
        }
      }
    } catch (e) {}
  }

  /**
   * Computes the base API endpoint for all fetch calls.
   * Format returned always ends with "/api" (without trailing slash).
   */
  function getApiBaseUrl() {
    var custom = getCustomBackendUrl();
    if (custom) {
      // Strip trailing slashes and /api if user appended it
      var cleaned = custom.replace(/\/+$/, "").replace(/\/api$/, "");
      return cleaned + "/api";
    }

    if (typeof window === "undefined" || !window.location) {
      return "/api";
    }

    var protocol = window.location.protocol;
    var hostname = window.location.hostname;
    var port = window.location.port;

    // 1. Direct local file inspection
    if (protocol === "file:") {
      return "http://localhost:8080/api";
    }

    // 2. Localhost development
    if (hostname === "localhost" || hostname === "127.0.0.1") {
      // If frontend is running on a static dev server (5500, 3000, 5173, etc.)
      if (port && port !== "8080") {
        return "http://localhost:8080/api";
      }
      return "/api";
    }

    // 3. GitHub Pages deployment
    // e.g. https://sethu4637-cell.github.io/sethu-portfolio/
    if (hostname.endsWith("github.io")) {
      return DEFAULT_RENDER_BACKEND.replace(/\/+$/, "").replace(/\/api$/, "") + "/api";
    }

    // 4. Default: relative /api for Docker, Nginx, or single-domain hosting
    return "/api";
  }

  /**
   * Returns the display root of the backend server (without /api)
   */
  function getBackendRootUrl() {
    var apiBase = getApiBaseUrl();
    return apiBase.replace(/\/api$/, "");
  }

  root.PORTFOLIO_CONFIG = {
    DEFAULT_RENDER_BACKEND: DEFAULT_RENDER_BACKEND,
    getApiBaseUrl: getApiBaseUrl,
    getBackendRootUrl: getBackendRootUrl,
    getCustomBackendUrl: getCustomBackendUrl,
    setCustomBackendUrl: setCustomBackendUrl,
    STORAGE_KEY: STORAGE_KEY
  };
})(typeof window !== "undefined" ? window : this);
