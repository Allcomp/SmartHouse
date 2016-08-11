/*
 * Utility.java
 *
 * Created on 22. leden 2010, 19:45
 *
 * Copyright 2012 Petr Mikše. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cz.allcomp.shs.allcomplib.common;

import java.math.BigDecimal;
import java.text.ChoiceFormat;
import java.util.Calendar;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.prefs.Preferences;

/**
 *
 * @author Petr Mikše
 */
public class Utility {

    /**
     * Zero in BigDecimal format with scale = 1
     */
    public static final BigDecimal ZERO1 = BigDecimal.valueOf(0, 1);
    /**
     * Formatter for {@link #format(String pattern, int val)}
     */
    private static final ChoiceFormat form = new ChoiceFormat("0# ");

    /**
     * try to find localised version of the pattern if nothing found write error
     * to the system error stream and use an original pattern
     *
     * @param pattern key to the resource bundles
     * @return localised pattern
     */
    public static String getLocalizedLabel(String pattern) {
	try {
	    return PropertyResourceBundle.getBundle("labels").getString(pattern);
	} catch (MissingResourceException ex) {
	    //System.err.println(ex);
	    ex.printStackTrace();
	    return pattern;
	}
    }

    /**
     * try to find localised version of the pattern if nothing found write error
     * to the system error stream and use an original pattern then format
     * arguments with the pattern
     *
     * @param pattern Formatting pattern and key to the resource bundles too
     * @param args data to format
     * @return localised formatted label
     */
    public static String getLocalizedLabel(String pattern, Object... args) {
	return String.format(getLocalizedLabel(pattern), args);
    }

    /**
     * get localised String using ChoiceFormat
     *
     * @param pattern Formatting pattern and key to the resource bundles too
     * @param val value for ChoiceFormat
     * @return localised String
     */
    public static String format(String pattern, int val) {
	if ("".equals(pattern)) {
	    return "";
	}
	String loc = getLocalizedLabel(pattern);
	if (loc.indexOf('#') < 0) {
	    return loc;
	}
	synchronized (form) {
	    form.applyPattern(loc);
	    return form.format(val);
	}
    }

    /**
     * get localised and formatted String using ChoiceFormat
     *
     * @param pattern Formatting pattern and key to the resource bundles too
     * @param val value for ChoiceFormat
     * @return localised formatted String
     */
    public static String getFormattedLabel(String pattern, int val) {
	return String.format(format(pattern, val), val);
    }

    /**
     * Returns the Enum value represented by the string associated with the
     * specified key in {@link Preferences} node. The string is converted to a
     * Enum as by {@link Enum#valueOf(Class, String)}. Returns the specified default if
     * there is no value associated with the key, the backing store is
     * inaccessible, or if <tt>Enum#valueOf(Class, String)</tt>
     * would throw a {@link IllegalArgumentException} if the associated Enum
     * instance were searched for.
     *
     * @param prefs Preferences node
     * @param key key whose associated value is to be returned as an Enum
     * @param def the value to be returned in the event that this preference
     * node has no value associated with <tt>key</tt>
     * or the associated value cannot be interpreted as an Enum of the same
     * class, or the backing store is inaccessible.
     */
    public static <T extends Enum<T>> T getEnum(Preferences prefs, String key, T def) {
	T result;
	try {
	    result = Enum.valueOf(def.getDeclaringClass(), prefs.get(key, def.name()));
	} catch (IllegalArgumentException ex) {
	    //System.err.println(ex);
	    ex.printStackTrace();
	    result = def;
	}
	return result;
    }

