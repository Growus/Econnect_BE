package com.growus.econnect.service.plant;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.InputSource;

@Service
@RequiredArgsConstructor
public class PlantTypeService {

    @Value("${openapi.api.key}")
    private String apiKey;

    @Value("${openapi.api.url}")
    private String apiUrl;

    public List<String> getPlantTypes(int pageNo, int numOfRows) {
        try {
            // API 요청을 위한 URL 생성
            String urlStr = apiUrl + "?apiKey=" + apiKey + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
            URI uri = new URI(urlStr);

            // API 호출 및 XML 데이터 수신
            RestTemplate restTemplate = new RestTemplate();
            String xmlData = restTemplate.getForObject(uri, String.class);

            // XML 데이터 파싱하여 cntntsSj 리스트 반환
            return parsePlantTypesFromXml(xmlData);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<String> parsePlantTypesFromXml(String xmlData) throws Exception {
        List<String> plantTypes = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList itemListNodes = document.getElementsByTagName("item");

        for (int i = 0; i < itemListNodes.getLength(); i++) {
            Element itemElement = (Element) itemListNodes.item(i);
            String type = getElementValue(itemElement, "cntntsSj");
            if (type != null) {
                plantTypes.add(type);
            }
        }

        return plantTypes;
    }

    private String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}
