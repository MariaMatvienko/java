package com.mariamatvienko.olx_lab;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AdvParser {

    public Set<AdvertisementInfo>  parse(String request) {
        String requestUrl = "https://www.olx.ua/d/uk/list/q-" + request.replaceAll(" ", "-");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);

        Set<AdvertisementInfo> allAdsInfo = new LinkedHashSet<>();
        Set<String> processedLinks = new LinkedHashSet<>();

        while (!Objects.equals(requestUrl, "")) {
            driver.get(requestUrl);
            Document doc = Jsoup.parse(driver.getPageSource());
            allAdsInfo.addAll(parsePages(doc, processedLinks));

            try {
                requestUrl = "https://www.olx.ua" + doc.getElementsByAttributeValue("data-cy", "pagination-forward").first().attr("href");
            } catch (Exception ignored) {
                requestUrl = "";
            }
        }

        return allAdsInfo;
    }

    private List<AdvertisementInfo> parsePages(Document doc, Set<String> processedLinks) {
        List<AdvertisementInfo> ads = new ArrayList<>();
        List<String> allUrls = new ArrayList<>();
        Elements elements = doc.getElementsByAttributeValue("data-cy", "l-card");
        for (Element element: elements) {
            try {
                allUrls.add("https://www.olx.ua"  + element.getElementsByClass("css-rc5s2u").first().attr("href"));
            } catch (Exception e) {
                System.out.println("Failed get urls!");
            }
        }

        allUrls.removeAll(processedLinks);
        for (String link: allUrls) {
            try {
                Document pageDoc = Jsoup.connect(link).get();
                String id = pageDoc.getElementsByAttributeValue("class", "css-12hdxwj er34gjf0").first().text();
                String name = pageDoc.getElementsByAttributeValue("data-cy", "ad_title").first().text();
                String price = pageDoc.getElementsByAttributeValue("class", "css-ddweki er34gjf0").first().text();
                String publish = pageDoc.getElementsByAttributeValue("data-cy", "ad-posted-at").first().text();
                String description = pageDoc.getElementsByAttributeValue("class", "css-bgzo2k er34gjf0").first().text();

                ads.add(new AdvertisementInfo(id, link, name, price, publish, description));
            } catch (Exception e) {
                System.out.println("Parse failed!");
            }
        }
        processedLinks.addAll(allUrls);

        return ads;
    }

}
