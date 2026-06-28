import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;

/**
 * CipherSight – Cybersecurity Awareness Learning Desktop App
 * Tabbed CardLayout UI. Red accent. Custom drawn logos on every module.
 */
public class CipherSight extends JFrame {

    // ── Palette (RED accent) ──────────────────────────────────────────────────
    static final Color BG = new Color(0x0F, 0x0A, 0x14);
    static final Color SIDEBAR_BG = new Color(0x18, 0x0E, 0x22);
    static final Color CARD_BG = new Color(0x22, 0x16, 0x30);
    static final Color CARD_LINE = new Color(0x3A, 0x28, 0x4E);
    static final Color TEXT = new Color(0xF2, 0xEC, 0xFF);
    static final Color TEXT_DIM = new Color(0x9A, 0x8C, 0xB4);
    static final Color ACCENT = new Color(0xFF, 0x3A, 0x3A);   // red
    static final Color ACCENT_DK = new Color(0xBB, 0x1A, 0x1A);   // dark red
    static final Color ACCENT_GL = new Color(0xFF, 0x7A, 0x7A);   // light red glow
    static final Color PURPLE = new Color(0xBB, 0x96, 0xEE);
    static final Color WARN = new Color(0xFF, 0xC8, 0x50);
    static final Color GREEN = new Color(0x5E, 0xF0, 0xA8);

    // ── Module accent colours ─────────────────────────────────────────────────
    static final Color C_CAESAR = new Color(0xFF, 0x3A, 0x3A);
    static final Color C_HASH = new Color(0xBB, 0x96, 0xEE);
    static final Color C_AVAL = new Color(0x5E, 0xF0, 0xA8);
    static final Color C_HIDDEN = new Color(0xFF, 0xC8, 0x50);
    static final Color C_HOMO = new Color(0xFF, 0x7A, 0x3A);
    static final Color C_ABOUT = new Color(0xFF, 0x3A, 0x3A);

    // ── State ─────────────────────────────────────────────────────────────────
    private final Set<String> done = new LinkedHashSet<>();
    private JLabel doneCountLbl;
    private JProgressBar dashBar;
    private JLabel dashBarLbl;
    private CardLayout cards;
    private JPanel cardHolder;
    private final List<SideItem> sideItems = new ArrayList<>();

