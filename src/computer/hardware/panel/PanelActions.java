package computer.hardware.panel;

import computer.TimeToRock;
import computer.software.File2Mem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * The actions of the panel.
 *
 * @version v0.2.0
 */

public class PanelActions {
    private Logger logger = TimeToRock.logger;
    public void addButtonActions(
            HashMap<String, JButton> allButtons,
            JScrollPane console,
            JTextArea consoleOutput,
            HashMap<String, JTextField> allRegisterValuesOnPanel
    ){
        /*
            A test button. It is invisible by default.
            Use this button to test some code.
         */
        allButtons.get("test").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked test");
            }
        });
        /*
            Write all the registers' value on the panel to the real register.
         */
        allButtons.get("writeRegs").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(String name: allRegisterValuesOnPanel.keySet()){
                    String value = allRegisterValuesOnPanel.get(name).getText();
                    String oldValue = TimeToRock.regs.get(name).getValueString();
                    if (!value.equals(oldValue)){
                        TimeToRock.regs.get(name).setValue(value);
                        System.out.println("Update register " + name + " value from " + oldValue + " to " + value + ".");
                    }
                }
            }
        });
        /*
            Restart the machine. Load IPL.txt to the memory and reset all the registers.
         */
        allButtons.get("IPL").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Restarting system...");
                TimeToRock.isHalt = false;
                TimeToRock.mem.reset();
                TimeToRock.regs.reset();
                TimeToRock.gui.updateRegsValueOnPanel();
                try {
                    File2Mem.readAndSave("data/IPL.txt");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                System.out.println("IPL finished.");
                logger.info("IPL finished.");
            }
        });
        /*
            Load memory.txt and put it in the memory.
         */
        allButtons.get("loadMem").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Start loading memory...");
                TimeToRock.isHalt = false;
                try {
                    File2Mem.readAndSave("data/memory.txt");
                } catch (Exception ex) {
//                    ex.printStackTrace();
                    System.out.println("Fail to load data/memory.txt !");
                    logger.warning("Fail to load data/memory.txt !");
                }
                System.out.println("Load memory finished.");
            }
        });
        /*
            Run single step.
         */
        allButtons.get("SS").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Single step...");
                TimeToRock.controller.tik();
            }
        });
        /*
            Keep running till the end of the program.
         */
        allButtons.get("Run").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Run...");
                    try {
                        TimeToRock.controller.run();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
        });
        /*
            Clear the console output.
         */
        allButtons.get("Clear").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consoleOutput.setText("");
            }
        });
        /*
            Ready to run program 1. Gather those inputs.
         */
        allButtons.get("Program").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*System.out.println("Starting Program 1...");
                TimeToRock.isHalt = false;
                try {
                    File2Mem.readAndSave("data/program1.txt");
                } catch (Exception ex) {
//                    ex.printStackTrace();
                    System.out.println("Fail to load data/program1.txt !");
                    logger.warning("Fail to load data/program1.txt !");
                }
                System.out.println("Load program 1 finished.");*/
                try{
                    String [] options = {"Program 1","Program 2"};
                    String choice =  (String)JOptionPane.showInputDialog(
                            null,
                            "Which Program do you want to run?",
                            "Program Choice",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );
                    switch(choice){
                        case "Program 1":
                            System.out.println("Starting Program 1...");
                            TimeToRock.isHalt = false;
                            try {
                                File2Mem.readAndSave("data/program1.txt");
                            } catch (Exception ex) {
//                    ex.printStackTrace();
                                System.out.println("Fail to load data/program1.txt !");
                                logger.warning("Fail to load data/program1.txt !");
                            }
                            System.out.println("Load program 1 finished.");
                            break;
                        case "Program 2":
                            System.out.println("Starting Program 2...");
                            TimeToRock.isHalt = false;
                            try {
                                File2Mem.readAndSave("data/program2.txt");
                            } catch (Exception ex) {
//                    ex.printStackTrace();
                                System.out.println("Fail to load data/program2.txt !");
                                logger.warning("Fail to load data/program2.txt !");
                            }
                            System.out.println("Load program 2 finished.");
                            break;
                    }
                }
                catch(Exception a){//e is already in use
                    System.out.println("Operation canceled");
                }
            }
        });
        /*
            Write something into memory or show some memory on the console.
         */
        allButtons.get("showMem").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //How to use JOptionPane
                //https://cloud.tencent.com/developer/article/1702763
                try{
                    String [] options = {"Write a memory","Show some memory"};
                    String choice =  (String)JOptionPane.showInputDialog(
                            null,
                            "请输入你的选项：",
                            "提示",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );
                    switch(choice){
                        case "Write a memory":
                            System.out.println("Write Memory");
                            int address = Integer.parseInt(JOptionPane.showInputDialog(
                                    null,
                                    "Where do you want to store(0~2048):",
                                    "Memory address",
                                    JOptionPane.PLAIN_MESSAGE
                            ));
                            String value = JOptionPane.showInputDialog(
                                    null,
                                    "What do you want to store(16bits binary string):",
                                    "Memory value",
                                    JOptionPane.PLAIN_MESSAGE
                            );
                            TimeToRock.mem.writeMemory(address, value);
                            System.out.println("Memory: " + value + " written to " + address);
                            break;
                        case "Show some memory":
                            System.out.println("Show Memory");
                            int offset = Integer.parseInt(JOptionPane.showInputDialog(
                                    null,
                                    "Where do you want to start(0~2048):",
                                    "Memory range",
                                    JOptionPane.PLAIN_MESSAGE
                            ));
                            int num = Integer.parseInt(JOptionPane.showInputDialog(
                                    null,
                                    "How much memory you want to see:",
                                    "Memory range",
                                    JOptionPane.PLAIN_MESSAGE
                            ));
                            StringBuilder memInfo = new StringBuilder();
                            for(int i = offset; i < offset + num; i++){
                                memInfo.append("\n");
                                memInfo.append(i);
                                memInfo.append("->");
                                memInfo.append(TimeToRock.mem.readMemoryWordStringWithoutCache(i));
                            }
                            System.out.println(memInfo);
                            System.out.println("\nEnd showing memory");
                            break;
                    }
                }
                catch(Exception a){//e is already in use
                    System.out.println("Operation canceled");
                }
            }
        });
        /*
            Scroll the cursor of the console output to the bottom.
         */
        allButtons.get("toBottom").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JScrollBar vertical = console.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            }
        });
    }
}
