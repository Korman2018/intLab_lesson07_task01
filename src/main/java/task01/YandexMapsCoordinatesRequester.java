package task01;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YandexMapsCoordinatesRequester {
    private static final Logger LOGGER = LoggerFactory.getLogger("httpTask");

    private static final String URL = "https://yandex.ru/maps";
    private static final String COOKIE_VALUE = "maps_los=0; maps_app_promo=0:1:0:1544438434596:1; yandexuid=8991896131542183820; i=4JKZzPTOKpvkABojy6w2HiYKMMzljLOOSqxe92Eww6+j55Q/5hm6vBJXU7w/bsC1qjk4ZbpMkIKCia0c4rAHG+uwp5Q=; _ym_uid=1542701083401959260; _ym_d=1542701083; mda=0; fuid01=5c067aa00f5c7a50.O5oWnSGPeARXnwOPYUj1giXWeyy1h9CWDRFu0u7k7ypqRdKD3g2SKPKbdMoi6__SEp555BShiMpoWbuBIr91L0-9bScgYTWk_NZFN1uSijeU0C_j9ceypbKooqsyWOhJ; my=YwA=; yp=1857543829.yrts.1542183829#1857543830.yrtsi.1542183830#1560370827.szm.1:1920x1080:1920x969#1544611249.gpauto.56_843239:53_197103:1555:0:1544438439; _ym_wasSynced=%7B%22time%22%3A1544602827688%2C%22params%22%3A%7B%22eu%22%3A0%7D%2C%22bkParams%22%3A%7B%7D%7D; _ym_isad=2";

    private static final String HEADER_WITH_YANDEX_UID = "Content-Security-Policy";

    private static final String CSRF_TOKEN_REGEX = "\"csrfToken\":\"([^\"]++)";
    private static final String YANDEX_UID_REGEX = "yandexuid=[^\"]++";
    private static final String COORDINATES_REGEX = "coordinates\":\\[(.*?),(.*?)\\]";


    public static String getCoordinates(String place) {
        LOGGER.info("GET request one");

        HttpResponse<String> responseOne = null;
        try {
            responseOne = Unirest
                    .get(URL)
                    .header("Cookie", COOKIE_VALUE)
                    .asString();
        } catch (UnirestException e) {
            LOGGER.error("Exception in response one , URL:{}", URL);
        }

        // we don't check status codes
        String bodyOne = responseOne.getBody();
        String headerWithYandexUID = responseOne.getHeaders().getFirst(HEADER_WITH_YANDEX_UID);
        String csrfToken = findFirstFindSubstringByRegex(bodyOne, CSRF_TOKEN_REGEX, 1);
        String uid = findFirstFindSubstringByRegex(headerWithYandexUID, YANDEX_UID_REGEX, 0);

        LOGGER.info("{} from response header", uid);
        LOGGER.info("csrfToken={} from response body", csrfToken);

        LOGGER.info("GET request two");
        HttpResponse<String> responseTwo = null;
        try {
            responseTwo = Unirest
                    .get(URL)
                    .header("Cookie", uid)
                    .queryString("text", place)
                    .queryString("lang", "ru_RU")
                    .queryString("csrfToken", csrfToken)
                    .asString();
        } catch (UnirestException e) {
            LOGGER.error("Exception in response two, URL:{}", URL);
        }

        String bodyTwo = responseTwo.getBody();
        String coordinateOne = findFirstFindSubstringByRegex(bodyTwo, COORDINATES_REGEX, 1);
        String coordinateTwo = findFirstFindSubstringByRegex(bodyTwo, COORDINATES_REGEX, 2);

        LOGGER.info("Coordinates for place:'{}' (latitude = {} longitude = {})", place, coordinateTwo, coordinateOne);
        return coordinateTwo + " " + coordinateOne;
    }

    private static String findFirstFindSubstringByRegex(String sourceString, String regex, int groupNumber) {
        if (sourceString != null) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sourceString);

            if (matcher.find()) {
                return matcher.group(groupNumber);
            }
        }
        return "";
    }
}
