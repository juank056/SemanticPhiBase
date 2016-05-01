/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */
package edu.upm.spbw.utils;

import javax.faces.context.FacesContext;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>
 * Utility class which loads the applications message resource bundle. This
 * class can be used to internationalize string values that are located in
 * Beans.
 * </p>
 * <p>
 * This bean can be access statically or setup as a Application scoped bean in
 * the faces_config.xml
 * </p>
 * <p>
 * This Bean should be scoped in JSF as an Application Bean. And as a result can
 * be passed into other beans via chaining or by loading via the FacesContext.
 * Loading an Application scoped bean "messageLoader" via the FacesContext would
 * look like the following:
 * </p>
 * <p>
 * JSF 1.1:
 * </p>
 * 
 * <pre>
 * Application application = FacesContext.getCurrentInstance().getApplication();
 * ApplicationMessages messageLoader = ((ApplicationMessages) application
 * 		.createValueBinding(&quot;#{messageLoader}&quot;).getValue(
 * 				FacesContext.getCurrentInstance()));
 * </pre>
 * <p>
 * JSF 1.2:
 * </p>
 * 
 * <pre>
 * FacesContext fc = FacesContext.getCurrentInstance();
 * ELContext elc = fc.getELContext();
 * ExpressionFactory ef = fc.getApplication().getExpressionFactory();
 * ValueExpression ve = ef.createValueExpression(elc, expr, Object.class);
 * </pre>
 * 
 * @since 1.7
 */
public class ApplicationMessages {

	public static final String MESSAGE_PATH = "edu.upm.spbw.resources.messages";

	// message bundle for component.
	private static ResourceBundle messages;

	/**
	 * Initialize internationalization.
	 */
	private static void init() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale();
		messages = ResourceBundle.getBundle(MESSAGE_PATH, locale);
	}

	/**
	 * Gets a string for the given key from this resource bundle or one of its
	 * parents.
	 * 
	 * @param key
	 *            the key for the desired string
	 * @return the string for the given key. If the key string value is not
	 *         found the key itself is returned.
	 */
	public static String getMessage(String key, String... params) {
		try {
			if (messages == null) {
				init();
			}
			// Obtiene mensaje
			String message = messages.getString(key);
			// Asigna parametros
			for (int i = 1; i <= params.length; i++) {
				message = message.replace(Constants.AMPERSAND + i,
						params[i - 1]);
			}
			// Retorna mensaje
			return message;
		}
		// on any failure we just return the key, which should aid in debugging.
		catch (Exception e) {
			return key;
		}
	}
}
