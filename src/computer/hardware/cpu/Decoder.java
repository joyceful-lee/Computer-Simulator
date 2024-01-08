package computer.hardware.cpu;

import computer.TimeToRock;

import java.util.HashMap;

/**
 * Decoder that subtracts instruction strings into parts that we need.
 *
 * @version v0.0.2
 */

public class Decoder {
    public HashMap<String, String> output = new HashMap<>();
    public Decoder(){
        output.put("OP", "000000"); // Operation Code
        output.put("R", "00"); // General Purpose Register
        output.put("IX", "00"); // Index Register
        output.put("I", "0"); // Indirect Bit
        output.put("Address", "000000"); // A binary number, also referred to as Immediate

        output.put("RX", "00"); // Rx for arithmetic and logical instructions
        output.put("RY", "00"); // RY for arithmetic and logical instructions
    }
    /**
     * @param inst The full instruction string
     * @return Returns the wanted dictionary as a HashMap
     */
    public HashMap<String, String> decode(String inst){
        String OP, R, IX, I, Address, RX, RY;
        OP = inst.substring(0, 6);
        R = inst.substring(6, 8);
        IX = inst.substring(8, 10);
        I = inst.substring(10, 11);
        Address = inst.substring(11, 16);
        RX = inst.substring(6, 8);
        RY = inst.substring(8, 10);

        output.put("OP", OP);
        output.put("R", R);
        output.put("IX", IX);
        output.put("I", I);
        output.put("Address", Address);
        output.put("RX", RX);
        output.put("RY", RY);

        TimeToRock.logger.info(
                "OP->" + OP + "\n" +
                        "R->" + R + "\n" +
                        "IX->" + IX + "\n" +
                        "I->" + I + "\n" +
                        "Address->" + Address + "\n" +
                        "RX->" + RX + "\n" +
                        "RY->" + RY + "\n"
        );
        return output;
    }
}
