package org.osehra.cpe.vista.util;

import org.osehra.cpe.vista.rpc.broker.protocol.AbstractRpcProtocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Functions that emulate MUMPS functions.
 */
public class VistaStringUtils {

    public static final String U = "^";

    public static String piece(String s) {
        return piece(s, U);
    }

    public static String piece(String s, int piece1) {
        return piece(s, U, piece1, 0);
    }

    public static String piece(String s, int piece1, int piece2) {
        return piece(s, U, piece1, piece2);
    }

    public static String piece(String s, String delimiter) {
        return piece(s, delimiter, 1, 0);
    }

    public static String piece(String s, String delimiter, int piece1) {
        return piece(s, delimiter, piece1, 0);
    }

    /**
     * Returns a field within a string using the specified delimiter.
     *
     * @param s
     * @param delimiter
     * @param piece1
     * @param piece2
     * @return
     */
    public static String piece(String s, String delimiter, int piece1, int piece2) {
        int delimiterIndex = 0;
        int pieceNumber = 1;
        String result = "";
        String scratch = s;
        if (piece2 == 0)
            piece2 = piece1;
        do {
            delimiterIndex = scratch.indexOf(delimiter);
            if (delimiterIndex > 0 || ((pieceNumber > piece1 - 1) && pieceNumber < piece2 + 1)) {
                if ((pieceNumber > piece1 - 1) && (pieceNumber < piece2 + 1)) {
                    if ((pieceNumber > piece1) && (!scratch.equals("")))
                        result = result + delimiter;
                    if (delimiterIndex > 0) {
                        result = result + scratch.substring(0, delimiterIndex);
                        scratch = scratch.substring(delimiterIndex + delimiter.length(), scratch.length());
                    } else {
                        result = result + scratch;
                        scratch = "";
                    }
                } else {
                    scratch = scratch.substring(delimiterIndex + delimiter.length(), scratch.length());
                }
            } else if (!scratch.equals("")) {
                scratch = "";
            }
            pieceNumber++;
        } while (pieceNumber <= piece2);

        return result;
    }

    /**
     * Performs a character-for-character replacement within a string.
     *
     * @param s
     * @param identifier
     * @param associator
     * @return
     */
    public static String translate(String s, String identifier, String associator) {
        String newString = "";

        for (int index = 0; index < s.length(); index++) {
            String substring = s.substring(index, index + 1);
            int position = identifier.indexOf(substring);

            if (position != -1)
                newString = newString + associator.substring(position, position + 1);
            else
                newString = newString + s.substring(index, index + 1);
        }
        return newString;
    }

    /**
     * Calculates the CRC-16 value of the given string using the same algorithm as VistA Kernel XLF function library.
     *
     * @param s The string for which to calculate the CRC-16.
     * @return the CRC-16 value
     *
     * @see "VistA CRC16^XLFCRC"
     */
    public static long crc16(String s) {
        CRC16 crc = new CRC16();
        crc.update(s.getBytes(AbstractRpcProtocol.VISTA_CHARSET));
        return crc.getValue();
    }

    /**
     * Calculates the Hex representation of the CRC-16 value of the given string using the same algorithm as VistA Kernel XLF function library.
     *
     * @param s The string for which to calculate the CRC-16.
     * @return the CRC-16 value as Hex
     *
     * @see #crc16(String)
     * @see "VistA CRC16^XLFCRC"
     */
    public static String crc16Hex(String s) {
        return Integer.toHexString((int) crc16(s)).toUpperCase();
    }

    /**
     * Utility for splitting strings into lists of strings to pass as arguments to VistA Remote Procedure Calls (RPCs) to
     * work around LITERAL parameter length limitations in the RPC Broker protocol.  This will work for a particular RPC
     * parameter if and only if the parameter is defined in VistA as being of <code>WORD PROCESSING</code> type.
     *
     * @param s The string to split (if necessary)
     * @see #splitLargeStringIfNecessary(String, int)
     * @see "VistA FileMan REMOTE PROCEDURE,PARAMETER TYPE(8994.02,.02)"
     */
    public static Object splitLargeStringIfNecessary(String s) {
        return splitLargeStringIfNecessary(s, 245);
    }

    /**
     * Splits strings greater than the specified length into lists of strings of the specified length.  Strings less than
     * than the desired length are returned unmodified.
     *
     * @param s The string to split (if necessary).
     * @param length The desired length of each string in the
     * @return <code>s</code> if
     */
    public static Object splitLargeStringIfNecessary(String s, int length) {
        if (s == null) {
            return "";
        } else if (s.length() <= length) {
            return s;
        } else {
            List<String> ret = new ArrayList<String>();
            while (s.length() > length) {
                ret.add(s.substring(0, length));
                s = s.substring(length);
            }
            if (s.length() > 0) {
                ret.add(s);
            }
            return ret;
        }
    }
}