    /**
     * Returns the BigDecimal value represented by the string associated with
     * the specified key in {@link Preferences} node. The string is converted to
     * a BigDecimal as by {@link BigDecimal#BigDecimal(String)}. Returns the
     * specified default if there is no value associated with the key, the
     * backing store is inaccessible, or if
     * <tt>BigDecimal#BigDecimal(String)</tt>
     * would throw a {@link NumberFormatException} if the associated value were
     * passed.
     *
     * @param prefs Preferences node
     * @param key key whose associated value is to be returned as a BigDecimal
     * @param def the value to be returned in the event that this preference
     * node has no value associated with <tt>key</tt>
     * or the associated value cannot be interpreted as a BigDecimal, or the
     * backing store is inaccessible.
     */
    public static BigDecimal getBigDecimal(Preferences prefs, String key, BigDecimal def) {
	String str = prefs.get(key, null);
	if (str == null) {
	    return def;
	}
	BigDecimal result;
	try {
	    result = new BigDecimal(str);
	    if (result.equals(def)) {
		result = def;
	    }
	} catch (NumberFormatException ex) {
	    System.out.println(ex);
	    //ex.printStackTrace();
	    result = def;
	}
	return result;
    }

    /**
     * Create file name according to the given pattern using calendar fields
     *
     * @param pattern giving filename format
     * @param calendar giving datum for use in filename
     * @return filename String
     */
    public static String getTimeName(String pattern, Calendar calendar) {
	return String.format(pattern,
		calendar.get(Calendar.YEAR),
		calendar.get(Calendar.MONTH) + 1,
		calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * convert software version integer to String
     *
     * @param i integer to convert
     * @return String for a given version
     */
    public static String swver2string(int i) {
	//return String.format("%d.%02d", i / 100, i % 100);
	StringBuilder sb = new StringBuilder(8);
	sb.append(i >>> 24);
	if ((i &= 0xFFFFFF) != 0) {
	    sb.append('.');
	    sb.append((char) (i >>> 16));
	    if ((i &= 0xFFFF) != 0) {
		sb.append((char) (i >>> 8));
		if ((i &= 0xFF) != 0) {
		    sb.append((char) i);
		}
	    }
	}
	return sb.toString();
    }

    //private static final int T_MIN = -250;
    //private static final int T_MAX = 770;
    /**
     * Maximal analog input value
     */
    public static final int IN_MAX = 0xFFC0;
    /**
     * Zero Celsius grade in cents of Kelvin
     */
    public static final int CELSIUS0 = 27315;
    private static final float A0 = 2981 - CELSIUS0 / 10;
    private static final float A1 = -220.3f;
    private static final float A2 = 11.2f;
    private static final float A3 = -0.6f;
    private static final float A4 = 0.053f;

    /**
     * Calculate temperature from the given A/DC result Used data for 15kOhm
     * thermistor If error occurred return Integer.MIN_VALUE
     *
     * @param ad A/DC result in a range 0, IN_MAX
     * @return temperature in °C/10
     */
    public static int getThermistorTemperature(int ad) {
	//return (T_MIN-T_MAX)*ad/IN_MAX+T_MAX;
	if (ad <= 0 || ad >= IN_MAX) {
	    return Integer.MIN_VALUE;
	}
	float r2r1 = (float) ad / (float) (IN_MAX - ad);
	float lnr = (float) Math.log(r2r1);
	return Math.round(A0 + lnr * (A1 + lnr * (A2 + lnr * (A3 + lnr * A4))));
    }
    /**
     * All possible chars for representing a number as a String
     */
    final static char[] digits = {
	'0', '1', '2', '3', '4', '5', '6', '7',
	'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String intArrayToHex(int[] a) {
	final int shift = 4;
	final int mask = 15;
	int aLen = a.length;
	int charPos = 8 * aLen;
	char[] buf = new char[charPos];

	// Translate all ints to Hex
	for (int i = aLen - 1; i >= 0; i--) {
	    int x = a[i];
	    for (int j = 8; j > 0; j--) {
		buf[--charPos] = digits[x & mask];
		x >>>= shift;
	    }
	}
	return new String(buf);
    }

    public static int[] hexToIntArray(String s) {
	final int shift = 4;
	final int radix = 16;
	int sLen = s.length();
	int numGroups = sLen / 8;
	if (8 * numGroups != sLen) {
	    throw new IllegalArgumentException(
		    "String length must be a multiple of eight.");
	}
	int[] result = new int[numGroups];

	// Translate all full groups from hex to intager array elements
	@SuppressWarnings("unused")
	int inCursor = 0, outCursor = 0;
	for (int i = 0; i < numGroups; i++) {
	    int item = 0;
	    int digit;
	    for (int j = 8; j > 0; j--) {
		digit = Character.digit(s.charAt(inCursor++), radix);
		if (digit < 0) {
		    throw new NumberFormatException("For input string: \"" + s + "\"");
		} else {
		    item = (item << shift) | digit;
		}
	    }
	    result[i] = item;
	}
	return result;
    }

    public static int[] hexToIntArray(String value, int[] def) {
	int[] result = def;
	try {
	    if (value != null) {
		result = hexToIntArray(value);
	    }
	} catch (RuntimeException e) {
	    // Ignoring exception causes specified default to be returned
	}
	return result;
    }

    /**
     * get localised String using ChoiceFormat
     *
     * @param pattern Formatting pattern and key to the resource bundles too
     * @param args value for ChoiceFormat
     * @return localised String
     */
    public static String patternFormat(String pattern, Object... args) {
	//if ("".equals(pattern)) {
	//    return "";
	//}
	try {
	    pattern = PropertyResourceBundle.getBundle("messages").getString(pattern);
	} catch (MissingResourceException e) {
	    System.err.print(e);
	    String c = String.format(pattern, args);
	    System.err.println(": " + c);
	    //ee.printStackTrace();
	}
	if (args.length >= 1 && (args[0] instanceof Number) && pattern.indexOf('#') > 0) {
	    synchronized (form) {
		form.applyPattern(pattern);
		pattern = form.format(((Number) args[0]).longValue());
	    }
	}
	return String.format(pattern, args);
    }

    public static String patternFormat(String fcename, String ptrn, Object... args) {
	try {
	    return String.format(PropertyResourceBundle.getBundle("messages").getString(fcename + "_" + ptrn), args);
	} catch (MissingResourceException e) {
	    return patternFormat(ptrn, args);
	}
    }

    /**
     * try to find localised version of the pattern if nothing is found write
     * error to the system error stream and use an original pattern then format
     * arguments with the pattern
     *
     * @param ptrn Formatting pattern and key to the resource bundles too
     * @param args data to format
     * @return localised formatted message
     */
    public static String localFormat(String ptrn, Object... args) {
	try {
	    return String.format(PropertyResourceBundle.getBundle("messages").getString(ptrn), args);
	} catch (MissingResourceException ee) {
	    System.err.print(ee);
	    String c = String.format(ptrn, args);
	    System.err.println(": " + c);
	    //ee.printStackTrace();
	    return c;
	}
    }

    /**
     * try to find localised version of the prefixed pattern if nothing is found
     * unprefixed version {@link #localFormat(String, Object[])} is used
     *
     * @param fcename prefix to the key to the resource bundles
     * @param ptrn Formatting pattern and key to the resource bundles too
     * @param args data to format
     * @return localised formatted message
     */
    public static String localFormat(String fcename, String ptrn, Object... args) {
	try {
	    return String.format(PropertyResourceBundle.getBundle("messages").getString(fcename + "_" + ptrn), args);
	} catch (MissingResourceException e) {
	    return localFormat(ptrn, args);
	}
    }

    /**
     * use Enum name() instead of toString() for system message format
     *
     * @param args data to format
     * @return same args but String name() for Enum
     */
    static Object[] getSystemArgs(Object... args) {
	Object[] result = new Object[args.length];
	for (int i = 0; i < args.length; i++) {
	    result[i] = args[i] instanceof Enum ? ((Enum<?>) args[i]).name() : args[i];
	}
	return result;
    }

    /**
     * disable instantiation
     */
    private Utility() {
    }
}
