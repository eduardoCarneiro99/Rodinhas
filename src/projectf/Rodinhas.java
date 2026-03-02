package projectf;

import java.io.FileNotFoundException;
import java.util.Scanner;
import static java.Configs.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

public class Rodinhas {

    public static Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {
        String[] races = new String[N_RACES];
        String[][] participants = new String[MAX_PARTICIPANTS][N_INFO_FIELDS];
        double[][] prizes = new double[MAX_PARTICIPANTS][N_RACES];
        int[][] times = new int[MAX_PARTICIPANTS][N_RACES];
        List<String> participantsWithErrors = new ArrayList<String>();
        int op = 0, nParticipants = 0, nRaces = 0;

        String menu = "1-Add participants\n2-View Information\n3-Update Information\n4-Load Races\n5-Load times for a race\n6-Calculate prizes\n7-Backup\n8-Remove participant\n9-Add participant\n10-View average speeds of a participant\n11-Average of average speeds\n12-Create file with all information\n0-EXIT\n\nChoose an option:";

        do {
            System.out.println(menu);
            op = input.nextInt();
            input.nextLine();
            switch (op) {
                case 1:
                    System.out.println("File Name:");
                    String fileName = input.nextLine();
                    nParticipants = readFileIntoMemory(fileName, participants, nParticipants, participantsWithErrors);
                    break;
                case 2:
                    paginatedListing(participants);
                    break;
                case 3:
                    System.out.println("Member number");
                    String memberNumber = input.nextLine();
                    updateParticipantInfo(memberNumber, participants, nParticipants);
                    break;
                case 4:
                    loadRaceFile(races, nRaces);
                    break;
                case 5:
                    loadParticipantTimes(times, participants, nParticipants, races, nRaces);
                    break;
                case 6:
                    calculatePrizes(prizes, participants, nParticipants, races, times);
                    break;
                case 7:
                    backup(participants, nParticipants, times, races);
                    break;
                case 8:
                    nParticipants = removeParticipant(participants, nParticipants, times, prizes);
                    break;
                case 9:
                    nParticipants = newParticipant(participants, nParticipants);
                    break;
                case 10:
                    averageSpeed(nParticipants, participants, races, times);
                    break;
                case 11:
                    case11(races, times, participants, nParticipants, prizes);
                    break;
                case 12:
                    case12(participants, nParticipants, races, times, prizes);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Incorrect option. Repeat");
                    break;
            }
        } while (op != 0);
    }

//- Case 1
    public static int readFileIntoMemory(String fileName, String[][] participants, int nElems, List<String> participantsWithErrors) throws FileNotFoundException {
        try (Scanner fInput = new Scanner(new File(fileName+FILE_EXTENSION))) {
            while (fInput.hasNext() && nElems < MAX_PARTICIPANTS) {
                String fileLine = fInput.nextLine();
                if (fileLine.trim().length() > 0) {
                    nElems = saveData(fileLine, participants, nElems, participantsWithErrors);
                }
            }
        }
        return nElems;
    }

    public static int saveData(String line, String[][] participants, int nElems, List<String> participantsWithErrors) {
        String[] temp = line.split(DATA_SEPARATOR_1);
        if (temp.length == N_INFO_FIELDS) {
            String num = temp[0].trim();
            int pos = searchElement(num, nElems, participants);
            if (pos == -1) {
                participants[nElems][0] = num;
                participants[nElems][1] = temp[1].trim();
                participants[nElems][2] = temp[2].trim();
                participants[nElems][3] = temp[3].trim();
                nElems++;
            }
        } else {
            System.out.println("Line with errors: " + line);
            participantsWithErrors.add(line);
        }
        return nElems;
    }

    public static int searchElement(String value, int nEl, String[][] participants) {
        for (int i = 0; i < nEl; i++) {
            if (participants[i][0].equals(value)) {
                return i;
            }
        }
        return -1;
    }
// - END case 1

// - case 2
    /**
     * View all participant information (paginated) in memory
     *
     * @param matrix - matrix with the information to list
     * @param registeredParticipants – number of participants in the competition
     */
    public static void paginatedListing(String[][] matrix) {
        final int registeredParticipants = matrix.length;
        pause(); 
        for (int i = 0; i < registeredParticipants; i++) {
            System.out.println("\nPAGE: " + (i+1));
            header();
            showParticipant(matrix, i);
            System.out.println("");
        }
        pause();
    }

