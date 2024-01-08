package computer;

import computer.hardware.cpu.ALU;

import java.util.HashMap;

/**
 * Ignore this class.
 * Used this to check if some code works.
 */
public class test {
    public static void main(String [] args){
        ALU alu = new ALU();
        HashMap<String, String> res = new HashMap<>();
        res = alu.ALUAction("and", "0001", "0101");
        System.out.println(res.get("res"));
    }
}
