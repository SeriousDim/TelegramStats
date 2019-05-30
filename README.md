# TelegramStats

Solution of the [Telegram Contest task](https://t.me/contest/6) (March 10-24 2019).
Android software for showing simple charts based on JSON input file (located in assets folder).

_No specialized charting libraries have not been used._

___

Нужно доделать:
- [ ] Анимации дат
- [ ] Анимации графиков (изменение максимального значения)
- [ ] Оптимизация

Изучить:
- [ ] Алгоритм упрощения линий ([Алгоритм Рамера-Дугласа-Пекера](https://ru.wikipedia.org/wiki/Алгоритм_Рамера_—_Дугласа_—_Пекера))
- [ ] Способ анимации графика (ниже)

## Способ анимации графиков
См. заметку в Google Keep

- Создавать один Path для каждого графика, а не создавать его каждый кадр
- Деформировать их с помощью матриц: скейлить их