    /**
     * Shows the information of each participant
     *
     * @param matrix - matrix with all participant information
     * @param i - counter to go through the matrix lines
     */
    public static void showParticipant(String[][] matrix, int i) {
        for (int j = 0; j < N_INFO_FIELDS; j++) {
            if (j == 1) {
                System.out.printf("%30s", matrix[i][j]);
            } else {
                System.out.printf("%15s", matrix[i][j]);
            }
        }
    }

    public static void header() {
        System.out.printf("%50s%n", "PARTICIPANTS");
        System.out.printf("%75s%n",
                "==================================================");
    }

    public static void pause() {
        System.out.println("\n\nPress ENTER to continue\n");
        input.nextLine();
    }
// END case 2

// - case 3
    /**
     * Changes a parameter of a member chosen by the user
     *
     * @param memberNumber - member number whose information will be changed
     * @param matrix - matrix with participant information
     * @param nElems - number of elements already in the matrix
     * @return true/false
     */
    public static boolean updateParticipantInfo(String memberNumber, String[][] matrix, int nElems) {
        int pos = searchElement(memberNumber, nElems, matrix);
        if (pos > -1) {
            int op;
            do {
                showParticipant(matrix, pos);
                op = participantInfoMenu();
                switch (op) {
                    case 1:
                        System.out.println("New name:");
                        matrix[pos][1] = input.nextLine();
                        break;
                    case 2:
                        System.out.println("New car brand:");
                        matrix[pos][2] = input.nextLine();
                        break;
                    case 3:
                        System.out.println("New construction date:");
                        matrix[pos][3] = input.nextLine();
                        break;
                    case 0:
                        System.out.println("END");
                        break;
                    default:
                        System.out.println("Incorrect option");
                        break;
                }
            } while (op != 0);
            return true;
        }
        System.out.printf("Participant %s not found!\n", memberNumber);
        pause();
        return false;
    }

    /**
     * Shows the parameters that can be changed
     *
     * @return the chosen option
     */
    public static int participantInfoMenu() {
        String text = "UPDATE PARTICIPANT INFORMATION"
                + "\n PARTICIPANT NAME ... 1"
                + "\n CAR BRAND... 2"
                + "\n CONSTRUCTION DATE ... 3"
                + "\n END ... 0"
                + "\n\nWHAT IS YOUR OPTION?";
        System.out.printf("%n%s%n", text);
        int op = input.nextInt();
        input.nextLine();
        return op;
    }
    // END case 3

// - case 4
    /**
     * Loads the races and their distances into memory
     *
     * @param races - array where the information will be stored
     * @param nRaces - number of races already loaded
     * @throws FileNotFoundException
     */
    public static void loadRaceFile(String[] races, int nRaces) throws FileNotFoundException {
        String fileContent = "";
        Scanner fInput = new Scanner(new File(RACE_FILE));
        while (fInput.hasNext()) {
            String line = fInput.nextLine().strip();
            if (line.length() > 0) {
                fileContent += line;
            }
        }
        fInput.close();

        boolean savedRaces = saveRaces(fileContent, races, nRaces);

        if (savedRaces) {
            System.out.println("Races loaded successfully");
        } else {
            System.out.println("File with errors - races not loaded");
        }
    }

    /**
     * Saves the races one by one
     *
     * @param line - race to be saved
     * @param races - array where the races will be stored
     * @param nRaces - number of elements already in the array
     * @return true if the races were saved successfully, false if there was an error in the file
     */
    public static boolean saveRaces(String fileContent, String[] races, int nRaces) {
        String[] fileRaces = fileContent.split(DATA_SEPARATOR_1);
        if (fileRaces.length != N_RACES) {
            return false;
        } else {
            for (int i = 0; i < fileRaces.length; i++) {
                String[] race = fileRaces[i].split(DATA_SEPARATOR_2);
                if (race.length != N_FIELDS) {
                    return false;
                } else {
                    race[1] = race[1].replaceAll("Km", " ").trim();
                    races[i] = race[0] + (DATA_SEPARATOR_4) + race[1];
                }
            }
        }
        return true;
    }
// - END case 4

// - case 5
    public static void loadParticipantTimes(int[][] times, String[][] participants, int nParticipants, String[] races, int nRaces) throws FileNotFoundException {
        System.out.println("Race name?");
        String raceName = input.nextLine();
        int nElem = 0;
        String fileName = (raceName + ".txt");
        Scanner fInput = new Scanner(new File(fileName));
        while (fInput.hasNext() && nElem < nParticipants) {
            String line = fInput.nextLine();
            if (line.trim().length() > 0) {
                saveTimes(line, times, participants, nParticipants, raceName, races);
            }
        }
        fInput.close();
    }

