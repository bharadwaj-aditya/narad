package com.instasecure.apps.livelist.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import com.instasecure.exception.LiveListFileException;
import com.instasecure.exception.LiveListXmlException;

public class XmlNode extends HashMap {

	private static Logger logger = Logger.getLogger(XmlNode.class);
	private static final long serialVersionUID = -8885816894807590347L;
	private static final boolean IGNORE_NAMESPACE = true;
	private static final boolean IGNORE_CASE = true;

	// node name - correspondes to xml tag name
	private String name;

	// parent element
	private XmlNode parent;

	// map of attributes
	private Map attributes = new HashMap();

	// all subelements in the form of linear list
	private List elementList = new ArrayList();

	// textBuff value
	private StringBuilder textBuff = new StringBuilder();

	// text buffer containing continuous text 
	private transient StringBuilder tmpBuf = new StringBuilder();

    // location of element in the XML
    private int lineNumbwe;
    private int columnNumber;

    /**
	 * Static method that creates node for specified input source which
	 * contains XML data
	 * @param in
	 * @return XmlNode instance
     * @throws LiveListFileException 
     * @throws LiveListXmlException 
	 */
	public static XmlNode getInstance(InputSource in) /*throws LiveListXmlException, LiveListFileException*/ {
        return XmlParser.parse(in);
	}

	/**
	 * Constructor that defines name and connects to specified
	 * parent element.
	 * @param name
	 * @param parent
	 */
	public XmlNode(String name, XmlNode parent) {
		super();

		this.name = adaptName(name);
		this.parent = parent;

		if (parent != null) {
			parent.addElement(this);
		}
	}

	/**
	 * According to settings of this object changes element/attribute
	 * names to be namespace/case insensitive.
	 * @param s
	 * @return String
	 */
	private String adaptName(String s) {
		if (IGNORE_NAMESPACE) {
			int index = s.indexOf(':');
			if (index >= 0) {
				s = s.substring(index + 1);
			}
		}

		if (IGNORE_CASE) {
			s = s.toLowerCase();
		}

		return s;
	}

	/**
	 * @return Node name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Node textBuff.
	 */
	public String getText() {
		return textBuff == null ? null : textBuff.toString();
	}

	/**
	 * @return Parent node or null if instance is root node.
	 */
	public XmlNode getParent() {
		return parent;
	}

	/**
	 * For specified serach path returns element/attribute if found,
	 * or null otherwise. Path is sequence of elements separated with
	 * some of characters: ./\[]
	 * For example: msg[0].response[0].id is trying to find in node
	 * first msg subelement and than first response subelement and then
	 * attribute id.
	 * @param key
	 * @return Resulting value which should be eather XmlNode instance or string.
	 */
	private Object getSeq(String key) {
		StringTokenizer strTkzr = new StringTokenizer(key, "./\\[]");
		Object currValue = this;
		while (strTkzr.hasMoreTokens()) {
			String currKey = strTkzr.nextToken();
			if (currValue instanceof Map) {
				currValue = ((Map)currValue).get(currKey);
			} else if (currValue instanceof List) {
				try {
					List list = (List) currValue;
					int index = Integer.parseInt(currKey);

					if (index >= 0 && index < list.size()) {
						currValue = list.get(index);
					}
				} catch (NumberFormatException e) {
					return null;
				}
			} else {
				return null;
			}
		}

		return currValue;
	}

	/**
	 * Overriden get method - search both subelements and attributes
	 */
	public Object get(Object key) {
		if (key == null) {
			return null;
		}

		if (IGNORE_CASE) {
			key = ((String)key).toLowerCase();
		}

		String sKey = (String) key;

		if ( sKey.indexOf('/') >= 0 || sKey.indexOf('.') >= 0 || sKey.indexOf('\\') >= 0 || sKey.indexOf('[') >= 0 ) {
			return getSeq(sKey);
		}

		if (sKey.equalsIgnoreCase("_value")) {
			return getText();
		} else if (this.containsKey(key)) {
			return super.get(key);
		} else {
			return attributes.get(key);
		}
	}
	
	public String getString(Object key) {
		return (String) get(key);
	}

