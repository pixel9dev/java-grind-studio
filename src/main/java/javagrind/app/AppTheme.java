package javagrind.app;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;

/**
 * Clean dark-mode styling, typography, HTML viewer, and line-numbered Java code editor.
 */
public final class AppTheme {

    public static final Color BG_ROOT = new Color(0x18, 0x1A, 0x1F);
    public static final Color BG_PANEL = new Color(0x21, 0x25, 0x2B);
    public static final Color BG_CARD = new Color(0x28, 0x2C, 0x34);
    public static final Color BG_EDITOR = new Color(0x1B, 0x1D, 0x23);
    public static final Color BG_GUTTER = new Color(0x16, 0x18, 0x1D);
    public static final Color BG_CONSOLE = new Color(0x13, 0x15, 0x19);
    public static final Color BORDER_SUBTLE = new Color(0x36, 0x3B, 0x46);

    public static final Color FG_PRIMARY = new Color(0xE6, 0xEA, 0xF2);
    public static final Color FG_MUTED = new Color(0x9D, 0xA5, 0xB4);
    public static final Color FG_CODE = new Color(0xEB, 0xF0, 0xFA);

    public static final Color ACCENT_BLUE = new Color(0x3B, 0x82, 0xF6);
    public static final Color ACCENT_GREEN = new Color(0x10, 0xB9, 0x81);
    public static final Color ACCENT_AMBER = new Color(0xF5, 0x9E, 0x0B);
    public static final Color ACCENT_RED = new Color(0xEF, 0x44, 0x44);
    public static final Color ACCENT_PURPLE = new Color(0x8B, 0x5C, 0xF6);

    public static final Font FONT_UI = pickFont(Font.PLAIN, 14, "Segoe UI", "Inter", "SansSerif");
    public static final Font FONT_UI_BOLD = pickFont(Font.BOLD, 14, "Segoe UI", "Inter", "SansSerif");
    public static final Font FONT_TITLE = pickFont(Font.BOLD, 18, "Segoe UI", "Inter", "SansSerif");
    public static final Font FONT_SMALL = pickFont(Font.PLAIN, 12, "Segoe UI", "Inter", "SansSerif");
    public static final Font FONT_CODE = pickFont(Font.PLAIN, 14, "JetBrains Mono", "Cascadia Mono", "Consolas", "Monospaced");
    public static final Font FONT_CONSOLE = pickFont(Font.PLAIN, 13, "JetBrains Mono", "Cascadia Mono", "Consolas", "Monospaced");

    private AppTheme() {}

