package main.com.schttttt_game.view;

import java.util.List;

import main.com.schttttt_game.model.*;

public class Grid_Display {

    // Codes ANSI
    private static final String RESET  = "\u001B[0m";
    private static final String BLUE   = "\u001B[1;94m";
    private static final String GREEN  = "\u001B[1;92m";
    private static final String RED    = "\u001B[1;91m";
    private static final String PURPLE = "\u001B[1;95m";
    private static final String YELLOW = "\u001B[1;93m";
    private static final String GREY   = "\u001B[1;97m";

    private static final int CELL_W  = 8;
    private static final int LABEL_W = 14;

    public static void display(Game_State state) {
        display(state, 0);
    }

    public static void clearScreen() {
        System.out.print("\u001B[2J\u001B[H");
        System.out.flush();
    }


    public static void display(Game_State state, int bottomPlayerIndex) {
        int topPlayerIndex = 1 - bottomPlayerIndex;
        Player topP    = state.getPlayers().get(topPlayerIndex);
        Player bottomP = state.getPlayers().get(bottomPlayerIndex);
        int topPlayerNum    = topPlayerIndex + 1;    // 1 or 2
        int bottomPlayerNum = bottomPlayerIndex + 1;
        List<Limit> limits = state.getLimits();
        int n = limits.size();

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("==================== GAME BOARD ====================");

        // Header : Number of limit
        System.out.print(pad("", LABEL_W));
        for (int i = 1; i <= n; i++) {
            System.out.print(center("L" + i, CELL_W));
        }
        System.out.println();

        // Cards of the player at the top
        for (int row = 2; row >= 0; row--) {
            String label = (row == 2) ? truncate(topP.getName(), LABEL_W) : "";
            System.out.print(pad(label, LABEL_W));
            for (Limit l : limits) {
                Card_Combinaison zone = l.getZone(topPlayerNum);
                Card c = (row < zone.getNbCards()) ? zone.getCard(row) : null;
                System.out.print(cell(c));
            }
            System.out.println();
        }

        // Control X (Top player)
        System.out.print(pad("", LABEL_W));
        for (Limit l : limits) {
            System.out.print(center(l.getControl() == topPlayerNum ? "  X  " : "", CELL_W));
        }
        System.out.println();

        System.out.print(pad("", LABEL_W));
        for (int i = 0; i < n; i++) {
            System.out.print(center("===", CELL_W));
        }
        System.out.println();

        // Control X (Bottom player)
        System.out.print(pad("", LABEL_W));
        for (Limit l : limits) {
            System.out.print(center(l.getControl() == bottomPlayerNum ? "  X  " : "", CELL_W));
        }
        System.out.println();

        // Bottom player cards
        for (int row = 0; row < 3; row++) {
            String label = (row == 0) ? truncate(bottomP.getName(), LABEL_W) : "";
            System.out.print(pad(label, LABEL_W));
            for (Limit l : limits) {
                Card_Combinaison zone = l.getZone(bottomPlayerNum);
                Card c = (row < zone.getNbCards()) ? zone.getCard(row) : null;
                System.out.print(cell(c));
            }
            System.out.println();
        }

        System.out.println("=================================================");
        System.out.println();
        System.out.println("Clan deck: " + state.getClanDeck().size() + (state.hasTacticDeck() ? " | Tactic deck: " + state.getTacticDeck().size() : ""));
        System.out.println();
    }

    public static void displayHand(Player p) {
        StringBuilder sb = new StringBuilder(p.getName() + "'s hand: ");
        if (p.getHand().getNbCards() == 0) {
            sb.append("(empty)");
        } else {
            for (int i = 0; i < p.getHand().getNbCards(); i++) {
                Card c = p.getHand().getCard(i);
                sb.append("[").append(i+1).append("] ")
                  .append(colorize(shortColor(c.getColor()) + c.getValue(), c.getColor()))
                  .append("  ");
            }
        }
        System.out.println(sb);
    }

    public static void displayLegend() {
        System.out.println("Color legend : "
            + colorize("B", "Blue")   + "=Blue  "
            + colorize("G", "Green")  + "=Green  "
            + colorize("R", "Red")    + "=Red  "
            + colorize("P", "Purple") + "=Purple  "
            + colorize("Y", "Yellow") + "=Yellow  "
            + colorize("E", "Grey")   + "=Grey");
    }

    private static String cell(Card c) {
        if (c == null){
            return "  [  ]  ";
        }
        String visible = "[" + shortColor(c.getColor()) + c.getValue() + "]";
        return "  " + colorize(visible, c.getColor()) + "  ";
    }

    private static String shortColor(String color) {
        switch (color) {
            case "Blue":   return "B";
            case "Green":  return "G";
            case "Red":    return "R";
            case "Purple": return "P";
            case "Yellow": return "Y";
            case "Grey":   return "E";
            default:       return "?";
        }
    }

    private static String colorize(String s, String color) {
        String code;
        switch (color) {
            case "Blue":   code = BLUE;   break;
            case "Green":  code = GREEN;  break;
            case "Red":    code = RED;    break;
            case "Purple": code = PURPLE; break;
            case "Yellow": code = YELLOW; break;
            case "Grey":   code = GREY;   break;
            default:       code = RESET;  break;
        }
        return code + s + RESET;
    }

    private static String pad(String s, int w) {
        if (s.length() >= w) return s.substring(0, w);
        return s + " ".repeat(w - s.length());
    }

    private static String center(String s, int w) {
        if (s.length() >= w) return s.substring(0, w);
        int total = w - s.length();
        int left  = total / 2;
        int right = total - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }

    private static String truncate(String s, int w) {
        return s.length() <= w ? s : s.substring(0, w);
    }
}