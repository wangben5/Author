package com.shadt.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomPersonService {
	public List<Person> getPersons(InputStream instream) throws Exception {
		List<Person> persons = new ArrayList<Person>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();// 创建DOM解析工厂
		DocumentBuilder dombuild = factory.newDocumentBuilder();// 创建DON解析器
		Document dom = dombuild.parse(instream);// 开始解析XML文档并且得到整个文档的对象模型
		Element root = (Element) dom.getDocumentElement();// 得到根节点<persons>
		NodeList personList = root.getElementsByTagName("person");// 得到根节点下所有标签为<person>的子节点
		for (int i = 0; i < personList.getLength(); i++) {// 遍历person节点
			Person person = new Person();// 首先创建一个Person
			Element personElement = (Element) personList.item(i);// 得到本次Person元素节点
			person.setId(new Integer(personElement.getAttribute("id")));// 得到Person节点中的ID
			NodeList personChilds = personElement.getChildNodes();// 得到Person节点下的所有子节点
			for (int j = 0; j < personChilds.getLength(); j++) {// 遍历person节点下的所有子节点
				if (personChilds.item(j).getNodeType() == Node.ELEMENT_NODE) {// 如果是元素节点的话
					Element childElement = (Element) personChilds.item(j); // 得到该元素节点
					if ("name".equals(childElement.getNodeName())) {// 如果该元素节点是name节点
						person.setName(childElement.getFirstChild()
								.getNodeValue());// 得到name节点下的第一个文本子节点的值
					} else if ("age".equals(childElement.getNodeName())) {// 如果该元素节点是age节点、
						person.setAge(new Short(childElement.getFirstChild()
								.getNodeValue()));// 得到age节点下的第一个文本字节点的值
					}
				}
			}
			persons.add(person);// 遍历完person下的所有子节点后将person元素加入到集合中去
		}
		return persons;
	}
}