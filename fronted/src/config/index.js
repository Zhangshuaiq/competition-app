let baseUrl;

if (process.env.NODE_ENV === 'development') {
    baseUrl = process.env.VUE_APP_BASE_URL;
} else {
    const host = window.location.hostname;
    const port = window.location.port ? `:${window.location.port}` : ':8080';
    baseUrl = `http://${host}${port}`;
}

export default baseUrl;
