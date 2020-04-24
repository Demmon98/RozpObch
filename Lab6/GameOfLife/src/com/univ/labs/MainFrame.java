package game;

import javax.swing.*;
import java.awt.*;

class MainFrame extends JFrame {
    private JPanel fieldPanel = null;
    private JButton	startBtn = null;
    private JButton	stopBtn	= null;
    private JLabel textLabel = null;
    private MainFrame self = this;
    private JScrollPane civilScrollPane = null;
    private int scrollPane = 1;


    MainFrame() {
        super("Игра жизни");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.NORTH);

        fieldPanel = new FieldPanel(40, 40, 15);
        add(fieldPanel);
        startBtn = new JButton("  Начать симуляцию  ");
        toolBar.add(startBtn);
        stopBtn = new JButton("  Остановить симуляцию  ");
        stopBtn.setEnabled(true);
        toolBar.add(stopBtn);
        textLabel = new JLabel("  Кол-во разных цивилизаций:  ");
        toolBar.add(textLabel);

        final DefaultComboBoxModel civilAmountModel = new DefaultComboBoxModel();

        civilAmountModel.addElement("1");
        civilAmountModel.addElement("2");
        civilAmountModel.addElement("3");
        civilAmountModel.addElement("4");

        final JComboBox civilCombo = new JComboBox(civilAmountModel);
        civilCombo.setSelectedIndex(0);
        civilScrollPane = new JScrollPane(civilCombo);
        toolBar.add(civilScrollPane);

        startBtn.addActionListener(e -> {
            if (civilCombo.getSelectedIndex() != -1) {
                scrollPane = Integer.parseInt((String)civilCombo.getItemAt(civilCombo.getSelectedIndex()));
                System.out.println(scrollPane);
            }
            ((FieldPanel)fieldPanel).startSimulation(scrollPane, 0.3f);
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
        });
		
        stopBtn.addActionListener(e -> {
            ((FieldPanel)fieldPanel).stopSimulation(startBtn);
            stopBtn.setEnabled(false);
        });
        pack();
        setVisible(true);
    }
}
