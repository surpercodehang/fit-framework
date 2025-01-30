window.onload = function() {
  //<editor-fold desc="Changeable Configuration Block">

  // the following lines will be replaced by docker/configurator, when it runs in a docker-container
  const pathname = window.location.pathname;
  const contextPath = pathname.substring(0, pathname.lastIndexOf('/'));
  const dataUrl = `${contextPath}/v3/openapi`;
  window.ui = SwaggerUIBundle({
    url: dataUrl,
    dom_id: '#swagger-ui',
    deepLinking: true,
    presets: [
      SwaggerUIBundle.presets.apis,
      SwaggerUIStandalonePreset
    ],
    plugins: [
      SwaggerUIBundle.plugins.DownloadUrl
    ],
    layout: "StandaloneLayout"
  });

  //</editor-fold>
};