	/**
	 * Adds new attribute with specified name and value.
	 * @param name
	 * @param value
	 */
	public void addAttribute(String name, String value) {
		attributes.put(adaptName(name), value);
	}

    public Map getAttributes() {
        return this.attributes;
    }
    
    public String getAttribute(String attName) {
    	return (String) this.attributes.get(adaptName(attName));
    }
    
    public boolean getBoolean(String attName)
    {
        attName = adaptName(attName);
    	if(this.attributes.containsKey(attName))
    	{
    		if(!"".equals(this.attributes.get(attName)))
    		{
    			return Boolean.parseBoolean((String) this.attributes.get(attName));
    		}
    	}
    	
    	return false;
    }
    
    public long getLong(String attName)
    {
        attName = adaptName(attName);
    	if(this.attributes.containsKey(attName))
    	{
    		if(!"".equals(this.attributes.get(attName)))
    		{	
    			return Long.parseLong((String) this.attributes.get(attName));
    		}
    	}
    	
    	return -1;
    }
    
    public int getInt(String attName)
    {
        attName = adaptName(attName);
    	if(this.attributes.containsKey(attName))
    	{
    		if(!"".equals(this.attributes.get(attName)))
    		{	
    			return Integer.parseInt((String) this.attributes.get(attName));
    		}
    	}
    	
    	return -1;
    }

    /**
	 * Adds new subelement.
	 * @param elementNode
	 */
	public void addElement(XmlNode elementNode) {
        flushText();
        
        String elementName = elementNode.getName();

		if (!this.containsKey(elementName)) {
			this.put(elementName, new ArrayList());
		}

		ArrayList elementsForName = (ArrayList) this.get(elementName);
		elementsForName.add(elementNode);

		elementList.add(elementNode);
	}

    /**
     * Adds new textBuff to element list
     * @param value
     */
    public void addElement(String value) {
        tmpBuf.append(value);
    }

