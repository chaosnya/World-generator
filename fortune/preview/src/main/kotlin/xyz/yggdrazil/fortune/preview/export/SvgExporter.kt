package xyz.yggdrazil.fortune.preview.export

import org.apache.batik.anim.dom.SVGDOMImplementation
import xyz.yggdrazil.fortune.Algorithm
import xyz.yggdrazil.fortune.preview.export.svg.SvgPainter
import xyz.yggdrazil.fortune.preview.gui.core.AlgorithmPainter
import xyz.yggdrazil.fortune.preview.gui.core.Config
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

object SvgExporter {

    @Throws(TransformerException::class, IOException::class)
    @JvmStatic fun main(args: Array<String>) {
        val impl = SVGDOMImplementation.getDOMImplementation()
        val svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
        val doc = impl.createDocument(svgNS, "svg", null)

        val svgRoot = doc.documentElement

        svgRoot.setAttributeNS(null, "width", "400")
        svgRoot.setAttributeNS(null, "height", "450")

        val rectangle = doc.createElementNS(svgNS, "rect")
        rectangle.setAttributeNS(null, "x", "10")
        rectangle.setAttributeNS(null, "y", "20")
        rectangle.setAttributeNS(null, "width", "100")
        rectangle.setAttributeNS(null, "height", "50")
        rectangle.setAttributeNS(null, "fill", "red")

        svgRoot.appendChild(rectangle)

        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        val source = DOMSource(doc)
        val file = File("/home/z/foo.svg")
        val fos = FileOutputStream(file)
        val result = StreamResult(fos)

        transformer.transform(source, result)

        fos.close()
    }

    @Throws(TransformerException::class, IOException::class)
    fun exportSVG(file: File, algorithm: Algorithm, config: Config,
                  width: Int, height: Int) {
        val impl = SVGDOMImplementation.getDOMImplementation()
        val svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
        val doc = impl.createDocument(svgNS, "svg", null)

        val svgRoot = doc.documentElement

        svgRoot.setAttributeNS(null, "width", Integer.toString(width))
        svgRoot.setAttributeNS(null, "height", Integer.toString(height))

        val painter = SvgPainter(doc, svgRoot)

        val algorithmPainter = AlgorithmPainter(algorithm,
                config, painter)

        algorithmPainter.width = width
        algorithmPainter.height = height
        algorithmPainter.paint()

        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        val source = DOMSource(doc)
        val fos = FileOutputStream(file)
        val result = StreamResult(fos)

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
        transformer.setOutputProperty(OutputKeys.METHOD, "xml")
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")

        transformer.transform(source, result)

        fos.close()
    }
}
