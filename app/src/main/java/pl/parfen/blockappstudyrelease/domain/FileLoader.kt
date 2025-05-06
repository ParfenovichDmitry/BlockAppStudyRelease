package pl.parfen.blockappstudyrelease.data.datasource.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.jsoup.Jsoup
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import java.io.*
import java.util.zip.ZipInputStream

object FileLoader {

    private const val TAG = "FileLoader"
    private const val BUFFER_SIZE = 8192

    suspend fun extractTextFromAssetsPart(context: Context, filePath: String, startLine: Int, linesToRead: Int): List<String> =
        withContext(Dispatchers.IO) {
            val lines = mutableListOf<String>()
            try {
                context.assets.open(filePath).use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        reader.lineSequence()
                            .filter { it.isNotBlank() }
                            .drop(startLine)
                            .take(linesToRead)
                            .forEach { line -> lines.add(line.replace(Regex("\\s{3,}"), " ")) }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error reading asset: ${e.message}")
                lines.add("Error reading file: ${e.message}")
            }
            lines
        }

    suspend fun extractTextPart(context: Context, uri: Uri, ext: String, startLine: Int, linesToRead: Int): List<String> =
        withContext(Dispatchers.IO) {
            when (ext.lowercase()) {
                "txt" -> readTxt(context, uri, startLine, linesToRead)
                "epub" -> readEpub(context, uri, startLine, linesToRead)
                "pdf" -> readPdf(context, uri, startLine, linesToRead)
                "docx" -> readDocx(context, uri, startLine, linesToRead)
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
                    .forEach { line -> lines.add(line.replace(Regex("\\s{3,}"), " ")) }
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
                        text.split("\n")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .forEach {
                                if (lines.size >= start && lines.size < start + limit)
                                    lines.add(it.replace(Regex("\\s{3,}"), " "))
                            }
                        if (lines.size >= start + limit) break
                    }
                }
            }
        }
        return lines.drop(start).take(limit)
    }

    private fun readEpub(context: Context, uri: Uri, start: Int, limit: Int): List<String> {
        val lines = mutableListOf<String>()
        var current = 0
        context.contentResolver.openInputStream(uri)?.use { input ->
            ZipInputStream(BufferedInputStream(input)).use { zip ->
                var entry = zip.nextEntry
                while (entry != null && lines.size < limit) {
                    if (entry.name.endsWith(".html") || entry.name.endsWith(".xhtml")) {
                        val html = readZipEntry(zip)
                        val doc = Jsoup.parse(html)
                        doc.select("p, h1, h2, h3").map { it.text().trim() }
                            .filter { it.isNotEmpty() }
                            .forEach {
                                if (current++ >= start && lines.size < limit)
                                    lines.add(it.replace(Regex("\\s{3,}"), " "))
                            }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
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
                    .forEach { lines.add(it.replace(Regex("\\s{3,}"), " ")) }
            }
        }
        return lines
    }

    private fun readZipEntry(zip: ZipInputStream): String {
        val buffer = ByteArrayOutputStream()
        val data = ByteArray(BUFFER_SIZE)
        var count: Int
        while (zip.read(data).also { count = it } != -1) {
            buffer.write(data, 0, count)
        }
        return buffer.toString(Charsets.UTF_8.name())
    }
}
