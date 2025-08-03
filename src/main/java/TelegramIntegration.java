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
        // Обработка команды /update
        if (update.hasMessage() && update.getMessage().hasText() &&
                update.getMessage().getText().equals("/update") &&
                update.getMessage().getFrom().getId().equals(real.getTelegram_id())) {

            List<ApplicationService> applications;
            try {
                applications = ApplicationService.loadFromJSON();
            } catch (IOException e) {
                sendText(real.getTelegram_id(), "❌ Ошибка загрузки заявок: " + e.getMessage());
                return;
            }

            for (ApplicationService application : applications) {
                if (!application.isPublished()) {
                    sendApplicationWithButtons(application);
                }
            }
        }

        // Обработка нажатий на Inline-кнопки
        else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    // Отправка заявки с кнопками "Принять" и "Отклонить"
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
    public void sendText(Long id, String text){
        SendMessage sm = SendMessage.builder()
                .chatId(id.toString())
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
                .text("❗Новая заявка❗").build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
