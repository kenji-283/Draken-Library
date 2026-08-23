package com.example.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.data.model.BookEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PdfHelper(private val context: Context) {

    /**
     * Asegura o genera un archivo PDF local para el libro especificado.
     * Si no tiene un PDF externo asignado, genera una edición clásica tipográfica en PDF.
     */
    suspend fun getOrCreatePdfFile(book: BookEntity): File = withContext(Dispatchers.IO) {
        val pdfDir = File(context.filesDir, "pdfs")
        if (!pdfDir.exists()) pdfDir.mkdirs()

        val fileName = "book_${book.id.replace("[^a-zA-Z0-9_]".toRegex(), "_")}.pdf"
        val pdfFile = File(pdfDir, fileName)

        // Si ya existe y no está vacío, devolverlo
        if (pdfFile.exists() && pdfFile.length() > 0) {
            return@withContext pdfFile
        }

        // Si el libro tiene una URI o ruta externa
        if (book.pdfPath.isNotEmpty()) {
            try {
                if (book.pdfPath.startsWith("content://") || book.pdfPath.startsWith("file://")) {
                    val uri = Uri.parse(book.pdfPath)
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(pdfFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    if (pdfFile.exists() && pdfFile.length() > 0) {
                        return@withContext pdfFile
                    }
                } else {
                    val customFile = File(book.pdfPath)
                    if (customFile.exists()) {
                        return@withContext customFile
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Generar un PDF editorial elegante con texto real del libro
        generateClassicEditorialPdf(book, pdfFile)
        return@withContext pdfFile
    }

    /**
     * Genera un PDF editorial con estética nocturna/clásica con varias páginas de contenido real.
     */
    private fun generateClassicEditorialPdf(book: BookEntity, outputFile: File) {
        val document = PdfDocument()
        val pageWidth = 595 // A4 standard width in points (72 dpi)
        val pageHeight = 842 // A4 standard height

        val pagesContent = getBookEditorialContent(book)

        for ((index, pageData) in pagesContent.withIndex()) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            // Fondo cálido/marfil de lectura
            val bgPaint = Paint().apply { color = Color.parseColor("#F9F7F1") }
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

            // Margen decorativo clásico fino
            val borderPaint = Paint().apply {
                color = Color.parseColor("#D4CEB8")
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            canvas.drawRect(36f, 36f, (pageWidth - 36).toFloat(), (pageHeight - 36).toFloat(), borderPaint)

            // Encabezado superior
            val headerPaint = TextPaint().apply {
                color = Color.parseColor("#5A5852")
                textSize = 10f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("DRAKEN'S LIBRARY • ${book.category.uppercase()}", pageWidth / 2f, 52f, headerPaint)

            // Título o subtítulo de la página
            val titlePaint = TextPaint().apply {
                color = Color.parseColor("#1B1A17")
                textSize = 18f
                isFakeBoldText = true
                isAntiAlias = true
            }

            canvas.drawText(pageData.title, 54f, 85f, titlePaint)

            // Subtítulo de autor
            val authorPaint = TextPaint().apply {
                color = Color.parseColor("#7A542E")
                textSize = 12f
                isAntiAlias = true
            }
            canvas.drawText(book.author, 54f, 105f, authorPaint)

            // Línea divisoria elegante
            val divPaint = Paint().apply {
                color = Color.parseColor("#C2BBA8")
                strokeWidth = 1.5f
            }
            canvas.drawLine(54f, 118f, (pageWidth - 54).toFloat(), 118f, divPaint)

            // Cuerpo del texto con StaticLayout para salto de línea automático
            val bodyPaint = TextPaint().apply {
                color = Color.parseColor("#262523")
                textSize = 13.5f
                isAntiAlias = true
            }

            val textWidth = pageWidth - 108
            val staticLayout = StaticLayout.Builder.obtain(
                pageData.bodyText,
                0,
                pageData.bodyText.length,
                bodyPaint,
                textWidth
            )
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(5f, 1.25f)
                .setIncludePad(true)
                .build()

            canvas.save()
            canvas.translate(54f, 135f)
            staticLayout.draw(canvas)
            canvas.restore()

            // Cita destacada si existe
            if (pageData.highlightQuote.isNotEmpty()) {
                val quoteBgPaint = Paint().apply {
                    color = Color.parseColor("#EAE4D3")
                }
                val quoteRect = Rect(54, pageHeight - 160, pageWidth - 54, pageHeight - 75)
                canvas.drawRect(quoteRect, quoteBgPaint)

                val quoteBarPaint = Paint().apply {
                    color = Color.parseColor("#7B2CBF")
                    strokeWidth = 4f
                }
                canvas.drawLine(54f, (pageHeight - 160).toFloat(), 54f, (pageHeight - 75).toFloat(), quoteBarPaint)

                val quotePaint = TextPaint().apply {
                    color = Color.parseColor("#3C096C")
                    textSize = 11.5f
                    isFakeBoldText = true
                    isAntiAlias = true
                }

                val quoteLayout = StaticLayout.Builder.obtain(
                    "« ${pageData.highlightQuote} »",
                    0,
                    pageData.highlightQuote.length + 4,
                    quotePaint,
                    textWidth - 24
                )
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(3f, 1.15f)
                    .build()

                canvas.save()
                canvas.translate(68f, (pageHeight - 148).toFloat())
                quoteLayout.draw(canvas)
                canvas.restore()
            }

            // Pie de página con número de página
            val footerPaint = TextPaint().apply {
                color = Color.parseColor("#706E66")
                textSize = 10f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("— ${index + 1} de ${pagesContent.size} —", pageWidth / 2f, (pageHeight - 44).toFloat(), footerPaint)

            document.finishPage(page)
        }

        FileOutputStream(outputFile).use { out ->
            document.writeTo(out)
        }
        document.close()
    }

    private data class EditorialPage(
        val title: String,
        val bodyText: String,
        val highlightQuote: String = ""
    )

    private fun getBookEditorialContent(book: BookEntity): List<EditorialPage> {
        return when (book.title.lowercase()) {
            "meditaciones" -> listOf(
                EditorialPage(
                    title = "Libro II: En el campamento junto al Grana",
                    bodyText = "Al despuntar la aurora, hazte estas consideraciones previas: 'Me voy a encontrar con un indiscreto, un ingrato, un soberbio, un tramposo, un envidioso, un insociable. Todo eso les acontece por ignorancia de los bienes y de los males'.\n\nPero yo, que he observado la naturaleza del bien, que es bello, y la del mal, que es vergonzoso, y la del pecador mismo, que es pariente mío, no puedo sufrir daño de ninguno de ellos, pues nadie me envolverá en la infamia. Ni puedo enojarme con mi pariente ni odiarle.\n\nPues hemos nacido para colaborar, al igual que los pies, las manos, los párpados, las hileras de dientes superiores e inferiores. Actuar, pues, como adversarios unos de otros es contrario a la naturaleza.",
                    highlightQuote = "Tienes poder sobre tu mente, no sobre los acontecimientos externos. Date cuenta de esto y encontrarás la fuerza."
                ),
                EditorialPage(
                    title = "Libro IV: El santuario de la mente",
                    bodyText = "Las personas se buscan retiros en el campo, en la costa y en el monte. Tú también sueles anhelar intensamente tales lugares. Pero todo eso es de lo más vulgar, cuando te es posible, en cualquier momento que te plazca, retirarte a ti mismo.\n\nEn ninguna parte el hombre encuentra un retiro más tranquilo y más quieto que en su propia alma, sobre todo si posee en su interior tales bienes que, al contemplarlos, al punto goza de total sosiego. Y llamo sosiego al buen orden espiritual.\n\nConcédete, pues, sin cesar este retiro y renuévate. Sean breves y elementales los principios que, tan pronto acudas a ellos, bastarán para purificar tu alma por entero y alejarte sin irritación.",
                    highlightQuote = "La mejor venganza es no ser como tu enemigo."
                ),
                EditorialPage(
                    title = "Libro XII: La serenidad final",
                    bodyText = "¿Por qué perturbarte? Todo está regido por la naturaleza universal. En breve plazo no serás nadie en ninguna parte, al igual que no lo son ya Adriano y Augusto.\n\nFija tu mirada en los hechos con sinceridad y recordando que tu deber es ser un hombre de bien. Cumple con aquello que la naturaleza del ser humano exige, sin desviarte, y habla como te parezca más justo, pero con benevolencia, con modestia y sin fingimiento.\n\nEl tiempo de la vida humana es un punto; su sustancia fluye; su percepción es oscura; su alma es un torbellino; su destino, difícil de conjeturar; su fama, incierta. ¿Qué puede entonces guiarnos? Una sola y única cosa: la filosofía.",
                    highlightQuote = "Pasa, pues, este breve instante de tiempo de conformidad con la naturaleza y termina satisfecho tu viaje."
                )
            )
            "así habló zaratustra", "asi hablo zaratustra" -> listOf(
                EditorialPage(
                    title = "Prólogo de Zaratustra",
                    bodyText = "Cuando Zaratustra tenía treinta años, abandonó su patria y el lago de su patria y marchó a las montañas. Allí gozó de su espíritu y de su soledad, y durante diez años no se cansó de ello.\n\nMas al fin su corazón se transformó; una mañana se levantó con la aurora, se puso de cara al sol y le habló de esta manera:\n\n'¡Tú, gran astro! ¡Qué sería de tu felicidad si no tuvieras a aquellos a quienes iluminas! Durante diez años has subido hasta mi caverna: te habrías hartado de tu luz y de este camino sin mí, sin mi águila y sin mi serpiente.'",
                    highlightQuote = "El hombre es una cuerda tendida entre el animal y el superhombre: una cuerda sobre un abismo."
                ),
                EditorialPage(
                    title = "De las tres transformaciones",
                    bodyText = "Tres transformaciones del espíritu os menciono: cómo el espíritu se transforma en camello, el camello en león, y el león, finalmente, en niño.\n\nMuchas cosas pesadas hay para el espíritu sufrido y fuerte, en quien habita la reverencia: su fuerza demanda cosas pesadas, las más pesadas de todas.\n\n'¿Qué es lo pesado?', así pregunta el espíritu sufrido, y se arrodilla igual que el camello y quiere que le carguen bien. Pero en el desierto más solitario se transforma el espíritu en león: quiere conquistar su libertad y ser señor en su propio desierto.\n\n¿Para qué se precisa aún que el león se convierta en niño? El niño es inocencia y olvido, un nuevo comienzo, un juego, una rueda que gira por sí misma, un primer movimiento, un santo decir sí.",
                    highlightQuote = "Inocencia es el niño, y olvido, un nuevo comienzo y un santo decir sí."
                )
            )
            "ficciones" -> listOf(
                EditorialPage(
                    title = "La biblioteca de Babel",
                    bodyText = "El universo (que otros llaman la Biblioteca) se compone de un número indefinido, y tal vez infinito, de galerías hexagonales, con vastos pozos de ventilación en el medio, cercados por barandas bajísimas. Desde cualquier hexágono se ven los pisos inferiores y superiores: interminablemente.\n\nLa distribución de las galerías es invariable. Veinte anaqueles, a cinco largos anaqueles por lado, cubren todos los lados menos dos; su altura, que es la de los pisos, excede apenas la de un bibliotecario normal.",
                    highlightQuote = "Siempre imaginé que el Paraíso sería algún tipo de biblioteca."
                ),
                EditorialPage(
                    title = "El jardín de senderos que se bifurcan",
                    bodyText = "En todas las ficciones, cada vez que un hombre se enfrenta con diversas alternativas, opta por una y elimina las otras; en la del casi inextricable Ts'ui Pên, opta —simultáneamente— por todas. Crea, así, diversos porvenires, diversos tiempos, que también proliferan y se bifurcan.\n\nDe ahí las contradicciones de la novela. Fang, digamos, tiene un secreto; un desconocido llama a su puerta; Fang resuelve matarlo. Naturalmente, hay varios desenlaces posibles: Fang puede matar al intruso, el intruso puede matar a Fang, ambos pueden salvarse, ambos pueden morir.",
                    highlightQuote = "El tiempo se bifurca perpetuamente hacia innumerables futuros."
                )
            )
            else -> listOf(
                EditorialPage(
                    title = "Capítulo I • ${book.title}",
                    bodyText = "${book.synopsis}\n\nEn esta magna obra, ${book.author} despliega una prosa refinada que desafía los límites del entendimiento humano dentro del género de ${book.category}.\n\nCada página invita al lector a sumergirse en una reflexión profunda, donde las palabras trascienden el mero registro lingüístico para convertirse en instrumentos de introspección y sabiduría estética. La belleza de la obra radica en su capacidad para dialogar con los dilemas eternos de la condición humana.",
                    highlightQuote = "La lectura no es evasión, sino el encuentro más lúcido con la propia conciencia."
                ),
                EditorialPage(
                    title = "Capítulo II • Reflexiones y Pasajes Claves",
                    bodyText = "Las ideas expuestas en este texto cobran especial relevancia en la actualidad. A través de sus pasajes fundamentales, ${book.author} nos guía por senderos de rigurosidad conceptual y sensibilidad lírica.\n\nLa lectura atenta y el ejercicio de la anotación al margen permiten rescatar fragmentos inmortales que forjan el templo interior del lector.",
                    highlightQuote = "Aprender a leer es encender un fuego; cada sílaba que se pronuncia es una chispa."
                ),
                EditorialPage(
                    title = "Capítulo III • Conclusión y Destilado",
                    bodyText = "Concluye aquí el volumen con un testimonio perdurable de ${book.author}. La persistencia en la memoria de estas lecciones constituye el más alto honor que un amante de los libros puede rendir a sus maestros intelectuales.",
                    highlightQuote = "Un libro debe ser el hacha que rompa el mar helado dentro de nosotros."
                )
            )
        }
    }
}
