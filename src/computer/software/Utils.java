package computer.software;

import java.util.Arrays;

/**
 * Some tool functions.
 *
 * @version v0.2.0
 **/

public class Utils {
    public static boolean[] charArray2Bool(char[] inputBooleanArray){
        boolean[] output = new boolean[inputBooleanArray.length];
        for(int i = 0; i < inputBooleanArray.length; i++){
            output[i] = inputBooleanArray[i] == '1';
        }
        return output;
    }

    public static char[] bool2CharArray(boolean[] inputCharArray){
        char[] output = new char[inputCharArray.length];
        for(int i = 0; i < inputCharArray.length; i++){
            output[i] = inputCharArray[i] ? '1' : '0';
        }
        return output;
    }

    public static String bool2String(boolean[] inputBooleanArray){
        return Arrays.toString(bool2CharArray(inputBooleanArray));
    }

    /**
     * Transform the string into boolean array
     *
     * @param inputString the string that you want to transform
     * @return the result array
     */
    public static boolean[] string2Boolean(String inputString){
        return charArray2Bool(inputString.toCharArray());
    }

    /**
     * Extend the string to the length you want.
     * Fill with 0 as the prefix.
     *
     * @param   inputString the string you want to extend
     * @param   length the length you want to extend to
     * @return  the result after extended
     **/
    public static String extend(String inputString, int length){
        String data = String.format("%" + length+ "s", inputString);
        return data.replaceAll("\\s", "0");
    }
}
