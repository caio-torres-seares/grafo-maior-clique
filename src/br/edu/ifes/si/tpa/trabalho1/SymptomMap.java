package br.edu.ifes.si.tpa.trabalho1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class SymptomMap {

    private Map<Integer, String> mapa = new HashMap<>();

    public SymptomMap(String arquivoMap) {
        carregar(arquivoMap);
    }

    private void carregar(String arquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {

            String linha;

            while ((linha = br.readLine()) != null) {

                if (linha.trim().isEmpty()) continue;

                String[] partes = linha.split(",");

                int id = Integer.parseInt(partes[0].trim());
                String nome = partes[1].trim();

                mapa.put(id, nome);
            }

        } catch (Exception e) {
            System.out.println("Erro ao carregar mapa de sintomas: " + e.getMessage());
        }
    }

    public String getNome(int id) {
        return mapa.getOrDefault(id, "ID " + id);
    }
    
    public int getId(String nome) {
        for (Map.Entry<Integer, String> e : mapa.entrySet()) {
            if (e.getValue().equals(nome)) return e.getKey();
        }
        return -1;
    }

}
