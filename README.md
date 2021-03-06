# Облачное хранилище CloudDrive

## Описание
Приложение *CloudDrive* представляет собой REST-сервис и предназначено для быстрой и удобной работы пользователя 
с файлами: загрузки файлов на сервер, скачивание файлов с сервера, переименование и удаление файлов, 
а также получение списка файлов, хранящихся на сервере в настоящий момент. 
Данные пользователей (логин/пароль), а также данные о файлах, находящихся в облачном хранилище, 
заносятся в соответствующие базы данных UserData и Storage (СУБД PostgreSQL *cloud_drive*).

## Порядок использования


### Запуск приложения *CloudDrive*
Для запуска приложения необходимо в корневом каталоге запустить файл cloud_drive-0.0.1-SNAPSHOT.jar 
с помощью команды java -jar /cloud_drive-0.0.1-SNAPSHOT.jar.
Также существует возможность запуска сервиса с помощью приложения *Docker* - для этого необходимо в корневом каталоге
запустить команду создания контейнера из образа cloud_drive:7.0 на порту 8080.

### Запуск клиентского WEB-приложения *FRONT*
После запуска приложения *CloudDrive* необходимо запустить приложение *FRONT*. 
По умолчанию приложение запускается на порту 8081 - http://localhost:8081.
Для приложения *FRONT* также существует возможность запуска сервиса с помощью приложения *Docker* - для этого необходимо 
в корневом каталоге запустить команду создания и запуска контейнера из образа cloud_front:latest на порту 8081.

### СУБД PostgreSQL *cloud_drive*
Система управления базами данных PostgreSQL установлена на сервере. 
СУБД запускается на порту 5532, логин: alexey, пароль: 123.
При запуске приложения *CloudDrive* система управления миграциями базы данных Liquibase отслеживает все изменения в 
базах данных.

### Запуск приложений *CloudDrive*, *FRONT* и СУБД PostgreSQL *cloud_drive* с использованием средств *docker-compose*
Для автоматического запуска обоих приложений необходимо в корневом каталоге выполнить команду docker-compose up:
приложение *CloudDrive* запустится автоматически(Dockerfile для создания образа приложения *CloudDrive* хранится 
в корневом каталоге), а страница приложения *FRONT*(Dockerfile для создания образа приложения *FRONT* хранится в 
каталоге /front/netology-diplom-frontend) будет доступна в браузере по адресу http://localhost:8081; СУБД PostgreSQL 
*cloud_drive* автоматически запустится на порту 5433. Все образы (автоматически создаются образы для *Cloud Drive* - 
*cloud_drive_app:latest*, а для *FRONT* - *cloud_drive_front:latest* ) описаны в соответствующих файлах Dockerfile
для *CloudDrive* и *FRONT*, а параметры запуска контейнеров описаны в файле docker-compose.yml.
Для остановки выполнения приложений используйте команду docker-compose down, при этом все контейнеры после остановки 
приложений будут удалены.

## Работа с облачным хранилищем

### Авторизация
На первом этапе работы клиент вводит свои login и пароль (метод _/login_) - в приложении *FRONT*. 
Данные клиента проходят проверку на *CloudDrive*, запрос авторизуется и приложению *FRONT* отправляется сгенерированный
токен JWT, позволяющий клиенту в течение всего заданного периода времени осуществлять работу с файлами в облачном 
хранилище *CloudDrive*. Все дальнейшие запросы с *FRONT* отправляются с header "auth-token", 
который содержит значение токена.

### Работа с файлами
#### Облачное хранилище *CloudDrive* позволяет осуществлять следующие операции с файлами:
1. Вывод списка файлов пользователя, которые размещены в облачном хранилище (метод _/list_). Количество файлов в списке 
определяется параметром limit, который задаётся в приложении *FRONT*.
2. Добавление _(POST)_ файла в облачное хранилище (_/file_).
3. Переименование _(PUT)_ файла (_/file_).
4. Удаление (DELETE) файла (_/file_).
5. Скачивание _(GET)_ файла из облачного хранилища в локальное хранилище пользователя (_/file_).

### Завершение работы с приложением
Для выхода из приложения необходимо вызвать метод _/logout_, который удалит токен и все дальнейшие запросы с этим токеном 
приведут к возникновению ошибки _"Unauthorizated error"_ с кодом 401.
При возникновении ошибок на стороне сервера или при наличии ошибочных входных данных клиент получает через приложение 
*FRONT* информацию о проблемах, возникших в процессе операции с указанием причин, которые явились причиной этих ошибок.

## Настройки приложения *CloudDrive*
В файле *application.properties* (/cloud_drive/src/main/resources) вы можете изменить номер порта,
на котором будет работать приложение *CloudDrive* - в настройках по умолчанию установлен номер порта 8080.
Название базы данных (установлена "cloud_drive"), порт, на котором запускается база данных (5432), login ("alexey") 
и пароль ("111") для доступа к базе данных также могут быть изменены пользователям 
в настройках приложения файле *application.properties*.
Вы можете изменить пути к директориям, где хранятся папки с сохранёнными файлами пользователей 
"general.path", а также путь к директории, где хранятся файлы, загруженные с сервера "download.user.folder.path".
Также в настройках можно изменить параметры генерации токена JWT - секретный код для алгоритма хэширования 
(jwt.secret: по умолчанию определен "alexey") и продолжительность действия токена (jwt.expiration: по умолчанию 
установлена 7200 секунд (2 часа)).
Кроме того, в настройках приложения можно изменить максимальный размер скачиваемого файла
(по умолчанию установлен 2048KB).

## Спасибо
Выражаю огромную благодарность Максиму Воронцову за общее руководство, полезные и дельные советы, а также за 
исчерпывающие и своевременные комментарии в процессе реализации проекта, и, кроме того, за очень внимательное отношение 
и колоссальное терпение.

## Контактная информация
Ждём ваши отзывы и комментарии о приложении *Cloud Drive* - alexey.sharaburin@gmail.com.
