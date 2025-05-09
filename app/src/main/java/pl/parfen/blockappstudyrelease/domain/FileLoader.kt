package pl.parfen.blockappstudyrelease.domain

import android.content.Context
import android.net.Uri
import android.text.Html
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.siegmann.epublib.epub.EpubReader
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.*

object FileLoader {

    private const val BUFFER_SIZE = 8192
    private const val EXT_TXT = "txt"
    private const val EXT_PDF = "pdf"
    private const val EXT_EPUB = "epub"
    private const val EXT_DOCX = "docx"
    private const val WHITESPACE_REGEX = "\\s{3,}"
    private const val WHITESPACE_REPLACEMENT = " "

    suspend fun extractTextFromAssetsPart(
        context: Context,
        filePath: String,
        startLine: Int,
        linesToRead: Int
    ): List<String> = withContext(ioDispatcher) {
        val lines = mutableListOf<String>()
        try {
            context.assets.open(filePath).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.lineSequence()
                        .filter { it.isNotBlank() }
                        .drop(startLine)
                        .take(linesToRead)
                        .forEach { line -> lines.add(line.replace(Regex(WHITESPACE_REGEX), WHITESPACE_REPLACEMENT)) }
                }
            }
        } catch (e: Exception) {
            lines.add("Error reading file: ${e.message}")
        }
        lines
    }

    suspend fun extractTextPart(
        context: Context,
        uri: Uri,
        ext: String,
        startLine: Int,
        linesToRead: Int
    ): List<String> = withContext(ioDispatcher) {
        when (ext.lowercase()) {
            EXT_TXT -> readTxt(context, uri, startLine, linesToRead)
            EXT_PDF -> readPdf(context, uri, startLine, linesToRead)
            EXT_EPUB -> readEpub(context, uri, startLine, linesToRead)
            EXT_DOCX -> readDocx(context, uri, startLine, linesToRead)
            else -> listOf("Unsupported file format: $ext")
        }
    }

    suspend fun countTotalLines(context: Context, uri: Uri, ext: String): Int =
        extractTextPart(context, uri, ext, 0, Int.MAX_VALUE).size

    suspend fun countTotalLinesFromAssets(context: Context, filePath: String): Int =
        extractTextFromAssetsPart(context, filePath, 0, Int.MAX_VALUE).size

    private fun readTxt(context: Context, uri: Uri, start: Int, limit: Int): List<String> {
        val lines = mutableListOf<String>()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.lineSequence()
                    .filter { it.isNotBlank() }
                    .drop(start)
                    .take(limit)
                    .forEach { line -> lines.add(line.replace(Regex(WHITESPACE_REGEX), WHITESPACE_REPLACEMENT)) }
            }
        }
        return lines
    }

    private fun readPdf(context: Context, uri: Uri, start: Int, limit: Int): List<String> {
        val lines = mutableListOf<String>()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            PdfReader(inputStream).use { reader ->
                PdfDocument(reader).use { pdf ->
                    for (page in 1..pdf.numberOfPages) {
                        val text = PdfTextExtractor.getTextFromPage(pdf.getPage(page))
                        text.lines()
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .forEach {
                                if (lines.size >= start && lines.size < start + limit)
                                    lines.add(it.replace(Regex(WHITESPACE_REGEX), WHITESPACE_REPLACEMENT))
                            }
                        if (lines.size >= start + limit) break
                    }
                }
            }
        }
        return lines
    }

    private fun readEpub(context: Context, uri: Uri, start: Int, limit: Int): List<String> {
        val lines = mutableListOf<String>()
        context.contentResolver.openInputStream(uri)?.use { input ->
            val book = EpubReader().readEpub(input)
            var counter = 0

            for (resource in book.contents) {
                val html = resource.reader.readText()
                val text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
                val paragraphs = text.lines()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                for (line in paragraphs) {
                    if (counter++ >= start && lines.size < limit) {
                        lines.add(line.replace(Regex(WHITESPACE_REGEX), WHITESPACE_REPLACEMENT))
                    }
                    if (lines.size >= limit) break
                }
                if (lines.size >= limit) break
            }
        }
        return lines
    }

    private fun readDocx(context: Context, uri: Uri, start: Int, limit: Int): List<String> {
        val lines = mutableListOf<String>()
        context.contentResolver.openInputStream(uri)?.use { input ->
            XWPFDocument(input).use { doc ->
                doc.paragraphs.map { it.text.trim() }
                    .filter { it.isNotEmpty() }
                    .drop(start)
                    .take(limit)
                    .forEach { lines.add(it.replace(Regex(WHITESPACE_REGEX), WHITESPACE_REPLACEMENT)) }
            }
        }
        return lines
    }

    private val ioDispatcher = Dispatchers.IO
}