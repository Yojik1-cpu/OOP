package service.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;

public class JacocoReportParser {

    public static Double parseCoverage(Path reportFile) {
        if (reportFile == null || !Files.exists(reportFile)) {
            return null;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(reportFile.toFile());

            NodeList counters = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < counters.getLength(); i++) {
                Node node = counters.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE && "counter".equals(node.getNodeName())) {
                    Element counter = (Element) node;
                    if ("INSTRUCTION".equals(counter.getAttribute("type"))) {
                        double missed = Double.parseDouble(counter.getAttribute("missed"));
                        double covered = Double.parseDouble(counter.getAttribute("covered"));
                        double total = missed + covered;
                        if (total > 0) {
                            return covered / total;
                        }
                    }
                }
            }
        } catch (Exception e) {
            SimpleLogger.warn("Failed to parse JaCoCo report " + reportFile + ": " + e.getMessage());
        }
        return null;
    }
}
