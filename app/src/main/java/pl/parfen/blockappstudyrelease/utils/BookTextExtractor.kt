package pl.parfen.blockappstudyrelease.utils

import android.content.Context
import pl.parfen.blockappstudyrelease.data.model.Book
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy
import nl.siegmann.epublib.epub.EpubReader

import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor as DocExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.extractor.XWPFWordExtractor as DocxExtractor
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

object BookTextExtractor {

    fun extractText(context: Context, book: Book): String {
        val extension = book.file.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "txt" -> extractTxt(book.file)
            "pdf" -> extractPdf(book.file)
            "doc", "docx" -> extractDocOrDocx(book.file)
            "epub" -> extractEpub(book.file)
            else -> throw UnsupportedOperationException("Unsupported file format: .$extension")
        }
    }

    private fun extractTxt(filePath: String): String {
        return File(filePath).readText()
    }

    private fun extractPdf(filePath: String): String {
        val reader = PdfReader(filePath)
        val pdfDoc = PdfDocument(reader)
        val strategy = SimpleTextExtractionStrategy()
        val text = buildString {
            for (i in 1..pdfDoc.numberOfPages) {
                append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i), strategy)).append('\n')
            }
        }
        pdfDoc.close()
        return text
    }

    private fun extractDocOrDocx(filePath: String): String {
        val fis = FileInputStream(filePath)
        return if (filePath.endsWith(".docx")) {
            val docx = XWPFDocument(fis)
            val extractor = DocxExtractor(docx)
            val text = extractor.text
            extractor.close()
            docx.close()
            text
        } else {
            val doc = HWPFDocument(fis)
            val extractor = DocExtractor(doc)
            val text = extractor.text
            extractor.close()
            doc.close()
            text
        }
    }

    private fun extractEpub(filePath: String): String {
        val inputStream = FileInputStream(filePath)
        val book = EpubReader().readEpub(inputStream)
        return buildString {
            book.contents.forEach { resource ->
                try {
                    val reader = InputStreamReader(resource.inputStream)
                    append(reader.readText()).append("\n")
                } catch (_: Exception) { /* Пропускаем нечитабельные главы */ }
            }
        }
    }
}
