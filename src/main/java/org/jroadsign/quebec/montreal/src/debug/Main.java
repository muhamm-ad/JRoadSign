package org.jroadsign.quebec.montreal.src.debug;

import org.jroadsign.quebec.montreal.src.MontrealRoadPostSignsGeojsonReader;
import org.jroadsign.quebec.montreal.src.RoadSign;
import org.jroadsign.quebec.montreal.src.rpasign.RpaSign;
import org.jroadsign.quebec.montreal.src.rpasign.description.RpaSignDescRule;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String FILTERED_FILE = "src/main/resources/quebec/montreal/input/Filtered_TypeS_RoadSign.geojson";
    private static final File OUTPUT_DIR = new File("src/main/resources/quebec/montreal/output");

    private static SortedMap<Long, RoadSign> prepareRoadSignData() {
        MontrealRoadPostSignsGeojsonReader montrealRoadPostSignsGeojsonReader =
                new MontrealRoadPostSignsGeojsonReader(FILTERED_FILE);
        SortedMap<Long, RoadSign> roadSigns = montrealRoadPostSignsGeojsonReader.getSigns();
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

    public static void debugRpaSignRules(SortedMap<Long, RoadSign> roadSigns) {
        try (
                BufferedWriter writer_strDescription =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "strDescription.txt")));
                BufferedWriter writer_strDescriptionCleaned =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "strDescriptionCleaned.txt")));

                BufferedWriter writer_durationMinutes =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignRule_durationMinutes.txt")));
                BufferedWriter writer_dailyTimeRanges =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignRule_dailyTimeRanges.txt")));
                BufferedWriter writer_weeklyDays =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignRule_weeklyDays.txt")));
                BufferedWriter writer_annualMonthRanges =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignRule_annualMonthRanges.txt")));
                BufferedWriter writer_additionalInfos_1 =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignRule_additionalInfos_1.txt")));
                BufferedWriter writer_additionalInfos_2 =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignRule_additionalInfos_2.txt")));
        ) {
            for (RoadSign s : roadSigns.values()) {
                RpaSign rpaSign = s.getRpaSign();
                String rpaCode = s.getRpaSign().getCode().getStr();

                String strDescription = rpaSign.getDescription().getStrDescription();
                writer_strDescription.write(rpaCode + "\t==>\t");
                writer_strDescription.write(strDescription + "\n");

                String strDescriptionCleaned = rpaSign.getDescription().getStrDescriptionCleaned();
                writer_strDescriptionCleaned.write(rpaCode + "\t==>\t");
                writer_strDescriptionCleaned.write(strDescriptionCleaned + "\n");

                StringBuilder additionalInfos = new StringBuilder();
                StringBuilder durationMinutes = new StringBuilder();
                StringBuilder dailyTimeRanges = new StringBuilder();
                StringBuilder weeklyDays = new StringBuilder();
                StringBuilder annualMonthRanges = new StringBuilder();

                for (RpaSignDescRule rule : rpaSign.getDescription().getRpaSignDescRules()) {
                    durationMinutes.append(
                            rule.getListDurationMinutes() == null || rule.getListDurationMinutes().isEmpty() ?
                                    "" : rule.getListDurationMinutes() + ";");
                    dailyTimeRanges.append(
                            rule.getListDailyTimeRange() == null || rule.getListDailyTimeRange().isEmpty() ?
                                    "" : rule.getListDailyTimeRange() + ";");
                    weeklyDays.append(
                            rule.getWeeklyDays() == null || rule.getWeeklyDays().isEmpty() ?
                                    "" : rule.getWeeklyDays() + ";");
                    annualMonthRanges.append(
                            rule.getListAnnualMonthRange() == null || rule.getListAnnualMonthRange().isEmpty() ?
                                    "" : rule.getListAnnualMonthRange() + ";");

                    additionalInfos.append(rule.getAdditionalMetaData() == null ? "" : rule.getAdditionalMetaData() + ";");
                }

                if (!durationMinutes.isEmpty() && durationMinutes.toString().split(";").length > 1) {
                    writer_durationMinutes.write(rpaCode + "\t==>\t");
                    writer_durationMinutes.write(durationMinutes + "\n");
                }
                if (!dailyTimeRanges.isEmpty() && dailyTimeRanges.toString().split(";").length > 1) {
                    writer_dailyTimeRanges.write(rpaCode + "\t==>\t");
                    writer_dailyTimeRanges.write(dailyTimeRanges + "\n");
                }
                if (!weeklyDays.isEmpty() && weeklyDays.toString().split(";").length > 1) {
                    writer_weeklyDays.write(rpaCode + "\t==>\t");
                    writer_weeklyDays.write(weeklyDays + "\n");
                }
                if (!annualMonthRanges.isEmpty() && annualMonthRanges.toString().split(";").length > 1) {
                    writer_annualMonthRanges.write(rpaCode + "\t==>\t");
                    writer_annualMonthRanges.write(annualMonthRanges + "\n");
                }
                if (!additionalInfos.isEmpty()) {
                    // writer_additionalInfos_1.write(rpaCode + "\t==>\t");
                    writer_additionalInfos_1.write(additionalInfos + "\n");
                    writer_additionalInfos_2.write(rpaCode + "\t'" + strDescription + "'\t==>\t'" + additionalInfos + "'\n");
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to output file: " + e.getMessage());
        }
        // Define script path and arguments
        String scriptPath = "src/main/java/org/jroadsign/quebec/montreal/src/debug/sort_and_uniq.sh";
        String targetFiles_RpaSignRules = "src/main/resources/quebec/montreal/output/RpaSignRule_*.txt";
        String targetFiles_strDescriptions = "src/main/resources/quebec/montreal/output/strDescription*.txt";

        // Execute the script with arguments
        executeScript(scriptPath, targetFiles_RpaSignRules);
        executeScript(scriptPath, targetFiles_strDescriptions);
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
                .replace("weeklyDayss", "\n\t\t\t\tweeklyDayss")
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

    private static void debugRpaSign_file(SortedMap<Long, RoadSign> roadSigns) {
        try (
                // BufferedWriter writerRoadSigns = new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RoadSigns.txt")));
                BufferedWriter writerRpaSigns = new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSigns.txt")));
        ) {
            for (RoadSign s : roadSigns.values()) {
                // if (s.getRpaSign().getStringCode().equalsIgnoreCase("SV-QA")) { // Filter
                // writerRoadSigns.write(s + "\n");
                RpaSign rpaSign = s.getRpaSign();
                writerRpaSigns.write(formatLineRpaSign(rpaSign.toString()) + "\n");
                // }
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to output file: " + e.getMessage());
        }
    }

    private static void debugRpaSign_stdout(SortedMap<Long, RoadSign> roadSigns) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter the code to search for (e.g., SV-QA) or type 'exit' to quit: ");
            String searchCode = scanner.nextLine().trim();

            if (searchCode.equalsIgnoreCase("exit")) {
                break;
            }

            List<RoadSign> matchingRoadSigns = roadSigns.values().stream()
                    .filter(s -> s.getRpaSign().getCode().getStr().equalsIgnoreCase(searchCode))
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

    public static void main(String[] args) {
        if (!OUTPUT_DIR.exists()) {
            OUTPUT_DIR.mkdir();
        }

        SortedMap<Long, RoadSign> roadSigns = prepareRoadSignData();

        debugRpaSignRules(roadSigns);
        debugRpaSign_file(roadSigns);

        // debugRpaSign_stdout(roadSigns);
    }

}
