//Section: CAR
//Shaima Asiri - 1805932
//Anoud Al-harthy - 1806132
//Jood Alghamdi - 1905554
package Algorithms_Simulator;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author iisha
 */
public class Algorithms_Simulator extends javax.swing.JFrame {

//Global variables
    int NoProcesses;
    int[] BurstTime;
    float[] ArrivalTime;
    float[] WaitingTime;
    float[] Prioritys;
    float[] TurnaroundTime;
    int[] BurstTimeCopy;
    float[] TerminationTime;
    float[] Response;
    float[] ArrivalTimeCopy;
    float[] PriorityCopy;
    int[] ProcessesOrder; //Save Processes OrderOfProcesses, Used on FCFS, SJF and Prioritys algorithms
    String order = " ";
    String process;

    public Algorithms_Simulator() throws Exception {
        initComponents();
        Getdata();
    }

    // Getting All the informations of processes from text file, 
    // Then store it in proper arrays
    public void Getdata() throws Exception {

        // Read Information from text file
        File InfoFile = new File("ProcessesInformation.txt");
        Scanner Scan = new Scanner(InfoFile);
        NoProcesses = Scan.nextInt();

        // Set arrays size based on number of processes in the file
        // NOTE: ADD 1 to the NoProcesses, Because arrays indexes start with 0
        ArrivalTime = new float[NoProcesses + 1];
        BurstTime = new int[NoProcesses + 1];
        Prioritys = new float[NoProcesses + 1];
        WaitingTime = new float[NoProcesses + 1];

        // fill all the arrays with processes information
        for (int i = 1; i <= NoProcesses; i++) {
            ArrivalTime[i] = (float) Scan.nextFloat();
            BurstTime[i] = (int) Scan.nextInt();
            Prioritys[i] = (float) Scan.nextFloat();
        }

    }
    // *** START OF REQUIRED CALCULATIONS METHODS ***

    public void CalculateWaitingTime() {

        WaitingTime[1] = 0; // FIRST Process will execute immediately, Therefore --> waiting time = ZERO
        for (int i = 2; i <= NoProcesses; i++) {
            // If previous process ends before current process arrive,
            // Then current process will not wait -----> waiting time = ZERO
            if ((BurstTimeCopy[i - 1] + WaitingTime[i - 1] + ArrivalTimeCopy[i - 1]) <= ArrivalTimeCopy[i]
                    || (BurstTimeCopy[i - 1] + WaitingTime[i - 1]) == 0) {
                WaitingTime[i] = 0;
            } else // Otherwise, calculate waiting time
            {
                WaitingTime[i] = (BurstTimeCopy[i - 1] + WaitingTime[i - 1] 
                        + ArrivalTimeCopy[i - 1]) - ArrivalTimeCopy[i];
            }
        }

    }

    public void CalculateTurnaroundTime() {

        TurnaroundTime = new float[NoProcesses + 1];
        TurnaroundTime[1] = BurstTimeCopy[1]; //P1 will execute first once its arrive to ready queue,
        //its turnaroundTime=its burstTime
        for (int i = 2; i <= NoProcesses; i++) {
            TurnaroundTime[i] = WaitingTime[i] + BurstTimeCopy[i];
        }
    }

    public void CalculateTerminationTime() {

        TerminationTime = new float[NoProcesses + 1];
        TerminationTime[1] = ArrivalTimeCopy[1] + BurstTimeCopy[1];
        for (int i = 2; i <= NoProcesses; i++) {
            // If The process arraives at 0, then its Termination time = its turnaround time
            if (ArrivalTimeCopy[i] == 0) {
                TerminationTime[i] = TurnaroundTime[i];
            } else {
                // Otherwise, combine arrival time + waiting time + burst time 
                // To get the time that the process will end in
                TerminationTime[i] = ArrivalTimeCopy[i] + WaitingTime[i] + BurstTimeCopy[i];
            }

        }

    }

    public void CalculateResponse() {

        Response = new float[NoProcesses + 1];
        Response[1] = ArrivalTimeCopy[1];//Since that P1 will start execute immediately 
                                         // once its arrive to ready queue
                                         // its Response time = its arrival time
        for (int i = 2; i <= NoProcesses; i++) {
            // If current process arrives IN or AFTER the previous process terminate
            // Then once its arrive will start execute, its Response time = its arrival time, 
            if (ArrivalTimeCopy[i] >= TerminationTime[i - 1]) {
                Response[i] = ArrivalTimeCopy[i];
            } else {
                // else if current process arrives before previous process terminate
                // Then current process will get FIRST response once previous process terminate
                Response[i] = TerminationTime[i - 1];
            }
        }

    }

        // ** END OF REQUIRED CALCULATIONS METHODS ** 

