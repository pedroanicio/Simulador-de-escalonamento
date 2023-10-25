/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabalhoso1;

/*
  @author pedro
 */


import javax.swing.*;

import java.time.LocalDate;
import java.time.ZoneId;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;

import org.jfree.ui.RefineryUtilities;

public class roundRobinChart {
    public static int numProcessos;
    public static void main(String[] args) {
        String nome;
        int timeQuantum;
        numProcessos = Integer.parseInt(JOptionPane.showInputDialog("Digite a quantidade de processos: "));

        // Cria uma lista de processos
        Queue<Process> queue = new LinkedList<>();
        for (int i = 0; i < numProcessos; i++) {
            nome = JOptionPane.showInputDialog("Digite o nome do " + (i + 1) + " processo: ");
            timeQuantum = Integer.parseInt(JOptionPane.showInputDialog("Digite o tempo do processo: "));
            queue.add(new Process(nome, timeQuantum));
        }

        // Configuração do quantum de tempo
        int quantum;
        quantum = Integer.parseInt(JOptionPane.showInputDialog("Digite o time quantum de tempo: "));

        // Executa a simulação
        runRoundRobinSimulation(queue, quantum);
    }

    // Classe para representar um processo
    static class Process extends Thread {
        private final String nome;
        private final int timeQuantum;
        private int remainingTime;
        private final int totalTime;

        public Process(String nome, int timeQuantum) {
            this.nome = nome;
            this.timeQuantum = timeQuantum;
            this.remainingTime = timeQuantum;
            this.totalTime = timeQuantum;
        }

        @Override
        public void run() {
            System.out.println(nome + " começou a execução.");
            for (int i = 0; i < timeQuantum; i++) {
                try {
                    Thread.sleep(1000);
                    remainingTime--;
                    System.out.println(nome + " executando. Tempo restante: " + (timeQuantum - i) + " segundos");
                } catch (InterruptedException e) {
                    System.out.println(nome + " foi interrompido.");
                    return;
                }
            }
            System.out.println(nome + " concluído.");
        }

        public int getRemainingTime() {
            return remainingTime;
        }

        public int getTotalTime() {
            return totalTime;
        }
    }

    // Executa a simulação do Round Robin e exibe resultados
    static void runRoundRobinSimulation(Queue<Process> queue, int quantum) {
        // Cria o gráfico
        JFreeChart chart = new JFreeChart("Gráfico de Gantt");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);


        int currentTime = 0;
        TaskSeries processSeries = chart.getProcessSeries();


        int trocaContexto = 0; // Variável para contar as trocas de contexto

        //calcular tempo de execução e espera
        double tempoTotalExecucao = 0.0;
        double tempoTotalEspera = 0.0;

        //loop que continuará até que a fila queue esteja vazia
        while (!queue.isEmpty()) {
            //retira o proximo processo da fila e o armazena na variável currentProcess
            Process currentProcess = queue.poll();
            currentProcess.start(); // Inicia a execução do processo atual


            // Aqui é adicionado o processo ao gráfico à medida que ele é executado
            try {
                currentProcess.join(quantum * 1000L + 1000); // Aguarda a conclusão do processo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentTime += currentProcess.timeQuantum;

            if (currentProcess.isAlive()) {
                currentProcess.interrupt(); // Interrompe o processo se o quantum expirar
                queue.add(new Process(currentProcess.nome, currentProcess.timeQuantum - quantum)); // Coloca de volta na fila para ser executado novamente
            }
            Date startTime = Date.from(LocalDate.of(2023, 10, 17).atStartOfDay(ZoneId.systemDefault()).toInstant().plusSeconds(currentTime - currentProcess.timeQuantum));
            Date endTime = Date.from(LocalDate.of(2023, 10, 17).atStartOfDay(ZoneId.systemDefault()).toInstant().plusSeconds(currentTime));

            Task task = new Task(currentProcess.nome, startTime, endTime);
            processSeries.add(task);
            double percentComplete = (double) (currentProcess.getTotalTime() - currentProcess.getRemainingTime()) / currentProcess.getTotalTime();
            task.setPercentComplete(percentComplete);

            trocaContexto++;

            double tempoExecucao = currentProcess.getTotalTime() - currentProcess.getRemainingTime();
            double tempoEspera = currentTime - tempoExecucao;
            tempoTotalExecucao += tempoExecucao;
            tempoTotalEspera += tempoEspera;
        }
        JOptionPane.showMessageDialog(null, "Trocas de contexto: " + trocaContexto);
        System.out.println("-------------------------");
        double tempoMedioExecucao = tempoTotalExecucao / numProcessos;
        double tempoMedioEspera = tempoTotalEspera / numProcessos;
        System.out.println("Tempo Médio de Execução: " + tempoMedioExecucao);
        System.out.println("Tempo Médio de Espera: " + tempoMedioEspera);
    }


}