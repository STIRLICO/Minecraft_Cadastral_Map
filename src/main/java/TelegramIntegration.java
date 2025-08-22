import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TelegramIntegration extends TelegramLongPollingBot {
    private Login_object real;

    private String expectedInput = "";

    {
        try {
            real = Login_object.loadFromJSON();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "Cadastral Integration";
    }

    @Override
    public String getBotToken() {
        return real.getTelegram_token();
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        long userId = callbackQuery.getFrom().getId();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        int messageId = callbackQuery.getMessage().getMessageId();

        if (userId != real.getTelegram_id()) {
            answerCallbackQuery(callbackQuery.getId(), "❌ Вы не можете обрабатывать заявки!");
            return;
        }

        try {
            long applicationId = Long.parseLong(callbackData.split("_")[1]);
            boolean isAccepted = callbackData.startsWith("accept_");

            List<ApplicationService> applications = ApplicationService.loadFromJSON();
            ApplicationService targetApp = null;

            for (ApplicationService app : applications) {
                if (app.getId() == applicationId) {
                    targetApp = app;
                    if (isAccepted) {
                        app.setPublished(true);
                    } else {
                        applications.remove(app);
                    }
                    break;
                }
            }

            if (targetApp == null) {
                answerCallbackQuery(callbackQuery.getId(), "⚠️ Заявка не найдена!");
                return;
            }

            ApplicationService.saveToJSON(applications);

            EditMessageText editedMessage = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(callbackQuery.getMessage().getText() +
                            "\n\n🔹 *Статус:* " + (isAccepted ? "✅ ПРИНЯТА" : "❌ ОТКЛОНЕНА"))
                    .parseMode("Markdown")
                    .build();

            execute(editedMessage);
            answerCallbackQuery(callbackQuery.getId(), isAccepted ? "Заявка принята!" : "Заявка отклонена!");

        } catch (Exception e) {
            e.printStackTrace();
            answerCallbackQuery(callbackQuery.getId(), "⚠️ Ошибка: " + e.getMessage());
        }
    }

    private void answerCallbackQuery(String callbackQueryId, String text) {
        try {
            execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQueryId)
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText() &&
                update.getMessage().getFrom().getId().equals(real.getTelegram_id())) {

            String messageText = update.getMessage().getText();
            long userId = update.getMessage().getFrom().getId();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/update")) {
                List<ApplicationService> applications;
                try {
                    applications = ApplicationService.loadFromJSON();
                } catch (IOException e) {
                    sendText(chatId, "❌ Ошибка загрузки заявок: " + e.getMessage());
                    return;
                }

                for (ApplicationService application : applications) {
                    if (!application.isPublished()) {
                        sendApplicationWithButtons(application);
                    }
                }
            }

            else if (messageText.startsWith("/addcolor")) {
                handleAddColorCommand(messageText, chatId);
            }

            else if (expectedInput.equals("nickname")) {
                handleNicknameInput(messageText, chatId);
            }

            else if (expectedInput.equals("color")) {
                handleColorInput(messageText, chatId);
            }
        }


        else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleAddColorCommand(String messageText, long chatId) {
        String[] parts = messageText.split(" ", 3);

        if (parts.length == 3) {
            // Формат: /addcolor ник цвет
            String nickname = parts[1];
            String color = parts[2];

            try {
                addColorRecord(nickname, color);
                sendText(chatId, "✅ Запись добавлена: " + nickname + " - " + color);
            } catch (IOException e) {
                sendText(chatId, "❌ Ошибка при добавлении записи: " + e.getMessage());
            }
        } else if (parts.length == 1) {
            // Формат: /addcolor (без параметров)
            expectedInput = "nickname";
            sendText(chatId, "Введите никнейм владельца:");
        } else {
            sendText(chatId, "❌ Неправильный формат команды. Используйте: /addcolor никнейм цвет");
        }
    }

    private void handleNicknameInput(String nickname, long chatId) {
        expectedInput = "color";
        tempNickname = nickname;
        sendText(chatId, "Теперь введите цвет для " + nickname + " (в формате HEX, например #FF0000):");
    }

    private void handleColorInput(String color, long chatId) {
        try {
            addColorRecord(tempNickname, color);
            sendText(chatId, "✅ Запись добавлена: " + tempNickname + " - " + color);


            expectedInput = "";
            tempNickname = "";
        } catch (IOException e) {
            sendText(chatId, "❌ Ошибка при добавлении записи: " + e.getMessage());
        }
    }

    private String tempNickname = "";

    private void addColorRecord(String nickname, String color) throws IOException {
        List<Color_Object> colors = Color_Object.loadFromJSON();

        boolean found = false;
        for (Color_Object colorObj : colors) {
            if (colorObj.getName().equals(nickname)) {
                colorObj.setColor(color);
                found = true;
                break;
            }
        }

        if (!found) {
            colors.add(new Color_Object(nickname, color));
        }

        Color_Object.saveToJSON(colors);
    }

    private void sendApplicationWithButtons(ApplicationService application) {
        String text = String.format(
                "📄 *Заявка #%d*\n\n" +
                        "🔹 Участок: %d\n" +
                        "👤 Пользователь: %s\n" +
                        "❔ Тип: %s\n\n"+
                        "📝 Текст: %s",
                application.getId(),
                application.getArea_id(),
                application.getApplicant(),
                application.getType(),
                application.getText()
        );

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text("✅ Принять")
                .callbackData("accept_" + application.getId())
                .build());

        row.add(InlineKeyboardButton.builder()
                .text("❌ Отклонить")
                .callbackData("reject_" + application.getId())
                .build());

        rows.add(row);
        inlineKeyboard.setKeyboard(rows);

        SendMessage message = SendMessage.builder()
                .chatId(real.getTelegram_id().toString())
                .text(text)
                .parseMode("Markdown")
                .replyMarkup(inlineKeyboard)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendText(Long chatId, String text){
        SendMessage sm = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendNotification(){
        SendMessage sm = SendMessage.builder()
                .chatId(real.getTelegram_id().toString())
                .text("❗Новая заявка❗ /update").build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}