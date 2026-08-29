@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo   Empaquetador de Gimnasio Desktop (Java + EXE)
echo ===================================================

:: 1. Verificar o localizar Maven
set "MVN_EXEC="
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    set "MVN_EXEC=mvn"
) else (
    for /d %%I in ("%LOCALAPPDATA%\Programs\IntelliJ IDEA*") do (
        if exist "%%I\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
            set "MVN_EXEC=%%I\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
        )
    )
    if not defined MVN_EXEC (
        for /d %%I in ("%ProgramFiles%\JetBrains\IntelliJ IDEA*") do (
            if exist "%%I\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
                set "MVN_EXEC=%%I\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
            )
        )
    )
)

if not defined MVN_EXEC (
    echo [ERROR] No se encontro Maven en el PATH ni en IntelliJ IDEA.
    echo Por favor agrega Maven al PATH o instala Maven.
    pause
    exit /b 1
)

echo [OK] Maven encontrado: !MVN_EXEC!

:: 2. Verificar jpackage
where jpackage >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] No se encontro jpackage en el PATH.
    echo Asegurate de tener Java JDK configurado en el PATH.
    pause
    exit /b 1
)

echo 1. Compilando y empaquetando Fat JAR con Maven...
call "!MVN_EXEC!" clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo la compilacion de Maven.
    pause
    exit /b 1
)

echo 2. Limpiando build anterior...
if exist "GimnasioApp" rmdir /s /q "GimnasioApp"

echo 3. Generando ejecutable nativo (.exe) con jpackage...
jpackage --type app-image --name GimnasioApp --input target/ --main-jar Proyecto-Gym.jar --main-class com.gym.app.Main
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo jpackage.
    pause
    exit /b 1
)

echo.
echo ========================================================
echo  Exito! La aplicacion ejecutable se encuentra en:
echo  GimnasioApp\GimnasioApp.exe
echo ========================================================
echo.
pause

