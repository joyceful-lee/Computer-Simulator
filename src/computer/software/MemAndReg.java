package computer.software;

import computer.hardware.memory.Memory;
import computer.hardware.cpu.Register;

/**
 * A class that helps to transfer value between memory and register.
 *
 * @version v0.2.0
 **/

public class MemAndReg {
    public boolean reg2Mem(Register reg, Memory mem, int memAddress){
        return mem.writeMemory(memAddress, reg.getValueString());
    }
    public void mem2Reg(Memory mem, int memAddress, Register reg){
        reg.setValue(mem.readMemoryWordString(memAddress));
    }
}
