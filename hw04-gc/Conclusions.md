При анализе разных сборщиков мусора, можно сделать вывод, что для данной реализации приложения больше подходит сборщик GC1.
Наличие более плавных пауз "stop the world", в принципе больше сборок мусора в области молодого поколения и эти сборки - быстрее. 
Эти преимущества связаны, с разбиение области памяти на логические регионы - Eden, Survivor, Old Generation. Очистка производится в несколько потоков в тех регионах, которые наиболее нуждаются в особождении памяти.