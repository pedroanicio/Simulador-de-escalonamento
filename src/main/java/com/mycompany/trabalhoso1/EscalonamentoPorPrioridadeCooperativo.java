package com.mycompany.trabalhoso1;


import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;

import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.PriorityQueue;

/**
 * @author Arthur
 */

public class EscalonamentoPorPrioridadeCooperativo {

    public static void main(String[] args) {
        int numProcessos = Integer.parseInt(JOptionPane.showInputDialog("Digite a quantidade de processos: "));
        PriorityQueue<Process> queue = new PriorityQueue<>();

        for (int i = 0; i < numProcessos; i++) {
            String nome = JOptionPane.showInputDialog("Digite o nome do " + (i + 1) + " processo: ");
            int tempo = Integer.parseInt(JOptionPane.showInputDialog("Digite o tempo de duração do processo: "));
            int prioridade = Integer.parseInt(JOptionPane.showInputDialog("Digite a prioridade do processo: "));
            queue.add(new Process(nome, tempo, prioridade));
        }

        // Aqui é executado o escalonamento por prioridade cooperativo (não preemptivo)
        runEscalonamentoPorPrioridadeCooperativo(queue);
    }

    static class Process implements Comparable<Process> {
        // Aqui temos uma classe que representa um processo com nome, tempo de execução e prioridade.
        // Aqui temos os atributos da classe Process
        private String nome; // Nome do processo
        private int tempo; // Tempo de execução do processo
        private int prioridade; // Prioridade do processo

        // Aqui temos o construtor da classe Process
        public Process(String nome, int tempo, int prioridade) {
            this.nome = nome;
            this.tempo = tempo;
            this.prioridade = prioridade;
        }
        // Aqui temos os getters da classe Process
        public String getNome() {
            return nome;
        }

        public int getTempo() {
            return tempo;
        }

        public int getPrioridade() {
            return prioridade;
        }

        // Aqui temos o método compareTo que é utilizado para comparar dois processos
        @Override
        public int compareTo(Process other) {

            // Aqui ele compara dois processos com base em suas prioridades.
            // Vai retornar um valor negativo se this é prioridade mais baixa que other.
            // Vai retornar zero se this e other têm a mesma prioridade.
            // Vai retornar um valor positivo se this é prioridade mais alta que other.

            return Integer.compare(this.prioridade, other.prioridade);
        }
    }

    static void runEscalonamentoPorPrioridadeCooperativo(PriorityQueue<Process> queue) {
        // Esta função realiza o escalonamento cooperativo dos processos com base em prioridades.
        // Ela recebe uma fila de prioridade 'queue' contendo objetos 'Process' a serem escalonados.

        // Variáveis para rastreamento de estatísticas
        int tempoTotal = 0;             // Aqui é feito o rastreio do tempo total de execução
        int tempoEsperaTotal = 0;       // Aqui é feito o rastreio do tempo total de espera
        int trocasContexto = 0;         // Aqui é feito o rastreio do número de trocas de contexto

        // Aqui é criado o gráfico de Gantt
        JFreeChart chart = new JFreeChart("Gantt Chart");
        chart.pack();
        chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);

        // Aqui é criada a série de processos para o gráfico de Gantt
        TaskSeries processSeries = chart.getProcessSeries();

        // Aqui temos variáveis de controle de tempo
        Process processoAnterior = null; // Aqui é feito o rastreio do processo anterior
        int currentTime = 0;            // Aqui é feito o rastreio do tempo atual

        // Aqui é iniciado o loop principal de escalonamento
        while (!queue.isEmpty()) {
            // Aqui é obtido o próximo processo a ser executado a partir da fila de prioridade
            Process currentProcess = queue.poll();

            JOptionPane.showMessageDialog(null, "Escalonando " + currentProcess.getNome() + " (Tempo: " + currentProcess.getTempo() + ")");

            int endTime = tempoTotal + currentProcess.getTempo();

            currentTime += currentProcess.getTempo();

            // Aqui é adicionado o processo ao gráfico à medida que ele é executado
            Task task = new Task(currentProcess.getNome(),
                    Date.from(LocalDate.of(2023, 10, 17).atStartOfDay(ZoneId.systemDefault()).toInstant().plusSeconds(currentTime - currentProcess.getTempo())),
                    Date.from(LocalDate.of(2023, 10, 17).atStartOfDay(ZoneId.systemDefault()).toInstant().plusSeconds(currentTime))
            );
            task.setPercentComplete(0);
            processSeries.add(task);

            // Aqui simulamos execução do processo
            tempoTotal = endTime;

            // Aqui calculamos o tempo de espera
            int tempoEspera = tempoTotal - currentProcess.getTempo();
            tempoEsperaTotal += tempoEspera;

            // Aqui fazemos a verificação se ocorreu troca de contexto
            if (processoAnterior != null && !processoAnterior.equals(currentProcess)) {
                trocasContexto++;
            }

            System.out.println("Tempo de execução total até o momento: " + tempoTotal);
            System.out.println("Tempo de espera para " + currentProcess.getNome() + ": " + tempoEspera);
            System.out.println("-------------------------");

            processoAnterior = currentProcess;
        }

        // Aqui exibimos resultados da simulação
        System.out.println("Resultados da simulação:");
        System.out.println("Tempo médio de execução: " + (trocasContexto > 0 ? (double) tempoTotal / trocasContexto : 0));
        System.out.println("Tempo médio de espera: " + (trocasContexto > 0 ? (double) tempoEsperaTotal / trocasContexto : 0));
        System.out.println("Trocas de contexto efetuadas: " + trocasContexto);

        // Aqui exibimos o número de trocas de contexto em um JOptionPane
        JOptionPane.showMessageDialog(null, "Trocas de contexto efetuadas: " + trocasContexto);
    }

}