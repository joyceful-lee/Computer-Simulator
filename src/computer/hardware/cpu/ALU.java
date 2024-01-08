package computer.hardware.cpu;

import computer.software.Utils;

import java.util.HashMap;

/**
 * A component that contains arithmetic functions.
 *
 * @version v0.2.1
 **/

public class ALU {
    public HashMap<String, String> output;
    private int a;
    private int b;
    private int c;
    private String res;
    public ALU(){
        this.output = new HashMap<>();
//        this.output.put("res", "0");
//        this.output.put("overflow", "0");
    }

    /**
     * Arithmetic actions are executed here. Including add, mul, sub, div, and, or, not.
     * Alert!!! Make sure to extend the input string to the right length
     * before using this function. And the length of the result depends
     * on the length of input A.
     *
     * @param OP The action that the ALU will do
     * @param inputA The input value string A
     * @param inputB The input value string B
     * @return A HashMap that contains the result("res") and the overflow("overflow") signal. For mul and div
     * the results are "HI"&"LO"
     */
    public HashMap<String, String> ALUAction(String OP, String inputA, String inputB){
        this.a = Integer.parseInt(inputA, 2);
        this.b = Integer.parseInt(inputB, 2);
        switch (OP) {
            case "add"-> this.c = this.a + this.b;
            case "mul"-> this.c = this.a * this.b;
            case "sub"-> this.c = this.a - this.b;
            case "div"-> this.c = this.a / this.b;
            case "and"-> this.c = this.a & this.b;
            case "or" -> this.c = this.a | this.b;
            case "not"-> this.c = ~this.a;
        }
        // Check & keep the length
        this.res = Integer.toBinaryString(c);
        //Among all the OPs, mul and div are special
        if (OP.equals("mul")){
            if (this.res.length() > 2 * inputA.length()){
                this.output.put("overflow", "1");
                this.output.put("HI", this.res.substring(
                        this.res.length() - 2 * inputA.length(),
                        this.res.length() - inputA.length()
                ));
                this.output.put("LO", this.res.substring(
                        this.res.length() - inputA.length()
                ));
            }
            else{
                this.res = Utils.extend(Integer.toBinaryString(c),32);
                this.output.put("HI", this.res.substring(
                        0,
                        inputA.length()
                ));
                this.output.put("LO", this.res.substring(
                        inputA.length()
                ));
            }
        }
        else if(OP.equals("div")){
            if (this.res.length() > inputA.length()) {
                this.output.put("overflow", "1");
                this.output.put("res", this.res.substring(
                        this.res.length() - inputA.length()
                ));
            } else {
                int d = this.a % this.b;
                String rem = Utils.extend(Integer.toBinaryString(d),16);
                this.output.put("QUO", Utils.extend(this.res,16));
                this.output.put("REM", rem);
            }
        }
        // Not mul or div
        else {
            if (this.res.length() > inputA.length()) {
                this.output.put("overflow", "1");
                this.output.put("res", this.res.substring(
                        this.res.length() - inputA.length()
                ));
            } else {
                this.res = Utils.extend(this.res, inputA.length());
                this.output.put("overflow", "0");
                this.output.put("res", this.res);
            }
        }
        return this.output;
    }
}
