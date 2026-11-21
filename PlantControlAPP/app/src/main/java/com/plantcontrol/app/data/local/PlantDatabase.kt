package com.plantcontrol.app.data.local

import com.plantcontrol.app.R
// Repositório centralizado de todas as plantas suportadas pelo sistema.
// Para adicionar uma nova planta, basta inserir um novo bloco PlantEntity nesta lista.
object PlantDatabase {
    val initialCatalog = listOf(
        PlantEntity(
            id = 1,
            name = "Tomate",
            scientificName = "Solanum lycopersicum",
            imageRes = R.drawable.ic_tomate, // Ícone que será mapeado para Icons.Rounded.LocalDining
            minHumidity = 40,
            maxHumidity = 60,
            lightRequirement = "Sol Pleno (6h a 8h/dia | 1000+ Lux)",
            irrigationType = "Gotejamento frequente e regular",
            soilType = "Solo areno-argiloso rico em matéria orgânica",
            fullDescription = "O tomateiro é uma planta herbácea de ciclo anual pertencente à família Solanaceae. Exige fornecimento hídrico equilibrado: oscilações bruscas de umidade durante a frutificação provocam podridão apical e rachadura dos frutos. O controle por histerese (40%-60%) é o padrão ótimo agronômico.",
            curiosities = "Originário da região dos Andes, foi considerado venenoso pelos europeus no século XVI."
        ),
        PlantEntity(
            id = 2,
            name = "Orquídea Phalaenopsis",
            scientificName = "Phalaenopsis spp.",
            imageRes = R.drawable.ic_orquidea_phalaenopsis,
            minHumidity = 30,
            maxHumidity = 50,
            lightRequirement = "Luz Difusa / Meia-sombra (Evitar sol direto)",
            irrigationType = "Rega espaçada com secagem intermediária",
            soilType = "Substrato leve com casca de pinus, carvão e esfagno",
            fullDescription = "Planta epífita que na natureza cresce aderida a troncos de árvores. Suas raízes apodrecem com facilidade se o substrato permanecer encharcado. Recomenda-se esperar secar moderadamente antes do próximo ciclo.",
            curiosities = "As raízes das orquídeas realizam fotossíntese quando expostas à luz."
        ),
        PlantEntity(
            id = 3,
            name = "Samambaia Americana",
            scientificName = "Nephrolepis exaltata",
            imageRes = R.drawable.ic_samambaia_americana,
            minHumidity = 60,
            maxHumidity = 80,
            lightRequirement = "Sombra com boa luminosidade indireta",
            irrigationType = "Irrigação constante e pulverização frequente",
            soilType = "Substrato altamente orgânico com boa retenção hídrica",
            fullDescription = "Planta nativa de florestas tropicais úmidas. Apresenta alta taxa de transpiração foliar e baixa tolerância ao estresse hídrico. A umidade do substrato deve ser mantida sempre elevada.",
            curiosities = "As samambaias existem há mais de 300 milhões de anos."
        ),
        PlantEntity(
            id = 4,
            name = "Suculenta Echeveria",
            scientificName = "Echeveria elegans",
            imageRes = R.drawable.ic_suculenta_echeveria,
            minHumidity = 15,
            maxHumidity = 30,
            lightRequirement = "Sol Pleno a Meia-sombra direta",
            irrigationType = "Irrigação muito espaçada",
            soilType = "Substrato arenoso com drenagem ultra rápida",
            fullDescription = "Espécie suculenta que funciona como reservatório hídrico. O excesso de água é a principal causa de mortalidade da planta. O acionamento só deve ocorrer quando o substrato estiver quase seco.",
            curiosities = "Possui uma camada protetora natural contra o sol e a chuva nas folhas."
        ),
        PlantEntity(
            id = 5,
            name = "Manjericão Italiano",
            scientificName = "Ocimum basilicum",
            imageRes = R.drawable.ic_manjericao_italiano,
            minHumidity = 45,
            maxHumidity = 65,
            lightRequirement = "Sol Pleno (pelo menos 5h diárias)",
            irrigationType = "Irrigação diária moderada",
            soilType = "Solo fértil, drenável e com adubação orgânica",
            fullDescription = "Erva aromática anual. Apresenta folhas tenras que murcham rapidamente se o solo secar abaixo de 40%, porém recupera o turgor prontamente após a irrigação.",
            curiosities = "Na Grécia antiga, o manjericão era considerado uma erva sagrada e nobre."
        ),
        PlantEntity(
            id = 6,
            name = "Costela-de-Adão",
            scientificName = "Monstera deliciosa",
            imageRes = R.drawable.ic_costela_de_adao,
            minHumidity = 50,
            maxHumidity = 70,
            lightRequirement = "Luz filtrada ou Meia-sombra",
            irrigationType = "Rega moderada",
            soilType = "Solo com turfa, fibra de coco e perlita",
            fullDescription = "Planta tropical de folhagem exuberante. Aprecia substrato ligeiramente úmido, mas não tolera solo compacto e saturado. Ideal para ambientes internos.",
            curiosities = "As aberturas nas suas folhas servem para permitir a passagem do vento."
        ),
        PlantEntity(
            id = 7,
            name = "Espada-de-São-Jorge",
            scientificName = "Dracaena trifasciata",
            imageRes = R.drawable.ic_espada_de_sao_jorge,
            minHumidity = 20,
            maxHumidity = 40,
            lightRequirement = "Tolerante a sombra profunda até sol direto (100-1200 Lux)",
            irrigationType = "Ciclos muito espaçados (a cada 10 a 20 dias)",
            soilType = "Substrato arenoso ou terra vegetal bem drenada",
            fullDescription = "Uma das plantas ornamentais mais resistentes existentes. Suporta longos períodos de seca extrema graças às suas reservas no rizoma. O excesso de água é seu único ponto fraco.",
            curiosities = "Consta na lista da NASA de plantas purificadoras de ar, com capacidade comprovada de filtrar benzeno, formaldeído e xileno do ambiente."
        ),
        PlantEntity(
            id = 8,
            name = "Jiboia Verde",
            scientificName = "Epipremnum aureum",
            imageRes = R.drawable.ic_jiboia_verde,
            minHumidity = 45,
            maxHumidity = 65,
            lightRequirement = "Meia-sombra ou Luz Indireta (300-700 Lux)",
            irrigationType = "Rega 1 a 2 vezes por semana",
            soilType = "Substrato leve, humoso e aerado",
            fullDescription = "Trepadeira tropical vigorosa e muito versátil. Suporta variações moderadas de irrigação, mas atinge seu melhor desenvolvimento com umidade controlada em torno de 50%.",
            curiosities = "Pode ser cultivada tanto na terra quanto diretamente na água (hidroponia) por tempo indeterminado."
        ),
        PlantEntity(
            id = 9,
            name = "Alecrim",
            scientificName = "Salvia rosmarinus",
            imageRes = R.drawable.ic_alecrim,
            minHumidity = 25,
            maxHumidity = 45,
            lightRequirement = "Sol Pleno constante (6h+ diárias | 1000+ Lux)",
            irrigationType = "Rega reduzida (tolerante à seca)",
            soilType = "Solo calcário, arenoso e de excelente drenagem",
            fullDescription = "Arbusto perene de origem mediterrânea adaptado a solos áridos e rochosos. Não tolera acúmulo de água junto ao colo da raiz, exigindo faixas baixas de umidade.",
            curiosities = "Estudos de neurociência apontam que o aroma do alecrim pode estimular a memória de trabalho e a concentração humana."
        ),
        PlantEntity(
            id = 10,
            name = "Pimenta Biquinho",
            scientificName = "Capsicum chinense",
            imageRes = R.drawable.ic_pimenta_biquinho,
            minHumidity = 40,
            maxHumidity = 60,
            lightRequirement = "Sol Pleno direto (6h/dia | 1000+ Lux)",
            irrigationType = "Irrigação regular sem encharcar",
            soilType = "Solo argilo-arenoso bem adubado",
            fullDescription = "Variedade brasileira muito popular por seu sabor doce e aroma marcante sem ardência excessiva. Demanda irrigação estável para não abortar flores na fase reprodutiva.",
            curiosities = "É originária do estado de Minas Gerais e conquistou a gastronomia internacional pelo seu formato cônico que lembra uma gota."
        ),
        PlantEntity(
            id = 11,
            name = "Zamioculca",
            scientificName = "Zamioculcas zamiifolia",
            imageRes = R.drawable.ic_zamioculca,
            minHumidity = 20,
            maxHumidity = 35,
            lightRequirement = "Sombra a Meia-sombra (Tolera baixa luminosidade | 150-400 Lux)",
            irrigationType = "Rega muito rara (a cada 15 ou 20 dias)",
            soilType = "Substrato ultra leve com perlita e casca",
            fullDescription = "Planta de folhagem brilhante originária da África Oriental. Armazena grandes quantidades de água em seus caules e batatas subterrâneas (tubérculos), sofrendo rápido apodrecimento se regada com frequência.",
            curiosities = "Suas folhas naturalmente parecem enceradas, dando a impressão de ser uma planta artificial mesmo em perfeito estado de saúde."
        ),
        PlantEntity(
            id = 12,
            name = "Cacto Mandacaru",
            scientificName = "Cereus jamacaru",
            imageRes = R.drawable.ic_cacto_mandacaru,
            minHumidity = 10,
            maxHumidity = 25,
            lightRequirement = "Sol Pleno intenso (1200+ Lux)",
            irrigationType = "Rega esporádica (somente após solo 100% seco)",
            soilType = "Solo arenoso, pedregoso e pobre em matéria orgânica",
            fullDescription = "Símbolo da caatinga brasileira, possui incrível capacidade de retenção hídrica em seu caule fotossintético. Os ciclos de irrigação são bastante espaçados.",
            curiosities = "Suas flores brancas desabrocham apenas durante a noite e murcham assim que o sol nasce na manhã seguinte."
        ),
        PlantEntity(
            id = 13,
            name = "Hortelã-Comum",
            scientificName = "Mentha spicata",
            imageRes = R.drawable.ic_hortela_comum,
            minHumidity = 55,
            maxHumidity = 75,
            lightRequirement = "Meia-sombra a Sol Pleno moderado (600-900 Lux)",
            irrigationType = "Irrigação diária frequente",
            soilType = "Solo muito rico em matéria orgânica e úmido",
            fullDescription = "Planta rizomatosa de crescimento rápido e invasivo. Gosta de substrato constantemente úmido e responde muito bem à irrigação automática por faixas elevadas.",
            curiosities = "Se plantada no mesmo canteiro que outras espécies, suas raízes se espalham tão depressa que podem sufocar plantas vizinhas."
        ),
        PlantEntity(
            id = 14,
            name = "Lavanda Francesa",
            scientificName = "Lavandula dentata",
            imageRes = R.drawable.ic_lavanda_francesa,
            minHumidity = 25,
            maxHumidity = 45,
            lightRequirement = "Sol Pleno obrigatório (6h+ | 1000+ Lux)",
            irrigationType = "Rega moderada a baixa",
            soilType = "Solo calcário, arenoso e de secagem rápida",
            fullDescription = "Subarbusto aromático de flores arroxeadas. Aprecia boa circulação de ar, sol quente e solo seco entre as regas. O excesso de umidade na raiz é fatal.",
            curiosities = "O óleo essencial extraído da lavanda é um dos relaxantes naturais mais estudados e utilizados na aromaterapia mundial."
        )
    )
}