    private static Font pickFont(int style, int size, String... candidates) {
        Set<String> available = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String name : candidates) {
            if (available.contains(name)) {
                return new Font(name, style, size);
            }
        }
        return new Font(Font.SANS_SERIF, style, size);
    }

    public static void installLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        UIManager.put("Panel.background", BG_PANEL);
        UIManager.put("Label.foreground", FG_PRIMARY);
        UIManager.put("Label.font", FONT_UI);
        UIManager.put("TabbedPane.background", BG_PANEL);
        UIManager.put("TabbedPane.foreground", FG_PRIMARY);
        UIManager.put("TabbedPane.selected", BG_CARD);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.font", FONT_UI_BOLD);
        UIManager.put("SplitPane.background", BG_ROOT);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("OptionPane.background", BG_PANEL);
        UIManager.put("OptionPane.messageForeground", FG_PRIMARY);
    }

    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_UI_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        return btn;
    }

    public static JButton createGhostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_UI);
        btn.setBackground(BG_CARD);
        btn.setForeground(FG_PRIMARY);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        return btn;
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        );
    }

    public static JEditorPane createHtmlPane() {
        JEditorPane pane = new JEditorPane();
        pane.setEditable(false);
        pane.setContentType("text/html");
        pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        pane.setBackground(BG_CARD);
        pane.setForeground(FG_PRIMARY);

        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet css = kit.getStyleSheet();
        css.addRule("body { font-family: 'Segoe UI', sans-serif; font-size: 13pt; color: #E6EAF2; background-color: #282C34; margin: 12px; line-height: 1.5; }");
        css.addRule("h1 { font-size: 17pt; color: #60A5FA; margin-top: 0; margin-bottom: 8px; }");
        css.addRule("h2 { font-size: 14pt; color: #93C5FD; margin-top: 14px; margin-bottom: 6px; }");
        css.addRule("h3 { font-size: 12.5pt; color: #FBBF24; margin-top: 12px; margin-bottom: 4px; }");
        css.addRule("p { margin-top: 4px; margin-bottom: 8px; }");
        css.addRule("ul { margin-top: 4px; margin-bottom: 8px; margin-left: 18px; }");
        css.addRule("li { margin-bottom: 4px; }");
        css.addRule("code { font-family: Consolas, monospace; background-color: #1B1D23; color: #FCD34D; padding: 1px 4px; }");
        css.addRule("pre { font-family: Consolas, monospace; font-size: 11.5pt; background-color: #181A1F; color: #E6EAF2; padding: 10px; border: 1px solid #363B46; margin: 8px 0; }");
        css.addRule(".trap { background-color: #2D2218; border-left: 4px solid #F59E0B; padding: 8px 10px; margin: 10px 0; }");
        css.addRule(".tip { background-color: #162B26; border-left: 4px solid #10B981; padding: 8px 10px; margin: 10px 0; }");
        css.addRule(".badge { background-color: #1E3A5F; color: #93C5FD; padding: 2px 6px; font-size: 10.5pt; font-weight: bold; }");
        pane.setEditorKit(kit);
        return pane;
    }

    public static JScrollPane wrapScroll(JComponent comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_SUBTLE, 1));
        sp.getViewport().setBackground(comp.getBackground());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    /**
     * Line-numbered Java source editor with 4-space Tab support and Ctrl+Enter run shortcut.
     */
    public static final class CodeEditorPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final JTextArea editor;
        private final JTextArea gutter;

        public CodeEditorPanel(Runnable onRunShortcut, Runnable onTextChanged) {
            super(new BorderLayout());
            setBackground(BG_EDITOR);

            editor = new JTextArea();
            editor.setFont(FONT_CODE);
            editor.setBackground(BG_EDITOR);
            editor.setForeground(FG_CODE);
            editor.setCaretColor(Color.WHITE);
            editor.setSelectionColor(new Color(0x26, 0x4F, 0x78));
            editor.setSelectedTextColor(Color.WHITE);
            editor.setTabSize(4);
            editor.setMargin(new Insets(10, 10, 10, 10));

            gutter = new JTextArea(" 1 ");
            gutter.setFont(FONT_CODE);
            gutter.setBackground(BG_GUTTER);
            gutter.setForeground(FG_MUTED);
            gutter.setEditable(false);
            gutter.setFocusable(false);
            gutter.setMargin(new Insets(10, 8, 10, 8));
            gutter.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_SUBTLE));

            // Replace Tab with 4 spaces for clean Java indentation
            editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "insertFourSpaces");
            editor.getActionMap().put("insertFourSpaces", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editor.replaceSelection("    ");
                }
            });

            // Bind Ctrl+Enter -> Run / Test!
            if (onRunShortcut != null) {
                editor.getInputMap(JComponent.WHEN_FOCUSED).put(
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), "runCodeNow");
                editor.getActionMap().put("runCodeNow", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onRunShortcut.run();
                    }
                });
            }

            editor.getDocument().addDocumentListener(new DocumentListener() {
                private void sync() {
                    updateLineNumbers();
                    if (onTextChanged != null) {
                        onTextChanged.run();
                    }
                }
                @Override public void insertUpdate(DocumentEvent e) { sync(); }
                @Override public void removeUpdate(DocumentEvent e) { sync(); }
                @Override public void changedUpdate(DocumentEvent e) { sync(); }
            });

            JScrollPane scroll = new JScrollPane(editor);
            scroll.setRowHeaderView(gutter);
            scroll.setBorder(BorderFactory.createLineBorder(BORDER_SUBTLE, 1));
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            setPreferredSize(new Dimension(520, 360));
            add(scroll, BorderLayout.CENTER);
        }

        private void updateLineNumbers() {
            int lines = Math.max(1, editor.getLineCount());
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= lines; i++) {
                sb.append(String.format("%2d ", i));
                if (i < lines) sb.append('\n');
            }
            gutter.setText(sb.toString());
        }

        public String getCode() {
            return editor.getText();
        }

        public void setCode(String code) {
            editor.setText(code);
            editor.setCaretPosition(0);
            updateLineNumbers();
        }

        public JTextArea getTextArea() {
            return editor;
        }
    }
}
