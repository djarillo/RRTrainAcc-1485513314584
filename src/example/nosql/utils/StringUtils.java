package example.nosql.utils;

public class StringUtils {

	public static String reformatString(String cadena) {
		String cadenaFormat = null;
		
		if (cadena.startsWith("-")) {
			if (cadena.endsWith("\u00A0")) {
				cadenaFormat = cadena.substring(1, cadena.length() - 1);
			} else {
				cadenaFormat = cadena.substring(1);
			}
		} else if (cadena.endsWith("\u00A0")) {
			cadenaFormat = cadena.substring(0, cadena.length() - 1);
		} else {
			cadenaFormat = cadena;
		}
		
		cadenaFormat = cadenaFormat.trim();
		
		return cadenaFormat;
	}
}
