package net.wurstclient.forge.commands.fbot;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.ChatUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FBotCMD extends Command {

    private final String[] dataSet = new String[]{
            "Default keybind to open ClickGUI is P",
            "Default command prefix is . (Can be changed to - via the ClickGUI)",
            "If you encounter a crash caused by Fallen, delete the Wurst folder in your Minecraft directory and relaunch Fallen.",
            "Discord invite link: https://discord.gg/S8KMuFyd9Y",
            "YouTube channel link: https://www.youtube.com/channel/UC5oCS79ifCyw27ef_8dB0QA",
            "Website link: destruc69.github.io/fallen/",
            "Change a slider setting with .setSlider command.",
            "Change a checkbox setting with .setCheckbox command.",
            "Change an enum setting with .setEnum command.",
            "Set your yaw and pitch with .yaw / .pitch command.",
            "Position HUD elements via the ClickGUI.",
            "Report module issues on Discord.",
            "GitHub link: https://github.com/Destruc69/ForgeWurst-Fallen",
            "Donate via PayPal: https://www.paypal.me/WurstImperium"
    };

    public FBotCMD() {
        super("fbot", "A dataset reliant bot to provide assistance and help.", "Syntax: .fbot <content>");
    }

    @Override
    public void call(String[] args) throws CmdException, IOException {
        if (args.length < 1) {
            // Handle insufficient arguments (e.g., provide usage instructions).
            throw new CmdSyntaxError();
        }

        String userQuery = args[0].toLowerCase().trim(); // Convert to lowercase and remove leading/trailing spaces

        Map<String, Double> answerProbabilities = new HashMap<>();

        for (String answer : dataSet) {
            // Calculate a probability score for each answer based on user query matching.
            double probability = calculateProbability(answer, userQuery);
            answerProbabilities.put(answer, probability);
        }

        // Find the answer with the highest probability.
        String bestAnswer = findBestAnswer(answerProbabilities);

        if (bestAnswer != null) {
            // Provide the best answer to the user
            ChatUtils.message(bestAnswer);
        } else {
            // No suitable answer found
            ChatUtils.message("I'm sorry, but I couldn't find an answer to that question. You can ask our supportive community on our discord: https://discord.gg/S8KMuFyd9Y");
        }
    }

    private double calculateProbability(String answer, String userQuery) {
        // Implement your probability calculation logic here.
        // You can use various techniques, such as keyword matching, NLP libraries, or custom scoring.
        // The higher the probability, the more relevant the answer is to the user's query.
        // Return a value between 0 and 1.
        // Example: return a simple keyword matching score.
        return keywordMatchingScore(answer, userQuery);
    }

    private double keywordMatchingScore(String answer, String userQuery) {
        // Convert both the answer and userQuery to lowercase for case-insensitive matching
        String lowerCaseAnswer = answer.toLowerCase();
        String lowerCaseQuery = userQuery.toLowerCase();

        // Split the user query into words using whitespace as the delimiter
        String[] queryWords = lowerCaseQuery.split("\\s+");

        int matchingCount = 0;

        for (String word : queryWords) {
            // Check if the lowercased answer contains the query word
            if (lowerCaseAnswer.contains(word)) {
                matchingCount++;
            }
        }

        // Handle the case when queryWords is empty to avoid division by zero
        if (queryWords.length == 0) {
            return 0.0;
        }

        // Calculate the matching score as a ratio of matching words to total words in the query
        return (double) matchingCount / queryWords.length;
    }


    private String findBestAnswer(Map<String, Double> answerProbabilities) {
        // Find and return the answer with the highest probability.
        double maxProbability = 0;
        String bestAnswer = null;

        for (Map.Entry<String, Double> entry : answerProbabilities.entrySet()) {
            if (entry.getValue() > maxProbability) {
                maxProbability = entry.getValue();
                bestAnswer = entry.getKey();
            }
        }

        return bestAnswer;
    }
}