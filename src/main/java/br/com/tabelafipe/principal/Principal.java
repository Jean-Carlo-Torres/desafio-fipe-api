package br.com.tabelafipe.principal;

import br.com.tabelafipe.models.Dados;
import br.com.tabelafipe.models.Modelos;
import br.com.tabelafipe.models.Veiculo;
import br.com.tabelafipe.services.ConsumoApi;
import br.com.tabelafipe.services.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner sc = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String API_URL = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu(){
        var menu = """
                == OPÇÕES ==
                [1] Carro
                [2] Moto
                [3] Caminhao
                
                Escolha uma opção:
                """;

        System.out.println(menu);
        int opcao = sc.nextInt();
        String endereco = null;
        switch (opcao) {
            case 1:
                endereco = API_URL + "carros/marcas";
                break;
            case 2:
                endereco = API_URL + "motos/marcas";
                break;
            case 3:
                endereco = API_URL + "caminhoes/marcas";
                break;
            default:
                System.out.println("Opção inválida!");
                break;
        }

        var json = consumoApi.obterDados(endereco);
        System.out.println(json);
        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.print("Informe o código para consuntar: ");
        sc.nextLine();
        var codigoMarca = sc.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumoApi.obterDados(endereco);
        var modelosLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca: ");
        modelosLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite uma parte do nome do carro para ser buscado:");
        var nomeVeiculo = sc.nextLine();

        List<Dados> modelosFiltrados = modelosLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos encontrados: ");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite o código do modelo para consultar o valor:");
        var codigoModelo = sc.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumoApi.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);

        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumoApi.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }
        System.out.println("Todos os veículos filtrados por ano: ");
        veiculos.forEach(System.out::println);
    }
}
