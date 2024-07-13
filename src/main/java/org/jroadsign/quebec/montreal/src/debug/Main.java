package org.jroadsign.quebec.montreal.src.debug;

import org.jroadsign.quebec.montreal.src.MontrealRoadPostSignsGeojsonReader;
import org.jroadsign.quebec.montreal.src.RoadSign;
import org.jroadsign.quebec.montreal.src.rpasign.RpaSign;
import org.jroadsign.quebec.montreal.src.rpasign.description.RoadSignDescCleaner;
import org.jroadsign.quebec.montreal.src.rpasign.description.RpaSignDescParser;
import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String FILTERED_FILE = "src/main/resources/quebec/montreal/input/Filtered_TypeS_RoadSign.geojson";
    private static final File OUTPUT_DIR = new File("src/main/resources/quebec/montreal/output");

    private static SortedMap<Long, RoadSign> prepareRoadSignData() {
        MontrealRoadPostSignsGeojsonReader montrealRoadPostSignsGeojsonReader =
                new MontrealRoadPostSignsGeojsonReader(FILTERED_FILE);
        SortedMap<Long, RoadSign> roadSigns = montrealRoadPostSignsGeojsonReader.getSigns();
        LOGGER.info("Number of road signs: " + roadSigns.size());
        return roadSigns;
    }

    private static void executeScript(String scriptPath, String var1) {
        ProcessBuilder processBuilder = new ProcessBuilder(scriptPath, var1);
        processBuilder.redirectErrorStream(true);  // Optionally redirect error stream to standard output

        try {
            Process process = processBuilder.start();
            process.waitFor();

            // Optionally print the script output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Error executing script: " + e.getMessage());
        }
    }

    public static void debugRpaSignDescription(SortedMap<Long, RoadSign> roadSigns) {
        // Create a map of writers and their corresponding getters from RpaSignDescParser
        Map<BufferedWriter, Function<RpaSignDescParser, String>> writersWithGetters = new HashMap<>();
        try (
                BufferedWriter writer_sDescription =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "sDescription.txt")));
                BufferedWriter writer_cleanedDescription =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "cleanedDescription.txt")));

                /*BufferedWriter writer =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc.txt")));
                BufferedWriter writer_durationInMinutes =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc_durationMinutes.txt")));
                BufferedWriter writer_dayHours =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc_dailyTimeRange.txt")));
                BufferedWriter writer_weekdays =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc_weeklyDayRange.txt")));
                BufferedWriter writer_months =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc_annualMonthRange.txt")));
                BufferedWriter writer_additionalMetaData =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc_additionalInfo.txt")));*/
        ) {
            for (RoadSign s : roadSigns.values()) {
                RpaSign rpaSign = s.getRpaSign();

                String sDescription = rpaSign.getStringDescription();
                writer_sDescription.write(sDescription + "\n");

                String cleanedDescription = RoadSignDescCleaner.cleanDescription(sDescription);
                writer_cleanedDescription.write(cleanedDescription + "\n");

                /*RpaSignDescParser rpaSignDes = new RpaSignDescParser(cleanedDescription);

                writer.write(sDescription + "\n\t==> ");
                writer.write(rpaSignDes + "\n");

                if (rpaSignDes.getDurationMinutes() != null && !rpaSignDes.getDurationMinutes().isEmpty()) {
                    writer_durationInMinutes.write("(" + s.getRpaSign().getStringCode() + ")\t'" + sDescription + "'\t==>\t");
                    writer_durationInMinutes.write(rpaSignDes.getDurationMinutes() + "\n");
                }
                if (rpaSignDes.getDailyTimeRange() != null && !rpaSignDes.getDailyTimeRange().isEmpty()
                        && rpaSignDes.getDailyTimeRange().contains(";")) {
                    writer_dayHours.write("(" + s.getRpaSign().getStringCode() + ")\t'" + sDescription + "'\t==>\t");
                    writer_dayHours.write(rpaSignDes.getDailyTimeRange() + "\n");
                }
                if (rpaSignDes.getWeeklyDayRange() != null && !rpaSignDes.getWeeklyDayRange().isEmpty() &&
                        rpaSignDes.getWeeklyDayRange().contains(";")) {
                    writer_weekdays.write("(" + s.getRpaSign().getStringCode() + ")\t'" + sDescription + "'\t==>\t");
                    writer_weekdays.write(rpaSignDes.getWeeklyDayRange() + "\n");
                }
                if (rpaSignDes.getAnnualMonthRange() != null && !rpaSignDes.getAnnualMonthRange().isEmpty() &&
                        rpaSignDes.getAnnualMonthRange().contains(";")) {
                    writer_months.write("(" + s.getRpaSign().getStringCode() + ")\t'" + sDescription + "'\t==>\t");
                    writer_months.write(rpaSignDes.getAnnualMonthRange() + "\n");
                }
                if (rpaSignDes.getAdditionalInfo() != null && !rpaSignDes.getAdditionalInfo().isEmpty()) {
                    writer_additionalMetaData.write("(" + s.getRpaSign().getStringCode() + ")\t'" + sDescription + "'\t==>\t");
                    writer_additionalMetaData.write(rpaSignDes.getAdditionalInfo() + "\n");
                }*/
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to output file: " + e.getMessage());
        }
        /*// Define script path and arguments
        String scriptPath = "src/main/java/org/jroadsign/quebec/montreal/src/debug/sort_and_uniq.sh";
        String targetFiles = "src/main/resources/quebec/montreal/output/RpaSignDesc_*.txt";

        // Execute the script with arguments
        executeScript(scriptPath, targetFiles);*/
    }

    private static String formatLineRpaSign(String lineOfRpaSign) {
        return lineOfRpaSign
                .replace("id", "\n\tid")
                .replace("description", "\n\tdescription")
                .replace("strDescription", "\n\t\tstrDescription")

                .replace("rpaSignDescRules", "\n\t\trpaSignDescRules")
                .replace("[RpaSignDescRule", "[\n\t\t\tRpaSignDescRule")
                .replace("}, RpaSignDescRule", "\n\t\t\t},\n\t\t\tRpaSignDescRule")

                .replace("listDurationMinutes", "\n\t\t\t\tlistDurationMinutes")
                .replace("parkingAuthorized", "\n\t\t\t\tparkingAuthorized")
                .replaceAll("(?<!list)DurationMinutes", "\n\t\t\t\t\t\tDurationMinutes")
                .replace("listDailyTimeRange", "\n\t\t\t\tlistDailyTimeRange")
                .replaceAll("(?<!list)DailyTimeRange", "\n\t\t\t\t\t\tDailyTimeRange")
                .replace("weeklyDays", "\n\t\t\t\tweeklyDays")
                .replace("listAnnualMonthRange", "\n\t\t\t\tlistAnnualMonthRange")
                .replaceAll("(?<!list)AnnualMonthRange", "\n\t\t\t\t\t\tAnnualMonthRange")
                .replace("additionalMetaData", "\n\t\t\t\tadditionalMetaData")
                .replace("}, code", "\n\t},\n\tcode")

                .replace("'}", "'\n}")
                .replace("],", "\n\t\t\t\t],")
                .replace("{", " {")
                .replace("}]", "\t\t\t}\n\t\t]")
                .replace("RpaSign{", "],\nRpaSign{")

                .replace("'null'", "null");
    }

    private static void debugRoadSign_to_file(SortedMap<Long, RoadSign> roadSigns) {
        try (
                BufferedWriter writerRoadSigns = new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RoadSigns.txt")));
                BufferedWriter writerRpaSigns = new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSigns.txt")));
        ) {
            for (RoadSign s : roadSigns.values()) {
                if (s.getRpaSign().getStringCode().equalsIgnoreCase("SV-QA")) {
                    writerRoadSigns.write(s + "\n");
                    RpaSign rpaSign = s.getRpaSign();
                    writerRpaSigns.write(formatLineRpaSign(rpaSign.toString()) + "\n");
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to output file: " + e.getMessage());
        }
    }

    private static void debugRoadSign_to_stdout(SortedMap<Long, RoadSign> roadSigns) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter the code to search for (e.g., SV-QA) or type 'exit' to quit: ");
            String searchCode = scanner.nextLine().trim();

            if (searchCode.equalsIgnoreCase("exit")) {
                break;
            }

            List<RoadSign> matchingRoadSigns = roadSigns.values().stream()
                    .filter(s -> s.getRpaSign().getStringCode().equalsIgnoreCase(searchCode))
                    .collect(Collectors.toList());

            if (matchingRoadSigns.isEmpty()) {
                System.err.println("No road signs found with the code: " + searchCode);
            } else {
                for (RoadSign s : matchingRoadSigns) {
                    RpaSign rpaSign = s.getRpaSign();
                    System.out.println(formatLineRpaSign(rpaSign.toString()));
                }
            }
        }
    }

    public static void checkPattern() {
        final String WEEKLY_DAYS_EXPRESSION_PATTERN =
                GlobalConfigs.ALL_TIME_EXCEPT_PATTERN + "\\s+" +
                        String.format(GlobalConfigs.DAY_TIME_RANGE_PATTERN, "\\s*", "\\s*(AU?|-)\\s*") + "\\s+" +
                        GlobalConfigs.WEEKLY_DAYS_RANGE_PATTERN;
        System.out.println("Pattern : \"" + WEEKLY_DAYS_EXPRESSION_PATTERN + "\"");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter a string to compare to the pattern (or type 'exit' to quit): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            Pattern pattern = Pattern.compile("\\b(" + WEEKLY_DAYS_EXPRESSION_PATTERN + ")\\b",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(input);

            if (matcher.matches()) {
                System.out.println("Yes, matched");
            } else {
                System.out.println("Not matched");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        // This main method is for debugging purposes.
        /*if (!OUTPUT_DIR.exists()) {
            OUTPUT_DIR.mkdir();
        }*/

        SortedMap<Long, RoadSign> roadSigns = prepareRoadSignData();

        debugRpaSignDescription(roadSigns);
        //debugRoadSign_to_file(roadSigns);
        debugRoadSign_to_stdout(roadSigns);
        //checkPattern();
    }

}
