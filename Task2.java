// Copyright 2020
// Author: Matei Simtinică

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Task2
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task2 extends Task {
    // TODO: define necessary variables and/or data structures

    int families;
    int dim;
    int relations;
    int variables;

    Integer[][] adjacencyMatrix;

    // variabila unde se memoreaza clauzele pe masura se ce construiesc
    StringBuilder clauses = new StringBuilder("");
    // poate fi "True" sau "False"
    String oracleAnswer;
    // contine alegerea variabilelor, in caz ca oracleAnswer este True
    String[] oracleSolution;

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
        dim = Integer.parseInt(inputLine[2]);

        // se calculeaza numarul de variabile folosite pentru ccodificare
        variables = families * dim;

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

        // fiecare nod din clica trebuie sa fie ocupat
        for (int i = 1; i <= dim; i++) {
            for (int j = 0; j < families; j++) {
                
                clauses.append(((i + dim * j) + " "));
                clausesNumber++;
            }
            clauses.append(0 + "\n");
        }
        
        

        // un nod nu poate fi pe doua pozitii diferite in clica
        for (int i = 0; i < families; i++)
        {
           int a = i * dim + 1;
            
           for (int j = 1; j < dim; j++) {
                for (int k = j + 1; k <= dim; k++) {
                    
                    clauses.append(- a + " " + - (k + dim * i) +  " " + " 0\n");
                    clausesNumber++;

                }
                a++;
            }
        }

        // doua noduri care nu au muchie intre ele nu se pot 
        // afla amandoua in clica
        for (int i = 1; i <= families; i++) {
            for (int j = 1; j <= families; j++) {
                
                if (adjacencyMatrix[i][j] == 0 && i != j) {

                    for (int k = 1; k < dim; k++) {
                        for (int l = k + 1; l <= dim; l++) {
                           
                            clauses.append(- ((i - 1) * dim + k) + " " + - ((j - 1) * dim + l) + " " + " 0\n");
                            clausesNumber++;

                        }
                    }

                }
            }
        }

        // scrierea in fisier
        writer.write("p cnf " + variables + " " + clausesNumber + "\n");
        
        writer.write(clauses.toString());
        // resetarea StringBuilder-ului
        clauses.setLength(0);

        writer.close();

    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        // TODO: extract the current problem's answer from the answer given by the oracle (oracleOutFilename)

        BufferedReader r = new BufferedReader(new FileReader(oracleOutFilename));

        // raspunsul primit de la oracol
        oracleAnswer = r.readLine();

        // daca raspunsul este "True" se citeste solutia data
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
        // pe prima linie a fisierului va fi "True" sau "False"
        writer.write(oracleAnswer + "\n");
        
        if (oracleAnswer.equals("True")) {
            for (int i = 1; i <= variables; i++) {
                // se analizeaza cate o variabila din raspunsul dat de oracol
                int choice = Integer.parseInt(oracleSolution[i - 1]);
                 // daca are valoarea true ( e pozitiva )
                if (choice > 0) {
                    if (choice % dim != 0) {
                        
                        int family = Math.floorDiv(choice, dim)+ 1;
                        writer.write( family + " ");
                    
                    } else {
                        
                        int family = choice/dim;
                        writer.write(family + " ");
                    }

                }
            }
        }
        writer.close();
    }
}