    // DRAWING GANTT CHART METHOD    
    public String CreateGanttChart() {

        //String to Save printing gantt chart
        String GanttChart = " ";

        // Loop to print upper bar
        for (int i = 1; i <= NoProcesses; i++) {
            for (int j = 0; j < BurstTimeCopy[i]; j++) {
                GanttChart += "__";
            }
            GanttChart += " ";
        }
        GanttChart += "\n|";

        // Loop to print processes in the middle 
        for (int i = 1; i <= NoProcesses; i++) {
            for (int j = 0; j < BurstTimeCopy[i] - 1; j++) {
                GanttChart += " ";
            }
            GanttChart += "P" + ProcessesOrder[i];
            for (int j = 0; j < BurstTimeCopy[i] - 1; j++) {
                GanttChart += " ";
            }
            GanttChart += "|";
        }
        GanttChart += "\n ";

        // Loop to print bottom bar
        for (int i = 1; i <= NoProcesses; i++) {
            for (int j = 0; j < BurstTimeCopy[i]; j++) {
                GanttChart += "¯¯";
            }
            GanttChart += " ";
        }
        GanttChart += "\n";

        // Loop to print showing time under the chart (beginning and termination time)
        GanttChart += "0";
        for (int i = 1; i <= NoProcesses; i++) {
            for (int j = 0; j < BurstTimeCopy[i]; j++) {
                GanttChart += "  ";
            }
            if (TurnaroundTime[i] > 9) {
                GanttChart = GanttChart.substring(0, GanttChart.length() - 1);
            }
            GanttChart += (int) TurnaroundTime[i] + "";

        }
        GanttChart += "\n";
        return GanttChart;

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        FirstComeFirstServe = new javax.swing.JButton();
        Priority = new javax.swing.JButton();
        ShortestJobFirst = new javax.swing.JButton();
        Exit = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Project");

        jPanel1.setBackground(new java.awt.Color(244, 249, 254));
        jPanel1.setName(""); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(253, 211, 117));
        jLabel1.setText("CPU Scheduling");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 20, 290, 64));

        FirstComeFirstServe.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.disabledShadow"));
        FirstComeFirstServe.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        FirstComeFirstServe.setForeground(new java.awt.Color(0, 153, 204));
        FirstComeFirstServe.setText("FCFS");
        FirstComeFirstServe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FirstComeFirstServeActionPerformed(evt);
            }
        });
        jPanel1.add(FirstComeFirstServe, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 140, 120, 40));

        Priority.setBackground(new java.awt.Color(255, 255, 255));
        Priority.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        Priority.setForeground(new java.awt.Color(0, 153, 204));
        Priority.setText("Priority");
        Priority.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PriorityActionPerformed(evt);
            }
        });
        jPanel1.add(Priority, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 200, 120, 40));

        ShortestJobFirst.setBackground(new java.awt.Color(255, 255, 255));
        ShortestJobFirst.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        ShortestJobFirst.setForeground(new java.awt.Color(0, 153, 204));
        ShortestJobFirst.setText("SJF");
        ShortestJobFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShortestJobFirstActionPerformed(evt);
            }
        });
        jPanel1.add(ShortestJobFirst, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 260, 120, 40));

        Exit.setBackground(new java.awt.Color(255, 255, 255));
        Exit.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        Exit.setForeground(new java.awt.Color(0, 153, 204));
        Exit.setText("Exit");
        Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitActionPerformed(evt);
            }
        });
        jPanel1.add(Exit, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 320, 120, 40));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Details/CPU.png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 300, 310));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void FirstComeFirstServeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FirstComeFirstServeActionPerformed
        int i, j, temp;
        float tempF;
        BurstTimeCopy = new int[NoProcesses + 1]; 
        ArrivalTimeCopy = new float[NoProcesses + 1];
        ProcessesOrder = new int[NoProcesses + 1]; //Save Processes OrderOfProcesses

        // Copy burst times for all processes from global burstTime array to Local bursttimeCopy array 
        // to modify it without affecting the original values in burstTime array
        for (i = 1; i <= NoProcesses; i++) {
            BurstTimeCopy[i] = BurstTime[i];
            ArrivalTimeCopy[i] = ArrivalTime[i];
            ProcessesOrder[i] = i;
        }

        // Sort the processes based on its Arrival time "First come First Served"
        for (i = NoProcesses; i >= 1; i--) {
            for (j = 1; j <= NoProcesses; j++) {

                if (ArrivalTimeCopy[j - 1] > ArrivalTimeCopy[j]) {

                    // Swap Arrival Time 
                    tempF = ArrivalTimeCopy[j - 1];
                    ArrivalTimeCopy[j - 1] = ArrivalTimeCopy[j];
                    ArrivalTimeCopy[j] = tempF;

                    // Swap Burst Time
                    temp = BurstTimeCopy[j - 1];
                    BurstTimeCopy[j - 1] = BurstTimeCopy[j];
                    BurstTimeCopy[j] = temp;

                    // Swap order of the process
                    temp = ProcessesOrder[j - 1];
                    ProcessesOrder[j - 1] = ProcessesOrder[j];
                    ProcessesOrder[j] = temp;

                }
            }
        }

        // Calculate Waiting time for each process
        CalculateWaitingTime();
        // Calculate Turnaround time for each process
        CalculateTurnaroundTime();

        //Calculating Termination Time for each process
        CalculateTerminationTime();

        //Calculating Response Time for each process
        CalculateResponse();

        // -----------------------------
        //Printing the results in GUI
        Result FCFS = new Result(1);
        FCFS.setVisible(true);

        DefaultTableModel model = (DefaultTableModel) FCFS.DetailTable().getModel();//create table
        //show the content in a table on the screen  
        for (i = 1; i <= NoProcesses; i++) {
            Object Data[] = new Object[]{
                process = "P" + ProcessesOrder[i],
                WaitingTime[i], TurnaroundTime[i], Response[i], TurnaroundTime[i]
            };

            model.addRow(Data);

        }
        // save processes order in string to print it on its field
        order = "";
        for (i = 1; i <= NoProcesses; i++) {
            order += " P" + ProcessesOrder[i];
        }

        //print processes order in textfield on the screen
        FCFS.OrderOfProcesses().setText(order);
        // creat gantt chart of processes
        String Chart = CreateGanttChart();
        //print gantt chart of processes in its textarea on the screen
        FCFS.GanttChart().setText(Chart);


    }//GEN-LAST:event_FirstComeFirstServeActionPerformed

    private void PriorityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PriorityActionPerformed
        int i, j, temp;
        float tempF;
        BurstTimeCopy = new int[NoProcesses + 1];
        ArrivalTimeCopy = new float[NoProcesses + 1];
        ProcessesOrder = new int[NoProcesses + 1]; //Save Processes OrderOfProcesses
        PriorityCopy = new float[NoProcesses + 1];

        // Copy burst, Priority and arrival times for all processes from global arrays to local arrays (Copy)
        // to modify it without affecting the original arrays values 
        for (i = 1; i <= NoProcesses; i++) {
            BurstTimeCopy[i] = BurstTime[i];
            ArrivalTimeCopy[i] = ArrivalTime[i];
            PriorityCopy[i] = Prioritys[i];
            ProcessesOrder[i] = i;
        }

        // Sort Based on the priority, smaller values means higher priority
        for (i = NoProcesses; i >= 1; i--) {
            for (j = 1; j <= NoProcesses; j++) {
                if (PriorityCopy[j - 1] > PriorityCopy[j]) {

                    // Swap Priority
                    tempF = PriorityCopy[j - 1];
                    PriorityCopy[j - 1] = PriorityCopy[j];
                    PriorityCopy[j] = tempF;

                    // Swap Burst Time
                    temp = BurstTimeCopy[j - 1];
                    BurstTimeCopy[j - 1] = BurstTimeCopy[j];
                    BurstTimeCopy[j] = temp;

                    // Swap Arrival Time 
                    tempF = ArrivalTimeCopy[j - 1];
                    ArrivalTimeCopy[j - 1] = ArrivalTimeCopy[j];
                    ArrivalTimeCopy[j] = tempF;

                    // Swap order of the process
                    temp = ProcessesOrder[j - 1];
                    ProcessesOrder[j - 1] = ProcessesOrder[j];
                    ProcessesOrder[j] = temp;
                } // IF the priority is the same, compare the processes with their Arrival time (FCFS)
                else if (PriorityCopy[j - 1] == PriorityCopy[j]) {
                    if (ArrivalTimeCopy[j - 1] > ArrivalTimeCopy[j]) {

                        // Swap Priority
                        tempF = PriorityCopy[j - 1];
                        PriorityCopy[j - 1] = PriorityCopy[j];
                        PriorityCopy[j] = tempF;

                        // Swap Burst Time
                        temp = BurstTimeCopy[j - 1];
                        BurstTimeCopy[j - 1] = BurstTimeCopy[j];
                        BurstTimeCopy[j] = temp;

                        // Swap Arrival Time 
                        tempF = ArrivalTimeCopy[j - 1];
                        ArrivalTimeCopy[j - 1] = ArrivalTimeCopy[j];
                        ArrivalTimeCopy[j] = tempF;

                        // Swap order of the process
                        temp = ProcessesOrder[j - 1];
                        ProcessesOrder[j - 1] = ProcessesOrder[j];
                        ProcessesOrder[j] = temp;

                    }

                }
            }
        }

        // Calculate Waiting time for each process
        CalculateWaitingTime();

        // Calculate Turnaround time for each process
        CalculateTurnaroundTime();

        //Calculating Termination Time for each process
        CalculateTerminationTime();

        //Calculating Response Time for each process
        CalculateResponse();

        // -----------------------------
        //Printing the results in GUI
        Result P = new Result(1);
        P.setVisible(true);

        DefaultTableModel model = (DefaultTableModel) P.DetailTable().getModel();//create table
        //show the content in a table on the screen  
       
        for (i = 1; i <= NoProcesses; i++) {
            Object rowData[] = new Object[]{
                process = "P" + ProcessesOrder[i],
                WaitingTime[i], TurnaroundTime[i], Response[i], TurnaroundTime[i]
            };

            model.addRow(rowData);

        }
        // save processes order in string to print it on its field
        order = "";
        for (i = 1; i <= NoProcesses; i++) {
            order += " P" + ProcessesOrder[i];
        }

        //print processes order in textfield on the screen
        P.OrderOfProcesses().setText(order);
        // creat gantt chart of processes
        String Chart = CreateGanttChart();
        //print gantt chart of processes in its textarea on the screen
        P.GanttChart().setText(Chart);


    }//GEN-LAST:event_PriorityActionPerformed

    private void ShortestJobFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShortestJobFirstActionPerformed
        int i, j, temp;
        float tempF;
        BurstTimeCopy = new int[NoProcesses + 1];
        ArrivalTimeCopy = new float[NoProcesses + 1];
        ProcessesOrder = new int[NoProcesses + 1]; //Save Processes OrderOfProcesses

        // Copy burst and arrival times for all processes from global burstTime and ArrivalTime array 
        // to Local bursttimeCopy and ArrivalTimeCopy array 
        // to modify it without affecting the original arrays values 
        for (i = 1; i <= NoProcesses; i++) {
            BurstTimeCopy[i] = BurstTime[i];
            ArrivalTimeCopy[i] = ArrivalTime[i];
            ProcessesOrder[i] = i;
        }

        // Sort the processes based on its Burst time "Shortest executes first"
        for (i = NoProcesses; i >= 1; i--) {
            for (j = 1; j <= NoProcesses; j++) {

                if (BurstTimeCopy[j - 1] > BurstTimeCopy[j]) {

                    // Swap Burst Time
                    temp = BurstTimeCopy[j - 1];
                    BurstTimeCopy[j - 1] = BurstTimeCopy[j];
                    BurstTimeCopy[j] = temp;

                    // Swap Arrival Time 
                    tempF = ArrivalTimeCopy[j - 1];
                    ArrivalTimeCopy[j - 1] = ArrivalTimeCopy[j];
                    ArrivalTimeCopy[j] = tempF;

                    // Swap order of the process
                    temp = ProcessesOrder[j - 1];
                    ProcessesOrder[j - 1] = ProcessesOrder[j];
                    ProcessesOrder[j] = temp;

                }
            }
        }

        // Calculate Waiting time for processes
        CalculateWaitingTime();

        // Calculate Turnaround time
        CalculateTurnaroundTime();

        //Calculating Termination Time
        CalculateTerminationTime();

        //Calculating Response Time
        CalculateResponse();
        
        // -----------------------------
        //Printing the results in GUI
        Result SJF = new Result(1);
        Result S = new Result(1);
        SJF.setVisible(true);

        DefaultTableModel model = (DefaultTableModel) SJF.DetailTable().getModel();//create table
        //show the content in a table on the screen  
        for (i = 1; i <= NoProcesses; i++) {
            Object rowData[] = new Object[]{
               process = "P" + ProcessesOrder[i],
                WaitingTime[i], TurnaroundTime[i], Response[i], TurnaroundTime[i]
            };

            model.addRow(rowData);

        }
        // save processes order in string to print it on its field
        order = "";
        for (i = 1; i <= NoProcesses; i++) {
            order += " P" + ProcessesOrder[i];
        }

        //print processes order in textfield on the screen
        SJF.OrderOfProcesses().setText(order);
        // creat gantt chart of processes
        String Chart = CreateGanttChart();
        //print gantt chart of processes in its textarea on the screen
        SJF.GanttChart().setText(Chart);
    }//GEN-LAST:event_ShortestJobFirstActionPerformed

    private void ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_ExitActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Algorithms_Simulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Algorithms_Simulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Algorithms_Simulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Algorithms_Simulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Algorithms_Simulator().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(Algorithms_Simulator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Exit;
    private javax.swing.JButton FirstComeFirstServe;
    private javax.swing.JButton Priority;
    private javax.swing.JButton ShortestJobFirst;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
