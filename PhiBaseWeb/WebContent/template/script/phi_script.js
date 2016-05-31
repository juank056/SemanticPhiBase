//Pagina actual
var currentPage = window.location.href;
var parts = currentPage.split("/PhiBaseWeb/");
currentPage = parts[1];
// URL de ayuda
var helpURL = "/PhiBaseWeb/help/index_help.jsf?service=";
// Base de Help
var helpBase = "/PhiBaseWeb/help/docs/";
// Nombre de la pagina de ayuda
var openHelpPage = "phi_help_page";

/**
 * Funcion para abrir la ayuda del sistema
 */
function openHelp() {
	// Abre ayuda en el servicio actual
	win = window.open(helpURL + currentPage, openHelpPage);
	win.focus();
}

/**
 * Para abrir la ayuda con la tecla F2
 */
function openHelpKey(e) {
	// Tecla
	var keycode = 0;
	// Obtiene codigo
	if (window.event)
		keycode = window.event.keyCode;
	else if (e)
		keycode = e.which;
	// Revisa que seaF2
	if (keycode == 113) {
		openHelp();
	}
}

/**
 * Asigna pantalla de ayuda de acuerdo al servicio desplegado
 */
function setHelpService() {
	// Obtiene variables para cargar servicio
	var url = location.href;
	url = url.replace(/.*\?(.*?)/, "$1");
	var variables = url.split("&");
	for (var i = 0; i != variables.length; i++) {
		var separ = variables[i].split("=");
		eval('var ' + separ[0] + '="' + separ[1] + '"');
	}
	// Si finalmente hay un servicio, lo carga en pantalla
	if (service) {
		// Asigna servicio
		document.getElementById("serviceContainer").setAttribute("src",
				helpBase + service);
	}
}

/**
 * Asigna pantalla de ayuda para un servicio
 * 
 * @param service
 *            Servicio para abrir la ayuda
 */
function openHelpService(service) {
	// Asigna servicio
	document.getElementById("serviceContainer").setAttribute("src",
			helpBase + service);
}

/**
 * Se encarga de llamar al servicio de visualización para desplegar el grafo
 * 
 * @param serverURL
 *            URL del servicio de visualización
 * @param interactionType
 *            Tipo RDF de la interaccion
 * @param interactionIRI
 *            IRI de la interacción relacionada
 * @param elementType
 *            RDF type del elemento a desplegar
 * @param elementIRI
 *            IRI del elemento a desplegar
 * @param integrated
 *            Indicador de visualizacion integrada en el software
 */
function inner_showPhiBaseGraph(serverURL, interactionType, interactionIRI,
		elementType, elementIRI, integrated) {
	// Obtiene Iframe de visualizacion
	var visFrame = document.getElementById("visualizationGraph");
	// Div de visualizacion
	var visDiv = document.getElementById("visualizationDiv");
	// Esconde elementos
	visFrame.style.display = "none";
	visDiv.style.display = "none";
	// Url de llamado
	var callUrl = serverURL + "?interaction=" + interactionIRI + "&class_type="
			+ elementIRI;
	// Revisa si la visualizacion esta integrada
	if (integrated) {
		// Asigna src
		visFrame.setAttribute("src", callUrl);
		// Visualiza
		visFrame.style.display = "block";
	} else {
		// Contenido del div
		visDiv.innerHTML = "<a href='" + callUrl + "' target='_blank'>"
				+ "Abrir Grafo" + "</a>";
		// Visualiza
		visDiv.style.display = "block";
		// Abre pagina nueva
		window.open(callUrl);
	}
}