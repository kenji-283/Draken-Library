package com.example.data.datasource

import com.example.data.model.IdolEntity

/**
 * Fuente de datos de muestra para el Salón de los Ídolos de Draken's Library.
 */
object IdolsDataSource {

    val IDOLS: List<IdolEntity> = listOf(
        IdolEntity(
            id = "idol-nietzsche",
            nombre = "Friedrich Nietzsche",
            rutaFoto = "img_idol_nietzsche",
            epoca = "1844 - 1900",
            corriente = "Filosofía Trágica / Vitalismo",
            fraseCelebre = "Aquel que tiene un porqué para vivir se puede enfrentar a todos los cómos.",
            obrasPrincipales = "Así habló Zaratustra, Más allá del bien y del mal, El ocaso de los ídolos, La genealogía de la moral, El nacimiento de la tragedia",
            biografia = "Filósofo, poeta, músico y filólogo alemán, cuya obra ha ejercido una profunda influencia en la historia intelectual contemporánea. Desafió los fundamentos del cristianismo y la moral tradicional, proclamó la transvaloración de todos los valores y concibió ideas revolucionarias como el Übermensch (Superhombre), la voluntad de poder y el eterno retorno. Su estilo aforístico y apasionado lo convierte en una de las cumbres estéticas del pensamiento universal.",
            porqueMeEncanto = "Su prosa es un martillo incendiario que destruye la complacencia. Nietzsche no escribe para ser simplemente leído, sino para desafiar tu propia existencia. Su defensa de la vida frente al nihilismo pasivo y su exigencia de forjarse a uno mismo ante el dolor más profundo transforman la lectura de su obra en una experiencia existencial ineludible."
        ),
        IdolEntity(
            id = "idol-borges",
            nombre = "Jorge Luis Borges",
            rutaFoto = "img_idol_borges",
            epoca = "1899 - 1986",
            corriente = "Ficción Filosófica / Ultraísmo",
            fraseCelebre = "Siempre imaginé que el Paraíso sería algún tipo de biblioteca.",
            obrasPrincipales = "Ficciones, El Aleph, El libro de arena, Inquisiciones, El hacedor",
            biografia = "Escritor, ensayista, poeta y traductor argentino, considerado una de las figuras cumbre de la literatura en lengua española y universal del siglo XX. Creador de un universo literario único poblado de laberintos, espejos, paradojas temporales, teologías fantásticas y bibliotecas cósmicas. Su erudición deslumbrante y su prosa lúcida y concisa reinventaron el cuento fantástico y la crítica filosófica.",
            porqueMeEncanto = "Borges es el arquitecto supremo del intelecto literario. Cada uno de sus cuentos es una joya geométrica donde un solo párrafo encierra más conceptos filosóficos que tratados enteros. Leerlo es un juego sagrado donde el tiempo, la identidad y el infinito se convierten en poesía pura."
        ),
        IdolEntity(
            id = "idol-camus",
            nombre = "Albert Camus",
            rutaFoto = "img_idol_camus",
            epoca = "1913 - 1960",
            corriente = "Absurdismo / Existencialismo",
            fraseCelebre = "En medio del invierno, aprendí por fin que había en mí un verano invencible.",
            obrasPrincipales = "El extranjero, El mito de Sísifo, La peste, El hombre rebelde, La caída",
            biografia = "Novelista, ensayista, dramaturgo y filósofo francés nacido en Argelia. Galardonado con el Premio Nobel de Literatura en 1957. Desarrolló el pensamiento del 'absurdo': la contradicción irresoluble entre el anhelo humano de sentido y el silencio indiferente del universo. Frente a este absurdo, propuso la rebelión consciente, la solidaridad humana y la pasión por vivir.",
            porqueMeEncanto = "La honestidad moral inquebrantable de Camus es un faro en momentos de desesperanza. No ofrece falsos consuelos celestiales ni promesas vacías; nos enseña a mirar el abismo de frente con dignidad, nobleza y compasión hacia nuestros semejantes."
        ),
        IdolEntity(
            id = "idol-poe",
            nombre = "Edgar Allan Poe",
            rutaFoto = "img_drakens_banner",
            epoca = "1809 - 1849",
            corriente = "Romanticismo Oscuro / Gótico",
            fraseCelebre = "Los que sueñan de día son conscientes de muchas cosas que escapan a los que sueñan solo de noche.",
            obrasPrincipales = "El cuervo, Narraciones extraordinarias, El gato negro, El corazón delator, Los crímenes de la calle Morgue",
            biografia = "Escritor, poeta, crítico y periodista estadounidense. Renovador de la novela gótica, pionero del relato detectivesco moderno y maestro indiscutible del terror psicológico y la poesía melancólica.",
            porqueMeEncanto = "Su dominio del ritmo, la atmósfera opresiva y los rincones más sombríos de la psique humana. Poe convirtió el dolor y la belleza fúnebre en una sinfonía literaria que resuena intensamente en los amantes de las noches solitarias."
        ),
        IdolEntity(
            id = "idol-calderon",
            nombre = "Calderón de la Barca",
            rutaFoto = "img_drakens_banner",
            epoca = "1600 - 1681",
            corriente = "Siglo de Oro Español / Teatro Barroco",
            fraseCelebre = "Que toda la vida es sueño, y los sueños, sueños son.",
            obrasPrincipales = "La vida es sueño, El gran teatro del mundo, El alcalde de Zalamea, La dama duende",
            biografia = "Sacerdote, poeta y dramaturgo del Siglo de Oro español. Es el máximo exponente del teatro filosófico y alegórico barroco, perfeccionando la métrica y la profundidad conceptual del drama español.",
            porqueMeEncanto = "La grandeza filosófica con la que cuestiona la realidad humana, el destino y el libre albedrío a través de versos sonoros y sublimes que nunca pierden su vigencia."
        ),
        IdolEntity(
            id = "idol-sagan",
            nombre = "Carl Sagan",
            rutaFoto = "img_drakens_banner",
            epoca = "1934 - 1996",
            corriente = "Divulgación Científica / Astrofísica",
            fraseCelebre = "En algún lugar, algo increíble está esperando ser descubierto.",
            obrasPrincipales = "Cosmos, El mundo y sus demonios, Un punto azul pálido, Contacto, Los dragones del Edén",
            biografia = "Astrónomo, astrofísico, cosmólogo y divulgador científico estadounidense. Promotor incansable del pensamiento crítico, la exploración espacial y la búsqueda de vida extraterrestre.",
            porqueMeEncanto = "Su capacidad única para combinar el rigor del método científico con un sentido de asombro poético casi místico ante la inmensidad del universo."
        )
    )

    fun getById(id: String): IdolEntity? {
        return IDOLS.firstOrNull { it.id == id }
    }
}

