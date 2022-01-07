package pepse.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * class is in charge of reading Highscores from a formatted JSON file downloaded from server.
 */
public final class ReadScores {
    private static final String DELIMITER = "\"";
    private static final String REGEX = "[^\"a-zA-Z0-9]";
    private static final String PLAYER_ENTRY = "player";

    /**
     * method parses a JSON file containing the highscores information, into a String in format:
     * 1. {player} {score} \n 2. {player} {score} \n ...
     * @param json String stream in JSON format
     * @return String of the parsed info
     */
    public static String readScores(String json) {
        List<String> scores = new ArrayList<>();
        // remove all special characters, except ".
        json = json.replaceAll(REGEX, "");
        // split by " ".
        List<String> asArray = new ArrayList<>(List.of(json.split(DELIMITER)));
        // remove all empty entries.
        asArray.removeAll(Collections.singleton(""));
        // iterate over JSON and extract all player names and scores.
        Iterator<String> iter = asArray.listIterator();
        while (iter.hasNext()) {
            String curr = iter.next();
            if (curr.equals(PLAYER_ENTRY)) {
                String name = iter.next();
                iter.next();
                String score = iter.next();
                scores.add(name);
                scores.add(score);
            }
        }
        // build a string in format: "1.{name} {score} \n 2.{name} {score} \n..."
        StringBuilder scoreMsg = new StringBuilder();
        int i = 0;
        int count = 1;
        for (String s : scores) {
            // add the placing number
            if (i % 2 == 0) {
                scoreMsg.append(count).append(". ");
                count++;
            }
            scoreMsg.append(s).append(" ");
            if (i % 2 == 1)
                scoreMsg.append("\n");
            i ++;
        }
        return String.valueOf(scoreMsg);

    }
}