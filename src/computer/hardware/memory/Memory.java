package computer.hardware.memory;

import computer.TimeToRock;

/**
 * Memory. Use an array to implement it. Has a intger to discribe
 * how many memory are already in use and do we need to extend it.
 * I have no EXCEPTION HANDLING here because I think it's not what
 * a hardware should do. Cache already added as a internal class by
 * the end of this class. But it only works when you are using string
 * reading functions(not read bit or read char).
 *
 * Usage:
 *  void reset(): reset the memory.
 *  boolean readMemory(int i, int j): get a single bit from the memory
 *  boolean writeMemory(int i, int j, boolean data): write a single bit to memory
 *  boolean checkAvailability(int i, int j): check one bit of the memory is available or not
 *  boolean checkAvailability(int i): check one word of the memory is available or not
 *  boolean extendMemory(int i): extend the memory to i words
 *  boolean writeMemory(int i, char[] inputCharArray): another write function
 *  boolean writeMemory(int i, String inputString): another write function
 *  boolean[] readMemoryWordArray(int i): do what it says
 *  char[] readMemoryWordChar(int i): do what it says
 *  String readMemoryWordString(int i): do what it says
 *
 * @version v0.1.0
 */

public class Memory{
    private boolean[][] memoryField;
    private int currentSize;
    private int highestUsed;    //marks the highest used memory address
    private Cache cache;

    /**
     * Constructor for objects of class Memory
     */
    public Memory(){
        this.currentSize = 2048;
        this.highestUsed = -1;
        this.memoryField = new boolean[4096][16];
        TimeToRock.logger.info("Memory initialized.");
        this.cache = new Cache(this);
    }

    public void reset(){
        this.currentSize = 2048;
        this.highestUsed = -1;
        this.memoryField = new boolean[4096][16];
        TimeToRock.logger.info(("Memory reset."));
    }

    //The return value of this function is the requested memory.
    public boolean readMemory(int i, int j){
        return memoryField[i][j];
    }

    //Returns wether the write attempt is successful.
    public boolean writeMemory(int i, int j, boolean data){
        if (this.checkAvailability(i, j)){
            this.memoryField[i][j] = data;
            TimeToRock.logger.info("Memory write success.");
            return true;
        }
        TimeToRock.logger.info("Memory write fail.");
        return false;
    }

    //Do this before write data in memory. Returns the check result.
    public boolean checkAvailability(int i, int j){
        if (i > 4096 || i < 0 || j > 15 || j < 0){
            return false;
        }
        else if(i > currentSize){
            return this.extendMemory(i);
        }
        return true;
    }

    public boolean checkAvailability(int i){
        if (i > 4096 || i < 0){
            return false;
        }
        else if(i > currentSize){
            return this.extendMemory(i);
        }
        return true;
    }

    //Do as the name.
    public boolean extendMemory(int i){
        this.currentSize = i;
        if (i > this.highestUsed){
            this.highestUsed = i;
        }
        return false;
    }

    public boolean shrinkMemory(){
        return false;   //Don't know if it's needed.
    }

    public boolean writeMemory(int i, char[] inputCharArray){
        for (int j = 0; j < inputCharArray.length; j++){
            if (!this.writeMemory(i, j, (inputCharArray[j] == '1'))){
                return false;
            }
        }
        return true;
    }

    public boolean writeMemory(int i, String inputString){
        this.cache.invalidCache(i);
        return this.writeMemory(i, inputString.toCharArray());
    }

    public boolean[] readMemoryWordArray(int i){
        return this.memoryField[i];
    }

    public char[] readMemoryWordChar(int i){
        char[] outputCharArray = new char[this.memoryField[i].length];
        for (int j = 0; j < this.memoryField[i].length; j++){
            outputCharArray[j] = (this.memoryField[i][j] ? '1' : '0');
        }
        return outputCharArray;
    }

    public String readMemoryWordString(int i){
        return this.cache.readCache(i);
//        return String.valueOf(this.readMemoryWordChar(i));
    }

    public String readMemoryWordStringWithoutCache(int i){
        return String.valueOf(this.readMemoryWordChar(i));
    }
}
