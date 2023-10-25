package com.mycompany.trabalhoso1;


import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.ui.RefineryUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


class Processo {
    String nome;
    int tempoChegada;
    int tempoProcesso;
    int tempoRestante;

    public Processo(String nome, int tempoChegada, int tempoProcesso) {
        this.nome = nome;
        this.tempoChegada = tempoChegada;
        this.tempoProcesso = tempoProcesso;
        this.tempoRestante = tempoProcesso;
    }
}

public class ShortestRemainingTimeFirst extends ApplicationFrame {
    private TaskSeriesCollection dataset;
    private TaskSeries processSeries;
    private int currentTime = 0;

    public ShortestRemainingTimeFirst(String title) {
        super(title);
        dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryItemRenderer renderer = new GanttRenderer();
        plot.setRenderer(renderer);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    private TaskSeriesCollection createDataset() {
        TaskSeriesCollection dataset = new TaskSeriesCollection();
        processSeries = new TaskSeries("Processos");
        dataset.add(processSeries);
        return dataset;
    }

    private JFreeChart createChart(IntervalCategoryDataset dataset) {
        return ChartFactory.createGanttChart(
                "SRTF Algorithm Gantt Chart", "Processo", "Tempo", dataset, true, true, false
        );
    }

    public void addDataPoint(String processName, Date startTime, Date endTime) {
        Task task = new Task(processName, startTime, endTime);
        processSeries.add(task);
    }

    public void executeSRTF(List<Processo> processos) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleAtFixedRate(() -> {
            int shortestRemainingTime = Integer.MAX_VALUE;
            Processo shortestProcess = null;
            boolean allProcessesCompleted = true;

            for (Processo processo : processos) {
                if (processo.tempoChegada <= currentTime && processo.tempoRestante < shortestRemainingTime && processo.tempoRestante > 0) {
                    shortestRemainingTime = processo.tempoRestante;
                    shortestProcess = processo;
                }

                if (processo.tempoRestante > 0) {
                    allProcessesCompleted = false;
                }

                if (processo.tempoRestante > 0) {
                    Date startTime = Date.from(LocalDate.of(2023, 10, 17).atStartOfDay(ZoneId.systemDefault()).toInstant().plusSeconds(currentTime - processo.tempoRestante));
                    Date endTime = Date.from(LocalDate.of(2023, 10, 17).atStartOfDay(ZoneId.systemDefault()).toInstant().plusSeconds(currentTime));
                    addDataPoint(processo.nome, startTime, endTime);
                }
            }

            if (allProcessesCompleted) {
                executor.shutdown();
            }

            if (shortestProcess != null) {
                System.out.println(currentTime + "     |  " + shortestProcess.nome);
                shortestProcess.tempoRestante--;
                currentTime++;
            } else {
                System.out.println(currentTime + "     |  NULL");
                currentTime++;
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        int numProcessos, tempoChegada, tempoProcesso;
        String nome;

        numProcessos = Integer.parseInt(JOptionPane.showInputDialog("Digite a quantidade de processos: "));

        List<Processo> processos = new ArrayList<>();
        for (int i = 0; i < numProcessos; i++) {
            nome = JOptionPane.showInputDialog("Digite o nome do " + (i + 1) + " processo: ");
            tempoChegada = Integer.parseInt(JOptionPane.showInputDialog("Digite o tempo de chegada do processo: "));
            tempoProcesso = Integer.parseInt(JOptionPane.showInputDialog("Digite o tempo de duração do processo: "));
            processos.add(new Processo(nome, tempoChegada, tempoProcesso));
        }

        ShortestRemainingTimeFirst chart = new ShortestRemainingTimeFirst("SRTF Algorithm Gantt Chart");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);

        chart.executeSRTF(processos);
    }
}


