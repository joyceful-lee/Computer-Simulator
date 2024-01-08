package computer.hardware.cpu;

import computer.software.Utils;

import java.io.IOException;

import static computer.TimeToRock.*;

public class MFR {
    private static String trapAddr;
    private static int mfrAddr = 950; //mfr at 950
    private static String code;
    private static int[] trapList = new int[16];
    public MFR(){
        trapList[0] = Integer.parseInt("001111101000",2);//trap at 1000
        for (int i = 1; i < 16; i++){
            trapList[i] = trapList[i-1] + 20;
        }
        trapAddr = Utils.extend(code,12); //set right trap list start
        mem.writeMemory(0, trapAddr);
    }

    //for machine fault, sets and saves PC & sets MFR
    public static boolean setMFR(String code){
        setRoutine();
        regs.get("MFR").setValue(code);
        mem.writeMemory(4, Utils.extend(regs.get("PC").getValueString(),16)); //store PC
        mem.writeMemory(1, Utils.extend(Integer.toBinaryString(mfrAddr),16));
        int temp = Integer.parseInt(mem.readMemoryWordString(1),2);
        regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(temp),12)); //set pc to address at mem 1
        return true;
    }

    //for trap instruction, set and saves PC
    public void setTRAP(String address) throws IOException {
        if(Integer.parseInt(address,2) > 15 || Integer.parseInt(address,2) < 15){
            setMFR("0001");
        }
        else {
            mem.writeMemory(2, Utils.extend(regs.get("PC").getValueString(),16)); //store PC
            int temp = Integer.parseInt(mem.readMemoryWordString(0), 2);
            regs.get("PC").setValue(Utils.extend(Integer.toBinaryString(temp), 12)); //set to pc to address at mem 0
            this.code = address;
        }
    }

    public String getTrapAddr(){
        return trapAddr;
    }

    public int getMfrAddr(){
        return mfrAddr;
    }

    public String getList(){
        String temp = trapList.toString();
        return temp;
    }

    public boolean returnTrap(){
        regs.get("PC").setValue(mem.readMemoryWordString(2));
        return true;
    }

    public boolean returnMFR(){
        regs.get("PC").setValue(mem.readMemoryWordString(4));
        return true;
    }

    public static void setRoutine(){
        mem.writeMemory(950, "0000000000000000");
        isHalt=false;
        mem.writeMemory(951, "0011010000100100");
    }

    public static void main(String [] args){

    }
}
