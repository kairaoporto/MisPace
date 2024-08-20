import javax.swing.*;

class StartPanel extends JFrame {

    public StartPanel() {

        setSize(550, 600);
        setTitle("MisPace");
        JFrame container = new JFrame("MisPace");

        container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container.setContentPane(new GameLogic());

        container.setUndecorated(false);
        container.setResizable(false);
        container.pack();
        container.setLocationRelativeTo(null);
        container.setVisible(true);

    }

    public static void main(String args[]) {
        new StartPanel();
    }


}