    // =========================================================================
    //  MAIN
    // =========================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new CipherSight().setVisible(true);
        });
    }

    public CipherSight() {
        super("CipherSight  –  Cybersecurity Awareness Platform");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 840);
        setMinimumSize(new Dimension(1050, 700));
        setLocationRelativeTo(null);

        JPanel root = solidBg(new JPanel(new BorderLayout()), BG);
        root.add(buildSidebar(), BorderLayout.WEST);

        cards = new CardLayout();
        cardHolder = solidBg(new JPanel(cards), BG);
        cardHolder.add(buildDashboard(), "Dashboard");
        cardHolder.add(buildCaesarPanel(), "Caesar Cipher");
        cardHolder.add(buildHashPanel(), "SHA-256 Hash");
        cardHolder.add(buildAvalanchePanel(), "Avalanche Effect");
        cardHolder.add(buildHiddenPanel(), "Hidden Characters");
        cardHolder.add(buildHomographPanel(), "Homograph Attack");
        cardHolder.add(buildAboutPanel(), "About");

        root.add(cardHolder, BorderLayout.CENTER);
        setContentPane(root);
        showCard("Dashboard");
    }

    // =========================================================================
    //  SIDEBAR
    // =========================================================================
    private JPanel buildSidebar() {
        JPanel sb = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                // right border line
                g.setColor(CARD_LINE);
                g.fillRect(getWidth() - 1, 0, 1, getHeight());
            }
        };
        sb.setOpaque(false);
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setPreferredSize(new Dimension(248, 800));

        // ── App logo row ──
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        logoRow.setOpaque(false);
        logoRow.setBorder(new EmptyBorder(24, 18, 18, 18));
        logoRow.setAlignmentX(LEFT_ALIGNMENT);
        logoRow.add(new AppLogoIcon(40));
        JPanel wm = col();
        wm.add(lbl("CipherSight", TEXT, 17, Font.BOLD));
        wm.add(lbl("Security Learning", TEXT_DIM, 11, Font.PLAIN));
        logoRow.add(wm);
        sb.add(logoRow);
        sb.add(hRule());
        sb.add(gap(6));

        JLabel navHdr = lbl("  MODULES", TEXT_DIM, 10, Font.BOLD);
        navHdr.setAlignmentX(LEFT_ALIGNMENT);
        sb.add(navHdr);
        sb.add(gap(4));

        // icon  title            subtitle           card-key
        Object[][] nav = {
                {new DashLogoIcon(20), "Dashboard", "Overview & progress", "Dashboard"},
                {new CaesarLogoIcon(20), "Caesar Cipher", "Shift encryption", "Caesar Cipher"},
                {new HashLogoIcon(20), "SHA-256 Hash", "One-way fingerprints", "SHA-256 Hash"},
                {new AvalLogoIcon(20), "Avalanche Effect", "Small change, big hash", "Avalanche Effect"},
                {new HiddenLogoIcon(20), "Hidden Chars", "Invisible Unicode", "Hidden Characters"},
                {new HomoLogoIcon(20), "Homograph Attack", "Look-alike letters", "Homograph Attack"},
                {new AboutLogoIcon(20), "About", "Developer info", "About"},
        };
        for (Object[] n : nav) {
            SideItem item = new SideItem((JComponent) n[0], (String) n[1], (String) n[2]);
            String key = (String) n[3];
            item.addActionListener(e -> showCard(key));
            sideItems.add(item);
            sb.add(item);
            sb.add(gap(2));
        }
        sb.add(Box.createVerticalGlue());
        return sb;
    }

    private void showCard(String key) {
        cards.show(cardHolder, key);
        // match by title or key
        for (SideItem s : sideItems)
            s.setActive(s.title.equals(key) || key.startsWith(s.title));
    }

    // =========================================================================
    //  DASHBOARD
    // =========================================================================
    private JScrollPane buildDashboard() {
        JPanel page = scrollPage();

        // ── Header with logo ──
        page.add(moduleHeader(new DashLogoIcon(52), "Dashboard",
                "Your learning overview and quick module launcher.", C_CAESAR));
        page.add(gap(4));

        // ── Stat cards ──
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(LEFT_ALIGNMENT);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        doneCountLbl = new JLabel("0");
        doneCountLbl.setForeground(ACCENT);
        doneCountLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        stats.add(statCard("5", "Modules Available", null));
        stats.add(statCard("0", "Completed", doneCountLbl));
        stats.add(statCard("100%", "Offline & Safe", null));
        page.add(stats);
        page.add(gap(16));

        // ── Progress ──
        JPanel pc = card();
        pc.add(lbl("Your Progress", TEXT, 15, Font.BOLD));
        pc.add(gap(8));
        dashBar = new JProgressBar(0, 5);
        dashBar.setValue(0);
        dashBar.setForeground(ACCENT);
        dashBar.setBackground(new Color(255, 255, 255, 18));
        dashBar.setBorderPainted(false);
        dashBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));
        dashBar.setAlignmentX(LEFT_ALIGNMENT);
        dashBarLbl = lbl("0 / 5 modules completed", ACCENT, 13, Font.PLAIN);
        dashBarLbl.setAlignmentX(LEFT_ALIGNMENT);
        pc.add(dashBar);
        pc.add(gap(6));
        pc.add(dashBarLbl);
        page.add(pc);
        page.add(gap(16));

        // ── Module tiles with logos ──
        JPanel grid = new JPanel(new GridLayout(2, 3, 18, 18));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 290));

        Object[][] tiles = {
                {new CaesarLogoIcon(38), "Caesar Cipher", "Caesar Cipher", C_CAESAR},
                {new HashLogoIcon(38), "SHA-256 Hash", "SHA-256 Hash", C_HASH},
                {new AvalLogoIcon(38), "Avalanche Effect", "Avalanche Effect", C_AVAL},
                {new HiddenLogoIcon(38), "Hidden Chars", "Hidden Characters", C_HIDDEN},
                {new HomoLogoIcon(38), "Homograph Attack", "Homograph Attack", C_HOMO},
                {new AboutLogoIcon(38), "About Developer", "About", C_ABOUT},
        };
        for (Object[] t : tiles)
            grid.add(dashTile((JComponent) t[0], (String) t[1], (String) t[2], (Color) t[3]));
        page.add(grid);
        page.add(gap(16));

        // ── Learning path ──
        JPanel lp = card();
        lp.add(lbl("Recommended Learning Path", TEXT, 15, Font.BOLD));
        lp.add(gap(8));
        String[] steps = {
                "1.  Caesar Cipher       — understand basic letter-shift encryption",
                "2.  SHA-256 Hash        — see one-way cryptographic fingerprints",
                "3.  Avalanche Effect    — tiny input change, completely different hash",
                "4.  Hidden Characters   — detect invisible Unicode in plain text",
                "5.  Homograph Attack    — spot look-alike phishing letters",
        };
        for (String s : steps) {
            JLabel sl = lbl(s, TEXT_DIM, 13, Font.PLAIN);
            sl.setBorder(new EmptyBorder(3, 0, 3, 0));
            lp.add(sl);
        }
        page.add(lp);
        return wrap(page);
    }

    private JPanel dashTile(JComponent logo, String label, String target, Color col) {
        JPanel tile = new JPanel() {
            boolean hov = false;

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        hov = true;
                        repaint();
                    }

                    public void mouseExited(MouseEvent e) {
                        hov = false;
                        repaint();
                    }

                    public void mouseClicked(MouseEvent e) {
                        showCard(target);
                    }
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? new Color(col.getRed(), col.getGreen(), col.getBlue(), 30) : CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(col);
                g2.setStroke(new BasicStroke(1.6f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 18, 18);
                g2.dispose();
            }
        };
        tile.setOpaque(false);
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBorder(new EmptyBorder(16, 18, 16, 18));

        // logo circle background
        JPanel logoWrap = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 45));
                g2.fillOval(0, 0, 50, 50);
                g2.dispose();
            }
        };
        logoWrap.setOpaque(false);
        logoWrap.setPreferredSize(new Dimension(50, 50));
        logoWrap.setMaximumSize(new Dimension(50, 50));
        logoWrap.setMinimumSize(new Dimension(50, 50));
        logoWrap.setLayout(new GridBagLayout());
        logoWrap.add(logo);
        logoWrap.setAlignmentX(LEFT_ALIGNMENT);
        tile.add(logoWrap);
        tile.add(gap(10));

        for (String line : label.split("\n")) {
            JLabel tl = lbl(line, TEXT, 14, Font.BOLD);
            tl.setAlignmentX(LEFT_ALIGNMENT);
            tile.add(tl);
        }
        return tile;
    }

    // =========================================================================
    //  MODULE 1 – CAESAR CIPHER
    // =========================================================================
    private JScrollPane buildCaesarPanel() {
        JPanel page = scrollPage();
        page.add(moduleHeader(new CaesarLogoIcon(52), "Caesar Cipher Visualizer",
                "This Module helps users learn how the Caesar Cipher works by showing how each letter is shifted during encryption and decryption.\n", C_CAESAR));

        JPanel demo = card();
        demo.add(lbl("Try it yourself", TEXT, 15, Font.BOLD));
        demo.add(gap(10));

        JTextField input = field("Type a message, e.g.  HELLO WORLD");
        input.setAlignmentX(LEFT_ALIGNMENT);
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        demo.add(input);
        demo.add(gap(10));

        JPanel shiftRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        shiftRow.setOpaque(false);
        shiftRow.setAlignmentX(LEFT_ALIGNMENT);
        shiftRow.add(lbl("Shift Key:", TEXT, 13, Font.PLAIN));
        JSpinner spin = new JSpinner(new SpinnerNumberModel(3, 1, 25, 1));
        spin.setPreferredSize(new Dimension(64, 30));
        shiftRow.add(spin);
        demo.add(shiftRow);
        demo.add(gap(10));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        JButton encBtn = btn("  Encrypt  ");
        JButton decBtn = btn("  Decrypt  ");
        btnRow.add(encBtn);
        btnRow.add(decBtn);
        demo.add(btnRow);
        demo.add(gap(12));

        JLabel vizLbl = lbl("Character-by-character output:", TEXT_DIM, 12, Font.PLAIN);
        vizLbl.setAlignmentX(LEFT_ALIGNMENT);
        demo.add(vizLbl);
        demo.add(gap(6));

        JPanel charsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        charsRow.setOpaque(false);
        charsRow.setAlignmentX(LEFT_ALIGNMENT);
        demo.add(charsRow);
        demo.add(gap(8));

        JLabel resultLbl = lbl("", ACCENT, 14, Font.BOLD);
        resultLbl.setAlignmentX(LEFT_ALIGNMENT);
        demo.add(resultLbl);
        page.add(demo);
        page.add(gap(16));

        encBtn.addActionListener(e -> doCaesar(input.getText(), (int) spin.getValue(), true, charsRow, resultLbl));
        decBtn.addActionListener(e -> doCaesar(input.getText(), (int) spin.getValue(), false, charsRow, resultLbl));

        page.add(infoCard("What is Caesar Cipher?",
                "The Caesar Cipher is one of the oldest and easiest encryption methods. It works by shifting each letter in the message by a fixed number of positions in the alphabet. The same shift value is used to encrypt and decrypt the message. It is simple to understand and is mainly used for learning basic cryptography concepts."));
        page.add(gap(10));
        page.add(infoCard("Why is it important?",
                "The Caesar Cipher is important because it introduces the basic idea of encryption in a simple way. It helps students understand how secret messages can be created and recovered using a key. Although it is not secure today, it is a great starting point for learning how modern encryption methods have developed."));
        page.add(gap(10));
        page.add(infoCard("Limitations",
                "Not Secured because it has only 25 possible shift keys exist. Any attacker can try all of them in under a second " +
                        "(brute force). Letter frequency analysis can also crack it instantly."));
        page.add(gap(10));
        page.add(infoCard("Best Practices",
                "Use Caesar Cipher only as a teaching tool. For real data protection use modern " +
                        "algorithms such as AES-256 with proper key management."));
        return wrap(page);
    }

    private void doCaesar(String text, int shift, boolean enc, JPanel row, JLabel result) {
        row.removeAll();
        if (text == null || text.trim().isEmpty()) {
            result.setText("Please type something first.");
            row.revalidate();
            row.repaint();
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            char s = shiftChar(c, enc ? shift : -shift);
            sb.append(s);
            JLabel cell = new JLabel(String.valueOf(s));
            cell.setBackground(new Color(0x30, 0x18, 0x18));
            cell.setForeground(ACCENT_GL);
            cell.setFont(new Font("Consolas", Font.BOLD, 15));
            cell.setBorder(new CompoundBorder(
                    new LineBorder(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 100), 1, true),
                    new EmptyBorder(5, 9, 5, 9)));
            cell.setOpaque(true);
            row.add(cell);
        }
        result.setText("Result: " + sb);
        row.revalidate();
        row.repaint();
        markDone("Caesar Cipher");
    }

    private char shiftChar(char c, int sh) {
        if (Character.isUpperCase(c)) return (char) ('A' + (((c - 'A') + sh) % 26 + 26) % 26);
        if (Character.isLowerCase(c)) return (char) ('a' + (((c - 'a') + sh) % 26 + 26) % 26);
        return c;
    }

    // =========================================================================
    //  MODULE 2 – SHA-256 HASH
    // =========================================================================
    private JScrollPane buildHashPanel() {
        JPanel page = scrollPage();
        page.add(moduleHeader(new HashLogoIcon(52), "SHA-256 Hash Generator",
                "This Module helps users understand how hashing works by converting any input into a fixed-length hash value for verification.", C_HASH));

        JPanel demo = card();
        demo.add(lbl("Try it yourself", TEXT, 15, Font.BOLD));
        demo.add(gap(10));

        JTextField input = field("Type any text, e.g.  MyPassword123");
        input.setAlignmentX(LEFT_ALIGNMENT);
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        demo.add(input);
        demo.add(gap(10));

        JButton go = btn("  Generate SHA-256 Hash  ");
        go.setAlignmentX(LEFT_ALIGNMENT);
        demo.add(go);
        demo.add(gap(14));

        demo.add(lbl("Generated hash:", TEXT_DIM, 12, Font.PLAIN));
        demo.add(gap(4));

        JTextArea out = monoArea(ACCENT);
        out.setText("Hash will appear here…");
        JScrollPane osp = scrollOf(out, 68);
        demo.add(osp);
        page.add(demo);
        page.add(gap(16));

        go.addActionListener(e -> {
            out.setText(sha256(input.getText()));
            markDone("SHA-256 Hash");
        });

        page.add(infoCard("What is SHA-256?",
                "SHA-256 is a hashing algorithm that converts any text or data into a unique 64-character hash value. Unlike encryption, hashing cannot be reversed to get the original text. Even a small change in the input creates a completely different hash. It is widely used to verify data integrity and protect digital information."));
        page.add(gap(10));
        page.add(infoCard("Why is it one-way?",
                "The mathematical operations (bit shifts, rotations, XOR, modular addition) " +
                        "are easy to compute forward but practically impossible to reverse with current technology."));
        page.add(gap(10));
        page.add(infoCard("Real-world uses",
                "File integrity checks after downloads, password storage in databases " +
                        "(server stores the hash, never the actual password), and blockchain " +
                        "(each Bitcoin block contains the hash of the previous block)."));
        page.add(gap(10));
        page.add(infoCard("Best Practices",
                "Never use raw SHA-256 alone for passwords. Always add a unique random salt " +
                        "and use a purpose-built slow algorithm like bcrypt or Argon2, " +
                        "which are deliberately slow to resist brute-force guessing."));
        return wrap(page);
    }

    private String sha256(String text) {
        if (text == null || text.isEmpty()) return "Please type some text first.";
        try {
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            byte[] b = d.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }
    }

    // =========================================================================
    //  MODULE 3 – AVALANCHE EFFECT
    // =========================================================================
    private JScrollPane buildAvalanchePanel() {
        JPanel page = scrollPage();
        page.add(moduleHeader(new AvalLogoIcon(52), "Avalanche Effect Demonstrator",
                "See how one tiny change scrambles the entire SHA-256 hash.", C_AVAL));

        JPanel demo = card();
        demo.add(lbl("Enter two similar texts to compare", TEXT, 15, Font.BOLD));
        demo.add(gap(10));

        demo.add(lbl("Original text:", TEXT_DIM, 12, Font.PLAIN));
        JTextField tf1 = field("e.g.  Password123");
        tf1.setText("Password123");
        tf1.setAlignmentX(LEFT_ALIGNMENT);
        tf1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        demo.add(tf1);
        demo.add(gap(10));

        demo.add(lbl("Modified text:", TEXT_DIM, 12, Font.PLAIN));
        JTextField tf2 = field("e.g.  Password124");
        tf2.setText("Password124");
        tf2.setAlignmentX(LEFT_ALIGNMENT);
        tf2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        demo.add(tf2);
        demo.add(gap(10));

        JButton go = btn("  Compare Hashes  ");
        go.setAlignmentX(LEFT_ALIGNMENT);
        demo.add(go);
        demo.add(gap(14));

        JTextArea h1 = monoArea(ACCENT_GL);
        JTextArea h2 = monoArea(ACCENT_GL);
        demo.add(lbl("Original hash:", TEXT_DIM, 12, Font.PLAIN));
        demo.add(scrollOf(h1, 58));
        demo.add(gap(8));
        demo.add(lbl("Modified hash:", TEXT_DIM, 12, Font.PLAIN));
        demo.add(scrollOf(h2, 58));
        demo.add(gap(14));

        JLabel diffLbl = lbl("Difference: —", ACCENT, 14, Font.BOLD);
        diffLbl.setAlignmentX(LEFT_ALIGNMENT);
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setForeground(GREEN);
        bar.setBackground(new Color(255, 255, 255, 18));
        bar.setBorderPainted(false);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        bar.setAlignmentX(LEFT_ALIGNMENT);
        demo.add(diffLbl);
        demo.add(gap(6));
        demo.add(bar);
        page.add(demo);
        page.add(gap(16));

        go.addActionListener(e -> {
            String s1 = sha256(tf1.getText()), s2 = sha256(tf2.getText());
            h1.setText(s1);
            h2.setText(s2);
            int diff = 0, len = Math.min(s1.length(), s2.length());
            for (int i = 0; i < len; i++) if (s1.charAt(i) != s2.charAt(i)) diff++;
            int pct = len == 0 ? 0 : (int) (diff * 100.0 / len);
            diffLbl.setText(String.format("Difference: %d%% of hash characters changed  (%d / %d chars)", pct, diff, len));
            bar.setValue(pct);
            markDone("Avalanche Effect");
        });

        page.add(infoCard("What is the Avalanche Effect?",
                "A property of well-designed hash functions: changing even a single character " +
                        "in the input causes roughly 50% of the output bits to change unpredictably."));
        page.add(gap(10));
        page.add(infoCard("Why does it matter?",
                "It makes hash functions unpredictable — you cannot reverse-engineer the input " +
                        "by tweaking hashes. It also means any corruption or tampering is instantly detectable."));
        page.add(gap(10));
        page.add(infoCard("Practical example",
                "Linux/macOS file downloads publish SHA-256 checksums. If even one byte is " +
                        "corrupted during download, the checksum changes completely, immediately revealing " +
                        "the corruption."));
        return wrap(page);
    }

    // =========================================================================
    //  MODULE 4 – HIDDEN CHARACTERS
    // =========================================================================
    private static final Map<Integer, String> INVIS = new LinkedHashMap<>();

    static {
        INVIS.put(0x200B, "Zero Width Space (ZWSP)");
        INVIS.put(0x200C, "Zero Width Non-Joiner (ZWNJ)");
        INVIS.put(0x200D, "Zero Width Joiner (ZWJ)");
        INVIS.put(0xFEFF, "Zero Width No-Break Space / BOM");
        INVIS.put(0x2060, "Word Joiner");
        INVIS.put(0x180E, "Mongolian Vowel Separator");
        INVIS.put(0x00AD, "Soft Hyphen");
        INVIS.put(0x2028, "Line Separator");
        INVIS.put(0x2029, "Paragraph Separator");
    }

    private JScrollPane buildHiddenPanel() {
        JPanel page = scrollPage();
        page.add(moduleHeader(new HiddenLogoIcon(52), "Hidden Character Detector",
                "Find invisible Unicode characters hiding inside plain text.", C_HIDDEN));

        JPanel demo = card();
        demo.add(lbl("Paste or type text to scan", TEXT, 15, Font.BOLD));
        demo.add(gap(10));

        JTextArea inputArea = new JTextArea(4, 40);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBackground(new Color(0x14, 0x0C, 0x20));
        inputArea.setForeground(TEXT);
        inputArea.setCaretColor(TEXT);
        inputArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane isp = new JScrollPane(inputArea);
        isp.setBorder(new LineBorder(CARD_LINE, 1, true));
        isp.setAlignmentX(LEFT_ALIGNMENT);
        isp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        demo.add(isp);
        demo.add(gap(10));

        JButton go = btn("  Scan for Hidden Characters  ");
        go.setAlignmentX(LEFT_ALIGNMENT);
        demo.add(go);
        demo.add(gap(14));

        demo.add(lbl("Highlighted result  (red [!] = hidden char found):", TEXT_DIM, 12, Font.PLAIN));
        demo.add(gap(4));
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setBackground(new Color(0x14, 0x0C, 0x20));
        pane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane psp = scrollOf2(pane, 90);
        demo.add(psp);
        demo.add(gap(10));

        demo.add(lbl("Findings:", TEXT_DIM, 12, Font.PLAIN));
        demo.add(gap(4));
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        list.setBackground(new Color(0x14, 0x0C, 0x20));
        list.setForeground(TEXT);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane lsp = new JScrollPane(list);
        lsp.setBorder(new LineBorder(CARD_LINE, 1, true));
        lsp.setAlignmentX(LEFT_ALIGNMENT);
        lsp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        demo.add(lsp);
        page.add(demo);
        page.add(gap(16));

        go.addActionListener(e -> scanHidden(inputArea.getText(), pane, model));

        page.add(infoCard("What are hidden characters?",
                "Unicode includes zero-width and invisible formatting characters " +
                        "(e.g. Zero Width Space U+200B, BOM U+FEFF) that render as nothing on screen " +
                        "but exist in the raw byte data."));
        page.add(gap(10));
        page.add(infoCard("How attackers misuse them",
                "Hidden characters can bypass keyword filters, fingerprint leaked documents, " +
                        "make two visually identical strings technically different, or smuggle " +
                        "data past content moderation systems."));
        page.add(gap(10));
        page.add(infoCard("Best Practices",
                "Use Paste as Plain Text whenever possible. Scan untrusted code, contracts, " +
                        "or messages with a detector before acting on them. " +
                        "Never trust text solely because it looks clean on screen."));
        return wrap(page);
    }

    private void scanHidden(String text, JTextPane pane, DefaultListModel<String> model) {
        model.clear();
        pane.setText("");
        if (text == null) text = "";
        StyledDocument doc = pane.getStyledDocument();
        Style norm = pane.addStyle("n", null);
        StyleConstants.setForeground(norm, TEXT);
        StyleConstants.setFontFamily(norm, "Segoe UI");
        StyleConstants.setFontSize(norm, 13);
        Style flag = pane.addStyle("f", null);
        StyleConstants.setForeground(flag, WARN);
        StyleConstants.setBold(flag, true);
        try {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (INVIS.containsKey((int) c)) {
                    doc.insertString(doc.getLength(), "[!]", flag);
                    model.addElement("Position " + i + ":  " + INVIS.get((int) c)
                            + "  (U+" + String.format("%04X", (int) c) + ")");
                } else {
                    doc.insertString(doc.getLength(), String.valueOf(c), norm);
                }
            }
        } catch (Exception ignored) {
        }
        if (model.isEmpty()) model.addElement("✓  No invisible characters detected — text looks clean.");
        markDone("Hidden Characters");
    }

    // =========================================================================
    //  MODULE 5 – HOMOGRAPH ATTACK
    // =========================================================================
    private JScrollPane buildHomographPanel() {
        JPanel page = scrollPage();
        page.add(moduleHeader(new HomoLogoIcon(52), "Homograph Attack Demonstrator",
                "Spot look-alike letters from Cyrillic, Greek, or other scripts.", C_HOMO));

        JPanel demo = card();
        demo.add(lbl("Try it yourself", TEXT, 15, Font.BOLD));
        demo.add(gap(4));
        demo.add(lbl("Hint: compare  paypal.com  vs  p\u0430ypal.com  (Cyrillic 'a')", TEXT_DIM, 12, Font.PLAIN));
        demo.add(gap(10));

        JTextField input = field("Type or paste a URL or username to analyse");
        input.setAlignmentX(LEFT_ALIGNMENT);
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        demo.add(input);
        demo.add(gap(10));

        JButton go = btn("  Scan for Look-alike Letters  ");
        go.setAlignmentX(LEFT_ALIGNMENT);
        demo.add(go);
        demo.add(gap(14));

        demo.add(lbl("Highlighted result  (red underline = suspicious character):", TEXT_DIM, 12, Font.PLAIN));
        demo.add(gap(4));
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setBackground(new Color(0x14, 0x0C, 0x20));
        pane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane psp = scrollOf2(pane, 90);
        demo.add(psp);
        demo.add(gap(10));

        demo.add(lbl("Findings:", TEXT_DIM, 12, Font.PLAIN));
        demo.add(gap(4));
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        list.setBackground(new Color(0x14, 0x0C, 0x20));
        list.setForeground(TEXT);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane lsp = new JScrollPane(list);
        lsp.setBorder(new LineBorder(CARD_LINE, 1, true));
        lsp.setAlignmentX(LEFT_ALIGNMENT);
        lsp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        demo.add(lsp);
        page.add(demo);
        page.add(gap(16));

        go.addActionListener(e -> scanHomograph(input.getText(), pane, model));

        page.add(infoCard("What is a Homograph Attack?",
                "Attackers register domain names using characters from other Unicode scripts " +
                        "(Cyrillic, Greek) that look visually identical to Latin letters. " +
                        "Example: Cyrillic a (U+0430) looks exactly like Latin a (U+0061)."));
        page.add(gap(10));
        page.add(infoCard("Real-world phishing example",
                "A fake banking site at p\u0430ypal.com (with Cyrillic a) looks identical " +
                        "to paypal.com in most fonts, but it is a completely different address " +
                        "that leads to a phishing server."));
        page.add(gap(10));
        page.add(infoCard("How to stay safe",
                "Always bookmark important sites rather than typing URLs. Modern browsers " +
                        "display punycode (xn--...) for suspicious international domains. " +
                        "Never trust a URL purely because it looks correct at a glance."));
        return wrap(page);
    }

    private void scanHomograph(String text, JTextPane pane, DefaultListModel<String> model) {
        model.clear();
        pane.setText("");
        if (text == null) text = "";
        StyledDocument doc = pane.getStyledDocument();
        Style norm = pane.addStyle("n", null);
        StyleConstants.setForeground(norm, TEXT);
        StyleConstants.setFontFamily(norm, "Segoe UI");
        StyleConstants.setFontSize(norm, 13);
        Style flag = pane.addStyle("f", null);
        StyleConstants.setForeground(flag, ACCENT);
        StyleConstants.setBold(flag, true);
        StyleConstants.setUnderline(flag, true);
        try {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                Character.UnicodeBlock blk = Character.UnicodeBlock.of(c);
                boolean sus = blk == Character.UnicodeBlock.CYRILLIC || blk == Character.UnicodeBlock.GREEK;
                doc.insertString(doc.getLength(), String.valueOf(c), sus ? flag : norm);
                if (sus) model.addElement("Position " + i + ":  '" + c + "'  is "
                        + (blk == Character.UnicodeBlock.CYRILLIC ? "Cyrillic" : "Greek")
                        + " script  (U+" + String.format("%04X", (int) c) + ")  — looks like Latin");
            }
        } catch (Exception ignored) {
        }
        if (model.isEmpty()) model.addElement("✓  No suspicious look-alike characters detected.");
        markDone("Homograph Attack");
    }

    // =========================================================================
    //  ABOUT
    // =========================================================================
    private JScrollPane buildAboutPanel() {
        JPanel page = scrollPage();
        page.add(moduleHeader(new AboutLogoIcon(52), "About the Developer",
                "Who built CipherSight and why.", C_ABOUT));

        // ── Developer profile card ──
        JPanel devCard = card();
        JPanel profileRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 0));
        profileRow.setOpaque(false);
        profileRow.setAlignmentX(LEFT_ALIGNMENT);

        // Avatar (drawn shield / person icon)
        profileRow.add(new DevAvatarIcon(72));

        JPanel info = col();
        info.add(lbl("Paramgiri Goswami", TEXT, 21, Font.BOLD));
        info.add(gap(4));
        info.add(lbl("3rd year Diploma • Computer Science & Engineering", TEXT_DIM, 13, Font.PLAIN));
        info.add(gap(3));
        info.add(lbl("Project created: June 2026", TEXT_DIM, 12, Font.PLAIN));
        info.add(gap(3));
        info.add(lbl("Institution: Government Polytechnic - Bhuj", TEXT_DIM, 12, Font.PLAIN));
        profileRow.add(info);

        devCard.add(profileRow);
        devCard.add(gap(16));
        devCard.add(hLine());
        devCard.add(gap(12));

        JLabel aboutTxt = html(
                "CipherSight was built as a cybersecurity awareness platform for students. " +
                        "It combines 2 ideas - an encryption concepts (Caesar Cipher, SHA-256, Avalanche Effect) " +
                        "with a detection concepts (Hidden Characters, Homograph Attacks) into one " +
                        "ethical, offline Java Swing System. No real attacks are performed — " +
                        "every module is a safe and  educational visualization.", 530, "#F2ECFF", "12");
        aboutTxt.setAlignmentX(LEFT_ALIGNMENT);
        devCard.add(aboutTxt);
        page.add(devCard);
        page.add(gap(16));

        // ── Module logo showcase card ──
        JPanel showcase = card();
        showcase.add(lbl("Module Logos & What They Mean", TEXT, 15, Font.BOLD));
        showcase.add(gap(12));

        Object[][] logos = {
                {new CaesarLogoIcon(36), "Caesar Cipher", C_CAESAR, "Key icon — unlocking letter-shift encryption"},
                {new HashLogoIcon(36), "SHA-256 Hash", C_HASH, "Hash grid — fixed-size digital fingerprint"},
                {new AvalLogoIcon(36), "Avalanche Effect", C_AVAL, "Cascade waves — one change, huge impact"},
                {new HiddenLogoIcon(36), "Hidden Characters", C_HIDDEN, "Eye with slash — invisible Unicode detection"},
                {new HomoLogoIcon(36), "Homograph Attack", C_HOMO, "Warning letters — look-alike character alert"},
        };
        for (Object[] row : logos) {
            JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
            r.setOpaque(false);
            r.setAlignmentX(LEFT_ALIGNMENT);

            JPanel circle = new JPanel() {
                final Color col = (Color) row[2];

                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 50));
                    g2.fillOval(0, 0, 44, 44);
                    g2.dispose();
                }
            };
            circle.setOpaque(false);
            circle.setPreferredSize(new Dimension(44, 44));
            circle.setLayout(new GridBagLayout());
            circle.add((JComponent) row[0]);

            JPanel txt2 = col();
            JLabel nm = lbl((String) row[1], TEXT, 13, Font.BOLD);
            JLabel ds = lbl((String) row[3], TEXT_DIM, 12, Font.PLAIN);
            txt2.add(nm);
            txt2.add(ds);

            r.add(circle);
            r.add(txt2);
            showcase.add(r);
        }
        page.add(showcase);
        page.add(gap(16));

        // ── Tech stack card ──
        JPanel tech = card();
        tech.add(lbl("Technology Stack", TEXT, 15, Font.BOLD));
        tech.add(gap(8));
        String[][] stack = {
                {"Language", "Java 11+"},
                {"UI Library", "Java Swing (javax.swing)"},
                {"Crypto", "java.security.MessageDigest  (SHA-256 built-in)"},
                {"Build", "Single-file:  javac CipherSight.java  &&  java CipherSight"},
                {"Platform", "Cross-platform — Windows, macOS, Linux"},
        };
        for (String[] row : stack) {
            JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            r2.setOpaque(false);
            r2.setAlignmentX(LEFT_ALIGNMENT);
            r2.add(lbl(row[0] + ":  ", PURPLE, 13, Font.BOLD));
            r2.add(lbl(row[1], TEXT_DIM, 13, Font.PLAIN));
            tech.add(r2);
            tech.add(gap(3));
        }
        page.add(tech);
        return wrap(page);
    }

    // =========================================================================
    //  SHARED UI HELPERS
    // =========================================================================

    /**
     * Module page header: big logo circle + title + subtitle
     */
    private JPanel moduleHeader(JComponent logo, String title, String subtitle, Color col) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // logo in coloured circle
        JPanel circle = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // glow
                g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 40));
                g2.fillOval(-4, -4, 76, 76);
                g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 80));
                g2.fillOval(0, 0, 68, 68);
                g2.setColor(col);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawOval(1, 1, 66, 66);
                g2.dispose();
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(68, 68));
        circle.setLayout(new GridBagLayout());
        circle.add(logo);

        JPanel txt = col();
        JLabel t = lbl(title, TEXT, 23, Font.BOLD);
        t.setBorder(new CompoundBorder(
                new MatteBorder(0, 4, 0, 0, col),
                new EmptyBorder(2, 10, 2, 0)));
        JLabel s = lbl(subtitle, TEXT_DIM, 13, Font.PLAIN);
        s.setBorder(new EmptyBorder(4, 16, 0, 0));
        txt.add(t);
        txt.add(s);

        p.add(circle);
        p.add(txt);
        // bottom gap wrapper
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setAlignmentX(LEFT_ALIGNMENT);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        wrap.add(p);
        wrap.add(gap(20));
        return wrap;
    }

    private JPanel card() {
        JPanel c = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(CARD_LINE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(new EmptyBorder(20, 22, 20, 22));
        c.setAlignmentX(LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10000));
        return c;
    }

    private JPanel infoCard(String title, String body) {
        JPanel c = card();
        JLabel tl = lbl(title, ACCENT, 14, Font.BOLD);
        tl.setAlignmentX(LEFT_ALIGNMENT);
        c.add(tl);
        c.add(gap(6));
        JLabel bl = html(body, 560, "#9A8CB4", "11");
        bl.setAlignmentX(LEFT_ALIGNMENT);
        c.add(bl);
        return c;
    }

    private JPanel statCard(String val, String label, JLabel bindTo) {
        JPanel c = card();
        c.setMaximumSize(new Dimension(10000, 108));
        JLabel vl = bindTo != null ? bindTo : lbl(val, ACCENT, 30, Font.BOLD);
        if (bindTo == null) vl.setText(val);
        vl.setForeground(ACCENT);
        vl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        vl.setAlignmentX(LEFT_ALIGNMENT);
        JLabel ll = lbl(label, TEXT_DIM, 12, Font.PLAIN);
        ll.setAlignmentX(LEFT_ALIGNMENT);
        c.add(vl);
        c.add(gap(4));
        c.add(ll);
        return c;
    }

    private JButton btn(String text) {
        JButton b = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed() ? ACCENT_DK : (getModel().isRollover() ? ACCENT_GL : ACCENT);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(new EmptyBorder(9, 20, 9, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(false);
        return b;
    }

    private JTextField field(String hint) {
        JTextField f = new JTextField();
        f.setBackground(new Color(0x14, 0x0C, 0x20));
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(
                new LineBorder(CARD_LINE, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        f.setToolTipText(hint);
        return f;
    }

    private JTextArea monoArea(Color fg) {
        JTextArea a = new JTextArea(2, 50);
        a.setEditable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(false);
        a.setBackground(new Color(0x14, 0x0C, 0x20));
        a.setForeground(fg);
        a.setFont(new Font("Consolas", Font.PLAIN, 12));
        a.setBorder(new EmptyBorder(8, 10, 8, 10));
        return a;
    }

    private JScrollPane scrollOf(JTextArea a, int h) {
        JScrollPane sp = new JScrollPane(a);
        sp.setBorder(new LineBorder(CARD_LINE, 1, true));
        sp.setAlignmentX(LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
        return sp;
    }

    private JScrollPane scrollOf2(JTextPane p, int h) {
        JScrollPane sp = new JScrollPane(p);
        sp.setBorder(new LineBorder(CARD_LINE, 1, true));
        sp.setAlignmentX(LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
        return sp;
    }

    private JLabel lbl(String t, Color c, int size, int style) {
        JLabel l = new JLabel(t);
        l.setForeground(c);
        l.setFont(new Font("Segoe UI", style, size));
        return l;
    }

    private JLabel html(String body, int w, String hex, String ptSize) {
        return new JLabel("<html><body style='width:" + w + "px;color:" + hex + ";font-size:" + ptSize + "pt;'>" + body + "</body></html>");
    }

    private JPanel col() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    private <T extends JPanel> T solidBg(T p, Color c) {
        p.setBackground(c);
        p.setOpaque(true);
        return p;
    }

    private Component gap(int h) {
        return Box.createVerticalStrut(h);
    }

    private JSeparator hRule() {
        JSeparator s = new JSeparator();
        s.setForeground(CARD_LINE);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        s.setAlignmentX(LEFT_ALIGNMENT);
        return s;
    }

    private JSeparator hLine() {
        return hRule();
    }

    private JPanel scrollPage() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(28, 32, 32, 32));
        return p;
    }

    private JScrollPane wrap(JPanel content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setBackground(BG);
        return sp;
    }

    private void markDone(String mod) {
        done.add(mod);
        if (doneCountLbl != null) doneCountLbl.setText(String.valueOf(done.size()));
        if (dashBar != null) {
            dashBar.setValue(done.size());
            if (dashBarLbl != null) dashBarLbl.setText(done.size() + " / 5 modules completed");
        }
    }

    // =========================================================================
    //  SIDEBAR ITEM
    // =========================================================================
    class SideItem extends JPanel {
        final String title;
        private boolean active = false, hover = false;
        private final List<ActionListener> listeners = new ArrayList<>();

        SideItem(JComponent icon, String title, String sub) {
            this.title = title;
            setOpaque(false);
            setLayout(new BorderLayout(12, 0));
            setBorder(new EmptyBorder(8, 14, 8, 14));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel iconWrap = new JPanel(new GridBagLayout());
            iconWrap.setOpaque(false);
            iconWrap.setPreferredSize(new Dimension(26, 26));
            iconWrap.add(icon);

            JPanel txt = col();
            JLabel tl = new JLabel(title);
            tl.setForeground(TEXT);
            tl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            JLabel sl = new JLabel(sub);
            sl.setForeground(TEXT_DIM);
            sl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            txt.add(tl);
            txt.add(sl);

            add(iconWrap, BorderLayout.WEST);
            add(txt, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    for (ActionListener l : listeners) l.actionPerformed(null);
                }

                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        void addActionListener(ActionListener l) {
            listeners.add(l);
        }

        void setActive(boolean a) {
            active = a;
            repaint();
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 38));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // left bar
                g2.setColor(ACCENT);
                g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
            } else if (hover) {
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================================
    //  CUSTOM DRAWN LOGO ICONS
    //  Each is a lightweight JComponent that draws itself with Graphics2D.
    //  Size is passed via constructor so they scale for sidebar (20px) or
    //  module header (52px) or dashboard tile (38px).
    // =========================================================================

    /**
     * App logo: shield with "CS" inside
     */
    static class AppLogoIcon extends JComponent {
        final int sz;

        AppLogoIcon(int sz) {
            this.sz = sz;
            setPreferredSize(new Dimension(sz, sz));
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            float s = sz / 40f;
            g2.translate(sz / 2f - 20 * s, sz / 2f - 22 * s);
            g2.scale(s, s);
            // shield body
            int[] xs = {20, 36, 36, 20, 4, 4};
            int[] ys = {2, 8, 24, 38, 24, 8};
            g2.setColor(ACCENT);
            g2.fillPolygon(xs, ys, 6);
            g2.setColor(ACCENT_DK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawPolygon(xs, ys, 6);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("CS", (40 - fm.stringWidth("CS")) / 2, 25);
            g2.dispose();
        }
    }

    /**
     * Dashboard: grid of 4 squares
     */
    static class DashLogoIcon extends JComponent {
        final int sz;

        DashLogoIcon(int sz) {
            this.sz = sz;
            setPreferredSize(new Dimension(sz, sz));
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int pad = sz / 8, gap2 = sz / 10, cell = (sz - 2 * pad - gap2) / 2;
            g2.setColor(ACCENT);
            g2.fillRoundRect(pad, pad, cell, cell, 4, 4);
            g2.fillRoundRect(pad + cell + gap2, pad, cell, cell, 4, 4);
            g2.setColor(ACCENT_GL);
            g2.fillRoundRect(pad, pad + cell + gap2, cell, cell, 4, 4);
            g2.fillRoundRect(pad + cell + gap2, pad + cell + gap2, cell, cell, 4, 4);
            g2.dispose();
        }
    }

    /**
     * Caesar: old-style key
     */
    static class CaesarLogoIcon extends JComponent {
        final int sz;

        CaesarLogoIcon(int sz) {
            this.sz = sz;
            setPreferredSize(new Dimension(sz, sz));
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            float s = sz / 40f;
            g2.translate(0, 0);
            int r = (int) (10 * s);      // key ring radius
            int cx = (int) (14 * s), cy = (int) (20 * s);
            g2.setColor(C_CAESAR);
            g2.setStroke(new BasicStroke(Math.max(2f, 2.5f * s), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // ring
            g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);
            // hole in ring
            g2.setColor(new Color(C_CAESAR.getRed(), C_CAESAR.getGreen(), C_CAESAR.getBlue(), 80));
            g2.fillOval(cx - r + 2, cy - r + 2, 2 * r - 4, 2 * r - 4);
            g2.setColor(C_CAESAR);
            // shaft
            int shaftLen = (int) (18 * s);
            g2.drawLine(cx + r, cy, cx + r + shaftLen, cy);
            // teeth
            int tx = cx + r + (int) (10 * s), h = (int) (5 * s);
            g2.drawLine(tx, cy, tx, cy + h);
            tx = cx + r + (int) (15 * s);
            g2.drawLine(tx, cy, tx, cy + (int) (4 * s));
            g2.dispose();
        }
    }

    /**
     * SHA-256 Hash: # grid symbol
     */
    static class HashLogoIcon extends JComponent {
        final int sz;

        HashLogoIcon(int sz) {
            this.sz = sz;
            setPreferredSize(new Dimension(sz, sz));
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(C_HASH);
            float sw = Math.max(1.5f, sz / 16f);
            g2.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int p = sz / 6;
            int a = p, b = sz - p;
            int o1 = (int) (sz * 0.35), o2 = (int) (sz * 0.65);
            // vertical bars (slanted slightly)
            g2.drawLine(o1 - sz / 14, a, o1 + sz / 14, b);
            g2.drawLine(o2 - sz / 14, a, o2 + sz / 14, b);
            // horizontal bars
            g2.drawLine(a, o1, b, o1);
            g2.drawLine(a, o2, b, o2);
            g2.dispose();
        }
    }

    /**
     * Avalanche: cascade / wave lines
     */
    static class AvalLogoIcon extends JComponent {
        final int sz;

        AvalLogoIcon(int sz) {
            this.sz = sz;
            setPreferredSize(new Dimension(sz, sz));
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(Math.max(1.5f, sz / 18f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Color[] cols = {C_AVAL, new Color(C_AVAL.getRed(), C_AVAL.getGreen(), C_AVAL.getBlue(), 160),
                    new Color(C_AVAL.getRed(), C_AVAL.getGreen(), C_AVAL.getBlue(), 90)};
            int rows = 3;
            int pad = sz / 7;
            for (int i = 0; i < rows; i++) {
                g2.setColor(cols[i]);
                int y = (int) (pad + i * ((sz - 2.0 * pad) / (rows - 1)));
                // sine-ish wave
                Path2D path = new Path2D.Float();
                int segs = 5;
                float segW = (sz - 2f * pad) / segs;
                path.moveTo(pad, y);
                for (int j = 0; j < segs; j++) {
                    float x0 = pad + j * segW, x1 = pad + (j + 1) * segW;
                    float amp = sz / 9f * (1 + i * 0.5f);
                    float cy1 = y + (j % 2 == 0 ? -amp : amp), cy2 = y + (j % 2 == 0 ? amp : -amp);
                    path.curveTo(x0 + segW / 3, cy1, x0 + 2 * segW / 3, cy2, x1, y);
                }
                g2.draw(path);
            }
            g2.dispose();
        }
    }

    /**
     * Hidden Characters: eye with a slash
     */
    static class HiddenLogoIcon extends JComponent {
        final int sz;

        HiddenLogoIcon(int sz) {
            this.sz = sz;
            setPreferredSize(new Dimension(sz, sz));
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(C_HIDDEN);
            float sw = Math.max(1.5f, sz / 18f);
            g2.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = sz / 2, cy = sz / 2;
            int ew = sz * 5 / 7, eh = sz * 2 / 7;
            // eye outline
            Path2D eye = new Path2D.Float();
            eye.moveTo(cx - ew / 2, cy);
            eye.curveTo(cx - ew / 4, cy - eh, cx + ew / 4, cy - eh, cx + ew / 2, cy);
            eye.curveTo(cx + ew / 4, cy + eh, cx - ew / 4, cy + eh, cx - ew / 2, cy);
            g2.draw(eye);
            // pupil
            int pr = sz / 9;
            g2.drawOval(cx - pr, cy - pr, 2 * pr, 2 * pr);
            // slash
            g2.setColor(new Color(C_HIDDEN.getRed(), C_HIDDEN.getGreen(), C_HIDDEN.getBlue(), 200));
            g2.setStroke(new BasicStroke(sw + 0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx - sz / 4, cy + sz / 4, cx + sz / 4, cy - sz / 4);
            g2.dispose();
        }
    }

    /**
     * Homograph: two A letters side by side (one normal, one suspicious)
     */
    static class HomoLogoIcon extends JComponent {
        final int sz;

        HomoLogoIcon(int sz) {
            this.sz = sz;
            setPreferredSize(new Dimension(sz, sz));
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int fs = Math.max(10, (int) (sz * 0.54));
            g2.setFont(new Font("Serif", Font.BOLD, fs));
            FontMetrics fm = g2.getFontMetrics();
            int ty = sz / 2 + fm.getAscent() / 2 - fm.getDescent();
            // left A – normal (green-ish)
            g2.setColor(TEXT);
            g2.drawString("A", sz / 2 - fm.stringWidth("A") - sz / 10, ty);
            // right A – suspicious (red + underline)
            g2.setColor(C_HOMO);
            int rx = sz / 2 + sz / 10;
            g2.drawString("A", rx, ty);
            g2.setStroke(new BasicStroke(Math.max(1.5f, sz / 20f)));
            g2.drawLine(rx, ty + 2, rx + fm.stringWidth("A"), ty + 2);
            g2.dispose();
        }
    }

    /**
     * About: person silhouette
     */
    static class AboutLogoIcon extends JComponent {
        final int sz;

        AboutLogoIcon(int sz) {
            this.sz = sz;
            setPreferredSize(new Dimension(sz, sz));
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(C_ABOUT);
            int cx = sz / 2;
            // head
            int hr = sz / 6;
            g2.fillOval(cx - hr, sz / 8, 2 * hr, 2 * hr);
            // body arc
            int bw = sz * 3 / 5, bh = sz * 2 / 5;
            g2.fillArc(cx - bw / 2, sz / 2, bw, bh, 0, 180);
            g2.dispose();
        }
    }

    /**
     * Developer avatar: bigger shield with "DEV"
     */
    static class DevAvatarIcon extends JComponent {
        final int sz;

        DevAvatarIcon(int sz) {
            this.sz = sz;
            setPreferredSize(new Dimension(sz, sz));
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // circle background
            g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 60));
            g2.fillOval(0, 0, sz, sz);
            g2.setColor(ACCENT);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawOval(1, 1, sz - 2, sz - 2);
            // person
            int cx = sz / 2;
            int hr = sz / 6;
            g2.fillOval(cx - hr, sz / 8, 2 * hr, 2 * hr);
            int bw = sz * 3 / 5, bh = sz * 2 / 5;
            g2.fillArc(cx - bw / 2, sz / 2, bw, bh, 0, 180);
            g2.dispose();
        }
    }
}