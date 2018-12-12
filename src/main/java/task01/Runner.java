package task01;

import com.mashape.unirest.http.Unirest;

import java.io.IOException;

import static task01.YandexMapsCoordinatesRequester.getCoordinates;

public class Runner {
    public static void main(String[] args) throws IOException {
        getCoordinates("Ижевск 40 лет Победы 142 41");
        getCoordinates("EPAM Minsk");
        Unirest.shutdown();
    }
}
