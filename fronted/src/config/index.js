// Use the address opened by the browser. In development, vue.config.js proxies
// backend requests so LAN clients never send requests to their own localhost.
const baseUrl = window.location.origin;

export default baseUrl;
