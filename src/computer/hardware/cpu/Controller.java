package computer.hardware.cpu;

import computer.hardware.memory.Memory;
import computer.TimeToRock;
import computer.hardware.panel.Panel;
import computer.software.Instructions;
import computer.software.Utils;

import java.util.*;

/**
 * Controller that controls the process of this machine.
 * Added an ARM7 like pipeline.
 *
 * @version v0.3.0
 */

public class Controller {
    private final Decoder decoder;
    private final Instructions instructions;
    Memory mem = TimeToRock.mem;
    Registers regs = TimeToRock.regs;
    List<LinkedList<Integer>> status;

    String[] prePC = {"000000000000", "000000000000", "000000000000"};
    List<HashMap<String, String>> predecodedInst = Arrays.asList(
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>()
    );
    HashMap<String, String> decodedInst = new HashMap<>();

    String lastPC = "000000000000";

    public Controller(){
        this.decoder = new Decoder();
        this.instructions =  new Instructions();

        this.resetStatus();
        this.showPipe();
    }

    // Reset the status of the pipeline.
    private void resetStatus(){
        this.status = Arrays.asList(
                new LinkedList<>(){{
                    add(1);
                    add(2);
                    add(3);
                }},
                new LinkedList<>(){{
                    add(0);
                    add(1);
                    add(2);
                }},
                new LinkedList<>(){{
                    add(0);
                    add(0);
                    add(1);
                }}
        );
    }

    // Show pipeline status
    private void showPipe(){
        StringBuilder pipSta = new StringBuilder();
        for(int i = 0; i < 3; i++){
            for (Integer st: this.status.get(i)
            ) {
                switch (st){
                    case 0->{
                        pipSta.append("St ");
                    }
                    case 1->{
                        pipSta.append("If ");
                    }
                    case 2->{
                        pipSta.append("De ");
                    }
                    case 3->{
                        pipSta.append("Ex ");
                    }
                }
            }
            pipSta.append("\n");
        }
//        Panel.pipelineStatus.setText("a\nb\nc");
        Panel.pipelineStatus.setText(String.valueOf(pipSta));
        System.out.println("Pipeline status:");
        System.out.println(pipSta);
    }

    /*
        Runs a single tik.
     */
    public boolean tik(){
        TimeToRock.steps++;
        if(TimeToRock.isHalt)
            return false;

        /*
            Pipeline.
            Fetch-> Decode->Execute->Fetch-> Decode->Execute...
                    Fetch-> Decode ->Execute
                            Fetch  ->Decode->Execute
            If PC is changed at executing stage, which means pre-fetched and pre-decoded
            instructions are not what we actually want, we would have to abandon them.
            Uses a 3-way queue array to describe the machine status.

            0 stall 1 fetch 2 decode 3 execute

            Clock cycle 0:
            queue[0]:[1][2][3]
            queue[1]:[0][1][2]
            queue[2]:[0][0][1]

            Clock cycle 1:
            queue[0]:[2][3][1]
            queue[1]:[1][2][3]
            queue[2]:[0][1][2]
                    .
                    .
                    .
         */

        this.showPipe();
        for(int i = 0; i < 3; i++){
            switch (this.status.get(i).poll()){
                case 0->{
                    TimeToRock.logger.info("Stall. Do nothing here.");
                }
                case 1->{
                    // Instruction Fetch
                    // MAR <- PC, PC++
                    regs.get("MAR").setValue(regs.get("PC").getValueString());
                    this.lastPC = regs.get("PC").getValueString();
                    regs.PCStepOne();
                    //MBR <- MEM[MAR], IR <- MBR
                    regs.get("MBR").setValue(mem.readMemoryWordString(Integer.parseInt(regs.get("MAR").getValueString(), 2)));
                    regs.get("PRE-IR").setValue(regs.get("MBR").getValueString());
                    this.prePC[i] = regs.get("MBR").getValueString();
//                TimeToRock.logger.info("Now IR->" + regs.get("IR").getValueString());
                }
                case 2->{
                    regs.get("IR").setValue(this.prePC[i]);
                    // Instruction Decode
                    predecodedInst.set(i,
                            (HashMap<String, String>) decoder.decode(regs.get("IR").getValueString()).clone());
                }
                case 3->{
                    decodedInst = (HashMap<String, String>) predecodedInst.get(i).clone();
                    // Execute
                    instructions.execute(decodedInst, regs, mem);
                    if (!(Utils.extend(Integer.toBinaryString(Integer.parseInt(this.lastPC, 2) + 1), 12).equals(regs.get("PC").getValueString()))){
                        // Some J type instructions have been executed.
                        this.resetStatus();
                    }
                }
            }
            switch(this.status.get(i).getLast()){
                case 1->{
                    this.status.get(i).add(2);
                }
                case 2->{
                    this.status.get(i).add(3);
                }
                case 3->{
                    this.status.get(i).add(1);
                }
            }
        }
/*
        // Without pipeline

        // Instruction Fetch
        // MAR <- PC, PC++
        regs.get("MAR").setValue(regs.get("PC").getValueString());
        regs.PCStepOne();
        //MBR <- MEM[MAR], IR <- MBR
        regs.get("MBR").setValue(mem.readMemoryWordString(Integer.parseInt(regs.get("MAR").getValueString(), 2)));
        regs.get("IR").setValue(regs.get("MBR").getValueString());
        TimeToRock.logger.info("Now IR->" + regs.get("IR").getValueString());

        // Instruction Decode
        if(Objects.equals(regs.get("IR").getValueString(), "0000000000000000")){
            TimeToRock.logger.info("Has no instruction to run anymore.");
            return false;
        }
        HashMap<String, String> decodedInst = decoder.decode(regs.get("IR").getValueString());

        // Execute
        instructions.execute(decodedInst, regs, mem);
        */
        // Result Store
        // Errr this was done during executing.

        //Update Panel
        TimeToRock.gui.updateRegsValueOnPanel();

        // Next Instruction
        return true; // Still has instructions to run.
    }

    /*
        Keeps running until HLT or finish all the instructions.
     */
    public void run() throws InterruptedException {
        while(tik()){
            System.out.println("Inst " + TimeToRock.steps + " executed.");
            TimeToRock.logger.info("Inst " + TimeToRock.steps + " executed.");
//            if(TimeToRock.isHalt){
//                break;
//            }
//            TimeUnit.SECONDS.sleep(1);
        }
    }
}
