package com.achir;



import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {


    public static void main(String[] args) {
        int max = Integer.parseInt(args[0]);
        int min = Integer.parseInt(args[1]);
        int step = Integer.parseInt(args[2]);
        String folderAnalysis = String.valueOf(args[3]);
        String outputFile = String.valueOf(args[4]);

        BiPredicate<String,String> lineIs = (l, s) -> l.split(",")[0].toLowerCase().equals(s);
        Predicate<String> isStartRXLine = l -> l.split(",")[0].toLowerCase().equals("startrx");
        Function<String,Integer> lineToNodeID = l -> Integer.valueOf(l
                .split("RXid")[1]
                .split(",")[0]
                .split("=")[1]);
        Function<String,Double> lineToRxPowerDbm = l -> Double.valueOf(l
                .split("RxPowerW")[1]
                .split(",")[0]
                .split("=")[1]);

        // init
        //String folderAnalysis = File.separator+"Users"+File.separator+"achir"+File.separator+
        //        "Documents"+File.separator+"coding"+File.separator+"NS3"+File.separator+"nswave"+File.separator+
        //        "runSimulationINRIA";

        boolean analysis = true;

        if (analysis) {
            // analyse the obtained data
            Map<Integer, Map<Integer, Double>> allResults = new HashMap<>();
            for (int i = min; i <= max; i = i + step) {
                String fileName = "results_" + String.valueOf(i) + ".output.tr";
                Path Path = Paths.get(folderAnalysis + File.separator + fileName);
                Map<Integer, Double> list = null;
                try {
                    list = Files.lines(Path)
                            .filter(l->lineIs.test(l,"startrx"))
                            .collect(Collectors.toMap(
                                    l -> lineToNodeID.apply(l),
                                    l -> lineToRxPowerDbm.apply(l)
                            ));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                allResults.put(Integer.valueOf(i), list);
            }
            //System.out.println(allResults);
            DataAnalysisUtils dAU = new DataAnalysisUtils<Double,Double, Double>();
            dAU.mapToTxtFile(allResults,outputFile);
        }

    }
}
