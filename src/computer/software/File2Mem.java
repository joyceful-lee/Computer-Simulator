package computer.software;

import computer.hardware.cpu.Registers;
import computer.hardware.memory.Memory;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
 * Reads in Instructions.txt automatically when called via IPL
 * Sets text into memory.
 *
 * @version v0.0.2
 *
 * */
public class File2Mem {
    public static void main(String[] args) {

    }

    //the function to read the memory file
    //reads into a List then converts said list into an array
    public static void readAndSave(String name) throws Exception{
        List<String> data = new ArrayList<>();
        Memory mem = computer.TimeToRock.mem;
        JFrame frame = new JFrame();
        ClassLoader classLoader = File2Mem.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream(name);
        if(is == null) {
            JOptionPane.showMessageDialog(frame, "File not found.");
        }
        try(InputStreamReader sR = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader buffRead = new BufferedReader(sR)){
            String str;

            //reads file into string list
            while((str=buffRead.readLine())!=null) {
                data.add(str);
            }
            //changes list to data array, primArray for primary Array
            String[] primArray = new String[data.size()];
            primArray = data.toArray(primArray);
            String[] temp;
            int address;
            String line;
            int start = 6;
            for (int i = 0; i < data.size(); i++) {
                temp = primArray[i].split(" ");
                address = Integer.parseInt(temp[0], 16);
                line = Integer.toBinaryString(Integer.parseInt(temp[1], 16));
                line = String.format("%16s", line);
                line = line.replaceAll("\\s", "0");

                int offset;
                offset = start - address; //6 for beginning of memory
                start++;
                mem.writeMemory(offset + address, line);
            }

        }
        char[] arr = Integer.toBinaryString(7).toCharArray();
        char[] temp = new char[12];
        for(int i = 0; i< 12; i++) {
            if((arr.length-(i+1))<0 ) {
                temp[11-i] = '0';
            }
            else {
                temp[11-i] = arr[arr.length-(i+1)];
            }
        }
    }
}