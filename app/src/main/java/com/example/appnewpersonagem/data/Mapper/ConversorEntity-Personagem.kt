package com.example.appnewpersonagem.data.Mapper

import com.example.appnewpersonagem.data.PersonagemEntity
import personagem.Personagem
import raca.*

val racas: Map<String, Raca> = mapOf(
    "Alto Elfo" to AltoElfo(),
    "Anão" to Anao(),
    "Anão da Montanha" to AnaoDaMontanha(),
    "Anão da Colina" to AnaoDaColina(),
    "Drow" to Drow(),
    "Draconato" to Draconato(),
    "Elfo" to Elfo(),
    "Elfo da Floresta" to ElfoDaFloresta(),
    "Gnomo" to Gnomo(),
    "Gnomo da Floresta" to GnomoDaFloresta(),
    "Gnomo das Rochas" to GnomoDasRochas(),
    "Halfling" to Halfling(),
    "Halfling Pés-Leves" to HalflingPesLeves(),
    "Halfling Robusto" to HalflingRobusto(),
    "Humano" to Humano(),
    "Meio-Elfo" to MeioElfo(),
    "Meio-Orc" to MeioOrc(),
    "Tiefling" to Tiefling()
)

fun Personagem.toEntity(): PersonagemEntity {
    return PersonagemEntity(
        nome = this.nome,
        raca = racas.filterValues { it == this.raca }.keys.toString().filterNot { it == '[' || it == ']' },
        forca = this.forca,
        destreza = this.destreza,
        constituicao = this.constituicao,
        inteligencia = this.inteligencia,
        sabedoria = this.sabedoria,
        carisma = this.carisma,
        vida = this.vida,
        nivel = this.nivel
    )
}

fun PersonagemEntity.toPersonagem(raca: String): Personagem {
    val personagem = Personagem(
        nome = this.nome,
        raca = racas.getValue(raca),
        forca = this.forca,
        destreza = this.destreza,
        constituicao = this.constituicao,
        inteligencia = this.inteligencia,
        sabedoria = this.sabedoria,
        carisma = this.carisma)
    personagem.vida = this.vida
    personagem.nivel = this.nivel
    return personagem
}