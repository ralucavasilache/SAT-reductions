// Copyright 2020
// Author: Matei Simtinică

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Task3 This being an optimization problem, the solve method's logic has to
 * work differently. You have to search for the minimum number of arrests by
 * successively querying the oracle. Hint: it might be easier to reduce the
 * current task to a previously solved task
 */
public class Task3 extends Task {
    String task2InFilename;
    String task2OutFilename;
    // TODO: define necessary variables and/or data structures

    int families;
    int relations;
    Integer[][] adjacencyMatrix;
    // matricea de adiacenta pentru graful complementar
    Integer[][] complementMatrix;
    String task2Answer;
    // relatiile din graful complementar
    StringBuilder complementRelations = new StringBuilder("");
    // numarul de relatii din graful complementar
    int complementRelationsNumber = 0;
    String[] task2Solution;

    @Override
    public void solve() throws IOException, InterruptedException {
        task2InFilename = inFilename + "_t2";
        task2OutFilename = outFilename + "_t2";
        Task2 task2Solver = new Task2();
        task2Solver.addFiles(task2InFilename, oracleInFilename, oracleOutFilename, task2OutFilename);

        readProblemData();

        // TODO: implement a way of successively querying the oracle (using Task2) about
        // various arrest numbers until you
        // find the minimum

        reduceToTask2();

        
        // interogam oracolul pentru a afla clica de dimensiune maxima
        // k = families, k = families - 1 etc.
       for (int i = families; i >= 1; i--) {

            FileWriter writer1 = new FileWriter(task2InFilename, false);
            
            writer1.write(families + " " + complementRelationsNumber + " " + i + "\n");
            writer1.write(complementRelations.toString());

            writer1.close();

            task2Solver.solve();
            extractAnswerFromTask2();

            // daca s-a gasit clica maxima, se opreste cautarea
            if (task2Answer.compareTo("True") == 0) {
                break;
            }

        }

        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        // TODO: read the problem input (inFilename) and store the data in the object's
        // attributes

        BufferedReader buffer = new BufferedReader(new FileReader(inFilename));
        
        // se memoreaza datele de pe prima linie in cele 2 variabile
        String[] inputLine = buffer.readLine().split(" ");

        families = Integer.parseInt(inputLine[0]);
        relations = Integer.parseInt(inputLine[1]);

        adjacencyMatrix = new Integer[families + 1][families + 1];

        // initializare matrice de adiacenta
        for (int i = 0; i <= families; i++) {
            for (int j = 0; j <= families; j++) {
                adjacencyMatrix[i][j] = 0;
            }
        }
        
        // completare matrice de adiacenta
        for (int i = 1; i <= relations; i++) {

            inputLine = buffer.readLine().split(" ");

            int line = Integer.parseInt(inputLine[0]);
            int col = Integer.parseInt(inputLine[1]);

            adjacencyMatrix[line][col] = 1;
            adjacencyMatrix[col][line] = 1;
        }

        buffer.close();
    }

    public void reduceToTask2() {
        // TODO: reduce the current problem to Task2

        complementMatrix = new Integer[families + 1][families + 1];
        
        // completare matrice de adiacenta pentru graful complementar
        for (int i = 1; i <= families; i++) {
            for (int j = 1; j <= families; j++) {
                
                if (adjacencyMatrix[i][j] == 1) {
                    
                    complementMatrix[i][j] = 0;
                    complementMatrix[j][i] = 0;

                } else {

                    complementMatrix[i][j] = 1;
                    complementMatrix[j][i] = 1;
                }
            }
        }

        // memorarea relatiilor din matricea complementara
        for (int i = 1; i <= families; i++) {
            for (int j = i + 1; j <= families; j++) {
                
                if (complementMatrix[i][j] == 1) {
                    
                    complementRelationsNumber++;
                    complementRelations.append(i + " " + j + "\n");
                }
            }
        }

    }

    public void extractAnswerFromTask2() throws IOException {
        // functia citeste raspunsul dat de interogarea oracolului prin task2

        BufferedReader r = new BufferedReader(new FileReader(task2OutFilename));

        // poate fi "True" sau "False"
        task2Answer = r.readLine();
        // daca e "True", se citeste solutia
        if (task2Answer.equals("True")) {

            task2Solution = r.readLine().split(" "); 
        }
        r.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        // TODO: write the answer to the current problem (outFilename)

        FileWriter writer = new FileWriter(outFilename, false);

            // raspunsul problemei va fi reprezentat de toate nodurile care
            // nu fac parte din clica maxima
            for(Integer i = 1; i <= families; i++) {
                
                if (!Arrays.asList(task2Solution).contains(i.toString())) {
                    writer.write(i + " ");
                }
            }

        writer.close();
    }
}
