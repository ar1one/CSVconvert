package ru.netology;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // first part
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCVS(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json);

        List<Employee> list1 = parseXML("data.xml");
        String json2 = listToJson(list1);
        writeString(json2);

    }

    private static <T> String listToJson(List<T> list) {
        Type listType = new TypeToken<List<T>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return  gson.toJson(list, listType);
    }

    private static void writeString(String string) {
        try(FileWriter writer = new FileWriter("data.json")) {
            writer.write(string);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseCVS(String[] col, String name) {
        try(CSVReader reader = new CSVReader(new FileReader(name))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(col);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            List<Employee> list = csv.parse();
            return list;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Employee> parseXML(String url) {
        List<Employee> list = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(url));
            Node root = doc.getDocumentElement(); //staff

            NodeList nodeList = root.getChildNodes(); //2 employee 3 #text

            for(int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                List<String> valueOf= new ArrayList<>();
                if(Node.ELEMENT_NODE == node.getNodeType()) {
                    NodeList list1 = node.getChildNodes();

                    for(int j = 0; j < list1.getLength(); j++) {
                        Node node_ = list1.item(j);
                        if(Node.ELEMENT_NODE == node_.getNodeType()) {
                            valueOf.add(node_.getTextContent());

                        }
                    }
                    long id = Long.parseLong(valueOf.get(0));
                    String firstName = valueOf.get(1);
                    String lastName = valueOf.get(2);
                    String country = valueOf.get(3);
                    int age = Integer.parseInt(valueOf.get(4));

                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    list.add(employee);
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}