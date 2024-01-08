package computer.hardware.memory;

/**
 * A simple fully associative cache work with Memory.
 * B: Size of each block, 1 words
 * C: Total size of cache, 16 words
 * M: Total size of memory(which is actually 4096 here)
 *
 * @version v0.0.2
 */

public class Cache {
    private final int B = 1;
    private final int C = 16;
    private int[] dataN; // Block number stored in cache
    private String[] data;
    private int nextAvaliable = 0;
    private Memory mem;

    public Cache(Memory mem) {
        //16 lines of cache
        this.data = new String[C];
        this.dataN = new int[C / B];
        for (int i = 0; i < C / B; i++)
            this.dataN[i] = -1;
        this.mem = mem;
    }

    /**
     * See if that block of memory is already in the cache. If so, read it from cache.
     * If not, read it from memory and put it into cache.
     */
    public String readCache(int A) {
        int MBN = A / B; // Memory block number
        // int MA = A % B; // Memory in block address
        boolean already_in = false; // if already exist in cache
        int i;
        for (i = 0; i < (C / B); i++){
            if (MBN == dataN[i]){
                already_in = true;
                break;
            }
        }
        //if already exist in cache
        if(already_in){
//            System.out.println("This mem already exist in cache. Read directly.");
            return data[i];
            //return data[i * B + MA];
        }
        //if not in cache yet
        else{
//            System.out.println("This mem not in cache yet. Write it in and read directly.");
            //把内存从(A / B) * B到(A / B) * B + B的数据存到缓存下一块可用空间里。现在一共有16快，从0存到15然后回到0。
            data[nextAvaliable] = mem.readMemoryWordStringWithoutCache(A);
            dataN[nextAvaliable] = A;
            String returnValue = data[nextAvaliable];
            if (nextAvaliable < C - 1)
                nextAvaliable++;
            else
                nextAvaliable = 0;
            return returnValue;
        }
    }

    // Invalid cache when the related memory is changed
    public void invalidCache(int A){
        int MBN = A / B; // Memory block number
        int i;
        for (i = 0; i < (C / B); i++){
            if (MBN == dataN[i]){
                dataN[i] = -1;
                break;
            }
        }
    }
}