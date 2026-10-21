package MAIN.COM.schttttt_game.view;

import java.util.Scanner;

public class Menu {
    public static int showMenu(Scanner scanner) {
        System.out.println("=== WELCOME ON SCHOTTEN TOTTEN ===");
        System.out.println("1. Player vs Player");
        System.out.println("2. Player vs AI");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }
}