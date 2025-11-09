# Product Catalog Service

Консольное приложение для управления каталогом товаров маркетплейса.

## О приложении

**Product Catalog Service** — система для управления товарами с функциями поиска, фильтрации, кэширования и логирования действий. Приложение работает в консоли и сохраняет данные между запусками.

## Структура проекта

```
product-catalog-service/
├── Main.java
├── model/
│   ├── Product.java
│   ├── User.java
│   ├── AuditRecord.java
│   ├── AppData.java
│   ├── CacheEntry.java
│   └── enums/
│       └── Action.java
├── repository/
│   └── ProductRepository.java
├── service/
│   ├── CatalogService.java
│   ├── UserService.java
│   ├── AuditService.java
│   ├── CacheService.java
│   ├── DataStorage.java
│   └── impl/
│       ├── CatalogServiceImpl.java
│       ├── UserServiceImpl.java
│       ├── AuditServiceImpl.java
│       ├── CacheServiceImpl.java
│       └── DataStorageImpl.java
└── ui/
    └── ConsoleMenu.java
```

## Как запустить

### Вариант 1: Из IDE (IntelliJ IDEA / Eclipse)

1. Откройте папку проекта в IDE
2. Найдите файл `Main.java`
3. Нажмите правой кнопкой → **Run 'Main.main()'**
4. Или нажмите **Shift + F10** (IntelliJ) / **Ctrl + F11** (Eclipse)

### Вариант 2: Из командной строки

**Windows:**
```cmd
# Перейти в папку с Main.java
cd path\to\project

# Скомпилировать все файлы
javac -encoding UTF-8 Main.java main\java\ru\ylab\model\*.java main\java\ru\ylab\model\enums\*.java main\java\ru\ylab\repository\*.java main\java\ru\ylab\service\*.java main\java\ru\ylab\service\impl\*.java main\java\ru\ylab\ui\*.java

# Запустить
java Main
```

**macOS / Linux:**
```bash
# Перейти в папку с Main.java
cd path/to/project

# Скомпилировать все файлы
javac -encoding UTF-8 Main.java main/java/ru/ylab/model/*.java main/java/ru/ylab/model/enums/*.java main/java/ru/ylab/repository/*.java main/java/ru/ylab/service/*.java main/java/ru/ylab/service/impl/*.java main/java/ru/ylab/ui/*.java

# Запустить
java Main
```

## Первый запуск

При первом запуске приложение автоматически создаст:
- 10 тестовых товаров
- 2 тестовых пользователя

**Учётные данные для входа:**
```
Логин: admin
Пароль: admin123
```

или

```
Логин: user
Пароль: user123
```

## Как использовать

После запуска вы увидите главное меню:

```
1. Просмотр всех товаров
2. Поиск товара
3. Добавить товар
4. Редактировать товар
5. Удалить товар
6. Фильтрация товаров
7. Метрики приложения
8. История действий
9. Выход
```

### Примеры операций

**Добавить товар:**
```
Выбор: 3
Название: iPhone 15
Категория: Electronics
Бренд: Apple
Цена: 999.99
Описание: Latest model
```

**Поиск:**
```
Выбор: 2
Ключевое слово: laptop
```

**Фильтрация по цене:**
```
Выбор: 6
→ По цене
Минимальная цена: 500
Максимальная цена: 1500
```


## Файлы, которые создаёт программа

После первого запуска появятся:
- `marketplace_data.ser` — основной файл с данными (текущая версия данных)
- `marketplace_data_backup.ser` — резервная копия (предыдущая версия данных)
- `*.class` — скомпилированные файлы (можно удалить)

