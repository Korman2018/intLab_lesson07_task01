package task01;

import static task01.YandexMapsCoordinatesRequester.getCoordinates;

public class Runner {
    public static void main(String[] args) {
        getCoordinates("Ижевск 40 лет Победы 142 41");
        getCoordinates("EPAM Minsk");
    }
}
