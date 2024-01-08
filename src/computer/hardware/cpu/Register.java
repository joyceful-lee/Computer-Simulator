package computer.hardware.cpu;

import computer.TimeToRock;

/**
 * Basic register.
 * Usage:
 *  To set value:
 *      Use setValue(anyType) function. anyType can be boolean[] / char[] / String .
 *  To get value:
 *      Use getValue() to get value returned as boolean[] . You can also use getValueChar() or getValueString() .
 *  To get the size of the register:
 *      Use getSize()
 *
 * @version v0.0.2
 */
public class Register{
    private boolean value[];
    private int size;

    public void setValue(boolean [] inputBooleanArray){
        if(inputBooleanArray.length != this.size){
            TimeToRock.logger.warning("Trying to load data to register at a wrong size!");
        }
        for (int i = 0; i < inputBooleanArray.length; i++){
            this.value[i] = inputBooleanArray[i];
        }
        // this.value = inputBooleanArray;  //This may cause problem between object reference and data reference.
    }

    public boolean[] getValue(){
        return this.value;
    }

    /**
     * Constructor for objects of class Register
     */
    public Register(int size){
        this.value = new boolean[size];
        this.size = size;
    }

    //Above are what we have in hardware. Under are some utils.

    public int getSize(){
        return this.size;
    }

    public void setValue(char [] inputCharArray){
        for (int i = 0; i < inputCharArray.length; i++){
            this.value[i] = (inputCharArray[i] == '1' ? true : false);
        }
        if(inputCharArray.length != this.size){
            TimeToRock.logger.warning("Trying to load data to register at a wrong size!");
        }
    }

    public void setValue(String inputString){
        this.setValue(inputString.toCharArray());
    }

    public char[] getValueChar(){
        char[] outputCharArray = new char[this.value.length];
        for (int i = 0; i < this.value.length; i++){
            outputCharArray[i] = (this.value[i] ? '1' : '0');
        }
        return outputCharArray;
    }

    public String getValueString(){
        return String.valueOf(this.getValueChar());
    }
}
