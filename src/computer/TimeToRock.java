package computer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Logger;

import computer.hardware.cpu.*;
import computer.hardware.memory.Memory;
import computer.hardware.panel.Panel;
import computer.software.File2Mem;

/**
 * Main class in this program.
 *
 * @version v0.2.0
 */

public class TimeToRock {
    public static Logger logger = Logger.getGlobal();
    public static Registers regs = new Registers();
    public static Memory mem = new Memory();
    public static Controller controller = new Controller();
    public static boolean isHalt = false;
    public static int steps = 0;
    public static ALU alu = new ALU();
    public static Panel gui;
    public static MFR mfr = new MFR();

    public static void main(String [] args){
        gui = new Panel();
    }
}
