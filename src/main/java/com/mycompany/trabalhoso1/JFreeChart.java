package com.mycompany.trabalhoso1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import javax.swing.*;

public class JFreeChart extends JFrame {
    private TaskSeries processSeries;

    //Aqui implementamos o gráfico, como suas dimensões
    public JFreeChart(String applicationTitle) {
        super(applicationTitle);

        IntervalCategoryDataset dataset = createDataset();
        org.jfree.chart.JFreeChart chart = createChart(dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 400));

        setContentPane(chartPanel);
    }

    public TaskSeries getProcessSeries() {
        return processSeries; // Retorna a série de processos
    }

    private IntervalCategoryDataset createDataset() {
        TaskSeriesCollection dataset = new TaskSeriesCollection();
        dataset.add(processSeries = new TaskSeries("Processos")); // Adiciona a série de processos ao gráfico
        return dataset;
    }

    //Aqui definimos a estrutura do gráfico, como título, processos, tempo
    private org.jfree.chart.JFreeChart createChart(IntervalCategoryDataset dataset) {
        org.jfree.chart.JFreeChart chart = ChartFactory.createGanttChart(
                "Gráfico de Gantt",
                "Processos",
                "Tempo",
                dataset,
                true,
                true,
                false
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setOrientation(PlotOrientation.HORIZONTAL);

        return chart; // Retorna o gráfico
    }
}