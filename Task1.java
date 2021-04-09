// Copyright 2020
// Author: Matei Simtinică

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Task1
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task1 extends Task {
    // TODO: define necessary variables and/or data structures
    
    int families;
    int spies;
    int relations;
    int variables;
    // poate fi "True" sau "False"
    String oracleAnswer;
    // contine alegerea variabilelor, in caz ca oracleAnswer este True
    String[] oracleSolution;

    Integer[][] adjacencyMatrix;

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
        
        String[] inputLine = buffer.readLine().split(" ");
        
        // se memoreaza datele de pe prima linie in cele 3 variabile
        families = Integer.parseInt(inputLine[0]);
        relations = Integer.parseInt(inputLine[1]);
        spies = Integer.parseInt(inputLine[2]);

        // numarul total de variabile folosite pentru codificare
        variables = families * spies;

        adjacencyMatrix = new Integer[families + 1][families + 1];

        // initializare matrice de adiacenta
        for (int i = 0; i <= families; i++) {
            for (int j = 0; j <= families; j++) {
                adjacencyMatrix[i][j] = 0;
            }
        }

        // completare matrice de adiacenta
        for (int i = 0; i < relations; i++) {

            inputLine = buffer.readLine().split(" ");

            int line = Integer.parseInt(inputLine[0]);
            int col = Integer.parseInt(inputLine[1]);

            adjacencyMatrix[line][col] = 1;
        }

        buffer.close();
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        // TODO: transform the current problem into a SAT problem and write it (oracleInFilename) in a format
        //  understood by the oracle

        FileWriter writer = new FileWriter(oracleInFilename);

        // se calculeaza numarul de clauze care vor rezulta
        int clausesNumber = families + families * spies * (spies - 1) / 2 + spies * relations;

        // prima linie din fisierul de output va contine "p cnf", numarul de variabile
        // si numarul de clauze
        writer.write("p cnf " + variables + " " + clausesNumber + "\n");


        // fiecare familie trebuie sa aiba cel putin un spion
        int aux = 1;
        for (int i = 1 ; i <= families; i++){
            for (int j = 1; j <= spies; j++) {
        
                writer.write(aux + " ");
                aux++;
            }
            writer.write(0 + "\n");
        }


        // fiecare familie trebuie sa aiba cel mult un spion
        for (int i = 0; i < families; i++)
        {
            int a = i * spies + 1;

            for(int j = 1; j < spies; j++) {
                for (int k = j + 1; k <= spies; k++) {

                    writer.write(- a + " " + - (k + i * spies) + " 0\n");

                }
                a++;
            }
        }

        // familiile care se inteleg trebuie sa aiba spioni diferiti
        for (int i = 1; i <= families; i++) {
            for (int j = 1; j <= families; j++) {
               
                if (adjacencyMatrix[i][j] == 1) 
               {
                    for (int k = 1; k <= spies; k++) {
                        
                        int x = (-1) * (spies * (i - 1) + k);
                        int y = (-1) * (spies * (j - 1) + k);
                        
                        writer.write(x + " " + y + " 0\n");
                    }
                }
            }
        }

        writer.close();

    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        // TODO: extract the current problem's answer from the answer given by the oracle (oracleOutFilename)

        BufferedReader r = new BufferedReader(new FileReader(oracleOutFilename));

        oracleAnswer = r.readLine();

        if (oracleAnswer.equals("True")) {
            
            r.readLine();
            oracleSolution = r.readLine().split(" "); 
        }

        r.close();

    }

    @Override
    public void writeAnswer() throws IOException {
        // TODO: write the answer to the current problem (outFilename)
        
        FileWriter writer = new FileWriter(outFilename);
        
        // prima linie din fisierul de iesire contine "True" sau "False"
        writer.write(oracleAnswer + "\n");
        
        // daca problema are solutie, trebuie interpretat rezultatul
        if (oracleAnswer.equals("True")) {
            
            for (int i = 1; i <= variables; i++) {
                
                // se analizeaza cate o variabila din raspunsul dat de oracol
                int choice = Integer.parseInt(oracleSolution[i - 1]);
                // daca are valoarea true ( e pozitiva )
                if (choice > 0) {
                    // calculam se spion vizeaza variabila respectiva 
                    int spy = choice % spies;
                    if (spy == 0) {
                        writer.write(spies + " ");
                    } else {
                        writer.write(spy + " ");
                    }
                }
            }
        }
        writer.close();
    }
}
