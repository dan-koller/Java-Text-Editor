package editor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {

    private final JTextArea textArea;
    private final JFileChooser fileChooser = new JFileChooser();
    private Matcher matcher;
    private boolean isFirstMatch = true;

    public TextEditor() {
        // Basic window settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 540);
        setTitle("Java Text Editor");

        // Icons
        ImageIcon openIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/open.png")));
        ImageIcon saveIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/save.png")));
        ImageIcon searchIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/search.png")));
        ImageIcon prevIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/prev.png")));
        ImageIcon nextIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/next.png")));

        // Set textarea...
        textArea = new JTextArea();
        textArea.setName("TextArea");
        // ...and add it to the scrollpane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        scrollPane.setName("ScrollPane");

        // Save and load buttons
        JButton saveButton = new JButton(saveIcon);
        saveButton.addActionListener(actionEvent -> save(textArea));
        saveButton.setName("SaveButton");

        JButton openButton = new JButton(openIcon);
        openButton.addActionListener(actionEvent -> open(textArea));
        openButton.setName("OpenButton");

        // Search field
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(260, 60));
        searchField.setName("SearchField");

        // Use regex option
        JCheckBox useRegexCheckbox = new JCheckBox("Use regex?");
        useRegexCheckbox.setName("UseRegExCheckbox");

        // Previous and next searches
        JButton startSearchButton = new JButton(searchIcon);
        startSearchButton.addActionListener(actionEvent -> search(textArea, searchField, useRegexCheckbox));
        startSearchButton.setName("StartSearchButton");

        JButton prevMatchButton = new JButton(prevIcon);
        prevMatchButton.addActionListener(actionEvent -> prevMatch(textArea));
        prevMatchButton.setName("PreviousMatchButton");

        JButton nextMatchButton = new JButton(nextIcon);
        nextMatchButton.addActionListener(actionEvent -> nextMatch(textArea));
        nextMatchButton.setName("NextMatchButton");

        // Add menu-bar
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F); // Only works on win32
        fileMenu.setName("MenuFile");

        JMenuItem openMenuItem = new JMenuItem("Load");
        openMenuItem.addActionListener(actionEvent -> open(textArea));
        openMenuItem.setName("MenuOpen");

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(actionEvent -> save(textArea));
        saveMenuItem.setName("MenuSave");

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(actionEvent -> {
            System.exit(0);
        });
        exitMenuItem.setName("MenuExit");

        // Add file menu components
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        // Search menu
        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");

        JMenuItem searchMenuItem = new JMenuItem("Search for entered phrase...");
        searchMenuItem.addActionListener(actionEvent -> search(textArea, searchField, useRegexCheckbox));
        searchMenuItem.setName("MenuStartSearch");

        JMenuItem prevMatchMenuItem = new JMenuItem("Previous result");
        prevMatchMenuItem.addActionListener(actionEvent -> prevMatch(textArea));
        prevMatchMenuItem.setName("MenuPreviousMatch");

        JMenuItem nextMatchMenuItem = new JMenuItem("Next result");
        nextMatchMenuItem.addActionListener(actionEvent -> nextMatch(textArea));
        nextMatchMenuItem.setName("MenuNextMatch");

        JMenuItem useRegexMenuItem = new JMenuItem("Enable/Disable regex");
        useRegexMenuItem.addActionListener(actionEvent -> {
            useRegexCheckbox.setSelected(!useRegexCheckbox.isSelected());
        });
        useRegexMenuItem.setName("MenuUseRegExp");

        // Add search menu components
        searchMenu.add(searchMenuItem);
        searchMenu.addSeparator();
        searchMenu.add(prevMatchMenuItem);
        searchMenu.add(nextMatchMenuItem);
        searchMenu.addSeparator();
        searchMenu.add(useRegexMenuItem);

        // Add components to menu-bar
        menuBar.add(fileMenu);
        menuBar.add(searchMenu);
        setJMenuBar(menuBar);

        // Create an invisible FileChooser instance
        fileChooser.setVisible(false);
        fileChooser.setName("FileChooser");

        // Place components
        JPanel menuPanel = new JPanel();
        menuPanel.add(saveButton);
        menuPanel.add(openButton);
        menuPanel.add(searchField);
        menuPanel.add(startSearchButton);
        menuPanel.add(prevMatchButton);
        menuPanel.add(nextMatchButton);
        menuPanel.add(useRegexCheckbox);
        menuPanel.add(fileChooser); // optional, it's hidden anyway

        JPanel editorPanel = new JPanel();
        editorPanel.add(scrollPane);

        add(menuPanel, BorderLayout.NORTH);
        add(editorPanel, BorderLayout.CENTER);

        // Render window components
        setVisible(true);
    }

    // IO methods
    private void open(JTextArea textArea) {
        fileChooser.setVisible(true);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            // Clear textarea from previous entries
            textArea.setText("");
            // Open file
            BufferedReader openFile = null;
            try {
                openFile = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                textArea.read(openFile, null);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (openFile != null) {
                    try {
                        openFile.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private void save(JTextArea textArea) {
        fileChooser.setVisible(true);

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            BufferedWriter writeFile = null;
            try {
                writeFile = new BufferedWriter(new FileWriter(saveFile));
                textArea.write(writeFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (writeFile != null) {
                    try {
                        writeFile.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    // Search methods
    private void search(JTextArea textArea, JTextField searchField, JCheckBox regexCheckbox) {
        Thread searchThread;
        if (regexCheckbox.isSelected()) {
            searchThread = new Thread(() -> {
                Pattern searchPattern = Pattern.compile(searchField.getText());
                matcher = searchPattern.matcher(textArea.getText());
                nextMatch(textArea);
            });
        } else {
            // Non-regex search
            searchThread = new Thread(() -> {
                String searchedString = searchField.getText();
                String[] ignorables = {"\\", ".", "^", "[", "]", "$", "-", "(", ")", "{", "}", "|", "?", "*", "+", "!"};
                String replacement;
                // Replace with regex characters whern searching for matches
                for (String ignore : ignorables) {
                    replacement = "\\" + ignore;
                    searchedString = searchedString.replace(ignore, replacement);
                }
                Pattern searchPattern = Pattern.compile(searchedString);
                matcher = searchPattern.matcher(textArea.getText());
                nextMatch(textArea);
            });
        }
        searchThread.setDaemon(true);
        searchThread.start();
    }

    private void nextMatch(JTextArea textArea) {
        String content = textArea.getText();
        int index = 0;
        String matchedString = null;

        if (matcher.find()) {
            matchedString = matcher.group();

            // Check for first match otherwise continue
            if (isFirstMatch) {
                index = content.indexOf(matchedString);
                isFirstMatch = false;
            } else {
                int startPoint = matcher.start();
                if (matcher.find(startPoint)) {
                    index = content.indexOf(matchedString, startPoint);
                }
            }
            textArea.setCaretPosition(index + matchedString.length());
            textArea.select(index, index + matchedString.length());
            textArea.grabFocus();
        } else if (matcher.find(0)) {
            matchedString = matcher.group();
            index = content.indexOf(matchedString);
            textArea.setCaretPosition(index + matchedString.length());
            textArea.select(index, index + matchedString.length());
            textArea.grabFocus();
        }
    }

    @SuppressWarnings("Result is ignored")
    private void prevMatch(JTextArea textArea) {
        int counter;
        int index = 0;
        int endPoint = matcher.start();

        String matchedString = matcher.group();

        if (endPoint != 0) {
            if (matcher.find(0)) {
                int currentIndex = 0;
                int firstIndex = matcher.start();
                counter = 0;
                if (firstIndex == endPoint) {
                    while (matcher.find()) {
                        index = matcher.start();
                    }
                    matcher.find(index);
                } else {
                    do {
                        if (counter != 0) {
                            if (matcher.find()) {
                                currentIndex = matcher.start();
                                if (currentIndex < endPoint) {
                                    index = currentIndex;
                                }
                            }
                        } else {
                            counter++;
                            currentIndex = matcher.start();
                            if (currentIndex < endPoint) {
                                index = currentIndex;
                            }
                        }
                    } while (currentIndex < endPoint);
                }
                matcher.find(index);
            }
        } else {
            while (matcher.find()) {
                index = matcher.start();
            }
            matcher.find(index);
        }
        textArea.setCaretPosition(index + matchedString.length());
        textArea.select(index, index + matchedString.length());
        textArea.grabFocus();
    }

    // Create an invisible border to properly align components
    private static void setMargin(JComponent aComponent, int aTop, int aRight, int aBottom, int aLeft) {
        Border border = aComponent.getBorder();
        Border marginBorder = new EmptyBorder(new Insets(aTop, aLeft, aBottom, aRight));
        aComponent.setBorder(border == null ? marginBorder : new CompoundBorder(marginBorder, border));
    }
}
