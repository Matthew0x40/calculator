package com.matthew0x40.calculator.util;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Util {
	public static Color fromHex(String hex) {
        if (!hex.startsWith("#")) {
            hex = "#" + hex;
        }
        if (hex.length() > 7) {
            throw new IllegalArgumentException();
        }
        return new Color(
                Integer.valueOf( hex.substring( 1, 3 ), 16 ),
                Integer.valueOf( hex.substring( 3, 5 ), 16 ),
                Integer.valueOf( hex.substring( 5, 7 ), 16 ) );
    }
	
	public static void applyQuality(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	}

	public static String streamToString(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = br.readLine()) != null) {
		   sb.append(line);
		}
		br.close();
		return sb.toString();
	}
	
	public static boolean openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	            return true;
	        } catch (Exception e) {
	            return false;
	        }
	    } else {
	    	return false;
	    }
	}

	public static boolean openWebpage(URL url) {
	    try {
	        return openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        return false;
	    }
	}
	
	public static String formatDouble(String dstr) {
		if (dstr.endsWith(".0")) {
			return dstr.substring(0, dstr.length()-2);
		} else {
			return dstr;
		}
	}
	
	public static String formatDouble(double d) {
		String dstr = Double.toString(d);
		if (dstr.endsWith(".0")) {
			return dstr.substring(0, dstr.length()-2);
		} else {
			return dstr;
		}
	}
}