    public static void saveTimes(String line, int[][] times, String[][] participants, int nParticipants, String raceName, String[] races) {
        String[] temp = line.split(DATA_SEPARATOR_3);
        if (temp.length != N_FIELDS) {
            System.out.println("Time or member number missing");
        } else {
            String num = temp[0].trim();
            int pos = searchElement(num, nParticipants, participants);
            int raceIndex = searchRace(races, raceName);
            if (pos == -1) {
                System.out.println("This participant does not exist");
            } else {
                String time = temp[1].trim();
                times[pos][raceIndex] = calculateTime(time);
            }
        }
    }

    public static int searchRace(String[] races, String raceName) {
        for (int i = 0; i < N_RACES; i++) {
            String[] temp = races[i].split(DATA_SEPARATOR_4);
            String race = temp[0].trim();
            if (race.equalsIgnoreCase(raceName)) {
                return i;
            }
        }
        return -1;
    }

    public static int calculateTime(String time) {
        String[] temp = time.split(DATA_SEPARATOR_2);
        if (temp.length == N_FIELDS) {
            int mins = Integer.parseInt(temp[0]) * 60;
            int timeInSeconds = mins + Integer.parseInt(temp[1]);
            return timeInSeconds;
        }
        return 0;
    }
// - END case 5

// - case 6
    public static void calculatePrizes(double[][] prizes, String[][] participants, int nParticipants, String[] races, int[][] times) {
        int minTime = 0;
        System.out.println("Which race do you want to calculate prizes for?");
        String race = input.nextLine();
        int racePos = searchRace(races, race);
        for (int p = 0; p < nParticipants; p++) {
            if (times[p][racePos] != 0) {
                minTime = times[p][racePos];
                break;
            }
        }
        for (int i = 0; i < nParticipants; i++) {
            if (times[i][racePos] != 0 && times[i][racePos] < minTime) {
                minTime = times[i][racePos];
            }
        }
        for (int k = 0; k < nParticipants; k++) {
            if (times[k][racePos] == minTime) {
                prizes[k][racePos] = prizeValue(participants[k][3], racePos, races) + 500;
            } else {
                prizes[k][racePos] = prizeValue(participants[k][3], racePos, races);
            }
        }
        System.out.println("Prizes loaded successfully");
    }

    public static double prizeValue(String date, int racePos, String[] races) {
        double age = age(convertDateToYYYYMMDD(date));
        int distance = distances(races, racePos);
        double prize = (double) distance * 2 * (age / 20);
        return prize;
    }

    public static int distances(String[] races, int racePos) {
        String[] temp = races[racePos].split(DATA_SEPARATOR_4);
        int distance = Integer.parseInt(temp[1].trim());
        return distance;
    }

    public static String convertDateToYYYYMMDD(String date) {
        String[] aux = date.trim().split(DATA_SEPARATOR_3);
        String day = aux[0].length() < 2 ? "0" + aux[0] : aux[0];
        String month = aux[1].length() < 2 ? "0" + aux[1] : aux[1];
        return aux[2] + month + day;
    }

    public static int age(String yyyymmdd) {
        int year = Integer.parseInt(yyyymmdd.substring(0, 4));
        int month = Integer.parseInt(yyyymmdd.substring(4, 6));
        int day = Integer.parseInt(yyyymmdd.substring(6, 8));
        Calendar today = Calendar.getInstance();
        int dayH = today.get(Calendar.DAY_OF_MONTH);
        int monthH = today.get(Calendar.MONTH) + 1;
        int yearH = today.get(Calendar.YEAR);
        if (monthH > month || (monthH == month && dayH >= day)) {
            return yearH - year;
        }
        return yearH - year - 1;
    }
// - END case 6

// - case 7
    public static void backup(String[][] participants, int nParticipants, int[][] times, String[] races) throws FileNotFoundException {
        try (Formatter outputBackup = new Formatter(new File("Backup.txt"))) {
            backupParticipants(participants, nParticipants, outputBackup);
            outputBackup.format("%n");
            backupTimes(times, nParticipants, outputBackup);
            outputBackup.format("%n");
            backupRaces(races, outputBackup);
            outputBackup.format("%n");
            System.out.println("Backup Created");
        }
    }

