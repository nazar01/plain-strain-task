# plain-strain-task
Решение плоской задачи теории упругости в произвольной односвязной области методом конечных элементов.

Каждый модуль находится в своей папке, а общение между модулями происходит через файлы в корневой папке.

Граничные точки и треугольники должны быть перечислены в порядке против часовой стрелки.

# GUI
Для запуска в папке GUI введите `npm install` и `npm start`.

На вкладке "Задание области" можно нащелкать область левой кнопкой мыши, а затем выбирая точки правым щелчком задавать им начальные условия. (то есть кликнули по точке правой кнопкой мыши, а потом кликнули в её новую позицию, или никуда не кликнули, если у точки отсутствует перемещение) Если включить триангуляцию, то можно нащелкать треугольники, тоже правой кнопкой мыши.
