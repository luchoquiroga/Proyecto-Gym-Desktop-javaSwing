@echo off
echo 1. Limpiando y empaquetando el Fat JAR con Maven...
call mvn clean package -DskipTests

echo 2. Eliminando build anterior si existe...
if exist "GimnasioApp" rmdir /s /q "GimnasioApp"

echo 3. Generando el ejecutable con jpackage...
jpackage --type app-image --name GimnasioApp --input target/ --main-jar Proyecto-Gym.jar --main-class com.gym.app.Main

echo Proceso terminado.
pause