    public static void backupParticipants(String[][] participants, int nParticipants, Formatter outputBackup) {
        outputBackup.format("%s%n", "Participants");
        String aux;
        for (int i = 0; i < nParticipants; i++) {
            aux = "";
            for (int j = 0; j < N_INFO_FIELDS; j++) {
                aux += participants[i][j] + "; ";
            }
            outputBackup.format("%s%n", aux);
        }
        outputBackup.format("%s%n", "**************PARTICIPANTS**************");
    }

    public static void backupTimes(int[][] times, int nParticipants, Formatter outputBackup) {
        outputBackup.format("%s%n", "Times");
        String aux;
        for (int i = 0; i < nParticipants; i++) {
            aux = "";
            for (int j = 0; j < N_RACES; j++) {
                aux += times[i][j] + "; ";
            }
            outputBackup.format("%s%n", aux);
        }
        outputBackup.format("%s%n", "**************TIMES**************");
    }

    public static void backupRaces(String[] races, Formatter outputBackup) {
        outputBackup.format("%s%n", "Races");
        String aux;
        for (int i = 0; i < N_RACES; i++) {
            aux = "";
            aux += races[i] + "; ";
            outputBackup.format("%s%n", aux);
        }
        outputBackup.format("%s%n", "**************RACES**************");
    }
// - END case 7

// - case 8
    public static int removeParticipant(String[][] participants, int nParticipants, int[][] times, double[][] prizes) {
        System.out.println("Member number to remove");
        String value = input.nextLine();
        int pos = searchElement(value, nParticipants, participants);
        if (pos != -1) {
            for (int i = pos; i < nParticipants; i++) {
                participants[i] = participants[i + 1];
            }
            for (int j = 0; j < N_INFO_FIELDS; j++) {
                participants[nParticipants - 1][j] = null;
            }
        }
        for (int i = pos; i < nParticipants; i++) {
            times[i] = times[i + 1];
        }
        for (int j = 0; j < N_RACES; j++) {
            times[nParticipants - 1][j] = 0;
        }
        for (int i = pos; i < nParticipants; i++) {
            prizes[i] = prizes[i + 1];
        }
        for (int j = 0; j < N_RACES; j++) {
            prizes[nParticipants - 1][j] = 0;
        }
        nParticipants--;
        return nParticipants;
    }
// - END case 8

// - case 9
    public static int newParticipant(String[][] participants, int nParticipants) {
        if (nParticipants == MAX_PARTICIPANTS) {
            System.out.println("Cannot add new participant, remove one first");
        } else {
            System.out.println("Member number of new participant?");
            String newMemberNum = input.nextLine();
            int pos = searchElement(newMemberNum, nParticipants, participants);
            if (pos == -1) {
                System.out.println("Name of new participant?");
                String newName = input.nextLine();
                System.out.println("Car brand of new participant?");
                String newCar = input.nextLine();
                System.out.println("Construction date of new participant's car?");
                String newDate = input.nextLine();
                participants[nParticipants][0] = newMemberNum;
                participants[nParticipants][1] = newName;
                participants[nParticipants][2] = newCar;
                participants[nParticipants][3] = newDate;
                nParticipants++;
            } else {
                System.out.println("Participant already registered");
            }
            return nParticipants;
        }
        return nParticipants;
    }
// - END case 9

// - case 10
    public static void averageSpeed(int nParticipants, String[][] participants, String[] races, int[][] times) {
        System.out.println("Member to view");
        String memberNum = input.nextLine();
        int pos = searchElement(memberNum, nParticipants, participants);
        for (int i = 0; i < N_RACES; i++) {
            String[] temp = races[i].split(DATA_SEPARATOR_4);
            if (times[pos][i] != 0) {
                double timeInHours = (double) (times[pos][i]) / 3600;
                double avgSpeed = (double) (distances(races, i) / timeInHours);
                String value = String.format("%.3f", avgSpeed);
                System.out.println("The average speed of participant " + memberNum + " in race " + temp[0] + " was: " + value + " Km/h");
                fastest(i, times, nParticipants, races);
                slowest(i, times, nParticipants, races);
            } else {
                System.out.println("This member did not participate in race " + temp[0]);
            }
        }
    }

