/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabalhoso1;

/**
 * @author pedro
 */


import javax.swing.*;

import java.util.LinkedList;
import java.util.Queue;

public class RoundRobin {
    public static void main(String[] args) {
        String nome;
        int numProcessos, timeQuantum;
        numProcessos = Integer.parseInt(JOptionPane.showInputDialog("Digite a quantidade de processos: "));

        // Cria uma lista de processos
        Queue<Process> queue = new LinkedList<>();
        for (int i = 0; i < numProcessos; i++) {
            nome = JOptionPane.showInputDialog("Digite o nome do " + (i + 1) + " processo: ");
            timeQuantum = Integer.parseInt(JOptionPane.showInputDialog("Digite o time quantum do processo: "));
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
        private String name;
        private int timeQuantum;

        public Process(String name, int timeQuantum) {
            this.name = name;
            this.timeQuantum = timeQuantum;
        }

        @Override
        public void run() {
            System.out.println(name + " começou a execução.");
            for (int i = 0; i < timeQuantum; i++) {
                try {
                    Thread.sleep(1000);
                    System.out.println(name + " executando. Tempo restante: " + (timeQuantum - i) + " segundos");
                } catch (InterruptedException e) {
                    System.out.println(name + " foi interrompido.");
                    return;
                }
            }
            System.out.println(name + " concluído.");
        }
    }

    // Executa a simulação do Round Robin e exibe resultados
    static void runRoundRobinSimulation(Queue<Process> queue, int quantum) {
        int trocaContexto = 0; // Variável para contar as trocas de contexto

        //loop que continuará até que a fila queue esteja vazia
        while (!queue.isEmpty()) {
            //retira o proximo processo da fila e o armazena na variável currentProcess
            Process currentProcess = queue.poll();
            currentProcess.start(); // Inicia a execução do processo atual

            // Aqui é adicionado o processo ao gráfico à medida que ele é executado
            try {
                currentProcess.join(quantum * 1000 + 1000); // Aguarda a conclusão do processo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (currentProcess.isAlive()) {
                currentProcess.interrupt(); // Interrompe o processo se o quantum expirar
                queue.add(new Process(currentProcess.name, currentProcess.timeQuantum - quantum)); // Coloca de volta na fila para ser executado novamente
            }
            trocaContexto++;
        }
        JOptionPane.showMessageDialog(null, "Trocas de contexto: " + trocaContexto);
    }

}