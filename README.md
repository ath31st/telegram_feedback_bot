# Feedback Bot / Бот для обратной связи

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![Telegram](https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)
![SonarLint](https://img.shields.io/badge/SonarLint-CB2029?style=for-the-badge&logo=sonarlint&logoColor=white)

## Содержание / Contents

1. [Версия программы](#версия-программы--program-version)
2. [Введение](#введение--introduction)
3. [Функционал](#функционал--functionality)
4. [Установка](#установка--installation)
5. [Список команд](#список-команд--list-of-supported-commands)
6. [Версии](#версии--versions)
7. [Лицензия](#лицензия--license)

## Версия программы / Program version

1.0.0

## Введение / Introduction

Телеграм бот для получения обратной связи. Может быть настроен на пересылку сообщений в группу или
конкретному пользователю. Поддерживает несколько языков (русский, английский).

Telegram bot for collecting feedback. It Can be configured to forward messages to a group or
specific user. Supports multiple languages (Russian, English).

## Функционал / Functionality

- Получение обратной связи от пользователей
- Форматирование текста, добавление дополнительной информации об отправителе (юзернейм, ID,
  локализация)
- Пересылка дополненных сообщений от пользователей в группу администраторов или конкретному
  пользователю (который принимает фидбек)
- Доставка ответа на обратную связь пользователю
- При настройке работы с группой администраторов, бот будет ожидать в течение 2 минут ответ того
  администратора, который нажал кнопку "Ответить", остальные сообщения от других администраторов
  будут игнорироваться.
- Бот поддерживает несколько языков (русский, английский), язык устанавливается в зависимости от
  локализации пользователя, который оставляет обратную связь.


- Collecting user feedback
- Text formatting with sender info (username, ID, locale)
- Forwarding messages to an admin group or specific recipient
- Delivering responses back to users
- For group mode: 2-minute timeout for selected admin to respond
- Russian and English language support (auto-detected by user locale)

## Установка / Installation

### Системные требования / System requirements

- Java 17 (или выше / or higher)
- Gradle 8.10 (опционально / optional)

### Инструкция по установке / Installation guide

1. Клонируйте репозиторий / Clone repository:
    ```bash
    git clone https://github.com/ath31st/telegram_feedback_bot
    ```
2. Перейдите в директорию / Go to directory:
    ```bash
    cd telegram_feedback_bot
    ```
3. Проверьте версию Java 17+ / Verify Java 17+:
    ```bash
    java -version
    ```

4. Запустите скрипт для сборки проекта / Run build script:

   для windows / for windows:
   ```bash
   gradlew.bat build
    ```
   для linux / for linux:
   ```bash
   ./gradlew build
    ```

Этот скрипт скачает Gradle автоматически и создаст JAR-файл со всеми зависимостями.

This will download Gradle automatically and create a standalone JAR file with all dependencies
included (fat JAR).

### Инструкция по запуску / How to run

1. Найдите сгенерированный JAR-файл в папке build/libs/feedback_bot-x.x.x-standalone.jar / Locate
   the generated JAR file in build/libs/feedback_bot-x.x.x-standalone.jar
2. Запустите JAR-файл / Run the JAR file:

   для linux / for linux:
    ```bash
   export TELEGRAM_BOT_TOKEN="your-bot-token-here"
   export FEEDBACK_CHAT_ID="your-chat-id-here"
   java -jar build/libs/feedback_bot-x.x.x-standalone.jar
    ```
   для windows / for windows:
    ```bash
   set TELEGRAM_BOT_TOKEN="your-bot-token-here"
   set FEEDBACK_CHAT_ID="your-chat-id-here"
   java -jar build\libs\feedback_bot-x.x.x-standalone.jar
    ```

Будьте внимательны, что вам нужно определить версию приложения вместо x.x.x

Beware that you need to define an application version instead of x.x.x

## Список команд / List of supported commands:

    /start
    /settings
    /help

## Версии / Versions:

- Kotlin: 2.1.10
- TelegramBotAPI: 24.0.2
- Gradle: 8.10

## Лицензия / License

This project is distributed under the terms of the MIT license. See [LICENSE](LICENSE).