package computer.software;

import computer.TimeToRock;
import computer.hardware.cpu.IOBuff;
import computer.hardware.cpu.MFR;
import computer.hardware.cpu.Registers;
import computer.hardware.memory.Memory;
import computer.hardware.panel.Panel;

import javax.swing.*;
import java.util.HashMap;
import java.lang.Math;

import static computer.TimeToRock.*;

/**
 * Defines actions of all instructions. This class can still be improved.
 * Some instructions should use ALU.
 *
 * @version v0.2.0
 */

public class Instructions {
    IOBuff b = new IOBuff("file");
    IOBuff p = new IOBuff("panel");
    int num = 0;
    String in = null;

    public void execute(HashMap<String, String> inst, Registers regs, Memory mem) {
        String OP, R, IX, I, Address, RX, RY;
        OP = inst.get("OP");
        R = inst.get("R");
        IX = inst.get("IX");
        I = inst.get("I");
        Address = inst.get("Address");
        RX = inst.get("RX");
        RY = inst.get("RY");
        String gpr = ("GPR" + Integer.parseInt(R, 2));

        //Calculate EA
        int EA = 0;
        int cIX;
        if (Integer.parseInt(IX, 2) > 0 && Integer.parseInt(IX, 2) < 4) {
            //IX 1..3
            //Forgot I'm using HashMap, so actually I don't need these switch cases
            String ix = "X" + Integer.parseInt(IX, 2);
            String value = regs.get(ix).getValueString();
            cIX = Integer.parseInt(value, 2);
            if (Integer.parseInt(I, 2) == 0)
                /*
                    The IX field has an index register number
                    the contents of that register are added to
                    the contents of the address field
                 */
                EA = cIX + Integer.parseInt(Address, 2);
            else
                //Both indirect addressing and indexing
                EA = Integer.parseInt(mem.readMemoryWordString(cIX + Integer.parseInt(Address, 2)), 2);
        } else if (Integer.parseInt(IX, 2) == 0) {
            if (Integer.parseInt(I, 2) == 0)
                //No indirect addressing
                EA = Integer.parseInt(Address, 2);
            else
                //Indirect addressing, but NO indexing
                EA = Integer.parseInt(mem.readMemoryWordString(Integer.parseInt(Address, 2)), 2);
        }

        try {
            //Execute instruction
            switch (OP) {
                case "000000" -> {
                    /*
                        HLT
                        Stops the machine.
                     */
                    System.out.println("HLT");
                    TimeToRock.isHalt = true;
//                    regs.get("PC").setValue("000000000000");//reset PC
                }
                case "011110" -> {
                    /*
                        TRAP
                        Traps to memory address 0, which contains the
                        address of a table in memory. Stores the PC+1
                        in memory location 2. The table can have a
                        maximum of 16 entries representing 16 routines
                        for user-specified instructions stored elsewhere
                        in memory. Trap code contains an index into the
                        table, e.g. it takes values 0 – 15. When a TRAP
                        instruction is executed, it goes to the routine
                        whose address is in memory location 0, executes
                        those instructions, and returns to the instruction
                        stored in memory location 2. The PC+1 of the TRAP
                        instruction is stored in memory location 2.
                     */
                    System.out.println("TRAP");
                    mfr.setTRAP(Address);
                }
                case "000001" -> {
                    /*
                        LDR
                        Load the mem[EA] to specific GPR
                        Load Register From Memory, r = 0..3
                        r <- c(EA)
                        note that EA is computed as given above
                     */
                    System.out.println("LDR");
                    String value = mem.readMemoryWordString(EA);
                    regs.get(gpr).setValue(Utils.extend(value, 16));
                }
                case "000010" -> {
                    /*
                        STR
                        Store the specific GPR to mem[EA]
                        Store Register To Memory, r = 0..3
                        Memory(EA) <- c(r)
                     */
                    System.out.println("STR");
                    if(!inBoundCheck(EA)){
                        break;
                    }
                    String value = regs.get(gpr).getValueString();
                    mem.writeMemory(EA, value);
                }
                case "000011" -> {
                    /*
                        LDA
                        Write the mem[EA] value into the specific GPR
                        Load Register with Address, r = 0..3
                        r <- EA
                     */
                    System.out.println("LDA");
                    String value = Utils.extend(Integer.toBinaryString(EA), 16);
                    regs.get(gpr).setValue(value);
                }
                case "101001" -> {
                    /*
                        LDX
                        Write value of mem[EA] to Index Register
                        Load Index Register from Memory, x = 1..3
                        Xx <- c(EA)
                     */
                    System.out.println("LDX");
                    String ix = ("X" + Integer.parseInt(IX, 2));
                    String value = mem.readMemoryWordString(EA);
                    regs.get(ix).setValue(value);
                }
                case "101010" -> {
                    /*
                        STX
                        Store Index Register to Memory. X = 1..3
                        Memory(EA) <- c(Xx)
                     */
                    System.out.println("STX");
                    if(!inBoundCheck(EA)){
                        break;
                    }
                    String ix = ("X" + Integer.parseInt(IX, 2));
                    String value = regs.get(ix).getValueString();
                    mem.writeMemory(EA, value);
                }
                case "001010" -> {
                    /*
                        JZ
                        Jump to EA if register is 0
                        Jump If Zero:
                        If c(r) = 0, then PC <- EA
                        Else PC <- PC+1
                     */
                    System.out.println("JZ");
                    if (Integer.parseInt(regs.get(gpr).getValueString(), 2) == 0) {
                        if(!inBoundCheck(EA)){
                            break;
                        }
                        regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(EA), 12));
                    }
                }
                case "001011" -> {
                    /*
                       JNE
                       Jump to EA if register is not 0
                       Jump If Not Equal:
                       If c(r) != 0, then PC <- EA
                       Else PC <- PC + 1
                     */

                    System.out.println("JNE");
                    if (Integer.parseInt(regs.get(gpr).getValueString(), 2) != 0) {
                        if(!inBoundCheck(EA)){
                            break;
                        }
                        regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(EA), 12));
                    }
                }
                case "001100" -> {
                    /*
                        JCC
                        Jump to EA if register CC is 1
                        Jump If Condition Code
                        cc replaces r for this instruction
                        cc takes values 0, 1, 2, 3 as above and specifies
                        the bit in the Condition Code Register to check;
                        If cc bit  = 1, PC <- EA
                        Else PC <- PC + 1
                     */

                    System.out.println("JCC");
                    if(!inBoundCheck(EA)){
                        break;
                    }
                    if (Integer.parseInt(R, 2) == 0 && Integer.parseInt(regs.get("CC").getValueString(), 2) == 8)
                        regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(EA), 12));
                    else if (Integer.parseInt(R, 2) == 1 && Integer.parseInt(regs.get("CC").getValueString(), 2) == 4)
                        regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(EA), 12));
                    else if (Integer.parseInt(R, 2) == 2 && Integer.parseInt(regs.get("CC").getValueString(), 2) == 2)
                        regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(EA), 12));
                    else if ((Integer.parseInt(R, 2) == 3) && Integer.parseInt(regs.get("CC").getValueString(), 2) == 1)
                        regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(EA), 12));


                }
                case "001101" -> {
                    /*
                        JMA
                        Unconditional Jump To Address
                        PC <- EA,
                        Note: r is ignored in this instruction
                     */

                    System.out.println("JMA");
                    if(!inBoundCheck(EA)){
                        break;
                    }
                    regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(EA), 12));
                }
                case "001110" -> {
                    /*
                        JSR
                        Jump and Save Return Address:
                        R3 <- PC+1;
                        PC <- EA
                        R0 should contain pointer to arguments
                        Argument list should end with –1 (all 1s) value
                     */
                    System.out.println("JSR");
                    if(!inBoundCheck(EA)){
                        break;
                    }
                    //R3 <- PC+1
                    int value = Integer.parseInt(regs.get("PC").getValueString(), 2);
                    regs.get("GPR3").setValue(Utils.extend(Integer.toBinaryString(value), 16));
                    //PC <- EA
                    regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(EA), 12));
                }
                case "001111" -> {
                    /*
                        RFS
                        Return From Subroutine w/ return code as Immed portion (optional)
                        stored in the instruction’s address field.
                        R0 <- Immed; PC <- c(R3)
                        IX, I fields are ignored.
                     */
                    System.out.println("RFS");
                    inBoundCheck(Integer.parseInt(regs.get("GPR3").getValueString(),2));
                    regs.get("GPR0").setValue(Utils.extend(Address, 16));
                    int value = Integer.parseInt(regs.get("GPR3").getValueString(), 2);
                    regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(value), 12));
                    //Optional what???
                }
                case "010000" -> {
                    /*
                        SOB
                        Subtract One and Branch. R = 0..3
                        r <- c(r) – 1
                        If c(r) > 0,  PC <- EA;
                        Else PC <- PC + 1
                     */
                    System.out.println("SOB");
                    //Actually should be c(r) <- c(r) - 1 ? Not quite sure here.
                    int temp = Integer.parseInt(regs.get(gpr).getValueString(), 2) - 1;
                    System.out.println(temp);
                    regs.get(gpr).setValue(Utils.extend(Integer.toBinaryString(temp), 16));
                    if (temp > 0) {
                        if(!inBoundCheck(EA)){
                            break;
                        }
                        regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(EA), 12));
                    }
                }
                case "010001" -> {
                    /*
                        JGE
                        Jump Greater Than or Equal To:
                        If c(r) >= 0, then PC <- EA
                        Else PC <- PC + 1
                     */
                    System.out.println("JGE");
                    if (inBoundCheck(Integer.parseInt(regs.get(gpr).getValueString(),2))) {
                        //Maybe do something else about pipeline here in the future?
                        //PC <- EA
                        regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(EA), 12));
                    } else {
                        //PC <- PC + 1
                        break; //PC+1 already saved
                    }
                }
                case "000100" -> {
                    /*
                        AMR
                        Add Memory To Register, r = 0..3
                        r <- c(r) + c(EA)
                     */
                    System.out.println("AMR");
                    //r <- c(r) + c(EA)
                    HashMap<String, String> res = TimeToRock.alu.ALUAction(
                            "add",
                            regs.get(gpr).getValueString(),
                            mem.readMemoryWordString(EA)
                    );
                    regs.get(gpr).setValue(res.get("res"));
                }
                //Instructions after this are all using ALU
                case "000101" -> {
                    /*
                        SMR
                        Subtract Memory From Register, r = 0..3
                        r<- c(r) – c(EA)
                     */
                    System.out.println("SMR");
                    /*String s1 = regs.get(gpr).getValueString();
                    String s2 = mem.readMemoryWordString(EA);
                    num = Integer.parseInt(s1, 2) - Integer.parseInt(s2,2);
                    System.out.println(num);
                    regs.get(gpr).setValue(Utils.extend(Integer.toBinaryString(num),16));*/
                    HashMap<String, String> res = TimeToRock.alu.ALUAction(
                            "sub",
                            regs.get(gpr).getValueString(),
                            mem.readMemoryWordString(EA)
                    );
                    regs.get(gpr).setValue(res.get("res"));
                }
                case "000110" -> {
                    /*
                        AIR
                        Add Immediate to Register, r = 0..3
                        r <- c(r) + Immed
                        Note:
                        1. if Immed = 0, does nothing
                        2. if c(r) = 0, loads r with Immed
                        IX and I are ignored in this instruction
                     */
                    System.out.println("AIR");
                    HashMap<String, String> res = TimeToRock.alu.ALUAction(
                            "add",
                            regs.get(gpr).getValueString(),
                            Address
                    );
                    regs.get(gpr).setValue(res.get("res"));
                }
                case "000111" -> {
                    /*
                        SIR
                        Subtract Immediate  from Register, r = 0..3
                        r <- c(r) - Immed
                        Note:
                        1. if Immed = 0, does nothing
                        2. if c(r) = 0, loads r1 with –(Immed)
                        IX and I are ignored in this instruction
                     */
                    System.out.println("SIR");
                    HashMap<String, String> res = TimeToRock.alu.ALUAction(
                            "sub",
                            regs.get(gpr).getValueString(),
                            Address
                    );
                    System.out.println(res.get("res"));
                    regs.get(gpr).setValue(res.get("res"));
                }
                case "010100" -> {
                    /*
                        MLT
                        Multiply Register by Register
                        rx, rx+1 <- c(rx) * c(ry)
                        rx must be 0 or 2
                        ry must be 0 or 2
                        rx contains the high order bits, rx+1 contains the low order bits of the result
                        Set OVERFLOW flag, if overflow
                     */
                    System.out.println("MLT");
                    if ((!RX.equals("00") && !RX.equals(("10")) || (!RY.equals("00") && !RY.equals("10")))) {
                        mfr.setMFR("0100");
                        TimeToRock.logger.warning("Instruction error when MLT. RX or RY must be 0 or 2!");
                    } else {
                        HashMap<String, String> res = TimeToRock.alu.ALUAction(
                                "mlt",
                                regs.get("GPR" + Integer.parseInt(RX, 2)).getValueString(),
                                regs.get("GPR" + Integer.parseInt(RY, 2)).getValueString()
                        );
                        regs.get("GPR" + Integer.parseInt(RX, 2)).setValue(res.get("HI"));
                        regs.get("GPR" + (Integer.parseInt(RX, 2) + 1)).setValue(res.get("LO"));
                    }
                }
                case "010101" -> {
                    /*
                        DVD
                        Divide Register by Register
                        rx, rx+1 <- c(rx)/ c(ry)
                        rx must be 0 or 2
                        rx contains the quotient; rx+1 contains the remainder
                        ry must be 0 or 2
                        If c(ry) = 0, set cc(3) to 1 (set DIVZERO flag)
                     */
                    System.out.println("DVD");
                    if ((!RX.equals("00") && !RX.equals(("10")) || (!RY.equals("00") && !RY.equals("10")))) {
                        mfr.setMFR("0100");
                        TimeToRock.logger.warning("Instruction error when DVD. RX or RY must be 0 or 2!");
                    } else {
                        /*int x = Integer.parseInt(regs.get("GPR" + Integer.parseInt(RX, 2)).getValueString(),2);
                        int y = Integer.parseInt(regs.get("GPR" + Integer.parseInt(RY, 2)).getValueString(),2);
                        int quotient = x/y;
                        int remainder = x%y;
                        regs.get("GPR" + Integer.parseInt(RX, 2)).setValue(Utils.extend(Integer.toBinaryString(quotient),16));
                        regs.get("GPR" + (Integer.parseInt(RX, 2) + 1)).setValue(Utils.extend(Integer.toBinaryString(remainder),16));*/
                        HashMap<String, String> res = TimeToRock.alu.ALUAction(
                                "div",
                                regs.get("GPR" + Integer.parseInt(RX, 2)).getValueString(),
                                regs.get("GPR" + Integer.parseInt(RY, 2)).getValueString()
                        );
                        regs.get("GPR" + Integer.parseInt(RX, 2)).setValue(res.get("QUO"));
                        regs.get("GPR" + (Integer.parseInt(RX, 2) + 1)).setValue(res.get("REM"));

                    }
                }
                case "010110" -> {
                    /*
                        TRR
                        Test the Equality of Register and Register
                        If c(rx) = c(ry), set cc(4) <- 1; else, cc(4) <- 0
                     */
                    System.out.println("TRR");
                    String temp = null;
                    if (Integer.parseInt(IX, 2) == 0)
                        temp = "GPR0";
                    else if (Integer.parseInt(IX, 2) == 1)
                        temp = "GPR1";
                    else if (Integer.parseInt(IX, 2) == 2)
                        temp = "GPR2";
                    else if (Integer.parseInt(IX, 2) == 3)
                        temp = "GPR3";
                    int one = Integer.parseInt(regs.get(gpr).getValueString(), 2);
                    int two = Integer.parseInt(regs.get(temp).getValueString(), 2);

                    System.out.println(one + " " + two);
                    if (one == two) {
                        regs.get("CC").setValue("0001");
                    } else {
                        regs.get("CC").setValue("0000");
                    }

                }
                case "010111" -> {
                    /*
                        AND
                        Logical And of Register and Register
                        c(rx) <- c(rx) AND c(ry)
                     */
                    System.out.println("AND");
                    HashMap<String, String> res = TimeToRock.alu.ALUAction(
                            "and",
                            regs.get("GPR" + Integer.parseInt(RX, 2)).getValueString(),
                            regs.get("GPR" + Integer.parseInt(RY, 2)).getValueString()
                    );
                    regs.get("GPR" + Integer.parseInt(RX, 2)).setValue(res.get("res"));
                }
                case "011000" -> {
                    /*
                        ORR
                        Logical Or of Register and Register
                        c(rx) <- c(rx) OR c(ry)
                     */
                    System.out.println("ORR");
                    HashMap<String, String> res = TimeToRock.alu.ALUAction(
                            "or",
                            regs.get("GPR" + Integer.parseInt(RX, 2)).getValueString(),
                            regs.get("GPR" + Integer.parseInt(RY, 2)).getValueString()
                    );
                    regs.get("GPR" + Integer.parseInt(RX, 2)).setValue(res.get("res"));
                }
                case "011001" -> {
                    /*
                        NOT
                        Logical Not of Register To Register
                        C(rx) <- NOT c(rx)
                     */

                    System.out.println("NOT");
                    HashMap<String, String> res = TimeToRock.alu.ALUAction(
                            "not",
                            regs.get("GPR" + Integer.parseInt(RX, 2)).getValueString(),
                            regs.get("GPR" + Integer.parseInt(RY, 2)).getValueString()
                    );
                    regs.get("GPR" + Integer.parseInt(RX, 2)).setValue(res.get("res"));
                }
                case "011111" -> {
                    //shift reg by count;

                    System.out.println("SRC");
                    String ixtemp;
                    if (I == "0") {
                        ixtemp = "00";
                    } else if (I == "1") {
                        ixtemp = "01";
                    } else if (I == "2") {
                        ixtemp = "10";
                    } else {
                        ixtemp = "11";
                    }
                    int aL = Integer.parseInt(ixtemp.substring(0, 1), 2);
                    int lR = Integer.parseInt(ixtemp.substring(1, 2), 2);
                    int count = Integer.parseInt(Address, 2);
                    ixtemp = regs.get(R).getValueString();
                    if (count == 0) {
                        break;
                    }
                    srcFunc(aL, lR, ixtemp.toCharArray(), count, Integer.parseInt(R, 2));
                    break;
                }
                case "100000" -> {
                    //rotate reg by count
                    System.out.println("RRC");
                    String ixtemp;
                    if (I == "0") {
                        ixtemp = "00";
                    } else if (I == "1") {
                        ixtemp = "01";
                    } else if (I == "2") {
                        ixtemp = "10";
                    } else {
                        ixtemp = "11";
                    }
                    int aL = Integer.parseInt(ixtemp.substring(0, 1), 2);
                    int lR = Integer.parseInt(ixtemp.substring(1, 2), 2);
                    int count = Integer.parseInt(Address, 2);
                    ixtemp = regs.get(R).getValueString();
                    if (count == 0) {
                        break;
                    }
                    if (aL == 0) {
                        break;
                    } else {
                        rccFunc(lR, ixtemp.toCharArray(), count, Integer.parseInt(R, 2));
                    }
                    break;
                }
                case "111101" -> {
                    //input char to reg from device
                    System.out.println("IN");
                    int devid = Integer.parseInt(Address, 2);
                    switch (devid) {
                        case (0):
                            String text = Panel.keyText.getText();
                            if ((text.trim().length() <= 0)) {
                                mfr.setMFR("0100");
                                System.out.println("No text via keyboard.");
                                TimeToRock.logger.warning("Invalid input!");
                            }
                            if (textCheck(text)) {
                                if(p.isEmpty()){
                                    p.panelText();
                                }
                                readText(text, gpr);
                            } else {
                                System.out.println("Problem with input");
                                isHalt = true;
                            }
                            break;
                        case (2):{
                            //buffer;
                            if(b.isEmpty()) {
                                b.setFile();
                            }
                            int ascii = b.getOneDigit();
                            regs.get(gpr).setValue(Utils.extend(Integer.toBinaryString(ascii), 16));
                        }
                        break;
                        case (3): //switches
                            //read(text);
                            break;
                        case (4):
                            read(regs.get("GPR0").getValueString(), "GPR0");
                            break;
                        case (5): //gpr1
                            read(regs.get("GPR1").getValueString(), "GPR1");
                            break;
                        case (6): //gpr2
                            read(regs.get("GPR2").getValueString(), "GPR2");
                            break;
                        case (7): //gpr3
                            read(regs.get("GPR3").getValueString(), "GPR3");
                            break;
                        case (8): //IX1
                            read(regs.get("X1").getValueString(), "X1");
                            break;
                        case (9): //IX2
                            read(regs.get("X2").getValueString(), "X2");
                            break;
                        case (10): //IX3
                            read(regs.get("X3").getValueString(), "X3");
                            break;
                        case (11): //MBR
                            read(regs.get("MBR").getValueString(), "MBR");
                            break;
                        default:
                            JFrame frame = new JFrame();
                            mfr.setMFR("0100");
                            TimeToRock.logger.warning("Unrecognized devid!");
                            break;
                    }
                    break;
                }
                case "111110" -> {
                    //output char to device from reg
                    System.out.println("OUT");
                    int devid = Integer.parseInt(Address, 2);
                    switch (devid) {
                        case (1): //from unspecified gpr
                            System.out.println("Register Character: " + Integer.parseInt(regs.get(gpr).getValueString(), 2) + "\n");
                            Panel.printText.append("Register Character: " + Integer.parseInt(regs.get(gpr).getValueString(), 2) + "\n");
                            break;
                        case (3): //switches
                            //print(text);
                            break;
                        case (4): //gpr0
                            System.out.println("Register Character: " + Integer.parseInt(regs.get("GPR0").getValueString(), 2) + "\n");
                            Panel.printText.append("Register Character: " + Integer.parseInt(regs.get("GPR0").getValueString(), 2) + "\n");
                            break;
                        case (5): //gpr1
                            System.out.println("Register Character: " + Integer.parseInt(regs.get("GPR1").getValueString(),2) + "\n");
                            Panel.printText.append("\n" + "Register Character: " + Integer.parseInt(regs.get("GPR1").getValueString(),2) + "\n");
                            break;
                        case (6): //gpr2
                            System.out.println("Register Character: " + Integer.parseInt(regs.get("GPR2").getValueString(), 2) + "\n");
                            Panel.printText.append("Register Character: " + Integer.parseInt(regs.get("GPR2").getValueString(), 2) + "\n");
                            break;
                        case (7): //gpr3
                            System.out.println("Register Character: " + Integer.parseInt(regs.get("GPR3").getValueString(), 2) + "\n");
                            Panel.printText.append("Register Character: " + Integer.parseInt(regs.get("GPR3").getValueString(), 2) + "\n");
                            break;
                        case (8): //IX1
                            System.out.println("Register Character: " + Integer.parseInt(regs.get("X1").getValueString(), 2) + "\n");
                            Panel.printText.append("Register Character: " + Integer.parseInt(regs.get("X1").getValueString(), 2) + "\n");
                            break;
                        case (9): //IX2
                            System.out.println("Register Character: " + Integer.parseInt(regs.get("X2").getValueString(), 2) + "\n");
                            Panel.printText.append("Register Character: " + Integer.parseInt(regs.get("X2").getValueString(), 2) + "\n");
                            break;
                        case (10): //IX3
                            System.out.println("Register Character: " + Integer.parseInt(regs.get("X3").getValueString(), 2) + "\n");
                            Panel.printText.append("Register Character: " + Integer.parseInt(regs.get("X3").getValueString(), 2) + "\n");
                            break;
                        case (11): //MBR
                            System.out.println("Register Character: " + Integer.parseInt(regs.get("MBR").getValueString(), 2) + "\n");
                            Panel.printText.append("Register Character: " + Integer.parseInt(regs.get("MBR").getValueString(), 2) + "\n");
                            break;
                        case (12): //Print text
                            System.out.println((char)Integer.parseInt(regs.get(gpr).getValueString(), 2));
                            Panel.printText.append(String.valueOf((char)Integer.parseInt(regs.get(gpr).getValueString(), 2)));
                            break;
                        default:
                            System.out.println("Unrecognized devid.");
                            mfr.setMFR("0100");
                            TimeToRock.logger.warning("Unrecognized devid!");
                            break;
                    }
                    break;
                }
                case "111111" -> {
                    System.out.println("CHK");
                    //check device status to reg
                    switch(Integer.parseInt(Address,2)){
                        case(0): {
                            String text = Panel.keyText.getText();
                            if ((text.trim().length() <= 0)) {//keyboard is empty = 0
                                regs.get(gpr).setValue(Utils.extend(Integer.toBinaryString(0),12));
                            } else{ //keyboard not empty = 1;
                                regs.get(gpr).setValue(Utils.extend(Integer.toBinaryString(1),12));
                            }
                            break;
                        }
                        case(1):{
                            //console printer always on
                            regs.get(gpr).setValue(Utils.extend(Integer.toBinaryString(1),12));
                            break;
                        }
                        case(2):{
                            if(p.isEmpty() && b.isEmpty()){ //buffer is empty = 0
                                regs.get(gpr).setValue(Utils.extend(Integer.toBinaryString(0),12));
                            } else{
                                regs.get(gpr).setValue(Utils.extend(Integer.toBinaryString(1),12));
                            }
                            break;
                        }
                        default:
                            mfr.setMFR("0100");
                    }
                }
                case "100001" -> {
                    /* FADD - Floating Add Memory to Register
                     *  c(fr) <- c(fr) + c(EA)
                     *  c(fr) <- c(fr) + c(c(EA)), if I bit set
                     *  fr must be 0 or 1.
                     *  OVERFLOW may be set
                     */
                    System.out.println("FADD");
                    if (Integer.parseInt(R,2)>1) {
                        TimeToRock.logger.warning("Instruction error when FADD. fr must be 0 or 1!");
                        mfr.setMFR("0100");
                    }
                    double exp = Integer.parseInt(regs.get("FR" + Integer.parseInt(R, 2)).getValueString().substring(1,8),2);
                    double total = Integer.parseInt(regs.get("FR" + Integer.parseInt(R, 2)).getValueString().substring(8,16),2);
                    if(Integer.parseInt(regs.get("FR" + Integer.parseInt(R, 2)).getValueString().substring(0,1), 2) == 1){
                        total = total * -1;
                    }
                    total = total + Integer.parseInt(mem.readMemoryWordString(EA),2);
                    String exact = Integer.toBinaryString((int)total);
                    int place = exact.length();
                    String s;
                    String tempExp = Utils.extend(Integer.toBinaryString(place), 7); //exponent
                    String tempM = Utils.extend((exact), 8); //mantissa
                    if (total < 0) { //<0 = negative so sign = 1;
                        s = "1";
                    }else{
                        s = "0";
                    }

                    regs.get("FR" + Integer.parseInt(R, 2)).setValue(s + tempExp + tempM);
                    break;
                }
                case "100010" -> {
                    /* FSUB - Floating subtract from Register
                     *  c(fr) <- c(fr) - c(EA)
                     *  c(fr) <- c(fr) + c(c(EA))
                     *  fr must be 1 or 0
                     *  UNDERFLOW may be set */
                    System.out.println("FSUB");
                    if (Integer.parseInt(R,2)>1) {
                        TimeToRock.logger.warning("Instruction error when FADD. fr must be 0 or 1!");
                        mfr.setMFR("0100");
                    }
                    double exp = Integer.parseInt(regs.get("FR" + Integer.parseInt(R, 2)).getValueString().substring(1,8),2);
                    double total = Integer.parseInt(regs.get("FR" + Integer.parseInt(R, 2)).getValueString().substring(8,16),2);
                    if(Integer.parseInt(regs.get("FR" + Integer.parseInt(R, 2)).getValueString().substring(0,1), 2) == 1){
                        total = total * -1;
                    }
                    total = total - Integer.parseInt(mem.readMemoryWordString(EA),2);
                    String exact = Integer.toBinaryString((int)total);
                    int place = exact.length();
                    String s;
                    String tempExp = Utils.extend(Integer.toBinaryString(place), 7); //exponent
                    String tempM = Utils.extend((exact), 8); //mantissa
                    if (total < 0) { //<0 = negative so sign = 1;
                        s = "1";
                    }else{
                        s = "0";
                    }

                    regs.get("FR" + Integer.parseInt(R, 2)).setValue(s + tempExp + tempM);
                    break;
                }
                case "100011" -> {
                    /* VADD - Vector Add
                     *  c(fr) <- c(fr) - c(EA)
                     *  c(fr) <- c(fr) + c(c(EA))
                     *  fr must be 1 or 0
                     *  UNDERFLOW may be set */
                    System.out.println("VADD");
                    if(Integer.parseInt(R, 2) > 1){
                        MFR.setMFR("0010");
                    }
                    int length = Integer.parseInt(R, 2);
                    int address, address1;
                    if (Integer.parseInt(I, 2) == 1) {
                        address = Integer.parseInt(mem.readMemoryWordString(EA),2);
                        address1 = Integer.parseInt(mem.readMemoryWordString(EA + 1),2);
                    } else {
                        address = EA;
                        address1 = EA + 1;
                    }
                    //sum each vector then add the two
                    int sum = 0, sum1 = 0;
                    for (int i = 0; i < length; i++) {
                        sum = sum + Integer.parseInt(mem.readMemoryWordString(address + i),2);
                    }
                    for (int j = 0; j < length; j++) {
                        sum1 = sum1 + Integer.parseInt(mem.readMemoryWordString(address1 + j),2);
                    }
                    int temp = sum + sum1;
                    regs.get("FR" + Integer.parseInt(R, 2)).setValue(Utils.extend(Integer.toBinaryString(temp),16));
                    break;
                }
                case "100100"-> {
                    //VSUB
                    System.out.println("VSUB");
                    if(Integer.parseInt(R, 2) > 1){
                        MFR.setMFR("0010");
                    }
                    int length = Integer.parseInt(R, 2);
                    int address, address1;
                    if (Integer.parseInt(I, 2) == 1) {
                        address = Integer.parseInt(mem.readMemoryWordString(EA),2);
                        address1 = Integer.parseInt(mem.readMemoryWordString(EA + 1),2);
                    } else {
                        address = EA;
                        address1 = EA + 1;
                    }
                    //sum each vector then subtract the two
                    int sum = 0, sum1 = 0;
                    for (int i = 0; i < length; i++) {
                        sum = sum + Integer.parseInt(mem.readMemoryWordString(address + i),2);
                    }
                    for (int j = 0; j < length; j++) {
                        sum1 = sum1 + Integer.parseInt(mem.readMemoryWordString(address1 + j),2);
                    }
                    int temp = sum - sum1;
                    regs.get("FR" + Integer.parseInt(R, 2)).setValue(Utils.extend(Integer.toBinaryString(temp),16));
                    break;
                }

                case "100101"->{
                    //CNVRT
                    System.out.println("CNVRT");
                    if(Integer.parseInt(R, 2) > 1){
                        MFR.setMFR("0010");
                    }
                    String memory = mem.readMemoryWordString(EA);
                    int sign = Integer.parseInt(memory.substring(0,1),2);
                    int exponent = Integer.parseInt(memory.substring(1,8),2);
                    int mantissa = Integer.parseInt(memory.substring(8,16),2);

                    if(Integer.parseInt(R,2) == 1) {
                        //fixed -> floating
                        String exact = Integer.toBinaryString(Integer.parseInt(memory, 2));
                        int place = exact.length();
                        String s;
                        String tempExp = Utils.extend(Integer.toBinaryString(place), 7); //exponent
                        String tempM = Utils.extend(exact, 8); //mantissa
                        if (Integer.parseInt(memory, 2) < 0) { //<0 = negative so sign = 1;
                            s = "1";
                        }else{
                            s = "0";
                        }

                        regs.get("FR0").setValue(s + tempExp + tempM);
                    }
                    else if(Integer.parseInt(R,2) == 0){
                        //floating -> fixed
                        double num = mantissa * Math.pow(2, exponent);
                        if (sign == 1){
                            //negative number
                            num = num * -1;
                        }
                        regs.get("FR0").setValue(Utils.extend(Integer.toBinaryString((int) num),16));
                    }
                    break;
                }
                case"110010"->{
                    //LDFR - EA = sign + exponent, EA+1 = mantissa
                    System.out.println("LDFR");
                    if(Integer.parseInt(R, 2) > 1){
                        MFR.setMFR("0010");
                    }
                    int exp = Integer.parseInt(mem.readMemoryWordString(EA),2);
                    int man = Integer.parseInt(mem.readMemoryWordString(EA+1),2);
                    if((man*Math.pow(2, exp)>0)){
                        regs.get("FR" + Integer.parseInt(R, 2)).setValue("0" + Utils.extend(Integer.toBinaryString((int)(man*Math.pow(2, exp))), 15));
                    }else {

                        regs.get("FR" + Integer.parseInt(R, 2)).setValue("1" + Utils.extend(Integer.toBinaryString((int)(man*Math.pow(2, exp))), 15));
                    }
                }
                case"110011"->{
                    //STFR - EA = sign + exponent, EA+1 = mantissa
                    System.out.println("STFR");
                    if(Integer.parseInt(R, 2) > 1) {
                        MFR.setMFR("0010");
                    }
                    mem.writeMemory(EA, regs.get("FR"+Integer.parseInt(R,2)).getValueString().substring(0, 8));
                    mem.writeMemory(EA+1, regs.get("FR"+Integer.parseInt(R,2)).getValueString().substring(8, 16));
                }
                default -> {
                    regs.get("MFR").setValue("0100"); //illegal operation code
                    throw new IllegalStateException("Unexpected OP code: " + OP);
                }
            }
        } catch (Exception e) {
            TimeToRock.logger.warning("Instruction " + OP + " Error");
            TimeToRock.logger.warning(e.getMessage());
            System.out.println("Instruction " + OP + " Error");
        }
    }

    //shift for src instruction
    public void srcFunc(int aL, int lR, char[] temp, int count, int gpr) {
        char[] empty = new char[temp.length];
        if (aL == 1) { //logically
            if (lR == 1) { //left
                for (int i = 0; i < temp.length; i++) {
                    if (i >= (15 - count)) {
                        empty[i] = '0';
                    } else {
                        empty[i] = temp[i + count];
                    }
                }
                regs.get(Integer.toBinaryString(gpr)).setValue(empty);
            } else if (lR == 0) { //right
                for (int i = 15; i >= 0; i--) {
                    if (i <= (15 - count)) {
                        empty[i] = '0';
                    } else {
                        empty[i] = temp[i - count];
                    }
                }
                regs.get(Integer.toBinaryString(gpr)).setValue(empty);
            }
        } else if (aL == 0) {
            if (lR == 1) { //left
                for (int i = 0; i < temp.length; i++) {
                    if (i >= (15 - count)) {
                        empty[i] = '0';
                    } else {
                        empty[i] = temp[i + count];
                    }
                }
                regs.get("PC").setValue("1000");
                regs.get(Integer.toBinaryString(gpr)).setValue(empty);
            } else if (lR == 0) { //right
                for (int i = 15; i >= 0; i--) {
                    if (i <= (15 - count)) {
                        empty[i] = temp[i - count];
                    } else {
                        empty[i] = temp[i];
                    }
                }
                regs.get("PC").setValue("0100");
                regs.get(Integer.toBinaryString(gpr)).setValue(empty);
            }
        }
    }

    //used for rcc opcode
    public void rccFunc(int lR, char[] temp, int count, int gpr) {
        char[] empty = new char[temp.length];

        if (lR == 1) { //left
            int j = 0;
            for (int i = 0; i < temp.length; i++) {
                if (i >= (15 - count)) {
                    empty[i] = temp[j];
                    j++;
                } else {
                    empty[i] = temp[i + count];
                }
            }
            regs.get(Integer.toBinaryString(gpr)).setValue(empty);
        } else if (lR == 0) { //right
            int j = temp.length;
            for (int i = 15; i >= 0; i--) {
                if (i <= (15 - count)) {
                    empty[i] = temp[j - 1];
                    j--;
                } else {
                    empty[i] = temp[i - count];
                }
            }
            regs.get(Integer.toBinaryString(gpr)).setValue(empty);
        }
    }

    /* used in in instruction - checks if valid number between 0 and 65535
     * if not, program will halt with quick explanation (neg int or too big)
     */
    public static boolean numberCheck(String str) {
        String[] temp = str.split("\\n");
        for (int i = 0; i < temp.length; i++) {
            if (Integer.parseInt(temp[i]) < 0) {
                System.out.println("Negative integer.");
                return false;
            }
            if (Integer.parseInt(temp[i]) > 65535) {
                System.out.println("Integer too big.");
                return false;
            }
        }
        return true;
    }

    public static boolean textCheck(String str) {
        return true;
    }

    //sets register and prints data set
    public void read(String input, String gpr) {
        String[] temp = input.split("\\s+");
        System.out.println("Input: " + temp[num] + "\n");
        String value = Utils.extend(Integer.toBinaryString(Integer.parseInt(temp[num])), 16);
        regs.get(gpr).setValue(value);
        num++;
    }

    public boolean numberCreator(String str, String gpr) {
        char[] ch = str.toCharArray();
        String next = new String(ch);
        for (int i = 0; i < str.length(); i++) {
            if (Integer.parseInt(String.valueOf(ch[i])) < 0) {
                return false;
            }
        }
        Integer.parseInt(next);
        if (numberCheck(next)) {
            String value = Utils.extend(Integer.toBinaryString(Integer.parseInt(next)), 16);
            regs.get(gpr).setValue(value);
            System.out.println("Input: " + next + "\n");
            num++;
            return true;
        } else {
            return false;
        }
    }

    public boolean readText (String input, String gpr) {
        String[] temp = input.split("\\s+");
        char[] tempChar = input.toCharArray();
        //checks if input is number, then saves whole number (not individual characters)
        try {
            numberCreator(temp[num], gpr);
            return true;
        }catch(Exception e){
            //if not number, saves individual characters at each memory location
            int number = p.getOneDigit();
            System.out.println("Input: " + (char)number + "\n");
            regs.get(gpr).setValue(Utils.extend(Integer.toBinaryString(number), 16));
            return true;
        }
    }

    public boolean inBoundCheck(int ea){
        if(ea > 2048) {
            mfr.setMFR("0100");//TODO mfr
            return false;
        }
        if(ea < 6){
            mfr.setMFR("0001");
            return false;
        }
        return true;
    }
}