    public static void fastest(int i, int[][] times, int nParticipants, String[] races) {
        int minTime = 0;
        for (int p = 0; p < nParticipants; p++) {
            if (times[p][i] != 0) {
                minTime = times[p][i];
                break;
            }
        }
        for (int k = 0; k < nParticipants; k++) {
            if (times[k][i] != 0 && times[k][i] < minTime) {
                minTime = times[k][i];
            }
        }
        double timeInHours = (double) minTime / 3600;
        double avgSpeed = (double) distances(races, i) / timeInHours;
        String value = String.format("%.3f", avgSpeed);
        System.out.println("The average speed of the fastest participant in this race was: " + value + " Km/h");
    }

    public static void slowest(int i, int[][] times, int nParticipants, String[] races) {
        int maxTime = 0;
        for (int k = 0; k < nParticipants; k++) {
            if (times[k][i] > maxTime) {
                maxTime = times[k][i];
            }
        }
        double timeInHours = (double) maxTime / 3600;
        double avgSpeed = (double) distances(races, i) / timeInHours;
        String value = String.format("%.3f", avgSpeed);
        System.out.println("The average speed of the slowest participant in this race was: " + value + " Km/h");
    }
// - END case 10

// - case 11
    public static void case11(String[] races, int[][] times, String[][] participants, int nParticipants, double[][] prizes) {
        for (int i = 0; i < N_RACES; i++) {
            String[] temp = races[i].split(DATA_SEPARATOR_4);
            double sumSpeeds = 0;
            int countParticipants = 0;
            double sumPrizes = 0;
            for (int j = 0; j < nParticipants; j++) {
                int distance = distances(races, i);
                int time = times[j][i];
                if (time != 0) {
                    double timeInHours = (double) time / 3600;
                    double speed = (double) distance / timeInHours;
                    sumSpeeds += speed;
                    countParticipants++;
                }
            }
            String value = String.format("%.3f", (sumSpeeds / countParticipants));
            System.out.println("The average of the average speeds in race " + temp[0] + " was " + value + "km/h");
            fastest(i, times, nParticipants, races);
            for (int k = 0; k < nParticipants; k++) {
                sumPrizes += prizes[k][i];
            }
            System.out.println("The total prizes awarded in this race was: " + sumPrizes + "€.");
            System.out.println();
        }
    }
// END case 11

// - case 12
    public static void case12(String[][] participants, int nParticipants, String[] races, int[][] times, double[][] prizes) throws FileNotFoundException {
        double totalPrizes = 0;
        try (Formatter output = new Formatter(new File("GrandPrize.txt"))) {
            output.format("%50s%n", "Prize Listing");
            output.format("%6s", "Num");
            output.format("%3s", " ");
            output.format("%-20s", "Name");
            output.format("%-20s", "Car Age");
            output.format("%20s", "Total Prizes");
            output.format("%n");
            int[] indices = new int[nParticipants];
            double[] temp = new double[nParticipants];
            for (int j = 0; j < nParticipants; j++) {
                temp[j] = calculatePrizesSum(prizes, j);
                indices[j] = j;
            }
            for (int j = 0; j < nParticipants; j++) {
                for (int k = 0; k < nParticipants; k++) {
                    if (temp[j] > temp[k]) {
                        double aux = temp[j];
                        temp[j] = temp[k];
                        temp[k] = aux;
                        int auxInd = indices[j];
                        indices[j] = indices[k];
                        indices[k] = auxInd;
                    }
                }
            }
            for (int i = 0; i < nParticipants; i++) {
                output.format("%6s", participants[indices[i]][0]);
                output.format("%3s", " ");
                output.format("%-20s", reducedName(participants[indices[i]][1]));
                output.format("%10s", age(convertDateToYYYYMMDD(participants[indices[i]][3])));
                float p = calculatePrizesSum(prizes, indices[i]);
                totalPrizes += p;
                output.format("%24.2f%n", p);
            }
            output.format("%55s", "TOTAL");
            output.format("%8.2f%n", totalPrizes);
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            output.format("%s%s%s", "<", dateFormat.format(date), ">");
            output.close();
        }
    }

    public static float calculatePrizesSum(double[][] prizes, int pos) {
        float prize = 0;
        for (int i = 0; i < N_RACES; i++) {
            prize = (float) (prize + prizes[pos][i]);
        }
        return prize;
    }

    public static String reducedName(String name) {
        String[] temp = name.split(" ");
        String finalName = temp[1] + " " + temp[0].charAt(0) + ".";
        return finalName;
    }
// END case 12
}
