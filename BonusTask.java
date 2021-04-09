// Copyright 2020
// Author: Matei Simtinică

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Bonus Task
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class BonusTask extends Task {
    // TODO: define necessary variables and/or data structures

    int families;
    int relations;
    Integer[][] adjacencyMatrix;
    // matricea de adiacenta pentru graful complementar
    Integer[][] complementMatrix;
    String[] oracleSolution;
    StringBuilder clauses = new StringBuilder("");

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        // TODO: read the problem input (inFilename) and store the data in the object's attributes

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

    @Override
    public void formulateOracleQuestion() throws IOException {
        // TODO: transform the current problem into a SAT problem and write it (oracleInFilename) in a format
        //  understood by the oracle

        FileWriter writer = new FileWriter(oracleInFilename, false);

        int clausesNumber = 0;

        // clauze soft : nodul u face parte din clica
        for (int u = 1; u <= families; u++) {
            // fiecare caluza soft are costul 1
            clauses.append(1 + " " + u + " 0\n");
            clausesNumber++;
        }

        // clauze hard : doua noduri care nu au legatura (in graful complementar)
        // nu pot fi amandoua in clica
        for (int u = 1; u <= families; u++) {
            for (int v = 1; v <= families; v++) {

                if (adjacencyMatrix[u][v] == 1 && u != v) {
                // fiecare clauza hard are costul = numarul de noduri + 1
                    clauses.append((families + 1) + " " + -u + " " + -v + " 0\n");
                    clausesNumber++;
                }
            }
        }

        writer.write("p wcnf " + families + " " + clausesNumber + " " + (families + 1) + "\n");
        writer.write(clauses.toString());
        
        clauses.setLength(0);

        writer.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        // TODO: extract the current problem's answer from the answer given by the oracle (oracleOutFilename)
        
        BufferedReader r = new BufferedReader(new FileReader(oracleOutFilename));

        r.readLine();
        oracleSolution = r.readLine().split(" "); 

        r.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        // TODO: write the answer to the current problem (outFilename)
        
        FileWriter writer = new FileWriter(outFilename, false);

        for (int i = 1; i <= families; i++) {
            // se analizeaza cate o variabila din raspunsul dat de oracol
            int choice = Integer.parseInt(oracleSolution[i - 1]);
             // daca are valoarea false (e negativa)
            if (choice < 0) {
                writer.write(- choice + " ");
            }
        }
        writer.close();
    }
}
