package br.edu.ifes.si.tpa.trabalho1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import br.edu.ifes.si.tpa.trabalho1.algoritmos.AlgoritmoMaiorClique;
import br.edu.ifes.si.tpa.trabalho1.estruturas.Aresta;
import br.edu.ifes.si.tpa.trabalho1.estruturas.Grafo;
import br.edu.ifes.si.tpa.trabalho1.estruturas.In;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.control.Slider;


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
    
    // Animação:
    private List<Integer> cliqueAtual = new ArrayList<>();
    private List<Integer> maiorClique = new ArrayList<>();
    
    private long delay = 400;

    public static void main(String[] args) {
        
        // Primeiro grafo:
        String arquivo = "../_dados/grafo_sindrome_gripe.txt";;
        
        // Segundo grafo (Comente o arquivo acima e utilize o de baixo):
        //String arquivo = "../_dados/grafo_comorbidades.txt";
        
        // Grafo do teste de mesa:
        //String arquivo = "../_dados/grafo_teste_mesa.txt";
        
        String arquivoMap = arquivo.replace(".txt", ".map");
        
        In in = new In(arquivo);
        G = new Grafo(in);
        
        symptomMap = new SymptomMap(arquivoMap);
        
        launch();
    }

    @Override
    public void start(Stage stage) {
        
        Label labelVelocidade = new Label("Velocidade:");
        labelVelocidade.setLayoutX(20);
        labelVelocidade.setLayoutY(60);

        Slider sliderDelay = new Slider(0, 1, 0.5);
        sliderDelay.setLayoutX(120);
        sliderDelay.setLayoutY(60);
        sliderDelay.setPrefWidth(200);
        
        sliderDelay.setShowTickLabels(false);
        sliderDelay.setShowTickMarks(false);
        
        
        /*
        sliderDelay.setMin(1000);  // valor maior = lento
        sliderDelay.setMax(10);    // valor menor = rápido
        sliderDelay.setValue(400); // inicial
*/
        int delayMin = 10;     // mais rápido
        int delayMax = 1000; 


        sliderDelay.valueProperty().addListener((obs, oldV, newV) -> {
            double t = newV.doubleValue(); // varia de 0 a 1
            delay = (int) (delayMax - t * (delayMax - delayMin));  
            // esquerda (0) => delayMax
            // direita (1) => delayMin
        });



        btnClique.setLayoutX(20);
        btnClique.setLayoutY(20);

        btnClique.setOnAction((ActionEvent e) -> {
            // 1. Limpa o estado anterior
            cliqueAtual.clear();
            maiorClique.clear();
            desenharGrafo(); 

            // 2. Desativa o botão para não clicar de novo
            btnClique.setDisable(true); 

            // 3. Cria e inicia a Thread do algoritmo
            Thread algorithmThread = new Thread(() -> {
                
                // 4. Chama a função recursiva (que vamos colar no Passo 4)
                encontrarMaiorClique(0); 

                // 5. Quando o algoritmo TERMINA, destaca o resultado final
                Platform.runLater(() -> {
                    destacarClique(maiorClique); 
                    btnClique.setDisable(false); 
                });
            });
            algorithmThread.setDaemon(true);
            algorithmThread.start();
        });

        root.getChildren().add(btnClique);
        root.getChildren().add(edgesGroup);
        root.getChildren().add(nodesGroup);
        root.getChildren().addAll(labelVelocidade, sliderDelay);

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
        for (int v = 0; v < G.V(); v++) {
            // O Círculo do vértice 'v' é o item (v * 3) no nodesGroup
            Circle circle = (Circle) nodesGroup.getChildren().get(v * 3);

            if (cliqueSet.contains(v)) {
                circle.setFill(Color.web("#FA0DA4"));
                circle.setStroke(Color.BLACK);
                circle.setStrokeWidth(2);
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
        double r = 180;

        for (int i = 0; i < n; i++) {
            double ang = 2 * Math.PI * i / n;
            double x = cx + r * Math.cos(ang);
            double y = cy + r * Math.sin(ang);
            posicoes.put(i, new double[]{x, y});
        }
    }
    
    // ------------------------------------------------------
    // Animação do grafo detalhada
    // ------------------------------------------------------
    
    private void pausarExecucao() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void destacarVertice(int vertice, Color cor) {
        if (vertice >= 0 && (vertice * 3) < nodesGroup.getChildren().size()) {
            Circle circle = (Circle) nodesGroup.getChildren().get(vertice * 3);
            circle.setFill(cor);
        }
    }
    
    private void encontrarMaiorClique(int vertice) {
        
        Platform.runLater(() -> destacarVertice(vertice, Color.YELLOW));
        pausarExecucao();

        if (vertice == G.V()) {
            if (cliqueAtual.size() > maiorClique.size()) {
                maiorClique = new ArrayList<>(cliqueAtual);
                
            }
            return;
        }

        boolean podeAdicionar = true;
        for (int v : cliqueAtual) {
            if (!G.existeArestaEntre(vertice, v)) {
                podeAdicionar = false;
                break;
            }
        }

        if (podeAdicionar) {
            cliqueAtual.add(vertice);
            Platform.runLater(() -> destacarVertice(vertice, Color.BLUEVIOLET));
            pausarExecucao();

            encontrarMaiorClique(vertice + 1); 

            cliqueAtual.remove(cliqueAtual.size() - 1);
            Platform.runLater(() -> destacarVertice(vertice, Color.LIGHTGRAY));
            pausarExecucao();
        }

        Color corOriginal = mapColors.getColor(vertice);
        Platform.runLater(() -> destacarVertice(vertice, corOriginal));

        encontrarMaiorClique(vertice + 1);
    }
}
