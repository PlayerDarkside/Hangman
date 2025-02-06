import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.util.Random;

public class Main {
    public static void printHangmanState(int errorCounter) {
        final String[] HANGMAN_STATES = {
                """
          +---+
          |   |
              |
              |
              |
              |
        =========
        """,
                """
          +---+
          |   |
          O   |
              |
              |
              |
        =========
        """,
                """
          +---+
          |   |
          O   |
          |   |
              |
              |
        =========
        """,
                """
          +---+
          |   |
          O   |
         /|   |
              |
              |
        =========
        """,
                """
          +---+
          |   |
          O   |
         /|\\  |
              |
              |
        =========
        """,
                """
          +---+
          |   |
          O   |
         /|\\  |
         /    |
              |
        =========
        """,
                """
          +---+
          |   |
          O   |
         /|\\  |
         / \\  |
              |
        =========
        """
        };

        System.out.println(HANGMAN_STATES[errorCounter]);
    }

    public static String makeWordPlaceholder(int lettersNumber) {
        StringBuilder placeholder = new StringBuilder();
        final int NUMBER_OF_REPETITIONS = Math.max(0, lettersNumber);
        placeholder.append("#".repeat(NUMBER_OF_REPETITIONS));

        return placeholder.toString();
    }

    public static String replacePlaceholderWithLetter(String originalWord, String wordPlaceholder, char userLetter) {
        ArrayList<Character> originalWordArray = new ArrayList<>();
        ArrayList<Character> wordPlaceholderArray = new ArrayList<>();

        for (char c : originalWord.toCharArray()) { originalWordArray.add(c); }

        for (char c : wordPlaceholder.toCharArray()) { wordPlaceholderArray.add(c); }

        for (int i = 0; i < originalWordArray.size(); i++) {
            if (originalWordArray.get(i) == userLetter) {
                wordPlaceholderArray.set(i, userLetter);
            }
        }
        
        StringBuilder sb = new StringBuilder();
        for (char c : wordPlaceholderArray) {
            sb.append(c);
        }

        wordPlaceholder = sb.toString();
        return wordPlaceholder;
    }

    public static boolean isUserLetterCyrillic(char userCharacter) {
        if ((userCharacter >= 'а' && userCharacter <= 'я') || (userCharacter >= 'А' && userCharacter <= 'Я') || (userCharacter == 'ё' || userCharacter == 'Ё')) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isUserChoiceCorrect(int userChoice) {
        if (userChoice == 1 || userChoice == 2) {
            return false;
        } else {
            return true;
        }

    }


    public static void launchGame(String hiddenWord, String wordPlaceholder) {
        int mistakesCounter = 0;
        Scanner scanner = new Scanner(System.in);

        while (isInGame(hiddenWord, wordPlaceholder, mistakesCounter)) {
            System.out.println("Загаданное слово: " + wordPlaceholder);
            System.out.print("Введите букву: ");
            char userLetter = scanner.next().charAt(0);
            userLetter = Character.toLowerCase(userLetter);

            if (isUserLetterCyrillic(userLetter)) {
                if (isUserLetterCorrect(hiddenWord, userLetter)) {
                    wordPlaceholder = replacePlaceholderWithLetter(hiddenWord, wordPlaceholder, userLetter);
                    System.out.println(replacePlaceholderWithLetter(hiddenWord, wordPlaceholder, userLetter));
                } else {
                    mistakesCounter++;
                }

                printHangmanState(mistakesCounter);

                if (isWin(hiddenWord, wordPlaceholder)) {
                    showWinMessage();
                    break;
                }

                if (isLose(mistakesCounter)) {
                    showLoseMessage(hiddenWord);
                    break;
                }
            } else {
                showErrorMessage();
                printHangmanState(mistakesCounter);
            }
        }
    }

    public static boolean isInGame(String hiddenWord, String wordPlaceholder, int mistakesCounter) {
        final int MISTAKES_FOR_LOSS = 6;
        if (!hiddenWord.equals(wordPlaceholder) && mistakesCounter <= MISTAKES_FOR_LOSS) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isUserLetterCorrect(String hiddenWord, char userLetter) {
        if (hiddenWord.indexOf(userLetter) != -1) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLose(int mistakesCounter) {
        final int MISTAKES_FOR_LOSE = 6;
        if (mistakesCounter == MISTAKES_FOR_LOSE) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isWin(String hiddenWord, String wordPlaceholder) {
        if (hiddenWord.equals(wordPlaceholder)) {
            return true;
        } else {
            return false;
        }
    }

    public static void showErrorMessage() {
        System.out.println("Некорректный ввод. Попробуйте ещё раз.");
    }

    public static void showWinMessage () {
        System.out.println("Вы угадали слово! ");
    }

    public static void showLoseMessage (String hiddenWord) {
        System.out.println("Вы проиграли. Загаданное слово было: " + hiddenWord);
    }

    public static String generateWord(ArrayList<String> words) {
        Random rand = new Random();
        final int randomIndex = rand.nextInt(words.size());
        return words.get(randomIndex);
    }

    public static void restartGame(String hiddenWord, String wordPlaceholder, int userChoice, Scanner scanner, ArrayList<String> words) {
        while (userChoice != 1) {
            hiddenWord = generateWord(words);
            wordPlaceholder = makeWordPlaceholder(hiddenWord.length());
            launchGame(hiddenWord, wordPlaceholder);
            System.out.println("Выберете действие: \n1. Выйти из игры\n2. Угадать другое слово.");
            userChoice = scanner.nextInt();
            userChoice = getValidUserChoice(userChoice, scanner);
        }
    }

    public static int getValidUserChoice(int userChoice, Scanner scanner) {
        while (isUserChoiceCorrect(userChoice)) {
            System.out.println("Некорректный ввод. Попробуйте ещё раз.");
            userChoice = scanner.nextInt();
        }
        return userChoice;
    }

    public static void main(String[] args) throws IOException {
        final int INITIAL_STATE = 0;
        ArrayList<String> words = new ArrayList<>();
        File file = new File("src/resources/words.txt");
        words.addAll(Files.readAllLines(file.toPath()));

        String hiddenWord = generateWord(words);
        String wordPlaceholder = makeWordPlaceholder(hiddenWord.length());

        Scanner scanner = new Scanner(System.in);
        System.out.println("Виселица. Ваша задача -- угадать случайное слово. \nВы можете ошибиться 6 раз, после чего наступает проигрыш.");
        printHangmanState(INITIAL_STATE);
        System.out.println("1. Начать игру\n2. Выйти из игры");
        int userChoice = scanner.nextInt();
        userChoice = getValidUserChoice(userChoice, scanner);

        switch (userChoice) {
            case 1:
                launchGame(hiddenWord, wordPlaceholder);
                System.out.println("Выберете действие: \n1. Выйти из игры\n2. Угадать другое слово.");
                userChoice = scanner.nextInt();
                userChoice = getValidUserChoice(userChoice, scanner);
                if (userChoice == 1) {
                    break;
                } else {
                    restartGame(hiddenWord, wordPlaceholder, userChoice, scanner, words);
                }
                break;
            case 2:
                break;
        }
    }
}