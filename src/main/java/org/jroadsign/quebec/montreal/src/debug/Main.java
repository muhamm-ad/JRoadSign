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
    private static final String SORT_UNIQ_SCRIPT_FILE = "src/main/java/org/jroadsign/quebec/montreal/src/debug/sort_and_uniq.sh";


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
                StringBuilder days = new StringBuilder();
                StringBuilder annualMonthRanges = new StringBuilder();

                for (RpaSignDescRule rule : rpaSign.getDescription().getRpaSignDescRules()) {
                    durationMinutes.append(rule.getListDurationMinutes().isEmpty() ? "" : rule.getListDurationMinutes() + ";");
                    dailyTimeRanges.append(rule.getListDailyTimeRange().isEmpty() ? "" : rule.getListDailyTimeRange() + ";");
                    days.append(rule.getListDay().isEmpty() ? "" : rule.getListDay() + ";");
                    annualMonthRanges.append(rule.getListAnnualMonthRange().isEmpty() ? "" : rule.getListAnnualMonthRange() + ";");
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
                if (!days.isEmpty() && days.toString().split(";").length > 1) {
                    writer_weeklyDays.write(rpaCode + "\t==>\t");
                    writer_weeklyDays.write(days + "\n");
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

        executeScript(SORT_UNIQ_SCRIPT_FILE, "src/main/resources/quebec/montreal/output/RpaSignRule_*.txt");
        executeScript(SORT_UNIQ_SCRIPT_FILE, "src/main/resources/quebec/montreal/output/strDescription*.txt");
    }

    private static void debugRoadSigns(SortedMap<Long, RoadSign> roadSigns) {
        String out_json = "RoadSigns.json";
        try (
                BufferedWriter writerRpaSigns = new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, out_json)));
        ) {
            writerRpaSigns.write("{ \"RoadSigns\" : [");
            for (RoadSign s : roadSigns.values()) {
                RpaSign rpaSign = s.getRpaSign();
                /*
                RpaSignDesc rpaSignDesc = rpaSign.getDescription();
                List<RpaSignDescRule> rules = rpaSignDesc.getRpaSignDescRules();
                RpaSignDescRule rule = rules.get(0);
                if (rules.size() > 1
                        && !rule.getListDurationMinutes().isEmpty()
                        && !rule.getListDailyTimeRange().isEmpty()
                        && !rule.getListDay().isEmpty()
                        && !rule.getListAnnualMonthRange().isEmpty()
                ) // Filter
                */
                writerRpaSigns.write(rpaSign.toJson() + ",\n");
            }
            writerRpaSigns.write("]}");
        } catch (IOException e) {
            LOGGER.severe("Error writing to output file: " + e.getMessage());
        }

        executeScript(SORT_UNIQ_SCRIPT_FILE, out_json);
    }

    private static void debugRpaSign(SortedMap<Long, RoadSign> roadSigns) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter the code to search for sign (e.g., SV-QA) or type 'exit' to quit: ");
            String searchCode = scanner.nextLine().trim();

            if (searchCode.equalsIgnoreCase("exit")) break;

            List<RoadSign> matchingRoadSigns = roadSigns.values().stream()
                    .filter(s -> s.getRpaSign().getCode().getStr().equalsIgnoreCase(searchCode))
                    .distinct()
                    .collect(Collectors.toList());

            if (matchingRoadSigns.isEmpty()) {
                System.err.println("No road signs found with the code: " + searchCode);
            } else {
                System.out.print("{\"" + searchCode + "\" : [");
                for (RoadSign s : matchingRoadSigns) {
                    System.out.print(s.getRpaSign().toJson() + ",\n");
                }
                System.out.print("]}");
            }
        }
    }

    public static void main(String[] args) {
        if (!OUTPUT_DIR.exists()) {
            OUTPUT_DIR.mkdir();
        }

        SortedMap<Long, RoadSign> roadSigns = prepareRoadSignData();

        // debugRpaSignRules(roadSigns);
        debugRoadSigns(roadSigns);

        // debugRpaSign(roadSigns);
    }

}
