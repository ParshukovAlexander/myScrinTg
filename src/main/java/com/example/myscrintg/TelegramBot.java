package com.example.myscrintg;

import com.example.myscrintg.config.BotConfig;
import com.example.myscrintg.entity.ClientEntity;
import com.example.myscrintg.entity.FolderEntity;
import com.example.myscrintg.entity.PhotoEntity;
import com.example.myscrintg.service.ClientService;
import com.example.myscrintg.service.FolderService;
import com.example.myscrintg.service.PhotoService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    Map<Long,String> status= new HashMap<>();

    Map<Long,String> intoGallery = new HashMap<>();

    Map<Long,String> intoPhoto = new HashMap<>();

    @Autowired
    private PhotoService photoService;

    @Autowired
    private ClientService client;

    @Autowired
    private FolderService folder;

    final BotConfig config;

    List<BotCommand> listOfCommands = new ArrayList<>();

    public TelegramBot(BotConfig config) {
        this.config = config;
        listOfCommands.add(new BotCommand("/start", "welcome"));
        listOfCommands.add(new BotCommand("/mydata", "Просмотреть мои данные и их настройку"));
        listOfCommands.add(new BotCommand("/add_directory", "Добавить каталог"));
        listOfCommands.add(new BotCommand("/all_directory", "Открыть все каталоги"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bots" + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {


        Boolean booleanPhoto=false;
        Boolean booleanHasText=false;

        Boolean booleanCallback=update.hasCallbackQuery();
        Boolean booleanMessage = update.hasMessage();
        if (booleanMessage) {
            booleanPhoto = update.getMessage().hasPhoto();
            booleanHasText = update.getMessage().hasText();
        }

        Long chatId;
        if (update.hasCallbackQuery()) {
            chatId=update.getCallbackQuery().getMessage().getChatId();
        }else {
            chatId = update.getMessage().getChatId();
        }
        System.out.println(status.get(chatId));
        if ( booleanHasText && status.containsValue("into gallery:") && update.getMessage().getText().equals("Добавить фото")){
            prepareAndSendMessage(chatId,"Отправь фото вместе с названием");
            status.put(chatId,"add photo");
        }
        else if ( booleanHasText && status.containsValue("into gallery:")
                && update.getMessage().getText().equals("Удалить каталог «"+intoGallery.get(update.getMessage().getChatId())+"» со всеми изображениями")){
            folder.delete(intoGallery.get(chatId),update);
            prepareAndSendMessage(chatId,"Каталог удален");
        }
        else if ( booleanHasText && status.containsValue("into gallery:")
                && update.getMessage().getText().equals("Переименовать «"+intoGallery.get(update.getMessage().getChatId())+"»")){
            status.put(chatId,"Change name");
            prepareAndSendMessage(chatId,"Напиши новое имя каталога.");
        }
        else if (status.containsValue("add photo") && update.getMessage().getPhoto()!=null){
            String res=addPhoto(update,chatId);
            prepareAndSendMessage(chatId,res);
        }
        else if (!booleanPhoto && update.getMessage()!=null && update.getMessage().getText().charAt(0)=='/' ){
            chooseFromMenu(update);
        }
        else if (status.containsValue("into gallery:") && update.getCallbackQuery().getData().equals("Да \uD83D\uDC4D")){
            prepareAndSendMessage(chatId,"Отправь фото вместе с названием");
            status.put(chatId,"add photo");
        }else if (status.containsValue("into gallery:") && update.getCallbackQuery().getData().equals("Нет \uD83D\uDC4E")){
            prepareAndSendMessage(chatId,"Жаль, для просмотра всех каталогов перейди по /all_directory  или выбери данный пункт в меню \" ");
        }
        else if (update.hasCallbackQuery() && status.containsValue("into gallery:")){
            showPhoto(update);
        }

        else if (booleanHasText && status.containsValue("all_directory") ){
            status.put(update.getMessage().getChatId(),"add_directory");
            prepareAndSendMessage(chatId, "Напиши название создоваемого каталога");
        }

        else if (update.hasCallbackQuery() && status.containsValue("all_directory")){
            showPhotoInDirectory(update);
        }else if (update.getMessage().hasText() && status.containsValue("add_directory")){
            folder.saveFolder(update);
            prepareAndSendMessage(chatId, "Каталог создан. Для просмотра всех каталогов перейди по /all_directory" +
                    " или выбери данный пункт в меню ");
        }else if (status.containsValue("mydata") && update.getMessage().getText().equals("Удалить все данные ❌")){
            client.deleteClient(update);
            prepareAndSendMessage(chatId, "Данные удалены");
        }else if ( status.containsValue("into photo")) {
            settingPhoto(update);
        }else if ( status.containsValue("Change name")) {
            folder.changeName(update.getMessage().getText(),intoGallery.get(chatId),update);
            intoGallery.put(chatId,update.getMessage().getText());
            prepareAndSendMessage(chatId, "Каталог был переименован. Для просмотра всех каталогов перейди по /all_directory");
        }
    }

    private void settingPhoto(Update update) {
        String photoName = intoPhoto.get(update.getMessage().getChatId());
        String folderName = intoGallery.get(update.getMessage().getChatId());
        if (update.getMessage().hasText()) {
            String text = update.getMessage().getText();

            if (text.equals("Удалить \uD83D\uDDD1")) {
                photoService.delete(folderName, photoName,update);
                prepareAndSendMessage(update.getMessage().getChatId(), "Фотография стерта. Для перехода к каталогам: /all_directory");
            } else if (text.equals("Изменить название \uD83D\uDCC4")) {
                prepareAndSendMessage(update.getMessage().getChatId(), "Напиши новое название фотографии");
            } else if (text.equals("Изменить фотографию \uD83D\uDCF7")) {
                prepareAndSendMessage(update.getMessage().getChatId(), "Загружай новую фотографию");
            } else {
                photoService.changeNamePhoto(text, photoName, folderName,update);
                prepareAndSendMessage(update.getMessage().getChatId(), "Имя измененно. Для перехода к каталогам: /all_directory");
            }
        }else{
            photoService.changePhoto(photoName,folderName,update);
            prepareAndSendMessage(update.getMessage().getChatId(), "Фото измененно. Для перехода к каталогам: /all_directory");

        }
    }

    private String addPhoto(Update update,Long chatId) {
       return photoService.savePhoto(update,intoGallery.get(chatId));
    }

    private void showPhoto(Update update) {
        status.put(update.getCallbackQuery().getMessage().getChatId(),"into photo");
        PhotoEntity photo = photoService.getPhoto(update,intoGallery.get(update.getCallbackQuery().getMessage().getChatId()));
        SendPhoto sendPhoto=new SendPhoto();
        InputFile inputFile = new InputFile(photo.getPhoto());
        sendPhoto.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendPhoto.setPhoto(inputFile);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        sendPhoto.setCaption("Изображение: "+photo.getPhotoName()+". Дата загрузки: "+photo.getLocalDateTime().format(format));
        createSettingsPhoto(update);
        intoPhoto.put(update.getCallbackQuery().getMessage().getChatId(),photo.getPhotoName());
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createSettingsPhoto(Update update) {
        List<String> listSettingsPhoto = new ArrayList<>();
        String del = EmojiParser.parseToUnicode("Удалить " + ":wastebasket:");
        String change = EmojiParser.parseToUnicode("Изменить название " + ":page_facing_up:");
        String changePhoto = EmojiParser.parseToUnicode("Изменить фотографию " + ":camera:");
        listSettingsPhoto.add(del);
        listSettingsPhoto.add(change);
        listSettingsPhoto.add(changePhoto);
        prepareAndSendButtonSettingLine(listSettingsPhoto,update,"Настройки фото");
    }

    private void showPhotoInDirectory(Update update) {
        status.put(update.getCallbackQuery().getMessage().getChatId(),"into gallery:");
        intoGallery.put(update.getCallbackQuery().getMessage().getChatId(),update.getCallbackQuery().getData());
        List<PhotoEntity> photoInFolder = photoService.getPhotoInFolder(folder.getFolderByName(update.getCallbackQuery().getData(),update));
        List<String>photoName = new ArrayList<>();
        for (PhotoEntity p:photoInFolder){
            photoName.add(p.getPhotoName());
        }
        if (photoName.size()==0) {
            photoName.add("Да \uD83D\uDC4D");
            photoName.add("Нет \uD83D\uDC4E");
            prepareAndSendButtonLine(photoName,update.getCallbackQuery().getMessage().getChatId(),"Каталог пуст добавить фото? в директорию «"
                    +update.getCallbackQuery().getData()+"»");
        }else{
        prepareAndSendButtonLine(photoName,update.getCallbackQuery().getMessage().getChatId(),"Фото из каталога: "
                +update.getCallbackQuery().getData());
        }
        List <String> list = new ArrayList<>();
        list.add("Добавить фото");
        list.add("Удалить каталог «"+intoGallery.get(update.getCallbackQuery().getMessage().getChatId())+"» со всеми изображениями");
        list.add("Переименовать «"+intoGallery.get(update.getCallbackQuery().getMessage().getChatId())+"»");
        prepareAndSendButtonSettingLine(list, update, "Настройки в катлоге" );
    }


    public void chooseFromMenu(Update update) {

        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        if (messageText.equals("/start")) {
            client.saveClient(update);
            prepareAndSendMessage(chatId, "Здравствуй, "+update.getMessage().getFrom().getFirstName()+"! Добро пожаловать в телеграмм бот с добавлением фотографий. " +
                    "Для создания своей первой директории (каталога или папки) перейди в /add_directory или найди аналогичный пункт в меню");
            status.put(update.getMessage().getChatId(),"start");

        } else if (messageText.equals("/add_directory")) {
            status.put(update.getMessage().getChatId(),"add_directory");
            prepareAndSendMessage(chatId, "Напиши название создаваемого каталога \uD83D\uDDC2");

        } else if (messageText.equals("/all_directory")) {
            status.put(update.getMessage().getChatId(),"all_directory");
            showAllDirectory(update);
            settingDirectory(update);
        }else if (messageText.equals("/mydata")) {
            showMyData(update);
        }
    }

    private void showMyData(Update update) {
        ClientEntity clientEntity = client.getClient(update);
        prepareAndSendMessage(update.getMessage().getChatId(),clientEntity.toString());
        List<String> clientSetting = new ArrayList<>();
        clientSetting.add("Удалить все данные ❌");
        prepareAndSendButtonSettingLine(clientSetting,update,"Так твои данные записаны в БД");
        status.put(update.getMessage().getChatId(),"mydata");
    }

    private void settingDirectory(Update update) {
        List<String> listSettingsDirectory = new ArrayList<>();
        listSettingsDirectory.add("Добавить каталог  ➕");
        prepareAndSendButtonSettingLine(listSettingsDirectory,update,"Настройки каталога");
    }

    private void prepareAndSendButtonSettingLine(List<String> listSettingsDirectory, Update update,String text) {
        Long chatId;
        if (update.hasCallbackQuery()) {
            chatId=update.getCallbackQuery().getMessage().getChatId();
        }else {
            chatId = update.getMessage().getChatId();
        }
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        for (var l: listSettingsDirectory){
            row.add(l);
        }
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        executeMessage(message);

    }

    private void showAllDirectory(Update update) {
        ClientEntity clientEntity = client.getClient(update);
        List<FolderEntity> directory = folder.getAllDirectoryForId(clientEntity);
        List <String> forPrint=new ArrayList<>();
        for (var d : directory) {
            forPrint.add(d.getFolderName());
        }
        prepareAndSendButtonLine(forPrint,update.getMessage().getChatId(),"Вот твои каталоги \uD83D\uDDC2");
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }


    private void prepareAndSendButtonLine(List<String> buttonNames, Long chatId, String addText) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        int count = 0;
        for (String buttonName : buttonNames) {
            var button = new InlineKeyboardButton();
            button.setText(buttonName);
            button.setCallbackData(buttonName);
            rowInLine.add(button);
            count++;
            if (count % 2 == 0 || count == buttonNames.size()) {

                rowsLine.add(rowInLine);
                markupInline.setKeyboard(rowsLine);
                rowInLine = new ArrayList<>();
            }
        }


        SendMessage messageButton = new SendMessage();
        messageButton.setChatId(chatId);
        messageButton.setText(addText);
        messageButton.setReplyMarkup(markupInline);
        executeMessage(messageButton);
    }

}