    public void flushText() {
        String value = tmpBuf.toString();
        if (!"".equals(value)) {
            StringTokenizer tokenizer = new StringTokenizer(value, "\n\r");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();
                if (token.length() != 0) {
                    elementList.add(token);
                    if (textBuff.length() > 0) {
                        textBuff.append('\n');
                    }
                    textBuff.append(token);
                }
            }
            tmpBuf.delete( 0, tmpBuf.length() );
        }
    }


    /**
     * Returns a list of elements with the given name
     * @param name
     * @return
     */
    public Object getElement(String name) {
		if (IGNORE_CASE) {
			name = name.toLowerCase();
		}
        return super.get(name);
    }
    
    /**
     * Returns the first element with the given name
     * @param name
     * @return
     */
    public Object getFirstElement(String name) {
    	if(super.get(name) != null){
    		if(((ArrayList)super.get(name)).size() >0){
    			return ((ArrayList)super.get(name)).get(0);
    		}
    	}
        return null;
    }    
    
    /**
     * Gets all elements in this node
     * @return
     */
    public List getElementList() {
        return elementList;
    }

    /**
	 * Prints instance in treelike form to the default output.
	 * Useful for testing.
	 */
	public void print() {
		print(0);
	}

	private void print(int level) {
		for (int i = 0; i < level; i++) {
			System.out.print("     ");
		}
		System.out.print(name + ": " + attributes + ": TEXT = [" + textBuff + "]\n");

		Iterator it = elementList.iterator();
		while (it.hasNext()) {
            Object element = it.next();
            if (element instanceof XmlNode) {
                XmlNode childNode = (XmlNode) element;
                childNode.print(level + 1);
            } else {
                for (int i = 0; i <= level; i++) {
                    System.out.print("     ");
                }
                System.out.println((String)element);
            }
		}
	}
	
    public String getXML() {
        StringBuilder retBuffer = new StringBuilder();
        retBuffer.append("<").append(getName());
        Iterator itrAttr = attributes.keySet().iterator();
        String attaName;
        while(itrAttr.hasNext()){
            attaName = itrAttr.next().toString();
			retBuffer.append(" ").append(attaName).append("=\"")
					.append(StringEscapeUtils.escapeXml(attributes.get(attaName).toString())).append("\" ");
        }
        retBuffer.append(">");
        Iterator it = elementList.iterator();
        while (it.hasNext()) {
            Object element = it.next();
            if (element instanceof XmlNode) {
                
                XmlNode childNode = (XmlNode) element;
                retBuffer.append(childNode.getXML());
                
            } 
        }
        retBuffer.append("</").append(getName()).append(">");
        return retBuffer.toString();
    }
    
    /**
     * Return a detailed representation with attributes and values
     * @return
     */
    public Map<String, Object> getMap() {
    	return getMap(true, true, true);
    }
    
    /**
     * Gives a simple representation of the xml to be used in soap methods
     * @return
     */
    public Map<String, Object> getSoapMap() {
    	return getMap(true, false, false);
    }
	
    /**
     * Get a map representation of the xml
     * @param encloseNodeInMap - Set the root node as the root of the map.
     * @param setAttributes - Set a map named __attributes__ based on xml attributes
     * @param setValueTag - Set the value in a map named __value__
     * @return
     */
	public Map<String, Object> getMap(boolean encloseNodeInMap, boolean setAttributes, boolean setValueTag) {
		Map<String, Object> nodeMap = new LinkedHashMap<String, Object>();

		for (Object elementObj : elementList) {
			if (elementObj instanceof XmlNode) {
				XmlNode childNode = (XmlNode) elementObj;

				List<Map> childElementList = null;
				Object object = nodeMap.get(childNode.getName());
				if (object == null) {
					childElementList = new ArrayList<Map>();
					nodeMap.put(childNode.getName(), childElementList);
				} else if (object instanceof List){
					childElementList = (List) object;
				} else {
					//Throw Exception! Overriding data
					logger.info("While creating xml map - Bean with same name contains basic data types and elements: "
							+ getName());
				}
				
				
				Map<String, Object> childMap = childNode.getMap(false, setAttributes, setValueTag);
				boolean isChildMapEmpty = childMap.isEmpty();
				if (setAttributes) {
					childMap.put("__attributes__", childNode.getAttributes());
				}
				
				if (setValueTag) {
					if (!childMap.containsKey("__value__")) {
						childMap.put("__value__", childNode.getText());
					}
				} else if (isChildMapEmpty) {
					nodeMap.put(childNode.getName(), childNode.getText());
					continue;//To avoid setting the child value twice
				}
				childElementList.add(childMap);
			} else {
			}
		}

		if (encloseNodeInMap) {
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put(getName(), nodeMap);
			return hashMap;
		}
		return nodeMap;
	}
	
//	public Map<String, Object> getMap(boolean encloseNodeInMap) {
//		Map<String, Object> nodeMap = new LinkedHashMap<String, Object>();
//		//nodeMap.putAll(attributes);
//
//		for (Object elementObj : elementList) {
//			if (elementObj instanceof XmlNode) {
//				XmlNode childNode = (XmlNode) elementObj;
//
//				List<Map> childElementList = null;
//				Object object = nodeMap.get(childNode.getName());
//				if (object == null) {
//					childElementList = new ArrayList<Map>();
//					nodeMap.put(childNode.getName(), childElementList);
//				} else if (object instanceof List){
//					childElementList = (List) object;
//				} else {
//					//Throw Exception! Overriding data
//					System.out.println("Bean with same name contains basic data types and elements: " + getName());
//				}
//				
//				Map<String, Object> childMap = childNode.getMap(false);
//				if (childMap.isEmpty()) {
//					childMap.putAll(childNode.getAttributes());
//					childMap.put(childNode.getName(), childNode.getText());
//				}
//				childElementList.add(childMap);
//			} else {
//			}
//		}
//
//		if (encloseNodeInMap) {
//			HashMap<String, Object> hashMap = new HashMap<String, Object>();
//			hashMap.put(getName(), nodeMap);
//			return hashMap;
//		}
//		return nodeMap;
//	}

    public void setLocation(int lineNumber, int columnNumber) {
        this.lineNumbwe = lineNumber;
        this.columnNumber = columnNumber;
    }

    public int getLineNumber() {
        return lineNumbwe;
    }

    public int getColumnNumber() {
        return columnNumber;
    }
    
}