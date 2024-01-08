package computer.hardware.memory;

import computer.hardware.memory.Memory;
import java.io.BufferedReader;
import java.io.FileReader;

public class CacheTest {
    public static void readAndSave(Memory mem, String path) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        int address;
        String data;
        String line;
        String [] temp;
        while ((line = reader.readLine()) != null) {
            temp = line.split(" ");
            address = Integer.parseInt(temp[0], 16);
            data = Integer.toBinaryString(Integer.parseInt(temp[1], 16));
            data = String.format("%16s", data);
            data = data.replaceAll("\\s", "0");
            mem.writeMemory(address, data);
        }
    }

    public static void main(String [] args) throws Exception {
        Memory mem = new Memory();
        readAndSave(mem, "src/data/IPL.txt");
//        System.out.println(System.getProperty("user.dir"));
        System.out.println(mem.readMemoryWordString(6));
        System.out.println(mem.readMemoryWordString(6));
        System.out.println(mem.readMemoryWordString(7));
        System.out.println(mem.readMemoryWordString(6));
        System.out.println(mem.readMemoryWordString(5));
        System.out.println(mem.readMemoryWordString(8));
    }
}
