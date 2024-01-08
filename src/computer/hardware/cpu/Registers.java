package computer.hardware.cpu;

import computer.software.Utils;

import java.util.HashMap;

/**
 * A bunch of registers. All the registers should be declared & called here.
 * To know how to use them, see the comment in Register.java
 *
 * @version v0.2.1
 */
public class Registers
{
    /*
    public Register PC; //Program Counter: address of next instruction to be executed. Note that 2^12 = 4096.
    public Register CC; //Condition Code: set when arithmetic/logical operations are executed;
    // it has four 1-bit elements: overflow, underflow, division by zero, equal-or-not.
    //They may be referenced as cc(0), cc(1), cc(2), cc(3).
    //Or by the names OVERFLOW, UNDERFLOW, DIVZERO, EQUALORNOT
    public Register IR; //Instruction Register: holds the instruction to be executed
    public Register MAR;    //Memory Address Register: holds the address of the word to be fetched from memory
    public Register MBR;    //Memory Buffer Register: holds the word just fetched from or the word to be /last stored into memory
    public Register MFR;    //Machine Fault Register: contains the ID code if a machine fault after it occurs
    //public Register[] X;    //Index Register: contains a base address that supports base register addressing of memory.
    public Register X1; //Index Register as above. Because [] starts from 0, maybe it's better to name it in this way to prevent misunderstanding.
    public Register X2;
    public Register X3;
    public Register GPR0;
    public Register GPR1;
    public Register GPR2;
    public Register GPR3;
    public Register[] common; // 32 common registers.
    */
    private HashMap<String, Register> registers;

    /**
     * Constructor for objects of class Registers
     */
    public Registers() {
        this.registers = new HashMap<>();
        this.registers.put("PC", new Register(12));
        this.registers.get("PC").setValue("000000000110");
        this.registers.put("CC", new Register(4));
        this.registers.put("PRE-IR", new Register(16));
        this.registers.put("IR", new Register(16));
        this.registers.put("MAR", new Register(12));
        this.registers.put("MBR", new Register(16));
        this.registers.put("MFR", new Register(4));
        this.registers.put("X1", new Register(16));
        this.registers.put("X2", new Register(16));
        this.registers.put("X3", new Register(16));
        this.registers.put("GPR0", new Register(16));
        this.registers.put("GPR1", new Register(16));
        this.registers.put("GPR2", new Register(16));
        this.registers.put("GPR3", new Register(16));
        this.registers.put("FR0", new Register(16));
        this.registers.put("FR1", new Register(16));
    }

    //Get registers
    public HashMap<String, Register> getRegisters(){
        return this.registers;
    }

    //Get a single register
    public Register get(String name){
        return this.registers.get(name);
    }

    //PC+1. Simulates a simple adder with overflow signal returned.
    public boolean PCStepOne()
    {
        /*
        boolean[] value = this.registers.get("PC").getValue();
        boolean carry = true;

        for (int i = 0; i < 12; i++){
            if (carry){
                if (value[i]){
                    value[i] = false;
                }
                else{
                    value[i] = true;
                    carry = false;
                }
            }
        }
        return carry;
        */
        String value = this.registers.get("PC").getValueString();
        String newValue = Integer.toBinaryString(Integer.parseInt(value, 2) + 1);
        boolean overflow = false;
        if(newValue.length() > 12)
            overflow = true;
        String newValueString = Utils.extend(newValue, 12);
        this.registers.get("PC").setValue(newValueString);
        return overflow;
    }

    //Set all the registers to 0
    public void reset(){
        this.registers.get("PC").setValue("000000000110");
        this.registers.get("PRE-IR").setValue("0000000000000000");
        this.registers.get("IR").setValue("0000000000000000");
        this.registers.get("MAR").setValue("000000000000");
        this.registers.get("MBR").setValue("0000000000000000");
        this.registers.get("X1").setValue("0000000000000000");
        this.registers.get("X2").setValue("0000000000000000");
        this.registers.get("X3").setValue("0000000000000000");
        this.registers.get("GPR0").setValue("0000000000000000");
        this.registers.get("GPR1").setValue("0000000000000000");
        this.registers.get("GPR2").setValue("0000000000000000");
        this.registers.get("GPR3").setValue("0000000000000000");
        this.registers.get("FR0").setValue("0000000000000000");
        this.registers.get("FR1").setValue("0000000000000000");
        this.registers.get("MFR").setValue("0000");
        this.registers.get("CC").setValue("0000");
    }
}
