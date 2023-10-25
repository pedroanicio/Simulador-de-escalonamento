/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabalhoso1;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


import java.util.PriorityQueue;
import javax.swing.*;


import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;

import org.jfree.ui.RefineryUtilities;

/**
 * @author pedro
 */
public class ShortestJobFirst {

    private static JFreeChart chart;
    public static int numProcessos;

    public static void main(String[] args) {
        int tempo;
        String nome;

        //fila de prioridade para organizar os processos em ordem com base na sua duração.
        PriorityQueue<Process> queue = new PriorityQueue<>();
        int trocasContexto = 0; // Contador de trocas de contexto


        // Crie uma lista de processos com durações diferentes
        numProcessos = Integer.parseInt(JOptionPane.showInputDialog("Digite a quantidade de processos: "));


        for (int i = 0; i < numProcessos; i++) {
            nome = JOptionPane.showInputDialog("Digite o nome do " + (i + 1) + " processo: ");
            tempo = Integer.parseInt(JOptionPane.showInputDialog("Digite o tempo de duração do processo: "));
            queue.add(new Process(nome, tempo));
        }

        //inicia o escalonamento
        runShortestJobFirst(queue, trocasContexto);

    }

    static void runShortestJobFirst(PriorityQueue<Process> queue, int trocasContexto) {
        JFreeChart chart = new JFreeChart("Gráfico de Gantt");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);

        int currentTime = 0;
        TaskSeries processSeries = chart.getProcessSeries();

        double tempoTotalExecucao = 0.0;
        double tempoTotalEspera = 0.0;

        while (!queue.isEmpty()) {
            Process currentProcess = queue.poll();

            //JOptionPane.showMessageDialog(null, "Executando " + currentProcess.getNome() + " (Duração: " + currentProcess.getTempo() + ")");
            System.out.println("Executando " + currentProcess.getNome() + " (Duração: " + currentProcess.getTempo() + ")");

            //aguarda o tempo necessário para conclusão do processo
            try {
                int segundosParaEsperar = currentProcess.getTempo(); // Tempo de espera em segundos
                Thread.sleep(segundosParaEsperar * 1000); // O argumento é em milissegundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            currentTime += currentProcess.getTempo();
            System.out.println("Tempo de execução total até agora: " + currentTime);

            trocasContexto++;

            // Adicione o processo ao gráfico à medida que ele é executado
            // Obtém a data atual e adiciona o tempo de execução do processo
            Task task = new Task(currentProcess.getNome(),
                    Date.from(LocalDate.of(2023, 10, 17).atStartOfDay(ZoneId.systemDefault()).toInstant().plusSeconds(currentTime - currentProcess.getTempo())),
                    Date.from(LocalDate.of(2023, 10, 17).atStartOfDay(ZoneId.systemDefault()).toInstant().plusSeconds(currentTime))
            );
            task.setPercentComplete(0);
            processSeries.add(task);

            double tempoExecucao = currentProcess.getTempo();
            double tempoEspera = currentTime - tempoExecucao;
            tempoTotalExecucao += tempoExecucao;
            tempoTotalEspera += tempoEspera;
        }

        JOptionPane.showMessageDialog(null, "Trocas de contexto: " + trocasContexto);
        System.out.println("Trocas de contexto: " + trocasContexto);

        System.out.println("-------------------------");

        double tempoMedioExecucao = tempoTotalExecucao / numProcessos;
        double tempoMedioEspera = tempoTotalEspera / numProcessos;
        System.out.println("Tempo Médio de Execução: " + tempoMedioExecucao);
        System.out.println("Tempo Médio de Espera: " + tempoMedioEspera);


        if (!chart.isEnabled()) {
            System.exit(0);
        }
    }

    static class Process implements Comparable<Process> {

        private String nome;
        private int tempo;

        public Process(String nome, int tempo) {
            this.nome = nome;
            this.tempo = tempo;
        }

        public String getNome() {
            return nome;
        }

        public int getTempo() {
            return tempo;
        }

        @Override
        public int compareTo(Process other) {
            return Integer.compare(this.tempo, other.tempo);
        }
    }


}
