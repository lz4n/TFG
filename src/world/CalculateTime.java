package world;

import java.util.Scanner;

public class CalculateTime {
    public static void main (String [] args) {
        Scanner scnLn = new Scanner(System.in);
        while (true) {
            System.out.println("""
                               ¿Qué quieres hacer?
                               1) Transformar de ticks a horas.
                               2) Transformar de horas a ticks.
                               3) Salir.
                               """);
            switch (scnLn.nextLine()) {
                case "1" -> tickToHour(scnLn);
                case "2" -> hourToTick(scnLn);
                case "3" -> {
                    return;
                }
            }
        }
    }

    private static void hourToTick(Scanner scnLn) {
        System.out.println("Inserte la hora, siguiendo el formato hh:mm");
        String time = scnLn.nextLine();
        int hour = Integer.parseInt(time.split(":")[0]);
        float minute = Integer.parseInt(time.split(":")[1]);

        int ticks = (int) (
                (hour * World.TOTAL_DAY_DURATION / 24) +
                        (minute * World.TOTAL_DAY_DURATION) / (24 % World.TOTAL_DAY_DURATION * 60)
        );
        System.out.println(ticks);
    }

    private static void tickToHour(Scanner scnLn) {
        System.out.println("Inserte un número de ticks");
        int ticks = Integer.parseInt(scnLn.nextLine());
        System.out.printf("%02d:%02d\n",
                ticks * 24 / World.TOTAL_DAY_DURATION,
                ticks * 24 % World.TOTAL_DAY_DURATION * 60 / World.TOTAL_DAY_DURATION);
    }
}
