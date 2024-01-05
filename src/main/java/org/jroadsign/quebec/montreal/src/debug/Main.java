package org.jroadsign.quebec.montreal.src.debug;

import org.jroadsign.quebec.montreal.src.MontrealRoadPostSignsGeojsonReader;
import org.jroadsign.quebec.montreal.src.RoadSign;
import org.jroadsign.quebec.montreal.src.rpasign.RpaSign;
import org.jroadsign.quebec.montreal.src.rpasign.description.RpaSignDescription;
import org.jroadsign.quebec.montreal.src.rpasign.description.RpaSignDescriptionParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.logging.Logger;


public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String FILTERED_FILE =
            "src/main/resources/quebec/montreal/input/Filtered_TypeS_RoadSign.geojson";
    private static final File OUTPUT_DIR =
            new File("src/main/resources/quebec/montreal/output");

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
        // Create a map of writers and their corresponding getters from RpaSignDescriptionParser
        Map<BufferedWriter, Function<RpaSignDescriptionParser, String>> writersWithGetters = new HashMap<>();
        try (
                BufferedWriter writer =
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
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSignDesc_additionalInfo.txt")));
        ) {
            for (RoadSign s : roadSigns.values()) {
                RpaSign rpaSign = s.getRpaSign();
                String sDescription = rpaSign.descriptionRpaSign().getStringDescription();
                RpaSignDescriptionParser rpaSignDes = new RpaSignDescriptionParser(sDescription);


                if (rpaSignDes.getAdditionalInfo() != null && !rpaSignDes.getAdditionalInfo().isEmpty()) {// DEBUG: filter
                    writer.write(sDescription + "\n\t==> ");
                    writer.write(rpaSignDes + "\n");
                }

                if (rpaSignDes.getDurationMinutes() != null && !rpaSignDes.getDurationMinutes().isEmpty())
                    writer_durationInMinutes.write(rpaSignDes.getDurationMinutes() + "\n");
                if (rpaSignDes.getDailyTimeRange() != null && !rpaSignDes.getDailyTimeRange().isEmpty())
                    writer_dayHours.write(rpaSignDes.getDailyTimeRange() + "\n");
                if (rpaSignDes.getWeeklyDayRange() != null && !rpaSignDes.getWeeklyDayRange().isEmpty())
                    writer_weekdays.write(rpaSignDes.getWeeklyDayRange() + "\n");
                if (rpaSignDes.getAnnualMonthRange() != null && !rpaSignDes.getAnnualMonthRange().isEmpty())
                    writer_months.write(rpaSignDes.getAnnualMonthRange() + "\n");
                if (rpaSignDes.getAdditionalInfo() != null && !rpaSignDes.getAdditionalInfo().isEmpty())
                    writer_additionalMetaData.write(rpaSignDes.getAdditionalInfo() + "\n");
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to output file: " + e.getMessage());
        } finally {
            // Define script path and arguments
            String scriptPath = "src/main/java/org/jroadsign/quebec/montreal/src/debug/sort_and_uniq.sh";
            String targetFiles = "src/main/resources/quebec/montreal/output/RpaSignDesc_*.txt";

            // Execute the script with arguments
            executeScript(scriptPath, targetFiles);
        }
    }

    private static String formatLineRpaSing(String lineOfRpaSign) {
        return lineOfRpaSign.replaceAll("\\{", " { ")
                .replaceAll("}", " }")
                .replaceAll("idRpaSign", "\n\tidRpaSign")
                .replaceAll("descriptionRpaSign", "\n\tdescriptionRpaSign")
                .replaceAll("stringDescription", "\n\t\tstringDescription")
                .replaceAll("durationMinutesList", "\n\t\tdurationMinutesList")
                .replaceAll("dailyTimeRangeList", "\n\t\tdailyTimeRangeList")
                .replaceAll("DailyTimeRange", "\n\t\t\t\tDailyTimeRange")
                .replaceAll("weeklyDays", "\n\t\tweeklyDays")
                .replaceAll("annualMonthRangeList", "\n\t\tannualMonthRangeList")
                .replaceAll("AnnualMonthRange", "\n\t\t\t\tAnnualMonthRange")
                .replaceAll("additionalMetaData", "\n\t\tadditionalMetaData")
                .replaceAll("codeRpaSign", "\n\tcodeRpaSign")

                .replaceAll("' },", "'\n\t}")
                .replaceAll("' }", "'\n}")
                .replaceAll("],", "\n\t\t\t],");
    }


    private static void debugRoadSign(SortedMap<Long, RoadSign> roadSigns) {
        try (
                BufferedWriter writerRoadSigns =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RoadSigns.txt")));
                BufferedWriter writerRpaSigns =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSigns.txt")));
        ) {
            for (RoadSign s : roadSigns.values()) {

                RpaSignDescription rpaSignDescription = s.getRpaSign().descriptionRpaSign();
                int numDurationMinutes = rpaSignDescription.getDurationMinutesList().size();
                int numDailyTimeRanges = rpaSignDescription.getDailyTimeRangeList().size();
                int numAnnualMonthRanges = rpaSignDescription.getAnnualMonthRangeList().size();

                if (numDurationMinutes > 1 || numDailyTimeRanges > 1 || numAnnualMonthRanges > 1) { // DEBUG: filter
                    writerRoadSigns.write(s + "\n");
                    RpaSign rpaSign = s.getRpaSign();
                    writerRpaSigns.write(formatLineRpaSing(rpaSign.toString()) + "\n");
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to output file: " + e.getMessage());
        }

        /*finally {
            // Define script path and arguments
            String scriptPath = "src/main/java/org/jroadsign/quebec/montreal/src/debug/format_rpasigns.sh";
            String targetFiles = "src/main/resources/quebec/montreal/output/RpaSigns.txt";

            // Execute the script with arguments
            executeScript(scriptPath, targetFiles);
        }*/
    }

    public static void main(String[] args) {
        // This main method is for debugging purposes.
        if (!OUTPUT_DIR.exists()) {
            OUTPUT_DIR.mkdir();
        }

        SortedMap<Long, RoadSign> roadSigns = prepareRoadSignData();

        debugRpaSignDescription(roadSigns);
        debugRoadSign(roadSigns);
    }

}
