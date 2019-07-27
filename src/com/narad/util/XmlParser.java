package com.narad.util;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

public class XmlParser extends DefaultHandler {

	private static Logger logger = Logger.getLogger(XmlParser.class);
	
	private static final int NUM_PARSERS = 10;
	
	private static SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	
	private static Queue<SAXParser> parserQueue = new LinkedList<SAXParser>();
	
	static {
		parserFactory.setValidating(false);
		parserFactory.setNamespaceAware(false);

		try {
			for (int i = 0; i < NUM_PARSERS; i++) {
				parserQueue.add(parserFactory.newSAXParser());
			}
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}
	
    XmlNode root;
    

    // working stack of elements
    private transient Stack<XmlNode> elementStack = new Stack<XmlNode>();
    private Locator locator;
    
	public static XmlNode parseFile(String filePath) /*throws LiveListXmlException, LiveListFileException*/ {

		FileReader fr = null;
		try {
			fr = new FileReader(filePath);
			InputSource in = new InputSource(fr);
			return parse(in);
		} catch (FileNotFoundException e) {
			return null;
			/*throw new LiveListFileException("exception while parsing ", e,
					LiveListFileException.FILE_NOT_FOUND_EXCEPTION, null, filePath);*/
			/*} catch (LiveListXmlException xmle) {
			xmle.setFilePath(filePath);
			throw xmle;
		} catch (LiveListFileException filee) {
			filee.setFilePath(filePath);
			throw filee;*/
		}finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					logger.info("Exception while closing input stream for " + filePath);
				}
			}
		}
	}
    
	public static XmlNode parse(String s) /*throws LiveListXmlException, LiveListFileException*/ {
		InputSource in = new InputSource(new ByteArrayInputStream(s.getBytes()));
		return parse(in);
	}

    public static XmlNode parse(InputSource in) /*throws LiveListXmlException, LiveListFileException*/ {
        XmlParser handler = new XmlParser();
        SAXParser parser = null;
        try {
			parser = parserQueue.poll();
			parser.reset();

            // call parsing
            parser.parse(in, handler);

		} catch (SAXException saxe) {
			/*throw new LiveListXmlException("exception while parsing ", saxe, LiveListXmlException.SAX_PARSE_EXCEPTION,
					null, null);*/
		} catch (IOException e) {
			/*throw new LiveListFileException("exception while parsing ", e, LiveListFileException.IO_EXCEPTION, null,
					null);*/
		} catch (RuntimeException re) {
			/*throw new LiveListXmlException("exception while parsing ", re, LiveListXmlException.SAX_PARSE_EXCEPTION,
					null, null);*/
		}finally {
			if (parser != null) {
				parserQueue.add(parser);
			}
        }

        return handler.root;
    }

    private XmlParser() {
        this.setDocumentLocator(new LocatorImpl());
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    private XmlNode getCurrentNode() {
        return elementStack.size() > 0 ? elementStack.peek() : null;
    }

    public void characters(char[] ch, int start, int length) {
        XmlNode currNode = getCurrentNode();
        if (currNode != null) {
            currNode.addElement(new String(ch, start, length));
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        XmlNode currNode = getCurrentNode();
        XmlNode newNode = new XmlNode(qName, currNode);
        newNode.setLocation(this.locator.getLineNumber(), this.locator.getColumnNumber());
        elementStack.push(newNode);

        if (currNode == null) {
            root = newNode;
        }

        int attsCount = attributes.getLength();
        for (int i = 0; i < attsCount; i++) {
            newNode.addAttribute(attributes.getQName(i), attributes.getValue(i));
        }
    }

    public void endElement(String uri, String localName, String qName) {
        if (elementStack.size() > 0) {
            getCurrentNode().flushText();
            elementStack.pop();
        }
    }
}
