package javagrind.app;

import javagrind.app.AppTheme.CodeEditorPanel;
import javagrind.app.CurriculumData.Flashcard;
import javagrind.app.CurriculumData.Lesson;
import javagrind.app.CurriculumData.QuizItem;
import javagrind.app.ProblemBank.CodingProblem;
import javagrind.enterprise.AsyncPipelineOrchestrator;
import javagrind.enterprise.AuditEventBus;
import javagrind.enterprise.EmbeddedRestServer;
import javagrind.enterprise.ExecutionOutcome;
import javagrind.enterprise.SkillRegistryScanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Interactive Desktop Java Application for learning Java, grinding 32 coding problems
 * with an embedded JDK 17 compiler, practicing 20 MCQs, and mastering 25 interview flashcards.
 */
public final class JavaGrindApp extends JFrame {

    private static final long serialVersionUID = 1L;
    private final ProgressStore store = new ProgressStore();
    private final JTabbedPane tabs = new JTabbedPane();

    // Header progress widgets
    private final JLabel statsLabel = new JLabel();
    private final JProgressBar overallBar = new JProgressBar(0, 100);

    // Tab 1 (Learn) state
    private JList<Lesson> lessonList;
    private JEditorPane lessonHtmlPane;
    private CodeEditorPanel playgroundEditor;
    private JTextArea playgroundConsole;
    private JButton runPlaygroundBtn;
    private Lesson activeLesson;

    // Tab 2 (Coding Grind) state
    private final DefaultListModel<CodingProblem> problemListModel = new DefaultListModel<>();
    private JList<CodingProblem> problemList;
    private JComboBox<String> groupFilterCombo;
    private JEditorPane problemHtmlPane;
    private CodeEditorPanel solutionEditor;
    private JTextArea testConsole;
    private JButton runTestsBtn;
    private CodingProblem activeProblem;
    private boolean suppressDraftSave = false;

    // Tab 3 (Quiz) state
    private List<QuizItem> activeQuizDeck = new ArrayList<>(CurriculumData.QUIZZES);
    private int quizIndex = 0;
    private JLabel quizHeaderLabel;
    private JTextArea quizPromptArea;
    private final JButton[] optionButtons = new JButton[4];
    private JTextArea quizExplanationArea;

    // Tab 4 (Flashcards) state
    private int cardIndex = 0;
    private boolean cardShowingAnswer = false;
    private JLabel cardTopicLabel;
    private JLabel cardSideBadge;
    private JTextArea cardBodyArea;
    private JButton cardMasteredBtn;

    // Tab 5 (Enterprise Lab) state
    private final EmbeddedRestServer restServer = new EmbeddedRestServer(this::buildLiveProgressJson);
    private JLabel serverStatusLabel;
    private JTextArea enterpriseOutputArea;
    private JTextArea auditStreamArea;

