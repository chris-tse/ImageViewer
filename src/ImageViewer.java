import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import static java.awt.event.KeyEvent.VK_ESCAPE;

public class ImageViewer extends JFrame {
    private static JPanel contentPane;
    private static DefaultListModel<String> model = new DefaultListModel<>();
    private static JList<String> list;
    private static JLabel mainLabel;
    private static ImageIcon imgicon;
    private static File[] imgs;
    private static int currentImage = 0;
    private static String dir;
    private static String res;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            ImageViewer frame = new ImageViewer();
            frame.setVisible(true);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the frame.
     */
    public ImageViewer() {



        // Do setup stuff
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(200, 200, 1000, 400);

        // Set up menu
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // File menu and items
        JMenu filemenu = new JMenu("File");
        menuBar.add(filemenu);
        JMenuItem quitItem = new JMenuItem("Quit");
        JMenuItem openItem = new JMenuItem("Open...");
        filemenu.add(openItem);
        filemenu.addSeparator();
        filemenu.add(quitItem);
        openItem.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        quitItem.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        OpenerListener ol = new OpenerListener();
        openItem.addActionListener(ol);

        // Navigate Menu
        JMenu navMenu = new JMenu("Navigate");
        menuBar.add(navMenu);
        JMenuItem nextItem = new JMenuItem("Next Image");
        JMenuItem prevItem = new JMenuItem("Previous Image");
        navMenu.add(nextItem);
        navMenu.add(prevItem);
        nextItem.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        prevItem.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

        nextItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imgUp();
            }
        });

        prevItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imgDown();
            }
        });

        // Action Maps for arrow keys
        InputMap im = getRootPane().getInputMap();
        ActionMap am = getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");

        am.put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imgDown();
            }
        });
        am.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imgUp();
            }
        });




        // Set main contentPane
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);


        list = new JList<>(model);
        list.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 50), new BevelBorder(BevelBorder.LOWERED)));
        model.addElement("Empty");
        contentPane.add(list, BorderLayout.WEST);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                currentImage = ((JList)e.getSource()).getSelectedIndex();
                setImage(currentImage);
            }
        });


        // Initialize center JLabel with image resizable
        mainLabel = new JLabel("");
        imgicon = new ImageIcon(getClass().getResource("start.png"));  // set default image
        mainLabel.setIcon(imgicon);
        mainLabel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                mainLabel.setIcon(new ImageIcon(
                        imgicon.getImage()
                                .getScaledInstance(mainLabel.getWidth(), mainLabel.getHeight(), Image.SCALE_FAST)));
            }
        });
        contentPane.add(mainLabel, BorderLayout.CENTER);

        // Button controls
        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.SOUTH);
        JButton nextButton = new JButton("Next");
        JButton prevButton = new JButton("Prev");
        panel.add(prevButton);
        panel.add(nextButton);

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imgUp();
            }
        });

        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imgDown();
            }
        });
    }

    private class OpenerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser();
            jfc.setCurrentDirectory(new File("."));
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.setAcceptAllFileFilterUsed(false);
            if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                System.out.println("Current directory: " + jfc.getCurrentDirectory());
                System.out.println("Current file: " + jfc.getSelectedFile().getAbsolutePath());
                imgs = jfc.getSelectedFile().getAbsoluteFile().listFiles();
                currentImage = 0;
                dir = jfc.getSelectedFile().getAbsoluteFile().toString();
//                for (File file : imgArray) {
//                    System.out.println(file.getName());
//                }
                System.out.println(dir + imgs[0].getName());
                setImage(0);
                // Set up list

                addItems();
                mainLabel.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {}
                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        imgUp();
                    }
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                });



            } else {
                System.out.println("No selection");
            }
        }
    }

    private static void imgDown() {
        if (currentImage == 0)
            currentImage = imgs.length -1;
        else
            currentImage--;
        setImage(currentImage);
        list.setSelectedIndex(currentImage);
    }

    private static void imgUp() {
        if (currentImage == imgs.length-1)
            currentImage = 0;
        else
            currentImage++;
        setImage(currentImage);
        list.setSelectedIndex(currentImage);
    }

    private static void setImage(int index) {
        res = dir + "/" + imgs[index].getName();
        imgicon = new ImageIcon(res);
        mainLabel.setIcon(new ImageIcon(imgicon.getImage().getScaledInstance(mainLabel.getWidth(), mainLabel.getHeight(), Image.SCALE_FAST)));
        if (imgicon.getIconWidth() < mainLabel.getWidth())
            mainLabel.setIcon(new ImageIcon(imgicon.getImage().getScaledInstance(mainLabel.getWidth(), mainLabel.getHeight(), Image.SCALE_FAST)));
    }

    private static void addItems() {
        model.clear();
        String[] arr;
        for (File file : imgs) {
            arr = file.getName().split("/");
            model.addElement(arr[arr.length - 1]);
        }
        list.setSelectedIndex(0);

    }
}

