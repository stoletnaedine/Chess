import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Parser {

    private static final Logger LOG = LogManager.getLogger(Parser.class);

    /**
     * Анализирует аргументы запуска приложения и проставляет полям класса Chess необходимые
     * значения
     * 
     * @param args аргументы запуска приложения
     */
    public void parseArgs(final String[] args) {
        if (args.length == 3) {
            for (int pos = 0; pos < args.length; pos++) {
                if (args[pos].equals("--show-path")) {
                    Chess.isShowPath = true;
                }
                if (args[pos].equals("--task")) {
                    Chess.isTask = true;
                    Chess.pathXML = args[pos + 1];
                } else if (args[pos].equals("--show-path") && pos == 0) {
                    if (Chess.isValidArg(args[pos + 1]) && Chess.isValidArg(args[pos + 2])) {
                        Chess.startPoint = Converter.convertArgToPoint(args[pos + 1]);
                        Chess.finishPoint = Converter.convertArgToPoint(args[pos + 2]);
                    }

                } else if (args[pos].equals("--show-path") && pos == 2) {
                    if (Chess.isValidArg(args[pos - 2]) && Chess.isValidArg(args[pos - 1])) {
                        Chess.startPoint = Converter.convertArgToPoint(args[pos - 2]);
                        Chess.finishPoint = Converter.convertArgToPoint(args[pos - 1]);
                    }
                }
            }
        }
        if (args.length == 2) {
            for (int pos = 0; pos < args.length; pos++) {
                if (args[pos].equals("--task")) {
                    Chess.isTask = true;
                    Chess.pathXML = args[Math.abs(pos - 1)];
                } else if (Chess.isValidArg(args[0]) && Chess.isValidArg(args[1])) {
                    Chess.startPoint = Converter.convertArgToPoint(args[0]);
                    Chess.finishPoint = Converter.convertArgToPoint(args[1]);
                }
            }
        }
    }

    /**
     * Анализирует XML-файл и и проставляет полям класса Chess необходимые значения
     * 
     * @param pathXML
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws DocumentException
     */
    public void parseXML(final String pathXML)
            throws SAXException, ParserConfigurationException, DocumentException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        factory.setSchema(schemaFactory.newSchema(
                new Source[] {new StreamSource(Chess.class.getResourceAsStream("task.xsd"))}));
        SAXParser parser = factory.newSAXParser();
        SAXReader reader = new SAXReader(parser.getXMLReader());
        reader.setValidation(false);
        ErrorHandler errorHandler = new ErrorHandler() {

            @Override
            public void warning(final SAXParseException exception) throws SAXException {
                LOG.info("Warning: " + exception);

            }

            @Override
            public void fatalError(final SAXParseException exception) throws SAXException {
                LOG.fatal("Fatal Error: " + exception);

            }

            @Override
            public void error(final SAXParseException exception) throws SAXException {
                LOG.error("Error: " + exception);

            }
        };
        reader.setErrorHandler(errorHandler);
        File xmlFile = new File(pathXML);
        Document document = reader.read(xmlFile);

        Element root = document.getRootElement();

        for (Element element : root.elements()) {
            if (element.getName().equals("start")) {
                Chess.coordinateOne = element.getText();
                Chess.startPoint = Converter.convertArgToPoint(Chess.coordinateOne);
            }
            if (element.getName().equals("finish")) {
                Chess.coordinateTwo = element.getText();
                Chess.finishPoint = Converter.convertArgToPoint(Chess.coordinateTwo);
            }
        }

    }

}
