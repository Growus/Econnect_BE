package com.growus.econnect.service.plant;

import com.growus.econnect.dto.plant.PlantTypeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.growus.econnect.base.common.UserAuthorizationUtil.getCurrentUserId;

@Service
public class PlantTypeService {

    @Value("${openapi.api.key}")
    private String apiKey;

    @Value("${openapi.api.url}")
    private String apiUrl;

    @Value("${openapi.speclmanage.url}")
    private String speclmanageUrl;

    Long userId = getCurrentUserId();

    public List<PlantTypeDTO> getPlantTypes(int pageNo, int numOfRows) {
        try {
            String urlStr = apiUrl + "?apiKey=" + apiKey + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
            URI uri = new URI(urlStr);

            RestTemplate restTemplate = new RestTemplate();
            String xmlData = restTemplate.getForObject(uri, String.class);

            return parsePlantTypesFromXml(xmlData);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public PlantTypeDTO getPlantTypeByCntntsNo(String cntntsNo) {
        try {
            // 첫 번째 요청: 식물 기본 정보
            String urlStr = apiUrl + "?apiKey=" + apiKey + "&cntntsNo=" + cntntsNo;
            URI uri = new URI(urlStr);

            RestTemplate restTemplate = new RestTemplate();
            String xmlData = restTemplate.getForObject(uri, String.class);
            System.out.println("Basic XML Response: " + xmlData); // 로그에 응답 XML을 기록

            // 기본 정보 파싱
            PlantTypeDTO basicPlantType = parsePlantTypeByCntntsNoFromXml(xmlData, cntntsNo);

            // 두 번째 요청: 식물 상세 정보
            if (basicPlantType != null) {
                String speclManageUrlStr = getSpeclmanageInfoUrl(cntntsNo);
                URI speclManageUri = new URI(speclManageUrlStr);
                String speclManageXmlData = restTemplate.getForObject(speclManageUri, String.class);
                System.out.println("Detail XML Response: " + speclManageXmlData); // 로그에 응답 XML을 기록

                // 상세 정보 파싱
                String speclmanageInfo = parseSpeclmanageInfoFromXml(speclManageXmlData, cntntsNo);
                basicPlantType.setSpeclmanageInfo(speclmanageInfo);
            }

            return basicPlantType;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<PlantTypeDTO> parsePlantTypesFromXml(String xmlData) throws Exception {
        List<PlantTypeDTO> plantTypes = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList itemListNodes = document.getElementsByTagName("item");

        for (int i = 0; i < itemListNodes.getLength(); i++) {
            Element itemElement = (Element) itemListNodes.item(i);
            String type = getElementValue(itemElement, "cntntsSj");
            String cntntsNo = getElementValue(itemElement, "cntntsNo");
            if (type != null && cntntsNo != null) {
                plantTypes.add(new PlantTypeDTO(type, cntntsNo));
            }
        }

        return plantTypes;
    }

    private PlantTypeDTO parsePlantTypeByCntntsNoFromXml(String xmlData, String cntntsNo) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList itemListNodes = document.getElementsByTagName("item");
        for (int i = 0; i < itemListNodes.getLength(); i++) {
            Element itemElement = (Element) itemListNodes.item(i);
            String fetchedCntntsNo = getElementValue(itemElement, "cntntsNo");
            if (cntntsNo.equals(fetchedCntntsNo)) {
                String type = getElementValue(itemElement, "cntntsSj");
                return new PlantTypeDTO(type, cntntsNo);
            }
        }
        return null;
    }

    private String parseSpeclmanageInfoFromXml(String xmlData, String cntntsNo) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList itemListNodes = document.getElementsByTagName("item");
        for (int i = 0; i < itemListNodes.getLength(); i++) {
            Element itemElement = (Element) itemListNodes.item(i);
            String fetchedCntntsNo = getElementValue(itemElement, "cntntsNo");

            if (cntntsNo.equals(fetchedCntntsNo)) {
                return getElementValue(itemElement, "speclmanageInfo");
            }
        }
        return null; // speclmanageInfo가 없는 경우
    }

    private String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    private String getSpeclmanageInfoUrl(String cntntsNo) {
        // API 엔드포인트와 파라미터를 적절히 수정
        return speclmanageUrl + "?apiKey=" + apiKey + "&cntntsNo=" + cntntsNo;
    }
}
