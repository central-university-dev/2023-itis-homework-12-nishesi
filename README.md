# ПОИСК НА САЙТЕ

Домашнее задание для рефакторинга проекта
Нужно
  1. Повысить читаемость кода
  2. Добавить тесты чтобы понять контракт
  3. Угадать версию эластика с которой все это работает)
  4. Добавить композер файл для запуска окружения и тест контейнеры для интеграционных тестов.

# Результат

## Баги

- поиск по названию не работает (из-за type += "?")
- поиск только по брэнду не работает
- поиск по sku может не выдать результата если elasticsearch не успел переиндексироваться
- точное совпадение последнего слова не работает (пример Iphone 13, Iphone 14)
- поиск по каталогу и типу не работает из-за неправильного поля в запросе (catalogueId вместо catalogue_id)
- поиск только по type не работает (в строке остается type)
- поиск по типу и названию не работает (type остается в поисковой строке)
- поиск только по каталогу не работает (поиск по type)
- поиск по совпадению text = type.*name не работает
- поиск по брэнду и типу не работает (в запросе забыт фильтр по типу)

Все найденные баги исправлены, рабочий функционал ~~вроде как~~ не испорчен. Есть соответствующие тесты как на правильный функционал, так и на баги.
Также подправил изначальный код и адаптировал тесты под него для наглядности в ветке test_main