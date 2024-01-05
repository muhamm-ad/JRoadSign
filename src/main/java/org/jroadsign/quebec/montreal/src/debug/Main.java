package org.jroadsign.quebec.montreal.src.debug;

import org.jroadsign.quebec.montreal.src.MontrealRoadPostSignsGeojsonReader;
import org.jroadsign.quebec.montreal.src.RoadSign;
import org.jroadsign.quebec.montreal.src.rpasign.RpaSign;
import org.jroadsign.quebec.montreal.src.rpasign.description.RpaSignDescriptionParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String FILTERED_FILE = "src/main/resources/quebec/montreal/input/Filtered_TypeS_RoadSign.geojson";
    private static final File OUTPUT_DIR = new File("src/main/resources/quebec/montreal/output");

    private static SortedMap<Long, RoadSign> prepareRoadSignData() {
        MontrealRoadPostSignsGeojsonReader montrealRoadPostSignsGeojsonReader =
                new MontrealRoadPostSignsGeojsonReader(FILTERED_FILE);
        SortedMap<Long, RoadSign> roadSigns = montrealRoadPostSignsGeojsonReader.getSigns();
        System.out.println(roadSigns.size());
        return roadSigns;
    }

    public static void getFilesRpaSignDescription(SortedMap<Long, RoadSign> roadSigns) {
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
            writersWithGetters.put(writer, RpaSignDescriptionParser::toString);
            writersWithGetters.put(writer_durationInMinutes, RpaSignDescriptionParser::getDurationMinutes);
            writersWithGetters.put(writer_dayHours, RpaSignDescriptionParser::getDailyTimeRange);
            writersWithGetters.put(writer_weekdays, RpaSignDescriptionParser::getWeeklyDayRange);
            writersWithGetters.put(writer_months, RpaSignDescriptionParser::getAnnualMonthRange);
            writersWithGetters.put(writer_additionalMetaData, RpaSignDescriptionParser::getAdditionalInfo);

            for (RoadSign s : roadSigns.values()) {
                RpaSign rpaSign = s.getRpaSign();
                RpaSignDescriptionParser rpaSignDes =
                        new RpaSignDescriptionParser(rpaSign.descriptionRpaSign().getStringDescription());
                writer.write(String.valueOf(rpaSignDes) + "\n");

                for (Map.Entry<BufferedWriter, Function<RpaSignDescriptionParser, String>> entry : writersWithGetters.entrySet()) {
                    BufferedWriter writerCurrent = entry.getKey();
                    String description = entry.getValue().apply(rpaSignDes);
                    if (description != null && !description.isEmpty())
                        writerCurrent.write(description + "\n");
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to output file: " + e.getMessage());
        } finally {
            writersWithGetters.forEach((writer, getter) -> {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOGGER.severe("Error closing file writer: " + e.getMessage());
                }
            });
        }
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

    private static void debugRpaSignDescription(SortedMap<Long, RoadSign> roadSigns) {
        getFilesRpaSignDescription(roadSigns);

        // Define script path and arguments
        String scriptPath = "src/main/java/org/jroadsign/quebec/montreal/src/debug/sort_and_uniq.sh";
        String targetFiles = "src/main/resources/quebec/montreal/output/RpaSignDesc*.txt";

        // Execute the script with arguments
        executeScript(scriptPath, targetFiles);
    }

    private static void debugRoadSign(SortedMap<Long, RoadSign> roadSigns) {
        try (
                BufferedWriter writerRoadSigns =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RoadSigns.txt")));
                BufferedWriter writerRpaSigns =
                        new BufferedWriter(new FileWriter(new File(OUTPUT_DIR, "RpaSigns.txt")));
        ) {
            for (RoadSign s : roadSigns.values()) {
                writerRoadSigns.write(s + "\n");
                RpaSign rpaSign = s.getRpaSign();
                writerRpaSigns.write(rpaSign + "\n");
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to output file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        if (!OUTPUT_DIR.exists()) {
            OUTPUT_DIR.mkdir();
        }

        SortedMap<Long, RoadSign> roadSigns = prepareRoadSignData();

        debugRpaSignDescription(roadSigns);
        debugRoadSign(roadSigns);
    }

}
