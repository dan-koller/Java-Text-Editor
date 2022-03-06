package editor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
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

        // Set text area
        textArea = new JTextArea();

        // Icons TO-DO: Fix paths on finished projects
        String pathPrefix = "/Users/daniel/Projekte/Text Editor/Text Editor/task/src/resources/";

        ImageIcon openIcon = new ImageIcon(pathPrefix + "open.png");
        ImageIcon saveIcon = new ImageIcon(pathPrefix + "save.png");
        ImageIcon searchIcon = new ImageIcon(pathPrefix + "search.png");
        ImageIcon prevIcon = new ImageIcon(pathPrefix + "prev.png");
        ImageIcon nextIcon = new ImageIcon(pathPrefix + "next.png");

        // Scrollpane for textarea
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        Dimension spDimension = new Dimension(700, 400);
        scrollPane.setPreferredSize(spDimension);

        // Save and load buttons
        JButton saveButton = new JButton(saveIcon);
        JButton openButton = new JButton(openIcon);

        saveButton.addActionListener(actionEvent -> save(textArea));
        openButton.addActionListener(actionEvent -> open(textArea));

        // Search field
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(260, 60));

        // Use regex option
        JCheckBox useRegexCheckbox = new JCheckBox("Use regex?");

        // Previous and next searches
        JButton startSearchButton = new JButton(searchIcon);
        startSearchButton.addActionListener(actionEvent -> search(textArea, searchField, useRegexCheckbox));
        JButton prevMatchButton = new JButton(prevIcon);
        prevMatchButton.addActionListener(actionEvent -> prevMatch(textArea));
        JButton nextMatchButton = new JButton(nextIcon);
        nextMatchButton.addActionListener(actionEvent -> nextMatch(textArea));

        // Add menubar
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem openMenuItem = new JMenuItem("Load");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        openMenuItem.addActionListener(actionEvent -> open(textArea));
        saveMenuItem.addActionListener(actionEvent -> save(textArea));
        exitMenuItem.addActionListener(actionEvent -> {
            System.exit(0);
        });

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        // Search menu
        JMenu searchMenu = new JMenu("Search");

        JMenuItem searchMenuItem = new JMenuItem("Search for entered phrase...");
        JMenuItem prevMatchMenuItem = new JMenuItem("Previous result");
        JMenuItem nextMatchMenuItem = new JMenuItem("Next result");
        JMenuItem useRegexMenuItem = new JMenuItem("Enable/Disable regex");

        searchMenuItem.addActionListener(actionEvent -> search(textArea, searchField, useRegexCheckbox));
        prevMatchMenuItem.addActionListener(actionEvent -> prevMatch(textArea));
        nextMatchMenuItem.addActionListener(actionEvent -> nextMatch(textArea));
        useRegexMenuItem.addActionListener(actionEvent -> {
            useRegexCheckbox.setSelected(!useRegexCheckbox.isSelected());
        });

        searchMenu.add(searchMenuItem);
        searchMenu.add(prevMatchMenuItem);
        searchMenu.add(nextMatchMenuItem);
        searchMenu.add(useRegexMenuItem);

        // Add components to menubar
        menuBar.add(fileMenu);
        menuBar.add(searchMenu);
        setJMenuBar(menuBar);

        // Set component names
        textArea.setName("TextArea");
        searchField.setName("SearchField");
        saveButton.setName("SaveButton");
        openButton.setName("OpenButton");
        startSearchButton.setName("StartSearchButton");
        prevMatchButton.setName("PreviousMatchButton");
        nextMatchButton.setName("NextMatchButton");
        useRegexCheckbox.setName("UseRegExCheckbox");
        fileChooser.setName("FileChooser");
        scrollPane.setName("ScrollPane");
        fileMenu.setName("MenuFile");
        searchMenu.setName("MenuSearch");
        openMenuItem.setName("MenuOpen");
        saveMenuItem.setName("MenuSave");
        exitMenuItem.setName("MenuExit");
        searchMenuItem.setName("MenuStartSearch");
        prevMatchMenuItem.setName("MenuPreviousMatch");
        nextMatchMenuItem.setName("MenuNextMatch");
        useRegexMenuItem.setName("MenuUseRegExp");

        // Place components
        JPanel menuPanel = new JPanel();
        menuPanel.add(saveButton);
        menuPanel.add(openButton);
        menuPanel.add(searchField);
        menuPanel.add(startSearchButton);
        menuPanel.add(prevMatchButton);
        menuPanel.add(nextMatchButton);
        menuPanel.add(useRegexCheckbox);

        // Only for testing purposes
        fileChooser.setVisible(false);
        menuPanel.add(fileChooser);

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
