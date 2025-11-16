package br.edu.ifes.si.tpa.trabalho1;

import br.edu.ifes.si.tpa.trabalho1.algoritmos.AlgoritmoMaiorClique;
import br.edu.ifes.si.tpa.trabalho1.estruturas.Aresta;
import br.edu.ifes.si.tpa.trabalho1.estruturas.Grafo;
import br.edu.ifes.si.tpa.trabalho1.estruturas.In;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
import javafx.scene.shape.Circle;

public class Main extends Application {

    public static Grafo G;
    public static AlgoritmoMaiorClique algoritmoClique;
    
    public static SymptomMap symptomMap;

    private Map<Integer, double[]> posicoes = new HashMap<>();

    Group root = new Group();
    Group edgesGroup = new Group();
    Group nodesGroup = new Group();

    Button btnClique = new Button("Executar Maior Clique");

    MapColors mapColors;

    public static void main(String[] args) {
        // Primeiro grafo:
        String arquivo = args[0].substring(9);
        String arquivoMap = args[1].substring(9);
        
        In in = new In(arquivo);
        G = new Grafo(in);
        
        symptomMap = new SymptomMap(arquivoMap);
        
        launch();
        
        // Segundo grafo:
        

        /*
        String arquivo = args[0].substring(9);
        In in = new In(arquivo);
        G = new Grafo(in);
        launch();
        */
    }

    @Override
    public void start(Stage stage) {

        btnClique.setLayoutX(20);
        btnClique.setLayoutY(20);

        btnClique.setOnAction((ActionEvent e) -> {
            algoritmoClique = new AlgoritmoMaiorClique(G);
            List<Integer> clique = algoritmoClique.encontrarMaiorClique();
            destacarClique(clique);
        });

        root.getChildren().add(btnClique);
        root.getChildren().add(edgesGroup);
        root.getChildren().add(nodesGroup);

        posicionarVerticesEmCirculo();
        mapColors = new MapColors(G);
        desenharGrafo();

        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Grafo Maior Clique");
        stage.setScene(scene);
        stage.show();
    }

    // ------------------------------------------------------
    // DESENHAR GRAFO
    // ------------------------------------------------------

    private void desenharGrafo() {

        edgesGroup.getChildren().clear();
        nodesGroup.getChildren().clear();

        // Desenhar arestas
        for (int v = 0; v < G.V(); v++) {
            for (Aresta a : G.adj(v)) {

                int w = (a.getV1() == v ? a.getV2() : a.getV1());

                if (v < w) {

                    double[] p1 = posicoes.get(v);
                    double[] p2 = posicoes.get(w);

                    Line line = new Line(p1[0], p1[1], p2[0], p2[1]);
                    line.setStroke(Color.web("#DAE9F8"));
                    line.setStrokeWidth(2);

                    edgesGroup.getChildren().add(line);
                }
            }
        }

        // Desenhar nós
        for (int v = 0; v < G.V(); v++) {

            double[] pos = posicoes.get(v);

            Circle circle = new Circle();
            circle.setCenterX(pos[0]);
            circle.setCenterY(pos[1]);
            circle.setRadius(20); 
            circle.setFill(mapColors.getColor(v));
            circle.setStroke(Color.BLACK);
            
            // === ID dentro da célula ===
            Text idText = new Text(String.valueOf(v));
            idText.setFont(new Font(14));
            idText.setX(pos[0] - 4);      
            idText.setY(pos[1] + 5);      

            String nome = Main.symptomMap.getNome(v);

            Text nameText = new Text(nome);
            nameText.setFont(new Font(12));
            
            double textWidth = nameText.getLayoutBounds().getWidth();
            nameText.setX(pos[0] - textWidth / 2); 
            nameText.setY(pos[1] + 35);   

            nodesGroup.getChildren().add(circle);
            nodesGroup.getChildren().add(idText);
            
            nodesGroup.getChildren().add(nameText);
        }
    }

    // ------------------------------------------------------
    // DESTACAR MAIOR CLIQUE
    // ------------------------------------------------------

    private void destacarClique(List<Integer> clique) {
        Set<Integer> cliqueSet = new HashSet<>(clique);

        // Pintar arestas do clique
        for (int i = 0; i < edgesGroup.getChildren().size(); i++) {

            Line linha = (Line) edgesGroup.getChildren().get(i);

            int v = encontrarVerticePorCoordenada(linha.getStartX(), linha.getStartY());
            int w = encontrarVerticePorCoordenada(linha.getEndX(), linha.getEndY());

            if (cliqueSet.contains(v) && cliqueSet.contains(w)) {
                linha.setStroke(Color.web("#FA0DA4"));
                linha.setStrokeWidth(4);
            }
        }

        // Pintar nós
        for (int i = 0; i < nodesGroup.getChildren().size(); i += 2) {

            Rectangle rect = (Rectangle) nodesGroup.getChildren().get(i);
            Text tx = (Text) nodesGroup.getChildren().get(i + 1);

            int id = symptomMap.getId(tx.getText());

            if (cliqueSet.contains(id)) {
                rect.setFill(Color.web("#FA0DA4"));
                rect.setStroke(Color.BLACK);
                rect.setStrokeWidth(2);
            }
        }
    }

    private int encontrarVerticePorCoordenada(double x, double y) {
        for (Map.Entry<Integer, double[]> e : posicoes.entrySet()) {
            double[] p = e.getValue();
            if (Math.abs(p[0] - x) < 2 && Math.abs(p[1] - y) < 2) {
                return e.getKey();
            }
        }
        return -1;
    }

    // ------------------------------------------------------
    // POSICIONAMENTO EM CÍRCULO
    // ------------------------------------------------------

    private void posicionarVerticesEmCirculo() {
        int n = G.V();
        double cx = 450, cy = 350;
        double r = 280;

        for (int i = 0; i < n; i++) {
            double ang = 2 * Math.PI * i / n;
            double x = cx + r * Math.cos(ang);
            double y = cy + r * Math.sin(ang);
            posicoes.put(i, new double[]{x, y});
        }
    }
}