    public JavaGrindApp() {
        super("Java Grind Studio — Core-to-Enterprise Java 17 Workbench");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1360, 850);
        setMinimumSize(new Dimension(1040, 680));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                restServer.stop();
            }
        });

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BG_ROOT);
        root.add(buildHeaderBar(), BorderLayout.NORTH);

        tabs.setBackground(AppTheme.BG_PANEL);
        tabs.setForeground(AppTheme.FG_PRIMARY);
        tabs.addTab("  1. Learn & Playground (8)  ", buildLearnTab());
        tabs.addTab("  2. Coding Grind (36)  ", buildGrindTab());
        tabs.addTab("  3. MCQ & SQL Quiz (20)  ", buildQuizTab());
        tabs.addTab("  4. Flashcards & 48h Plan  ", buildFlashcardsAndPlanTab());
        tabs.addTab("  5. Enterprise Lab (REST, Async & Patterns)  ", buildEnterpriseLabTab());

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);

        AuditEventBus.getInstance().subscribe(ev -> SwingUtilities.invokeLater(this::refreshAuditStream));
        AuditEventBus.getInstance().publish("StudioApp", "APP_INITIALIZED", "Java 17 Workbench ready");

        refreshHeaderStats();
    }

    // ==================== HEADER BAR ====================

    private JPanel buildHeaderBar() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(AppTheme.BG_ROOT);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));

        JLabel title = new JLabel("Java Grind Studio");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("  |  JDK 17 Compiler Sandbox, 36 Coding Problems, Embedded REST & Design Patterns");
        subtitle.setFont(AppTheme.FONT_SMALL);
        subtitle.setForeground(AppTheme.FG_MUTED);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        left.setOpaque(false);
        left.add(title);
        left.add(subtitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        statsLabel.setFont(AppTheme.FONT_UI_BOLD);
        statsLabel.setForeground(AppTheme.FG_PRIMARY);

        overallBar.setPreferredSize(new Dimension(180, 18));
        overallBar.setStringPainted(true);
        overallBar.setBackground(AppTheme.BG_CARD);
        overallBar.setForeground(AppTheme.ACCENT_GREEN);
        overallBar.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_SUBTLE, 1));

        right.add(statsLabel);
        right.add(overallBar);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private void refreshHeaderStats() {
        long lessonsDone = CurriculumData.LESSONS.stream().filter(l -> store.isLessonDone(l.id())).count();
        long problemsDone = ProblemBank.PROBLEMS.stream().filter(p -> store.isProblemSolved(p.id())).count();
        long quizzesDone = CurriculumData.QUIZZES.stream().filter(q -> store.isQuizAnsweredCorrectly(q.id())).count();
        int cardsDone = 0;
        for (int i = 0; i < CurriculumData.FLASHCARDS.size(); i++) {
            if (store.isFlashcardMastered(i)) cardsDone++;
        }

        statsLabel.setText(String.format(
                "Lessons: %d/%d   |   Grind Solved: %d/%d   |   Quiz: %d/%d   |   Cards: %d/%d",
                lessonsDone, CurriculumData.LESSONS.size(),
                problemsDone, ProblemBank.PROBLEMS.size(),
                quizzesDone, CurriculumData.QUIZZES.size(),
                cardsDone, CurriculumData.FLASHCARDS.size()
        ));

        int totalItems = CurriculumData.LESSONS.size() + ProblemBank.PROBLEMS.size()
                + CurriculumData.QUIZZES.size() + CurriculumData.FLASHCARDS.size();
        int doneItems = (int) (lessonsDone + problemsDone + quizzesDone + cardsDone);
        int pct = (int) Math.round((doneItems * 100.0) / totalItems);
        overallBar.setValue(pct);
        overallBar.setString(pct + "% Ready");
    }

    // ==================== TAB 1: LEARN JAVA & PLAYGROUND ====================

    private JPanel buildLearnTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.BG_PANEL);

        DefaultListModel<Lesson> model = new DefaultListModel<>();
        CurriculumData.LESSONS.forEach(model::addElement);

        lessonList = new JList<>(model);
        lessonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lessonList.setBackground(AppTheme.BG_PANEL);
        lessonList.setForeground(AppTheme.FG_PRIMARY);
        lessonList.setFont(AppTheme.FONT_UI);
        lessonList.setFixedCellHeight(42);
        lessonList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Lesson lesson = (Lesson) value;
                boolean done = store.isLessonDone(lesson.id());
                String prefix = done ? "[DONE] " : "       ";
                lbl.setText(" " + prefix + lesson.title());
                lbl.setFont(done ? AppTheme.FONT_UI_BOLD : AppTheme.FONT_UI);
                lbl.setBackground(isSelected ? AppTheme.ACCENT_BLUE : AppTheme.BG_PANEL);
                lbl.setForeground(isSelected ? Color.WHITE : (done ? AppTheme.ACCENT_GREEN : AppTheme.FG_PRIMARY));
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return lbl;
            }
        });

        lessonHtmlPane = AppTheme.createHtmlPane();

        playgroundEditor = new CodeEditorPanel(this::executePlayground, null);
        playgroundConsole = new JTextArea();
        playgroundConsole.setEditable(false);
        playgroundConsole.setFont(AppTheme.FONT_CONSOLE);
        playgroundConsole.setBackground(AppTheme.BG_CONSOLE);
        playgroundConsole.setForeground(AppTheme.FG_PRIMARY);
        playgroundConsole.setMargin(new java.awt.Insets(10, 12, 10, 12));
        playgroundConsole.setText("Click 'Run Playground (Ctrl+Enter)' to compile and execute the Java code above!");

        // Playground toolbar
        JPanel playToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        playToolbar.setBackground(AppTheme.BG_PANEL);

        runPlaygroundBtn = AppTheme.createButton("Run Playground (Ctrl+Enter)", AppTheme.ACCENT_GREEN, Color.WHITE);
        runPlaygroundBtn.addActionListener(e -> executePlayground());

        JButton resetDemoBtn = AppTheme.createGhostButton("Reset Demo Code");
        resetDemoBtn.addActionListener(e -> {
            if (activeLesson != null) {
                playgroundEditor.setCode(activeLesson.playgroundCode());
            }
        });

        JButton completeAndGrindBtn = AppTheme.createButton(
                "Mark Lesson Done & Practice Problems ->", AppTheme.ACCENT_BLUE, Color.WHITE);
        completeAndGrindBtn.addActionListener(e -> {
            if (activeLesson != null) {
                store.setLessonDone(activeLesson.id(), true);
                lessonList.repaint();
                refreshHeaderStats();
                jumpToProblemGroup(activeLesson.targetProblemGroup());
            }
        });

        playToolbar.add(runPlaygroundBtn);
        playToolbar.add(resetDemoBtn);
        playToolbar.add(completeAndGrindBtn);

        JPanel editorBox = new JPanel(new BorderLayout());
        editorBox.setBackground(AppTheme.BG_PANEL);
        editorBox.add(playToolbar, BorderLayout.NORTH);
        editorBox.add(playgroundEditor, BorderLayout.CENTER);

        JPanel consoleBox = new JPanel(new BorderLayout());
        consoleBox.setBackground(AppTheme.BG_PANEL);
        JLabel outTitle = new JLabel("  Live Console Output:");
        outTitle.setFont(AppTheme.FONT_UI_BOLD);
        outTitle.setForeground(AppTheme.FG_MUTED);
        outTitle.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        consoleBox.add(outTitle, BorderLayout.NORTH);
        consoleBox.add(AppTheme.wrapScroll(playgroundConsole), BorderLayout.CENTER);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorBox, consoleBox);
        rightSplit.setResizeWeight(0.66);
        rightSplit.setDividerSize(6);

        JSplitPane centerSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, AppTheme.wrapScroll(lessonHtmlPane), rightSplit);
        centerSplit.setResizeWeight(0.45);
        centerSplit.setDividerSize(6);

        JScrollPane leftScroll = AppTheme.wrapScroll(lessonList);
        leftScroll.setPreferredSize(new Dimension(295, 600));

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, centerSplit);
        mainSplit.setDividerLocation(295);
        mainSplit.setDividerSize(6);

        lessonList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && lessonList.getSelectedValue() != null) {
                loadLesson(lessonList.getSelectedValue());
            }
        });
        lessonList.setSelectedIndex(0);

        panel.add(mainSplit, BorderLayout.CENTER);
        return panel;
    }

    private void loadLesson(Lesson lesson) {
        activeLesson = lesson;
        lessonHtmlPane.setText(lesson.html());
        lessonHtmlPane.setCaretPosition(0);
        playgroundEditor.setCode(lesson.playgroundCode());
        playgroundConsole.setForeground(AppTheme.FG_MUTED);
        playgroundConsole.setText("Click 'Run Playground (Ctrl+Enter)' to execute " + lesson.id() + " live!");
    }

    private void executePlayground() {
        String code = playgroundEditor.getCode();
        runPlaygroundBtn.setEnabled(false);
        runPlaygroundBtn.setText("Compiling & Running...");
        playgroundConsole.setForeground(AppTheme.FG_MUTED);
        playgroundConsole.setText("Compiling Playground.java with JDK 17...");

        new SwingWorker<LiveJavaEngine.RunOutput, Void>() {
            @Override
            protected LiveJavaEngine.RunOutput doInBackground() {
                return LiveJavaEngine.compileAndRunPlayground(code);
            }

            @Override
            protected void done() {
                runPlaygroundBtn.setEnabled(true);
                runPlaygroundBtn.setText("Run Playground (Ctrl+Enter)");
                try {
                    LiveJavaEngine.RunOutput res = get();
                    if (!res.compiledOk() || res.timedOut()) {
                        playgroundConsole.setForeground(AppTheme.ACCENT_RED);
                        AuditEventBus.getInstance().publish("Playground", "RUN_FAILED",
                                (activeLesson != null ? activeLesson.id() : "code") + " (" + res.elapsedMs() + " ms)");
                    } else {
                        playgroundConsole.setForeground(AppTheme.ACCENT_GREEN);
                        AuditEventBus.getInstance().publish("Playground", "RUN_OK",
                                (activeLesson != null ? activeLesson.id() : "code") + " (" + res.elapsedMs() + " ms)");
                    }
                    playgroundConsole.setText(res.output() + "\n\n[Finished in " + res.elapsedMs() + " ms]");
                    playgroundConsole.setCaretPosition(0);
                } catch (Exception ex) {
                    playgroundConsole.setForeground(AppTheme.ACCENT_RED);
                    playgroundConsole.setText("Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void jumpToProblemGroup(String groupName) {
        tabs.setSelectedIndex(1);
        groupFilterCombo.setSelectedItem(groupName);
        if (problemListModel.getSize() > 0) {
            problemList.setSelectedIndex(0);
        }
    }

    // ==================== TAB 2: CODING GRIND (32 PROBLEMS) ====================

    private JPanel buildGrindTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.BG_PANEL);

        // Left sidebar: category filter + problem list
        JPanel sidebar = new JPanel(new BorderLayout(0, 6));
        sidebar.setBackground(AppTheme.BG_PANEL);
        sidebar.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 4));

        Set<String> groups = new LinkedHashSet<>();
        groups.add("All Categories (" + ProblemBank.PROBLEMS.size() + ")");
        for (CodingProblem p : ProblemBank.PROBLEMS) {
            groups.add(p.group());
        }
        groupFilterCombo = new JComboBox<>(groups.toArray(new String[0]));
        groupFilterCombo.setFont(AppTheme.FONT_UI_BOLD);
        groupFilterCombo.setBackground(AppTheme.BG_CARD);
        groupFilterCombo.setForeground(AppTheme.FG_PRIMARY);
        groupFilterCombo.addActionListener(e -> populateProblemList());

        problemList = new JList<>(problemListModel);
        problemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        problemList.setBackground(AppTheme.BG_PANEL);
        problemList.setFont(AppTheme.FONT_UI);
        problemList.setFixedCellHeight(38);
        problemList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                CodingProblem p = (CodingProblem) value;
                boolean solved = store.isProblemSolved(p.id());
                String mark = solved ? "[PASS]" : "[TODO]";
                lbl.setText(String.format(" %s %-4s %s", mark, p.id(), p.title()));
                lbl.setFont(solved ? AppTheme.FONT_UI_BOLD : AppTheme.FONT_UI);
                lbl.setBackground(isSelected ? AppTheme.ACCENT_BLUE : AppTheme.BG_PANEL);
                lbl.setForeground(isSelected ? Color.WHITE : (solved ? AppTheme.ACCENT_GREEN : AppTheme.FG_PRIMARY));
                return lbl;
            }
        });

        sidebar.add(groupFilterCombo, BorderLayout.NORTH);
        sidebar.add(AppTheme.wrapScroll(problemList), BorderLayout.CENTER);
        sidebar.setPreferredSize(new Dimension(305, 600));

        // Problem statement HTML pane
        problemHtmlPane = AppTheme.createHtmlPane();

        // Code editor + auto-draft saving
        solutionEditor = new CodeEditorPanel(this::executeProblemTests, () -> {
            if (!suppressDraftSave && activeProblem != null) {
                store.saveDraftCode(activeProblem.id(), solutionEditor.getCode());
            }
        });

        // Action bar above editor
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        actionBar.setBackground(AppTheme.BG_PANEL);

        runTestsBtn = AppTheme.createButton("Compile & Run Tests (Ctrl+Enter)", AppTheme.ACCENT_GREEN, Color.WHITE);
        runTestsBtn.addActionListener(e -> executeProblemTests());

        JButton hintBtn = AppTheme.createGhostButton("Show Hint");
        hintBtn.addActionListener(e -> {
            if (activeProblem != null) {
                testConsole.setForeground(AppTheme.ACCENT_AMBER);
                testConsole.setText("HINT FOR " + activeProblem.id() + " (" + activeProblem.title() + "):\n"
                        + "------------------------------------------------------------\n"
                        + activeProblem.hint());
            }
        });

        JButton solBtn = AppTheme.createGhostButton("View Reference Solution");
        solBtn.addActionListener(e -> {
            if (activeProblem != null) {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Load the reference solution into the editor for " + activeProblem.id() + "?\n"
                                + "(Tip: Try on your own for 10-15 mins first!)",
                        "Load Reference Solution",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    solutionEditor.setCode(activeProblem.solutionCode());
                }
            }
        });

        JButton resetBtn = AppTheme.createGhostButton("Reset Starter");
        resetBtn.addActionListener(e -> {
            if (activeProblem != null) {
                store.clearDraftCode(activeProblem.id());
                suppressDraftSave = true;
                solutionEditor.setCode(activeProblem.starterCode());
                suppressDraftSave = false;
            }
        });

        JButton nextUnsolvedBtn = AppTheme.createButton("Next Unsolved ->", AppTheme.ACCENT_BLUE, Color.WHITE);
        nextUnsolvedBtn.addActionListener(e -> selectNextUnsolved());

        actionBar.add(runTestsBtn);
        actionBar.add(hintBtn);
        actionBar.add(solBtn);
        actionBar.add(resetBtn);
        actionBar.add(nextUnsolvedBtn);

        JPanel editorContainer = new JPanel(new BorderLayout());
        editorContainer.setBackground(AppTheme.BG_PANEL);
        editorContainer.add(actionBar, BorderLayout.NORTH);
        editorContainer.add(solutionEditor, BorderLayout.CENTER);

        testConsole = new JTextArea();
        testConsole.setEditable(false);
        testConsole.setFont(AppTheme.FONT_CONSOLE);
        testConsole.setBackground(AppTheme.BG_CONSOLE);
        testConsole.setForeground(AppTheme.FG_PRIMARY);
        testConsole.setMargin(new java.awt.Insets(10, 12, 10, 12));

        JPanel consoleContainer = new JPanel(new BorderLayout());
        consoleContainer.setBackground(AppTheme.BG_PANEL);
        JLabel testHeader = new JLabel("  Automated Test Suite Output:");
        testHeader.setFont(AppTheme.FONT_UI_BOLD);
        testHeader.setForeground(AppTheme.FG_MUTED);
        testHeader.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        consoleContainer.add(testHeader, BorderLayout.NORTH);
        consoleContainer.add(AppTheme.wrapScroll(testConsole), BorderLayout.CENTER);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorContainer, consoleContainer);
        rightSplit.setResizeWeight(0.65);
        rightSplit.setDividerSize(6);

        JSplitPane workSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, AppTheme.wrapScroll(problemHtmlPane), rightSplit);
        workSplit.setResizeWeight(0.36);
        workSplit.setDividerSize(6);

        JSplitPane outerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, workSplit);
        outerSplit.setDividerLocation(305);
        outerSplit.setDividerSize(6);

        problemList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && problemList.getSelectedValue() != null) {
                loadProblem(problemList.getSelectedValue());
            }
        });

        populateProblemList();
        panel.add(outerSplit, BorderLayout.CENTER);
        return panel;
    }

    private void populateProblemList() {
        String selectedGroup = (String) groupFilterCombo.getSelectedItem();
        problemListModel.clear();
        for (CodingProblem p : ProblemBank.PROBLEMS) {
            if (selectedGroup == null || selectedGroup.startsWith("All ") || p.group().equals(selectedGroup)) {
                problemListModel.addElement(p);
            }
        }
        if (!problemListModel.isEmpty()) {
            problemList.setSelectedIndex(0);
        }
    }

    private void loadProblem(CodingProblem p) {
        activeProblem = p;
        String statusBadge = store.isProblemSolved(p.id())
                ? "<span class='badge' style='background:#064E3B;color:#6EE7B7;'>SOLVED</span>"
                : "<span class='badge'>" + p.difficulty().toUpperCase() + "</span>";
        problemHtmlPane.setText("<p>" + statusBadge + " &nbsp; <span class='badge'>" + p.group() + "</span></p>"
                + p.descriptionHtml());
        problemHtmlPane.setCaretPosition(0);

        suppressDraftSave = true;
        solutionEditor.setCode(store.getDraftCode(p.id(), p.starterCode()));
        suppressDraftSave = false;

        testConsole.setForeground(AppTheme.FG_MUTED);
        testConsole.setText("Write your solution inside `class Solution` above and press Ctrl+Enter (or click Compile & Run Tests)!");
    }

    private void executeProblemTests() {
        if (activeProblem == null) return;
        CodingProblem target = activeProblem;
        String source = solutionEditor.getCode();
        store.saveDraftCode(target.id(), source);

        runTestsBtn.setEnabled(false);
        runTestsBtn.setText("Testing...");
        testConsole.setForeground(AppTheme.FG_MUTED);
        testConsole.setText("Compiling Solution.java and running edge-case test suite...");

        new SwingWorker<LiveJavaEngine.RunOutput, Void>() {
            @Override
            protected LiveJavaEngine.RunOutput doInBackground() {
                return ProblemBank.evaluate(target, source);
            }

            @Override
            protected void done() {
                runTestsBtn.setEnabled(true);
                runTestsBtn.setText("Compile & Run Tests (Ctrl+Enter)");
                try {
                    LiveJavaEngine.RunOutput res = get();
                    String text = res.output();
                    boolean allPassed = res.compiledOk() && !res.timedOut()
                            && text.startsWith(ProblemBank.ALL_PASSED_TOKEN);
                    if (allPassed) {
                        text = text.substring(ProblemBank.ALL_PASSED_TOKEN.length());
                        store.markProblemSolved(target.id());
                        problemList.repaint();
                        refreshHeaderStats();
                        loadProblemHeaderOnly(target);
                        testConsole.setForeground(AppTheme.ACCENT_GREEN);
                        AuditEventBus.getInstance().publish("GrindEngine", "PROBLEM_SOLVED",
                                target.id() + " " + target.title() + " (" + res.elapsedMs() + " ms)");
                    } else {
                        testConsole.setForeground(res.compiledOk() && !res.timedOut()
                                ? AppTheme.ACCENT_AMBER : AppTheme.ACCENT_RED);
                        AuditEventBus.getInstance().publish("GrindEngine", "TEST_ATTEMPT",
                                target.id() + " (" + (res.compiledOk() ? "cases failed" : "compile error") + ")");
                    }
                    testConsole.setText(text + "\n[Finished in " + res.elapsedMs() + " ms]");
                    testConsole.setCaretPosition(0);
                } catch (Exception ex) {
                    testConsole.setForeground(AppTheme.ACCENT_RED);
                    testConsole.setText("Execution error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void loadProblemHeaderOnly(CodingProblem p) {
        String statusBadge = "<span class='badge' style='background:#064E3B;color:#6EE7B7;'>SOLVED</span>";
        problemHtmlPane.setText("<p>" + statusBadge + " &nbsp; <span class='badge'>" + p.group() + "</span></p>"
                + p.descriptionHtml());
        problemHtmlPane.setCaretPosition(0);
    }

    private void selectNextUnsolved() {
        int size = problemListModel.getSize();
        if (size == 0) return;
        int start = Math.max(0, problemList.getSelectedIndex());
        for (int offset = 1; offset <= size; offset++) {
            int idx = (start + offset) % size;
            if (!store.isProblemSolved(problemListModel.get(idx).id())) {
                problemList.setSelectedIndex(idx);
                problemList.ensureIndexIsVisible(idx);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Every problem in this view is solved! Switch category or take the Quiz!");
    }

    // ==================== TAB 3: INTERACTIVE QUIZ ARENA ====================

    private JPanel buildQuizTab() {
        JPanel outer = new JPanel(new BorderLayout(16, 12));
        outer.setBackground(AppTheme.BG_PANEL);
        outer.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Top deck selector
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel deckBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        deckBar.setOpaque(false);
        JLabel filterLbl = new JLabel("Quiz Deck:");
        filterLbl.setFont(AppTheme.FONT_UI_BOLD);
        JComboBox<String> deckCombo = new JComboBox<>(new String[]{
                "All Questions (20)", "Core Java Traps (10)", "SQL & Backend (10)"
        });
        deckCombo.setFont(AppTheme.FONT_UI_BOLD);
        deckCombo.setBackground(AppTheme.BG_CARD);
        deckCombo.setForeground(AppTheme.FG_PRIMARY);
        deckCombo.addActionListener(e -> {
            String sel = (String) deckCombo.getSelectedItem();
            activeQuizDeck = new ArrayList<>();
            for (QuizItem q : CurriculumData.QUIZZES) {
                if (sel == null || sel.startsWith("All ")
                        || (sel.startsWith("Core") && q.deck().equals("Core Java Traps"))
                        || (sel.startsWith("SQL") && q.deck().equals("SQL & Backend"))) {
                    activeQuizDeck.add(q);
                }
            }
            quizIndex = 0;
            renderCurrentQuiz();
        });
        deckBar.add(filterLbl);
        deckBar.add(deckCombo);

        quizHeaderLabel = new JLabel();
        quizHeaderLabel.setFont(AppTheme.FONT_TITLE);
        quizHeaderLabel.setForeground(AppTheme.FG_PRIMARY);

        topBar.add(quizHeaderLabel, BorderLayout.WEST);
        topBar.add(deckBar, BorderLayout.EAST);

        // Center card: code snippet + 4 option buttons + explanation
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);

        quizPromptArea = new JTextArea();
        quizPromptArea.setEditable(false);
        quizPromptArea.setFont(AppTheme.FONT_CODE);
        quizPromptArea.setBackground(AppTheme.BG_EDITOR);
        quizPromptArea.setForeground(AppTheme.FG_CODE);
        quizPromptArea.setMargin(new java.awt.Insets(14, 16, 14, 16));
        JScrollPane promptScroll = AppTheme.wrapScroll(quizPromptArea);
        promptScroll.setPreferredSize(new Dimension(800, 195));

        JPanel optionsGrid = new JPanel(new GridLayout(4, 1, 0, 8));
        optionsGrid.setOpaque(false);
        char[] letters = {'A', 'B', 'C', 'D'};
        for (int i = 0; i < 4; i++) {
            char choice = letters[i];
            JButton btn = AppTheme.createGhostButton("");
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setFont(AppTheme.FONT_CODE);
            btn.addActionListener(e -> handleQuizAnswer(choice));
            optionButtons[i] = btn;
            optionsGrid.add(btn);
        }

        quizExplanationArea = new JTextArea();
        quizExplanationArea.setEditable(false);
        quizExplanationArea.setLineWrap(true);
        quizExplanationArea.setWrapStyleWord(true);
        quizExplanationArea.setFont(AppTheme.FONT_UI);
        quizExplanationArea.setBackground(AppTheme.BG_CARD);
        quizExplanationArea.setForeground(AppTheme.FG_PRIMARY);
        quizExplanationArea.setBorder(AppTheme.cardBorder());
        quizExplanationArea.setPreferredSize(new Dimension(800, 105));

        center.add(promptScroll, BorderLayout.NORTH);
        center.add(optionsGrid, BorderLayout.CENTER);
        center.add(quizExplanationArea, BorderLayout.SOUTH);

        // Bottom navigation
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        nav.setOpaque(false);
        JButton prevBtn = AppTheme.createGhostButton("<- Previous Question");
        prevBtn.addActionListener(e -> {
            if (!activeQuizDeck.isEmpty()) {
                quizIndex = (quizIndex - 1 + activeQuizDeck.size()) % activeQuizDeck.size();
                renderCurrentQuiz();
            }
        });
        JButton nextBtn = AppTheme.createButton("Next Question ->", AppTheme.ACCENT_BLUE, Color.WHITE);
        nextBtn.addActionListener(e -> {
            if (!activeQuizDeck.isEmpty()) {
                quizIndex = (quizIndex + 1) % activeQuizDeck.size();
                renderCurrentQuiz();
            }
        });
        nav.add(prevBtn);
        nav.add(nextBtn);

        outer.add(topBar, BorderLayout.NORTH);
        outer.add(center, BorderLayout.CENTER);
        outer.add(nav, BorderLayout.SOUTH);

        renderCurrentQuiz();
        return outer;
    }

    private void renderCurrentQuiz() {
        if (activeQuizDeck.isEmpty()) return;
        QuizItem q = activeQuizDeck.get(quizIndex);
        String solvedTag = store.isQuizAnsweredCorrectly(q.id()) ? "  [Answered Correctly]" : "";
        quizHeaderLabel.setText(String.format("[%d / %d] %s — %s%s",
                quizIndex + 1, activeQuizDeck.size(), q.deck(), q.title(), solvedTag));
        quizPromptArea.setText(q.promptCode());
        quizPromptArea.setCaretPosition(0);

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText("  " + q.options().get(i));
            optionButtons[i].setBackground(AppTheme.BG_CARD);
            optionButtons[i].setForeground(AppTheme.FG_PRIMARY);
        }
        quizExplanationArea.setForeground(AppTheme.FG_MUTED);
        quizExplanationArea.setText("Click an option (A, B, C, or D) above to check your answer and see the JVM/SQL explanation!");
    }

    private void handleQuizAnswer(char picked) {
        QuizItem q = activeQuizDeck.get(quizIndex);
        char correct = q.answer();
        char[] letters = {'A', 'B', 'C', 'D'};
        for (int i = 0; i < 4; i++) {
            if (letters[i] == correct) {
                optionButtons[i].setBackground(new Color(0x06, 0x5F, 0x46));
                optionButtons[i].setForeground(Color.WHITE);
            } else if (letters[i] == picked) {
                optionButtons[i].setBackground(new Color(0x7F, 0x1D, 0x1D));
                optionButtons[i].setForeground(Color.WHITE);
            } else {
                optionButtons[i].setBackground(AppTheme.BG_CARD);
                optionButtons[i].setForeground(AppTheme.FG_MUTED);
            }
        }

        if (picked == correct) {
            store.setQuizAnsweredCorrectly(q.id(), true);
            refreshHeaderStats();
            quizExplanationArea.setForeground(AppTheme.ACCENT_GREEN);
            quizExplanationArea.setText("CORRECT! (" + correct + ")\n" + q.explanation());
        } else {
            quizExplanationArea.setForeground(AppTheme.ACCENT_AMBER);
            quizExplanationArea.setText("NOT QUITE — Correct answer is " + correct + ".\n" + q.explanation());
        }
    }

    // ==================== TAB 4: FLASHCARDS & 48H BATTLE PLAN ====================

    private JPanel buildFlashcardsAndPlanTab() {
        JPanel splitWrap = new JPanel(new GridLayout(1, 2, 18, 0));
        splitWrap.setBackground(AppTheme.BG_PANEL);
        splitWrap.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Left: Interactive Flashcard Deck
        JPanel cardSection = new JPanel(new BorderLayout(0, 12));
        cardSection.setBackground(AppTheme.BG_PANEL);

        JPanel cardTop = new JPanel(new BorderLayout());
        cardTop.setOpaque(false);
        cardTopicLabel = new JLabel();
        cardTopicLabel.setFont(AppTheme.FONT_TITLE);
        cardSideBadge = new JLabel();
        cardSideBadge.setFont(AppTheme.FONT_UI_BOLD);
        cardSideBadge.setForeground(AppTheme.ACCENT_AMBER);
        cardTop.add(cardTopicLabel, BorderLayout.WEST);
        cardTop.add(cardSideBadge, BorderLayout.EAST);

        JPanel flashcardBox = new JPanel(new BorderLayout());
        flashcardBox.setBackground(AppTheme.BG_CARD);
        flashcardBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.ACCENT_PURPLE, 2),
                BorderFactory.createEmptyBorder(28, 26, 28, 26)
        ));
        flashcardBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cardBodyArea = new JTextArea();
        cardBodyArea.setEditable(false);
        cardBodyArea.setLineWrap(true);
        cardBodyArea.setWrapStyleWord(true);
        cardBodyArea.setFont(AppTheme.FONT_TITLE);
        cardBodyArea.setBackground(AppTheme.BG_CARD);
        cardBodyArea.setForeground(AppTheme.FG_PRIMARY);
        cardBodyArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        MouseAdapter flipClick = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardShowingAnswer = !cardShowingAnswer;
                renderFlashcard();
            }
        };
        flashcardBox.addMouseListener(flipClick);
        cardBodyArea.addMouseListener(flipClick);

        JLabel flipHint = new JLabel("Click anywhere on the card (or 'Flip Card') to reveal the interview answer",
                SwingConstants.CENTER);
        flipHint.setFont(AppTheme.FONT_SMALL);
        flipHint.setForeground(AppTheme.FG_MUTED);

        flashcardBox.add(cardBodyArea, BorderLayout.CENTER);
        flashcardBox.add(flipHint, BorderLayout.SOUTH);

        JPanel cardButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        cardButtons.setOpaque(false);

        JButton prevCardBtn = AppTheme.createGhostButton("<- Prev");
        prevCardBtn.addActionListener(e -> {
            cardIndex = (cardIndex - 1 + CurriculumData.FLASHCARDS.size()) % CurriculumData.FLASHCARDS.size();
            cardShowingAnswer = false;
            renderFlashcard();
        });

        JButton flipBtn = AppTheme.createButton("Flip Card", AppTheme.ACCENT_PURPLE, Color.WHITE);
        flipBtn.addActionListener(e -> {
            cardShowingAnswer = !cardShowingAnswer;
            renderFlashcard();
        });

        cardMasteredBtn = AppTheme.createButton("Mark Mastered", AppTheme.ACCENT_GREEN, Color.WHITE);
        cardMasteredBtn.addActionListener(e -> {
            boolean nowMastered = !store.isFlashcardMastered(cardIndex);
            store.setFlashcardMastered(cardIndex, nowMastered);
            refreshHeaderStats();
            renderFlashcard();
        });

        JButton nextCardBtn = AppTheme.createGhostButton("Next ->");
        nextCardBtn.addActionListener(e -> {
            cardIndex = (cardIndex + 1) % CurriculumData.FLASHCARDS.size();
            cardShowingAnswer = false;
            renderFlashcard();
        });

        cardButtons.add(prevCardBtn);
        cardButtons.add(flipBtn);
        cardButtons.add(cardMasteredBtn);
        cardButtons.add(nextCardBtn);

        cardSection.add(cardTop, BorderLayout.NORTH);
        cardSection.add(flashcardBox, BorderLayout.CENTER);
        cardSection.add(cardButtons, BorderLayout.SOUTH);

        // Right: 48-Hour Battle Plan Checklist
        JPanel planSection = new JPanel(new BorderLayout(0, 12));
        planSection.setBackground(AppTheme.BG_CARD);
        planSection.setBorder(AppTheme.cardBorder());

        JLabel planTitle = new JLabel("48-Hour Placement Drive Battle Plan (Interactive Checklist)");
        planTitle.setFont(AppTheme.FONT_TITLE);
        planTitle.setForeground(Color.WHITE);

        JPanel checkListPanel = new JPanel();
        checkListPanel.setLayout(new BoxLayout(checkListPanel, BoxLayout.Y_AXIS));
        checkListPanel.setBackground(AppTheme.BG_CARD);

        for (int i = 0; i < CurriculumData.BATTLE_PLAN_STEPS.size(); i++) {
            int stepIdx = i;
            String text = CurriculumData.BATTLE_PLAN_STEPS.get(i);
            JCheckBox cb = new JCheckBox("<html><body style='width:420px;padding:4px;'>" + text + "</body></html>");
            cb.setSelected(store.isPlanStepDone(stepIdx));
            cb.setFont(AppTheme.FONT_UI);
            cb.setBackground(AppTheme.BG_CARD);
            cb.setForeground(cb.isSelected() ? AppTheme.ACCENT_GREEN : AppTheme.FG_PRIMARY);
            cb.setFocusPainted(false);
            cb.addActionListener(e -> {
                store.setPlanStepDone(stepIdx, cb.isSelected());
                cb.setForeground(cb.isSelected() ? AppTheme.ACCENT_GREEN : AppTheme.FG_PRIMARY);
            });
            checkListPanel.add(cb);
            checkListPanel.add(Box.createVerticalStrut(8));
        }

        planSection.add(planTitle, BorderLayout.NORTH);
        planSection.add(AppTheme.wrapScroll(checkListPanel), BorderLayout.CENTER);

        renderFlashcard();

        splitWrap.add(cardSection);
        splitWrap.add(planSection);
        return splitWrap;
    }

    private void renderFlashcard() {
        Flashcard c = CurriculumData.FLASHCARDS.get(cardIndex);
        boolean mastered = store.isFlashcardMastered(cardIndex);
        cardTopicLabel.setText(String.format("Card %d / %d  —  %s",
                cardIndex + 1, CurriculumData.FLASHCARDS.size(), c.topic()));

        if (cardShowingAnswer) {
            cardSideBadge.setText("MODEL ANSWER");
            cardSideBadge.setForeground(AppTheme.ACCENT_GREEN);
            cardBodyArea.setFont(AppTheme.FONT_UI);
            cardBodyArea.setText(c.answer());
        } else {
            cardSideBadge.setText(mastered ? "QUESTION [MASTERED]" : "INTERVIEW QUESTION");
            cardSideBadge.setForeground(mastered ? AppTheme.ACCENT_GREEN : AppTheme.ACCENT_AMBER);
            cardBodyArea.setFont(AppTheme.FONT_TITLE);
            cardBodyArea.setText("\n" + c.question());
        }
        cardMasteredBtn.setText(mastered ? "Unmark Mastered" : "Mark Mastered");
    }

    // ==================== TAB 5: ENTERPRISE ARCHITECTURE LAB ====================

    private JPanel buildEnterpriseLabTab() {
        JPanel outer = new JPanel(new BorderLayout(14, 14));
        outer.setBackground(AppTheme.BG_PANEL);
        outer.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        // Top controls grid: Row 1 = REST Microservice, Row 2 = Async CompletableFuture & Reflection
        JPanel controlsWrapper = new JPanel(new GridLayout(2, 1, 0, 10));
        controlsWrapper.setOpaque(false);

        JPanel restCard = new JPanel(new BorderLayout(8, 6));
        restCard.setBackground(AppTheme.BG_CARD);
        restCard.setBorder(AppTheme.cardBorder());

        serverStatusLabel = new JLabel("Embedded HTTP REST Server (jdk.httpserver + TokenBucketRateLimiter + LRU Cache): STOPPED");
        serverStatusLabel.setFont(AppTheme.FONT_UI_BOLD);
        serverStatusLabel.setForeground(AppTheme.ACCENT_AMBER);

        JPanel restBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        restBtns.setOpaque(false);

        JButton startServerBtn = AppTheme.createButton("Start Local REST Server", AppTheme.ACCENT_GREEN, Color.WHITE);
        JButton stopServerBtn = AppTheme.createGhostButton("Stop Server");
        JButton healthBtn = AppTheme.createButton("GET /api/v1/health (JMX)", AppTheme.ACCENT_BLUE, Color.WHITE);
        JButton skillsApiBtn = AppTheme.createButton("GET /api/v1/skills (LRU Cached)", AppTheme.ACCENT_BLUE, Color.WHITE);
        JButton progressApiBtn = AppTheme.createGhostButton("GET /api/v1/progress");
        JButton burstBtn = AppTheme.createButton("Simulate 8x Burst (HTTP 429)", AppTheme.ACCENT_AMBER, Color.BLACK);

        startServerBtn.addActionListener(e -> {
            try {
                int port = restServer.start();
                serverStatusLabel.setText("Embedded HTTP REST Server: RUNNING on http://127.0.0.1:" + port);
                serverStatusLabel.setForeground(AppTheme.ACCENT_GREEN);
                enterpriseOutputArea.setForeground(AppTheme.ACCENT_GREEN);
                enterpriseOutputArea.setText("Server bound on http://127.0.0.1:" + port
                        + "\nClick 'GET /api/v1/health' or 'GET /api/v1/skills' to invoke real Java 17 HttpClient requests!");
            } catch (Exception ex) {
                enterpriseOutputArea.setForeground(AppTheme.ACCENT_RED);
                enterpriseOutputArea.setText("Failed to start server: " + ex.getMessage());
            }
        });

        stopServerBtn.addActionListener(e -> {
            restServer.stop();
            serverStatusLabel.setText("Embedded HTTP REST Server: STOPPED");
            serverStatusLabel.setForeground(AppTheme.ACCENT_AMBER);
            enterpriseOutputArea.setForeground(AppTheme.FG_MUTED);
            enterpriseOutputArea.setText("REST server stopped cleanly.");
        });

        healthBtn.addActionListener(e -> callRestEndpointAsync("/api/v1/health"));
        skillsApiBtn.addActionListener(e -> callRestEndpointAsync("/api/v1/skills"));
        progressApiBtn.addActionListener(e -> callRestEndpointAsync("/api/v1/progress"));

        burstBtn.addActionListener(e -> runBurstSimulationAsync());

        restBtns.add(startServerBtn);
        restBtns.add(healthBtn);
        restBtns.add(skillsApiBtn);
        restBtns.add(progressApiBtn);
        restBtns.add(burstBtn);
        restBtns.add(stopServerBtn);

        restCard.add(serverStatusLabel, BorderLayout.NORTH);
        restCard.add(restBtns, BorderLayout.CENTER);

        // Row 2: CompletableFuture + Strategy/Builder + Reflection @EnterpriseSkill Scanner
        JPanel patternsCard = new JPanel(new BorderLayout(8, 6));
        patternsCard.setBackground(AppTheme.BG_CARD);
        patternsCard.setBorder(AppTheme.cardBorder());

        JLabel patternsTitle = new JLabel("Enterprise Concurrency, GoF Design Patterns, Sealed Outcomes & Reflection Scanner");
        patternsTitle.setFont(AppTheme.FONT_UI_BOLD);
        patternsTitle.setForeground(Color.WHITE);

        JPanel patternBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        patternBtns.setOpaque(false);

        JButton asyncOkBtn = AppTheme.createButton("Run Parallel CompletableFuture Pipeline", AppTheme.ACCENT_PURPLE, Color.WHITE);
        JButton asyncFallbackBtn = AppTheme.createGhostButton("Simulate 503 Fault -> Resilient Fallback");
        JButton scanSkillsBtn = AppTheme.createButton("Scan @EnterpriseSkill Annotations (Reflection)", AppTheme.ACCENT_BLUE, Color.WHITE);

        asyncOkBtn.addActionListener(e -> runAsyncPipelineDemo(false));
        asyncFallbackBtn.addActionListener(e -> runAsyncPipelineDemo(true));
        scanSkillsBtn.addActionListener(e -> runReflectionSkillScan());

        patternBtns.add(asyncOkBtn);
        patternBtns.add(asyncFallbackBtn);
        patternBtns.add(scanSkillsBtn);

        patternsCard.add(patternsTitle, BorderLayout.NORTH);
        patternsCard.add(patternBtns, BorderLayout.CENTER);

        controlsWrapper.add(restCard);
        controlsWrapper.add(patternsCard);

        // Split bottom: Left = Live Response / Inspector, Right = Real-Time Pub/Sub Audit Stream
        enterpriseOutputArea = new JTextArea();
        enterpriseOutputArea.setEditable(false);
        enterpriseOutputArea.setFont(AppTheme.FONT_CONSOLE);
        enterpriseOutputArea.setBackground(AppTheme.BG_CONSOLE);
        enterpriseOutputArea.setForeground(AppTheme.FG_PRIMARY);
        enterpriseOutputArea.setMargin(new java.awt.Insets(10, 12, 10, 12));
        enterpriseOutputArea.setText("""
                Welcome to the Enterprise Java 17 Architecture Lab!
                Everything on this tab runs real production-style JDK 17 components implemented in `javagrind.enterprise`:
                  1. Start the Embedded HTTP REST Server -> send live Java 17 HttpClient GET requests.
                  2. Trigger an 8-request burst -> watch TokenBucketRateLimiter throttle excess calls with HTTP 429!
                  3. Run the 3-service parallel CompletableFuture pipeline (Strategy + Builder + Sealed Outcome).
                  4. Run the Reflection Scanner -> inspect @EnterpriseSkill runtime annotations across the codebase!""");

        auditStreamArea = new JTextArea();
        auditStreamArea.setEditable(false);
        auditStreamArea.setFont(AppTheme.FONT_CONSOLE);
        auditStreamArea.setBackground(AppTheme.BG_CONSOLE);
        auditStreamArea.setForeground(AppTheme.ACCENT_GREEN);
        auditStreamArea.setMargin(new java.awt.Insets(10, 12, 10, 12));

        JPanel leftBox = new JPanel(new BorderLayout(0, 4));
        leftBox.setOpaque(false);
        JLabel outLbl = new JLabel("Live HTTP / Pipeline / Reflection Inspector:");
        outLbl.setFont(AppTheme.FONT_UI_BOLD);
        leftBox.add(outLbl, BorderLayout.NORTH);
        leftBox.add(AppTheme.wrapScroll(enterpriseOutputArea), BorderLayout.CENTER);

        JPanel rightBox = new JPanel(new BorderLayout(0, 4));
        rightBox.setOpaque(false);
        JLabel auditLbl = new JLabel("Real-Time Observer Event Bus (AuditEventBus + java.time.Instant):");
        auditLbl.setFont(AppTheme.FONT_UI_BOLD);
        rightBox.add(auditLbl, BorderLayout.NORTH);
        rightBox.add(AppTheme.wrapScroll(auditStreamArea), BorderLayout.CENTER);

        JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftBox, rightBox);
        bottomSplit.setResizeWeight(0.55);
        bottomSplit.setDividerSize(6);

        outer.add(controlsWrapper, BorderLayout.NORTH);
        outer.add(bottomSplit, BorderLayout.CENTER);
        refreshAuditStream();
        return outer;
    }

    private void callRestEndpointAsync(String path) {
        new SwingWorker<ExecutionOutcome<String>, Void>() {
            @Override
            protected ExecutionOutcome<String> doInBackground() throws Exception {
                if (!restServer.isRunning()) {
                    int port = restServer.start();
                    SwingUtilities.invokeLater(() -> {
                        serverStatusLabel.setText("Embedded HTTP REST Server: RUNNING on http://127.0.0.1:" + port);
                        serverStatusLabel.setForeground(AppTheme.ACCENT_GREEN);
                    });
                }
                return restServer.invokeLocalGet(path);
            }

            @Override
            protected void done() {
                try {
                    ExecutionOutcome<String> outcome = get();
                    outcome.fold(
                            ok -> {
                                enterpriseOutputArea.setForeground(AppTheme.ACCENT_GREEN);
                                enterpriseOutputArea.setText(ok.value());
                                return null;
                            },
                            err -> {
                                enterpriseOutputArea.setForeground(AppTheme.ACCENT_RED);
                                enterpriseOutputArea.setText("ERROR [" + err.errorCode() + "]: " + err.message());
                                return null;
                            },
                            throttled -> {
                                enterpriseOutputArea.setForeground(AppTheme.ACCENT_AMBER);
                                enterpriseOutputArea.setText("HTTP 429 TOO MANY REQUESTS — Client '" + throttled.clientId()
                                        + "' rate-limited! Wait ~" + throttled.retryAfterMs() + " ms for token refill.");
                                return null;
                            }
                    );
                    enterpriseOutputArea.setCaretPosition(0);
                } catch (Exception ex) {
                    enterpriseOutputArea.setForeground(AppTheme.ACCENT_RED);
                    enterpriseOutputArea.setText("Request failed: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void runBurstSimulationAsync() {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                int port = restServer.start();
                SwingUtilities.invokeLater(() -> {
                    serverStatusLabel.setText("Embedded HTTP REST Server: RUNNING on http://127.0.0.1:" + port);
                    serverStatusLabel.setForeground(AppTheme.ACCENT_GREEN);
                });
                restServer.resetRateLimiter();
                StringBuilder sb = new StringBuilder("FIRING 8 RAPID CONCURRENT-BURST REQUESTS (Token Bucket Capacity = 5):\n");
                sb.append("----------------------------------------------------------------------\n");
                for (int i = 1; i <= 8; i++) {
                    ExecutionOutcome<String> out = restServer.invokeLocalGet("/api/v1/health");
                    int reqNum = i;
                    out.fold(
                            ok -> sb.append(String.format("  Request #%d -> HTTP 200 OK (%d ms)%n", reqNum, ok.latencyMs())),
                            err -> sb.append(String.format("  Request #%d -> ERROR %s%n", reqNum, err.message())),
                            lim -> sb.append(String.format("  Request #%d -> HTTP 429 THROTTLED by TokenBucketRateLimiter!%n", reqNum))
                    );
                }
                return sb.toString();
            }

            @Override
            protected void done() {
                try {
                    enterpriseOutputArea.setForeground(AppTheme.ACCENT_AMBER);
                    enterpriseOutputArea.setText(get());
                } catch (Exception ex) {
                    enterpriseOutputArea.setText("Burst error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void runAsyncPipelineDemo(boolean simulateFault) {
        enterpriseOutputArea.setForeground(AppTheme.FG_MUTED);
        enterpriseOutputArea.setText("Running 3 parallel CompletableFuture tasks (Catalog + Tax + Inventory)...");
        new SwingWorker<ExecutionOutcome<AsyncPipelineOrchestrator.CheckoutQuote>, Void>() {
            @Override
            protected ExecutionOutcome<AsyncPipelineOrchestrator.CheckoutQuote> doInBackground() {
                return AsyncPipelineOrchestrator.executeParallelCheckout(
                        "JAVA-ENT-PRO",
                        AsyncPipelineOrchestrator.DiscountStrategy.enterpriseTier(),
                        simulateFault
                );
            }

            @Override
            protected void done() {
                try {
                    ExecutionOutcome<AsyncPipelineOrchestrator.CheckoutQuote> out = get();
                    out.fold(
                            ok -> {
                                AuditEventBus.getInstance().publish("AsyncPipeline",
                                        simulateFault ? "FALLBACK_RECOVERED" : "PARALLEL_SUCCESS",
                                        ok.value().summaryText());
                                enterpriseOutputArea.setForeground(AppTheme.ACCENT_GREEN);
                                enterpriseOutputArea.setText(
                                        "PARALLEL COMPLETABLEFUTURE ORCHESTRATION SUCCEEDED:\n"
                                        + "----------------------------------------------------------------------\n"
                                        + "  " + ok.value().summaryText() + "\n\n"
                                        + "Patterns Combined:\n"
                                        + "  - CompletableFuture.supplyAsync + thenCombine + orTimeout + exceptionally\n"
                                        + "  - DiscountStrategy (Functional Strategy Pattern: 18% Enterprise Tier)\n"
                                        + "  - CheckoutQuote.Builder (Fluent Immutable Builder Pattern)\n"
                                        + "  - ExecutionOutcome<T> (Java 17 Sealed Result Hierarchy)"
                                );
                                return null;
                            },
                            err -> {
                                enterpriseOutputArea.setForeground(AppTheme.ACCENT_RED);
                                enterpriseOutputArea.setText("Pipeline failed: " + err.message());
                                return null;
                            },
                            lim -> null
                    );
                } catch (Exception ex) {
                    enterpriseOutputArea.setText("Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void runReflectionSkillScan() {
        List<SkillRegistryScanner.DiscoveredSkill> skills = SkillRegistryScanner.scanAll();
        AuditEventBus.getInstance().publish("ReflectionScan", "ANNOTATIONS_READ",
                "Discovered " + skills.size() + " @EnterpriseSkill components");
        StringBuilder sb = new StringBuilder();
        sb.append("RUNTIME REFLECTION METADATA SCAN (@EnterpriseSkill):\n");
        sb.append("Discovered ").append(skills.size()).append(" annotated subsystems in loaded bytecode:\n");
        sb.append("----------------------------------------------------------------------\n");
        for (SkillRegistryScanner.DiscoveredSkill s : skills) {
            sb.append("  [").append(s.category()).append("]\n")
              .append("    Component : ").append(s.implementingClass()).append(" — ").append(s.name()).append('\n')
              .append("    Summary   : ").append(s.summary()).append("\n\n");
        }
        enterpriseOutputArea.setForeground(AppTheme.FG_PRIMARY);
        enterpriseOutputArea.setText(sb.toString());
        enterpriseOutputArea.setCaretPosition(0);
    }

    private void refreshAuditStream() {
        if (auditStreamArea == null) return;
        StringBuilder sb = new StringBuilder();
        for (AuditEventBus.AuditEvent ev : AuditEventBus.getInstance().recentEvents()) {
            sb.append(ev.formattedLine()).append('\n');
        }
        auditStreamArea.setText(sb.toString());
        auditStreamArea.setCaretPosition(0);
    }

    private String buildLiveProgressJson() {
        long lessonsDone = CurriculumData.LESSONS.stream().filter(l -> store.isLessonDone(l.id())).count();
        long problemsDone = ProblemBank.PROBLEMS.stream().filter(p -> store.isProblemSolved(p.id())).count();
        long quizzesDone = CurriculumData.QUIZZES.stream().filter(q -> store.isQuizAnsweredCorrectly(q.id())).count();
        var cs = restServer.cacheStats();
        return String.format("""
                {
                  "lessonsCompleted": %d,
                  "lessonsTotal": %d,
                  "problemsSolved": %d,
                  "problemsTotal": %d,
                  "quizzesCorrect": %d,
                  "quizzesTotal": %d,
                  "lruCacheHitRatioPercent": %.1f
                }""",
                lessonsDone, CurriculumData.LESSONS.size(),
                problemsDone, ProblemBank.PROBLEMS.size(),
                quizzesDone, CurriculumData.QUIZZES.size(),
                cs.hitRatioPercent()
        );
    }

    // ==================== ENTRYPOINT ====================

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            AppTheme.installLookAndFeel();
            JavaGrindApp app = new JavaGrindApp();
            app.setVisible(true);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
