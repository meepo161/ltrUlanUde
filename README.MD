>Чтоб проект заработал необходимо выполнить ряд действий.
>1. Установить k2mod в локальный репозиторий maven
>mvn install:install-file -Dfile=./k2mod-3.0.0.jar -DgroupId=com.ucicke -DartifactId=k2mod -Dversion=3.0.0 -Dpackaging=jar
>2. Установить ltrdll.exe
>3. Выбрать jdk 1.8 x86
>4. clean compile exec:java
>5. Invalidate and restart