package org.jroadsign.quebec.montreal.src.debug;

import org.jroadsign.quebec.montreal.src.MontrealRoadPostSignsGeojsonReader;
import org.jroadsign.quebec.montreal.src.RoadSign;
import org.jroadsign.quebec.montreal.src.rpasign.RpaSign;
import org.jroadsign.quebec.montreal.src.rpasign.description.RpaSignDescriptionParser;

import java.io.*;
import java.util.SortedMap;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String FILTERED_FILE =
            "src/main/resources/quebec/montreal/input/Filtered_TypeS_RoadSign.geojson";

    public static void testSpliter() {

        MontrealRoadPostSignsGeojsonReader montrealRoadPostSignsGeojsonReader = new MontrealRoadPostSignsGeojsonReader(FILTERED_FILE);

        SortedMap<Long, RoadSign> signs = montrealRoadPostSignsGeojsonReader.getSigns();
        System.out.println(signs.size());

        File dir = new File("src/main/resources/quebec/montreal/output");
        if (!dir.exists()) {
            dir.mkdir();
        }

        try (
                // Create the output files
                BufferedWriter writer =
                        new BufferedWriter(new FileWriter(new File(dir, "RpaSignDesc.txt")));

                BufferedWriter writer_durationInMinutes =
                        new BufferedWriter(new FileWriter(new File(dir, "RpaSignDesc_durationMinutes.txt")));
                BufferedWriter writer_dayHours =
                        new BufferedWriter(new FileWriter(new File(dir, "RpaSignDesc_dailyTimeRange.txt")));
                BufferedWriter writer_weekdays =
                        new BufferedWriter(new FileWriter(new File(dir, "RpaSignDesc_weeklyDayRange.txt")));
                BufferedWriter writer_months =
                        new BufferedWriter(new FileWriter(new File(dir, "RpaSignDesc_annualMonthRange.txt")));
                BufferedWriter writer_additionalMetaData =
                        new BufferedWriter(new FileWriter(new File(dir, "RpaSignDesc_additionalInfo.txt")));

        ) {
            for (RoadSign s : signs.values()) {
                RpaSign rpaSign = s.getRpaSign();
                RpaSignDescriptionParser rpaSignDes = new RpaSignDescriptionParser(rpaSign.descriptionRpaSign());
                writer.write(String.valueOf(rpaSignDes) + "\n");

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
        }
    }

    public static void main(String[] args) throws Exception {
        testSpliter();

        // Define script path and arguments
        String scriptPath = "src/main/java/org/jroadsign/quebec/montreal/src/debug/sort_and_uniq.sh";
        String targetFiles = "src/main/resources/quebec/montreal/output/RpaSignDesc*.txt";

        // Execute the script with arguments
        executeScript(scriptPath, targetFiles);
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

}
